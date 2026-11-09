# MODULE 13 & 14 — ACCEPTANCE & TEST MATRIX

**Project**: Math Master (`com.example`)  
**Date**: 2026-11-07  
**Verification Standard**: Automated JVM & Robolectric Test Suite (`gradle :app:testDebugUnitTest`) + Full Build Compilation (`compile_applet`)

---

## 1. Module 13 (BCS Preparation Center) Acceptance Criteria

| ID | Requirement | Verification Method | Target Assertion & Status |
| :--- | :--- | :--- | :--- |
| **M13-01** | Official BPSC Profile Versions (All 6 Profiles) | `BcsPreparationCenterUnitTest` | All 6 profiles (`BCS_50TH_PRELIM` [20M+15M=35M], `BCS_WRITTEN_008_MATH` [50M], `BCS_WRITTEN_009_MENTAL` [50M], `BCS_WRITTEN_REGULAR` [50M+50M=100M], `BCS_51ST_SPECIAL` [10M+10M=20M Health Cadre], `BCS_LEGACY_PRELIM` [15M+15M=30M 35th–49th BCS]) resolve with exact verified marks, official URLs, and negative marking rules. (**PASS**) |
| **M13-02** | Separate Math & Mental Ability Syllabi + 9-Domain Mapping | `BcsPreparationCenterUnitTest` | Mathematical Reasoning covers all 5 official Preliminary units + 12 Written Code 008 topic areas organized across 9 `BcsMathDomain` pedagogical categories with explicit `officialSyllabusMappingBn`; Mental Ability maintains its distinct 6-category architecture + Abstract Reasoning support. (**PASS**) |
| **M13-03** | 5-Tier Question Provenance | `BcsPreparationCenterUnitTest` | Every historical and practice question has a valid `BcsProvenanceTier` / `BcsVerificationStatus`, non-empty explanation, verified answer index, and audit trail in `provenanceAuditLog` (33 verified past BCS questions + 30 syllabus/lesson practice questions). (**PASS**) |
| **M13-04** | 19-Part Bengali Guide & Shortcut Proofs | `BcsPreparationCenterUnitTest` | Every `BcsTopicGuideLesson` across all 11 master lessons (9 domains, `11 × 19 = 209` parts) provides non-blank standard method, smart shortcut, mathematical proof (`shortcutProofAndWhyItWorksBn`), Written Code 008 rubric steps, and mini-check. (**PASS**) |
| **M13-05** | Profile-Isolated Persistence & Switching | `BcsPreparationCenterUnitTest` & `EcosystemUpgradeV7UnitTest` | Recording progress, mistakes, or study goals under `BCS_50TH_PRELIM` and switching to `BCS_WRITTEN_008_MATH`, `BCS_WRITTEN_009_MENTAL`, or `BCS_51ST_SPECIAL` preserves separate progress records via `BcsTopicProgressEntity.buildScopedKey` without overwriting. (**PASS**) |
| **M13-06** | Deterministic Weak-Topic Intelligence | `BcsPreparationCenterUnitTest` | `BcsWeakTopicIntelligenceEngine` accurately flags low-accuracy, high-mistake, or slow-solve topics from persisted student records per active profile. (**PASS**) |

---

## 2. Module 14 (BOOK / Digital Mathematics Academy) Acceptance Criteria

| ID | Requirement | Verification Method | Target Assertion & Status |
| :--- | :--- | :--- | :--- |
| **M14-01** | 4 Textbooks & 47 Chapters Mapped | `EcosystemUpgradeV7UnitTest` & `BcsPreparationCenterUnitTest` | `BookId.entries.size == 4`; `BookCurriculumRepository.getAllChapters().size == 47` (`8 + 11 + 11 + 17`); total PDF pages `== 894` (`161 + 181 + 190 + 362`). (**PASS**) |
| **M14-02** | Page Offset & Edition Metadata | `BcsPreparationCenterUnitTest` | `printedToPdfPage(p) == p + 5` for all 4 books; `BookEditionMetadata` is non-blank and verified for every `BookId`. (**PASS**) |
| **M14-03** | Packaged Full-Color NCTB PDFs & Native Companion | `BcsPreparationCenterUnitTest` | `app/src/main/assets/books/*.pdf` contains genuine optimized full-color NCTB PDFs (`5,372,426` + `6,391,697` + `6,459,157` + `12,969,807` = `31,193,087` bytes, 894 pages); `resolveOfflinePdfFile` unpacks them offline and `getStructuredPageContent` provides native Bengali companion content for every page. (**PASS**) |
| **M14-04** | 10 Question Formats & 9 Practice Modes | `BcsPreparationCenterUnitTest` | `BookQuestionFormat.entries.size == 10`; `BookPracticeMode.entries.size == 9` (including `WEAK_TOPIC`, `FORMULA_PRACTICE`, `REVISION_PRACTICE`); Bengali and English digits, negatives, decimals, and large numbers evaluate accurately. (**PASS**) |
| **M14-05** | 12 Interactive Labs & Mathematical Accuracy | `EcosystemUpgradeV7UnitTest` | All 12 `BookInteractiveLabType` entries produce distinct, mathematically accurate Bengali explanation lines (`computeLabExplanationLines`) including Prime Factorization/Divisibility and Circle Theorems. (**PASS**) |
| **M14-06** | Offline Search with Numeral Normalization | `EcosystemUpgradeV7UnitTest` | `BookCurriculumRepository.searchAllBooks` matches both Bengali (`"অধ্যায় ১৩"`, `"১৬"`) and English (`"chapter 13"`, `"16"`) queries across chapters, lessons, formulas, examples, theorems, and questions. (**PASS**) |

---

## 3. Global, Database Migration & Protected-Module Regression Criteria

| ID | Requirement | Verification Method | Target Assertion & Status |
| :--- | :--- | :--- | :--- |
| **GLB-01** | Protected Modules 01–12 Unchanged | `EcosystemUpgradeV7UnitTest` & `HomeNavigationAndOrderingUnitTest` | All 14 Home Grid positions (`01 TABLES` through `14 BOOK`), routes, and Modules 01–12 models/calculators pass 100% of existing regression tests. (**PASS**) |
| **GLB-02** | Non-Destructive Room v7 → v8 Migration | `EcosystemUpgradeV7UnitTest` & `BcsPreparationCenterUnitTest` | Pre-populated v7 database migrates cleanly via `MIGRATION_7_8` to v8 with zero data loss across all 19 v7 tables and full functionality on new v8 columns/tables (`20` tables total). (**PASS**) |
| **GLB-03** | Cross-Module Curriculum Bridge | `BcsPreparationCenterUnitTest` | `CrossModuleCurriculumBridge` resolves valid NCTB chapters for every BCS unit/lesson and valid BCS enrichment links for all 47 NCTB chapters with explicit authority badges. (**PASS**) |
| **GLB-04** | 100% Offline Operation | Code & Manifest Audit | Zero cloud AI, Firebase, or network dependencies added; all assets, questions, guides, and labs operate locally offline. (**PASS**) |
