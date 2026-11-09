# MODULE INVENTORY & TECHNICAL AUDIT REPORT
**Project**: Math Master (`com.example`)  
**Scope**: Complete 14-Module Home Grid Reconstruction & Technical Audit  
**Protected Boundary**: Modules 01–04 (100% Frozen / Read-Only)  
**Eligible Upgrade Scope**: Modules 05–14

---

## 1. Visual Grid Reconstruction (Combined Analysis of Screenshots 1–3)

The three Home Screen screenshots represent successive vertical scroll positions of the single 2-column `LazyVerticalGrid` in `HomeScreen.kt` (`HomeScreenDefaults.createHomeModules()`):
- **Screenshot 1 (Top Scroll)**: Displays Modules 01–06 (`TABLES`, `PRIMES`, `SQUARES`, `FORMULAS`, `FORMULA_LIBRARY`, `PRACTICE_LAB`) with the top edges of Modules 07–08 (`MENTAL_MATH`, `NUMBER_SYSTEM`) visible at the bottom.
- **Screenshot 2 (Middle Scroll)**: Displays Modules 07–12 (`MENTAL_MATH`, `NUMBER_SYSTEM`, `PROBLEM_SOLVER`, `VISUAL_EXPLORER`, `QUIZZES`, `DASHBOARD`) with the bottom edges of Modules 05–06 at the top and top edges of Modules 13–14 at the bottom.
- **Screenshot 3 (Bottom Scroll)**: Displays Modules 09–14 (`PROBLEM_SOLVER`, `VISUAL_EXPLORER`, `QUIZZES`, `DASHBOARD`, `BCS_MATH`, `BOOK`).

---

## 2. Complete 14-Module Inventory Table

