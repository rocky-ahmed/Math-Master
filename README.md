# Math Master (গণিত মাস্টার)

**Math Master** is a 100% offline-first Android mathematics education, NCTB textbook (`Class 6–10`), interactive math lab, step-by-step problem solver, and BCS preparation application written in **Kotlin** with **Jetpack Compose (Material 3)** and **Room Database (v9)**.

- **Official GitHub Repository:** [`https://github.com/rocky-ahmed/Math-Master`](https://github.com/rocky-ahmed/Math-Master) (`main` branch)
- **Application ID:** `com.aistudio.applet.ghqqzu` (`namespace = "com.example"`)
- **Version:** `1.0` (`versionCode = 1`, `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`)

---

## 1. Key Capabilities & 14 Interactive Modules

1. **নামতা ও গুণন (`TablesScreen`)** — Interactive multiplication tables (`1..100`), step-by-step patterns, mastery tracking, and drills.
2. **মৌলিক সংখ্যা (`PrimesScreen`)** — Sieve of Eratosthenes visualizer, primality checker, prime factorization, and twin/co-prime explorer.
3. **বর্গ, ঘন ও মূল (`SquaresCubesScreen`)** — Squares, cubes, square roots, cube roots, and digital root patterns.
4. **বীজগণিতের সূত্রাবলি (`FormulasScreen`)** — Core algebraic identities, geometric visual proofs, and interactive numerical substitution.
5. **সমন্বিত সূত্র ভাণ্ডার (`FormulaLibraryScreen`)** — 100+ formulas across Arithmetic, Algebra, Geometry, Mensuration, Trigonometry, Combinatorics, and Statistics (`NCTB`, `BDMO`, and `BCS` curriculum tracks) with bookmark deep-linking and mastery tracking.
6. **অনুশীলন ল্যাব (`PracticeLabScreen`)** — Multi-format interactive drills (`MCQ`, `KEYPAD`, `TRUE_FALSE`) with instant diagnostic feedback.
7. **মানসিক গণিত চ্যালেঞ্জ (`MentalMathScreen`)** — Timed mental arithmetic speed drills, streak multipliers, and mental calculation tricks.
8. **বাস্তব সংখ্যা ও বিভাজ্যতা (`NumberSystemScreen`)** — Number base converter (`Binary`, `Octal`, `Decimal`, `Hex`), Euclidean GCD/LCM, divisibility rules (`2..19`), modular exponentiation, divisor analysis, and `MCQ`/`KEYPAD`/`TRUE_FALSE` practice.
9. **গাণিতিক সমাধানকারী ইঞ্জিন (`ProblemSolverScreen`)** — Deterministic offline step-by-step Bengali problem solver across 14 mathematical topics in `LEARNING`, `SHORTCUT`, and `BCS` modes.
10. **ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (`VisualExplorerScreen`)** — Interactive Canvas visualizations for fractions, geometry, coordinate planes, and statistical charts.
11. **কুইজ ও পরীক্ষা কেন্দ্র (`QuizzesScreen`)** — Full Exam Center supporting Quick Quizzes, Topic Exams, Custom Exams, Mistake Revision Exams, and BCS Preliminary Mocks (`MCQ`, `NUMERICAL`, `TRUE_FALSE`) with exact session persistence and monotonic timer tracking.
12. **অগ্রগতি ও বিশ্লেষণ ড্যাশবোর্ড (`DashboardScreen`)** — Cross-module accuracy analytics, daily streak tracking, 25-minute Pomodoro study timer, unified mistake notebook, and filterable practice history.
13. **পাঠ্যবই ও ইন্টারেক্টিভ ল্যাব (`BookHubAndDetailScreens` / `BookReaderAndLabsScreens`)** — Bundled offline NCTB textbooks for **Class 6, Class 7, Class 8, and Class 9–10** (**4 PDFs, 894 pages, 47 chapters, `31,193,087` bytes**), hybrid structured + native `PdfRenderer` reader (`+5` printed page offset, `1.0x`–`3.0x` zoom), 12 interactive math labs, chapter notes, and spaced-repetition revision.
14. **বিসিএস প্রস্তুতি (`BcsPreparationScreen`)** — 6 official BPSC exam profiles (`50th BCS Prelim 35 Marks`, `Written Combined 100 Marks`, `Written Code 008 Math 50 Marks`, `Written Code 009 Mental Ability 50 Marks`, `51st Special 20 Marks`, `Legacy 35th–49th Prelim 30 Marks`), 8-stage preparation journey, 9 math domains, 6 mental ability categories, provenance-verified question archive, and profile-isolated progress & mistake persistence.

---

## 2. Project Structure

```text
Math-Master/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/books/          # 4 bundled NCTB PDFs (class_6, class_7, class_8, class_9_10; 894 pages)
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/              # Room Database v9 (20 @Entity tables, MIGRATION_1_2..8_9, MathDao, MathRepository)
│       │   │   ├── math/              # SharedMathEngine, ProblemSolver, ExamEngine, BCS & Book Curriculum Engines
│       │   │   └── ui/                # MathViewModel, 14 Module Screens, Custom Components & Material 3 Theme
│       │   └── res/                   # Adaptive Launcher Icons, Drawables, Strings & XML Resources
│       └── test/java/com/example/     # 8 Unit & Robolectric Test Suites (172 automated tests)
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
├── RELEASE_APK/
│   └── app-release.apk                # Freshly built & verified v2-signed Release APK (Git-ignored binary output)
├── .env.example
├── .gitignore
├── README.md
├── build.gradle.kts
├── gradle.properties
├── metadata.json
└── settings.gradle.kts
```

---

## 3. Build, Test & Release Verification Summary

- **Automated Unit & Robolectric Tests:**
  ```bash
  gradle :app:testDebugUnitTest
  ```
  - **Status:** **`172 / 172` tests passing (`0` failures, `0` errors, `0` skipped)** across 8 test suites (`ExampleRobolectricTest`, `MathProblemSolverUnitTest`, `EcosystemUpgradeV7UnitTest`, `NumberSystemUnitTest`, `VisualMathExplorerUnitTest`, `BcsPreparationCenterUnitTest`, `HomeNavigationAndOrderingUnitTest`, `ExampleUnitTest`).
- **Debug APK Build:**
  ```bash
  gradle :app:assembleDebug
  ```
- **Signed Release APK Build:**
  ```bash
  KEYSTORE_PATH=/path/to/upload-keystore.jks STORE_PASSWORD=... KEY_PASSWORD=... gradle :app:assembleRelease
  ```
  - **Verified Output:** `app/build/outputs/apk/release/app-release.apk` and `RELEASE_APK/app-release.apk`
  - **Exact Size:** `44,528,027` bytes (`42.47 MiB`)
  - **SHA-256 Digest:** `cc378b037e52f5a02676c2ff6b3dbce4e671721ce5a451774fb07239ac500d98`
  - **Signature Verification:** APK Signature Scheme v2 (`CN=Math Master, OU=Education, O=MathMaster, L=Dhaka, ST=Dhaka, C=BD`, RSA 2048-bit, Cert SHA-256 `2a1c91e87e237272a93d807eecd5e37e1f9ccbb4e806fcd71cdf4a01932e8b7e`).

---

## 4. Engineering Documentation

See [`docs/README.md`](docs/README.md) for the complete index of all 10 final project stabilization reports (`FINAL_PROJECT_AUDIT_REPORT.md`, `FINAL_ROOT_CAUSE_DEFECT_REGISTER.md` covering `BUG-01`..`BUG-35`, `FINAL_BUG_FIX_AND_REFINEMENT_REPORT.md`, `FINAL_MATHEMATICAL_ACCURACY_REPORT.md`, `FINAL_EXAM_AND_BCS_VALIDATION_REPORT.md`, `FINAL_DATABASE_INTEGRITY_REPORT.md`, `FINAL_BOOK_AND_PDF_VALIDATION_REPORT.md`, `FINAL_PERFORMANCE_AND_UX_REPORT.md`, `FINAL_REGRESSION_TEST_REPORT.md`, and `FINAL_SECURITY_AND_PROJECT_ORGANIZATION_REPORT.md`) alongside historical engineering reports.


