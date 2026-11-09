# Math Master — Final Exam and BCS Validation Report

**Scope**: Quiz & Exam Engine (`QuizModels.kt`, `ExamEngineModels.kt`), BCS Preparation Module (`BcsExamProfileModels.kt`, `BcsPreparationModels.kt`, `BcsCurriculumAndGuideRepository.kt`), Negative Marking, Timer Integrity, and Multi-Profile Persistence.

---

## 1. Quiz & Exam Engine Validation

### 1.1 Exam Presets & Question Generation (`ExamEngineModels.kt`, `QuizModels.kt`)
- **8 Exam Presets Verified**:
  1. `QUICK_TEST` (১০ প্রশ্ন • ১০ মিনিট)
  2. `STANDARD_TEST` (২০ প্রশ্ন • ২০ মিনিট)
  3. `FULL_MODEL_TEST` (৩০ প্রশ্ন • ৩০ মিনিট)
  4. `PRACTICE_EXAM` (আনলিমিটেড/আনটাইমড অনুশীলন)
  5. `BCS_PRELIMINARY_MOCK` (বিসিএস প্রিলিমিনারি মক — গাণিতিক যুক্তি ও মানসিক দক্ষতা)
  6. `BANK_JOB_MATH` (ব্যাংক জব ম্যাথ মক)
  7. `PRIMARY_NTRCA_EXAM` (প্রাইমারি ও শিক্ষক নিবন্ধন মক)
  8. `WEAK_TOPIC_DRILL` (দুর্বল টপিক ফোকাসড ড্রিল)
- **3 Question Formats Verified**:
  - `MCQ` (4 distinct options with verified `correctIndex in 0..3`)
  - `TRUE_FALSE` (2 options: `"সত্য (True)"`, `"মিথ্যা (False)"` with `correctIndex in 0..1`)
  - `NUMERICAL` (Keypad input with guaranteed numeric `correctAnswerText` validated via `SharedMathEngine.isAnswerCorrect`)

### 1.2 Timer & Auto-Submit Integrity (`BUG-14` Verified)
- **Timed Modes** (`QuizMode.TIMED` and timed `ExamConfig` presets):
  - Driven by `MonotonicClock` (`deadlineElapsedRealtimeMs`), immune to UI frame drops or recomposition delays.
  - Automatically invokes `finishQuizSession()` when `remainingSec <= 0`.
- **Untimed Practice Modes** (`QuizMode.TOPIC`, `QuizMode.PRACTICE`, `QuizMode.MIXED`, `ExamPresetType.PRACTICE_EXAM`, `BcsPracticeMode.TOPIC_PRACTICE`, `BcsPracticeMode.MISTAKE_PRACTICE`):
  - Strictly set `configuredDurationSeconds = 0` and `deadlineElapsedRealtimeMs = 0L`.
  - Count elapsed time upward (`elapsedSeconds`) without ever triggering premature auto-submission.

### 1.3 Negative Marking & Review Incorrect Accuracy (`BUG-11`, `BUG-15` Verified)
- **Negative Marking Formula**:
  $$\text{Net Score} = \max\left(0.0,\; \text{Correct} \times 1.0 - \text{Wrong} \times \text{Penalty}\right)$$
  - Supports `0.0`, `0.25`, and `0.50` (BCS Preliminary standard) penalty per wrong answer.
  - Unanswered (`null`) questions incur **zero penalty** (`0.0`).
- **Review Incorrect Filtering**:
  - `startReviewIncorrectQuiz()` includes only questions the user actively answered wrong (`chosen != null && chosen != q.correctIndex`), excluding skipped/unanswered items.

---

## 2. BCS Preparation Module Validation

### 2.1 All 6 BCS Exam Profiles Verified (`BcsExamProfileModels.kt`)
1. **47th BCS Written (`BCS_47TH_WRITTEN`)** — গাণিতিক যুক্তি (৫০ নম্বর) ও মানসিক দক্ষতা (৫০ নম্বর)
2. **48th Special BCS (`BCS_48TH_SPECIAL`)** — বিশেষ বিসিএস প্রিলিমিনারি ও লিখিত প্রস্তুতি
3. **49th Special BCS (`BCS_49TH_SPECIAL`)** — বিশেষ বিসিএস গণিত ও মানসিক দক্ষতা
4. **50th BCS Preliminary & Written (`BCS_50TH_PRELIM`)** — ৩৫ নম্বর প্রিলিমিনারি (২০ গণিত + ১৫ মানসিক দক্ষতা)
5. **51st Special BCS (`BCS_51ST_SPECIAL`)** — বিশেষ বিসিএস সিলেবাস ও মডেল টেস্ট
6. **General Competitive Exam (`BCS_GENERAL_COMPETITIVE`)** — বিসিএস, ব্যাংক, প্রাইমারি ও NTRCA সমন্বিত ট্র্যাক

### 2.2 Question Bank, 50 Topic Guides & Multi-Profile Isolation
- **Question Bank**: `1,060+` curated questions (`560` Mathematical Reasoning across 12 syllabus units + `500` Mental Ability across 6 categories), plus `90+` Historical BCS Archive questions (10th–46th BCS) and `24` Written Constructed-Response problems.
- **50 Study Guides**: Every syllabus unit and mental ability category includes concept summaries, key formulas, worked examples, BCS shortcuts, common traps, and quick practice links.
- **Profile-Scoped Persistence (`BUG-20`, `BUG-21` Verified)**:
  - `bcs_topic_progress`, `bcs_mistake_notebook`, and `bcs_study_goals` are scoped by `examProfileId` (`"$examProfileId::$id"`), preventing cross-profile data overwrites while deduplicating `rawTopicId` counts on the Hub overview.
