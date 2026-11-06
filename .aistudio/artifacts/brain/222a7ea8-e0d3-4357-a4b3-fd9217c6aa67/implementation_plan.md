# Math Master — Next-Generation Examination Center, Learning Analytics Dashboard & Full-App Intelligence Upgrade

A comprehensive backend, data persistence, and interactive UI upgrade transforming Math Master into a unified, offline-first mathematics examination, learning analytics, and BCS competitive preparation ecosystem while preserving all existing user data, architecture, and working modules.

### User Review & Critical Decisions

> [!IMPORTANT]
> All Phase 1 user decisions and scope clarifications have been locked into this specification. None of the five selected P1 enhancement areas have been omitted or deferred.

- **Confirmed Decision 1 — Competitive Exam Presets & Negative Marking Policy**: The Examination Center will include quick presets (Quick Test, Topic-wise Test, Mixed Mathematics Test, Timed Mock Exam, Full-Length Mock Exam, Custom Exam, Practice Examination, Review Incorrect Answers, and BCS Preliminary Math Mock Preset) with negative marking **disabled (`0.0`) by default** and explicitly configurable (`0.25` or `0.50` per wrong answer) only when toggled by the user.
- **Confirmed Decision 2 — Configurable Daily Study Goals & Habit Tracking**: The Progress & Statistics Dashboard will provide user-configurable **daily question targets** (default: 20 questions/day) and **daily study time targets** (default: 15 minutes/day) persisted in local storage, paired with timezone-safe study streak tracking, a study activity calendar, and a weekly consistency score.
- **Confirmed Decision 3 — Complete Combined 5-Part P1 Enhancement Scope**: All **five** secondary-module upgrades will be implemented as one integrated scope alongside the Priority 1 Examination System and Priority 2 Analytics Dashboard:
  1. **Formula Library**: Interactive Variable Calculator (instant evaluation with variable definitions, substitution steps, and domain validation) + Formula Mini-Quiz with instant feedback.
  2. **Problem Solver**: Automatic local Saved Solution History (revisit, review, delete) + Similar Problem Generator across Easy, Medium, and Hard levels with step-by-step Bengali verification.
  3. **Visual Math Explainer**: Post-concept Mini Knowledge Checks with immediate feedback + direct Practice Lab / Quiz deep links and Formula Library / Problem Solver connections.
  4. **Real Numbers & Divisibility**: Guided Step-by-Step Number Explorer (`ℕ`, `W`, `ℤ`, `ℚ`, `ℚ'`, `ℝ`) with interactive number-line representations, set-relationship cards, interactive classification exercises, and topic mini-quizzes.
  5. **Dedicated BCS Mathematics Preparation Mode**: Organized by the verified official BPSC BCS Preliminary Mathematical Reasoning syllabus (Arithmetic, Algebra, Geometry & Mensuration, Set/Probability/Permutation), featuring authentic previous BCS questions with verified exam references (clearly distinguished from generated practice items), side-by-side Shortcut Techniques and Standard Step-by-Step Bengali Solutions, personalized weak-topic detection, and dedicated BCS Practice & Mock Tests.

---

### 1. Overview & Core Concept