| Grid Pos | SectionKey | Bengali Title | Subtitle | Badge | Route(s) | Primary Source Files | Protection Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **01** | `TABLES` | গুণ ও নামতা (১ থেকে ৪০) | ১-৪০ পর্যন্ত নামতার সহজ চার্ট ও অনুশীলন | ৪০ টি নামতা | `Screen.Tables`, `Screen.TableDetail` | `TablesScreen.kt`, `TableDetailScreen.kt`, `MultiplicationModels.kt` | **FROZEN / READ-ONLY** |
| **02** | `PRIMES` | মৌলিক সংখ্যা (১ থেকে ২০০) | ৪৬টি মৌলিক সংখ্যার তালিকা ও বিশদ বিশ্লেষণ | ৪৬ টি মৌলিক | `Screen.Primes` | `PrimesScreen.kt`, `PrimeNumberModels.kt` | **FROZEN / READ-ONLY** |
| **03** | `SQUARES` | বর্গ ও ঘন (১ থেকে ২০) | বর্গ, বর্গমূল, ঘন ও ঘনমূলের মান ও চার্ট | বর্গমূল ও ঘনমূল | `Screen.SquaresCubes` | `SquaresCubesScreen.kt`, `SquareCubeModels.kt` | **FROZEN / READ-ONLY** |
| **04** | `FORMULAS` | বীজগণিতীয় সূত্রাবলী | বর্গ ও ঘনের সূত্র, প্রতিপাদন ও অনুসিদ্ধান্ত | ব্যাখ্যা ও সমাধান | `Screen.Formulas`, `Screen.FormulaDetail` | `FormulasScreen.kt`, `FormulaDetailScreen.kt`, `AlgebraFormulaModels.kt` | **FROZEN / READ-ONLY** |
| **05** | `FORMULA_LIBRARY` | ফর্মুলা লাইব্রেরি (পূর্ণাঙ্গ সংগ্রহ) | বীজগণিত, জ্যামিতি ও ক্যালকুলাসের সূত্র | পূর্ণাঙ্গ সংগ্রহ | `Screen.FormulaLibrary` | `FormulaLibraryScreen.kt`, `FormulaLibraryModels.kt`, `SecondaryModuleUpgrades.kt` | **ELIGIBLE UPGRADE** |
| **06** | `PRACTICE_LAB` | গণিত প্র্যাকটিস ল্যাব | ধাপে ধাপে অনুশীলন ও স্বয়ংক্রিয় টিউটর | ইন্টারেক্টিভ ল্যাব | `Screen.PracticeLab`, `Screen.PracticeLabPlay` | `PracticeLabScreen.kt`, `PracticeLabPlayScreen.kt`, `PracticeLabModels.kt` | **ELIGIBLE UPGRADE** |
| **07** | `MENTAL_MATH` | মেন্টাল ম্যাথ চ্যালেঞ্জ | দ্রুত হিসাব করার তীব্র মানসিক প্রতিযোগিতা | টাইমার মোড | `Screen.MentalMath`, `Screen.MentalMathPlay` | `MentalMathScreen.kt`, `MentalMathPlayScreen.kt`, `MentalMathModels.kt` | **ELIGIBLE UPGRADE** |
| **08** | `NUMBER_SYSTEM` | বাস্তব সংখ্যা ও বিভাজ্যতা | সংখ্যা পদ্ধতি ও বিভাজ্যতার সহজ নিয়ম | ৪টি পদ্ধতি | `Screen.NumberSystem`, `Screen.NumberSystemPractice` | `NumberSystemScreen.kt`, `NumberSystemPracticeScreen.kt`, `NumberSystemModels.kt` | **ELIGIBLE UPGRADE** |
| **09** | `PROBLEM_SOLVER` | সমস্যা সমাধানকারী (Problem Solver) | পাটিগণিত ও বীজগণিতের ব্যাখ্যাসহ সমাধান | ১৬টি বিষয় | `Screen.ProblemSolver` | `ProblemSolverScreen.kt`, `MathProblemSolverEngine.kt`, `SolverModels.kt` | **ELIGIBLE UPGRADE** |
| **10** | `VISUAL_EXPLORER` | ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার | জ্যামিতি, দাঁড়িপাল্লা ও সূত্র ভিজ্যুয়ালাইজার | ১০টি বিভাগ | `Screen.VisualExplorer` | `VisualExplorerScreen.kt`, `VisualMathModels.kt` | **ELIGIBLE UPGRADE** |
| **11** | `QUIZZES` | ইন্টারেক্টিভ কুইজ পরীক্ষা | নামতা, মৌলিক ও সূত্রের এমসিকিউ পরীক্ষা | স্কোরবোর্ড যুক্ত | `Screen.Quizzes`, `Screen.QuizPlay` | `QuizzesScreen.kt`, `QuizPlayScreen.kt`, `QuizModels.kt`, `ExamEngineModels.kt` | **ELIGIBLE UPGRADE** |
| **12** | `DASHBOARD` | অগ্রগতি ও পরিসংখ্যান ড্যাশবোর্ড | দৈনিক স্ট্রিক ও চার্টভিত্তিক অ্যানালিটিক্স | চার্ট ও বিশ্লেষণ | `Screen.Dashboard` | `DashboardScreen.kt`, `LearningAnalyticsEngine.kt` | **ELIGIBLE UPGRADE** |
| **13** | `BCS_MATH` | বিসিএস গাণিতিক যুক্তি | গাণিতিক যুক্তি ও মানসিক দক্ষতা প্রস্তুতি | ৫০তম বিসিএস • প্রস্তুতি | `Screen.BcsPreparation` | `BcsPreparationScreen.kt`, `BcsPreparationModels.kt`, `BcsCurriculumAndGuideRepository.kt` | **ELIGIBLE UPGRADE** |
| **14** | `BOOK` | পাঠ্যবই ও গাইড (৬ষ্ঠ - ৯ম-১০ম) | ৪টি বোর্ড বই, ৪৭টি অধ্যায়, পিডিএফ ও অনুশীলন | ৪টি পাঠ্যবই • অফলাইন | `Screen.BookHub`, `BookDetail`, `BookChapterOverview`, `BookLesson`, `BookPractice`, `BookPdfReader`, `BookInteractiveLab`, `BookRevision` | `BookHubAndDetailScreens.kt`, `BookPracticeAndRevisionScreens.kt`, `BookCurriculumRepository.kt`, `BookQuestionEngine.kt` | **ELIGIBLE UPGRADE** |

---

## 3. Module-by-Module Technical & Pedagogical Gap Analysis (Modules 05–14)

1. **Module 05 (`FORMULA_LIBRARY`)**:
   - *Current State*: 12 subjects (`FormulaSubject`), difficulty tags, search/bookmarking, and a basic calculator sandbox in `SecondaryModuleUpgrades.kt`.
   - *Identified Gaps*: Lacks curriculum track filtering (`NCTB`, `BdMO`, `BCS`), formula-ID-accurate evaluation across all 12 subjects, persistent per-formula mastery tracking, and 1-tap cross-module action links to Practice Lab and Problem Solver.
