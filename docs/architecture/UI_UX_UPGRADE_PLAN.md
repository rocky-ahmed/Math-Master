# UI/UX UPGRADE PLAN (MATERIAL 3 • BENGALI TYPOGRAPHY • CROSS-MODULE BRIDGES)
**Project**: Math Master (`com.example`)  
**Protected Scope**: Modules 01–04 (`TABLES`, `PRIMES`, `SQUARES`, `FORMULAS`) — 0 UI/UX Changes

---

## 1. Home Screen Grid Preservation Contract

- The 14-card `LazyVerticalGrid` in `HomeScreen.kt` (`HomeScreenDefaults.createHomeModules()`) preserves:
  - Identical 14-item ordering (`0..3` Protected Modules 01–04; `4..13` Eligible Modules 05–14).
  - Identical card dimensions, icons, gradients, titles, subtitles, and badges matching Screenshots 1–3.
  - Full compatibility with `HomeNavigationAndOrderingUnitTest`.

---

## 2. Shared Pedagogical UI Components (Modules 05–14)

1. **Curriculum Track Filter Bar (`CurriculumTrackFilterRow`)**:
   - Horizontal scrollable Material 3 `FilterChip` bar (`সকল ট্র্যাক`, `NCTB পাঠ্যক্রম`, `BdMO অলিম্পিয়াড`, `বিসিএস প্রস্তুতি`) in Module 05 (`FormulaLibraryScreen`), Module 06 (`PracticeLabScreen`), and Module 08 (`NumberSystemScreen`).
   - Touch target height ≥ `48.dp` with explicit `Modifier.testTag("track_chip_${track.id}")`.
2. **Provenance & Mastery Badges**:
   - Compact pill chips on formula cards and practice problems indicating curriculum alignment (`NCTB`, `BdMO`, `BCS`) and mastery status (`আয়ত্তে এসেছে` / `অনুশীলন প্রয়োজন`).
3. **Diagnostic Error Coaching Card**:
   - Displayed in Practice Lab (`PracticeLabPlayScreen`) and Mental Math (`MentalMathPlayScreen`) explaining *why* a mistake occurred and showing the fastest mental or algebraic strategy.
4. **1-Tap Cross-Module Learning Bridges**:
   - **Formula Library → Practice Lab & Problem Solver**
   - **Problem Solver → Formula Library & Visual Explorer**
   - **BCS Syllabus Unit → NCTB Book Chapter Foundation**
   - **NCTB Book Chapter Overview → Problem Solver & BCS Unit**
   - **Dashboard → Unified Mistake Notebook & Spaced Revision Queue**