- **What It Does**: Evolves Math Master from a collection of standalone learning modules into a single connected learning intelligence platform. Every quiz, full examination, practice lab session, mental math sprint, formula calculation, solved problem, visual exploration, and BCS preparation drill feeds into a unified Room v4 persistence layer and a deterministic Learning Analytics & Recommendation Engine.
- **Current Baseline Findings (Phase Zero Audit)**:
  - **Database**: Currently at **Room version 3** (`BookmarkEntity`, `QuizScoreEntity`, `ProgressEntity`, `PracticeHistoryEntity`, `TopicAnalyticsEntity`, `RecentFormulaEntity`) with non-destructive `MIGRATION_1_2` and `MIGRATION_2_3`.
  - **Quiz Engine**: Currently supports a 10-question linear flow (`QuizMode`: `TOPIC`, `MIXED`, `TIMED`, `PRACTICE`, `DIFFICULTY_BASED`, `REVIEW_INCORRECT`) and 4 question types (`MCQ`, `TRUE_FALSE`, `NUMERICAL`, `MIXED`), but lacks custom exam configuration, multi-topic selection, negative marking, free question palette navigation, mark-for-review, persistent in-progress attempt recovery across process recreation, and rich per-question exam history.
  - **Dashboard**: Currently displays study streaks, a 7-day bar chart, static syllabus progress bars (`ProgressEntity`), and basic `QuizScoreEntity` summaries, but lacks time-window filtering (`Today`, `7d`, `30d`, `This Month`, `90d`, `All Time`), distinct Exam vs. Practice KPIs, confidence-weighted topic mastery classifications, configurable daily targets, and a deterministic recommendation engine.
- **Target Audience / Persona**: Bengali-speaking students and BCS / competitive job examination aspirants who need realistic timed mock examinations, shortcut vs. foundational problem-solving mastery, and actionable analytics to eliminate weak topics.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  - **Interactive Examination Center Flow**:
    1. *Configuration*: User selects an Exam Type preset or Custom Exam, picks single/multiple/all topics, chooses difficulty (`EASY`, `MEDIUM`, `HARD`, `MIXED`), sets question count, duration, marks per correct answer, optional negative marking (off by default), randomization toggles, and feedback policy, or resumes an interrupted active exam banner.
    2. *Active Examination*: Displays exam header, lifecycle-safe monotonic countdown timer, question counter, mathematical expression card, input control (MCQ options, True/False, or Bengali/ASCII numerical keypad), **Mark for Review** toggle, **Clear Answer** button, **Previous / Next** free navigation, and a collapsible **Question Palette** showing 5 distinct states (*Not Visited*, *Answered*, *Unanswered/Visited*, *Marked for Review*, *Answered & Marked for Review*).
    3. *Submission Confirmation & Result Analysis*: Pre-submit modal summarizes Total, Answered, Unanswered, Marked for Review, and Remaining Time. Submitting opens the Result Dashboard with final score, negative-mark breakdown, accuracy, average time per question, topic-wise and difficulty-wise breakdown cards, Weak Area badges, and actions to *Review All Questions*, *Retry Incorrect Questions*, *Practice Weak Topics*, or view *Exam History*.
  - **Comprehensive Analytics Dashboard Flow**:
    - Users filter analytics by time range (`আজ / Today`, `৭ দিন / 7D`, `৩০ দিন / 30D`, `এই মাস / This Month`, `৯০ দিন / 90D`, `সর্বমোট / All Time`) and module, inspect separated Examination KPIs vs. Practice KPIs, track daily question & study-time goals, explore confidence-weighted Topic Mastery cards (*Strong*, *Weak*, *Improving*, *Declining*, *Untouched*), and tap deterministic Learning Recommendations that deep-link directly into targeted Practice, Formula Review, or Mock Exams.
  - **Combined P1 Secondary Modules & BCS Preparation Flow**:
    - *Formula Library*: Users open any formula, enter custom variable values in the **Interactive Variable Calculator** to see live step-by-step substitution and exact results, or launch a 3–5 question **Formula Mini-Quiz**.
    - *Problem Solver*: Every solved problem is automatically persisted to **Saved Solution History**; users can revisit or delete past solutions and tap **Generate Similar Problems** (`Easy`, `Medium`, `Hard`) with full Bengali step-by-step verification.
    - *Visual Math Explainer*: After exploring an interactive canvas, users answer a **Mini Knowledge Check** card and use one-tap links to jump to related Practice Lab drills, Formula Library entries, or Problem Solver topics.
    - *Real Numbers Explorer*: Users step through a **Guided Number Set Explorer** (`ℕ ⊂ W ⊂ ℤ ⊂ ℚ ⊂ ℝ` and `ℚ'`) with interactive number-line plotting and solve interactive Set Classification exercises.
    - *Dedicated BCS Mathematics Preparation Mode*: Accessible from the Home screen and Examination Center; users browse official BPSC BCS Preliminary syllabus categories, study **Authentic Previous BCS Questions** (with verified exam number/year tags, separated from generated practice questions), toggle between **Standard Step-by-Step Method** and **Smart MCQ Shortcut Technique**, view personalized BCS weak topics, and launch **BCS Mock Tests**.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Retains the established Bengali-first Material 3 educational card design while adding structured examination palette indicators and clean analytical data visualizations.
  - *Color Palette & Semantic States*:
    - Primary Indigo (`#2563EB`), Secondary Teal (`#0D9488`), Tertiary Amber (`#D97706`).
    - Question Palette Semantics: Answered (`#10B981` Emerald), Unanswered/Visited (`#EF4444` Rose), Marked for Review (`#7C3AED` Purple), Answered & Marked (`#0284C7` Sky with check badge), Not Visited (`surfaceVariant` neutral).
  - *Accessibility & Responsiveness*: 48dp minimum touch targets, overflow-safe mathematical expression cards, adaptive grid/column layouts for portrait and landscape orientations, and explicit empty/partial/error states without fake placeholder numbers.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Unified Question & Examination Engine Without Duplication**
  - *Chosen Approach*: Extend the existing `QuizGenerator`, `PracticeLabGenerator`, and `SharedMathEngine` into a unified `ExamEngine` and `ExamQuestionBank` that powers both all six legacy `QuizMode` workflows (`TOPIC`, `MIXED`, `TIMED`, `PRACTICE`, `DIFFICULTY_BASED`, `REVIEW_INCORRECT`) and the new full Examination Center modes, Formula Mini-Quizzes, and BCS Mock Tests.
  - *Why*: Eliminates competing question models, guarantees exact mathematical validation (`Rational` / `BigDecimal`) and unique distractor generation everywhere, and keeps existing automated tests green.
