package com.example.math

import android.content.SharedPreferences
import com.example.data.model.BcsGoalCreditRecordEntity
import com.example.data.model.BcsStudyPlanGoalEntity

/**
 * Explicit lifecycle state machine for BCS daily study-goal completion credit (WP4.1 / F4-02).
 *
 * State transition invariants:
 * - [NOT_STARTED]: Session has not attempted completion credit yet (including freshly launched sessions).
 * - [IN_PROGRESS]: Completion credit transaction is currently executing.
 * - [RETRYABLE_FAILURE]: Failure occurred strictly BEFORE any Room DB mutation (zero DB mutation proven).
 *   Retry is permitted ONLY in the same ViewModel instance using the retained in-memory pending payload.
 * - [CREDITED]: Terminal state. Goal delta was applied and recorded with a matching durable receipt.
 * - [CREDIT_OUTCOME_UNKNOWN]: Terminal state. Post-commit/ambiguous failure, legacy pre-WP4.1 attempt,
 *   or completed attempt without a matching durable receipt. Never authorizes another Room mutation.
 */
enum class BcsGoalCreditState {
    NOT_STARTED,
    IN_PROGRESS,
    RETRYABLE_FAILURE,
    CREDITED,
    CREDIT_OUTCOME_UNKNOWN
}

data class BcsGoalCreditDelta(
    val attemptId: String,
    val examProfileId: String,
    val questionsDelta: Int,
    val studyTimeMinutesDelta: Int,
    val mockExamsDelta: Int,
    val revisionsDelta: Int = 0
) {
    val hasAnyCredit: Boolean
        get() = questionsDelta > 0 || studyTimeMinutesDelta > 0 || mockExamsDelta > 0 || revisionsDelta > 0
}

data class BcsGoalCreditStatus(
    val attemptId: String = "",
    val examProfileId: String = "",
    val state: BcsGoalCreditState = BcsGoalCreditState.NOT_STARTED,
    val appliedDelta: BcsGoalCreditDelta? = null,
    val failureMessageBn: String? = null
) {
    val isTerminal: Boolean
        get() = state == BcsGoalCreditState.CREDITED || state == BcsGoalCreditState.CREDIT_OUTCOME_UNKNOWN
}

enum class BcsGoalCreditFaultStage {
    BEFORE_DB_MUTATION,
    AFTER_DB_COMMIT
}

sealed class BcsGoalCreditApplyResult {
    data class Committed(val updatedGoal: BcsStudyPlanGoalEntity) : BcsGoalCreditApplyResult()
    data class FailedBeforeMutation(val cause: Throwable) : BcsGoalCreditApplyResult()
    data class AmbiguousOutcome(val cause: Throwable) : BcsGoalCreditApplyResult()
}

/**
 * Explicit outcome of the Room-atomic BCS goal-credit ledger transaction (BCS-GC-02).
 *
 * - [Applied]: Persisted PENDING ledger record transitioned atomically to CREDITED and incremented
 *   the matching bcs_study_goals row in the same SQLite transaction.
 * - [AlreadyCredited]: Ledger record for this attemptId was already CREDITED; zero goal mutation occurred.
 * - [NoPendingRecord]: No ledger record exists for this attemptId; zero ledger insert and zero goal mutation occurred.
 * - [InvalidLedgerState]: Persisted ledger record had an unknown status or invalid required fields; failed closed with zero goal mutation.
 */
sealed class BcsGoalCreditLedgerTxnResult {
    data class Applied(
        val record: BcsGoalCreditRecordEntity,
        val updatedGoal: BcsStudyPlanGoalEntity
    ) : BcsGoalCreditLedgerTxnResult()

    data class AlreadyCredited(
        val record: BcsGoalCreditRecordEntity
    ) : BcsGoalCreditLedgerTxnResult()

    data class NoPendingRecord(
        val attemptId: String
    ) : BcsGoalCreditLedgerTxnResult()

    data class InvalidLedgerState(
        val attemptId: String,
        val status: String,
        val reason: String
    ) : BcsGoalCreditLedgerTxnResult()
}

data class BcsGoalCreditReceipt(
    val attemptId: String,
    val examProfileId: String,
    val questionsDelta: Int,
    val studyTimeMinutesDelta: Int,
    val mockExamsDelta: Int,
    val revisionsDelta: Int,
    val creditedAtMs: Long
) {
    fun toDelta(): BcsGoalCreditDelta = BcsGoalCreditDelta(
        attemptId = attemptId,
        examProfileId = examProfileId,
        questionsDelta = questionsDelta,
        studyTimeMinutesDelta = studyTimeMinutesDelta,
        mockExamsDelta = mockExamsDelta,
        revisionsDelta = revisionsDelta
    )
}

object BcsGoalCreditCalculator {
    /**
     * Explicit elapsed-time rounding policy for BCS completion credit:
     * - If zero questions were attempted OR elapsedSeconds <= 0: returns 0 minutes.
     * - Otherwise: rounds to the nearest whole minute (`(elapsedSeconds + 30) / 60`)
     *   with a minimum floor of 1 minute for any completed session with answered work.
     */
    fun computeStudyTimeMinutesDelta(elapsedSeconds: Int, attemptedQuestions: Int): Int {
        if (attemptedQuestions <= 0 || elapsedSeconds <= 0) return 0
        return ((elapsedSeconds + 30) / 60).coerceAtLeast(1)
    }

