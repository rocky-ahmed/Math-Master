# Math Master — Packet A Execution Checkpoint (`BUGFIX_PACKET_A_CHECKPOINT.md`)

**Project**: Math Master (`com.aistudio.applet.ghqqzu` / `com.example`)  
**Workspace**: `/app/applet`  
**Packet Status**: **CLOSED** — **Tasks `A-01`, `A-02`, `A-03`, `A-04`, `A-05`, `A-06`, and `A-07` (Safe Fallback & UI Hardening) Completed & Verified** (`14 / 14` passed in `Phase1ValidationAndIntegrityUnitTest`; `9 / 9` passed in `Phase3DatabaseTransactionAndHistoryTest`; `15 / 15` passed in `Phase4Wp41IntegrityRegressionTest`; `8 / 8` passed in `Phase3MemoryAndPdfCacheTest`; `5 / 5` passed in `PrincipalAuditBug94To97RegressionTest`). Note: Original `class_9_10.pdf` asset remains **BLOCKED / UNAVAILABLE** (3 offline PDFs bundled for Classes 6–8; structured digital content available for all 4 books).  
**Last Updated**: `2026-11-26T09:35:00Z`

---

## 1. Packet A Revalidation Summary

| Task ID | Audit Ref | Target Component | Revalidation Status | Implementation Status |
| :---: | :---: | :--- | :---: | :---: |
| **`A-01`** | `F4-09 (Primary)` | `SharedMathEngine.isAnswerCorrect` numeric vs. boolean conflation | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-02`** | `F4-09 (9B)` | `ProblemSolverPedagogyComponents.kt:1024` unsafe substring `contains` match | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-03`** | `F4-09 (9C)` | `BookQuestionEngine.kt:395` loose `0.01` numeric tolerance | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-04`** | `F4-05A` | `MathRepository.recordBcsTopicProgress` unlocked RMW concurrency | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-05`** | `F4-05C` | Practice Lab / Mental Math / Number System / Book Practice split completion writes | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-06`** | `F4-06` | `MathViewModel.finishQuizSession` immediate-feedback correct-answer guard & in-flight submit coordination | `CONFIRMED OPEN` | **IMPLEMENTED AND VERIFIED** |
| **`A-07`** | `PDF-9-10` | Missing `app/src/main/assets/books/class_9_10.pdf` detection, centralized Bengali structured fallback & entry-point accessibility hardening | `CONFIRMED OPEN / BLOCKED ASSET` | **FALLBACK IMPLEMENTED AND VERIFIED (ORIGINAL PDF BLOCKED)** |

### Protected Baseline Implementations Verified Intact (Unmodified)
- **`F4-01`**: Number System CAS completion/progression guard (`MathViewModel.kt:4433–4493`) — **ALREADY FIXED & PRESERVED**.
- **`F4-02` & `BCS-GC-02`**: Room v11 (`MIGRATION_10_11`) `bcs_goal_credit_records` durable ledger — **ALREADY FIXED & PRESERVED**.
- **`F4-03`**: Canonical single BCS unified mistake key (`"BCS_${activeBcsProfileId}_$canonicalRawBcsId"`) — **ALREADY FIXED & PRESERVED**.
- **`F4-04`**: Removal of duplicate `resolveUnifiedMistake` in `submitBookPracticeAnswer` — **ALREADY FIXED & PRESERVED**.
- **`F4-05A (Goal Deltas)` & `F4-05B`**: `recordBcsStudyGoalDelta` (`bcsStudyGoalMutex` + `@Transaction`) and `deleteExamAttempt` (`dailyStudyWriteMutex`) — **ALREADY FIXED & PRESERVED**.
- **Protected Study Timer 2.0 & Crash-Safe Flush Journal (`BUG-78` / `BUG-86`)**: Strictly untouched.

---

## 2. Task `A-01` — `SharedMathEngine` Numeric vs. Boolean Answer Validation

### 2.1 Root Cause
In `app/src/main/java/com/example/math/SharedMathEngine.kt` (lines 417–420 prior to fix), `isAnswerCorrect(userAnswer, expectedAnswer)` included `"1"` in `trueTokens` and `"0"` in `falseTokens`:
```kotlin
val trueTokens = setOf("true", "সত্য", "হ্যাঁ", "yes", "1", "ঠিক")
val falseTokens = setOf("false", "মিথ্যা", "না", "no", "0", "ভুল")
if (normUser in trueTokens && normExpected in trueTokens) return true
if (normUser in falseTokens && normExpected in falseTokens) return true
```
Because `normalizeAnswerToken` normalizes Bengali `"১"` to `"1"`, `"০"` to `"0"`, `"1.0"` to `"1"`, `"0.0"` to `"0"`, and strips unit suffixes (e.g., `"1 সেমি"` → `"1"`), any numerical question expecting `1` or `0` across Quiz, Practice Lab, Number System, Book Practice, and Problem Solver falsely accepted boolean words (`"সত্য"`, `"হ্যাঁ"`, `"true"`, `"yes"`, `"ঠিক"`, `"মিথ্যা"`, `"না"`, `"false"`, `"no"`, `"ভুল"`). Conversely, boolean True/False questions falsely accepted raw numbers `"1"` / `"0"` / `"১"` / `"০"`.

### 2.2 Exact Modified Files
1. `app/src/main/java/com/example/math/SharedMathEngine.kt` (`lines 407–454`)
2. `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt` (`lines 605–705`)

### 2.3 Implementation Details
- Defined strictly non-numeric boolean alias sets in `SharedMathEngine`:
  - `TRUE_ANSWER_TOKENS = setOf("true", "সত্য", "হ্যাঁ", "yes", "ঠিক", "সত্য(true)")`
  - `FALSE_ANSWER_TOKENS = setOf("false", "মিথ্যা", "না", "no", "ভুল", "মিথ্যা(false)")`
- Added an explicit blank check on `expectedAnswer` (`if (userAnswer.isBlank() || expectedAnswer.isBlank()) return false`).
- Isolated boolean equivalence: when either `normExpected` or `normUser` is a boolean token, `isAnswerCorrect` returns `true` if and only if both belong to `TRUE_ANSWER_TOKENS` or both belong to `FALSE_ANSWER_TOKENS`, and immediately returns `false` otherwise (preventing any cross-matching between boolean words and numeric `"1"` / `"0"`).
- Preserved all existing numeric, Bengali numeral, unit-stripped, mixed-fraction (`"১ সমস্ত ১/২"`), simple-fraction (`"২/৪"` == `"1/2"`), ratio (`"৩:৪"` == `"6:8"`), percentage, and decimal equivalence rules for non-boolean inputs.
- Verified all 19 call sites across `MathViewModel.kt`, `QuizModels.kt`, `ExamEngineModels.kt`, `BookQuestionEngine.kt`, and `ProblemSolverPedagogyComponents.kt`.

### 2.4 Regression Tests Added
Added two comprehensive test methods to `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt`:
1. `a01_sharedMathEngine_numericOneAndZeroRejectBooleanWordsAndAcceptNumericForms`:
   - Verifies expected numeric `1` (`"1"`, `"১"`, `"1.0"`, `"১.০"`, `"1 সেমি"`) rejects all 18 Bengali/English true and false words (`"হ্যাঁ"`, `"সত্য"`, `"true"`, `"True"`, `"TRUE"`, `"yes"`, `"Yes"`, `"ঠিক"`, `"সত্য (True)"`, `"না"`, `"মিথ্যা"`, `"false"`, `"False"`, `"FALSE"`, `"no"`, `"No"`, `"ভুল"`, `"মিথ্যা (False)"`).
   - Verifies expected numeric `0` (`"0"`, `"০"`, `"0.0"`, `"০.০"`) rejects all 18 Bengali/English false and true words.
   - Verifies expected numeric `1` and `0` accept valid Bengali/English numerals, decimals (`"1.0"`, `"০.০"`), fractions (`"2/2"`, `"0/5"`), and whitespace-padded forms while rejecting `"0"` for `"1"` and `"১"` for `"০"`.
2. `a01_sharedMathEngine_booleanQuestionsAcceptValidAliasesAndRejectNumericOneZero`:
   - Verifies all true aliases match one another, all false aliases match one another, true vs. false aliases are rejected, and raw numeric `"1"`, `"১"`, `"0"`, `"০"` are rejected for boolean expected answers.
   - Verifies fractions, decimals, mixed fractions, unit suffixes, and ratios remain valid.

### 2.5 Exact Test Command & Actual Results (`A-01`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase1ValidationAndIntegrityUnitTest
  ```
