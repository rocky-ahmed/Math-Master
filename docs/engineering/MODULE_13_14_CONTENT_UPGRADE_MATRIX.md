# MODULE 13 & 14 — CONTENT UPGRADE MATRIX

**Project**: Math Master (`com.example`)  
**Date**: 2026-11-07  
**Scope**: Complete Curriculum, Guide, Practice, Lab, and Cross-Module Mapping Matrix for Module 13 (BCS Preparation Center) and Module 14 (BOOK / Digital Mathematics Academy)

---

## 1. Module 13 — BCS Mathematical Reasoning & Mental Ability Content Matrix

### 1.1 Mathematical Reasoning — 9 Application Pedagogical Domains Mapped to Official BPSC Syllabus (11 Master Lessons × 19 Parts)

> **Official Syllabus vs. Application Pedagogical Grouping**: The official 50th BCS Preliminary Syllabus defines **5 official units (`BcsSyllabusUnit.UNIT_1_ARITHMETIC` .. `UNIT_5_SETS_COMBINATORICS_PROBABILITY`, 4 marks each = 20 marks)**, and the official BPSC Written Syllabus (Subject Code 008, 50 marks) defines **12 topic areas**. Module 13 organizes these into **9 application-structured pedagogical domains (`BcsMathDomain`)** with **11 comprehensive 19-part master lessons (`BcsTopicGuideLesson`)**, where every domain and lesson explicitly records its official BPSC Preliminary Unit and Written Code 008 mapping (`officialSyllabusMappingBn`).

| Domain # | `BcsMathDomain` Enum | Official BPSC Syllabus Mapping (`officialSyllabusMappingBn`) | Master Lesson Count | Master Lesson ID(s) & Bengali Titles | Definitions | Rules | Formulas | Worked Examples | Linked NCTB Book & Chapters |
| :---: | :--- | :--- | :---: | :--- | :---: | :---: | :---: | :---: | :--- |
| **1** | `ARITHMETIC` | Prelim Unit 1 (4M) + Written Code 008 Topics 1–2 | **3** | 1. `bcs_math_real_numbers` (বাস্তব সংখ্যা ও বিভাজ্যতা)<br>2. `bcs_math_percentage_ratio` (শতকরা, অনুপাত ও লাভ-ক্ষতি)<br>3. `bcs_math_unitary_work_speed` (ঐকিক নিয়ম: সময়-কাজ, নল-চৌবাচ্চা, নৌকা ও ট্রেন) | 9 | 8 | 8 | 4 | Class 6 Ch 1–2, Class 7 Ch 1–2, Class 8 Ch 2, Class 9–10 Ch 1, 11 |
| **2** | `ALGEBRA` | Prelim Unit 2 (4M) + Written Code 008 Topics 3–5 | **1** | 4. `bcs_math_algebra_formulas` (বর্গ, ঘন, উৎপাদক ও দ্বিঘাত সমীকরণ) | 3 | 3 | 3 | 1 | Class 6 Ch 4–5, Class 7 Ch 4–7, Class 8 Ch 4–6, Class 9–10 Ch 3, 5, 12 |
| **3** | `SEQUENCES_SERIES` | Prelim Unit 3 (4M) + Written Code 008 Topic 6 | **1** | 5. `bcs_math_log_series` (সূচক, লগারিদমের ধর্ম ও সমান্তর-গুণোত্তর ধারা) | 3 | 4 | 4 | 1 | Class 8 Ch 1, Class 9–10 Ch 4, 13 |
| **4** | `GEOMETRY` | Prelim Unit 4 (Geometry) + Written Code 008 Topic 7 | **1** | 6. `bcs_math_geometry_mensuration` (ত্রিভুজ, চতুর্ভুজ, বৃত্ত ও পিথাগোরাসের উপপাদ্য) | 3 | 3 | 3 | 1 | Class 6 Ch 6–7, Class 7 Ch 8–10, Class 8 Ch 8–10, Class 9–10 Ch 6–8, 14–15 |
| **5** | `MENSURATION` | Prelim Unit 4 (Mensuration) + Written Code 008 Topic 8 | **1** | 7. `bcs_math_mensuration_3d` (পরিমিতি: ক্ষেত্রফল, ঘনবস্তু, সিলিন্ডার, কোণক ও গোলক) | 3 | 2 | 3 | 1 | Class 7 Ch 3, Class 8 Ch 3, Class 9–10 Ch 16 |
| **6** | `CARTESIAN_GEOMETRY` | Written Code 008 Topic 9 (+ Prelim Unit 4 Enrichment) | **1** | 8. `bcs_math_cartesian_geometry` (স্থানাঙ্ক জ্যামিতি: দূরত্ব, ঢাল ও সরলরেখার সমীকরণ) | 3 | 3 | 4 | 1 | Class 7 Ch 7, Class 8 Ch 6, Class 9–10 Ch 12 |
| **7** | `TRIGONOMETRY` | Written Code 008 Topic 10 (+ Prelim Unit 4 Enrichment) | **1** | 9. `bcs_math_trigonometry_distance` (ত্রিকোণমিতিক অনুপাত, অভেদাবলি এবং দূরত্ব ও উচ্চতা) | 3 | 3 | 3 | 1 | Class 9–10 Ch 9–10 |
| **8** | `SETS_COUNTING_PROBABILITY` | Prelim Unit 5 (Sets, Counting & Prob.) + Written Code 008 Topics 11–12 | **1** | 10. `bcs_math_sets_prob` (সেট ও ভেনচিত্র, বিন্যাস ⁿPᵣ, সমাবেশ ⁿCᵣ ও সম্ভাব্যতা) | 2 | 2 | 3 | 1 | Class 8 Ch 7, Class 9–10 Ch 2 |
| **9** | `STATISTICS` | Prelim Unit 5 (Statistics) + NCTB Class 6–10 Data Foundation | **1** | 11. `bcs_math_statistics_data` (পরিসংখ্যান: গড়, মধ্যক, প্রচুরক ও পরিমিত ব্যবধান) | 3 | 2 | 3 | 1 | Class 6 Ch 8, Class 7 Ch 11, Class 8 Ch 11, Class 9–10 Ch 17 |
| **Total** | **9 Domains** | **5 Prelim Units + 12 Written Code 008 Topics** | **11 Lessons (209 Parts)** | **11 × 19-Part Master Lessons** | **32** | **30** | **34** | **12** (+11 Mini-Checks) | **14 Cross-Module Bridge Entries** |

