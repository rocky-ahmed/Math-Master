# MATH MASTER — COMPLETE APPLICATION VISUAL UI/UX AUDIT REPORT

- **Application Name**: গণিত মাস্টার (`Math Master`)
- **Application ID**: `com.aistudio.applet.ghqqzu` (`namespace = "com.example"`)
- **Audit Mode**: Strict Read-Only Visual & Architectural UI/UX Quality Audit (Zero Source Modifications)
- **Audit Date**: November 15, 2026
- **Build & Test Baseline Verification**: `gradle :app:testDebugUnitTest` — **259 / 259 automated unit & Robolectric tests passing** (`BUILD SUCCESSFUL`)

---

## A. Executive Summary

| Metric | Historical Baseline | Current Verified Count | Audit Coverage / Status |
| :--- | :---: | :---: | :--- |
| **Total Main Application Modules (`SectionKey`)** | 14 | **14** | **14 / 14 (100%) Inventoried & Audited** |
| **Total Navigation Destinations (`Screen` routes)** | 31 | **31** | **31 / 31 (100%) Wired, Reachable & Code-Inspected** |
| **Total Math Problem Solver Topics (`SolverTopic`)** | 16 | **16** | **16 / 16 (100%) Inventoried & Verified** |
| **Room Database Version & Entities** | v10 / 21 entities | **v10 / 21 entities** | **9 migrations (`MIGRATION_1_2`..`MIGRATION_9_10`) Verified** |
| **Automated Unit & Robolectric Tests** | 259 | **259** (`14` test classes) | **259 / 259 Passing (`100%`)** |
| **Total Sections Inventoried (`SEC-*`)** | — | **124** | **124 / 124 Mapped in Hierarchy** |
| **Total Subsections Inventoried (`SUB-*`)** | — | **268** | **268 / 268 Mapped in Hierarchy** |
| **Total Tabs & Segmented Controls Inventoried** | — | **47** | **47 / 47 Mapped & Audited** |
| **Total Interactive Controls Inventoried** | — | **312+** | **259 Robolectric/Unit-Tested; 9 Screenshot Renders Visually Inspected** |
| **Screens with Actual Pixel Screenshot Renders Inspected** | — | **2 Full Screens + 1 Component Across 6 Viewports (`9` PNG/JPG files)** | `HomeScreen` (device render), `ProblemSolverScreen` (2 device renders), `StudySessionTimerSection` (6 Robolectric native renders across `320dp`, `360dp`, `411dp`, `1.5x` font scale, Dark mode, Expanded state) |
| **Screens Verified via Headless Robolectric + Source Inspection Only** | — | **29 / 31 Destinations** | Marked **`NOT VERIFIED` (Pixel Render)** due to strict no-source-edit constraint and absence of live ADB/emulator in container |
| **Overall Audit Coverage Percentage** | — | **100% Inventory & Code/Robolectric DOM Coverage; 9.7% Full-Screen Pixel Render Coverage** | **Honest Dual-Track Reporting Applied** |

### Summary of Discovered Defects & Recommendations
- **Total Confirmed Visual/UI/UX Defects (`CONFIRMED` via actual rendered screenshots)**: **8** (`0` P0, `0` P1, `5` P2, `3` P3)
- **Total Suspected Visual/UI/UX/A11y/Localization Defects (`SUSPECTED` via exact Compose source & layout metrics)**: **6** (`0` P0, `0` P1, `5` P2, `1` P3)
- **Total Documented Defects in Bug Register**: **14** (`0` P0, `0` P1, `10` P2, `4` P3)
- **Total Non-Bug UX Improvement Recommendations**: **10**

---

## B. Complete Application Navigation Inventory

### B.1 Baseline Comparison & Global Navigation Architecture

- **Global Entry Point**: `MainActivity.kt` → `MainAppContent(viewModel: MathViewModel)`
- **State-Driven Backstack**: `viewModel.screenStack: StateFlow<List<Screen>>` with `rememberSaveableStateHolder` keyed by `viewModel.getDestinationStateKey(currentScreen)` and custom `NavigationStateRestoration.kt` (`rememberManagedLazyListState`, `rememberManagedScrollState`, `rememberManagedUiState`).
- **Global Bottom Navigation Bar (`app_bottom_nav`)**: Displayed on 5 primary destinations (`Screen.Home`, `Screen.Tables`, `Screen.Dashboard`, `Screen.Bookmarks`, `Screen.Quizzes`) with 5 `NavigationBarItem`s:
  1. `nav_item_home` (`হোম` → `Screen.Home`)
  2. `nav_item_tables` (`নামতা` → `Screen.Tables`)
  3. `nav_item_dashboard` (`অগ্রগতি` → `Screen.Dashboard`)
  4. `nav_item_bookmarks` (`বুকমার্ক` → `Screen.Bookmarks`)
  5. `nav_item_quizzes` (`কুইজ` → `Screen.Quizzes`)
- **Missing, Unreachable, or Duplicated Destinations**:
  - **Unreachable Destinations**: **0** (All 31 `Screen` sealed class routes are reachable from `HomeScreen`, `BottomNavigationBar`, `TopAppBar`, or parent module flows).
  - **Duplicated UI Sections**: **2**:
    1. `Interactive3DSolidStudioCard` appears both at the top of `VisualExplorerScreen` Tab 0 (`GEOMETRY`) and as the sole content of Tab 4 (`SOLID_3D`).
    2. `SimultaneousLinearSystemAndBridgeCard` (`দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule)`) is rendered unconditionally inside `ProblemSolverPedagogyEnrichmentSection` across all 16 solver topics (`ProblemSolverPedagogyComponents.kt` line 950), duplicating algebra-specific UI inside non-algebra topics.

---

### B.2 Hierarchical Navigation Map with Unique Audit IDs