- **Gradle Outcome**: `BUILD SUCCESSFUL in 3m 43s`
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase1ValidationAndIntegrityUnitTest.xml`
  - **Tests Discovered & Executed**: `10`
  - **Passed**: `10`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a01_sharedMathEngine_numericOneAndZeroRejectBooleanWordsAndAcceptNumericForms`**: `PASSED (0.121s)`
  - **`a01_sharedMathEngine_booleanQuestionsAcceptValidAliasesAndRejectNumericOneZero`**: `PASSED (0.036s)`

---

## 3. Task `A-02` — Problem Solver Answer Validation Hardening

### 3.1 Root Cause
In `app/src/main/java/com/example/ui/screens/ProblemSolverPedagogyComponents.kt` (`lines 1022–1025` prior to fix), the step self-check ("নিজে চেষ্টা করুন" -> "যাচাই") button evaluated learner input using an unconstrained substring fallback:
```kotlin
val expected = (step.expectedStepValueBn ?: "").ifBlank { afterStr }
val isMatch = SharedMathEngine.isAnswerCorrect(userAttempt, expected) ||
    (userAttempt.trim().isNotBlank() && expected.contains(userAttempt.trim()))
```
Because `expected.contains(userAttempt.trim())` matched any non-blank substring, partial digits or prefixes such as `"1"`, `"12"`, or `"25"` for an expected answer of `"125"` (or `"১"`, `"১২"`, `"২৫"` for `"১২৫"`, or `"x"` for `"(x - 2)(x - 3)"`) were falsely graded as **Correct** (`"সঠিক! আপনি এই ধাপটি নিজে সমাধান করেছেন।"`).

### 3.2 Exact Modified Files
1. `app/src/main/java/com/example/ui/screens/ProblemSolverPedagogyComponents.kt` (`lines 1020–1143`)
2. `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt` (`lines 705–841`)

### 3.3 Implementation & Mathematical Equivalence Improvements
- Removed `expected.contains(userAttempt.trim())` from `InteractiveLearningStepCard` and replaced the check with `isSolverStepAttemptCorrect(userAttempt, expected)`.
- Implemented `internal fun isSolverStepAttemptCorrect(userAttempt: String, expectedStepValue: String): Boolean` and `private fun extractCompleteStepRhsCandidates(expectedStepValue: String): List<String>`:
  1. Rejects blank or whitespace-only `userAttempt` or `expectedStepValue` immediately.
  2. Performs direct normalized comparison and mathematical equivalence checking via `SharedMathEngine.isAnswerCorrect(trimmedAttempt, trimmedExpected)` (preserving Bengali/English numeral normalization, unit stripping, reduced fraction/mixed-fraction/decimal equivalence, and ratio equivalence).
  3. Safely supports structured step expectations (e.g., `"x = ১২৫"`, `"২৫ + ১৫ = ৪০"`, `"রূপান্তর ➜ ১২৫ টাকা"`, `"উত্তর: ১২৫ টাকা"`) by extracting only the **complete** right-hand side after `"➜"`, `"="`, or a non-numeric text label prefix `":"` (explicitly guarding against splitting numeric ratios like `"৩:৪"` / `"3:4"`) and checking `SharedMathEngine.isAnswerCorrect(trimmedAttempt, candidate)`.
  4. Supports equation-to-equation attempts (`"x = 125"` vs `"x = ১২৫"`) by verifying normalized LHS equality and RHS mathematical equivalence.

### 3.4 Regression Tests Added
Added two focused regression test methods to `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt`:
1. `a02_problemSolverStepValidation_rejectsPartialSubstringsAndAcceptsExactOrEquivalent125`:
   - Proves for expected `"125"` and `"১২৫"` that partial attempts `"1"`, `"১"`, `"12"`, `"১২"`, `"25"`, `"২৫"`, and `"5"` are rejected (`false`), while `"125"`, `"১২৫"`, `"  125  "`, `"  ১২৫  "`, `"125.0"`, and `"250/2"` are accepted (`true`).
2. `a02_problemSolverStepValidation_supportsFractionsDecimalsNegativesEquationsAndRejectsMalformedOrEmpty`:
   - Proves fraction/decimal/mixed-fraction equivalence (`"3/4"`, `"0.75"`, `"০.৭৫"`, `"6/8"` for `"৩/৪"`; `"১ সমস্ত ১/২"`, `"1.5"` for `"৩/২"`), rejecting partial `"3"`, `"4"`, `"/"`.
   - Proves negative values (`"-125"`, `"-১২৫"`, `"-3/4"` vs `"-০.৭৫"`), rejecting `"125"`, `"-12"`, `"-"`.
   - Proves structured equations/transformations/labels (`"x = ১২৫"`, `"২৫ + ১৫ = ৪০"`, `"রূপান্তর ➜ ১২৫ টাকা"`, `"উত্তর: ১২৫ টাকা"`), ratios (`"৩:৪"` vs `"3:4"` and `"6:8"`, rejecting `"4"`), algebraic factors (`"(x - 2)(x - 3)"`, rejecting partial `"(x - 2)"`), and malformed/empty inputs (`""`, `"   "`, `"abc"`, `"12..5"`, `"12/0"`, `"+++"`, `"--125"`, `"125/0"`, `"NaN"`).