    fun isUntimedBcsPractice(
        attemptId: String,
        immediateFeedback: Boolean,
        mode: QuizMode,
        configuredDurationSeconds: Int
    ): Boolean {
        return immediateFeedback ||
            mode == QuizMode.PRACTICE ||
            configuredDurationSeconds <= 0 ||
            attemptId.startsWith("bcs_mode_topic_practice_") ||
            attemptId.startsWith("bcs_mode_mistake_practice_")
    }

    fun isBcsMistakePracticeAttempt(attemptId: String): Boolean {
        return attemptId.startsWith("bcs_mode_mistake_practice_")
    }

    fun computeCompletionDelta(
        attemptId: String,
        examProfileId: String,
        correctCount: Int,
        wrongCount: Int,
        elapsedSeconds: Int,
        immediateFeedback: Boolean,
        mode: QuizMode,
        configuredDurationSeconds: Int
    ): BcsGoalCreditDelta {
        val attemptedQuestions = (correctCount.coerceAtLeast(0) + wrongCount.coerceAtLeast(0))
        if (attemptedQuestions <= 0) {
            return BcsGoalCreditDelta(
                attemptId = attemptId,
                examProfileId = examProfileId,
                questionsDelta = 0,
                studyTimeMinutesDelta = 0,
                mockExamsDelta = 0,
                revisionsDelta = 0
            )
        }
        val isUntimedPractice = isUntimedBcsPractice(
            attemptId = attemptId,
            immediateFeedback = immediateFeedback,
            mode = mode,
            configuredDurationSeconds = configuredDurationSeconds
        )
        val studyMinutes = computeStudyTimeMinutesDelta(elapsedSeconds, attemptedQuestions)
        val mockDelta = if (!isUntimedPractice) 1 else 0
        val revisionsDelta = if (isBcsMistakePracticeAttempt(attemptId)) 1 else 0
        return BcsGoalCreditDelta(
            attemptId = attemptId,
            examProfileId = examProfileId,
            questionsDelta = attemptedQuestions,
            studyTimeMinutesDelta = studyMinutes,
            mockExamsDelta = mockDelta,
            revisionsDelta = revisionsDelta
        )
    }
}

/**
 * Room-v10-compatible durable receipt and WP4.1 launch-origin store backed by SharedPreferences.
 *
 * - Launch-origin marker distinguishes sessions launched under WP4.1 (no launch-time credit)
 *   from legacy pre-WP4.1 IN_PROGRESS sessions (which already received launch-time credit).
 * - Credited receipt records applied completion credits keyed by [attemptId] so completed
 *   sessions or recreated ViewModels never double-credit.
 */
class BcsGoalCreditReceiptStore(private val prefs: SharedPreferences) {
    companion object {
        internal const val ORIGIN_KEY_PREFIX = "wp41_bcs_goal_origin_"
        internal const val RECEIPT_KEY_PREFIX = "wp41_bcs_goal_receipt_"
        private const val RECEIPT_TAG = "CREDITED"
    }

    fun markWp41AttemptLaunched(attemptId: String) {
        if (attemptId.isBlank()) return
        prefs.edit().putBoolean("$ORIGIN_KEY_PREFIX$attemptId", true).commit()
    }

    fun isWp41Attempt(attemptId: String): Boolean {
        if (attemptId.isBlank()) return false
        return prefs.getBoolean("$ORIGIN_KEY_PREFIX$attemptId", false)
    }

    fun recordCreditedReceipt(delta: BcsGoalCreditDelta, creditedAtMs: Long = System.currentTimeMillis()): Boolean {
        if (delta.attemptId.isBlank()) return false
        val encoded = listOf(
            RECEIPT_TAG,
            delta.examProfileId.replace("|", "_"),
            delta.questionsDelta.toString(),
            delta.studyTimeMinutesDelta.toString(),
            delta.mockExamsDelta.toString(),
            delta.revisionsDelta.toString(),
            creditedAtMs.toString()
        ).joinToString("|")
        return prefs.edit().putString("$RECEIPT_KEY_PREFIX${delta.attemptId}", encoded).commit()
    }

    fun readReceipt(attemptId: String): BcsGoalCreditReceipt? {
        if (attemptId.isBlank()) return null
        val raw = prefs.getString("$RECEIPT_KEY_PREFIX$attemptId", null) ?: return null
        val parts = raw.split("|")
        if (parts.size != 7 || parts[0] != RECEIPT_TAG) return null
        val profileId = parts[1].ifBlank { return null }
        val qDelta = parts[2].toIntOrNull() ?: return null
        val minDelta = parts[3].toIntOrNull() ?: return null
        val mockDelta = parts[4].toIntOrNull() ?: return null
        val revDelta = parts[5].toIntOrNull() ?: return null
        val ts = parts[6].toLongOrNull() ?: return null
        return BcsGoalCreditReceipt(
            attemptId = attemptId,
            examProfileId = profileId,
            questionsDelta = qDelta,
            studyTimeMinutesDelta = minDelta,
            mockExamsDelta = mockDelta,
            revisionsDelta = revDelta,
            creditedAtMs = ts
        )
    }

    fun hasReceipt(attemptId: String): Boolean = readReceipt(attemptId) != null
}