Each of the 11 `BcsTopicGuideLesson` instances provides all **19 pedagogical components** (`11 × 19 = 209` structured lesson parts):
1. Topic Introduction (`conceptIntroBn`), 2. Learning Objectives (`learningObjectivesBn`), 3. Prerequisite Knowledge (`prerequisiteKnowledgeBn`), 4. Simple Conceptual Explanation (`simpleConceptExplanationBn`), 5. Detailed Bengali Teaching (`detailedExplanationBn`), 6. Definitions (`importantDefinitionsBn`), 7. Mathematical Rules (`rulesAndPropertiesBn`), 8. Formula Explanations (`formulaExplanationsBn`), 9. Worked Examples (`workedExamplesBn`), 10. Standard Solution Method (`standardMethodBn`), 11. Smart Shortcut (`smartShortcutMethodBn`), 12. Proof/Reasoning Behind Shortcut (`shortcutProofAndWhyItWorksBn`), 13. Step-by-Step Written Code 008 Rubric (`writtenCode008RubricStepsBn`), 14. Common Examination Traps (`commonExamTrapsBn`), 15. Common Mistakes Warning (`commonMistakesWarningBn`), 16. Fast Calculation Techniques (`fastCalculationTechniquesBn`), 17. Practical Application (`practicalApplicationBn`), 18. Quick Revision Summary (`quickRevisionSummaryBn`), 19. Mini Knowledge Check (`miniCheckQuestionBn`, `miniCheckOptionsBn`, `miniCheckCorrectIndex`, `miniCheckExplanationBn`).

---

### 1.2 Mental Ability — Separate 6-Category + Written Code 009 Abstract Reasoning Track

