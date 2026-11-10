# Math Master — Final NCTB Book and PDF Validation Report

**Scope**: Module 14 (`BOOK — পাঠ্যবই`), 4 Bundled NCTB PDF Assets (`class_6.pdf`, `class_7.pdf`, `class_8.pdf`, `class_9_10.pdf`), 47 Chapters, 894 Total PDF Pages (`874` Printed Pages), Hybrid Structured & Native `PdfRenderer` Reader, 12 Interactive Math Labs, Chapter Practice (`BUG-22`, `BUG-26`, `BUG-29`), and Spaced-Repetition Revision Hub (`BUG-27`, `BUG-31`, `BUG-35`).

---

## 1. Bundled NCTB PDF Asset Inventory & Binary Checksum Verification (`app/src/main/assets/books/`)

All four bundled PDF files in `app/src/main/assets/books/` were inspected directly at the binary, SHA-256, and PDF page-tree levels (`BookModels.kt` & `EcosystemUpgradeV7UnitTest.releaseCandidate_phaseD_all4PdfAssets894PagesAnd47Chapters`):

| BookId | Exact Asset Path | Exact Size (Bytes) | Size (MiB) | SHA-256 Checksum | PDF Pages | Printed Pages (`BookId.totalPrintedPages`) | Chapters | Page Offset (`pdfPage = printedPage + 5`) | Status |
| :--- | :--- | ---: | ---: | :--- | :-: | :-: | :-: | :-: | :--- |
| **`CLASS_6`** | `app/src/main/assets/books/class_6.pdf` | `5,372,426` | `5.12 MiB` | `124f9e02f1acc31cc54cbec797c1efbe7cf823c2e63050492581634bb174e5db` | **`161`** | `1`–`156` (Ch 1–8: `1–149`, Ans: `150–155`) | `8` | `+5` (`pdfPage - 5`) | **VERIFIED** |
| **`CLASS_7`** | `app/src/main/assets/books/class_7.pdf` | `6,391,697` | `6.10 MiB` | `39e50f015d1eb328824a6ce1b8df7dcafac834a748e4f1af07ad523b830a4871` | **`181`** | `1`–`176` (Ch 1–11: `1–169`, Ans: `170–175`) | `11` | `+5` (`pdfPage - 5`) | **VERIFIED** |
| **`CLASS_8`** | `app/src/main/assets/books/class_8.pdf` | `6,459,157` | `6.16 MiB` | `7b2e5b1dd718ed5684a0bacdfd9970d9eb99dbaa015feb07a88cddfbca6a1574` | **`190`** | `1`–`185` (Ch 1–11: `1–174`, Ans: `175–184`) | `11` | `+5` (`pdfPage - 5`) | **VERIFIED** |
| **`CLASS_9_10`** | `app/src/main/assets/books/class_9_10.pdf` | `12,969,807` | `12.37 MiB` | `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0` | **`362`** | `1`–`357` (Ch 1–17: `1–344`, Ans: `345–354`) | `17` | `+5` (`pdfPage - 5`) | **VERIFIED** |
| **Total** | **4 Bundled NCTB PDFs** | **`31,193,087`** | **`29.75 MiB`** | **All 4 Checksums Verified** | **`894`** | **`874` Printed Pages** | **`47`** | **`+5` Uniform Offset** | **100% VERIFIED** |

### Full SHA-256 Digest Reference
- `app/src/main/assets/books/class_6.pdf`: `124f9e02f1acc31cc54cbec797c1efbe7cf823c2e63050492581634bb174e5db`
- `app/src/main/assets/books/class_7.pdf`: `39e50f015d1eb328824a6ce1b8df7dcafac834a748e4f1af07ad523b830a4871`
- `app/src/main/assets/books/class_8.pdf`: `7b2e5b1dd718ed5684a0bacdfd9970d9eb99dbaa015feb07a88cddfbca6a1574`
- `app/src/main/assets/books/class_9_10.pdf`: `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0`

---

## 2. Hybrid Reader (`BookReaderAndLabsScreens.kt`)