- **Decision 2: Non-Destructive Room v4 Upgrade (`MIGRATION_3_4`) with Transactional Autosave**
  - *Chosen Approach*: Upgrade `MathDatabase` from version 3 to version 4 via an explicit `MIGRATION_3_4` that preserves all six v3 tables untouched and adds `exam_attempts` (persisting both active in-progress state and completed exam records with serialized question/answer snapshots), `solver_history` (persisting solved problems), and `study_goals_daily` (persisting daily question/time targets and daily study logs).
  - *Why*: Autosaving active exam state (`questionsJson`, `userAnswersJson`, `markedQuestionsJson`, `currentIndex`, `elapsedSeconds`, `deadlineEpochMs`) inside Room allows seamless recovery if the Activity or process is recreated mid-exam, without losing user answers or resetting the countdown timer.
- **Decision 3: Single Source of Truth & Strict Separation of Exam vs. Practice Metrics**
  - *Chosen Approach*: Define deterministic, non-overlapping KPI formulas in a dedicated `LearningAnalyticsEngine`. Completed formal exams and quizzes are recorded in `exam_attempts` (with backwards-compatible summary sync to `quiz_scores`), while Practice Lab, Mental Math, and Number System drills record in `practice_history` and `topic_analytics` with explicit `module` tags (`EXAM`, `QUIZ`, `PRACTICE_LAB`, `MENTAL_MATH`, `NUMBER_SYSTEM`, `BCS_PREP`).
  - *Why*: Prevents double-counting questions across tables and allows the Dashboard to report both overall learning volume and distinct Exam vs. Practice accuracy.