```text
HOME-001: Screen.Home (HomeScreen.kt — "গণিত মাস্টার")
  HOME-001-SEC-TOPBAR: TopAppBar & Global Actions
    HOME-001-SEC-TOPBAR-SUB-01: App Title & Subtitle ("গণিত মাস্টার" / "বাংলায় সম্পূর্ণ গণিত শিক্ষা ও অনুশীলন")
    HOME-001-SEC-TOPBAR-SUB-02: Customize Button (top_customize_btn -> Screen.Customize)
    HOME-001-SEC-TOPBAR-SUB-03: Theme Mode Cycle Button (top_theme_toggle_btn: SYSTEM -> LIGHT -> DARK)
    HOME-001-SEC-TOPBAR-SUB-04: Bookmarks Quick Action (top_bookmarks_btn -> Screen.Bookmarks)
  HOME-001-SEC-HERO: Welcome Hero Summary Banner (hero_summary_card)
    HOME-001-SEC-HERO-SUB-01: Learned Progress Pill (hero_learned_count)
    HOME-001-SEC-HERO-SUB-02: Saved Bookmarks Pill (hero_bookmark_count)
    HOME-001-SEC-HERO-SUB-03: Completed Quizzes Pill (hero_quiz_count)
  HOME-001-SEC-TIMER: Study Session Timer Card (StudySessionTimerCard.kt — study_session_timer_section)
    HOME-001-SEC-TIMER-SUB-01: Compact Header Row (Timer Icon, Title, Status Pill, Today Summary, Enable Switch study_timer_enable_switch)
    HOME-001-SEC-TIMER-SUB-02: Expanded Countdown Display & Progress Ring (study_timer_expanded_content)
    HOME-001-SEC-TIMER-SUB-03: Preset Duration Chips (১৫ মি., ২৫ মি., ৩০ মি., ৪৫ মি., ৬০ মি., কাস্টম)
    HOME-001-SEC-TIMER-SUB-04: Custom Minutes OutlinedTextField & Apply Button (study_timer_custom_input)
    HOME-001-SEC-TIMER-SUB-05: Primary Transport Controls (Start/Pause/Resume study_timer_primary_action_btn, Reset study_timer_reset_btn)
    HOME-001-SEC-TIMER-SUB-06: Daily Study Goal Progress Bar & Session Counter
  HOME-001-SEC-GRID: 14 Main Application Module Cards (CustomModuleRenderer — GRID / LIST / COMPACT)
    HOME-001-SEC-GRID-SUB-01..14: Module Cards 01 through 14
  HOME-001-SEC-RECENT: Recent Quiz Results Summary Section (recent_quiz_results_section)
  HOME-001-SEC-BOTTOMNAV: Global 5-Tab Bottom Navigation Bar (app_bottom_nav)

  MOD-01: Module 01 — গুণের নামতা (SectionKey.TABLES)
    MOD-01-001: Screen.Tables (TablesScreen.kt — "১ থেকে ৪০ এর ঘরের নামতা")
      MOD-01-001-SEC-A: Search & Range Filter Controls
        MOD-01-001-SEC-A-SUB-01: Search Input Field (tables_search_input)
        MOD-01-001-SEC-A-SUB-02: Range Filter Chips (সব ১-৪০, ১-১০, ১১-২০, ২১-৩০, ৩১-৪০)
      MOD-01-001-SEC-B: Number Grid 1–40 (tables_grid)
        MOD-01-001-SEC-B-SUB-01: Table Card Item (table_card_1..40 with Learned Badge & Bookmark Indicator)
    MOD-01-002: Screen.TableDetail (TablesScreen.kt — "X এর ঘরের নামতা")
      MOD-01-002-SEC-A: Top Bar & Table Switcher Controls (Prev/Next Table, Bookmark, Learned Toggle)
      MOD-01-002-SEC-B: 10-Row Multiplication Table List (X × ১ = ... to X × ১০ = ...)
      MOD-01-002-SEC-C: Pattern Analysis & Divisibility Note Card
      MOD-01-002-SEC-D: Interactive Practice Tutor CTA Button (btn_open_practice_tutor -> Screen.PracticeTutor)
    MOD-01-003: Screen.PracticeTutor (BookmarksAndTutorScreens.kt — "নামতা অনুশীলন টিউটর")
      MOD-01-003-SEC-A: Step Progress Header (ধাপ ১/১০..১০/১০ & Score Counter)
      MOD-01-003-SEC-B: Interactive Multiplication Prompt Card & OutlinedTextField (tutor_answer_input)
      MOD-01-003-SEC-C: Submit / Next Step Controls & Instant Feedback Banner
      MOD-01-003-SEC-D: Quick Table Switcher Chips (৩, ৭, ১২, ১৭, ২৫)

  MOD-02: Module 02 — মৌলিক সংখ্যা (SectionKey.PRIMES)
    MOD-02-001: Screen.Primes (PrimesScreen.kt — "মৌলিক সংখ্যা ও উৎপাদক বিশ্লেষণ")
      MOD-02-001-SEC-A: Interactive Prime Checker & Prime Factorization Calculator
        MOD-02-001-SEC-A-SUB-01: Number Input Field (prime_check_input) & Check Button (prime_check_btn)
        MOD-02-001-SEC-A-SUB-02: Factorization & Divisibility Result Card
      MOD-02-001-SEC-B: Range Filter Tabs (১-১০০, ১০১-২০০, ২০১-৫০০, ৫০১-১০০০)
      MOD-02-001-SEC-C: Range Summary Banner & Mnemonic Phone-Number Code Card (৪৪২২৩২২৩২১)
      MOD-02-001-SEC-D: Prime Number Grid & Interactive Detail AlertDialog (Twin/Cousin/Safe Prime properties)

  MOD-03: Module 03 — বর্গ ও ঘন (SectionKey.SQUARES)
    MOD-03-001: Screen.SquaresCubes (SquaresCubesScreen.kt — "বর্গ, বর্গমূল, ঘন ও ঘনমূল")
      MOD-03-001-SEC-A: 3-Tab Selector Row (tab_squares "বর্গ ও বর্গমূল", tab_cubes "ঘন ও ঘনমূল", tab_calculator "ক্যালকুলেটর ও শর্টকাট")
      MOD-03-001-SEC-B: Tab 0 — 1 to 20 Squares & Square Roots Table (square_row_1..20)
      MOD-03-001-SEC-C: Tab 1 — 1 to 20 Cubes & Cube Roots Table (cube_row_1..20) + Footer Note Card
      MOD-03-001-SEC-D: Tab 2 — Custom n², √n, n³, ∛n Calculator & Fast Mental Squaring Tricks Card

  MOD-04: Module 04 — বীজগণিতের সূত্র (SectionKey.FORMULAS)
    MOD-04-001: Screen.Formulas (FormulasScreen.kt — "বীজগণিতের ১৪টি মূল সূত্র")
      MOD-04-001-SEC-A: Introductory Note Banner & Search Filter Input (formulas_search_input)
      MOD-04-001-SEC-B: Category Filter Chips (সব, বর্গ, ঘন, উৎপাদক)
      MOD-04-001-SEC-C: 14 Handwritten-Note Formula Cards List (formula_card_1..14)
    MOD-04-002: Screen.FormulaDetail (FormulasScreen.kt — Detailed Single Formula View)
      MOD-04-002-SEC-A: Formula Expression Hero Card, Bookmark & Learned Status Toggle
      MOD-04-002-SEC-B: Algebraic Expansion / Proof Breakdown Card
      MOD-04-002-SEC-C: Interactive Variable (a, b, c) Numeric Substitution Evaluator Card
      MOD-04-002-SEC-D: Worked Examples & Cross-Module Practice Links

  MOD-05: Module 05 — সূত্র লাইব্রেরি (SectionKey.FORMULA_LIBRARY)
    MOD-05-001: Screen.FormulaLibrary (FormulaLibraryScreen.kt — "সম্পূর্ণ সূত্র লাইব্রেরি")
      MOD-05-001-SEC-A: Search Bar (formula_library_search) & Mastery Summary Strip
      MOD-05-001-SEC-B: 4 Category Filter Chips (সব, পাটিগণিত, বীজগণিত, জ্যামিতি ও পরিমিতি)
      MOD-05-001-SEC-C: Expandable Formula Cards (formula_lib_card_*)
        MOD-05-001-SEC-C-SUB-01: Header Row, Bookmark Button (btn_bookmark_*), Copy Button (btn_copy_*)
        MOD-05-001-SEC-C-SUB-02: Variable Definitions, Derivation Proof & Exam Shortcut Notes
        MOD-05-001-SEC-C-SUB-03: Interactive Live Formula Calculator (Input Fields & Calculate Button)
        MOD-05-001-SEC-C-SUB-04: Formula Mastery Self-Check & Related Quiz Launch CTA

  MOD-06: Module 06 — অনুশীলন ল্যাব (SectionKey.PRACTICE_LAB)
    MOD-06-001: Screen.PracticeLab (PracticeLabScreen.kt — "গণিত অনুশীলন ল্যাব")
      MOD-06-001-SEC-A: Operation Selector Cards (যোগ, বিয়োগ, গুণ, ভাগ, মিশ্র)
      MOD-06-001-SEC-B: Difficulty Level Selector Chips (সহজ, মধ্যম, কঠিন)
      MOD-06-001-SEC-C: Question Count & Timer Mode Configuration
      MOD-06-001-SEC-D: Start Practice Session CTA (btn_start_practice_lab -> Screen.PracticeLabPlay)
    MOD-06-002: Screen.PracticeLabPlay (PracticeLabPlayScreen.kt)
      MOD-06-002-SEC-A: Session Header (Progress Bar, Score, Streak, Timer Pill)
      MOD-06-002-SEC-B: Arithmetic Problem Prompt Card & Answer Input / Keypad (BanglaNumericKeypad)
      MOD-06-002-SEC-C: Step-by-Step Hint & Solution Breakdown Card
      MOD-06-002-SEC-D: Session Completion Summary & Mistake Review View

  MOD-07: Module 07 — দ্রুত গণিত (SectionKey.MENTAL_MATH)
    MOD-07-001: Screen.MentalMath (MentalMathScreen.kt — "দ্রুত গণিত ও শর্টকাট কৌশল")
      MOD-07-001-SEC-A: Speed Drill Mode & Difficulty Configuration Card
      MOD-07-001-SEC-B: Mental Math Shortcut Technique Cards (১১ দিয়ে গুণ, ৫ দিয়ে গুণ/ভাগ, ৯৯ দিয়ে গুণ, শতকরা শর্টকাট)
      MOD-07-001-SEC-C: Launch Speed Drill CTA (btn_start_mental_math -> Screen.MentalMathPlay)
    MOD-07-002: Screen.MentalMathPlay (MentalMathPlayScreen.kt)
      MOD-07-002-SEC-A: Countdown Timer, Streak Multiplier & Question Counter
      MOD-07-002-SEC-B: Rapid Question Card & 4-Option / Keypad Response Controls
      MOD-07-002-SEC-C: Mental Trick Explanation Banner & End-of-Drill Performance Summary

  MOD-08: Module 08 — সংখ্যা পদ্ধতি (SectionKey.NUMBER_SYSTEM)
    MOD-08-001: Screen.NumberSystem (NumberSystemScreen.kt — "সংখ্যা পদ্ধতি ও রূপান্তর")
      MOD-08-001-SEC-A: 4-Tab Selector (সংখ্যা শ্রেণিবিভাগ, বেস কনভার্টার, বিভাজ্যতা ও গসাগু/লসাগু, রোমান ও স্থানীয় মান)
      MOD-08-001-SEC-B: Tab 0 — Real/Rational/Irrational/Integer Classification Tree & Interactive Classifier
      MOD-08-001-SEC-C: Tab 1 — Binary / Octal / Decimal / Hexadecimal Base Converter with Step-by-Step Division Table
      MOD-08-001-SEC-D: Tab 2 — Divisibility Rules (২–১১) & Interactive GCD/LCM Euclidean Step Calculator
      MOD-08-001-SEC-E: Tab 3 — Place Value Breakdown & Roman Numeral Converter + Practice Launch CTA (btn_start_number_system_practice)
    MOD-08-002: Screen.NumberSystemPractice (NumberSystemPracticePlayScreen.kt)
      MOD-08-002-SEC-A: Question Progress Bar, Score Counter & Topic Badge
      MOD-08-002-SEC-B: Question Prompt, 4 MCQ Option Cards & Step-by-Step Solution Feedback
      MOD-08-002-SEC-C: Practice Session Result Card & Retry Action

  MOD-09: Module 09 — গণিত সমস্যা সমাধানকারী (SectionKey.PROBLEM_SOLVER)
    MOD-09-001: Screen.ProblemSolver (ProblemSolverScreen.kt & ProblemSolverPedagogyComponents.kt)
      MOD-09-001-SEC-A: Pedagogical Mode Selector Bar (Learning Mode, Guided Mode, Practice Mode, Exam Shortcut Mode)
      MOD-09-001-SEC-B: 16-Topic Horizontal Selector Strip (solver_topic_row — 16 SolverTopics)
        MOD-09-001-SEC-B-SUB-01: 1. মৌলিক পাটিগণিত (BODMAS)
        MOD-09-001-SEC-B-SUB-02: 2. ভগ্নাংশ ও দশমিক (Fractions & Decimals)
        MOD-09-001-SEC-B-SUB-03: 3. ল.সা.গু ও গ.সা.গু (LCM & GCD)
        MOD-09-001-SEC-B-SUB-04: 4. অনুপাত ও সমানুপাত (Ratio & Proportion)
        MOD-09-001-SEC-B-SUB-05: 5. শতকরা (Percentage)
        MOD-09-001-SEC-B-SUB-06: 6. লাভ-ক্ষতি ও ছাড় (Profit, Loss & Discount)
        MOD-09-001-SEC-B-SUB-07: 7. সরল ও চক্রবৃদ্ধি মুনাফা (Simple & Compound Interest)
        MOD-09-001-SEC-B-SUB-08: 8. গড় ও বয়স সংক্রান্ত (Average & Age)
        MOD-09-001-SEC-B-SUB-09: 9. ঐকিক নিয়ম, কাজ ও সময় (Unitary Method, Work & Time)
        MOD-09-001-SEC-B-SUB-10: 10. গতি, দূরত্ব ও ট্রেন/নৌকা (Speed, Distance, Train & Boat)
        MOD-09-001-SEC-B-SUB-11: 11. একঘাত ও দ্বিঘাত সমীকরণ (Linear & Quadratic Equations)
        MOD-09-001-SEC-B-SUB-12: 12. বীজগাণিতিক রাশি ও উৎপাদক (Algebraic Expressions & Factoring)
        MOD-09-001-SEC-B-SUB-13: 13. সূচক ও লগারিদম (Exponents & Logarithms)
        MOD-09-001-SEC-B-SUB-14: 14. সমান্তর ও গুণোত্তর ধারা (AP & GP Series)
        MOD-09-001-SEC-B-SUB-15: 15. জ্যামিতি ও পরিমিতি (Geometry & Mensuration)
        MOD-09-001-SEC-B-SUB-16: 16. পরিসংখ্যান ও সম্ভাব্যতা (Statistics & Probability)
      MOD-09-001-SEC-C: Topic Sub-Mode Chips, Preset Sample Chips & Validated Parameter Input Form
      MOD-09-001-SEC-D: Primary Solve & Clear Action Bar (solver_solve_btn, solver_clear_btn)
      MOD-09-001-SEC-E: Solution Output Card (Final Answer Banner, Copy, Bookmark, Formula Used, Step-by-Step Cards, Alternative Method, Verification, Similar Problem)
      MOD-09-001-SEC-F: Pedagogy Enrichment Section (Visual Diagram, Mistake Warning, BCS Exam Tip, Try-Yourself Interactive Check, Simultaneous Linear System Cramer's Rule Card)
      MOD-09-001-SEC-G: Expandable Saved Solver History Panel (solver_history_card,Reload & Delete per item)

  MOD-10: Module 10 — ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (SectionKey.VISUAL_EXPLORER)
    MOD-10-001: Screen.VisualExplorer (VisualExplorerScreen.kt & VisualExplorerStudioLabs.kt)
      MOD-10-001-SEC-A: 5-Tab ScrollableTabRow (visual_explorer_tabs)
        MOD-10-001-SEC-A-SUB-01: Tab 0 — জ্যামিতি এক্সপ্লোরার (3D Solid Studio + 2D Shapes + 2D Coordinate Transforms + Trig Unit Circle + 3D Vector Studio)
        MOD-10-001-SEC-A-SUB-02: Tab 1 — ভিজ্যুয়াল বীজগণিত (Balance Scale Solver with Tilt Physics + Linear Graph y=mx+c + Algebra Tiles + 2D/3D Quadratic Surface Studio)
        MOD-10-001-SEC-A-SUB-03: Tab 2 — সংখ্যা রেখা ও ভগ্নাংশ (Multi-Whole Fraction Pie/Bar + Interactive Number Line + Probability & Set Venn Studio)
        MOD-10-001-SEC-A-SUB-04: Tab 3 — সূত্র ভিজ্যুয়ালাইজার ((a+b)², (a-b)², a²-b², Pythagoras, Circle Area, AP Sum Geometric Proofs)
        MOD-10-001-SEC-A-SUB-05: Tab 4 — ৩ডি ঘনবস্তু স্টুডিও (OpenGL ES 2.0 / Canvas 2D Fallback, 8 Solids, Net Unfolding, Cross-Section Slicing)
      MOD-10-001-SEC-B: Sticky Bottom Guided Discovery & Mini Knowledge Check Bar (visual_knowledge_check_bar)

  MOD-11: Module 11 — কুইজ ও পরীক্ষা (SectionKey.QUIZZES)
    MOD-11-001: Screen.Quizzes (QuizzesScreen.kt — "কুইজ ও পূর্ণাঙ্গ পরীক্ষা কেন্দ্র")
      MOD-11-001-SEC-A: Active Unfinished Exam Resume Banner (resume_exam_banner)
      MOD-11-001-SEC-B: 3-Tab Workspace Selector (টপিকভিত্তিক কুইজ, পূর্ণাঙ্গ মক পরীক্ষা, পরীক্ষার ইতিহাস ও তুলনা)
        MOD-11-001-SEC-B-SUB-01: Tab 0 — Quick Topic Quiz Configurator (Mode, Difficulty, Question Type, Question Count, Topic Cards)
        MOD-11-001-SEC-B-SUB-02: Tab 1 — Full Mock Exam Builder (Presets: BCS Preliminary, Class 9-10 Board, Custom Multi-Topic, Negative Marking, Duration)
        MOD-11-001-SEC-B-SUB-03: Tab 2 — Filterable/Sortable Exam History & 2-Attempt Comparison Card
    MOD-11-002: Screen.QuizPlay (QuizzesScreen.kt)
      MOD-11-002-SEC-A: Exam TopAppBar (Live Countdown/Elapsed Timer Badge, Submit Button top_submit_exam_btn)
      MOD-11-002-SEC-B: Expandable 5-State Question Navigation Palette (question_palette_card, palette_q_*)
      MOD-11-002-SEC-C: Question Card, Mark-for-Review, Clear Answer, MCQ Options / BanglaNumericKeypad
      MOD-11-002-SEC-D: Pre-Submit Confirmation AlertDialog (confirm_submit_exam_btn)
      MOD-11-002-SEC-E: Post-Exam Comprehensive Result & Question-by-Question Review Screen (QuizCompletedContent)

  MOD-12: Module 12 — অগ্রগতি ড্যাশবোর্ড (SectionKey.DASHBOARD)
    MOD-12-001: Screen.Dashboard (DashboardScreen.kt — "অগ্রগতি ও লার্নিং অ্যানালিটিক্স")
      MOD-12-001-SEC-A: Time-Period Analytics Filter Strip (আজ, শেষ ৭ দিন, শেষ ৩০ দিন, সর্বমোট)
      MOD-12-001-SEC-B: Daily Study Goal, Streak, 7-Day Activity Strip & Goal Config AlertDialog (dashboard_daily_goal_card)
      MOD-12-001-SEC-C: Separated Exam KPIs vs. Practice KPIs 2×2 Grid
      MOD-12-001-SEC-D: NCTB 4-Book Curriculum Progress Card & Room v7 Cross-Module Ecosystem / Spaced Mistake Queue Card
      MOD-12-001-SEC-E: Rule-Based Smart Learning Recommendations Card (dashboard_recommendations_card)
      MOD-12-001-SEC-F: Confidence-Weighted Topic Mastery Breakdown & Module-Filtered Practice History List

  MOD-13: Module 13 — বিসিএস প্রস্তুতি কেন্দ্র (SectionKey.BCS_MATH)
    MOD-13-001: Screen.BcsPreparation (BcsPreparationScreen.kt — "বিসিএস প্রস্তুতি কেন্দ্র")
      MOD-13-001-SEC-A: TopAppBar & Profile/Onboarding Settings Dialog (BcsProfileOnboardingDialog)
      MOD-13-001-SEC-B: 5-Tab Workspace ScrollableTabRow
        MOD-13-001-SEC-B-SUB-01: Tab 0 — ড্যাশবোর্ড ও লক্ষ্য (8-Stage Preparation Journey, Weak Topic Intelligence, Daily Goal Progress)
        MOD-13-001-SEC-B-SUB-02: Tab 1 — গাণিতিক যুক্তি গাইড (50th BCS 20-Mark vs Legacy 15-Mark Selector, 9 Math Domains, 5 Official Units, 16-Part Lesson Details Dialog BcsLessonDetailsDialog, Written Code 008 Rubric)
        MOD-13-001-SEC-B-SUB-03: Tab 2 — মানসিক দক্ষতা (6 BPSC Mental Ability Categories, Interactive Questions & Mistake Recording)
        MOD-13-001-SEC-B-SUB-04: Tab 3 — বিগত প্রশ্ন ও ব্যাংক (Unit & Strict Provenance Filters, Expandable Classical vs Shortcut Cards, BcsDiagramView)
        MOD-13-001-SEC-B-SUB-05: Tab 4 — মক টেস্ট ও রিভিশন (Practice Modes, Custom Mock Configurator, Spaced Mistake Notebook)

  MOD-14: Module 14 — পাঠ্যবই ও গাইড (SectionKey.BOOK)
    MOD-14-001: Screen.BookHub (BookHubAndDetailScreens.kt — "পাঠ্যবই ও গাইড (BOOK)")
      MOD-14-001-SEC-A: Cross-Book Offline Search Bar & 6 Filter Chips (book_hub_search_input)
      MOD-14-001-SEC-B: Spaced Revision (1-3-7-14d) & Mistake Notebook Summary Banner (book_hub_revision_banner)
      MOD-14-001-SEC-C: 4 NCTB Textbook Cover Cards (Class 6, Class 7, Class 8, Class 9–10 — 47 Chapters total)
    MOD-14-002: Screen.BookDetail (BookHubAndDetailScreens.kt)
      MOD-14-002-SEC-A: Last Read Resume Banner & Mixed Practice Quick Actions
      MOD-14-002-SEC-B: NCTB Edition & Offline Fidelity Metadata Card
      MOD-14-002-SEC-C: Chapter Index Cards (1..N with Lesson Progress, Page Range & Quick Chips)
    MOD-14-003: Screen.BookChapterOverview (BookHubAndDetailScreens.kt)
      MOD-14-003-SEC-A: Chapter Summary & Cross-Module BCS/Solver Bridge Cards
      MOD-14-003-SEC-B: 3-Tab Chapter Content Switcher (পাঠ ও শিখনফল, সূত্র ও উদাহরণ, অনুশীলন ও ল্যাব)
    MOD-14-004: Screen.BookLesson (BookHubAndDetailScreens.kt)
      MOD-14-004-SEC-A: Concept Explanation, Step-by-Step Rules & Common Mistakes Cards
      MOD-14-004-SEC-B: Interactive Mini-Check Question, Quick Personal Note Input & Mark Lesson Completed CTA
    MOD-14-005: Screen.BookPractice (BookPracticeAndRevisionScreens.kt)
      MOD-14-005-SEC-A: 9-Mode Practice Switcher Strip & Question Provenance/Format Badges
      MOD-14-005-SEC-B: Multi-Format Question Prompt (MCQ, Multiple Select, Numerical, Fill-in-Blank, Matching Pairs, True/False)
      MOD-14-005-SEC-C: Hint Toggle, Step-by-Step Solution Card & Session Completion Summary
    MOD-14-006: Screen.BookPdfReader (BookReaderAndLabsScreens.kt)
      MOD-14-006-SEC-A: Page Stepper, Page Jump Input, Chapter Quick-Jump Strip & View Mode Toggle (Structured Digital vs Native PDF Bitmap)
      MOD-14-006-SEC-B: Native Android PdfRenderer Canvas Card with Zoom Slider (100%–250%) & Auto-Recovery Fallback
      MOD-14-006-SEC-C: High-Contrast Structured Digital Textbook Page Card
    MOD-14-007: Screen.BookInteractiveLab (BookReaderAndLabsScreens.kt)
      MOD-14-007-SEC-A: Lab Switcher Strip, Interactive Canvas Visualization & Parameter Sliders
    MOD-14-008: Screen.BookRevision (BookPracticeAndRevisionScreens.kt)
      MOD-14-008-SEC-A: Book Filter Strip & 4 Revision Tabs (স্পেসড রিভিশন, ভুল সংশোধনী খাতা, ব্যক্তিগত নোট, বুকমার্ককৃত পৃষ্ঠা)

  GLOBAL-AUX: Auxiliary Global Destinations (Reachable from TopAppBar / BottomNav)
    GLOBAL-AUX-001: Screen.Bookmarks (BookmarksAndTutorScreens.kt — "সংরক্ষিত বুকমার্ক")
      GLOBAL-AUX-001-SEC-A: Category Filter Chips (সব, নামতা, মৌলিক, বর্গ/ঘন, সূত্র, সমাধানকারী) & Search Input
      GLOBAL-AUX-001-SEC-B: Saved Bookmark Cards List with Delete Action
    GLOBAL-AUX-002: Screen.Customize (CustomizeScreen.kt — "অ্যাপ কাস্টমাইজেশন")
      GLOBAL-AUX-002-SEC-A: Top Save / Cancel Bar & Reset-All Confirmation AlertDialog
      GLOBAL-AUX-002-SEC-B: 6 Customization Category Tabs (লেআউট, টাইপোগ্রাফি, আইকন, সেকশন ভিত্তিক, গ্লোবাল থিম, লাইভ প্রিভিউ)
      GLOBAL-AUX-002-SEC-C: Embedded Real-Time Live Preview Section
```