### 2.1 Native `PdfRenderer` Mode (`PreferredReaderViewMode.PDF_CANVAS`, `BUG-09` & `BUG-35`)
- **Book PDF Reader Concurrency, Bitmap Memory Management, `Mutex` Serialization & `LruCache` Optimization (`BUG-35`)**:
  - **Synchronized Atomic Asset Extraction (`resolveOfflinePdfFile`, `BUG-35`)**:
    - Copies the bundled PDF from `context.assets.open("books/${bookId.id}.pdf")` to a unique temporary file `cached_${bookId.id}_${System.nanoTime()}.pdf.tmp` in `context.cacheDir` under `pdfExtractionLock` synchronization and atomically renames to `cached_${bookId.id}.pdf`.
  - **Serialized Rendering & Bounded Cache (`BookPdfRenderCache`, `BUG-35`)**:
    - Serializes `PdfRenderer.openPage` operations using a coroutine `Mutex` (`renderMutex`).
    - Caches rendered page bitmaps in a bounded `LruCache<String, Bitmap>(12)` using `Bitmap.Config.RGB_565` (50% lower memory footprint than `ARGB_8888`) with an automatic `OutOfMemoryError` cache-eviction fallback.
- **Zoom & Page Controls**:
  - Supports `1.0x`, `1.25x`, `1.5x`, `2.0x`, `2.5x`, and `3.0x` zoom chips, direct printed-page and PDF-page jump (`BUG-09`), and 1-tap page bookmarking (`toggleBookPageBookmark`).

### 2.2 Structured Native Page Mode (`PreferredReaderViewMode.STRUCTURED`)
- Renders rich Bengali mathematical cards for every printed page across all 47 chapters (`BookCurriculumRepository.getStructuredPageContent`), including:
  - Learning objectives, concept blocks, highlighted formula boxes, step-by-step worked examples, common mistakes, and exercise problem sets.
  - Direct **"ল্যাব খুলুন"** deep links to all **12 Interactive Textbook Math Labs** (`BookInteractiveLabType`).

---

## 3. Book Practice & Revision Hub Refinements (`BUG-22`, `BUG-26`, `BUG-27`, `BUG-29`, `BUG-31`)

1. **Bidirectional Question Navigation (`BUG-22` Fixed)**:
   - Students can navigate backward (`previousBookPracticeQuestion()`) and forward (`nextBookPracticeQuestion()`) during a Book Practice session without losing their selected options or typed answers, and already-submitted questions are locked against duplicate submission or score inflation.
2. **Fractional, Ratio, Mixed Fraction & Dotless Unit Equivalence (`BUG-26` & `BUG-29` Fixed)**:
   - `BookQuestionEngine.isTextEquivalent()` delegates fallback validation to `SharedMathEngine.isAnswerCorrect()`, supporting equivalent fractions (`১/২` vs `0.5` vs `২/৪`), mixed fractions (`১ ১/২` vs `1.5` vs `১ সমস্ত ১/২`), ratios (`৩ : ৪` vs `6 : 8`), and dotted/dotless Bengali unit suffixes (`২৪ সেমি`, `১৫০ বর্গসেমি`, `৩৬টি`).
3. **Multi-Book Filter Bar in Revision Hub (`BUG-27` Fixed)**:
   - `BookRevisionScreen` provides a horizontal filter chip bar (`সব বই` + `ষষ্ঠ শ্রেণি`, `সপ্তম শ্রেণি`, `অষ্টম শ্রেণি`, `নবম-দশম শ্রেণি`) that filters Spaced Revisions, Mistakes, Personal Notes, and Bookmarked Pages by textbook.
4. **Book Mistake & Spaced Revision Cross-Table Synchronization (`BUG-31` Fixed)**:
   - Normalizes `rawId = questionId.removePrefix("mistake_").removePrefix("BOOK_")` and `revItemId = "mistake_$rawId"` across `recordBookQuestionMistake`, `markBookMistakeResolved`, and `deleteBookMistake` so no orphaned or double-prefixed rows remain in `book_spaced_revision`, and synchronizes resolution states with `unified_mistake_notebook` (`"BOOK_$rawId"`).
