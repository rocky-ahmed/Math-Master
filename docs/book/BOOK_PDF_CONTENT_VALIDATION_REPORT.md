# Math Master — NCTB Book & PDF Content Validation Report

## 1. Executive Summary
- **Bundled Asset Directory:** `app/src/main/assets/books/`
- **Total Bundled NCTB Textbooks:** **4 complete offline PDF textbooks** (`class_6.pdf`, `class_7.pdf`, `class_8.pdf`, `class_9_10.pdf`)
- **Total Verified PDF Pages:** **894 pages** (`161 + 181 + 190 + 362`)
- **Total Curriculum Chapters:** **47 chapters** (`8 + 11 + 11 + 17`)
- **Total Bundled PDF Asset Size:** **`31,193,087` bytes (`29.75 MiB`)**
- **Native Renderer:** `android.graphics.pdf.PdfRenderer` with LruCache bitmap caching, mutex-serialized background rendering (`Dispatchers.IO`), pinch-to-zoom (`1.0x`–`3.0x`), and printed-to-PDF page offset conversion (`+5`).

---

## 2. Verified Bundled PDF Asset Inventory & SHA-256 Checksums

> **Size Reconciliation Note:** Raw unoptimized scan archives in `/public/assets/aistudio/` total ~217 MB (`Class_6_ffa30c8b.pdf` 36.08 MB, `Class_7_8a4de8c4.pdf` 46.00 MB, `Class_8_8e299d4a.pdf` 29.59 MB, `Class_9-10_6131d29f.pdf` 106.24 MB), and an intermediate build used a 65.8 MB set. The production APK bundles the optimized, lossless-page-count assets in `app/src/main/assets/books/` totaling exactly **`31,193,087` bytes (`29.75 MiB`)**:

| Book ID | Class Level (Bangla) | Asset Path (`app/src/main/assets/`) | Exact Size (Bytes) | Exact Size (MiB) | PDF Pages | Printed Pages (`maxPrintedPage`) | Page Offset (`pdf - printed`) | Chapters | SHA-256 Digest |
|---|---|---|---:|---:|---:|---:|---:|---:|---|
| `CLASS_6` | ষষ্ঠ শ্রেণি | `books/class_6.pdf` | `5,372,426` | `5.12 MiB` | `161` | `156` | `+5` | `8` | `124f9e02f1acc31cc54cbec797c1efbe7cf823c2e63050492581634bb174e5db` |
| `CLASS_7` | সপ্তম শ্রেণি | `books/class_7.pdf` | `6,391,697` | `6.10 MiB` | `181` | `176` | `+5` | `11` | `39e50f015d1eb328824a6ce1b8df7dcafac834a748e4f1af07ad523b830a4871` |
| `CLASS_8` | অষ্টম শ্রেণি | `books/class_8.pdf` | `6,459,157` | `6.16 MiB` | `190` | `185` | `+5` | `11` | `7b2e5b1dd718ed5684a0bacdfd9970d9eb99dbaa015feb07a88cddfbca6a1574` |
| `CLASS_9_10` | নবম-দশম শ্রেণি | `books/class_9_10.pdf` | `12,969,807` | `12.37 MiB` | `362` | `357` | `+5` | `17` | `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0` |
| **Total** | **৬ষ্ঠ–১০ম শ্রেণি** | **4 Bundled PDFs** | **`31,193,087`** | **`29.75 MiB`** | **`894`** | **`874`** | **`+5`** | **`47`** | **Verified (`%PDF-1.x` headers & `%%EOF` trailers)** |

---

## 3. Chapter & Page Mapping Audit Across All 47 Chapters

