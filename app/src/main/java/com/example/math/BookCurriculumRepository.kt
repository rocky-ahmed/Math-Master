package com.example.math

object BookCurriculumRepository {
    val allBooks: List<BookId> = BookId.entries

    private val chaptersByBook: Map<BookId, List<BookChapterSpec>> = mapOf(
        BookId.CLASS_6 to BookCurriculumClass6.chapters,
        BookId.CLASS_7 to BookCurriculumClass7.chapters,
        BookId.CLASS_8 to BookCurriculumClass8.chapters,
        BookId.CLASS_9_10 to BookCurriculumClass910.chapters
    )

    fun getChapters(bookId: BookId): List<BookChapterSpec> =
        chaptersByBook[bookId].orEmpty()

    fun getAllChapters(): List<BookChapterSpec> =
        allBooks.flatMap { getChapters(it) }

    fun getChapter(bookId: BookId, chapterNumber: Int): BookChapterSpec? =
        getChapters(bookId).firstOrNull { it.chapterNumber == chapterNumber }

    fun getLesson(bookId: BookId, chapterNumber: Int, lessonId: String): BookLessonSection? =
        getChapter(bookId, chapterNumber)?.lessons?.firstOrNull { it.id == lessonId }

    fun getChapterForPrintedPage(bookId: BookId, printedPage: Int): BookChapterSpec {
        val chapters = getChapters(bookId)
        val clamped = printedPage.coerceIn(1, bookId.maxPrintedPage)
        return chapters.firstOrNull { clamped in it.printedPageStart..it.printedPageEnd }
            ?: chapters.lastOrNull { clamped >= it.printedPageStart }
            ?: chapters.first()
    }

    fun getTotalLessonsCount(bookId: BookId): Int =
        getChapters(bookId).sumOf { it.lessons.size }

    fun getTotalFormulasCount(bookId: BookId): Int =
        getChapters(bookId).sumOf { it.formulas.size }

