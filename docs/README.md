# Math Master — Engineering, Audit, QA & Release Documentation Index

Welcome to the engineering documentation directory for **Math Master** (`com.aistudio.applet.ghqqzu`), a 100% offline-first Bengali mathematics learning, NCTB textbook (`Class 6–10`, `4` PDFs totaling `894` PDF pages / `874` printed pages across `47` chapters), problem-solving, and BCS preparation Android application built with Kotlin, Jetpack Compose (Material 3), and Room Database v9 (`20` `@Entity` tables).

- **Official GitHub Repository:** [`https://github.com/rocky-ahmed/Math-Master`](https://github.com/rocky-ahmed/Math-Master) (`main` branch)

---

## 1. Final Project Stabilization & Audit Reports (10 Core Final Deliverables)

| # | Document Path | Category | Summary of Contents |
| :- | :--- | :--- | :--- |
| 1 | [`audits/FINAL_PROJECT_AUDIT_REPORT.md`](audits/FINAL_PROJECT_AUDIT_REPORT.md) | Final Project Audit | Complete inventory of all 14 modules, 30 screens, MVVM architecture, Room v9 schema (20 `@Entity` tables), 4 NCTB PDFs (`161 + 181 + 190 + 362 = 894` pages), and `BUG-01`..`BUG-36` summary |
| 2 | [`audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md`](audits/FINAL_ROOT_CAUSE_DEFECT_REGISTER.md) | Unified Defect Register | Reconciled root-cause analysis, severity classification, modified files, and test mappings for all **36 defects (`BUG-01` through `BUG-36`)** with preserved historical Bug IDs |
| 3 | [`engineering/FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md`](engineering/FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md) | Engineering Implementation | Code-level explanation of root-cause fixes (`BUG-22` through `BUG-36`, referencing `BUG-01`..`BUG-21`) across SharedMathEngine, ExamEngine, Book Practice, BCS, Number System, Dashboard KPIs, Unified Mistake Notebook, PDF Reader (`BUG-35`), and Home Screen Scroll Restoration (`BUG-36`) |
| 4 | [`audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md`](audits/FINAL_MATHEMATICAL_ACCURACY_REPORT.md) | Mathematical Accuracy | Validation of `SharedMathEngine` (`BUG-07`, `BUG-29`, `BUG-30`), `BookQuestionEngine` fractional/ratio equivalence (`BUG-26`), `MathProblemSolverEngine` (14 topics, `BUG-08`), `NumberSystemModels`, and `VisualMathModels` |
| 5 | [`bcs/FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md`](bcs/FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md) | Exam & BCS Validation | Validation of `ExamScoringEngine` (`BUG-01`, `BUG-34`), 8 exam presets, 6 `BcsExamProfile` modes (`BUG-05`), 1,060+ questions (`BUG-06`), 50 study guides, draft preservation (`BUG-33`), and profile/mistake sync (`BUG-13`, `BUG-20`, `BUG-21`, `BUG-32`) |
| 6 | [`database/FINAL_DATABASE_INTEGRITY_REPORT.md`](database/FINAL_DATABASE_INTEGRITY_REPORT.md) | Room v9 & Migrations | Complete inventory of all **20 Room `@Entity` tables** and non-destructive `MIGRATION_1_2`..`MIGRATION_8_9` verification (`BUG-10`) |
| 7 | [`book/FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md`](book/FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md) | NCTB Books & PDFs | Verification of `class_6.pdf` (`161` pp.), `class_7.pdf` (`class_7.pdf` `181` pp.), `class_8.pdf` (`190` pp.), and `class_9_10.pdf` (`362` pp.) — `894` PDF pages, `47` chapters, exact byte sizes & SHA-256 checksums, `BookPdfRenderCache` (`BUG-35`), and mistake sync (`BUG-31`) |
| 8 | [`performance/FINAL_PERFORMANCE_AND_UX_REPORT.md`](performance/FINAL_PERFORMANCE_AND_UX_REPORT.md) | Performance & UX | Audit of `collectAsStateWithLifecycle()` (`BUG-18`), `BookPdfRenderCache` `LruCache(12)` & `Mutex` (`BUG-35`), SQLite v9 secondary indexes (`BUG-10`), and navigation/scroll polish (`BUG-11`, `BUG-12`, `BUG-22`..`BUG-36`) |
| 9 | [`qa/FINAL_REGRESSION_TEST_REPORT.md`](qa/FINAL_REGRESSION_TEST_REPORT.md) | Automated QA & Testing | Complete breakdown of **`175 / 175` passing unit and Robolectric tests** (`0` failures, `0` errors) across all 8 test suites, reconciled with historical `165`, `167`, and `172` baselines |
| 10 | [`audits/FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md`](audits/FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md) | Security & Organization | Credential safety verification, `.gitignore` hygiene (`/public/assets/aistudio/`, `BUG-28`), and repository organization for Codex GitHub synchronization |

---

## 2. Historical Engineering & Phase Reports

| # | Document Path | Category | Summary of Contents |
| :- | :--- | :--- | :--- |
| 11 | [`audits/COMPLETE_PROJECT_ENGINEERING_AUDIT.md`](audits/COMPLETE_PROJECT_ENGINEERING_AUDIT.md) | Historical Audit | Earlier full-stack audit covering `BUG-01`..`BUG-21` |
| 12 | [`audits/ROOT_CAUSE_DEFECT_REGISTER.md`](audits/ROOT_CAUSE_DEFECT_REGISTER.md) | Historical Defect Register | Canonical Phase 1 defect register for `BUG-01`..`BUG-21` |
| 13 | [`engineering/BUG_FIX_IMPLEMENTATION_REPORT.md`](engineering/BUG_FIX_IMPLEMENTATION_REPORT.md) | Historical Bug Fixes | Implementation notes for `BUG-01`..`BUG-21` |
| 14 | [`audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md`](audits/MATHEMATICAL_ENGINE_VALIDATION_REPORT.md) | Historical Math Audit | Earlier mathematical engine validation report |
| 15 | [`bcs/EXAM_ENGINE_AND_BCS_VALIDATION_REPORT.md`](bcs/EXAM_ENGINE_AND_BCS_VALIDATION_REPORT.md) | Historical BCS Audit | Earlier BCS syllabus & provenance report |
| 16 | [`database/DATABASE_AND_MIGRATION_VALIDATION_REPORT.md`](database/DATABASE_AND_MIGRATION_VALIDATION_REPORT.md) | Historical DB Report | Earlier Room v9 schema & migration report |
| 17 | [`book/BOOK_PDF_CONTENT_VALIDATION_REPORT.md`](book/BOOK_PDF_CONTENT_VALIDATION_REPORT.md) | Historical Book Report | Earlier NCTB PDF content validation report (`161 + 181 + 190 + 362 = 894` pages) |
| 18 | [`audits/OFFLINE_DEPENDENCY_AUDIT.md`](audits/OFFLINE_DEPENDENCY_AUDIT.md) | Offline Dependency Audit | Verification that 100% of app features operate offline without network calls or cloud dependencies |
| 19 | [`performance/UI_UX_AND_PERFORMANCE_AUDIT.md`](performance/UI_UX_AND_PERFORMANCE_AUDIT.md) | Historical UI/UX Audit | Earlier Compose UI/UX and performance audit |
| 20 | [`release/FINAL_RELEASE_CANDIDATE_REPORT.md`](release/FINAL_RELEASE_CANDIDATE_REPORT.md) | Release Candidate | Historical release candidate verification report (`165 / 165` Phase 1 baseline) |
| 21 | [`qa/FINAL_SECURITY_AND_GITHUB_SYNC_REPORT.md`](qa/FINAL_SECURITY_AND_GITHUB_SYNC_REPORT.md) | GitHub Sync Guide | Historical security and GitHub sync report |
| 22 | [`architecture/BACKEND_ARCHITECTURE_PLAN.md`](architecture/BACKEND_ARCHITECTURE_PLAN.md) | System Architecture | High-level backend and data-flow architecture specification |