- **Class 6 (`BookCurriculumClass6.kt` — 8 Chapters):**
  - Ch 1: স্বাভাবিক সংখ্যা ও ভগ্নাংশ (Printed pp. 1–37 -> PDF pp. 6–42)
  - Ch 2: অনুপাত ও শতকরা (Printed pp. 38–57 -> PDF pp. 43–62)
  - Ch 3: পূর্ণসংখ্যা (Printed pp. 58–74 -> PDF pp. 63–79)
  - Ch 4: বীজগণিতীয় রাশি (Printed pp. 75–96 -> PDF pp. 80–101)
  - Ch 5: সরল সমীকরণ (Printed pp. 97–108 -> PDF pp. 102–113)
  - Ch 6: জ্যামিতির মৌলিক ধারণা (Printed pp. 109–127 -> PDF pp. 114–132)
  - Ch 7: ব্যবহারিক জ্যামিতি (Printed pp. 128–140 -> PDF pp. 133–145)
  - Ch 8: তথ্য ও উপাত্ত (Printed pp. 141–149 -> PDF pp. 146–154; Answer Key pp. 150–155)
- **Class 7 (`BookCurriculumClass7.kt` — 11 Chapters):**
  - Ch 1–11 covering মূলদ ও অমূলদ সংখ্যা, সমানুপাত ও লাভ-ক্ষতি, পরিমাপ, বীজগণিতীয় রাশির গুণ ও ভাগ, সূত্রাবলি ও প্রয়োগ, বীজগণিতীয় ভগ্নাংশ, সরল সমীকরণ, সমান্তরাল সরলরেখা, ত্রিভুজ, সর্বসমতা ও সদৃশতা, তথ্য ও উপাত্ত (Printed pp. 1–169, Answer Key pp. 170–175).
- **Class 8 (`BookCurriculumClass8.kt` — 11 Chapters):**
  - Ch 1–11 covering প্যাটার্ন, মুনাফা, পরিমাপ, বীজগণিতীয় সূত্রাবলি ও প্রয়োগ, বীজগণিতীয় ভগ্নাংশ, সরল সহসমীকরণ, সেট, চতুর্ভুজ, পিথাগোরাসের উপপাদ্য, বৃত্ত, তথ্য ও উপাত্ত (Printed pp. 1–174, Answer Key pp. 175–184).
- **Class 9–10 (`BookCurriculumClass910.kt` — 17 Chapters):**
  - Ch 1–17 covering বাস্তব সংখ্যা, সেট ও ফাংশন, বীজগাণিতিক রাশি, সূচক ও লগারিদম, এক চলকবিশিষ্ট সমীকরণ, রেখা-কোণ-ত্রিভুজ, ব্যবহারিক জ্যামিতি, বৃত্ত, ত্রিকোণমিতিক অনুপাত, দূরত্ব ও উচ্চতা, বীজগাণিতিক অনুপাত ও সমানুপাত, দুই চলকবিশিষ্ট সরল সহসমীকরণ, সসীম ধারা, অনুপাত-সদৃশতা-প্রতিসমতা, ক্ষেত্রফল সম্পর্কিত উপপাদ্য ও সম্পাদ্য, পরিমিতি, পরিসংখ্যান (Printed pp. 1–344, Answer Key pp. 345–354).

---

## 4. Hybrid Reader, Interactive Labs & Persistence Validation

1. **Printed <-> PDF Page Mapping:** `BookId.printedToPdfPage(printedPage)` and `BookId.pdfToPrintedPage(pdfPage)` clamp cleanly to `[1, totalPdfPages]` and `[1, totalPrintedPages]`.
2. **12 Interactive Math Labs (`BookInteractiveLabType`):** All 12 labs (`NUMBER_SYSTEM_EXPLORER`, `FRACTION_AND_RATIO_LAB`, `PROFIT_LOSS_PERCENTAGE_CALC`, `PATTERN_AND_SEQUENCE_LAB`, `ALGEBRA_INTERACTIVE_LAB`, `EQUATION_SOLVER_VISUALIZER`, `GEOMETRY_INTERACTIVE_LAB`, `PYTHAGORAS_THEOREM_VISUALIZER`, `TRIGONOMETRY_HEIGHT_DISTANCE_LAB`, `MENSURATION_CALCULATOR`, `DATA_AND_STATISTICS_LAB`, `SET_AND_FUNCTION_VISUALIZER`) are wired and verified.
3. **Reading State & Notes Persistence:** `book_reading_state`, `book_lesson_progress`, `book_notes`, `book_mistakes`, and `book_spaced_revision` persist across restarts in Room v9.