- **Decision 4: Verified Authentic BCS Questions vs. Labeled Practice Generators**
  - *Chosen Approach*: Tag every question in the BCS Mathematics Preparation Mode with an explicit `SourceProvenance` (`OFFICIAL_BCS` with verified exam badge such as `৪৫তম বিসিএস`, `৪৪তম বিসিএস`, `৪৩তম বিসিএস`, `৪১তম বিসিএস`, `৪০তম বিসিএস`, `৩৮তম বিসিএস`, `৩৭তম বিসিএস`, `৩৬তম বিসিএস`, `৩৫তম বিসিএস` vs. `SYLLABUS_PRACTICE` clearly labeled as `বিসিএস সিলেবাস অনুশীলন প্রশ্ন`).
  - *Why*: Strictly honors the requirement never to fabricate an official BCS question or year while providing unlimited syllabus-aligned practice alongside authentic past papers.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### Architecture & Component Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER (Jetpack Compose + M3)                     │
│                                                                                      │
│  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌──────────────────────┐  │
│  │ Priority 1:             │  │ Priority 2:             │  │ Priority 3 (All 5 P1)│  │
│  │ Examination Center UI   │  │ Learning Analytics      │  │ • Formula Calc & Quiz│  │
│  │ • ExamConfigScreen      │  │ Dashboard UI            │  │ • Solver History/Gen │  │
│  │ • ActiveExamScreen      │  │ • Time/Topic Filters    │  │ • Visual Mini-Checks │  │
│  │   (Palette, Timer, Nav) │  │ • KPI & Mastery Cards   │  │ • Real Numbers Guide │  │
│  │ • ExamResult & History  │  │ • Goals & Recommenders  │  │ • BCS Prep Mode Hub  │  │
│  └────────────┬────────────┘  └────────────┬────────────┘  └──────────┬───────────┘  │
└───────────────┼────────────────────────────┼──────────────────────────┼──────────────┘
                │                            ▼                          │
┌───────────────┴───────────────────────────────────────────────────────┴──────────────┐
│                         VIEWMODEL & SESSION ORCHESTRATION LAYER                      │
│                                                                                      │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │ MathViewModel                                                                  │  │
│  │ • ExamSessionController (Monotonic Timer, Autosave & Process-Death Recovery)   │  │
│  │ • LearningAnalyticsAggregator (Time Windows, Mastery Classifier, Rule Engine)  │  │
│  │ • Secondary Module Controllers (FormulaCalc, SolverHistory, BCSPrep, Explorer) │  │
│  └─────────────────────────────────────────┬──────────────────────────────────────┘  │
└────────────────────────────────────────────┼─────────────────────────────────────────┘
                                             ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                       SHARED MATHEMATICS & ASSESSMENT DOMAIN LAYER                   │
│                                                                                      │
│  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌──────────────────────┐  │
│  │ ExamScoring & Question  │  │ LearningAnalyticsEngine │  │ BCS & Secondary      │  │
│  │ Engine                  │  │ • Exact KPI Formulas    │  │ Engines              │  │
│  │ • Reproducible Seeded Qs│  │ • Sample-Size Confidence│  │ • FormulaEvaluator   │  │
│  │ • Exact Distractor Gen  │  │ • Trend & Streak Calc   │  │ • SimilarProblemGen  │  │
│  │ • Negative Mark Scorer  │  │ • Deterministic Advice  │  │ • BcsQuestionBank    │  │
│  └─────────────────────────┘  └─────────────────────────┘  └──────────────────────┘  │
└────────────────────────────────────────────┬─────────────────────────────────────────┘
                                             ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                     OFFLINE PERSISTENCE LAYER (Room SQLite v3 ──► v4)                │
│                                                                                      │
│  ┌────────────────────────────────────────────────────────────────────────────────┐  │
│  │ MathRepository ──► MathDao ──► MathDatabase (Version 4, Non-Destructive)       │  │
│  │ • Preserved v3: bookmarks, quiz_scores, user_progress, practice_history,       │  │
│  │                 topic_analytics, recent_formulas                               │  │
│  │ • Added in v4:  exam_attempts (active & completed), solver_history,            │  │
│  │                 daily_study_activity                                           │  │
│  └────────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

#### KPI Definitions & Mastery Rules (Single Source of Truth)

