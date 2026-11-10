# Math Master (গণিত মাস্টার)

**Math Master** is a 100% offline-first Android mathematics education, NCTB textbook (`Class 6–10`), interactive math lab, step-by-step problem solver, and BCS preparation application written in **Kotlin** with **Jetpack Compose (Material 3)** and **Room Database (v9)**.

- **Official GitHub Repository:** [`https://github.com/rocky-ahmed/Math-Master`](https://github.com/rocky-ahmed/Math-Master) (`main` branch)
- **Application ID:** `com.aistudio.applet.ghqqzu` (`namespace = "com.example"`)
- **Version:** `1.0` (`versionCode = 1`, `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`)

---

## 1. Key Capabilities & 14 Interactive Modules

1. **নামতা ও গুণন (`TablesScreen`)** — Interactive multiplication tables (`1..40` core & extended drills), step-by-step patterns, mastery tracking, and practice tutor.
2. **মৌলিক সংখ্যা (`PrimesScreen`)** — Sieve of Eratosthenes visualizer (`1..200`), primality checker, prime factorization, and twin/co-prime explorer.
3. **বর্গ, ঘন ও মূল (`SquaresCubesScreen`)** — Squares, cubes, square roots, cube roots (`1..20`), and digital root patterns.
4. **বীজগণিতের সূত্রাবলি (`FormulasScreen`)** — Core algebraic identities, geometric visual proofs, and interactive numerical substitution.
5. **সমন্বিত সূত্র ভাণ্ডার (`FormulaLibraryScreen`)** — `65+` / `100+` formulas across Arithmetic, Algebra, Geometry, Mensuration, Trigonometry, Combinatorics, and Statistics (`NCTB`, `BDMO`, and `BCS` curriculum tracks) with bookmark deep-linking (`BUG-19`) and mastery tracking.
6. **অনুশীলন ল্যাব (`PracticeLabScreen`)** — Multi-format interactive drills (`MCQ`, `KEYPAD`, `TRUE_FALSE`) with instant diagnostic feedback and unified mistake sync (`BUG-24`, `BUG-29`).
7. **মানসিক গণিত চ্যালেঞ্জ (`MentalMathScreen`)** — Timed mental arithmetic speed drills, streak multipliers, and mental calculation tricks (`BUG-24`).
8. **বাস্তব সংখ্যা ও বিভাজ্যতা (`NumberSystemScreen`)** — Number base converter (`Binary`, `Octal`, `Decimal`, `Hex`), Euclidean GCD/LCM, divisibility rules (`2..19`), modular exponentiation, divisor analysis, and `MCQ`/`KEYPAD`/`TRUE_FALSE` practice (`BUG-17`, `BUG-23`).
9. **গাণিতিক সমাধানকারী ইঞ্জিন (`ProblemSolverScreen`)** — Deterministic offline step-by-step Bengali problem solver across 14 mathematical topics in `LEARNING`, `SHORTCUT`, and `BCS` modes, plus distributive algebraic simplification (`BUG-08`, `BUG-30`).
10. **ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (`VisualExplorerScreen`)** — Interactive Canvas visualizations for fractions, geometry, coordinate planes, and statistical charts.
11. **কুইজ ও পরীক্ষা কেন্দ্র (`QuizzesScreen`)** — Full Exam Center supporting Quick Quizzes, Topic Exams, Custom Exams, Mistake Revision Exams, and BCS Preliminary Mocks (`MCQ`, `NUMERICAL`, `TRUE_FALSE`) with exact session persistence, monotonic timer tracking, unsubmitted draft preservation, and zero-attempt topic guards (`BUG-01`..`BUG-04`, `BUG-14`..`BUG-16`, `BUG-32`..`BUG-34`).
12. **অগ্রগতি ও বিশ্লেষণ ড্যাশবোর্ড (`DashboardScreen`)** — Cross-module accuracy analytics, daily streak tracking, 25-minute Pomodoro study timer, unified mistake notebook, and deduplicated Exam vs. Practice KPIs (`BUG-20`, `BUG-25`).
13. **পাঠ্যবই ও ইন্টারেক্টিভ ল্যাব (`BookHubAndDetailScreens` / `BookReaderAndLabsScreens`)** — Bundled offline NCTB textbooks for **Class 6 (`161` PDF pp.), Class 7 (`181` PDF pp.), Class 8 (`190` PDF pp.), and Class 9–10 (`362` PDF pp.)** (**4 PDFs, `894` PDF pages / `874` printed pages, `47` chapters, `31,193,087` bytes**), hybrid structured + native `PdfRenderer` reader (`+5` printed page offset, `BookPdfRenderCache` `Mutex` + `LruCache(12)` + `RGB_565`, `1.0x`–`3.0x` zoom, `BUG-09`, `BUG-35`), 12 interactive math labs, chapter notes, and spaced-repetition revision (`BUG-22`, `BUG-26`, `BUG-27`, `BUG-31`).
14. **বিসিএস প্রস্তুতি (`BcsPreparationScreen`)** — 6 official BPSC exam profiles (`50th BCS Prelim 35 Marks`, `Written Combined 100 Marks`, `Written Code 008 Math 50 Marks`, `Written Code 009 Mental Ability 50 Marks`, `51st Special 20 Marks`, `Legacy 35th–49th Prelim 30 Marks`), 8-stage preparation journey, 9 math domains, 6 mental ability categories, provenance-verified question archive, and profile-isolated progress & canonical mistake persistence (`BUG-05`, `BUG-06`, `BUG-13`, `BUG-21`, `BUG-32`).

---

## 2. Verified Bundled NCTB PDF Assets (`app/src/main/assets/books/`)

| Book ID | Asset Path | Exact Size (Bytes) | SHA-256 Checksum | PDF Pages | Printed Pages (`+5` Offset) | Chapters |
| :--- | :--- | ---: | :--- | :-: | :-: | :-: |
| **`CLASS_6`** | `app/src/main/assets/books/class_6.pdf` | `5,372,426` | `124f9e02f1acc31cc54cbec797c1efbe7cf823c2e63050492581634bb174e5db` | **`161`** | `156` (`1–149` + Ans `150–155`) | `8` |
| **`CLASS_7`** | `app/src/main/assets/books/class_7.pdf` | `6,391,697` | `39e50f015d1eb328824a6ce1b8df7dcafac834a748e4f1af07ad523b830a4871` | **`181`** | `176` (`1–169` + Ans `170–175`) | `11` |
| **`CLASS_8`** | `app/src/main/assets/books/class_8.pdf` | `6,459,157` | `7b2e5b1dd718ed5684a0bacdfd9970d9eb99dbaa015feb07a88cddfbca6a1574` | **`190`** | `185` (`1–174` + Ans `175–184`) | `11` |
| **`CLASS_9_10`** | `app/src/main/assets/books/class_9_10.pdf` | `12,969,807` | `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0` | **`362`** | `357` (`1–344` + Ans `345–354`) | `17` |
| **Total** | **4 Bundled NCTB Textbooks** | **`31,193,087`** | **All 4 SHA-256 Hashes Verified** | **`894`** | **`874`** | **`47`** |

---

## 3. Project Structure

```text
Math-Master/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/books/          # 4 bundled NCTB PDFs (class_6: 161, class_7: 181, class_8: 190, class_9_10: 362 = 894 pages)
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/              # Room Database v9 (20 @Entity tables, MIGRATION_1_2..8_9, MathDao, MathRepository)
│       │   │   ├── math/              # SharedMathEngine, ProblemSolver, ExamEngine, BCS & Book Curriculum Engines
│       │   │   └── ui/                # MathViewModel, NavigationStateRestoration, 14 Module Screens & Material 3 Theme
│       │   └── res/                   # Adaptive Launcher Icons, Drawables, Strings & XML Resources
│       └── test/java/com/example/     # 9 Unit & Robolectric Test Suites (184 automated tests)
├── gradle/
│   └── libs.versions.toml
├── docs/                              # Complete Engineering, Audit, QA, Database, Book, BCS & Release Documentation
│   ├── README.md
│   ├── architecture/
│   ├── audits/
│   ├── bcs/
│   ├── book/
│   ├── database/
│   ├── engineering/
│   ├── performance/
│   ├── qa/
│   └── release/
├── .env.example
├── .gitignore
├── README.md
├── build.gradle.kts
├── gradle.properties
├── metadata.json
└── settings.gradle.kts
```

---

## 4. Build, Test & Release Verification Summary

- **Automated Unit & Robolectric Tests:**
  ```bash
  gradle :app:testDebugUnitTest
  ```
  - **Latest Verified Status:** **`184 / 184` tests passing (`0` failures, `0` errors, `0` skipped)** across 9 test suites (`ExampleRobolectricTest`: `53`, `MathProblemSolverUnitTest`: `41`, `EcosystemUpgradeV7UnitTest`: `25`, `NumberSystemUnitTest`: `17`, `VisualMathExplorerUnitTest`: `17`, `BcsPreparationCenterUnitTest`: `12`, `HomeNavigationAndOrderingUnitTest`: `9`, `ApplicationWideScrollRestorationUnitTest`: `9`, `ExampleUnitTest`: `1`).
  - **Baseline Progression:** `165 / 165` (Phase 1 Core Engineering Audit, `BUG-01`..`BUG-21`) $\rightarrow$ `167 / 167` (Phase 2 Pre-Freeze Stabilization, `BUG-22`..`BUG-28`) $\rightarrow$ `172 / 172` (Phase 3 Independent Adversarial Bug Hunt, `BUG-29`..`BUG-35`) $\rightarrow$ `175 / 175` (Phase 4A Real-Device Home Screen Scroll Restoration, `BUG-36`) $\rightarrow$ **`184 / 184`** (Phase 4B Application-Wide Navigation, Scroll & Context State Restoration, `BUG-37`..`BUG-39`).
- **Debug APK Build:**
  ```bash
  gradle :app:assembleDebug
  ```
- **Signed Release APK Build:**
  ```bash
  KEYSTORE_PATH=/path/to/upload-keystore.jks STORE_PASSWORD=... KEY_PASSWORD=... gradle :app:assembleRelease
  ```

---

## 5. Engineering Documentation & Bug ID Traceability (`BUG-01` – `BUG-39`)

See [`docs/README.md`](docs/README.md) for the complete index of all 11 final project stabilization reports (`FINAL_PROJECT_AUDIT_REPORT.md`, `FINAL_ROOT_CAUSE_DEFECT_REGISTER.md` covering `BUG-01`..`BUG-39`, `FINAL_NAVIGATION_STATE_RESTORATION_AUDIT.md`, `FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md`, `FINAL_MATHEMATICAL_ACCURACY_REPORT.md`, `FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md`, `FINAL_DATABASE_INTEGRITY_REPORT.md`, `FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md`, `FINAL_PERFORMANCE_AND_UX_REPORT.md`, `FINAL_REGRESSION_TEST_REPORT.md`, and `FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md`) alongside historical engineering reports:
- **`BUG-01`**: Numerical Keypad Scoring and Negative Marking (`ExamScoringEngine` `-1` unanswered vs. `-2` wrong numerical sentinel).
- **`BUG-35`**: Book PDF Reader concurrency, bitmap memory management (`RGB_565`), `Mutex` serialization, and `LruCache(12)` optimization (`BookPdfRenderCache`).
- **`BUG-36`**: Home Screen Scroll Position Restoration (`HomeScrollPosition` + `SaveableStateHolder` + `LazyGridState.Saver`).
- **`BUG-37` – `BUG-39`**: Application-Wide Nested Navigation Scroll Restoration (`BUG-37`), Internal Tab/Filter Context Restoration (`BUG-38`), and Bottom-Navigation Tab State Retention (`BUG-39`).