### 3.5 Exact Test Command & Actual Results (`A-02`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase1ValidationAndIntegrityUnitTest
  ```
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase1ValidationAndIntegrityUnitTest.xml` (`timestamp="2026-11-25T06:51:18.687Z"`)
  - **Tests Discovered & Executed**: `12`
  - **Passed**: `12`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a02_problemSolverStepValidation_rejectsPartialSubstringsAndAcceptsExactOrEquivalent125`**: `PASSED (0.006s)`
  - **`a02_problemSolverStepValidation_supportsFractionsDecimalsNegativesEquationsAndRejectsMalformedOrEmpty`**: `PASSED (0.173s)`

---

## 4. Task `A-03` — `BookQuestionEngine` Numerical Answer Validation Hardening

### 4.1 Root Cause
In `app/src/main/java/com/example/math/BookQuestionEngine.kt` (`lines 386–399` prior to fix), `isTextEquivalent(userRaw, expectedRaw)` accepted any numeric attempt within a hardcoded global tolerance of `0.01`:
```kotlin
val uNum = uNorm.replace(",", "").toDoubleOrNull()
val eNum = eNorm.replace(",", "").toDoubleOrNull()
if (uNum != null && eNum != null && abs(uNum - eNum) < 0.01) {
    return true
}
return SharedMathEngine.isAnswerCorrect(userRaw, expectedRaw)
```
Additionally, falling back to `SharedMathEngine.isAnswerCorrect` introduced a secondary `1e-4` floating-point tolerance (`abs(dUser - dExpected) < 1e-4`), causing near-but-incorrect decimal answers (e.g., `"0.509"` or `"0.50001"` for expected `"0.50"`, or `"0"` for `"0.0005"`) to be falsely graded as **Correct**. Furthermore, malformed numeric strings such as `"12..5"` or `"--5"` were not rejected before falling through to non-numeric text comparison.

### 4.2 Exact Modified Files
1. `app/src/main/java/com/example/math/BookModels.kt` (`line 546`)
2. `app/src/main/java/com/example/math/BookQuestionEngine.kt` (`lines 1–10, 370–568`)
3. `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt` (`lines 842–980`)

### 4.3 Implementation Details
- **Explicit Per-Question Rounding Metadata**: Added `val acceptedRoundingDecimalPlaces: Int? = null` to `BookPracticeQuestion` (`BookModels.kt:546`), preserving full backward compatibility with all existing constructors (`null` default = exact comparison).
- **Removal of Global `0.01` Tolerance & Exact `BigDecimal` / `Rational` Comparison**:
  - Updated `BookQuestionEngine.evaluateQuestion` to forward `question.acceptedRoundingDecimalPlaces` to `BookQuestionEngine.isTextEquivalent(userRaw, expectedRaw, acceptedRoundingDecimalPlaces = null)`.
  - Replaced floating-point epsilon comparison with exact `BigDecimal` (`userDecimal.compareTo(expectedDecimal) == 0`) and exact reduced `Rational` comparison (`userRational == expectedRational` when ratio `:` presence matches).
  - When `acceptedRoundingDecimalPlaces != null && acceptedRoundingDecimalPlaces >= 0`, both user and expected values (including fractions converted to high-precision `BigDecimal`) are rounded to `acceptedRoundingDecimalPlaces` using `RoundingMode.HALF_UP` and compared via `compareTo == 0`.
- **Malformed Numeric & Non-Finite Rejection**:
  - Added `containsNonFiniteToken` to immediately reject `NaN`, `+NaN`, `-NaN`, `Infinity`, `+Infinity`, `-Infinity`, `inf`, `+inf`, `-inf`, `∞`, `+∞`, `-∞`.
  - Added lexical classification in `isMalformedFractionOrNumber(normalizedToken)` before non-numeric text equality: tokens composed exclusively of digits and numeric/fraction/ratio punctuation (`0-9`, `.`, `+`, `-`, `/`, `:`) that fail strict fraction/ratio validation (e.g., `1/0`, `0/0`, `12/0`, `3:0`, `0:0`, `1/2/3`, `1:2:3`) or fail `BigDecimal` parsing (`12..5`, `12.3.4`, `--5`, `++5`) are rejected immediately, even when compared against themselves.
  - Preserved valid text, boolean, and algebraic formula comparisons (`"abc"`, `"(x - 2) / (x + 3)"`, `"সত্য (True)"`).

### 4.4 Regression Tests Added
Added two comprehensive test methods to `app/src/test/java/com/example/Phase1ValidationAndIntegrityUnitTest.kt`:
1. `a03_bookQuestionEngine_exactDecimalAndFractionEquivalenceRejectsNearIncorrectValues`:
   - Verifies expected `"0.50"` accepts `"0.50"`, `"0.5"`, `"০.৫০"`, `"০.৫"`, `"1/2"`, `"১/২"`, `"2/4"` and rejects near-but-incorrect decimals `"0.509"`, `"০.৫০৯"`, `"0.491"`, `"0.499"`, `"0.501"`, `"0.505"`, `"0.5001"`, `"0.50001"`.
   - Verifies exact integer, fraction, mixed-fraction (`"১ সমস্ত ১/২"` == `"1.5"`), unit-suffixed (`"৩৬টি"` == `"36"`), and ratio (`"৩ : ৪"` == `"6 : 8"`) equivalence while rejecting `"0.33"` for `"1/3"` and rejecting ratio-to-fraction cross-matches (`"3:4"` vs `"3/4"` or `"0.75"`).
2. `a03_bookQuestionEngine_negativesZeroSmallLargeExplicitRoundingAndInvalidInputs`:
   - Verifies negative numbers (`"-15.5"`, `"-১৫.৫"`, `"-31/2"`), zero (`"0"`, `"০"`, `"0.0"`, `"-0.0"`, `"0/7"`, rejecting `"0.001"` and `"0.0001"`), very small decimals (`"0.0005"`, `"০.০০০৫"`, `"1/2000"`, rejecting `"0"` and `"0.0006"`), and large 18-digit integers (`"999999999999999999"` vs `"999999999999999998"`).
   - Verifies explicit `acceptedRoundingDecimalPlaces = 2` on `BookPracticeQuestion` with exact answer `"143/3"` (`47.666...`): without metadata `"47.67"` is rejected while `"143/3"` and `"৪৭ সমস্ত ২/৩"` are accepted; with `acceptedRoundingDecimalPlaces = 2`, `"47.67"`, `"৪৭.৬৭"`, `"47.666"`, and `"143/3"` are accepted while `"47.66"`, `"47.68"`, and `"47.7"` are rejected.
   - Verifies malformed and non-finite inputs (`""`, `"   "`, `"NaN"`, `"nan"`, `"-NaN"`, `"Infinity"`, `"+Infinity"`, `"-Infinity"`, `"inf"`, `"∞"`, `"1/0"`, `"0/0"`, `"12/0"`, `"-5/0"`, `"১/০"`, `"3:0"`, `"0:0"`, `"1:2:3"`, `"1/2/3"`, `"12..5"`, `"12.3.4"`, `"--5"`, `"++5"`) are rejected both against `"0.5"` and against themselves, while valid text/algebraic formulas (`"abc"`, `"(x - 2) / (x + 3)"`, `"সত্য (True)"`) remain supported.

### 4.5 Exact Test Command & Actual Results (`A-03`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase1ValidationAndIntegrityUnitTest
  ```
