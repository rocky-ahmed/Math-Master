# Math Master — Final Exam and BCS Validation Report

**Scope**: Quiz & Exam Engine (`QuizModels.kt`, `ExamEngineModels.kt`), BCS Preparation Module (`BcsExamProfileModels.kt`, `BcsPreparationModels.kt`, `BcsCurriculumAndGuideRepository.kt`), Negative Marking (`BUG-01`), Weak Topic Diagnostics (`BUG-34`), Timer & Draft Integrity (`BUG-04`, `BUG-14`, `BUG-33`), and Multi-Profile Persistence & Mistake Sync (`BUG-05`, `BUG-06`, `BUG-13`, `BUG-20`, `BUG-21`, `BUG-32`).

---

## 1. Quiz & Exam Engine Validation

### 1.1 Exam Presets & Question Generation (`ExamEngineModels.kt`, `QuizModels.kt`, `BUG-02`, `BUG-16`)
- **8 Exam Presets Verified**:
  1. `QUICK_TEST` (১০ প্রশ্ন • ১০ মিনিট)
  2. `STANDARD_TEST` (২০ প্রশ্ন • ২০ মিনিট)
  3. `FULL_MODEL_TEST` (৩০ প্রশ্ন • ৩০ মিনিট)
  4. `PRACTICE_EXAM` (আনলিমিটেড/আনটাইমড অনুশীলন)
  5. `BCS_PRELIMINARY_MOCK` (বিসিএস প্রিলিমিনারি মক — গাণিতিক যুক্তি ও মানসিক দক্ষতা)
  6. `BANK_JOB_MATH` (ব্যাংক জব ম্যাথ মক)
  7. `PRIMARY_NTRCA_EXAM` (প্রাইমারি ও শিক্ষক নিবন্ধন মক)
  8. `WEAK_TOPIC_DRILL` (দুর্বল টপিক ফোকাসড ড্রিল)
- **3 Question Formats Verified (`BUG-16`)**:
  - `MCQ` (4 distinct options with verified `correctIndex in 0..3`)
  - `TRUE_FALSE` (2 options: `"সত্য (True)"`, `"মিথ্যা (False)"` with `correctIndex in 0..1`)
  - `NUMERICAL` (Keypad input with guaranteed numeric `correctAnswerText` validated via `SharedMathEngine.isAnswerCorrect`)

### 1.2 Timer, Auto-Submit & Draft Integrity (`BUG-04`, `BUG-14`, `BUG-33` Verified)
- **Timed Modes** (`QuizMode.TIMED` and timed `ExamConfig` presets):
  - Driven by `MonotonicClock` (`deadlineElapsedRealtimeMs`), immune to UI frame drops or recomposition delays (`BUG-04`).
  - Automatically invokes `finishQuizSession()` when `remainingSec <= 0`, rescuing any unsubmitted pending option or typed answer drafts across visited questions (`BUG-33`).
- **Untimed Practice Modes** (`QuizMode.TOPIC`, `QuizMode.PRACTICE`, `QuizMode.MIXED`, `ExamPresetType.PRACTICE_EXAM`, `BcsPracticeMode.TOPIC_PRACTICE`, `BcsPracticeMode.MISTAKE_PRACTICE`):
  - Strictly set `configuredDurationSeconds = 0` and `deadlineElapsedRealtimeMs = 0L` (`BUG-14`).
  - Count elapsed time upward (`elapsedSeconds`) without ever triggering premature auto-submission.

### 1.3 Negative Marking, Review Incorrect & Weak Topic Diagnostics (`BUG-01`, `BUG-15`, `BUG-34` Verified)
- **Negative Marking Formula (`BUG-01`)**:
  $$\text{Net Score} = \max\left(0.0,\; \text{Correct} \times 1.0 - \text{Wrong} \times \text{Penalty}\right)$$
  - Supports `0.0`, `0.25`, and `0.50` (BCS Preliminary standard) penalty per wrong answer (`-2` sentinel for wrong non-distractor numerical keypad answers).
  - Unanswered (`null` or `-1`) questions incur **zero penalty** (`0.0`).
- **Review Incorrect Filtering (`BUG-15`)**:
  - `startReviewIncorrectQuiz()` includes only questions the user actively answered wrong (`ExamScoringEngine.isOptionAnswered(chosen) && chosen != q.correctIndex`), excluding skipped/unanswered items.
- **Zero-Attempt Topic Exclusion from `weakTopics` (`BUG-34`)**:
  - `ExamScoringEngine.evaluateExam()` requires `attemptedCount > 0` before classifying a topic with `accuracyPercent < 65.0` as a `weakTopic`, ensuring skipped topics with `0` wrong answers are never mislabeled as weak areas.

---

## 2. BCS Preparation Module Validation

### 2.1 All 6 BCS Exam Profiles Verified (`BcsExamProfileModels.kt`, `BUG-05`)
1. **50th BCS Preliminary (`BCS_50TH_PRELIM_2025`, Default)** — ৩৫ নম্বর প্রিলিমিনারি (২০ গাণিতিক যুক্তি + ১৫ মানসিক দক্ষতা)
2. **BCS Written Combined (`BCS_WRITTEN_COMBINED_100`)** — ১০০ নম্বর লিখিত (৫০ গণিত + ৫০ মানসিক দক্ষতা)
3. **BCS Written Mathematical Reasoning (`BCS_WRITTEN_MATH_008`)** — বিষয় কোড ০০৮ (৫০ নম্বর)
4. **BCS Written Mental Ability (`BCS_WRITTEN_MENTAL_009`)** — বিষয় কোড ০০৯ (৫০ নম্বর)
5. **51st Special BCS (`BCS_51ST_SPECIAL_2026`)** — বিশেষ বিসিএস (১০ গণিত + ১০ মানসিক দক্ষতা = ২০ নম্বর)
6. **Legacy 35th–49th BCS Preliminary (`BCS_LEGACY_35_TO_49_PRELIM`)** — ৩০ নম্বর প্রিলিমিনারি (১৫ গণিত + ১৫ মানসিক দক্ষতা)

### 2.2 Question Bank, 50 Topic Guides & Multi-Profile Isolation (`BUG-06`, `BUG-13`, `BUG-20`, `BUG-21`, `BUG-32`)
- **Question Bank**: `1,060+` curated questions (`560` Mathematical Reasoning across 12 syllabus units + `500` Mental Ability across 6 categories), plus `90+` Historical BCS Archive questions (10th–49th BCS) and `24` Written Constructed-Response problems.
- **50 Study Guides**: Every syllabus unit and mental ability category includes concept summaries, key formulas, worked examples, BCS shortcuts, common traps, and quick practice links.
- **Profile-Scoped Persistence & Canonical Mistake Sync (`BUG-13`, `BUG-20`, `BUG-21`, `BUG-32` Verified)**:
  - `bcs_topic_progress`, `bcs_mistake_notebook`, and `bcs_study_goals` are scoped by `examProfileId` (`"$examProfileId::$id"`), preventing cross-profile data overwrites while deduplicating `rawTopicId` counts on the Hub and Dashboard (`BUG-13`, `BUG-20`, `BUG-21`).
  - `MathRepository.extractCanonicalBcsQuestionId()` strips mode-specific prefixes (`bcs_exam_`, `bcs_prev_`, `bcs_mistake_retry_`, `bcs_ma_exam_`) and `_rep_` suffixes so retrying a question in `MISTAKE_PRACTICE` or Question Bank resolves the canonical mistake across both `bcs_mistake_notebook` and `unified_mistake_notebook` (`BUG-32`).
