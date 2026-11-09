# MODULE 13 & 14 — BACKEND & UI/UX ARCHITECTURE PLAN

**Project**: Math Master (`com.example`)  
**Date**: 2026-11-07  
**Database Upgrade**: Room SQLite v7 → v8 (Non-Destructive Incremental Migration)

---

## 1. Backend Architecture & Data Layer Design

### 1.1 Room v7 → v8 Non-Destructive Schema Evolution (`MIGRATION_7_8`)

To ensure **profile-isolated BCS preparation data** and **enhanced Book Reader persistence** without losing any existing user records, `MathDatabase` upgrades from version 7 to version 8 via `MIGRATION_7_8`:

1. **`bcs_user_profile` Table Upgrade**:
   - Retains `profileId` as `@PrimaryKey` (supporting both `"active_profile"` for the global active pointer and per-profile rows keyed by `BcsExamProfile.id`).
   - Adds columns via `ALTER TABLE`:
     - `preparationLevel TEXT NOT NULL DEFAULT 'BEGINNER'`
     - `weeklyTargetTopics INTEGER NOT NULL DEFAULT 3`
     - `weeklyTargetMocks INTEGER NOT NULL DEFAULT 1`
     - `targetAccuracyPercent INTEGER NOT NULL DEFAULT 75`

2. **`bcs_topic_progress` Table Upgrade (Profile-Scoped Isolation)**:
   - Adds column `examProfileId TEXT NOT NULL DEFAULT 'BCS_50TH_PRELIM_2025'` and index `index_bcs_topic_progress_examProfileId`.
   - Uses profile-scoped composite key formatting (`"${examProfileId}:${topicId}"` or backward-compatible raw `topicId` for default profile lookups) so each BCS profile maintains independent guide completion, mini-check status, attempted/correct counts, and mastery percentages.

3. **`bcs_mistake_notebook` & `exam_attempts` Profile Attribution**:
   - Adds `examProfileId TEXT NOT NULL DEFAULT 'BCS_50TH_PRELIM_2025'` to `bcs_mistake_notebook` with index `index_bcs_mistake_notebook_examProfileId`.
   - Adds `examProfileId TEXT NOT NULL DEFAULT ''` to `exam_attempts` with index `index_exam_attempts_examProfileId`.

4. **`bcs_study_goals` New Table (Room v8)**:
   - Stores daily and weekly study planner execution per `examProfileId` and `dateKey`:
     - `goalKey TEXT NOT NULL PRIMARY KEY` (`"${examProfileId}_${dateKey}"`)
     - `examProfileId TEXT NOT NULL`
     - `dateKey TEXT NOT NULL`
     - `lessonsCompleted INTEGER NOT NULL`
     - `questionsSolved INTEGER NOT NULL`
     - `revisionsCompleted INTEGER NOT NULL`
     - `studyTimeMinutes INTEGER NOT NULL`
     - `mockExamsCompleted INTEGER NOT NULL`
     - `updatedAt INTEGER NOT NULL`

5. **`book_reading_state` Table Upgrade**:
   - Adds columns via `ALTER TABLE`:
     - `preferredViewMode TEXT NOT NULL DEFAULT 'STRUCTURED'`
     - `lastZoomScale REAL NOT NULL DEFAULT 1.0`
     - `activeEditionId TEXT NOT NULL DEFAULT 'NCTB_2026_STANDARD'`

---

## 2. Domain Services & Engines

### 2.1 `CrossModuleCurriculumBridge` (Module 13 ↔ Module 14 Integration)
Provides deterministic, bidirectional mappings between:
- **BCS Syllabus Units & Topics (`Module 13`)** ↔ **NCTB Textbooks & Chapters (`Module 14`)**
- **NCTB Chapters (`Module 14`)** ↔ **Formula Library IDs (`Module 05`)**, **Problem Solver Topics (`Module 09`)**, and **Interactive Book Labs (`Module 14`)**
- Explicitly tags every mapping with a `CurriculumAuthorityBadge`:
  - `NCTB_FOUNDATION` (`এনসিটিবি পাঠ্যবই ভিত্তি`)
  - `BPSC_OFFICIAL_SYLLABUS` (`বিপিএসসি অফিশিয়াল সিলেবাস`)
  - `BCS_COMPETITIVE_PRACTICE` (`বিসিএস প্রতিযোগিতামূলক অনুশীলন`)
  - `SUPPLEMENTARY_ENRICHMENT` (`অতিরিক্ত গাণিতিক সমৃদ্ধকরণ`)