- **Gradle Outcome**: `BUILD SUCCESSFUL in 3m 34s`
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase1ValidationAndIntegrityUnitTest.xml` (`timestamp="2026-11-25T08:29:53.231Z"`)
  - **Tests Discovered & Executed**: `14`
  - **Passed**: `14`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a03_bookQuestionEngine_exactDecimalAndFractionEquivalenceRejectsNearIncorrectValues`**: `PASSED (0.016s)`
  - **`a03_bookQuestionEngine_negativesZeroSmallLargeExplicitRoundingAndInvalidInputs`**: `PASSED (0.015s)`

---

## 5. Task `A-04` — `MathRepository.recordBcsTopicProgress` Concurrency & Transactional Integrity

### 5.1 Root Cause
In `app/src/main/java/com/example/data/repository/MathRepository.kt` (`lines 854–916` prior to fix), `recordBcsTopicProgress` executed a two-step unlocked read-modify-write across separate DAO suspend calls:
1. `val existing = dao.getBcsTopicProgressById(resolvedKey)`
2. In-memory computation of `nextAttempted`, `nextCorrect`, `totalTimeSeconds`, boolean latches `isGuideCompleted` / `miniCheckPassed`, `masteryPercent`, and `lastStudiedAt`.
3. `dao.upsertBcsTopicProgress(updated)` (`@Insert(onConflict = OnConflictStrategy.REPLACE)`).

Because `getBcsTopicProgressById` and `upsertBcsTopicProgress` were not enclosed inside a single Room SQLite transaction, two or more concurrent callers (e.g., rapid guide completion, mini-check pass, and topic practice updates on the same BCS topic and profile) could read the same stale snapshot and overwrite each other's accumulated `questionsAttempted`, `questionsCorrect`, `totalTimeSeconds`, or completion latch flags.

### 5.2 Exact Modified Files
1. `app/src/main/java/com/example/data/db/MathDao.kt` (`lines 890–963`)
2. `app/src/main/java/com/example/data/repository/MathRepository.kt` (`lines 854–876`)
3. `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt` (`lines 30–36, 715–960`)

### 5.3 Transactional Implementation Details
- Added `@Transaction suspend fun recordBcsTopicProgressTransaction(...) : BcsTopicProgressEntity` in `MathDao.kt` (`lines 894–963`) so that canonical profile-scoped key resolution (`rawTopicId` for default `"BCS_50TH_PRELIM_2025"`, or `BcsTopicProgressEntity.buildScopedKey(safeProfile, rawTopicId)` for non-default profiles), snapshot retrieval (`getBcsTopicProgressById(resolvedKey)`), delta accumulation (`questionsAttempted`, `questionsCorrect`, `totalTimeSeconds`), monotonic boolean latch merging (`isGuideCompleted`, `miniCheckPassed`), mastery recalculation, monotonic timestamp update (`max(existing.lastStudiedAt, nowMs)`), and `upsertBcsTopicProgress(updated)` execute atomically inside a single Room SQLite transaction.
- Updated `MathRepository.recordBcsTopicProgress` (`MathRepository.kt:854–876`) to delegate directly to `dao.recordBcsTopicProgressTransaction(...)`.
- Confirmed zero nested mutexes or new repository locks were introduced: `dailyStudyWriteMutex`, `bcsStudyGoalMutex`, and `mistakeResolutionMutex` remain strictly separate, and `BCS-GC-02` Room v11 ledger and Study Timer production code remain completely untouched.

### 5.4 Concurrency Test Methodology & Regression Tests Added
Added two multi-threaded concurrency and persistence regression test methods to `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt`:
1. `a04_recordBcsTopicProgress_concurrentUpdatesSameTopicAndProfilePreserveAllAccumulations`:
   - Uses a dedicated 12-thread `Executors.newFixedThreadPool(12)` coroutine dispatcher and a dual `CountDownLatch` barrier (`readyLatch(12)` + `startLatch(1)` with 10-second bounded awaits) to release 12 concurrent coroutines simultaneously via `async(dispatcher)` + `awaitAll()` against topic `"bcs_math_real_numbers"` under profile `"BCS_50TH_PRELIM_2025"`.
   - Verifies exact accumulation across all 12 concurrent updates (`questionsAttempted == 24`, `questionsCorrect == 18`, `totalTimeSeconds == 180`), monotonic preservation of `isGuideCompleted == true` (set by only 1 worker) and `miniCheckPassed == true` (set by only 1 worker), `masteryPercent == 87`, and `lastStudiedAt == baseTimeMs + 1200L`.
2. `a04_recordBcsTopicProgress_concurrentMultiProfileMultiTopicIsolationAndDatabaseRecreationPersistence`:
   - Uses a file-backed Room database (`a04_bcs_topic_concurrency_persistence.db`) and a 12-thread `Executors.newFixedThreadPool(12)` dispatcher with a `CountDownLatch` barrier to release 12 concurrent updates simultaneously across 3 BCS profiles (`BCS_50TH_PRELIM_2025`, `BCS_51ST_SPECIAL`, `BCS_WRITTEN_008_MATH`) and 2 topics (`bcs_math_real_numbers`, `bcs_math_algebra`), including mixed raw and `"profile::topicId"` inputs.
   - Closes the database, re-opens a fresh Room database and `MathRepository` instance from disk, verifies all 3 profile-scoped rows exist with zero lost updates or cross-profile contamination, and verifies duplicate/repeated update idempotency (confirming completion latches, `subjectTrack`, and monotonic `lastStudiedAt` do not regress).