| Category # | Category ID | Bengali Title | Official Profile Coverage | Concept Guide (`BcsMentalAbilityCategoryGuide`) | Curated Questions (`mentalAbilityQuestions`) |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **1** | `VERBAL_REASONING` | ভাষাগত যৌক্তিক বিচার | 50th Prelim, Written 009, 51st Special, Legacy | Analogy, Syllogism, Logical Deduction, Blood Relations | 1 (`45th BCS`) |
| **2** | `PROBLEM_SOLVING` | সমস্যা সমাধান ও বিশ্লেষণ | 50th Prelim, Written 009, 51st Special, Legacy | Clock Angle (`|60H - 11M|/2`), Calendar Day-of-Week, Seating/Ranking | 2 (`37th BCS`, `40th BCS`) |
| **3** | `SPELLING_LANGUAGE` | বানান ও ভাষা দক্ষতা | 50th Prelim, Written 009, 51st Special, Legacy | Bengali & English Orthography, Dictionary Order, Word Formation | 1 (`38th BCS`) |
| **4** | `MECHANICAL_REASONING` | যান্ত্রিক দক্ষতা ও বলবিজ্ঞান | 50th Prelim, Written 009, 51st Special, Legacy | Gear Rotation (`GEAR_ROTATION`), Fixed/Movable Pulleys (`PULLEY_SYSTEM`), Lever Balance | 2 (`36th BCS`, `41st BCS`) |
| **5** | `SPACE_RELATION` | স্থানাঙ্ক ও দিক নির্ণয় (+ বিমূর্ত যুক্তি / Abstract Reasoning) | 50th Prelim, Written 009, 51st Special, Legacy | Compass Path (`COMPASS_DIRECTION`), Figure Counting (`TRIANGLE_COUNT`), Mirror Reflection (`MIRROR_REFLECTION`), Written 009 Abstract Rotation | 4 (`43rd`, `44th`, `46th BCS`, + `Written 009`) |
| **6** | `NUMERICAL_ABILITY` | সংখ্যাগত ক্ষমতা ও প্যাটার্ন | 50th Prelim, Written 009, 51st Special, Legacy | Number Matrix (`NUMBER_MATRIX`), Missing Number Series, Coding-Decoding | 2 (`39th BCS`, `42nd BCS`) |
| **Total** | **6 Categories** | **মানসিক দক্ষতা (Prelim 15M / Written 009 50M / 51st Special 10M)** | **All 6 Profiles** | **6 Concept Guides (with Written 009 Notes)** | **12 Questions (11 Verified Past BCS + 1 Written 009 Model)** |

---

### 1.3 Module 13 Question Bank Summary

| Question Bank Segment | Total Questions | Verified Historical BCS (`OFFICIAL_PREVIOUS_BCS` / `VERIFIED_OFFICIAL_PAPER`) | Syllabus / Concept Practice (`SYLLABUS_PRACTICE` / `CONCEPT_DERIVED_PRACTICE`) | Excluded Unverified Audit Entries |
| :--- | :---: | :---: | :---: | :---: |
| **Mathematical Reasoning (`BcsPreparationRepository`)** | **40 Active** | **22** (Unit 1: 6, Unit 2: 4, Unit 3: 4, Unit 4: 4, Unit 5: 4; spanning 10th–45th BCS) | **18** (Unit 1: 4, Unit 2: 5, Unit 3: 3, Unit 4: 3, Unit 5: 3) | 2 (Quarantined in `excludedUnverifiedQuestions`, never served in Previous BCS mode) |
| **Mental Ability (`BcsCurriculumAndGuideRepository`)** | **12 Active** | **11** (spanning 36th–46th BCS) | **1** (Written Code 009 Abstract Reasoning) | 0 |
| **Lesson Mini-Check Questions (`mathGuideLessons`)** | **11 Active** | — | **11** (1 per 19-part master lesson) | 0 |
| **Combined Module 13 Curated Bank** | **63 Active** | **33 Verified Historical BCS Questions** | **30 Syllabus & Lesson Practice Questions** | **20 Provenance Audit Log Entries** |

---

## 2. Module 14 — BOOK Content, Practice Modes & Interactive Labs Upgrade Matrix

### 2.1 Upgraded Practice Modes (9 Total Modes with Full Backward Compatibility)

| Mode ID | Bengali Title | Backward Compatibility with Previous Modes | Question Selection & Ordering Rule |
| :--- | :--- | :--- | :--- |
| `BASIC` | মৌলিক অনুশীলন (Basic) | Preserved (`BookPracticeMode.BASIC`) | Prioritizes `BASIC` difficulty items, definitions, fundamental rules, and direct formula recall. |
| `STANDARD` | আদর্শ অনুশীলন (Standard) | Preserved (`BookPracticeMode.STANDARD`) | Prioritizes `STANDARD` difficulty items, worked examples, and core subsection problems. |
| `ADVANCED` | উন্নত অনুশীলন (Advanced) | Preserved (`BookPracticeMode.ADVANCED`) | Prioritizes `ADVANCED` difficulty items, multi-step proofs, and analytical problems. |
| `CHAPTER_EXERCISE` | অনুশীলনীর প্রশ্ন (Original Textbook Exercise) | Preserved (`BookPracticeMode.CHAPTER_EXERCISE`) | Prioritizes `ORIGINAL_TEXTBOOK` provenance items with printed page citations (`170` total across 47 chapters). |
| `CHAPTER_TEST` | অধ্যায়ভিত্তিক পরীক্ষা (Chapter Test) | Preserved (`BookPracticeMode.CHAPTER_TEST`) | Comprehensive chapter evaluation across all 10 question formats (`499` total across 47 chapters). |
| `MIXED_PRACTICE` | মিশ্র অনুশীলন (Mixed Practice) | Preserved (`BookPracticeMode.MIXED_PRACTICE`) | Interleaved practice combining questions across chapters of the selected textbook (`12` questions per session). |
| `WEAK_TOPIC` | দুর্বল পাঠ অনুশীলন (Weak Topic Practice) | **New in v8** (`fromId` safely defaults unknown legacy IDs to `STANDARD`) | Targets `CONCEPT_DIAGNOSIS`, `STEP_BY_STEP_SOLVING`, and `TRUE_FALSE` misconception items. |
| `FORMULA_PRACTICE` | সূত্র অনুশীলন (Formula Practice) | **New in v8** | Targets `FORMULA_IDENTIFICATION`, `MATCHING_PAIRS`, and `FILL_IN_THE_BLANK` formula items. |
| `REVISION_PRACTICE` | পুনরালোচনা অনুশীলন (Revision Practice) | **New in v8** | Prioritizes `ORIGINAL_TEXTBOOK` and `CONCEPT_DERIVED` items for rapid pre-exam chapter revision. |