    fun getStructuredPageContent(bookId: BookId, printedPage: Int): StructuredTextbookPageContent {
        val safePrintedPage = printedPage.coerceIn(1, bookId.maxPrintedPage)
        val chapter = getChapterForPrintedPage(bookId, safePrintedPage)
        val matchingLesson = chapter.lessons.firstOrNull {
            safePrintedPage in it.printedPageStart..it.printedPageEnd
        } ?: chapter.lessons.firstOrNull()

        val isBackCoverPage = safePrintedPage == bookId.maxPrintedPage
        val isMathematiciansAppendix = bookId == BookId.CLASS_9_10 && safePrintedPage in 355..356
        val isAnswerKeyPage = safePrintedPage in bookId.answerKeyStartPrintedPage..bookId.answerKeyEndPrintedPage

        val sectionHeadingBn = when {
            isBackCoverPage -> "সমাপনী পৃষ্ঠা ও মুদ্রণ তথ্য (NCTB)"
            isMathematiciansAppendix -> "গণিতবিদদের সংক্ষিপ্ত পরিচিতি (পৃষ্ঠা ৩৫৫–৩৫৬)"
            isAnswerKeyPage -> "উত্তরমালা (অধ্যায়ভিত্তিক সংক্ষিপ্ত উত্তর)"
            matchingLesson != null -> "পাঠ ${matchingLesson.code}: ${matchingLesson.titleBn}"
            else -> "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)}: ${chapter.titleBn}"
        }

        val pageOffsetInChapter = (safePrintedPage - chapter.printedPageStart).coerceAtLeast(0)
        val formulasOnPage = if (chapter.formulas.isNotEmpty()) {
            val idx = pageOffsetInChapter % chapter.formulas.size
            val secondIdx = (idx + 1) % chapter.formulas.size
            if (chapter.formulas.size > 1 && pageOffsetInChapter == 0) {
                chapter.formulas
            } else if (chapter.formulas.size > 1) {
                listOf(chapter.formulas[idx], chapter.formulas[secondIdx]).distinctBy { it.id }
            } else {
                listOf(chapter.formulas[idx])
            }
        } else {
            emptyList()
        }

        val examplesOnPage = chapter.workedExamples.filter { it.printedPage == safePrintedPage }
            .ifEmpty {
                if (chapter.workedExamples.isNotEmpty()) {
                    listOf(chapter.workedExamples[pageOffsetInChapter % chapter.workedExamples.size])
                } else {
                    emptyList()
                }
            }

        val theoremsOnPage = chapter.theoremsOrConstructions.filter {
            it.printedPage == safePrintedPage || pageOffsetInChapter == 0
        }.ifEmpty {
            chapter.theoremsOrConstructions.take(1)
        }

        val rulesOnPage = buildList {
            matchingLesson?.stepByStepRulesBn?.let { addAll(it) }
            addAll(chapter.mathematicalRulesBn)
            matchingLesson?.quickMemoryTipsBn?.let { addAll(it) }
        }.distinct()

        val answerKeyOnPage = if (isAnswerKeyPage) {
            getChapters(bookId).flatMap { ch ->
                if (ch.answerKeySummaryBn.isNotEmpty()) {
                    ch.answerKeySummaryBn.map { "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(ch.chapterNumber)}: $it" }
                } else {
                    ch.workedExamples.map { ex ->
                        "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(ch.chapterNumber)} (${ex.titleBn}): উত্তর = ${ex.finalAnswerBn}"
                    }
                }
            }
        } else {
            chapter.answerKeySummaryBn.ifEmpty {
                examplesOnPage.map { "${it.titleBn}: চূড়ান্ত উত্তর = ${it.finalAnswerBn}" }
            }
        }

        val keyDefinitions = buildList {
            if (isMathematiciansAppendix) {
                add("ইউক্লিড, পিথাগোরাস, আর্যভট্ট, ব্রহ্মগুপ্ত, আল-খোয়ারিজমি, জন নেপিয়ার, দেকার্তে ও জর্জ ক্যান্টরের গাণিতিক অবদান।")
            }
            add(chapter.summaryBn)
            matchingLesson?.let { lesson ->
                add(lesson.conceptExplanationBn)
                addAll(lesson.keyPointsBn)
            }
            theoremsOnPage.forEach { th ->
                add("${th.typeLabelBn} — ${th.titleBn}: ${th.statementBn}")
            }
        }

        val exercisesSummary = if (isAnswerKeyPage || isMathematiciansAppendix || isBackCoverPage) {
            getChapters(bookId).map { ch ->
                "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(ch.chapterNumber)} (${ch.titleBn}): অনুশীলনীর উত্তরমালা ও যাচাই নির্দেশিকা"
            }
        } else {
            buildList {
                addAll(chapter.exerciseNamesBn)
                matchingLesson?.stepByStepRulesBn?.forEach { rule ->
                    add("সমাধান কৌশল: $rule")
                }
            }
        }

        return StructuredTextbookPageContent(
            bookId = bookId,
            printedPage = safePrintedPage,
            pdfPage = bookId.printedToPdfPage(safePrintedPage),
            chapterNumber = chapter.chapterNumber,
            chapterTitleBn = chapter.titleBn,
            sectionHeadingBn = sectionHeadingBn,
            keyDefinitionsBn = keyDefinitions,
            formulasOnPage = formulasOnPage,
            workedExamplesOnPage = examplesOnPage,
            exerciseSummaryBn = exercisesSummary,
            theoremsOnPage = theoremsOnPage,
            rulesOnPageBn = rulesOnPage,
            answerKeyOnPageBn = answerKeyOnPage
        )
    }

    fun searchAllBooks(
        query: String,
        filterType: BookSearchFilterType = BookSearchFilterType.ALL,
        bookFilter: BookId? = null
    ): List<BookSearchResult> {
        val trimmed = query.trim()
        val normalizedQuery = BengaliNumberFormatter.toEnglishDigits(trimmed).lowercase()
        val booksToSearch = if (bookFilter != null) listOf(bookFilter) else allBooks
        val results = mutableListOf<BookSearchResult>()

        fun matches(vararg texts: String): Boolean {
            if (trimmed.isEmpty()) return true
            return texts.any { text ->
                val lower = text.lowercase()
                val normalizedText = BengaliNumberFormatter.toEnglishDigits(lower)
                lower.contains(trimmed.lowercase()) || normalizedText.contains(normalizedQuery)
            }
        }

        for (book in booksToSearch) {
            for (chapter in getChapters(book)) {
                val chNumStr = chapter.chapterNumber.toString()
                val chNumBn = BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)

                // 1. Chapter search
                if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.CHAPTER) {
                    if (matches(chapter.titleBn, chapter.titleEn, chapter.summaryBn, "অধ্যায় $chNumBn", "chapter $chNumStr")) {
                        results.add(
                            BookSearchResult(
                                id = "ch_${book.id}_${chapter.chapterNumber}",
                                bookId = book,
                                chapterNumber = chapter.chapterNumber,
                                titleBn = "অধ্যায় $chNumBn: ${chapter.titleBn}",
                                subtitleBn = "${book.titleBn} • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageEnd)}",
                                snippetBn = chapter.summaryBn,
                                resultType = BookSearchFilterType.CHAPTER,
                                printedPage = chapter.printedPageStart
                            )
                        )
                    }
                }

                // 2. Lesson search
                if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.LESSON) {
                    for (lesson in chapter.lessons) {
                        if (matches(lesson.titleBn, lesson.code, lesson.conceptExplanationBn, lesson.keyPointsBn.joinToString(" "))) {
                            results.add(
                                BookSearchResult(
                                    id = "les_${lesson.id}",
                                    bookId = book,
                                    chapterNumber = chapter.chapterNumber,
                                    titleBn = "পাঠ ${lesson.code}: ${lesson.titleBn}",
                                    subtitleBn = "${book.shortLabelBn} • অধ্যায় $chNumBn • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(lesson.printedPageStart)}",
                                    snippetBn = lesson.conceptExplanationBn,
                                    resultType = BookSearchFilterType.LESSON,
                                    lessonId = lesson.id,
                                    printedPage = lesson.printedPageStart
                                )
                            )
                        }
                    }
                }