1. **Examination vs. Practice Separation**:
   - **Total Completed Exams**: Count of `ExamAttemptEntity` records where `status == COMPLETED` (plus legacy `QuizScoreEntity` records not linked to a v4 attempt or practice session, preventing double counting).
   - **Average & Best Exam Score (%)**: Mean and maximum of `percentage` across completed exam attempts within the selected time filter.
   - **Practice Sessions Completed**: Count of `PracticeHistoryEntity` records where `module IN ('PRACTICE_LAB', 'MENTAL_MATH', 'NUMBER_SYSTEM', 'FORMULA_MINI_QUIZ', 'BCS_PRACTICE')`.
   - **Total Questions Attempted / Correct / Incorrect**: Deduped sum of attempted, correct, and wrong questions across completed exams and practice sessions within the selected date window.
   - **Overall Accuracy (%)**: `(totalCorrect * 100) / max(1, totalCorrect + totalIncorrect)`.
   - **Total Learning Time**: Sum of `durationSeconds` across completed exams and practice sessions within the selected date window.
2. **Topic Mastery & Sample-Size Confidence Rules**:
   - A topic is **never** classified as Mastered from 1–2 questions.
   - **Confidence Level**:
     - `INSUFFICIENT_DATA`: `< 5` questions attempted (`অপর্যাপ্ত তথ্য — অন্তত ৫টি প্রশ্ন অনুশীলন করুন`).
     - `LOW_CONFIDENCE`: `5–9` questions attempted.
     - `MODERATE_CONFIDENCE`: `10–19` questions attempted.
     - `HIGH_CONFIDENCE`: `≥ 20` questions attempted.
   - **Classification**:
     - `UNTESTED (অনুশীলন করা হয়নি)`: `0` attempts.
     - `NEEDS_PRACTICE (আরও অনুশীলন প্রয়োজন)`: `1–4` attempts.
     - `WEAK (দুর্বল টপিক — রিভিশন প্রয়োজন)`: `≥ 5` attempts and `accuracy < 60%`.
     - `DEVELOPING (উন্নয়নশীল)`: `≥ 5` attempts and `accuracy 60%–79%` (or `≥ 80%` with `< 10` attempts).
     - `MASTERED (আয়ত্তকৃত / শক্তিশালী টপিক)`: `≥ 10` attempts and `accuracy ≥ 80%`.
   - **Trend Detection (`IMPROVING` / `DECLINING` / `STABLE`)**: Compares accuracy of the most recent 3 sessions for that topic against earlier sessions (`≥ +8%` = Improving, `≤ -8%` = Declining).
3. **Deterministic Recommendation Engine**:
   - Evaluates topic mastery, recent incorrect exam questions, slow average solve time (`> 45s/question`), and untouched BCS/core topics to emit ranked, explainable recommendations (each with a transparent `"কেন এই পরামর্শ:"` rationale and a direct navigation action).

#### Database Schema & Non-Destructive Room v4 Migration (`MIGRATION_3_4`)

- **Version Increment**: `MathDatabase` version `3 → 4`.
- **New Entities in v4**:
  1. `ExamAttemptEntity` (`tableName = "exam_attempts"`, indexed on `status`, `timestamp`, `examType`):
     - Stores `attemptId` (PK), `examType`, `titleBn`, `topicsCsv`, `difficulty`, `totalQuestions`, `durationSeconds`, `marksPerCorrect`, `negativeMarksPerWrong`, `allowNegativeMarking`, `status` (`IN_PROGRESS`, `COMPLETED`, `ABANDONED`), `currentIndex`, `questionsPayload` (deterministic serialized snapshot of questions/options), `answersPayload` (map of user answers & typed inputs), `reviewFlagsPayload` (marked-for-review indices), `visitedFlagsPayload`, `correctCount`, `wrongCount`, `unansweredCount`, `earnedMarks`, `negativeMarksDeducted`, `finalScore`, `maxMarks`, `percentage`, `accuracyPercent`, `timeSpentSeconds`, `startedAtEpochMs`, `deadlineEpochMs`, `timestamp`.
  2. `SolverHistoryEntity` (`tableName = "solver_history"`, indexed on `topicId`, `timestamp`):
     - Stores `id` (PK), `topicId`, `topicTitleBn`, `inputExpression`, `finalAnswerBn`, `exactFractionOrValue`, `stepsLog`, `verificationBn`, `timestamp`.
  3. `DailyStudyActivityEntity` (`tableName = "daily_study_activity"`, primary key `dateStr` `"yyyy-MM-dd"`):
     - Stores `dateStr`, `questionsAttempted`, `correctCount`, `studyTimeSeconds`, `examsCompleted`, `practiceSessionsCompleted`, `targetQuestions`, `targetTimeSeconds`, `updatedAt`.