### 5.5 Exact Test Command & Actual Results (`A-04`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase3DatabaseTransactionAndHistoryTest
  ```
- **Gradle Outcome**: `BUILD SUCCESSFUL in 1m 38s`
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase3DatabaseTransactionAndHistoryTest.xml` (`timestamp="2026-11-25T10:44:09.269Z"`)
  - **Tests Discovered & Executed**: `6`
  - **Passed**: `6`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a04_recordBcsTopicProgress_concurrentUpdatesSameTopicAndProfilePreserveAllAccumulations`**: `PASSED (0.128s)`
  - **`a04_recordBcsTopicProgress_concurrentMultiProfileMultiTopicIsolationAndDatabaseRecreationPersistence`**: `PASSED (8.189s)`

---

## 6. Task `A-05` — Atomic Practice Session Completion & Data Integrity (`Practice Lab`, `Mental Math`, `Number System`, `Book Practice`)

### 6.1 Root Cause & Four Affected Module Paths
Across four practice session modules in `app/src/main/java/com/example/ui/MathViewModel.kt`, session completion previously executed multiple independent, non-atomic repository calls outside a shared Room transaction:
1. **Practice Lab (`MathViewModel.finishPracticeSessionNow` — `lines 3564–3594`)**:
   - Executed `repository.recordQuizScore(...)` (`quiz_scores` table), then `repository.recordPractice("practice_${category.id}")` (`user_progress` table), and finally `repository.recordSessionAndAnalytics(...)` (`practice_history`, `topic_analytics`, `daily_study_activity` tables) as three separate transactions.
2. **Mental Math (`MathViewModel.finishMentalMathSession` — `lines 3944–3974`)**:
   - Executed `repository.recordQuizScore(...)`, then `repository.recordPractice("mental_math_${category.id}")`, and finally `repository.recordSessionAndAnalytics(...)` as three separate transactions.
3. **Number System (`MathViewModel.nextNumberSystemQuestion` completion branch — `lines 4466–4493`)**:
   - Executed `repository.recordQuizScore(...)` and `repository.recordSessionAndAnalytics(...)` as two separate transactions.
4. **Book Practice (`MathViewModel.finishBookPracticeSession` — `lines 5734–5763`)**:
   - Executed `repository.recordQuizScore(...)` and `repository.recordSessionAndAnalytics(...)` as two separate transactions.

If a database error, exception, or process interruption occurred after `recordQuizScore` (or `recordPractice`) before `recordSessionAndAnalytics` committed, the database retained partially committed session state (`QuizScoreEntity` and/or `ProgressEntity` without corresponding `PracticeHistoryEntity`, `TopicAnalyticsEntity`, or `DailyStudyActivityEntity`).

### 6.2 Exact Modified Files
1. `app/src/main/java/com/example/data/db/MathDao.kt` (`lines 82–95, 599–680`)
2. `app/src/main/java/com/example/data/repository/MathRepository.kt` (`lines 294–347`)
3. `app/src/main/java/com/example/math/PracticeLabModels.kt` (`line 245`)
4. `app/src/main/java/com/example/math/MentalMathModels.kt` (`line 257`)
5. `app/src/main/java/com/example/math/NumberSystemModels.kt` (`line 203`)
6. `app/src/main/java/com/example/math/BookModels.kt` (`line 568`)
7. `app/src/main/java/com/example/ui/MathViewModel.kt` (`lines 3564–3594, 3942–3970, 4463–4489, 5729–5759`)
8. `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt` (`lines 73, 976–1428`)

### 6.3 Transaction Architecture & Idempotency Strategy
- **Single Room `@Transaction` Boundary (`MathDao.completePracticeSessionTransaction`)**:
  - Combines the idempotency check, `insertQuizScore(QuizScoreEntity(...))`, optional `upsertProgress(ProgressEntity(itemId = learnedProgressItemId, isLearned = true, updatedAt = nowMs))`, and `recordSessionAndAnalyticsTransaction(...)` (`PracticeHistoryEntity`, `TopicAnalyticsEntity`, `DailyStudyActivityEntity`) inside one Room `@Transaction`.
  - `MathRepository.completePracticeSessionAtomically(...)` acquires `dailyStudyWriteMutex.lockWith { ... }` before entering `dao.completePracticeSessionTransaction(...)`, strictly preserving the Phase 3 lock ordering rule (`dailyStudyWriteMutex` before any `@Transaction` touching `daily_study_activity`) without introducing any new or nested mutexes.
- **Zero-Migration Durable Session Idempotency**:
  - Added `val sessionId: String = java.util.UUID.randomUUID().toString()` to `PracticeSessionState`, `MentalMathSessionState`, `NumberSystemPracticeState`, and `BookPracticeSessionState`. Every new or retried session generates a unique `sessionId`, while intra-session `.copy(...)` transitions preserve the active `sessionId`.
  - Inside `MathDao.completePracticeSessionTransaction`, when `sessionId` is non-blank, the transaction executes `insertProgressIfAbsent(ProgressEntity(itemId = "__session_receipt__$cleanSessionId", isLearned = false, updatedAt = nowMs))` (`@Insert(onConflict = OnConflictStrategy.IGNORE)` on the primary key of `user_progress`).
  - If `receiptRowId == -1L`, the session was already committed and the transaction returns `false` immediately without mutating `quiz_scores`, `practice_history`, `topic_analytics`, or `daily_study_activity`.
  - If the transaction aborts or rolls back mid-flight, SQLite automatically rolls back the `"__session_receipt__..."` insert along with all other writes so a subsequent retry of the same `sessionId` commits cleanly.
  - Updated `MathDao.getAllProgress()` (`WHERE itemId NOT LIKE '__session_receipt__%'`) so internal receipt keys remain invisible to user progress queries and dashboard `learnedItemsCount` calculations, requiring zero Room schema migration (Room v11 identity hash preserved).

### 6.4 Rollback Test Methodology & Regression Tests Added
Added three comprehensive regression test methods to `app/src/test/java/com/example/Phase3DatabaseTransactionAndHistoryTest.kt`:
1. `a05_completePracticeSessionAtomically_allFourModulesPersistScoreHistoryAnalyticsAndRollbackCleanlyOnMidTransactionFailure`:
   - Installs a deterministic SQLite `BEFORE INSERT ON daily_study_activity` trigger (`abort_a05_after_quiz_score`) that aborts the transaction **after** `insertQuizScore`, `upsertProgress`, `insertPracticeHistory`, and `upsertTopicAnalytics` have already executed.
   - Executes `completePracticeSessionAtomically` for all four module configurations (`PRACTICE_LAB`, `MENTAL_MATH`, `NUMBER_SYSTEM`, `BOOK`) and verifies 100% rollback across `quiz_scores`, `user_progress` (including session receipts), `practice_history`, `topic_analytics`, and `daily_study_activity` (zero partial state).
   - Drops the trigger and retries the exact same four session IDs, confirming all four commit atomically and persist exact scores, learned items, history, topic analytics, and aggregated daily study counters.
2. `a05_completePracticeSessionAtomically_duplicateAndConcurrentCompletionsAndDatabaseRecreationIdempotency`:
   - Uses a file-backed Room database (`a05_session_completion_idempotency.db`) and a 12-thread `Executors.newFixedThreadPool(12)` coroutine dispatcher with a `CountDownLatch` barrier to release 12 concurrent calls to `completePracticeSessionAtomically` with the same `sessionId = "shared_pl_session_99"`.
   - Verifies that strictly `1` call returns `true` and `11` return `false`, with `1` `QuizScoreEntity`, `1` `PracticeHistoryEntity`, and `totalAttempted == 10`.
   - Closes and re-opens the Room database and `MathRepository` from disk, verifies a duplicate call for `"shared_pl_session_99"` on the new repository instance is still rejected (`false`), and verifies a legitimate new session (`"distinct_pl_session_100"`) commits and accumulates normally (`2` scores, `2` history rows, `totalAttempted == 20`).
3. `a05_viewModelFourModules_practiceLabMentalMathNumberSystemAndBookPracticeCommitAtomicallyOnce`:
   - Exercises `MathViewModel` end-to-end across `finishPracticeSessionNow()`, `finishMentalMathSession()`, `nextNumberSystemQuestion()`, and `finishBookPracticeSession()` with repeated completion invocations per session, verifying strictly 1 `QuizScoreEntity`, 1 `PracticeHistoryEntity`, and 1 `TopicAnalyticsEntity` per module plus learned progress items for Practice Lab and Mental Math.

### 6.5 Exact Test Command & Actual Results (`A-05`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase3DatabaseTransactionAndHistoryTest
  ```
