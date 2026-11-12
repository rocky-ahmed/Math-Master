# Math Master (গণিত মাস্টার) — Visual Math Explorer 2.0

**Math Master** is a 100% offline-first Android mathematics education, NCTB textbook (`Class 6–10`), hybrid 2D + 3D visual math studio (**Visual Math Explorer 2.0**), step-by-step problem solver, and BCS preparation application written in **Kotlin** with **Jetpack Compose (Material 3)** and **Room Database (v9)**.

- **Official GitHub Repository:** [`https://github.com/rocky-ahmed/Math-Master`](https://github.com/rocky-ahmed/Math-Master) (`main` branch)
- **Application ID:** `com.aistudio.applet.ghqqzu` (`namespace = "com.example"`)
- **Version:** `1.0` (`versionCode = 1`, `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`)
- **Automated Regression Status:** **`215 / 215` unit & Robolectric tests passing (`0` failures, `0` errors, `0` skipped) across 10 test suites**
- **Canonical Defect Register:** **`BUG-01` through `BUG-73` (All 67 Historical `BUG-01`–`BUG-67` Original Definitions Preserved + `BUG-68`–`BUG-77` Newly Assigned Disambiguated/Split Defects)**

---

## 1. Key Capabilities & 14 Interactive Modules

1. **নামতা ও গুণন (`TablesScreen`)** — Interactive multiplication tables (`1..40` core & extended drills), step-by-step patterns, mastery tracking, and practice tutor (`BUG-61`, `BUG-62`).
2. **মৌলিক সংখ্যা (`PrimesScreen`)** — Sieve of Eratosthenes visualizer (`1..200`), primality checker with Bengali digit normalization (`BUG-77`), prime factorization, and twin/co-prime explorer (`BUG-60`).
3. **বর্গ, ঘন ও মূল (`SquaresCubesScreen`)** — Squares, cubes, square roots, cube roots (`1..20`), and digital root patterns.
4. **বীজগণিতের সূত্রাবলি (`FormulasScreen`)** — Core algebraic identities, geometric visual proofs, and interactive numerical substitution (`BUG-60`, `BUG-62`).
5. **সমন্বিত সূত্র ভাণ্ডার (`FormulaLibraryScreen`)** — `65+` / `100+` formulas across Arithmetic, Algebra, Geometry, Mensuration, Trigonometry, Combinatorics, and Statistics (`NCTB`, `BDMO`, and `BCS` curriculum tracks) with bookmark deep-linking (`BUG-19`) and mastery tracking.
6. **অনুশীলন ল্যাব (`PracticeLabScreen`)** — Multi-format interactive drills (`MCQ`, `KEYPAD`, `TRUE_FALSE`) with instant diagnostic feedback and unified mistake sync (`BUG-24`, `BUG-29`, `BUG-59`, `BUG-62`).
7. **মানসিক গণিত চ্যালেঞ্জ (`MentalMathScreen`)** — Timed mental arithmetic speed drills, streak multipliers, mental calculation tricks, and theme-aware Dark-Mode summary review cards (`BUG-24`, `BUG-59`, `BUG-64`).
8. **বাস্তব সংখ্যা ও বিভাজ্যতা (`NumberSystemScreen`)** — Number base converter (`Binary`, `Octal`, `Decimal`, `Hex`), Euclidean GCD/LCM, divisibility rules (`2..19`), modular exponentiation, divisor analysis with Bengali digit normalization (`BUG-77`), and `MCQ`/`KEYPAD`/`TRUE_FALSE` practice (`BUG-17`, `BUG-23`, `BUG-61`).
9. **গাণিতিক সমাধানকারী ইঞ্জিন (`ProblemSolverScreen`)** — Deterministic offline step-by-step Bengali problem solver across 14 mathematical topics in `LEARNING`, `SHORTCUT`, and `BCS` modes, plus distributive algebraic simplification and compact touch-target sizing (`BUG-08`, `BUG-30`, `BUG-61`, `BUG-66`).
10. **ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার ২.০ (`VisualExplorerScreen` + `VisualExplorerStudioLabs` + `Math3DGlViewport`)** — Hybrid 2D + True 3D Interactive Mathematics Studio across **9 Studio Tabs**:
    - **10 3D Solids** (`CUBE`, `CUBOID`, `CYLINDER`, `CONE`, `SPHERE`, `TRIANGULAR_PRISM`, `HEMISPHERE`, `SQUARE_PYRAMID`, `HEXAGONAL_PRISM`, `FRUSTUM`) with $SO(3)$ rigid-body hinge net unfolding (`BUG-76`), exact non-linear volume fill (`BUG-76`), analytical 3D plane cross-section slicer (`HORIZONTAL`, `VERTICAL`, `ANGLED`), 9-stage Bengali pedagogical stepper (`Introduce` $\to$ `Recap`, `BUG-72` duplicate checkpoint guard), and hardware EGL 24-bit/16-bit depth-buffer fallback (`BUG-73`).
    - **Interactive Studio Labs**: 2D Geometry with `zoomScale`, theme-aware Canvas labels (`BUG-57`), & exact 3-side triangles (`BUG-56`), Trigonometry Unit Circle & Elevation Lab, 3D $(X, Y, Z)$ Coordinate & Vector Lab + 2D Transformations (`BUG-55`), Algebra Balance Scale (`BUG-57`, `BUG-58`) & Polynomial Tiles Lab, Multi-Whole Improper Fractions (`BUG-54`) & Fraction Operations Lab, 2D Quadratic + 3D $z = f(x, y)$ Surface Explorer, and Formula Visual Proofs including 3D Cube (`BUG-53`).
11. **কুইজ ও পরীক্ষা কেন্দ্র (`QuizzesScreen`)** — Full Exam Center supporting Quick Quizzes, Topic Exams, Custom Exams, Mistake Revision Exams, and BCS Preliminary Mocks (`MCQ`, `NUMERICAL`, `TRUE_FALSE`) with exact session persistence, monotonic timer tracking, `effectiveAnswersMap` uncommitted answer inclusion (`BUG-47`), safe transactional exam deletion (`BUG-48`), sub-30s timer resume (`BUG-49`), declarative Bengali `TRUE_FALSE` formatting (`BUG-50`), content-seeded unique IDs (`BUG-51`), and `NUMERICAL` selection isolation (`BUG-52`).
12. **অগ্রগতি ও বিশ্লেষণ ড্যাশবোর্ড (`DashboardScreen`)** — Cross-module accuracy analytics, initial `0`-streak state on empty history (`BUG-40`), Pomodoro study timer with auto-flush (`BUG-74`), deduplicated `BOOK_`/`VISUAL_` Exam vs. Practice KPIs (`BUG-25`, `BUG-41`), chronological Exam/Quiz Performance Trend (`BUG-42`, `BUG-46`), time-windowed Topic Mastery isolation (`BUG-43`), Bengali topic normalization & recommendation routing (`BUG-44`), unbounded `>365`-day DST-safe streaks (`BUG-45`), Daily Goal configuration dialog (`BUG-75`), bullet-list spacing & action-button geometry (`BUG-65`), filter chip row spacing (`BUG-66`), and time-windowed Practice History & `BCS_PREP`/`VISUAL_MATH` chip filtering (`BUG-68`).
13. **পাঠ্যবই ও ইন্টারেক্টিভ ল্যাব (`BookHubAndDetailScreens` / `BookReaderAndLabsScreens`)** — Bundled offline NCTB textbooks for **Class 6 (`161` PDF pp.), Class 7 (`181` PDF pp.), Class 8 (`190` PDF pp.), and Class 9–10 (`362` PDF pp.)** (**4 PDFs, `894` PDF pages / `874` printed pages, `47` chapters, `31,193,087` bytes**), hybrid structured + native `PdfRenderer` reader (`+5` printed page offset, `BookPdfRenderCache` `Mutex` + `LruCache(12)` + `RGB_565`, `1.0x`–`3.0x` zoom, `BUG-09`, `BUG-35`), 12 interactive math labs, chapter notes, and spaced-repetition revision (`BUG-22`, `BUG-26`, `BUG-27`, `BUG-31`, `BUG-71`).
14. **বিসিএস প্রস্তুতি (`BcsPreparationScreen`)** — 6 official BPSC exam profiles (`50th BCS Prelim 35 Marks`, `Written Combined 100 Marks`, `Written Code 008 Math 50 Marks`, `Written Code 009 Mental Ability 50 Marks`, `51st Special 20 Marks`, `Legacy 35th–49th Prelim 30 Marks`), 8-stage preparation journey, 9 math domains, 6 mental ability categories, provenance-verified question archive, and profile-isolated progress & canonical mistake persistence (`BUG-05`, `BUG-06`, `BUG-13`, `BUG-21`, `BUG-32`, `BUG-66`, `BUG-69`, `BUG-70`).

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

## 3. Build, Test & 3-Tier GPU Verification Summary

- **Automated Unit & Robolectric Tests:**
  ```bash
  gradle :app:testDebugUnitTest
  ```
  - **Latest Verified Status:** **`215 / 215` tests passing (`0` failures, `0` errors, `0` skipped)** across **10 test suites**:
    1. `ExampleRobolectricTest`: `54`
    2. `MathProblemSolverUnitTest`: `41`
    3. `VisualMathExplorerUnitTest`: `36`
    4. `EcosystemUpgradeV7UnitTest`: `27`
    5. `NumberSystemUnitTest`: `17`
    6. `BcsPreparationCenterUnitTest`: `12`
    7. `ApplicationWideScrollRestorationUnitTest`: `10`
    8. `HomeNavigationAndOrderingUnitTest`: `9`
    9. `UiSpacingAndLayoutAuditUnitTest`: `8`
    10. `ExampleUnitTest`: `1`
  - **Baseline Progression:** `165 / 165` (Phase 1, `BUG-01`..`BUG-21`) $\rightarrow$ `167 / 167` (Phase 2, `BUG-22`..`BUG-28`) $\rightarrow$ `172 / 172` (Phase 3, `BUG-29`..`BUG-35`) $\rightarrow$ `175 / 175` (Phase 4A, `BUG-36`) $\rightarrow$ `185 / 185` (Phase 4B, `BUG-37`..`BUG-39`) $\rightarrow$ `192 / 192` (Phase 5, `BUG-40`..`BUG-52`, `BUG-68`, `BUG-74`, `BUG-75`) $\rightarrow$ `199 / 199` (Phase 6A, `BUG-53`..`BUG-58`, `BUG-76`) $\rightarrow$ `207 / 207` (Phase 6B, `BUG-59`..`BUG-67`, `BUG-69`..`BUG-71`, `BUG-77`) $\rightarrow$ **`215 / 215`** (Phase 7 Visual Math Explorer 2.0 Studio Upgrade, `BUG-72` & `BUG-73`).
- **3-Tier Verification Status for Visual Math Explorer 2.0 (`Math3DGlViewport.kt`):**
  1. **Source-Level GPU Renderer Verification:** **VERIFIED IN SOURCE** (`GLSurfaceView`, `DepthBuffer24FallbackConfigChooser` (`BUG-73`), `Math3DGlRenderer`, OpenGL ES 3.0/2.0 negotiation, GLSL shaders, `GL_DEPTH_TEST`, `glPolygonOffset`, `GL_BLEND`, `RENDERMODE_WHEN_DIRTY`, `onPause`/`onResume` lifecycle).
  2. **Unit / Robolectric Verification:** **PASS (`215 / 215` TESTS)** (Uses deterministic software projection fallback `Math3DEngine.projectScene` when `Build.FINGERPRINT` is `"robolectric"` plus JVM unit tests of `Math3DGlRenderer` matrix/projection/shader logic and `DepthBuffer24FallbackConfigChooser`).
  3. **Real Physical GPU / Device Verification:** **NOT VERIFIED ON PHYSICAL DEVICE (PENDING HARDWARE)** — Physical GPU frame performance is **not** marked `PASS` without hardware handset evidence. See [`docs/qa/FINAL_DEVICE_QA_REPORT.md`](docs/qa/FINAL_DEVICE_QA_REPORT.md) and [`docs/audits/FINAL_PROJECT_AUDIT_REPORT.md`](docs/audits/FINAL_PROJECT_AUDIT_REPORT.md) for the complete **11-point Physical-Device GPU Validation Execution Guide** (EGL context creation, ES 2.0/3.0 selection, 24/16-bit depth buffer, shader compilation, all 10 solids, $SO(3)$ net unfolding, cross-section slicing, volume-fill rendering, lifecycle recreation, Android GPU Inspector OpenGL/ANGLE profiling, `dumpsys gfxinfo` UI frame statistics, and instrumented renderer draw-call count verification for idle `RENDERMODE_WHEN_DIRTY` behavior).
