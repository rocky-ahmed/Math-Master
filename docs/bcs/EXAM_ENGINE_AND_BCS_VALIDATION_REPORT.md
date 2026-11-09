# MATH MASTER — EXAM ENGINE AND BCS VALIDATION REPORT

## 1. Examination Engine Architecture (`ExamEngineModels.kt`, `QuizModels.kt`, `MathViewModel.kt`)

### 1.1 Exam Presets & Configurations (`ExamPresetType`)
The examination engine supports 6 presets:
1. **`QUICK_TEST`**: 10 questions, 10 minutes, +1.0 / 0.0 negative marking.
2. **`FULL_MOCK_TEST`**: 25 questions, 25 minutes, +1.0 / -0.25 negative marking.
3. **`BCS_PRELIMINARY_MOCK`**: 35 questions (20 Mathematical Reasoning + 15 Mental Ability), 35 minutes, +1.0 / -0.50 negative marking (authentic BPSC standard).
4. **`CHAPTER_EXAM`**: 15 questions, 15 minutes, +1.0 / -0.25 negative marking.
5. **`PRACTICE_EXAM`**: Untimed (`durationMinutes = 0`, `configuredDurationSeconds = 0`), immediate per-question feedback, upward elapsed time tracking only.
6. **`CUSTOM_EXAM`**: User-configurable question count, duration, negative marking (`0.0`, `0.25`, `0.50`), difficulty, and question type (`MCQ`, `NUMERICAL`, `TRUE_FALSE`, `MIXED`).

### 1.2 Scoring & Negative Marking Invariants (`ExamScoringEngine`)
- **Sentinel Handling (`userAnswers: Map<Int, Int>`)**:
  - `null` or `-1` (`UNANSWERED_OPTION_SENTINEL`): Treated as **Unanswered** (`0.0` marks, `0.0` deduction).
  - `-2` (`WRONG_NUMERICAL_SENTINEL`): Used when a user submits a numerical keypad value that does not match the correct answer or any listed option index. Treated as **Answered Wrong** (`+1` to `attemptedCount`, `+1` to `wrongCount`, deducts `negativeMarksPerWrong`).
  - `idx in 0..3`: Treated as **Correct** if `idx == question.correctIndex` (`+marksPerCorrect`), or **Answered Wrong** if `idx != question.correctIndex` (`-negativeMarksPerWrong`).
- **Question Palette States (`QuestionPaletteState`)**:
  - `NOT_VISITED`, `VISITED_UNANSWERED`, `ANSWERED`, `MARKED_FOR_REVIEW`, `ANSWERED_AND_MARKED`.
  - `computePaletteState()` uses `isOptionAnswered(selectedAnswers[questionIndex])` so `-2` (wrong numerical answer) is accurately shown as `ANSWERED` (or `ANSWERED_AND_MARKED`) on the palette.

### 1.3 Timer & Lifecycle Resilience (`BUG-14` Validated)
- **Timed Exams (`configuredDurationSeconds > 0`, `!immediateFeedback`, `mode != QuizMode.PRACTICE`)**:
  - Uses monotonic `SystemClock.elapsedRealtime()` (`deadlineElapsedRealtimeMs`) so countdown calculation is immune to Compose frame drops or wall-clock adjustments.
  - Auto-submits cleanly when `rem <= 0`.
- **Untimed Quizzes & Practice Modes (`QuizMode.TOPIC`, `QuizMode.PRACTICE`, `QuizMode.MIXED`, `PRACTICE_EXAM`, `TOPIC_PRACTICE`, `MISTAKE_PRACTICE`)**:
  - `configuredDurationSeconds = 0` and `deadlineElapsedRealtimeMs = 0L`.
  - Tracks `elapsedSeconds` upward only without countdown auto-submission.

### 1.4 Pause, Resume & Process Recreation Persistence (`BUG-02` Validated)
- Active full exams are persisted to `ExamAttemptEntity` (`status = "IN_PROGRESS"`) on every answer selection, typed keypad input, palette jump, or review mark toggle.
- `ExamQuestionBank.serializeAnswersWithTyped()` and `deserializeTypedMap()` encode non-option typed strings (`idx:val~b64_<Base64>`) alongside integer indices so resuming an exam restores both `userAnswers` and `userTypedAnswers` with zero data loss.

---

## 2. BCS Mathematical Reasoning & Mental Ability Center (`BcsPreparationModels.kt`, `BcsExamProfileModels.kt`)

### 2.1 Multi-Profile Architecture (4 Profiles)
1. **`BCS_50TH_PRELIM` (`BCS_50TH_PRELIM_2025`)**: 20 Math + 15 Mental Ability marks, -0.50 negative marking, 35-minute full mock.
2. **`BCS_WRITTEN_008_MATH` (`BCS_WRITTEN_008_MATH`)**: Subject Code 008 Written Mathematical Reasoning & Mental Ability (50 Math + 50 Mental Ability marks, 12-step written solutions).
3. **`BCS_51ST_SPECIAL` (`BCS_51ST_SPECIAL`)**: Upcoming 51st & Special BCS preparation profile.
4. **`BCS_LEGACY_PRELIM` (`BCS_LEGACY_10_49`)**: Historical 10th–49th BCS Preliminary archive profile.

### 2.2 Content & Practice Modes Verified
- **5 Syllabus Units (`BcsSyllabusUnit`)**:
  1. `UNIT_1_ARITHMETIC` (বাস্তব সংখ্যা, ভগ্নাংশ, শতকরা, লাভ-ক্ষতি, সুদকষা, অনুপাত)
  2. `UNIT_2_ALGEBRA` (বীজগাণিতিক সূত্র, উৎপাদক, সূচক, লগারিদম, সমীকরণ, অসমতা)
  3. `UNIT_3_GEOMETRY` (রেখা, কোণ, ত্রিভুজ, বৃত্ত, পিথাগোরাস ও পরিমিতি)
  4. `UNIT_4_SET_PROBABILITY` (সেট, ভেনচিত্র, সমান্তর ও গুণোত্তর ধারা, বিন্যাস, সমাবেশ ও সম্ভাব্যতা)
  5. `UNIT_5_MENTAL_ABILITY` (ভাষাগত যৌক্তিক বিচার, সমস্যা সমাধান, বানান ও ভাষা, যান্ত্রিক দক্ষতা, স্থানাঙ্ক সম্পর্ক, সংখ্যাগত ক্ষমতা)
- **7 BCS Practice Modes (`BcsPracticeMode`)**:
  - Timed Modes: `FULL_PRELIM_35`, `MATH_20_MOCK`, `MENTAL_15_MOCK`, `PREVIOUS_BCS_SIMULATION`, `TIMED_TOPIC_TEST` (countdown timer enabled).
  - Practice Modes: `TOPIC_PRACTICE`, `MISTAKE_PRACTICE` (immediate feedback enabled, countdown disabled, upward elapsed time tracked).
- **Profile-Scoped Attribution**:
  - `BcsTopicProgressEntity.scopedKey(profileId, rawTopicId)` stores per-profile progress in Room v9 while `rawTopicId` allows deduplicated cross-profile reporting on the Dashboard (`BUG-20`).