- **Gradle Outcome**: `BUILD SUCCESSFUL in 1m 58s`
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase3DatabaseTransactionAndHistoryTest.xml` (`timestamp="2026-11-25T17:12:00.114Z"`)
  - **Tests Discovered & Executed**: `9`
  - **Passed**: `9`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a05_completePracticeSessionAtomically_allFourModulesPersistScoreHistoryAnalyticsAndRollbackCleanlyOnMidTransactionFailure`**: `PASSED (0.097s)`
  - **`a05_completePracticeSessionAtomically_duplicateAndConcurrentCompletionsAndDatabaseRecreationIdempotency`**: `PASSED (0.343s)`
  - **`a05_viewModelFourModules_practiceLabMentalMathNumberSystemAndBookPracticeCommitAtomicallyOnce`**: `PASSED (0.490s)`

---

## 7. Task `A-06` — Quiz Submission, Persistence Coordination & Finalization Race Fix

### 7.1 Root Cause
In `app/src/main/java/com/example/ui/MathViewModel.kt`, two related race and data-integrity defects existed across `submitQuizAnswer()` and `finishQuizSession()`:
1. **Missing Immediate-Feedback Skip Guard on the Correct-Answer (`else`) Branch in `finishQuizSession()`**:
   - In immediate-feedback mode (`!isFullExamMode`), `submitQuizAnswer()` already records mistake resolutions (`repository.markBcsMistakeResolved(...)` / `repository.resolveUnifiedMistake(...)`) and stores the submitted question index in `userAnswers`.
   - When constructing `questionMistakeOutcomes` inside `finishQuizSession()`, the wrong-answer branch (`ans != q.correctIndex`) checked `if (!winningCurrent.isFullExamMode && winningCurrent.userAnswers.containsKey(index)) return@mapIndexedNotNull null`, whereas the correct-answer (`else`) branch omitted that guard, causing every already-submitted correct answer in `!isFullExamMode` to be re-emitted into `questionMistakeOutcomes` and re-resolved during `finalizeExamAttempt`.
2. **Untracked Coroutine Persistence & Uncoordinated Finalization**:
   - `submitQuizAnswer()` updated `_quizSession.value` without an atomic CAS loop and launched an untracked `viewModelScope.launch` coroutine to persist mistake/resolution records.
   - `finishQuizSession()` did not await in-flight answer persistence coroutines before invoking `repository.finalizeExamAttempt(...)`, did not guard against stale old-session persistence completing after a new quiz session started, and did not roll back `_quizSession.value` if required answer persistence or exam finalization threw an exception.

### 7.2 Exact Modified Files
1. `app/src/main/java/com/example/ui/MathViewModel.kt` (`lines 102, 1179–1341, 2302–2374, 2378–2442, 2460, 2498, 2528, 2578–2726, 2850–3343, 3462–3464, 3488–3490, 3605–3607, 5444–5446, 5521–5523`)
2. `app/src/test/java/com/example/Phase4Wp41IntegrityRegressionTest.kt` (`lines 7, 25–26, 33, 35, 39, 42, 1311–1786`)

### 7.3 Implementation Details
- **Attempt-Scoped Persistence Tracking (`PendingQuizAnswerWrite`)**:
  - Added `PendingQuizAnswerWrite(attemptId, questionIndex, isCorrect, persistOperation, completion = CompletableDeferred<Result<Unit>>(), ...)` tracked per `(attemptId, questionIndex)` inside `pendingQuizAnswerWritesByAttempt` under `quizPersistenceLock`.
  - Rewrote `submitQuizAnswer()` with a `_quizSession.compareAndSet(current, nextState)` loop that rejects duplicate or post-finish submissions (`current.isAnswerSubmitted || current.isFinished || (!current.isFullExamMode && current.userAnswers.containsKey(current.currentIndex))`) and launches a tracked `PendingQuizAnswerWrite` via `launchQuizAnswerPersistenceWrite(write)`.
  - Guarded `selectQuizOption`, `setQuizTypedAnswer`, and `clearCurrentQuizAnswer` so already-submitted questions in immediate-feedback mode cannot be mutated or cleared.
- **Correct-Answer Skip Guard (`finishQuizSession`)**:
  - Added `if (!winningCurrent.isFullExamMode && winningCurrent.userAnswers.containsKey(index)) return@mapIndexedNotNull null` to the `else` (`ans == q.correctIndex`) branch in `finishQuizSession()` (`lines 3146–3148`), matching the wrong-answer branch (`lines 3123–3125`).
  - Unsubmitted draft answers (`pendingOptionSelections`, `pendingTypedAnswers`, or current `selectedOptionIndex` / `typedAnswer`) and all Full Exam (`isFullExamMode = true`) answers remain included in `questionMistakeOutcomes`.
- **Completion Synchronization & Duplicate-Finalization Protection**:
  - `finishQuizSession()` atomically claims an in-flight finalization slot in `inFlightQuizFinalizationAttemptIds` (rejecting any attempt already present in `finalizedQuizAttemptIds`, `inFlightQuizFinalizationAttemptIds`, or `supersededQuizAttemptIds`), snapshots `writesToAwait`, and awaits every `write.completion.await()` before acquiring `examPersistenceMutex` and invoking `repository.finalizeExamAttempt(...)`.
  - Upon successful `repository.finalizeExamAttempt(...)`, `winningAttemptId` is recorded in `finalizedQuizAttemptIds`.
- **Session Supersession Protection**:
  - Added monotonic sequence suffixes `_${bcsAttemptSeq.incrementAndGet()}` to `startQuiz` and `startConfiguredExam` `attemptId`s and called `supersedePreviousQuizSessionIfUnfinalized(previousAttemptId, newAttemptId)` across all session replacement entry points (`startQuiz`, `startConfiguredExam`, `resumeActiveExamAttempt`, `openCompletedExamResult`, `exitQuizToTopicList`, `startBcsModeExam`, `startBcsMockExam`).
  - If an older session is superseded while awaiting pending answer writes, its finalization coroutine aborts before calling `repository.finalizeExamAttempt(...)` and never mutates the newer session's `_quizSession.value`.
