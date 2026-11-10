# Math Master — Engineering, Audit, QA & Release Documentation Index

Welcome to the engineering documentation directory for **Math Master** (`com.aistudio.applet.ghqqzu`), a 100% offline-first Bengali mathematics learning, NCTB textbook (`Class 6–10`, `4` PDFs totaling `894` PDF pages / `874` printed pages across `47` chapters), problem-solving, and BCS preparation Android application built with Kotlin, Jetpack Compose (Material 3), and Room Database v9 (`20` `@Entity` tables).

- **Official GitHub Repository:** [`https://github.com/rocky-ahmed/Math-Master`](https://github.com/rocky-ahmed/Math-Master) (`main` branch)

---

## 1. Final Project Stabilization & Audit Reports (11 Core Final Deliverables)

| # | Document Path | Category | Summary of Contents |
| :- | :--- | :--- | :--- |
| 1 | [`audits/FINAL_PROJECT_AUDIT_REPORT.md`](audits/FINAL_PROJECT_AUDIT_REPORT.md) | Final Project Audit | Complete inventory of all 14 modules, 30 screens, MVVM architecture, Room v9 schema (20 `@Entity` tables), 4 NCTB PDFs (`161 + 181 + 190 + 362 = 894` pages), and `BUG-01`..`BUG-39` summary |
| 2 | [`audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md`](audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md) | Unified Defect Register | Reconciled root-cause analysis, severity classification, modified files, and test mappings for all **39 defects (`BUG-01` through `BUG-39`)** with preserved historical Bug IDs |
| 3 | [`audits/FINAL_NAVIGATION_STATE_RESTORATION_AUDIT.md`](audits/FINAL_NAVIGATION_STATE_RESTORATION_AUDIT.md) | Navigation & Scroll Audit | Complete 30-destination scroll & context state inventory, architectural root-cause analysis, and verification matrix for `BUG-36` through `BUG-39` |
| 4 | [`engineering/FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md`](engineering/FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md) | Engineering Implementation | Code-level explanation of root-cause fixes (`BUG-22` through `BUG-39`, referencing `BUG-01`..`BUG-21`) across Numerical Keypad Scoring & Negative Marking (`BUG-01`), SharedMathEngine, ExamEngine, Book Practice, BCS, Number System, Dashboard KPIs, Unified Mistake Notebook, Book PDF Reader Concurrency / `Mutex` / `LruCache` (`BUG-35`), Home Screen Scroll Restoration (`BUG-36`), and Application-Wide Scroll/Context Restoration (`BUG-37`–`BUG-39`) |
| 5 | [`audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md`](audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md) | Mathematical Accuracy | Validation of `SharedMathEngine` (`BUG-07`, `BUG-29`, `BUG-30`), `BookQuestionEngine` fractional/ratio equivalence (`BUG-26`), `MathProblemSolverEngine` (14 topics, `BUG-08`), `NumberSystemModels`, and `VisualMathModels` |
| 6 | [`bcs/FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md`](bcs/FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md) | Exam & BCS Validation | Validation of `ExamScoringEngine` Numerical Keypad Scoring & Negative Marking (`BUG-01`) and Weak-Topic Guards (`BUG-34`), 8 exam presets, 6 `BcsExamProfile` modes (`BUG-05`), 1,060+ questions (`BUG-06`), 50 study guides, draft preservation (`BUG-33`), and profile/mistake sync (`BUG-13`, `BUG-20`, `BUG-21`, `BUG-32`) |
| 7 | [`database/FINAL_DATABASE_INTEGRITY_REPORT.md`](database/FINAL_DATABASE_INTEGRITY_REPORT.md) | Room v9 & Migrations | Complete inventory of all **20 Room `@Entity` tables** and non-destructive `MIGRATION_1_2`..`MIGRATION_8_9` verification (`BUG-10`) |
| 8 | [`book/FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md`](book/FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md) | NCTB Books & PDFs | Verification of `class_6.pdf` (`161` pp.), `class_7.pdf` (`181` pp.), `class_8.pdf` (`190` pp.), and `class_9_10.pdf` (`362` pp.) — `894` PDF pages, `47` chapters, exact byte sizes & SHA-256 checksums, `BookPdfRenderCache` concurrency/memory/`Mutex`/`LruCache` optimization (`BUG-35`), and mistake sync (`BUG-31`) |
| 9 | [`performance/FINAL_PERFORMANCE_AND_UX_REPORT.md`](performance/FINAL_PERFORMANCE_AND_UX_REPORT.md) | Performance & UX | Audit of `collectAsStateWithLifecycle()` (`BUG-18`), `BookPdfRenderCache` `LruCache(12)` & `Mutex` (`BUG-35`), SQLite v9 secondary indexes (`BUG-10`), and navigation / application-wide scroll restoration polish (`BUG-11`, `BUG-12`, `BUG-22`..`BUG-39`) |
| 10 | [`qa/FINAL_REGRESSION_TEST_REPORT.md`](qa/FINAL_REGRESSION_TEST_REPORT.md) | Automated QA & Testing | Complete breakdown of **`184 / 184` passing unit and Robolectric tests** (`0` failures, `0` errors) across all 9 test suites, reconciled with historical `165`, `167`, `172`, and `175` baselines |
| 11 | [`audits/FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md`](audits/FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md) | Security & Organization | Credential safety verification, `.gitignore` hygiene (`/public/assets/aistudio/`, `BUG-28`), and repository organization for Codex GitHub synchronization |

---

## 2. Historical Engineering & Phase Reports

| # | Document Path | Category | Summary of Contents |
| :- | :--- | :--- | :--- |
| 12 | [`audits/COMPLETE_PROJECT_ENGINEERING_AUDIT.md`](audits/COMPLETE_PROJECT_ENGINEERING_AUDIT.md) | Historical Audit | Earlier full-stack audit covering `BUG-01`..`BUG-21` |
| 13 | [`audits/ROOT_CAUSE_DEFECT_REGISTER.md`](audits/ROOT_CAUSE_DEFECT_REGISTER.md) | Historical Defect Register | Canonical Phase 1 defect register for `BUG-01`..`BUG-21` (`BUG-01`: Numerical Keypad Scoring and Negative Marking) |
| 14 | [`engineering/BUG_FIX_IMPLEMENTATION_REPORT.md`](engineering/BUG_FIX_IMPLEMENTATION_REPORT.md) | Historical Bug Fixes | Implementation notes for `BUG-01`..`BUG-21` |
| 15 | [`audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md`](audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md) | Historical Math Audit | Earlier mathematical engine validation report |
| 16 | [`bcs/EXAM_ENGINE_AND_BCS_VALIDATION_REPORT.md`](bcs/EXAM_ENGINE_AND_BCS_VALIDATION_REPORT.md) | Historical BCS Audit | Earlier BCS syllabus & provenance report |
| 17 | [`database/DATABASE_AND_MIGRATION_VALIDATION_REPORT.md`](database/DATABASE_AND_MIGRATION_VALIDATION_REPORT.md) | Historical DB Report | Earlier Room v9 schema & migration report |
| 18 | [`book/BOOK_PDF_CONTENT_VALIDATION_REPORT.md`](book/BOOK_PDF_CONTENT_VALIDATION_REPORT.md) | Historical Book Report | Earlier NCTB PDF content validation report (`161 + 181 + 190 + 362 = 894` pages) |
| 19 | [`audits/OFFLINE_DEPENDENCY_AUDIT.md`](audits/OFFLINE_DEPENDENCY_AUDIT.md) | Offline Dependency Audit | Verification that 100% of app features operate offline without network calls or cloud dependencies |
| 20 | [`performance/UI_UX_AND_PERFORMANCE_AUDIT.md`](performance/UI_UX_AND_PERFORMANCE_AUDIT.md) | Historical UI/UX Audit | Earlier Compose UI/UX and performance audit |
| 21 | [`release/FINAL_RELEASE_CANDIDATE_REPORT.md`](release/FINAL_RELEASE_CANDIDATE_REPORT.md) | Release Candidate | Historical release candidate verification report (`165 / 165` Phase 1 baseline) |
| 22 | [`qa/FINAL_SECURITY_AND_GITHUB_SYNC_REPORT.md`](qa/FINAL_SECURITY_AND_GITHUB_SYNC_REPORT.md) | GitHub Sync Guide | Historical security and GitHub sync report |
| 23 | [`architecture/BACKEND_ARCHITECTURE_PLAN.md`](architecture/BACKEND_ARCHITECTURE_PLAN.md) | System Architecture | High-level backend and data-flow architecture specification |

---

## 3. Canonical Bug ID Traceability (`BUG-01` through `BUG-39`)

- **`BUG-01`**: **Numerical Keypad Scoring and Negative Marking** (`ExamEngineModels.kt`, `MathViewModel.kt`, `QuizzesScreen.kt` — `-1` unanswered sentinel vs. `-2` wrong numerical input sentinel).
- **`BUG-02` – `BUG-21`**: Core Phase 1 Engineering Audit defects (Exam stem deduplication, `v2` session persistence, `MonotonicClock`, 6 BCS profiles, provenance, `SharedMathEngine` `Rational` equivalence, Problem Solver guards, `+5` PDF page offset `BUG-09`, Room `v1..v9` migrations `BUG-10`, Home Grid adaptive layout `BUG-11`, `BackHandler` `BUG-12`, BCS profile isolation `BUG-13`, untimed timers `BUG-14`, Review Incorrect filter `BUG-15`, strict `NUMERICAL`/`TRUE_FALSE` `BUG-16`, Number System `KEYPAD`/`TRUE_FALSE` `BUG-17`, `collectAsStateWithLifecycle()` `BUG-18`, Formula Library bookmark deep-link `BUG-19`, Dashboard BCS deduplication `BUG-20`, multi-profile BCS mistake resolution `BUG-21`).
- **`BUG-22` – `BUG-28`**: Phase 2 Pre-Freeze Stabilization defects (Book Practice bidirectional navigation, Number System streak/duration/mistake logging, cross-module `unified_mistake_notebook` sync, Dashboard `BOOK_` KPI deduplication, `BookQuestionEngine` rational equivalence, `BookRevisionScreen` book filter chips, repository `.gitignore` hygiene).
- **`BUG-29` – `BUG-34`**: Phase 3 Independent Adversarial Bug Hunt defects (dotless textbook unit suffixes & mixed fractions, distributive algebraic simplification, Book mistake/spaced-revision ID normalization, canonical BCS question ID resolution, unsubmitted quiz draft preservation, zero-attempt weak-topic guard).
- **`BUG-35`**: **Book PDF Reader Concurrency, Bitmap Memory Management, `Mutex` Serialization & `LruCache` Optimization** (`BookReaderAndLabsScreens.kt` — `BookPdfRenderCache` `Mutex` serialization, bounded `LruCache<String, Bitmap>(12)`, `Bitmap.Config.RGB_565`, OOM fallback, and synchronized atomic PDF extraction).
- **`BUG-36`**: **Home Screen Scroll Position Restoration** (`MainActivity.kt`, `HomeScreen.kt`, `MathViewModel.kt` — `HomeScrollPosition` + `SaveableStateHolder` + `LazyGridState.Saver` preserving exact `firstVisibleItemIndex` and pixel `firstVisibleItemScrollOffset` across module navigation).
- **`BUG-37`**: **Application-Wide Nested Navigation Scroll State Loss** (`NavigationStateRestoration.kt`, `MathViewModel.kt`, `MainActivity.kt`, all 19 screen files — `rememberManagedLazyListState` / `rememberManagedLazyGridState` / `rememberManagedScrollState` + argument-scoped `getDestinationStateKey` + `_navigationContextStack`).
- **`BUG-38`**: **Internal Tab, Subsection, and Filter Context Reset on Re-Entry** (`NavigationStateRestoration.kt`, `MathViewModel.kt`, multi-tab module screens — `rememberManagedUiState` + per-tab scroll keys).
- **`BUG-39`**: **Bottom-Navigation Tab State Eviction on Tab Switch** (`MainActivity.kt` — persistent `SaveableStateHolder` retention across all 5 core Bottom-Navigation tabs: `Home`, `Tables`, `Dashboard`, `Bookmarks`, `Quizzes`).