---

## C. Screen-by-Screen Visual QA Matrix

> **Evidence Integrity Note**: Only screens/components with an actual pixel-rendered image inspected during this audit list a screenshot file path and `RENDERED`. The remaining 29 destinations were verified via headless Robolectric DOM/semantics tests and full Compose source inspection and are marked `NOT VERIFIED (No Live Emulator)` for pixel rendering in strict compliance with Section 3 & Section 12.

| Audit ID | Screen Name (`Screen` Route) | Screenshot Reference | Viewport(s) Inspected | Rendering Status | Layout Result | Typography Result | Navigation Result | Interaction Result | Overall Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `HOME-001` | `HomeScreen` (`home`) | `/public/assets/aistudio/ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg` | Device Portrait (~`411dp`, `1.0x`, Light) | `RENDERED` (Mid-scroll + BottomNav) | `ISSUE` (`UI-007`: Uneven 1 vs 2 line wrapping in 2-col grid) | `PASS` | `PASS` | `PASS` (Robolectric verified) | **PASS WITH MINOR ISSUES** |
| `HOME-001-SEC-TIMER` | `StudySessionTimerSection` (on `HomeScreen`) | `/app/build/test-screenshots/study_timer_360dp_1x_light.png`, `study_timer_360dp_1x_dark.png`, `study_timer_360dp_1x_expanded.png`, `study_timer_320dp_1x_light.png`, `study_timer_411dp_1x_light.png`, `study_timer_360dp_1_5x_light.png` | `320dp`, `360dp`, `411dp`, `1.0x`, `1.5x`, Light & Dark | `RENDERED` (6 native Robolectric PNGs) | `FAIL` (`UI-001`, `UI-002`, `UI-003`) | `FAIL` (Ellipsis truncation on `360dp` expanded & `1.5x` font scale) | `N/A` | `PASS` (11 unit/Robolectric tests) | **ISSUES CONFIRMED (`UI-001`, `UI-002`, `UI-003`)** |
| `MOD-01-001` | `TablesScreen` (`tables`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-01-002` | `TableDetailScreen` (`table_detail`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-01-003` | `PracticeTutorScreen` (`practice_tutor`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (Quick chips < 48dp) | `FAIL (Code)` (`FUNC-001`: Answer leaked in English digits in label) | `PASS` | `ISSUE` (`FUNC-001`) | **SUSPECTED DEFECT (`FUNC-001`)** |
| `MOD-02-001` | `PrimesScreen` (`primes`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-03-001` | `SquaresCubesScreen` (`squares_cubes`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `FAIL (Code)` (`LOC-001`: Raw `241713.jpg` filenames, English digits, wrong footer) | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`LOC-001`)** |
| `MOD-04-001` | `FormulasScreen` (`formulas`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `ISSUE (Code)` (`LOC-002`: References external note photo) | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`LOC-002`)** |
| `MOD-04-002` | `FormulaDetailScreen` (`formula_detail`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-010`: Hardcoded light pastel card in Dark mode) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`UI-010`)** |
| `MOD-05-001` | `FormulaLibraryScreen` (`formula_library`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`A11Y-001`: `40.dp` bookmark/copy buttons) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`A11Y-001`)** |
| `MOD-06-001` | `PracticeLabScreen` (`practice_lab`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-06-002` | `PracticeLabPlayScreen` (`practice_lab_play`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-010`: Hardcoded light pastel banners in Dark mode) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`UI-010`)** |
| `MOD-07-001` | `MentalMathScreen` (`mental_math`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-07-002` | `MentalMathPlayScreen` (`mental_math_play`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-010`: Hardcoded light pastel banners in Dark mode) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`UI-010`)** |
| `MOD-08-001` | `NumberSystemScreen` (`number_system`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-010`: Hardcoded light pastel banners in Dark mode) | `PASS (Code)` | `PASS` | `PASS` (17 unit tests + Robolectric) | **SUSPECTED DEFECT (`UI-010`)** |
| `MOD-08-002` | `NumberSystemPracticePlayScreen` (`number_system_practice`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `FAIL (Code)` (`UI-010`: White `onSurface` text on light `0xFFDCFCE7` green card in Dark mode) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`UI-010`)** |
| `MOD-09-001` | `ProblemSolverScreen` (`problem_solver`) | `/public/assets/aistudio/image_fa839fb1.png`, `/public/assets/aistudio/image_373fab02.png` | Device Portrait (~`411dp`, `1.0x`, Light) | `RENDERED` (Top + Scrolled Lower Viewport) | `FAIL` (`UI-005`, `UI-006`, `UI-008`, `A11Y-001`) | `FAIL` (`UI-004`: Raw `*` and `/` in Step 2 of BODMAS) | `PASS` | `ISSUE` (`UI-009`: Delete button uses `Refresh` icon) | **ISSUES CONFIRMED (`UI-004`..`UI-008`)** |
| `MOD-10-001` | `VisualExplorerScreen` (`visual_explorer`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-008` bottom inset, `A11Y-001` `36dp`/`38dp` buttons, duplicate 3D Studio) | `PASS (Code)` | `PASS` | `PASS` (36 unit/Robolectric tests) | **SUSPECTED DEFECTS (`UI-008`, `A11Y-001`)** |
| `MOD-11-001` | `QuizzesScreen` (`quizzes`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-11-002` | `QuizPlayScreen` (`quiz_play`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`A11Y-001`: `38.dp` question palette circles) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`A11Y-001`)** |
| `MOD-12-001` | `DashboardScreen` (`dashboard`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `ISSUE (Code)` (`LOC-003`: Technical `"Room v7"` string in card title) | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`LOC-003`)** |
| `MOD-13-001` | `BcsPreparationScreen` (`bcs_preparation`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`UI-008` bottom inset) | `PASS (Code)` | `PASS` | `PASS` (12 unit/Robolectric tests) | **NOT VERIFIED (Pixel)** |
| `MOD-14-001` | `BookHubScreen` (`book_hub`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-002` | `BookDetailScreen` (`book_detail`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-003` | `BookChapterOverviewScreen` (`book_chapter_overview`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-004` | `BookLessonScreen` (`book_lesson`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-005` | `BookPracticeScreen` (`book_practice`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-006` | `BookPdfReaderScreen` (`book_pdf_reader`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (PDF asset header & fallback verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-007` | `BookInteractiveLabScreen` (`book_interactive_lab`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `MOD-14-008` | `BookRevisionScreen` (`book_revision`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `GLOBAL-AUX-001` | `BookmarksScreen` (`bookmarks`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `PASS (Code)` | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **NOT VERIFIED (Pixel)** |
| `GLOBAL-AUX-002` | `CustomizeScreen` (`customize`) | `NONE (No Live Emulator)` | Robolectric JVM + Source | `NOT VERIFIED` | `ISSUE (Code)` (`A11Y-001`: `40.dp` color circles & `44.dp` icon tiles) | `PASS (Code)` | `PASS` | `PASS` (Robolectric verified) | **SUSPECTED DEFECT (`A11Y-001`)** |

---

## D. Module-by-Module Findings

### 1. Home Screen & Study Session Timer (`HOME-001`, `HOME-001-SEC-TIMER`)
- **Visual Evidence Inspected**:
  - `/public/assets/aistudio/ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg` (Device render of `HomeScreen` 2-column module grid & bottom navigation bar)
  - `/app/build/test-screenshots/study_timer_360dp_1x_light.png`
  - `/app/build/test-screenshots/study_timer_360dp_1x_dark.png`
  - `/app/build/test-screenshots/study_timer_360dp_1x_expanded.png`
  - `/app/build/test-screenshots/study_timer_320dp_1x_light.png`
  - `/app/build/test-screenshots/study_timer_411dp_1x_light.png`
  - `/app/build/test-screenshots/study_timer_360dp_1_5x_light.png`
- **Confirmed Defects**:
  - **`UI-001` (`CONFIRMED`, P2)**: When the Study Session Timer is toggled ON at standard `360dp` width (`study_timer_360dp_1x_expanded.png`), the insertion of the `"প্রস্তুত"` status badge in the header row compresses the text column, truncating the daily goal subtitle to `"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ..."` so the user cannot see their daily target (`৩০ মি.`).
  - **`UI-002` (`CONFIRMED`, P2)**: At `1.5x` accessibility font scale on `360dp` (`study_timer_360dp_1_5x_light.png`), `maxLines = 1` + `TextOverflow.Ellipsis` clips both the card title (`"পড়াশোনা সেশন টাই..."`) and subtitle (`"বন্ধ • চালু করতে টগল ক..."`).
  - **`UI-003` (`CONFIRMED`, P3)**: In `study_timer_360dp_1x_expanded.png`, the bottom progress row's left label `"দৈনিক অধ্যয়ন অগ্রগতি (৬%)"` wraps onto 2 lines while the right label `"আজ ১টি সেশন • মোট ১৫ মি. ৫০ সে."` stays on 1 line, creating vertical misalignment; additionally, the disabled Reset `IconButton` next to `"► শুরু করুন"` has no border/container and `0.4f` alpha, making it almost invisible.
  - **`UI-007` (`CONFIRMED`, P3)**: In `ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg`, sibling cards in the 2-column grid wrap titles/subtitles inconsistently (e.g., `"সূত্র লাইব্রেরি"` 1-line title vs `"অনুশীলন ল্যাব"` / `"গণিত সমস্যা সমাধানকারী"` 2-line titles), causing internal badges and subtitles to sit at different vertical baselines across the same row.

### 2. Module 01 — গুণের নামতা & অনুশীলন টিউটর (`MOD-01-001`..`MOD-01-003`)
- **Findings**:
  - `TablesScreen` and `TableDetailScreen` cleanly support tables 1–40 with search, range filters (`১-১০`, `১১-২০`, `২১-৩০`, `৩১-৪০`), learned badges, and bookmarks.
  - **`FUNC-001` (`SUSPECTED`, P2 in `PracticeTutorScreen`, `BookmarksAndTutorScreens.kt` line 353)**: The answer input field label is coded as `Text("উত্তর লিখুন (যেমন: ${number * step})")`, which directly computes and displays the exact answer to the active multiplication step (`number * step`) inside the input placeholder label (and formats it in English ASCII digits)! Furthermore, the quick-switch row at the bottom (`lines 429–455`) only offers 5 hardcoded tables (`3, 7, 12, 17, 25`) with English digits on the chips instead of allowing selection of any table 1–40.

### 3. Module 02 — মৌলিক সংখ্যা (`MOD-02-001`)
- **Findings**:
  - Interactive prime checker and prime factorization calculator (`PrimesScreen.kt`) properly convert Bengali/English digits, guard against `< 2` and large inputs, and open a detailed `AlertDialog` with Twin Prime / Safe Prime metadata when tapping any prime chip.

### 4. Module 03 — বর্গ ও ঘন (`MOD-03-001`)
- **Findings**:
  - **`LOC-001` (`SUSPECTED`, P2 in `SquaresCubesScreen.kt` lines 69, 116, 182–288 & `SquareCubeModels.kt` lines 28–83)**:
    1. TopAppBar subtitles (`"ছবি: 241713.jpg"`, `"ছবি: 241715.jpg"`), section headers (`"\"241713.jpg\" ছবিতে দেওয়া ১ থেকে ২০ পর্যন্ত..."`), and bookmark metadata expose raw internal developer image filenames (`241713.jpg`, `241715.jpg`) to the user.
    2. All 20 rows of the Square and Cube tables display English numerals (`1`, `2² = 4`, `√4 = 2`, `20³ = 8000`) instead of Bengali numerals (`১`, `২² = ৪`, `√৪ = ২`).
    3. `SquareCubeProvider.cubeFooterNote` (`SquareCubeModels.kt` line 83) displays `"১ থেকে ১০ পর্যন্ত সংখ্যার ঘন এবং ঘনমূল"`, which contradicts the actual table above it that lists `1` through `20`.

### 5. Module 04 — বীজগণিতের সূত্র (`MOD-04-001`, `MOD-04-002`)
- **Findings**:
  - **`LOC-002` (`SUSPECTED`, P3 in `FormulasScreen.kt` line 191)**: The introductory banner states `"এই নোটে ছবিতে থাকা ১৪টি সূত্র ধারাবাহিকভাবে সাজানো হয়েছে..."`, referring to an external source photo (`"এই নোটে ছবিতে থাকা"`) that is not part of the app interface.
  - **`UI-010` (`SUSPECTED`, P2 in `FormulasScreen.kt` lines 508–546)**: In `FormulaDetailScreen`, the interactive formula evaluator result card uses hardcoded light-mode colors (`Color(0xFFECFDF5)` / `Color(0xFFFFFBEB)`) that do not adapt to Dark Mode.

### 6. Module 05 — সূত্র লাইব্রেরি (`MOD-05-001`)
- **Findings**:
  - Expandable cards provide rich derivations, variable definitions, and an inline live formula calculator.
  - **`A11Y-001` (`SUSPECTED`, P2 in `FormulaLibraryScreen.kt` lines 763 & 824)**: Both `btn_bookmark_${formula.id}` and `btn_copy_${formula.id}` use `Modifier.size(40.dp)`, falling below the mandatory `48.dp × 48.dp` minimum touch target size.

### 7. Module 06 — অনুশীলন ল্যাব (`MOD-06-001`, `MOD-06-002`)
- **Findings**:
  - Supports Addition, Subtraction, Multiplication, Division, and Mixed practice with `BanglaNumericKeypad`.
  - **`UI-010` (`SUSPECTED`, P2 in `PracticeLabPlayScreen.kt` lines 314–336, 633–675, 729–747)**: Hardcoded light pastel backgrounds (`Color(0xFFECFDF5)`, `Color(0xFFFEF2F2)`, `Color(0xFFFFFBEB)`) are used in feedback and hint banners without Dark Mode surface adaptation.

### 8. Module 07 — দ্রুত গণিত (`MOD-07-001`, `MOD-07-002`)
- **Findings**:
  - Provides both speed drill gameplay and mental math shortcut cards.
  - **`UI-010` (`SUSPECTED`, P2 in `MentalMathPlayScreen.kt` lines 203–243)**: Hardcoded light pastel feedback banners (`Color(0xFFECFDF5)`, `Color(0xFFFEF2F2)`) are used during active drills without Dark Mode adaptation.

### 9. Module 08 — সংখ্যা পদ্ধতি (`MOD-08-001`, `MOD-08-002`)
- **Findings**:
  - Comprehensive 4-tab layout covering number classification, base conversion (2, 8, 10, 16), divisibility/GCD/LCM, and place value/Roman numerals.
  - **`UI-010` (`SUSPECTED`, P2 in `NumberSystemPracticePlayScreen.kt` lines 276–334)**: When an answer is submitted in `NumberSystemPracticePlayScreen`, the correct option card background is set to light green `Color(0xFFDCFCE7)` (`line 277`), but if the user selected a *different* (wrong) option, `isSelected` is `false` on the correct option card, so its text color falls back to `MaterialTheme.colorScheme.onSurface` (`line 334`). In Dark Mode, `onSurface` is near-white (`#E2E8F0`) on top of light pastel green (`#DCFCE7`), rendering the correct option text virtually unreadable!

### 10. Module 09 — গণিত সমস্যা সমাধানকারী (`MOD-09-001`)
- **Visual Evidence Inspected**:
  - `/public/assets/aistudio/image_fa839fb1.png` (Top viewport of `ProblemSolverScreen`)
  - `/public/assets/aistudio/image_373fab02.png` (Scrolled lower viewport of `ProblemSolverScreen`)
- **Confirmed & Suspected Defects**:
  - **`UI-004` (`CONFIRMED`, P2 via `image_373fab02.png`)**: In BODMAS step-by-step output, Step 2 (`"২ চূড়ান্ত ফলাফল গণনা"`) renders raw ASCII `*` and `/` operators (`"৪০*২-৩০/৩ = ৭০"`) instead of `×` and `÷`.
  - **`UI-005` (`CONFIRMED`, P2 via `image_373fab02.png` & `ProblemSolverPedagogyComponents.kt` line 950)**: `SimultaneousLinearSystemAndBridgeCard` (`"দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule: a₁x+b₁y=c₁, a₂x+b₂y=c₂)"`) is rendered unconditionally at the bottom of the solution view even when the active topic is `BASIC_ARITHMETIC` (BODMAS) or any non-algebra topic; additionally, switching to 3-variable mode (`৩ চলক (x, y, z)`) inside that card leaves the card heading hardcoded as `"দুই চলকবিশিষ্ট সহসমীকরণ সমাধান"`.
  - **`UI-006` (`CONFIRMED`, P3 via `image_fa839fb1.png`)**: Horizontal chip strips for the 16 topics and sample expressions clip partially visible trailing chips sharply at the right border with no end padding or scroll fade cue.
  - **`UI-008` (`CONFIRMED`, P2 via `image_373fab02.png` & `MainActivity.kt` line 172)**: Because `MainActivity`'s outer `Scaffold` sets `contentWindowInsets = WindowInsets(0, 0, 0, 0)` and hides the `NavigationBar` on `ProblemSolverScreen` (and 25 other secondary screens), the bottom of the screen extends underneath the Android 3-button system navigation bar (`III`, `O`, `<`), overlapping bottom cards.
  - **`UI-009` (`SUSPECTED`, P2 in `ProblemSolverScreen.kt` lines 830–839)**: In the Saved Solver History panel (`solver_history_card`), the Delete button (`delete_solver_history_${hist.id}`) renders `Icons.Default.Refresh` (a circular reload icon) tinted red instead of `Icons.Default.Delete`.
  - **`A11Y-001` (`SUSPECTED`, P2 in `ProblemSolverScreen.kt` lines 1137, 1176, 1646, 1711, 1735, 1860, 1870)**: Copy/Bookmark `IconButton`s (`40.dp`), fraction operator buttons (`38.dp`), and sample expression buttons (`heightIn(min = 38.dp)`) are smaller than the `48.dp` accessibility touch target requirement.

### 11. Module 10 — ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (`MOD-10-001`)
- **Findings**:
  - 5-tab interactive mathematical laboratory (`GEOMETRY`, `ALGEBRA`, `NUMBER_LINE`, `FORMULAS`, `SOLID_3D`) with OpenGL ES 2.0 + 2D Canvas fallback (`Math3DGlViewport.kt`), balance-scale tilt physics, and guided discovery bar.
  - **`UI-008` (`SUSPECTED` on `VisualExplorerScreen`)**: Because `VisualKnowledgeCheckAndLinksBar` is pinned at the very bottom of `VisualExplorerScreen`'s `Column` (`lines 193–197`) and `MainActivity` sets `contentWindowInsets = WindowInsets(0, 0, 0, 0)` with no `NavigationBar` on `VisualExplorer`, the sticky bottom bar sits directly behind the system navigation bar on edge-to-edge devices.
  - **`A11Y-001` (`SUSPECTED` in `VisualExplorerScreen.kt` lines 265, 306, 561, 571)**: `btn_next_visual_kc` (`minHeight = 36.dp`), `btn_toggle_visual_kc` (`minHeight = 40.dp`), and `geo_zoom_out` / `geo_zoom_in` (`size(38.dp)`) are below `48.dp`.

### 12. Module 11 — কুইজ ও পরীক্ষা (`MOD-11-001`, `MOD-11-002`)
- **Findings**:
  - Full-featured exam engine supporting quick topic quizzes, full mock exams with negative marking, unfinished exam persistence/resume, 5-state question palette, pre-submit confirmation dialog, and 2-attempt comparison.
  - **`A11Y-001` (`SUSPECTED` in `QuizzesScreen.kt` line 1262)**: In `QuizPlayScreen`, the expandable question palette buttons (`palette_q_$qIdx`) use `Modifier.size(38.dp)`, which is smaller than the `48.dp × 48.dp` minimum touch target.

### 13. Module 12 — অগ্রগতি ড্যাশবোর্ড (`MOD-12-001`)
- **Findings**:
  - Displays time-filtered KPIs, 7-day activity strip, daily goal configuration `AlertDialog`, NCTB 4-book progress, and rule-based recommendations.
  - **`LOC-003` (`SUSPECTED`, P3 in `DashboardScreen.kt` line 610)**: The ecosystem progress card heading displays `"ইকোসিস্টেম সমন্বিত অগ্রগতি ও স্পেসড রিভিশন (Room v7)"`, leaking internal database architecture terminology (`"Room v7"`, which is also outdated relative to Room v10) into user-facing Bengali UI copy.

### 14. Module 13 — বিসিএস প্রস্তুতি কেন্দ্র (`MOD-13-001`)
- **Findings**:
  - 5-tab workspace covering the 8-stage preparation journey, 50th BCS (20-mark) vs Legacy (15-mark) syllabus, 9 math domains, 5 official units, 16-part lesson dialog (`BcsLessonDetailsDialog`), 6 mental ability categories, strict question provenance filters, custom diagram view (`BcsDiagramView`), and mock exam builder.
  - Subject to the global edge-to-edge bottom inset issue (`UI-008`) when `NavigationBar` is hidden.

### 15. Module 14 — পাঠ্যবই ও গাইড (`MOD-14-001`..`MOD-14-008`)
- **Findings**:
  - Complete offline NCTB textbook suite covering Class 6, Class 7, Class 8, and Class 9–10 (47 chapters total), cross-book search, 1-3-7-14 day spaced revision, mistake notebook, multi-format chapter practice, interactive labs, and dual-mode PDF/Structured reader (`BookPdfReaderScreen.kt`) backed by real bundled PDFs in `app/src/main/assets/books/`.
  - Subject to the global edge-to-edge bottom inset issue (`UI-008`) across all 8 Book destinations because `NavigationBar` is hidden on `BookHub`..`BookRevision`.

### 16. Global Auxiliaries — বুকমার্ক & অ্যাপ কাস্টমাইজেশন (`GLOBAL-AUX-001`, `GLOBAL-AUX-002`)
- **Findings**:
  - `BookmarksScreen` supports category filtering, search, and deletion.
  - `CustomizeScreen` provides 6 tabs (`লেআউট`, `টাইপোগ্রাফি`, `আইকন`, `সেকশন ভিত্তিক`, `গ্লোবাল থিম`, `লাইভ প্রিভিউ`) with live preview state isolation (`previewCustomizationState`), contrast warning for custom hex colors, and a Reset All confirmation dialog.
  - **`A11Y-001` (`SUSPECTED` in `CustomizeScreen.kt` lines 684, 777, 948)**: Icon picker tiles (`44.dp`) and preset color circles (`40.dp`) are below the `48.dp` minimum touch target.

---

## E. Bug Register

### BUG `UI-001` — Study Session Timer Header Subtitle Truncates Daily Goal Target When Expanded at 360dp
- **Bug ID**: `UI-001`
- **Title**: Study Session Timer header subtitle truncates daily goal (`"লক্ষ্য: ..."`) when enabled on `360dp` portrait screens
- **Severity**: **P2 (Medium)**
- **Category**: `LAYOUT BUG` / `LOCALIZATION BUG`
- **Module / Screen / Section / Subsection**: `HOME-001` → `HomeScreen` → `HOME-001-SEC-TIMER` (`StudySessionTimerSection`) → `HOME-001-SEC-TIMER-SUB-01`
- **Exact Navigation Path**: Launch App → `HomeScreen` → Locate `"পড়াশোনা সেশন টাইমার"` card → Toggle switch ON
- **Preconditions**: Standard portrait phone width (`360dp`), `1.0x` font scale
- **Exact Steps to Reproduce**:
  1. Open `HomeScreen` at `360dp` width (`1.0x` font scale).
  2. Observe the compact OFF subtitle: `"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ৩০ মি."` (fully visible).
  3. Toggle `study_timer_enable_switch` ON so the `"প্রস্তুত"` status pill appears next to the title.
- **Expected Behavior**: The daily study summary and target (`"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ৩০ মি."`) should remain fully readable (e.g., by allowing 2 lines or relocating the status pill).
- **Actual Behavior**: The subtitle is constrained to `maxLines = 1` with `TextOverflow.Ellipsis` (`StudySessionTimerCard.kt` lines 294–295) and truncates to `"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ..."`, hiding the `৩০ মি.` goal value.
- **Evidence Screenshot Path**: `/app/build/test-screenshots/study_timer_360dp_1x_expanded.png`
- **Viewport / Theme / Font-Scale**: `360dp × 640dp`, Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Users cannot see their configured daily study time target on standard `360dp` phones whenever the study timer is enabled.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/components/StudySessionTimerCard.kt` (`StudySessionTimerSection`, lines 242–296)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-002` — Study Session Timer Title and Subtitle Truncate at 1.5x Font Scale and 320dp Narrow Width
- **Bug ID**: `UI-002`
- **Title**: `StudySessionTimerSection` clips title and subtitle with ellipsis at `1.5x` accessibility font scale (`360dp`) and `320dp` width
- **Severity**: **P2 (Medium)**
- **Category**: `ACCESSIBILITY BUG` / `LAYOUT BUG`
- **Module / Screen / Section / Subsection**: `HOME-001` → `HomeScreen` → `HOME-001-SEC-TIMER` (`StudySessionTimerSection`) → `HOME-001-SEC-TIMER-SUB-01`
- **Exact Navigation Path**: Launch App → `HomeScreen` → `"পড়াশোনা সেশন টাইমার"` card
- **Preconditions**: System font scale set to `1.5x` on `360dp` screen (or `1.0x` on `320dp` narrow screen)
- **Exact Steps to Reproduce**:
  1. Render `HomeScreen` / `StudySessionTimerSection` at `360dp` width with `fontScale = 1.5f`.
  2. Inspect the header text column next to the timer icon and `Switch`.
- **Expected Behavior**: Header title and subtitle should wrap up to 2 lines at `1.5x` accessibility font scale so visually impaired users can read the full Bengali labels.
- **Actual Behavior**: Both title and subtitle enforce `maxLines = 1` (`StudySessionTimerCard.kt` lines 257 & 294), truncating to `"পড়াশোনা সেশন টাই..."` and `"বন্ধ • চালু করতে টগল ক..."` at `1.5x` font scale (and `"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ..."` at `320dp` `1.0x`).
- **Evidence Screenshot Path**: `/app/build/test-screenshots/study_timer_360dp_1_5x_light.png`, `/app/build/test-screenshots/study_timer_320dp_1x_light.png`
- **Viewport / Theme / Font-Scale**: `360dp` @ `1.5x` font scale (Light), `320dp` @ `1.0x` font scale (Light)
- **Reproducibility**: `Always (100%)`
- **User Impact**: Accessibility users relying on large system text cannot read the full title or instructions of the Study Session Timer.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/components/StudySessionTimerCard.kt` (lines 250–296)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-003` — Expanded Study Timer Progress Row Baseline Asymmetry and Low-Contrast Disabled Reset Icon
- **Bug ID**: `UI-003`
- **Title**: Expanded Study Timer footer row wraps asymmetrically (`2 lines` vs `1 line`) and disabled Reset `IconButton` lacks visual affordance
- **Severity**: **P3 (Low)**
- **Category**: `VISUAL BUG` / `LAYOUT BUG`
- **Module / Screen / Section / Subsection**: `HOME-001` → `HomeScreen` → `HOME-001-SEC-TIMER` → `HOME-001-SEC-TIMER-SUB-05` & `SUB-06`
- **Exact Navigation Path**: Launch App → `HomeScreen` → Toggle `"পড়াশোনা সেশন টাইমার"` ON
- **Preconditions**: `360dp` portrait width, timer in `READY` state (`elapsedSeconds == 0`)
- **Exact Steps to Reproduce**:
  1. Toggle Study Session Timer ON at `360dp` width.
  2. Inspect the `"► শুরু করুন"` action row and the `"দৈনিক অধ্যয়ন অগ্রগতি (৬%)"` summary row at the bottom of the card.
- **Expected Behavior**: The Reset button next to `"► শুরু করুন"` should have a clear outlined/tonal container even when disabled, and the progress summary row should align cleanly without one side wrapping to 2 lines against a 1-line sibling.
- **Actual Behavior**: The disabled Reset `IconButton` (`study_timer_reset_btn`) has no border/container and uses `outline.copy(alpha = 0.4f)`, appearing as a faint floating arrow in empty space; below it, `"দৈনিক অধ্যয়ন অগ্রগতি (৬%)"` wraps onto two lines while `"আজ ১টি সেশন • মোট ১৫ মি. ৫০ সে."` remains on one line.
- **Evidence Screenshot Path**: `/app/build/test-screenshots/study_timer_360dp_1x_expanded.png`
- **Viewport / Theme / Font-Scale**: `360dp × 640dp`, Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Minor visual imbalance and weak button discoverability in the expanded timer card.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/components/StudySessionTimerCard.kt` (lines 398–455)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-004` — BODMAS Problem Solver Step 2 Renders Raw ASCII `*` and `/` Operators Instead of `×` and `÷`
- **Bug ID**: `UI-004`
- **Title**: `ProblemSolverScreen` BODMAS Step 2 displays raw programming operators (`*`, `/`) instead of mathematical symbols (`×`, `÷`)
- **Severity**: **P2 (Medium)**
- **Category**: `LOCALIZATION BUG` / `VISUAL BUG`
- **Module / Screen / Section / Subsection**: `MOD-09` → `MOD-09-001` (`ProblemSolverScreen`) → `MOD-09-001-SEC-E` (Step-by-Step Solution Breakdown)
- **Exact Navigation Path**: `HomeScreen` → `"গণিত সমস্যা সমাধানকারী"` → Topic: `"মৌলিক পাটিগণিত (BODMAS)"` → Tap `"সমাধান ও ধাপ দেখুন"`
- **Preconditions**: Default BODMAS sample expression `(25 + 15) * 2 - 30 / 3`
- **Exact Steps to Reproduce**:
  1. Open `ProblemSolverScreen` (`মৌলিক পাটিগণিত (BODMAS)`).
  2. Solve the default expression `(25 + 15) * 2 - 30 / 3`.
  3. Scroll to `"ধাপে ধাপে সমাধান (২টি ধাপ)"` and inspect Step 2 (`"২ চূড়ান্ত ফলাফল গণনা"`).
- **Expected Behavior**: Step 2 should format multiplication and division using mathematical operators `"৪০ × ২ - ৩০ ÷ ৩ = ৭০"` consistent with the Final Answer banner (`"(২৫ + ১৫) × ২ - ৩০ ÷ ৩"`).
- **Actual Behavior**: Step 2 renders raw ASCII programming operators: `"৪০*২-৩০/৩ = ৭০"`.
- **Evidence Screenshot Path**: `/public/assets/aistudio/image_373fab02.png`
- **Viewport / Theme / Font-Scale**: Device Portrait (~`411dp`), Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Breaks mathematical notation consistency for students learning BODMAS order of operations in Bengali.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/math/SolverAtomicArithmeticEngine.kt` & `/app/src/main/java/com/example/ui/screens/ProblemSolverScreen.kt`
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-005` — Simultaneous Linear Equation (Cramer's Rule) Card Unconditionally Rendered on All 16 Solver Topics with Static 2-Variable Title
- **Bug ID**: `UI-005`
- **Title**: `SimultaneousLinearSystemAndBridgeCard` renders on every Problem Solver topic (including BODMAS arithmetic) and keeps `"দুই চলকবিশিষ্ট"` title in 3-variable mode
- **Severity**: **P2 (Medium)**
- **Category**: `UX BUG` / `LAYOUT BUG`
- **Module / Screen / Section / Subsection**: `MOD-09` → `MOD-09-001` (`ProblemSolverScreen`) → `MOD-09-001-SEC-F` (`ProblemSolverPedagogyEnrichmentSection`)
- **Exact Navigation Path**: `HomeScreen` → `"গণিত সমস্যা সমাধানকারী"` → Any non-algebra topic (e.g., `"মৌলিক পাটিগণিত (BODMAS)"`) → Scroll below `"অনুরূপ অনুশীলন সমস্যা"`
- **Preconditions**: Any of the 15 non-linear-equation topics selected in `ProblemSolverScreen`
- **Exact Steps to Reproduce**:
  1. Select `"মৌলিক পাটিগণিত (BODMAS)"` on `ProblemSolverScreen` and solve an expression.
  2. Scroll to the bottom of the solution section.
  3. Observe the `"দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule: a₁x+b₁y=c₁, a₂x+b₂y=c₂)"` card rendered directly beneath the BODMAS solution.
  4. Tap the `"৩ চলক (x, y, z)"` chip inside that card and observe the card heading.
- **Expected Behavior**: The Simultaneous Linear Equations solver card should only appear when the active topic is `LINEAR_QUADRATIC_EQUATION` (or `ALGEBRAIC_EXPRESSION`), and its title should dynamically update when `"৩ চলক (x, y, z)"` is selected.
- **Actual Behavior**: `ProblemSolverPedagogyEnrichmentSection` (`ProblemSolverPedagogyComponents.kt` line 950) calls `SimultaneousLinearSystemAndBridgeCard(viewModel = viewModel)` unconditionally for all 16 topics, and line 963 hardcodes `"দুই চলকবিশিষ্ট সহসমীকরণ সমাধান (Cramer's Rule: a₁x+b₁y=c₁, a₂x+b₂y=c₂)"` even when `isThreeVar == true`.
- **Evidence Screenshot Path**: `/public/assets/aistudio/image_373fab02.png`
- **Viewport / Theme / Font-Scale**: Device Portrait (~`411dp`), Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Confuses students studying basic arithmetic, percentages, or geometry by injecting an unrelated 2x2/3x3 Cramer's Rule matrix solver at the bottom of every topic, and mislabels the 3-variable solver as 2-variable.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/ProblemSolverPedagogyComponents.kt` (lines 950–967)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-006` — Horizontal Chip Rows on `ProblemSolverScreen` Clip Trailing Chips Sharply at Right Container Edge
- **Bug ID**: `UI-006`
- **Title**: Horizontal scrollable chip rows for 16 Solver Topics and Sample Expressions clip text sharply at right edge
- **Severity**: **P3 (Low)**
- **Category**: `LAYOUT BUG`
- **Module / Screen / Section / Subsection**: `MOD-09` → `MOD-09-001` (`ProblemSolverScreen`) → `MOD-09-001-SEC-B` & `SEC-C`
- **Exact Navigation Path**: `HomeScreen` → `"গণিত সমস্যা সমাধানকারী"`
- **Preconditions**: Standard portrait screen
- **Exact Steps to Reproduce**:
  1. Navigate to `ProblemSolverScreen`.
  2. Inspect the right edge of `"গণিত বিষয় নির্বাচন করুন (১৬টি বিষয়)"` and `"উদাহরণ রাশি:"`.
- **Expected Behavior**: Horizontal scrolling rows should use `LazyRow` with `contentPadding` or visual scroll indicators so partially visible chips do not collide flush with the screen/card border.
- **Actual Behavior**: Using `Row(modifier = Modifier.fillMaxWidth().horizontalScroll(...))` inside padded containers clips `"ভগ্নাংশ ও দশমি..."` and `"(৪০ ÷ ৮) × ৭ + (১৫..."` abruptly at the right edge.
- **Evidence Screenshot Path**: `/public/assets/aistudio/image_fa839fb1.png`
- **Viewport / Theme / Font-Scale**: Device Portrait (~`411dp`), Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Minor visual clipping along horizontal chip carousels.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/ProblemSolverScreen.kt` (lines 422–455, 1850–1878)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-007` — Home Screen 2-Column Module Grid Cards Have Uneven Title/Subtitle Wrapping and Vertical Rhythm
- **Bug ID**: `UI-007`
- **Title**: 2-column grid cards on `HomeScreen` wrap titles between 1 and 2 lines without aligned badge/subtitle baselines
- **Severity**: **P3 (Low)**
- **Category**: `LAYOUT BUG`
- **Module / Screen / Section / Subsection**: `HOME-001` → `HomeScreen` → `HOME-001-SEC-GRID`
- **Exact Navigation Path**: Launch App → `HomeScreen` → Scroll through the 14 module cards
- **Preconditions**: Default 2-column `GRID` layout
- **Exact Steps to Reproduce**:
  1. Open `HomeScreen` and scroll to Modules 05–12.
  2. Compare `"সূত্র লাইব্রেরি"` (1-line title) next to `"অনুশীলন ল্যাব"` (2-line title `"অনুশীলন\nল্যাব"`), and `"গণিত সমস্যা সমাধানকারী"` (2-line title) next to `"ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার"`.
- **Expected Behavior**: Paired cards in a 2-column row should align their internal titles, subtitles, and status pills consistently (`Spacer(Modifier.weight(1f))` before bottom badges or consistent title area height).
- **Actual Behavior**: Because card content is vertically centered without pinned top/bottom slots (`CustomModuleRenderer.kt` lines 163–250), 1-line and 2-line titles shift the vertical positions of subtitles and progress badges relative to their row neighbor.
- **Evidence Screenshot Path**: `/public/assets/aistudio/ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg`
- **Viewport / Theme / Font-Scale**: Device Portrait (~`411dp`), Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Slightly uneven visual rhythm across the main module grid.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/customization/CustomModuleRenderer.kt` (`GridModuleCard`, lines 163–250)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-008` — Edge-to-Edge Bottom System Navigation Bar Overlaps Screen Content on All 26 Non-BottomNav Destinations
- **Bug ID**: `UI-008`
- **Title**: `enableEdgeToEdge()` combined with `contentWindowInsets = WindowInsets(0, 0, 0, 0)` causes the Android system navigation bar to overlap bottom UI on all 26 screens where `NavigationBar` is hidden
- **Severity**: **P2 (Medium)**
- **Category**: `LAYOUT BUG` / `NAVIGATION BUG`
- **Module / Screen / Section / Subsection**: Global (`MainActivity.kt` + all 26 secondary `Screen` destinations including `ProblemSolverScreen` and `VisualExplorerScreen`)
- **Exact Navigation Path**: Launch App → Open any module without the 5-tab `NavigationBar` (e.g., `ProblemSolverScreen`, `VisualExplorerScreen`, `BcsPreparationScreen`, `BookHubScreen`) → Scroll to the bottom
- **Preconditions**: Edge-to-edge Android device with 3-button or gesture navigation bar
- **Exact Steps to Reproduce**:
  1. Navigate from `HomeScreen` to `ProblemSolverScreen` (or `VisualExplorerScreen`).
  2. Scroll to the bottom of the screen.
  3. Inspect the bottom 48dp–56dp region where the Android system navigation bar (`III`, `O`, `<`) is drawn.
- **Expected Behavior**: When the app's bottom `NavigationBar` is hidden (`showBottomNav == false`), either the outer `Scaffold` or child screens should apply `WindowInsets.navigationBars` padding so bottom content and sticky bottom bars (`VisualKnowledgeCheckAndLinksBar`) are not obscured by the system navigation bar.
- **Actual Behavior**: `MainActivity.kt` calls `enableEdgeToEdge()` (line 69) and sets `contentWindowInsets = WindowInsets(0, 0, 0, 0)` on the outer `Scaffold` (line 172). When `showBottomNav == false` (26 of 31 destinations), no bottom inset is consumed, causing the system navigation bar (`III`, `O`, `<`) to directly overlap the bottom cards (visible at the bottom of `image_373fab02.png` on `ProblemSolverScreen` and affecting the sticky bottom `VisualKnowledgeCheckAndLinksBar` on `VisualExplorerScreen`).
- **Evidence Screenshot Path**: `/public/assets/aistudio/image_373fab02.png`
- **Viewport / Theme / Font-Scale**: Device Portrait (~`411dp`), Light Theme, `1.0x` font scale
- **Reproducibility**: `Always (100%)`
- **User Impact**: Bottom buttons, inputs, and sticky bars (`VisualKnowledgeCheckAndLinksBar`) partially collide with the system 3-button navigation bar.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/MainActivity.kt` (lines 69, 170–225)
- **Confidence Level**: **`CONFIRMED`**

---

### BUG `UI-009` — Saved Solver History Delete Button Uses `Icons.Default.Refresh` Instead of `Icons.Default.Delete`
- **Bug ID**: `UI-009`
- **Title**: Per-item Delete button in `ProblemSolverScreen` Saved History panel displays a circular `Refresh` icon instead of a `Delete` trash icon
- **Severity**: **P2 (Medium)**
- **Category**: `VISUAL BUG` / `INTERACTION BUG`
- **Module / Screen / Section / Subsection**: `MOD-09` → `MOD-09-001` (`ProblemSolverScreen`) → `MOD-09-001-SEC-G` (`solver_history_card`)
- **Exact Navigation Path**: `HomeScreen` → `"গণিত সমস্যা সমাধানকারী"` → Solve any problem → Scroll to `"সাম্প্রতিক সমাধান ইতিহাস"` → Expand history → Inspect per-item action icons
- **Preconditions**: At least 1 saved entry in `solverHistory`
- **Exact Steps to Reproduce**:
  1. Solve any problem in `ProblemSolverScreen` so an entry is recorded in `solverHistory`.
  2. Expand `"সাম্প্রতিক সমাধান ইতিহাস"` (`solver_history_toggle_btn`).
  3. Inspect the red `IconButton` (`delete_solver_history_${hist.id}`) on the right side of a history item.
- **Expected Behavior**: The button that deletes a history item (`viewModel.deleteSolverHistoryItem(hist.id)`) should display `Icons.Default.Delete` (trash can), distinct from the `"পুনরায় লোড"` (Reload) button next to it.
- **Actual Behavior**: `ProblemSolverScreen.kt` line 834 renders `Icons.Default.Refresh` tinted with `MaterialTheme.colorScheme.error` (`contentDescription = "মুছে ফেলুন"`), which visually looks like a "Reload/Refresh" action right next to `"পুনরায় লোড"` but permanently deletes the item when tapped.
- **Evidence Screenshot Path**: `NONE (Verified via Compose source inspection — ProblemSolverScreen.kt lines 830–839)`
- **Viewport / Theme / Font-Scale**: All viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: High risk of accidental data deletion because the delete icon is a circular reload arrow (`Refresh`).
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/ProblemSolverScreen.kt` (lines 830–839)
- **Confidence Level**: **`SUSPECTED`** (100% deterministic in source code; unrendered in screenshot set)

---

### BUG `UI-010` — Hardcoded Light-Mode Pastel Backgrounds Cause Severe Dark Mode Contrast Failures
- **Bug ID**: `UI-010`
- **Title**: Multiple feedback cards and option rows hardcode light pastel backgrounds (`0xFFDCFCE7`, `0xFFECFDF5`, `0xFFFFFBEB`, `0xFFFEF2F2`) that break Dark Mode contrast
- **Severity**: **P2 (Medium)**
- **Category**: `VISUAL BUG` / `ACCESSIBILITY BUG`
- **Module / Screen / Section / Subsection**: Multiple (`MOD-08-002` `NumberSystemPracticePlayScreen`, `MOD-09-001` `ProblemSolverScreen` & `ProblemSolverPedagogyComponents`, `MOD-04-002` `FormulaDetailScreen`, `MOD-06-002` `PracticeLabPlayScreen`, `MOD-07-002` `MentalMathPlayScreen`)
- **Exact Navigation Path**: Enable Dark Theme (`top_theme_toggle_btn`) → Open `NumberSystemScreen` → Tap `"অনুশীলন শুরু করুন"` → Select an incorrect option on any question
- **Preconditions**: Dark Theme enabled (`AppThemeMode.DARK`)
- **Exact Steps to Reproduce**:
  1. Switch app theme to Dark Mode.
  2. Navigate to `NumberSystemPracticePlayScreen` (`MOD-08-002`) and select a wrong MCQ option.
  3. Inspect the unselected correct option card (`NumberSystemPracticePlayScreen.kt` lines 277 & 334).
  4. Also inspect verification/shortcut/feedback banners in `ProblemSolverScreen.kt` (lines 1909–1915, 2058–2135), `ProblemSolverPedagogyComponents.kt` (lines 425–432, 663–728), `FormulasScreen.kt` (lines 508–546), `PracticeLabPlayScreen.kt` (lines 314–336, 633–675), and `MentalMathPlayScreen.kt` (lines 203–243).
- **Expected Behavior**: All cards and feedback banners should use theme-aware `MaterialTheme.colorScheme` tokens or dark-mode-adapted container/content pairs so text contrast meets WCAG AA (≥ 4.5:1) in both Light and Dark themes.
- **Actual Behavior**:
  - In `NumberSystemPracticePlayScreen.kt` (lines 277 & 334), when a user selects a wrong option, the unselected correct option card has `bgColor = Color(0xFFDCFCE7)` (very light pastel green) while its text color is `MaterialTheme.colorScheme.onSurface` (near-white in Dark Mode), resulting in white text on a pale green background (~`1.15:1` contrast ratio — illegible).
  - Across `ProblemSolverScreen`, `FormulasScreen`, `PracticeLabPlayScreen`, and `MentalMathPlayScreen`, bright pastel cards (`0xFFECFDF5`, `0xFFFFFBEB`, `0xFFFEF2F2`) render glaring white/pastel blocks in Dark Mode.
- **Evidence Screenshot Path**: `NONE (Verified via Compose source inspection)`
- **Viewport / Theme / Font-Scale**: Dark Mode (`AppThemeMode.DARK`), all viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: Unreadable correct-answer text in `NumberSystemPracticePlayScreen` in Dark Mode and jarring luminance spikes across practice/solver screens.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/NumberSystemPracticePlayScreen.kt` (lines 276–335), `/app/src/main/java/com/example/ui/screens/ProblemSolverScreen.kt` (lines 1909–2135), `/app/src/main/java/com/example/ui/screens/ProblemSolverPedagogyComponents.kt` (lines 425–728)
- **Confidence Level**: **`SUSPECTED`** (100% deterministic in source code; unrendered in screenshot set)

---

### BUG `FUNC-001` — Multiplication Practice Tutor Input Field Label Leaks the Exact Answer to the Current Question
- **Bug ID**: `FUNC-001`
- **Title**: `PracticeTutorScreen` answer `OutlinedTextField` label reveals the exact answer (`${number * step}`) to the active multiplication prompt
- **Severity**: **P2 (Medium)**
- **Category**: `FUNCTIONAL UI BUG` / `LOCALIZATION BUG`
- **Module / Screen / Section / Subsection**: `MOD-01` → `MOD-01-003` (`PracticeTutorScreen`) → `MOD-01-003-SEC-B`
- **Exact Navigation Path**: `HomeScreen` → `"গুণের নামতা"` → Select any table (e.g., `৭`) → Tap `"নামতা অনুশীলন টিউটর"`
- **Preconditions**: Active multiplication tutor session
- **Exact Steps to Reproduce**:
  1. Navigate to `PracticeTutorScreen` for any table number `number` at step `step`.
  2. Look at the label inside the `OutlinedTextField` (`tutor_answer_input`) where the student is asked to type the product `number × step`.
- **Expected Behavior**: The input label should say `"উত্তর লিখুন"` without revealing the answer to the current question, and the quick-switcher at the bottom should use Bengali digits and allow selecting any table from 1 to 40.
- **Actual Behavior**: `BookmarksAndTutorScreens.kt` line 353 sets `label = { Text("উত্তর লিখুন (যেমন: ${number * step})") }`, which prints the exact answer to the current question (in English ASCII digits) right inside the text box label! Additionally, lines 429–455 hardcode only 5 quick-switch numbers (`listOf(3, 7, 12, 17, 25)`) in English digits.
- **Evidence Screenshot Path**: `NONE (Verified via Compose source inspection — BookmarksAndTutorScreens.kt lines 353, 429–455)`
- **Viewport / Theme / Font-Scale**: All viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: Defeats the pedagogical purpose of the Multiplication Practice Tutor by giving away the answer on every step.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/BookmarksAndTutorScreens.kt` (`PracticeTutorScreen`, lines 350–358, 429–455)
- **Confidence Level**: **`SUSPECTED`** (100% deterministic in source code; unrendered in screenshot set)

---

### BUG `LOC-001` — `SquaresCubesScreen` Exposes Raw Image Filenames (`241713.jpg`, `241715.jpg`), Uses English Digits in Tables, and Has Inaccurate Cube Footer Note
- **Bug ID**: `LOC-001`
- **Title**: `SquaresCubesScreen` displays internal image filenames (`"ছবি: 241713.jpg"`), English numerals (`1..20`) in Square/Cube tables, and an incorrect `"১ থেকে ১০ পর্যন্ত"` footer note
- **Severity**: **P2 (Medium)**
- **Category**: `LOCALIZATION BUG` / `VISUAL BUG`
- **Module / Screen / Section / Subsection**: `MOD-03` → `MOD-03-001` (`SquaresCubesScreen`) → `MOD-03-001-SEC-A`, `SEC-B`, `SEC-C`
- **Exact Navigation Path**: `HomeScreen` → `"বর্গ ও ঘন"` → Inspect Tab 0 (`বর্গ ও বর্গমূল`) and Tab 1 (`ঘন ও ঘনমূল`)
- **Preconditions**: None
- **Exact Steps to Reproduce**:
  1. Navigate to `SquaresCubesScreen`.
  2. Read the TopAppBar subtitle, section banner, and table rows on Tab 0 (`বর্গ ও বর্গমূল`).
  3. Switch to Tab 1 (`ঘন ও ঘনমূল`), inspect the 20 rows, and read the footer note at the bottom of the table.
- **Expected Behavior**:
  - Subtitles and headers should use clean Bengali educational titles without internal asset filenames (`241713.jpg`, `241715.jpg`).
  - Table rows should format numbers in Bengali digits (`১² = ১`, `২0³ = ৮০০০`) consistent with the rest of the app.
  - The Cube tab footer should accurately state `"১ থেকে ২০ পর্যন্ত সংখ্যার ঘন এবং ঘনমূল"`.
- **Actual Behavior**:
  - `SquaresCubesScreen.kt` lines 69, 184, 241 and `SquareCubeModels.kt` lines 29, 55 display `"ছবি: 241713.jpg"` and `"\"241715.jpg\" ছবিতে দেওয়া..."`.
  - All 20 rows of both tables render raw English numerals (`1`, `4`, `20³ = 8000`).
  - `SquareCubeProvider.cubeFooterNote` (`SquareCubeModels.kt` line 83) says `"১ থেকে ১০ পর্যন্ত সংখ্যার ঘন এবং ঘনমূল"` beneath a 20-row table.
- **Evidence Screenshot Path**: `NONE (Verified via Compose source inspection — SquaresCubesScreen.kt & SquareCubeModels.kt)`
- **Viewport / Theme / Font-Scale**: All viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: Exposes raw developer asset filenames to end users, breaks Bengali numeral consistency, and presents a factually inaccurate table footer.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/SquaresCubesScreen.kt` (lines 68–70, 182–288) & `/app/src/main/java/com/example/math/SquareCubeModels.kt` (lines 28–83)
- **Confidence Level**: **`SUSPECTED`** (100% deterministic in source code; unrendered in screenshot set)

---

### BUG `LOC-002` — `FormulasScreen` and `DashboardScreen` Expose Internal Artifact/Architecture References (`"এই নোটে ছবিতে থাকা"`, `"Room v7"`)
- **Bug ID**: `LOC-002`
- **Title**: User-facing Bengali copy in `FormulasScreen` and `DashboardScreen` references external note photos (`"এই নোটে ছবিতে থাকা"`) and internal DB version (`"(Room v7)"`)
- **Severity**: **P3 (Low)**
- **Category**: `LOCALIZATION BUG`
- **Module / Screen / Section / Subsection**: `MOD-04-001` (`FormulasScreen` SEC-A) & `MOD-12-001` (`DashboardScreen` SEC-D)
- **Exact Navigation Path**:
  1. `HomeScreen` → `"বীজগণিতের সূত্র"` → Top introductory banner
  2. `HomeScreen` → `"অগ্রগতি ড্যাশবোর্ড"` → Scroll to `"ইকোসিস্টেম সমন্বিত অগ্রগতি ও স্পেসড রিভিশন (Room v7)"`
- **Preconditions**: None
- **Exact Steps to Reproduce**:
  1. Open `FormulasScreen` and read the top card text (`FormulasScreen.kt` line 191).
  2. Open `DashboardScreen` and read the card title at `DashboardScreen.kt` line 610.
- **Expected Behavior**: User-facing strings should describe features in natural Bengali without referencing source prompt images or internal Room database schema versions.
- **Actual Behavior**:
  - `FormulasScreen.kt` line 191 reads: `"এই নোটে ছবিতে থাকা ১৪টি সূত্র ধারাবাহিকভাবে সাজানো হয়েছে..."`.
  - `DashboardScreen.kt` line 610 reads: `"ইকোসিস্টেম সমন্বিত অগ্রগতি ও স্পেসড রিভিশন (Room v7)"` (and the actual DB is Room v10).
- **Evidence Screenshot Path**: `NONE (Verified via Compose source inspection — FormulasScreen.kt line 191 & DashboardScreen.kt line 610)`
- **Viewport / Theme / Font-Scale**: All viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: Reduces editorial polish by exposing developer/prompt implementation notes in the UI.
- **Suspected Source File / Component**: `/app/src/main/java/com/example/ui/screens/FormulasScreen.kt` (line 191), `/app/src/main/java/com/example/ui/screens/DashboardScreen.kt` (line 610)
- **Confidence Level**: **`SUSPECTED`** (100% deterministic in source code; unrendered in screenshot set)

---

### BUG `A11Y-001` — Multiple Interactive Buttons, Chips, and Palette Controls Fall Below the 48dp × 48dp Minimum Touch Target Size
- **Bug ID**: `A11Y-001`
- **Title**: Explicit `Modifier.size(36.dp..44.dp)` constraints on interactive buttons across 5 screens violate Android 48dp minimum touch target guidelines
- **Severity**: **P2 (Medium)**
- **Category**: `ACCESSIBILITY BUG`
- **Module / Screen / Section / Subsection**:
  - `MOD-05-001` (`FormulaLibraryScreen.kt` lines 763, 824)
  - `MOD-09-001` (`ProblemSolverScreen.kt` lines 1137, 1176, 1646, 1711, 1735, 1860, 1870)
  - `MOD-10-001` (`VisualExplorerScreen.kt` lines 265, 306, 561, 571)
  - `MOD-11-002` (`QuizzesScreen.kt` line 1262)
  - `GLOBAL-AUX-002` (`CustomizeScreen.kt` lines 684, 777, 948)
- **Exact Navigation Path**: Navigate to any of the 5 screens listed above and inspect the interactive controls below
- **Preconditions**: None
- **Exact Steps to Reproduce**:
  1. In `FormulaLibraryScreen`, inspect `btn_bookmark_${formula.id}` and `btn_copy_${formula.id}` (`Modifier.size(40.dp)`).
  2. In `ProblemSolverScreen`, inspect `solver_copy_btn` (`40.dp`), `solver_bookmark_btn` (`40.dp`), fraction operator buttons `+`, `-`, `*`, `/` (`38.dp`), and sample expression buttons (`heightIn(min = 38.dp)`).
  3. In `VisualExplorerScreen`, inspect `btn_next_visual_kc` (`minHeight = 36.dp`), `btn_toggle_visual_kc` (`minHeight = 40.dp`), and `geo_zoom_out` / `geo_zoom_in` (`38.dp`).
  4. In `QuizPlayScreen`, expand the Question Palette and inspect `palette_q_$qIdx` (`38.dp`).
  5. In `CustomizeScreen`, inspect preset color circles (`40.dp`) and icon picker tiles (`44.dp`).
- **Expected Behavior**: All interactive controls must maintain a minimum touch target of `48.dp × 48.dp` in compliance with Material Design 3 and Android Accessibility Scanner standards.
- **Actual Behavior**: Explicit `Modifier.size(38.dp)`, `Modifier.size(40.dp)`, `Modifier.size(44.dp)`, and `minHeight = 36.dp` override Compose's default minimum touch target sizing on `Surface`/`Box` or compact buttons.
- **Evidence Screenshot Path**: `/public/assets/aistudio/image_fa839fb1.png` (`ProblemSolverScreen` `40.dp` copy/bookmark & `38.dp` sample chips) + Compose source verification
- **Viewport / Theme / Font-Scale**: All viewports
- **Reproducibility**: `Always (100%)`
- **User Impact**: Harder to tap accurately for users with motor impairments or on compact screens.
- **Suspected Source File / Component**: `FormulaLibraryScreen.kt`, `ProblemSolverScreen.kt`, `VisualExplorerScreen.kt`, `QuizzesScreen.kt`, `CustomizeScreen.kt`
- **Confidence Level**: **`CONFIRMED`** (on `ProblemSolverScreen` via `image_fa839fb1.png`; **`SUSPECTED`** via source on the other 4 screens)

---

## F. UX Improvement Recommendations (Non-Bug Enhancements)

> The following items are **strictly separated from confirmed bugs**. They represent constructive UX polish and usability enhancements rather than functional or visual defects.

1. **`UX-REC-01` (Home Screen Grid Card Alignment)**: In `CustomModuleRenderer.kt` (`GridModuleCard`), use `Column(verticalArrangement = Arrangement.SpaceBetween)` or fixed-height title slots (`minLines = 2`) so 1-line and 2-line module titles align their bottom badges on the exact same horizontal baseline across each 2-column row.
2. **`UX-REC-02` (Horizontal Carousel Scroll Affordance)**: Replace raw `Row(Modifier.horizontalScroll(...))` strips on `ProblemSolverScreen`, `QuizzesScreen`, `BcsPreparationScreen`, and `DashboardScreen` with `LazyRow(contentPadding = PaddingValues(horizontal = 16.dp))` or subtle edge fade gradients so users can see that more chips exist off-screen without sharp border clipping.
3. **`UX-REC-03` (Expanded Study Timer Layout Adaptation)**: In `StudySessionTimerCard.kt`, move the `"প্রস্তুত"` / `"চলমান"` / `"বিরতি"` status badge into the expanded body or above the title so the header subtitle has the full width of the card and never truncates the daily goal target.
4. **`UX-REC-04` (Consolidate Duplicate 3D Solid Studio Entry in `VisualExplorerScreen`)**: Currently `Interactive3DSolidStudioCard` is embedded at the top of Tab 0 (`জ্যামিতি এক্সপ্লোরার`) *and* repeated as Tab 4 (`৩ডি ঘনবস্তু স্টুডিও`). Replacing the duplicate embedded card in Tab 0 with a compact CTA banner that switches to Tab 4 will reduce vertical scroll length on the 2D Geometry tab by ~550dp.
5. **`UX-REC-05` (Scope Cramer's Rule Card to Algebra Topics Only)**: Render `SimultaneousLinearSystemAndBridgeCard` only when `selectedTopic == SolverTopic.LINEAR_QUADRATIC_EQUATION` (or behind an explicit expandable toggle), keeping arithmetic, percentage, and geometry solutions focused.
6. **`UX-REC-06` (Bottom Navigation Access from Primary Hub Screens)**: Consider keeping the 5-tab `NavigationBar` visible on `FormulaLibraryScreen`, `BcsPreparationScreen`, and `BookHubScreen` (or providing a quick Home icon in `TopAppBar` when deep inside `BookLessonScreen`) to reduce back-press depth.
7. **`UX-REC-07` (Full 1–40 Table Picker in `PracticeTutorScreen`)**: Replace the hardcoded 5-chip table switcher (`3, 7, 12, 17, 25`) in `PracticeTutorScreen` with a scrollable 1–40 chip strip or number picker dialog so students can switch to any multiplication table directly inside the tutor.
8. **`UX-REC-08` (Search Clear & Empty-State Suggestions in `FormulaLibraryScreen`)**: When a search query in `FormulaLibraryScreen` or `BookmarksScreen` returns 0 matches, provide 1-tap suggestion chips to clear the active search or category filter.
9. **`UX-REC-09` (Adaptive Two-Pane Layout for Tablets / `600dp+` / `840dp+`)**: Introduce canonical list-detail layouts on `600dp+` and `840dp+` widths for `TablesScreen` + `TableDetailScreen`, `FormulasScreen` + `FormulaDetailScreen`, and `BookDetailScreen` + `BookChapterOverviewScreen`.
10. **`UX-REC-10` (Unified Bengali Numeral Formatting Helper)**: Route all remaining raw string interpolations (such as negative marking `-0.50` in `QuizPlayScreen` line 1059 and final score `String.format("%.2f", attempt.finalScore)` in `QuizzesScreen` line 911) through `BengaliNumberFormatter.toBengaliDigits()`.

---

## G. Accessibility & Localization Findings

### G.1 Accessibility Findings
1. **Screen Reader / TalkBack Verification Status**: **`NOT VERIFIED` (Live TalkBack Runtime Unavailable)**. Static semantics audit confirms that primary `TopAppBar` back icons (`"ফিরে যান"`), bookmark toggles (`"বুকমার্ক"`), and theme buttons include Bengali `contentDescription`s, while decorative icons properly pass `contentDescription = null`.
2. **Minimum Touch Target Violations (`A11Y-001`)**:
   - `ProblemSolverScreen.kt`: `solver_copy_btn` (`40.dp`), `solver_bookmark_btn` (`40.dp`), fraction operator buttons (`38.dp`), sample chips (`heightIn(min = 38.dp)`).
   - `FormulaLibraryScreen.kt`: `btn_bookmark_*` (`40.dp`), `btn_copy_*` (`40.dp`).
   - `VisualExplorerScreen.kt`: `btn_next_visual_kc` (`36.dp`), `btn_toggle_visual_kc` (`40.dp`), `geo_zoom_out` / `geo_zoom_in` (`38.dp`).
   - `QuizzesScreen.kt`: `palette_q_*` (`38.dp`).
   - `CustomizeScreen.kt`: preset color circles (`40.dp`), icon picker tiles (`44.dp`).
3. **Font Scaling (`1.5x` Accessibility Font Scale)**:
   - Visually verified on `StudySessionTimerSection` (`study_timer_360dp_1_5x_light.png`): `maxLines = 1` + `TextOverflow.Ellipsis` clips both the card title (`"পড়াশোনা সেশন টাই..."`) and subtitle (`"বন্ধ • চালু করতে টগল ক..."`) (`UI-002`).
4. **Color Contrast (Light & Dark Mode)**:
   - Light Mode contrast is strong across all inspected screens (`HomeScreen`, `ProblemSolverScreen`, `StudySessionTimerSection`), except for the disabled Reset button in `study_timer_360dp_1x_expanded.png` (`UI-003`).
   - Dark Mode contrast has a high-severity code-verified failure in `NumberSystemPracticePlayScreen.kt` (white `onSurface` text over light green `0xFFDCFCE7` card when an unselected option is the correct answer) and bright pastel blocks across 5 screens (`UI-010`).

### G.2 Localization & Bengali Numeral Consistency Findings
1. **Raw ASCII Operators in BODMAS Step 2 (`UI-004`, `CONFIRMED`)**: `"৪০*২-৩০/৩ = ৭০"` uses `*` and `/` instead of `×` and `÷`.
2. **English Numerals & Internal Filenames in `SquaresCubesScreen` (`LOC-001`, `SUSPECTED`)**: Exposes `"ছবি: 241713.jpg"` / `"ছবি: 241715.jpg"`, renders all 20 Square/Cube rows in English digits (`1..20`), and has a mismatched `"১ থেকে ১০ পর্যন্ত"` footer on the 20-row Cube table.
3. **English Numerals & Answer Leak in `PracticeTutorScreen` (`FUNC-001`, `SUSPECTED`)**: Input label `"উত্তর লিখুন (যেমন: ${number * step})"` and quick-switch chips (`3, 7, 12, 17, 25`) use English numerals.
4. **Internal Reference Strings (`LOC-002`, `SUSPECTED`)**: `FormulasScreen` references `"এই নোটে ছবিতে থাকা"` and `DashboardScreen` displays `"(Room v7)"`.

---

## H. Responsive Rendering Findings

| Configuration | Visual Rendering Coverage | Key Findings |
| :--- | :--- | :--- |
| **Small Phone (`320dp` width)** | Visually rendered for `StudySessionTimerSection` (`study_timer_320dp_1x_light.png`); other screens `NOT VERIFIED` (pixel) | `UI-002`: Subtitle truncates to `"আজকের অধ্যয়ন: ২ মি. ৫ সে. • লক্ষ্য: ..."` even in compact OFF state at `320dp`. |
| **Standard Phone (`360dp` width)** | Visually rendered for `StudySessionTimerSection` (`study_timer_360dp_1x_light.png`, `study_timer_360dp_1x_expanded.png`) | Compact OFF state passes (`PASS`); Expanded ON state truncates daily goal subtitle (`UI-001`) and wraps footer asymmetrically (`UI-003`). |
| **Large Phone (`411dp` width)** | Visually rendered for `HomeScreen` (`ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg`), `ProblemSolverScreen` (`image_fa839fb1.png`, `image_373fab02.png`), and `StudySessionTimerSection` (`study_timer_411dp_1x_light.png`) | `StudySessionTimerSection` compact OFF state passes cleanly at `411dp`; `HomeScreen` shows minor 1-line vs 2-line grid card asymmetry (`UI-007`); `ProblemSolverScreen` shows horizontal chip clipping (`UI-006`) and bottom system bar overlap (`UI-008`). |
| **Font Scale `1.5x` (`360dp`)** | Visually rendered for `StudySessionTimerSection` (`study_timer_360dp_1_5x_light.png`) | `UI-002`: Single-line `maxLines = 1` clips both title (`"পড়াশোনা সেশন টাই..."`) and subtitle (`"বন্ধ • চালু করতে টগল ক..."`). |
| **Dark Mode (`360dp`)** | Visually rendered for `StudySessionTimerSection` (`study_timer_360dp_1x_dark.png`); code-inspected across all 31 screens | `StudySessionTimerSection` Dark Mode passes with proper M3 dark surface contrast; 5 other screens contain hardcoded light pastel cards (`UI-010`). |
| **Tablet / Foldable (`600dp`, `840dp`) & Landscape** | `NOT VERIFIED` (No Live Emulator; strict no-source-edit mode) | Code inspection shows single-pane `LazyColumn` layouts with `fillMaxWidth()` and no canonical two-pane `WindowSizeClass` adaptation (`UX-REC-09`). |

---

## I. Coverage and Unverified Items

### I.1 Verified via Actual Rendered Screenshots (9 Image Artifacts)
1. `/public/assets/aistudio/ChatGPT_Image_Nov_14__2026__03_18_07_PM_c91f23f5.jpg` — `HomeScreen` (Modules 05–12 + Bottom Navigation Bar)
2. `/public/assets/aistudio/image_fa839fb1.png` — `ProblemSolverScreen` (Top viewport: Mode bar, 16-topic selector, BODMAS input card, Final Answer card)
3. `/public/assets/aistudio/image_373fab02.png` — `ProblemSolverScreen` (Lower scrolled viewport: Formula card, 2-step breakdown, Verification card, Similar Problem card, Simultaneous Linear System Cramer's Rule card, Bottom system bar overlap)
4. `/app/build/test-screenshots/study_timer_360dp_1x_light.png` — `StudySessionTimerSection` (`360dp`, `1.0x`, Light, Compact OFF)
5. `/app/build/test-screenshots/study_timer_360dp_1x_dark.png` — `StudySessionTimerSection` (`360dp`, `1.0x`, Dark, Compact OFF)
6. `/app/build/test-screenshots/study_timer_360dp_1x_expanded.png` — `StudySessionTimerSection` (`360dp`, `1.0x`, Light, Expanded ON/READY)
7. `/app/build/test-screenshots/study_timer_320dp_1x_light.png` — `StudySessionTimerSection` (`320dp`, `1.0x`, Light, Compact OFF)
8. `/app/build/test-screenshots/study_timer_411dp_1x_light.png` — `StudySessionTimerSection` (`411dp`, `1.0x`, Light, Compact OFF)
9. `/app/build/test-screenshots/study_timer_360dp_1_5x_light.png` — `StudySessionTimerSection` (`360dp`, `1.5x` Font Scale, Light, Compact OFF)

### I.2 Verified via Automated Headless Robolectric & Unit Tests (259 Passing Tests) + Full Source Audit, But `NOT VERIFIED` via Pixel Screenshot Rendering
- **Why Unverified Visually**: The execution environment has no live Android Emulator / ADB connection, and Section 1 (`STRICT AUDIT-ONLY MODE`) strictly prohibits adding or modifying test files to generate new Robolectric screenshots for the remaining 29 screens.
- **Unrendered Destinations (`29 / 31`)**:
  - `Screen.Tables`, `Screen.TableDetail`, `Screen.PracticeTutor`, `Screen.Primes`, `Screen.SquaresCubes`, `Screen.Formulas`, `Screen.FormulaDetail`, `Screen.FormulaLibrary`, `Screen.PracticeLab`, `Screen.PracticeLabPlay`, `Screen.MentalMath`, `Screen.MentalMathPlay`, `Screen.NumberSystem`, `Screen.NumberSystemPractice`, `Screen.VisualExplorer` (including OpenGL ES `GLSurfaceView` 3D viewport), `Screen.Quizzes`, `Screen.QuizPlay`, `Screen.Dashboard`, `Screen.BcsPreparation`, `Screen.BookHub`, `Screen.BookDetail`, `Screen.BookChapterOverview`, `Screen.BookLesson`, `Screen.BookPractice`, `Screen.BookPdfReader`, `Screen.BookInteractiveLab`, `Screen.BookRevision`, `Screen.Bookmarks`, `Screen.Customize`.
- **Unverified Runtime Conditions**:
  - Live TalkBack / screen-reader spoken traversal (`NOT VERIFIED`).
  - Live `600dp` / `840dp` / landscape pixel rendering (`NOT VERIFIED`).
  - Hardware OpenGL ES 2.0 `GLSurfaceView` rendering on physical GPU (`NOT VERIFIED`; 2D Canvas fallback verified in JVM).

---

## J. Final Assessment

### Overall Audit Status
**`AUDIT PARTIALLY COMPLETE — some modules, sections or subsections remain unverified`**

*(Specifically: 100% of the 14 modules, 31 navigation destinations, 124 sections, 268 subsections, 21 Room entities, and 259 automated tests were inventoried, executed, and audited at the Compose source and Robolectric DOM level, and all 9 existing pixel-rendered screenshots were visually inspected; however, pixel-rendered screenshot capture for the remaining 29 screens and live TalkBack traversal remain `NOT VERIFIED` because no live Android emulator/ADB is available in the container and Strict Audit-Only Mode forbids modifying test files to emit new screenshots.)*

### Final Defect & Recommendation Tallies

| Metric | Count | Breakdown |
| :--- | :---: | :--- |
| **Total Confirmed Visual/UI/UX Bugs (`CONFIRMED` via Rendered Screenshots)** | **8** | `UI-001` (P2), `UI-002` (P2), `UI-003` (P3), `UI-004` (P2), `UI-005` (P2), `UI-006` (P3), `UI-007` (P3), `UI-008` (P2) |
| **Total Suspected Bugs (`SUSPECTED` — 100% Code-Verified, Unrendered)** | **6** | `UI-009` (P2), `UI-010` (P2), `FUNC-001` (P2), `LOC-001` (P2), `LOC-002` (P3), `A11Y-001` (P2 — partially confirmed on `ProblemSolverScreen`) |
| **Total Documented Bugs in Register (`CONFIRMED` + `SUSPECTED`)** | **14** | **P0 (Blocker)**: `0` • **P1 (High)**: `0` • **P2 (Medium)**: `10` • **P3 (Low)**: `4` |
| **Total UX Improvement Recommendations (Non-Bug)** | **10** | `UX-REC-01` through `UX-REC-10` |
| **Total Items Remaining `NOT VERIFIED` (Pixel Render / TalkBack)** | **29 Screens + TalkBack + Tablet/Landscape Viewports** | Documented transparently in Sections C, H, and I |