### 2.2 `BcsWeakTopicIntelligenceEngine`
Evaluates learner records deterministically across four signals:
1. **Low Accuracy**: Topic accuracy `< 60%` across `>= 3` attempted questions.
2. **Repeated Mistakes**: Unresolved mistakes in `bcs_mistake_notebook` with `mistakeCount >= 2`.
3. **Excessive Solving Time**: Average time per question `> 75` seconds.
4. **Failed Revision / Mini-Check**: Topic where `isGuideCompleted == true` but `miniCheckPassed == false`.

---

## 3. UI/UX Screen Hierarchy & Progressive Disclosure

### 3.1 Module 13 — `BcsPreparationScreen`
- **Top Bar**: Profile Switcher Badge (`50th Prelim`, `Written 008`, `Written 009`, `Written Combined`, `51st Special`, `Legacy Prelim`) + Profile/Goal Settings button.
- **5-Tab Internal Workspace**:
  1. **ড্যাশবোর্ড ও স্টাডি প্ল্যানার (Dashboard & Study Planner)**: Active profile summary, Official vs. Personal Exam Countdown, Daily & Weekly Goals progress bars, 8-Stage Preparation Journey stepper, Weak-Topic Intelligence alerts, and Next Recommended Action card.
  2. **গাণিতিক যুক্তি গাইড (Mathematical Reasoning — 9 Domains)**: Filter by Official Syllabus Unit vs. All 9 Domains; opens 19-part Bengali Guide modal with expandable Shortcut Proof, Written Code 008 Step-by-Step Rubric, Mini Knowledge Check, and 1-tap NCTB Chapter & Interactive Lab bridges.
  3. **মানসিক দক্ষতা ট্র্যাক (Mental Ability Separate Track)**: 6 Official BPSC Categories + Abstract Reasoning filter chips, Category Concept Guide cards, interactive Canvas Diagram problems, and timed Mental Ability practice launch.
  4. **বিগত বিসিএস প্রশ্নব্যাংক (Verified Previous Question Bank)**: Filter by 5 Provenance Tiers, BCS Exam Number, or Unit; displays verbatim question, interactive option check, standard vs. shortcut solution, and provenance evidence drawer.
  5. **প্র্যাকটিস, মক টেস্ট ও রিভিশন (Practice, Mock Exam & Mistake Notebook)**: 14 Practice/Mock modes (Topic, Basic, Standard, Advanced, Speed, Daily Challenge, Weak Topic, Previous BCS, Written 008 Descriptive Practice, Subject Mock, Combined Math+Mental Mock), negative-marking selector, and Profile-Scoped Mistake Notebook.

### 3.2 Module 14 — `BookHubScreen`, `BookDetailScreen`, `BookChapterOverviewScreen`, `BookPdfReaderScreen`, `BookInteractiveLabScreen`
- **Book Hub**: Offline search bar with Bengali/English numeral normalization, 7 filter chips, Textbook Edition & Fidelity Verification banner, and 4 color-coded NCTB Book cards with chapter/lesson completion progress.
- **Chapter Overview**: 16-part Chapter Guide, Subsection list, Theorems & Constructions proof cards, Formulas & Worked Examples, Cross-Module BCS/Solver/Formula Bridge card, and 9-Mode Practice launcher.
- **Dual-Mode Offline Reader**: Instant switch between **Rich Native Structured Page View** (showing page-accurate subsections, formulas, theorem proofs, worked examples, exercises, and answer keys) and **Offline PDF Canvas View** (`PdfRenderer` with 100%–250% zoom, page jump, and bookmarks).
- **12 Interactive Labs**: Dedicated parameter sliders, sub-mode toggles (including Prime Factorization Sieve and Circle Theorem Explorer), real-time Canvas visualizations, and step-by-step mathematical explanations.