### 2.2 Interactive Labs Audit & Alias Resolution (12 Distinct Mathematical Engines)

| Lab # | Lab Enum | Companion Alias Resolution | Mathematical Engine & Interactive Controls |
| :---: | :--- | :--- | :--- |
| 1 | `NUMBER_SYSTEM_EXPLORER` | `NUMBER_LINE_LAB`, `PRIME_FACTORIZATION_SIEVE` | Number line addition/subtraction, absolute distance, prime factorization, Sieve of Eratosthenes, Euclidean GCD/LCM. |
| 2 | `FRACTION_AND_RATIO_LAB` | `PRIME_FACTOR_SIEVE_LAB`, `RATIO_PROPORTION_SCALER` | Equivalent fractions, ratio comparison, componendo-dividendo `(a+b)/(a-b)`, and prime factor divisor count. |
| 3 | `PROFIT_LOSS_PERCENTAGE_CALC` | `PROFIT_INTEREST_LAB` | Cost/Selling price, profit/loss %, VAT, simple interest `I = Prn`, compound interest `C = P(1+r)^n`, difference `C - A`. |
| 4 | `PATTERN_AND_SEQUENCE_LAB` | `SERIES_PROGRESSION_LAB` | Odd number sum `n²`, magic squares (3×3, 4×4), Fibonacci, AP `a+(n-1)d`, GP `ar^(n-1)` and partial sums. |
| 5 | `ALGEBRA_INTERACTIVE_LAB` | `ALGEBRA_IDENTITY_LAB` | Geometric area proof of `(a+b)²`, `a²-b²`, `(a+b)³`, reciprocal identities `x²+1/x²`, and exponents/logarithms. |
| 6 | `EQUATION_SOLVER_VISUALIZER` | `EQUATION_BALANCER_LAB`, `COORDINATE_GRAPH_LAB`, `COORDINATE_PLANE_EXPLORER` | Linear balance scale `ax+b=c`, quadratic roots `ax²+bx+c=0`, and 2×2 simultaneous line intersection `(x,y)`. |
| 7 | `GEOMETRY_INTERACTIVE_LAB` | `GEOMETRY_CANVAS_LAB`, `CIRCLE_CHORD_TANGENT_THEOREM` | Complementary/supplementary angles, parallel line transversal angles, triangle congruence/similarity, and Circle Theorem (`central = 2 × inscribed`). |
| 8 | `PYTHAGORAS_THEOREM_VISUALIZER` | `CIRCLE_THEOREM_LAB` | Visual squares on `a, b, c` (`a² + b² = c²`), Pythagorean triplets, and circle chord distance `l = 2√(r² - d²)`. |
| 9 | `TRIGONOMETRY_HEIGHT_DISTANCE_LAB` | `TRIG_UNIT_CIRCLE_LAB`, `TRIGONOMETRY_UNIT_CIRCLE` | Unit circle `sin θ, cos θ, tan θ`, standard angles `0°..90°`, and elevation/depression tower height `h = d tan θ`. |
| 10 | `MENSURATION_CALCULATOR` | `MENSURATION_3D_LAB`, `MENSURATION_3D_SURFACE_VOLUME` | Plane figures (rectangle, triangle, rhombus, trapezium, circle sector) and 3D solids (cuboid, cylinder, cone, sphere). |
| 11 | `DATA_AND_STATISTICS_LAB` | `STATISTICS_HISTOGRAM_LAB` | Frequency distribution range/classes, Arithmetic Mean, Median, Mode, Histogram, Pie Chart central angles, and Ogive curve. |
| 12 | `SET_AND_FUNCTION_VISUALIZER` | `SET_VENN_LAB` | Interactive 2-set Venn diagram (`A∪B`, `A∩B`, `A\B`, `A'`), power set `2^n`, and domain/range mapping. |