2. **Module 06 (`PRACTICE_LAB`)**:
   - *Current State*: 6 categories (`PracticeCategory`), 4 progressive levels, adaptive next-level suggestions, and 3 input formats.
   - *Identified Gaps*: Lacks curriculum track filters (`NCTB`, `BdMO`, `BCS`), a research-backed **Daily Interleaved Challenge** (mixing categories to boost retention), diagnostic distractor explanations, and a dedicated **Mistake Retry Session** mode.
3. **Module 07 (`MENTAL_MATH`)**:
   - *Current State*: 5 operation modes, 3 timer profiles (`30s`, `60s`, `120s`), and combo scoring.
   - *Identified Gaps*: Questions lack explicit **Mental Calculation Strategy Coaching** (Left-to-Right Addition, Compensation Subtraction, Halving-Doubling Multiplication, Base-100 Squaring, Percentage Fraction Anchors) and structured training tier progression.
4. **Module 08 (`NUMBER_SYSTEM`)**:
   - *Current State*: Classification hierarchy, divisibility rules `2..12`, prime factorization, and Euclidean GCD/LCM.
   - *Identified Gaps*: Divisibility rules stop at 12 (missing `13, 15, 16, 18, 25`), lacks interactive **Modular Arithmetic (`a^n mod m`) & Divisor Count/Sum (`d(N)`, `σ(N)`, `φ(N)`)** tools for BdMO/BCS, and lacks curriculum track filtering.
5. **Module 09 (`PROBLEM_SOLVER`)**:
   - *Current State*: 16 deterministic solver topics (`SolverTopic`), preset scenarios, and step-by-step explanations.
   - *Identified Gaps*: Needs explicit **Common Mistake Warnings**, exact fraction + decimal dual output where applicable, and 1-tap cross-module links to matching Formula Library entries, Visual Explorer labs, and Practice Lab drills.
6. **Module 10 (`VISUAL_EXPLORER`)**:
   - *Current State*: 10 visual tabs (`VisualTab`) with interactive sliders and micro-checks.
   - *Identified Gaps*: Needs an interactive **Number Theory Visualiser (Divisor Lattice / Factor Tree & Modular Clock)** and a **Guided Experiment Verification Banner** with 1-tap navigation to related formulas and solvers.
7. **Module 11 (`QUIZZES`)**:
   - *Current State*: Blueprint-based exam engine (`ExamEngineModels.kt`), 5 blueprints, OMR palette, negative marking, and topic breakdown.
   - *Identified Gaps*: Lacks per-question exam bookmarking during review and automatic synchronization of wrong exam answers into a unified cross-module mistake notebook.
8. **Module 12 (`DASHBOARD`)**:
   - *Current State*: `LearningAnalyticsEngine` computes streak, readiness score, subject mastery, and error taxonomy from `StudySessionLogEntity` and `QuestionAttemptLogEntity`.
   - *Identified Gaps*: Does not aggregate or display completion and spaced-revision due counts from **Module 13 (`BCS_MATH`)**, **Module 14 (`BOOK`)**, **Module 05 (`FormulaMasteryEntity`)**, or the **Unified Mistake Notebook (`UnifiedMistakeEntity`)**.
9. **Module 13 (`BCS_MATH`)**:
   - *Current State*: 50th BCS Preliminary (20 Math + 15 Mental Ability = 35 marks) & Written (Code 008/009) hubs, syllabus browser, mock test, mistake notebook, and trend matrix.
   - *Identified Gaps*: Needs structured **Written Code 008/009 Descriptive Step-by-Step Model Solution Rubrics** and **1-Tap NCTB Class 6–10 Chapter Foundation Links** on each BCS syllabus unit.
10. **Module 14 (`BOOK`)**:
    - *Current State*: 4 offline NCTB PDFs (`class_6.pdf`, `class_7.pdf`, `class_8.pdf`, `class_9_10.pdf`) mapped to 47 chapters (`BookCurriculumClass6..910.kt`), lessons, practice engine, interactive lab, and spaced revision.
    - *Identified Gaps*: Needs **1-Tap Cross-Module Bridges** on Chapter Overview screens to launch the matching **Problem Solver topic**, **Formula Library filter**, and **BCS Syllabus unit**.