- **Migration Guarantee**: `MIGRATION_3_4` uses `CREATE TABLE IF NOT EXISTS` and `CREATE INDEX IF NOT EXISTS` only. All existing v1–v3 tables and records remain 100% intact. `fallbackToDestructiveMigration` is strictly forbidden.

#### Implementation Order (Controlled Engineering Phases)

1. **Phase A & B — Room v4 Schema, Migration (`MIGRATION_3_4`), and Examination Domain Engine**:
   - Add v4 entities, DAO methods, repository transactions, `ExamModels`, `ExamQuestionBank`, `ExamScoringEngine`, and `ExamSessionController` with autosave and active-attempt recovery.
2. **Phase C & D — Interactive Examination Center UI, Result Analysis & Exam History**:
   - Upgrade `QuizzesScreen` and `QuizPlayScreen` to support both all 6 existing `QuizMode`s and the full Examination Configurator, Question Palette, Mark for Review, Clear Answer, Pre-Submit Confirmation Modal, Result Analysis Dashboard, and filterable/sortable Exam History with attempt comparison and deletion confirmation.
3. **Phase E & F — Learning Analytics Engine & Upgraded Progress Dashboard**:
   - Implement `LearningAnalyticsEngine` (date filters, separated Exam vs. Practice KPIs, confidence-weighted Topic Mastery, configurable Daily Study Goals, Study Activity Calendar, and Deterministic Recommendations) and upgrade `DashboardScreen`.
4. **Phase G & H — Combined 5-Part P1 Secondary Module Upgrades & Cross-Module Integration**:
   - **1. Formula Library**: Add `FormulaVariableCalculator` (with substitution steps & division-by-zero/domain guards) and `FormulaMiniQuiz` modal/sheet inside `FormulaLibraryScreen`.
   - **2. Problem Solver**: Add automatic persistence to `solver_history`, a Saved History tab/sheet with review & delete, and a `SimilarProblemGenerator` (`Easy`, `Medium`, `Hard`) in `ProblemSolverScreen`.
   - **3. Visual Math Explainer**: Add interactive `VisualKnowledgeCheck` cards after explanations and direct navigation links to Practice Lab, Quiz, Formula Library, and Problem Solver in `VisualExplorerScreen`.
   - **4. Real Numbers & Divisibility**: Add the `GuidedNumberSetExplorer` (`ℕ, W, ℤ, ℚ, ℚ', ℝ`), interactive number-line classification visualizers, and Set Classification mini-quizzes in `NumberSystemScreen`.
   - **5. Dedicated BCS Mathematics Preparation Mode**: Create the `BcsPreparationModels` & `BcsPreparationScreen` (linked from Home, Quizzes, and Dashboard) with official BPSC syllabus organization, authentic previous BCS questions (with verified exam year badges), Shortcut vs. Standard Step-by-Step Bengali solutions, weak-topic detection, and BCS Mock Tests.
5. **Phase I & J — Automated Testing, Migration Verification, Build & Engineering Report**:
   - Run existing tests, add comprehensive unit and Robolectric tests covering Examination flows, Scoring, Timer/Recovery, Dashboard KPIs, Room `MIGRATION_3_4`, and all 5 P1 upgrades, verify compilation via `compile_applet`, and produce the complete 20-section Engineering Report.