- **Failure & Cancellation Rollback**:
  - If any required `PendingQuizAnswerWrite` or `repository.finalizeExamAttempt(...)` fails or is cancelled, `winningAttemptId` is not marked committed in `finalizedQuizAttemptIds`, the error is recorded in `lastQuizPersistenceFailureByAttempt[winningAttemptId]`, and `rollbackUncommittedQuizFinalizationState(winningAttemptId, winningCurrent)` restores `_quizSession.value` to `isFinished = false` (only if `_quizSession.value.attemptId == winningAttemptId`) with all submitted answers preserved so a subsequent retry succeeds cleanly.

### 7.4 Regression Tests Added
Added three comprehensive regression test methods to `app/src/test/java/com/example/Phase4Wp41IntegrityRegressionTest.kt`:
1. `a06_correctAnswerSkipGuard_immediateAndFullExamMistakeOutcomeIsolation`:
   - Verifies in `!isFullExamMode` that an already-submitted correct answer (Q0) and an already-submitted wrong answer (Q1) are both excluded from `questionMistakeOutcomes` on `finishQuizSession()`, while an unsubmitted draft correct answer (Q2) is merged, scored, and included in `questionMistakeOutcomes`.
   - Verifies Q0's pre-existing unified mistake is resolved strictly once (`reviewIntervalDays: 1 -> 3`, not `7`), Q1's `failureCount` increments once (`1 -> 2`), Q2's mistake resolves once (`1 -> 3`), and post-submit mutation/clearing on Q0 is blocked.
   - Verifies in Full Exam mode (`isFullExamMode = true`) that both correct and wrong answers are included in `questionMistakeOutcomes` and persisted atomically on finish.
2. `a06_persistenceCoordination_finishAwaitsPendingWritesAndRollsBackOnFailure`:
   - Uses deterministic `CompletableDeferred<Unit>` gates (`q0Gate`, `q1Gate`) to suspend Q0 (correct) and Q1 (wrong) answer persistence while `finishQuizSession()` is called immediately.
   - Verifies `isQuizFinalizationInFlightForTesting(attemptId)` is `true`, `isQuizFinalizationCommittedForTesting(attemptId)` is `false`, and `FINALIZE_START` does not execute while answer writes are pending.
   - Injects a simulated Room failure on Q1's write, verifies `finalizeExamAttempt` does not execute, `isQuizFinalizationCommittedForTesting(attemptId)` remains `false`, `quizSession.value.isFinished` rolls back to `false` with both `userAnswers` preserved, and `getLastQuizPersistenceFailureForTesting(attemptId)` captures the exception.
   - Clears the fault and retries `finishQuizSession()`, verifying the failed answer write is retried and completes strictly before `FINALIZE_START` (`finalizeStartIdx > lastAnswerEndIdx`), and verifies final Room rows (`ExamAttemptEntity` `COMPLETED`, `UnifiedMistakeEntity`, and `QuizScoreEntity`).
3. `a06_raceAndSessionIsolation_concurrentSubmitFinishAndSupersededSessionGuard`:
   - Gates Session A's answer persistence, invokes `finishQuizSession()` on Session A, starts Session B while Session A's write is still suspended, and releases Session A's gate; verifies Session A is marked superseded, never runs `finalizeExamAttempt`, and never overwrites Session B's `_quizSession.value`.
   - Releases 8 concurrent threads calling `submitQuizAnswer()` via `CountDownLatch` on Session B Q0 (plus 4 sequential calls), verifying Q0 is scored and persisted strictly once (`answerWriteCountB == 1`).
   - Releases 8 concurrent threads calling `finishQuizSession()` via `CountDownLatch` on Session B (plus 4 sequential calls), verifying Session B finalizes strictly once (`finalizeCountB == 1`).

### 7.5 Exact Test Command & Actual Results (`A-06`)
- **Command**:
  ```bash
  gradle :app:testDebugUnitTest --tests com.example.Phase4Wp41IntegrityRegressionTest --rerun-tasks
  ```