                // 3. Formula search
                if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.FORMULA) {
                    for (formula in chapter.formulas) {
                        if (matches(formula.titleBn, formula.expressionBn, formula.explanationBn, chapter.titleBn)) {
                            results.add(
                                BookSearchResult(
                                    id = "frm_${formula.id}",
                                    bookId = book,
                                    chapterNumber = chapter.chapterNumber,
                                    titleBn = formula.titleBn,
                                    subtitleBn = "${book.shortLabelBn} • অধ্যায় $chNumBn (${chapter.titleBn})",
                                    snippetBn = "${formula.expressionBn} — ${formula.explanationBn}",
                                    resultType = BookSearchFilterType.FORMULA,
                                    printedPage = chapter.printedPageStart
                                )
                            )
                        }
                    }
                }

                // 4. Worked example search
                if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.EXAMPLE) {
                    for (ex in chapter.workedExamples) {
                        if (matches(ex.titleBn, ex.problemStatementBn, ex.finalAnswerBn, ex.stepsBn.joinToString(" "))) {
                            results.add(
                                BookSearchResult(
                                    id = "ex_${ex.id}",
                                    bookId = book,
                                    chapterNumber = chapter.chapterNumber,
                                    titleBn = ex.titleBn,
                                    subtitleBn = "${book.shortLabelBn} • অধ্যায় $chNumBn • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(ex.printedPage)}",
                                    snippetBn = "${ex.problemStatementBn} (উত্তর: ${ex.finalAnswerBn})",
                                    resultType = BookSearchFilterType.EXAMPLE,
                                    printedPage = ex.printedPage
                                )
                            )
                        }
                    }
                }

                // 5. Theorem / Construction search
                if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.THEOREM) {
                    for (th in chapter.theoremsOrConstructions) {
                        if (matches(th.titleBn, th.typeLabelBn, th.statementBn, th.stepsBn.joinToString(" "))) {
                            results.add(
                                BookSearchResult(
                                    id = "th_${th.id}",
                                    bookId = book,
                                    chapterNumber = chapter.chapterNumber,
                                    titleBn = "${th.typeLabelBn}: ${th.titleBn}",
                                    subtitleBn = "${book.shortLabelBn} • অধ্যায় $chNumBn • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(th.printedPage)}",
                                    snippetBn = th.statementBn,
                                    resultType = BookSearchFilterType.THEOREM,
                                    printedPage = th.printedPage
                                )
                            )
                        }
                    }
                }
            }
        }

        // 6. Question search if requested
        if (filterType == BookSearchFilterType.ALL || filterType == BookSearchFilterType.QUESTION) {
            for (book in booksToSearch) {
                for (chapter in getChapters(book)) {
                    val questions = BookQuestionEngine.getQuestionsForChapter(book, chapter.chapterNumber)
                    for (q in questions) {
                        if (matches(q.promptBn, q.explanationBn, q.correctTextAnswer, q.provenance.badgeBn)) {
                            val pageTag = q.sourcePrintedPage?.let { " • পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(it)}" } ?: ""
                            results.add(
                                BookSearchResult(
                                    id = "q_${q.id}",
                                    bookId = book,
                                    chapterNumber = chapter.chapterNumber,
                                    titleBn = "${q.provenance.badgeBn} (${q.format.labelBn})",
                                    subtitleBn = "${book.shortLabelBn} • অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)} (${chapter.titleBn})$pageTag",
                                    snippetBn = q.promptBn,
                                    resultType = BookSearchFilterType.QUESTION,
                                    printedPage = q.sourcePrintedPage ?: chapter.printedPageStart
                                )
                            )
                        }
                    }
                }
            }
        }

        return results.take(80)
    }
}