- **Gradle Outcome**: `BUILD SUCCESSFUL`
- **JUnit XML Report Inspected**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase4Wp41IntegrityRegressionTest.xml` (`timestamp="2026-11-25T18:24:49.707Z"`)
  - **Tests Discovered & Executed**: `15`
  - **Passed**: `15`
  - **Failed**: `0`
  - **Skipped**: `0`
  - **Errors**: `0`
  - **`a06_correctAnswerSkipGuard_immediateAndFullExamMistakeOutcomeIsolation`**: `PASSED (0.132s)`
  - **`a06_persistenceCoordination_finishAwaitsPendingWritesAndRollsBackOnFailure`**: `PASSED (0.248s)`
  - **`a06_raceAndSessionIsolation_concurrentSubmitFinishAndSupersededSessionGuard`**: `PASSED (0.195s)`

---

## 8. Task `A-07` — Missing Class 9–10 PDF Detection, Centralized Safe Bengali Fallback & Entry-Point Accessibility Hardening

### 8.1 Original PDF Asset Status & Blocker
- **Present Offline PDFs (3 Books Only)**:
  - `app/src/main/assets/books/class_6.pdf`: `5,372,426` bytes (present & valid `%PDF-` header)
  - `app/src/main/assets/books/class_7.pdf`: `6,391,697` bytes (present & valid `%PDF-` header)
  - `app/src/main/assets/books/class_8.pdf`: `6,459,157` bytes (present & valid `%PDF-` header)
- **Missing / Blocked Offline PDF Asset (Class 9–10)**:
  - `app/src/main/assets/books/class_9_10.pdf` is **UNAVAILABLE** on disk and **not fabricated or substituted**.
  - Expected verified asset specification:
    - **Size**: `12,969,807` bytes
    - **SHA-256**: `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0`
    - **Pages**: `362`
  - **Feature Status**: Offline PDF rendering for Class 9–10 remains explicitly **BLOCKED** (3-PDF offline support only; four-PDF support is NOT claimed). All **17 structured Class 9–10 chapters** (lessons, formulas, worked examples, geometry theorems/constructions, and practice) remain fully available offline.

### 8.2 Centralized Fallback & UI Accessibility Refinements
- **Authoritative Centralized Fallback (`BookReaderAndLabsScreens.kt`)**:
  - `isOfflinePdfAssetAvailable`, `getBookPdfAvailabilityStatus`, `resolveOfflinePdfFile`, and `renderPdfPageWithAutoRecovery` detect missing Class 9–10 PDF without crashing.
  - `BookPdfReaderScreen` intercepts even pre-seeded `preferredViewMode = "PDF_BITMAP"` preferences when `!isPdfAvailable`, forces `"STRUCTURED"` mode, keeps `reader_mode_pdf_btn` disabled and unselected, displays the Bengali availability notice (`book_pdf_unavailable_fallback_banner`), renders `structured_textbook_page_card`, and never displays `pdf_rendered_page_card`, `pdf_loading_box`, or `pdf_render_error_box`.
- **Entry-Point UI & Accessibility Refinements**:
  1. `app/src/main/java/com/example/ui/screens/BookHubAndDetailScreens.kt`:
     - `BookCoverCard` (`btn_pdf_book_${book.id}` and header page summary badge): switches to `Icons.AutoMirrored.Filled.MenuBook`, `"ডিজিটাল বই"`, `"ডিজিটাল পৃষ্ঠা"` count, and truthful Bengali `contentDescription` (`"ডিজিটাল পাঠ্যবই কনটেন্ট খুলুন (মূল PDF অনুপলব্ধ)"`) when PDF is unavailable.
     - `BookSearchResultCard` (`book_search_result_page_${result.id}`): displays `Icons.AutoMirrored.Filled.MenuBook`, `"ডিজিটাল পৃষ্ঠা ..."` label, and truthful Bengali `contentDescription` when PDF is unavailable while preserving navigation to `BookPdfReaderScreen`.
     - `BookDetailScreen` (`chapter_page_chip_${bookId.id}_${chapter.chapterNumber}`, `book_detail_resume_pdf_btn`, and disabled `book_detail_pdf_btn`): uses digital-book icon, `"ডিজিটাল পৃষ্ঠা ..."` label, and truthful Bengali `contentDescription` while keeping `book_detail_pdf_btn` disabled and preserving structured reader navigation.
     - `BookChapterOverviewScreen` (`chapter_overview_pdf_btn` and subtitle): switches to `Icons.AutoMirrored.Filled.MenuBook` and truthful Bengali `contentDescription` (`"ডিজিটাল পাঠ্যবই কনটেন্টে দেখুন (মূল PDF অনুপলব্ধ)"`) while preserving navigation into structured `BookPdfReaderScreen`.
     - `BookLessonScreen` (`book_lesson_pdf_btn` and subtitle): switches to `Icons.AutoMirrored.Filled.MenuBook` and truthful Bengali `contentDescription` (`"ডিজিটাল পাঠ্যবই কনটেন্ট খুলুন (মূল PDF অনুপলব্ধ)"`) while preserving navigation into structured `BookPdfReaderScreen`.
  2. `app/src/main/java/com/example/ui/screens/BookmarksAndTutorScreens.kt`:
     - `BookmarksScreen` (`BOOK_PAGE` items): uses `Icons.AutoMirrored.Filled.MenuBook`, truthful Bengali `contentDescription` (`"ডিজিটাল পাঠ্যবই কনটেন্ট খুলুন (মূল PDF অনুপলব্ধ)"`), and structured digital-page detail text when the book's PDF is unavailable, preserving navigation to `BookPdfReaderScreen`.
  3. `app/src/main/java/com/example/ui/screens/BookPracticeAndRevisionScreens.kt`:
     - `BookRevisionScreen` Tab 3 (`revision_bookmarked_page_${bId.id}_$page`): uses `Icons.AutoMirrored.Filled.MenuBook`, `"ডিজিটাল পাঠ্যবই কনটেন্ট (মূল PDF অনুপলব্ধ)"` subtitle, and truthful Bengali `contentDescription` when the book's PDF is unavailable, preserving navigation to `BookPdfReaderScreen`.

### 8.3 Regression Tests Verified (`app/src/test/java/com/example/Phase3MemoryAndPdfCacheTest.kt`)
1. `a07_pdfResolverAndCurriculumIntegrity_class6To8Available_class910MissingDetected_all17ChaptersIntact` (isolated DB/prefs setup; accurately asserts worked examples for algebraic/applied chapters and theorems/constructions for geometry chapters 7 & 15)
2. `a07_class910BookDetailAndReader_gracefulBengaliFallback_pdfActionsDisabled_structuredNavigationIntact`
3. `a07_chapterOverviewAndLessonPageActions_missingPdf_openStructuredReaderAndKeepPdfDisabled` *(new)*
4. `a07_searchBookmarkAndPracticeRevisionPageRoutes_missingPdf_resolveSafelyThroughStructuredReader` *(new)*

### 8.4 Exact Test Commands & Fresh JUnit XML Evidence
- **Sequential & Combined Gradle Verification Runs**:
  - `gradle :app:testDebugUnitTest --tests com.example.Phase3MemoryAndPdfCacheTest` -> `BUILD SUCCESSFUL` (`8 / 8` passed)
  - `gradle :app:testDebugUnitTest --tests com.example.PrincipalAuditBug94To97RegressionTest` -> `BUILD SUCCESSFUL` (`5 / 5` passed)
  - `gradle :app:testDebugUnitTest --tests com.example.Phase3MemoryAndPdfCacheTest --tests com.example.PrincipalAuditBug94To97RegressionTest` -> `BUILD SUCCESSFUL` (retains both fresh XML reports simultaneously)
- **JUnit XML Report 1**: `app/build/test-results/testDebugUnitTest/TEST-com.example.Phase3MemoryAndPdfCacheTest.xml`
  - **XML Timestamp**: `2026-11-26T09:34:12.040Z` (file mtime `2026-11-26T09:34:35.067Z` — strictly newer than all source edits `09:20:53Z`–`09:29:35Z`)
  - **Executed**: `8` | **Passed**: `8` | **Failed**: `0` | **Errors**: `0` | **Skipped**: `0`
- **JUnit XML Report 2**: `app/build/test-results/testDebugUnitTest/TEST-com.example.PrincipalAuditBug94To97RegressionTest.xml`
  - **XML Timestamp**: `2026-11-26T09:34:33.011Z` (file mtime `2026-11-26T09:34:35.067Z` — strictly newer than all source edits)
  - **Executed**: `5` | **Passed**: `5` | **Failed**: `0` | **Errors**: `0` | **Skipped**: `0`

---

## 9. Packet A Closure & Verification Distinction
- **Fallback implementation verified**: Yes — `A-01` through `A-07` software fixes, UI accessibility refinements, and all fallback/alternative navigation regression tests passed on JVM/Robolectric with fresh XML evidence.
- **Original Class 9–10 PDF unavailable**: Yes — `app/src/main/assets/books/class_9_10.pdf` (`12,969,807` bytes, SHA-256 `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0`, `362` pages) remains missing; offline PDF rendering for Class 9–10 remains explicitly **BLOCKED** (3-PDF offline support for Classes 6–8 only; all 17 Class 9–10 structured digital chapters remain available).
- **Physical-device validation not performed**: Yes — all verification in this environment ran under local JVM / Robolectric + Compose UI tests (`No Android Emulator or ADB available`).
- **Packet A Status**: **CLOSED** (all Packet A software defects `A-01` through `A-07` resolved and verified; `BCS-GC-02`, Study Timer production code, and Room migrations untouched).
