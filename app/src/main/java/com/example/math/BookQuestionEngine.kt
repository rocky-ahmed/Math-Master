package com.example.math

import kotlin.math.abs

object BookQuestionEngine {

    /**
     * Builds a comprehensive list of multi-format questions for any chapter of any book
     * covering all 10 question formats and all 3 provenance types:
     * - ORIGINAL_TEXTBOOK
     * - CONCEPT_DERIVED
     * - GENERATED
     */
    fun getQuestionsForChapter(
        bookId: BookId,
        chapterNumber: Int
    ): List<BookPracticeQuestion> {
        val chapter = BookCurriculumRepository.getChapter(bookId, chapterNumber)
            ?: return emptyList()
        val questions = mutableListOf<BookPracticeQuestion>()

        // 1. SINGLE_CHOICE_MCQ from each lesson's mini-check (ORIGINAL_TEXTBOOK)
        chapter.lessons.forEachIndexed { idx, lesson ->
            questions.add(
                BookPracticeQuestion(
                    id = "${bookId.id}_c${chapterNumber}_mcq_${idx + 1}",
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    lessonId = lesson.id,
                    format = BookQuestionFormat.SINGLE_CHOICE_MCQ,
                    difficultyMode = if (idx == 0) BookPracticeMode.BASIC else BookPracticeMode.STANDARD,
                    provenance = BookQuestionProvenance.ORIGINAL_TEXTBOOK,
                    promptBn = lesson.miniCheckQuestionBn,
                    optionsBn = lesson.miniCheckOptionsBn,
                    correctOptionIndices = setOf(lesson.miniCheckCorrectIndex),
                    correctTextAnswer = lesson.miniCheckOptionsBn.getOrElse(lesson.miniCheckCorrectIndex) { "" },
                    hintBn = lesson.quickMemoryTipsBn.firstOrNull() ?: chapter.summaryBn,
                    stepByStepSolutionBn = listOf(
                        "পাঠ ${lesson.code} (${lesson.titleBn}) এর মূল ধারণা অনুসারে:",
                        lesson.miniCheckExplanationBn
                    ),
                    explanationBn = lesson.miniCheckExplanationBn,
                    sourcePrintedPage = lesson.printedPageStart
                )
            )
        }

        // 2. FORMULA_IDENTIFICATION from chapter formulas (ORIGINAL_TEXTBOOK)
        if (chapter.formulas.isNotEmpty()) {
            val primaryFormula = chapter.formulas.first()
            val otherFormulas = BookCurriculumRepository.getChapters(bookId)
                .filter { it.chapterNumber != chapterNumber }
                .flatMap { it.formulas }
                .map { it.expressionBn }
                .distinct()
                .take(3)
            val distractors = if (otherFormulas.size >= 3) {
                otherFormulas
            } else {
                listOf("a + b = ab", "x² - y² = (x - y)²", "I = P + r + n")
            }
            val options = listOf(primaryFormula.expressionBn) + distractors.take(3)
            questions.add(
                BookPracticeQuestion(
                    id = "${bookId.id}_c${chapterNumber}_formula_1",
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    lessonId = chapter.lessons.firstOrNull()?.id,
                    format = BookQuestionFormat.FORMULA_IDENTIFICATION,
                    difficultyMode = BookPracticeMode.BASIC,
                    provenance = BookQuestionProvenance.ORIGINAL_TEXTBOOK,
                    promptBn = "‘${primaryFormula.titleBn}’ এর সঠিক গাণিতিক সূত্র বা সম্পর্ক কোনটি?",
                    optionsBn = options,
                    correctOptionIndices = setOf(0),
                    correctTextAnswer = primaryFormula.expressionBn,
                    hintBn = primaryFormula.explanationBn,
                    stepByStepSolutionBn = listOf(
                        "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNumber)} (${chapter.titleBn}) এর সূত্র তালিকা দেখ:",
                        "সূত্র: ${primaryFormula.expressionBn}",
                        "ব্যাখ্যা: ${primaryFormula.explanationBn}"
                    ),
                    explanationBn = "${primaryFormula.titleBn}: ${primaryFormula.expressionBn} (${primaryFormula.explanationBn})",
                    sourcePrintedPage = chapter.printedPageStart
                )
            )
        }

        // 3. STEP_BY_STEP_SOLVING from worked examples (ORIGINAL_TEXTBOOK)
        chapter.workedExamples.forEachIndexed { idx, ex ->
            val correctStep = ex.stepsBn.lastOrNull() ?: ex.finalAnswerBn
            val options = listOf(
                "চূড়ান্ত ধাপ ও উত্তর: ${ex.finalAnswerBn}",
                "ভুল চিহ্ন পরিবর্তন করে উত্তর: ০",
                "অসম্পূর্ণ ধাপ: শুধু প্রদত্ত মান গুণ করা",
                "বিপরীত সূত্র প্রয়োগ করে ঋণাত্মক মান নির্ণয়"
            )
            questions.add(
                BookPracticeQuestion(
                    id = "${bookId.id}_c${chapterNumber}_step_${idx + 1}",
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    lessonId = chapter.lessons.firstOrNull()?.id,
                    format = BookQuestionFormat.STEP_BY_STEP_SOLVING,
                    difficultyMode = BookPracticeMode.CHAPTER_EXERCISE,
                    provenance = BookQuestionProvenance.ORIGINAL_TEXTBOOK,
                    promptBn = "${ex.problemStatementBn} — ধাপে ধাপে সমাধান করলে সঠিক চূড়ান্ত ফলাফল কোনটি হবে?",
                    optionsBn = options,
                    correctOptionIndices = setOf(0),
                    correctTextAnswer = ex.finalAnswerBn,
                    hintBn = ex.stepsBn.firstOrNull() ?: "প্রথমে প্রদত্ত উপাত্তগুলো সূত্রে বসাও।",
                    stepByStepSolutionBn = ex.stepsBn + "অতএব নির্ণেয় উত্তর: ${ex.finalAnswerBn} ($correctStep)",
                    explanationBn = ex.stepsBn.joinToString(" → "),
                    sourcePrintedPage = ex.printedPage
                )
            )
        }

        // 4. TRUE_FALSE based on common mistakes & key points (CONCEPT_DERIVED)
        val firstLesson = chapter.lessons.firstOrNull()
        val keyFact = firstLesson?.keyPointsBn?.firstOrNull() ?: chapter.objectivesBn.firstOrNull() ?: chapter.summaryBn
        questions.add(
            BookPracticeQuestion(
                id = "${bookId.id}_c${chapterNumber}_tf_1",
                bookId = bookId,
                chapterNumber = chapterNumber,
                lessonId = firstLesson?.id,
                format = BookQuestionFormat.TRUE_FALSE,
                difficultyMode = BookPracticeMode.BASIC,
                provenance = BookQuestionProvenance.CONCEPT_DERIVED,
                promptBn = "সত্য নাকি মিথ্যা যাচাই কর: “$keyFact”",
                optionsBn = listOf("সত্য (True)", "মিথ্যা (False)"),
                correctOptionIndices = setOf(0),
                correctTextAnswer = "সত্য (True)",
                hintBn = "পাঠ্যবইয়ের মূল বক্তব্য ও সারসংক্ষেপ মিলিয়ে দেখ।",
                stepByStepSolutionBn = listOf(
                    "প্রদত্ত উক্তিটি অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNumber)} (${chapter.titleBn}) এর মূল তত্ত্বের সাথে সম্পূর্ণ সামঞ্জস্যপূর্ণ।",
                    "সুতরাং উক্তিটি সত্য।"
                ),
                explanationBn = "পাঠ্যবই অনুসারে উক্তিটি সম্পূর্ণ সত্য: $keyFact"
            )
        )

        // 5. MULTIPLE_SELECT covering learning objectives & rules (CONCEPT_DERIVED)
        val correctStatement1 = chapter.objectivesBn.getOrElse(0) { chapter.summaryBn }
        val correctStatement2 = firstLesson?.stepByStepRulesBn?.firstOrNull()
            ?: chapter.objectivesBn.getOrElse(1) { "গাণিতিক নিয়ম অনুসরণ করে ধাপে ধাপে সমাধান করা যায়" }
        val wrongMisconception = firstLesson?.commonMistakesBn?.firstOrNull()
            ?: "চিহ্ন ও বন্ধনীর ক্রম উপেক্ষা করলেও উত্তর অপরিবর্তিত থাকে"
        questions.add(
            BookPracticeQuestion(
                id = "${bookId.id}_c${chapterNumber}_ms_1",
                bookId = bookId,
                chapterNumber = chapterNumber,
                lessonId = firstLesson?.id,
                format = BookQuestionFormat.MULTIPLE_SELECT,
                difficultyMode = BookPracticeMode.ADVANCED,
                provenance = BookQuestionProvenance.CONCEPT_DERIVED,
                promptBn = "অধ্যায় ‘${chapter.titleBn}’ সম্পর্কে নিচের কোন দুটি তথ্য সঠিক? (একাধিক সঠিক উত্তর নির্বাচন কর)",
                optionsBn = listOf(
                    correctStatement1,
                    correctStatement2,
                    "ভুল ধারণা: $wrongMisconception",
                    "এই অধ্যায়ে কোনো গাণিতিক সূত্র বা নিয়ম প্রযোজ্য নয়"
                ),
                correctOptionIndices = setOf(0, 1),
                correctTextAnswer = "$correctStatement1; $correctStatement2",
                hintBn = "প্রথম দুটি বিকল্প পাঠ্যবইয়ের শিখনফল ও সমাধান নিয়ম নির্দেশ করে।",
                stepByStepSolutionBn = listOf(
                    "১ম বিকল্পটি অধ্যায়ের প্রধান শিখনফল: $correctStatement1",
                    "২য় বিকল্পটি সঠিক সমাধান কৌশল: $correctStatement2",
                    "৩য় বিকল্পটি একটি সাধারণ ভুল ($wrongMisconception), তাই এটি বর্জনীয়।"
                ),
                explanationBn = "১ম ও ২য় উভয় তথ্যই সঠিক।"
            )
        )

        // 6. MATCHING_PAIRS connecting chapter concepts/formulas (CONCEPT_DERIVED)
        val pairs = buildMatchingPairsForChapter(chapter)
        val pairSummary = pairs.joinToString(" | ") { "${it.first} = ${it.second}" }
        questions.add(
            BookPracticeQuestion(
                id = "${bookId.id}_c${chapterNumber}_match_1",
                bookId = bookId,
                chapterNumber = chapterNumber,
                lessonId = firstLesson?.id,
                format = BookQuestionFormat.MATCHING_PAIRS,
                difficultyMode = BookPracticeMode.STANDARD,
                provenance = BookQuestionProvenance.CONCEPT_DERIVED,
                promptBn = "বাম পাশের গাণিতিক ধারণার সাথে ডান পাশের সঠিক তথ্য/সূত্র মিল কর:",
                optionsBn = listOf(
                    pairSummary,
                    "বিপরীত ক্রমে এলোমেলো মিলকরণ",
                    "সবগুলো পদের মান শূন্য ধরা",
                    "কেবল প্রথম জোড়াটি সঠিক এবং বাকিগুলো অসংজ্ঞায়িত"
                ),
                correctOptionIndices = setOf(0),
                correctTextAnswer = pairSummary,
                matchingPairs = pairs,
                hintBn = "প্রতিটি সূত্রের নাম ও তার গাণিতিক প্রকাশ পাশাপাশি মিলিয়ে দেখ।",
                stepByStepSolutionBn = pairs.mapIndexed { i, p ->
                    "জোড়া ${BengaliNumberFormatter.toBengaliDigits(i + 1)}: ${p.first} → ${p.second}"
                },
                explanationBn = "সঠিক মিলকরণ: $pairSummary"
            )
        )

        // 7. NUMERICAL_INPUT dynamically generated for the chapter (GENERATED)
        val numericalQuestion = buildGeneratedNumericalQuestion(bookId, chapter)
        questions.add(numericalQuestion)

        // 8. FILL_IN_THE_BLANK for quick concept recall (CONCEPT_DERIVED)
        val blankQuestion = buildFillInTheBlankQuestion(bookId, chapter)
        questions.add(blankQuestion)

        // 9. SHORT_ANSWER for textbook definitions/worked answers (ORIGINAL_TEXTBOOK)
        val shortAnswerQuestion = buildShortAnswerQuestion(bookId, chapter)
        questions.add(shortAnswerQuestion)

        // 10. GEOMETRY_DIAGRAM (for geometry/mensuration/trig/graph chapters or visual representation)
        val diagramQuestion = buildDiagramQuestion(bookId, chapter)
        questions.add(diagramQuestion)

        // Ensure every chapter has at least 10 questions by adding second formula / objective MCQs if needed
        var extraIndex = 1
        while (questions.size < 10) {
            val objText = chapter.objectivesBn.getOrElse((extraIndex - 1) % chapter.objectivesBn.size.coerceAtLeast(1)) {
                chapter.summaryBn
            }
            questions.add(
                BookPracticeQuestion(
                    id = "${bookId.id}_c${chapterNumber}_obj_mcq_$extraIndex",
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    lessonId = firstLesson?.id,
                    format = BookQuestionFormat.SINGLE_CHOICE_MCQ,
                    difficultyMode = BookPracticeMode.CHAPTER_TEST,
                    provenance = BookQuestionProvenance.CONCEPT_DERIVED,
                    promptBn = "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNumber)} (${chapter.titleBn}) এর অন্যতম প্রধান শিখনফল কোনটি?",
                    optionsBn = listOf(
                        objText,
                        "কেবল মুখস্থ মান ব্যবহার করে কোনো ধাপ ছাড়াই অনুমান করা",
                        "গাণিতিক চিহ্ন ও একক সম্পূর্ণ বর্জন করা",
                        "সূত্র ছাড়া এলোমেলো সংখ্যা যোগ করা"
                    ),
                    correctOptionIndices = setOf(0),
                    correctTextAnswer = objText,
                    hintBn = "পাঠ্যবইয়ের শুরুতে প্রদত্ত শিখনফল (Learning Objectives) স্মরণ কর।",
                    stepByStepSolutionBn = listOf(
                        "অধ্যায়ের শিখনফল অনুযায়ী: $objText",
                        "সুতরাং ১ম বিকল্পটি সঠিক।"
                    ),
                    explanationBn = "সঠিক শিখনফল: $objText"
                )
            )
            extraIndex++
        }

        return questions
    }

    fun buildSessionQuestions(
        bookId: BookId,
        chapterNumber: Int?,
        mode: BookPracticeMode,
        maxCount: Int = 10,
        weakQuestionIds: Set<String> = emptySet()
    ): List<BookPracticeQuestion> {
        val rawPool = if (chapterNumber != null && mode != BookPracticeMode.MIXED_PRACTICE && mode != BookPracticeMode.REVISION_PRACTICE) {
            getQuestionsForChapter(bookId, chapterNumber)
        } else {
            BookCurriculumRepository.getChapters(bookId).flatMap { ch ->
                getQuestionsForChapter(bookId, ch.chapterNumber)
            }
        }

        val prioritized = when (mode) {
            BookPracticeMode.BASIC -> rawPool.sortedBy {
                if (it.difficultyMode == BookPracticeMode.BASIC) 0 else 1
            }
            BookPracticeMode.STANDARD -> rawPool.sortedBy {
                if (it.difficultyMode == BookPracticeMode.STANDARD) 0 else 1
            }
            BookPracticeMode.ADVANCED -> rawPool.sortedBy {
                if (it.difficultyMode == BookPracticeMode.ADVANCED || it.format == BookQuestionFormat.MULTIPLE_SELECT || it.format == BookQuestionFormat.NUMERICAL_INPUT) 0 else 1
            }
            BookPracticeMode.CHAPTER_EXERCISE -> rawPool.sortedBy {
                if (it.provenance == BookQuestionProvenance.ORIGINAL_TEXTBOOK) 0 else 1
            }
            BookPracticeMode.CHAPTER_TEST -> rawPool
            BookPracticeMode.MIXED_PRACTICE -> rawPool.shuffled()
            BookPracticeMode.WEAK_TOPIC -> rawPool.sortedBy { q ->
                when {
                    q.id in weakQuestionIds -> 0
                    q.format == BookQuestionFormat.NUMERICAL_INPUT || q.format == BookQuestionFormat.STEP_BY_STEP_SOLVING -> 1
                    else -> 2
                }
            }
            BookPracticeMode.FORMULA_PRACTICE -> rawPool.sortedBy { q ->
                when (q.format) {
                    BookQuestionFormat.FORMULA_IDENTIFICATION -> 0
                    BookQuestionFormat.MATCHING_PAIRS -> 1
                    BookQuestionFormat.FILL_IN_THE_BLANK -> 2
                    BookQuestionFormat.GEOMETRY_DIAGRAM -> 3
                    else -> 4
                }
            }
            BookPracticeMode.REVISION_PRACTICE -> rawPool.sortedBy { q ->
                when (q.provenance) {
                    BookQuestionProvenance.ORIGINAL_TEXTBOOK -> 0
                    BookQuestionProvenance.CONCEPT_DERIVED -> 1
                    BookQuestionProvenance.GENERATED -> 2
                }
            }
        }

        return prioritized.take(maxCount.coerceAtLeast(1))
    }

    /**
     * Evaluates a user's answer across any of the 10 question formats,
     * supporting both Bengali and English digits for numerical/text inputs.
     */
    fun evaluateQuestion(
        question: BookPracticeQuestion,
        selectedOptionIndices: Set<Int>,
        userTextInput: String
    ): Pair<Boolean, String> {
        return when (question.format) {
            BookQuestionFormat.SINGLE_CHOICE_MCQ,
            BookQuestionFormat.TRUE_FALSE,
            BookQuestionFormat.FORMULA_IDENTIFICATION,
            BookQuestionFormat.STEP_BY_STEP_SOLVING,
            BookQuestionFormat.MATCHING_PAIRS,
            BookQuestionFormat.GEOMETRY_DIAGRAM -> {
                val isCorrect = selectedOptionIndices.isNotEmpty() &&
                    selectedOptionIndices == question.correctOptionIndices
                val summary = selectedOptionIndices
                    .sorted()
                    .mapNotNull { question.optionsBn.getOrNull(it) }
                    .joinToString(", ")
                    .ifEmpty { userTextInput.trim() }
                isCorrect to summary.ifEmpty { "উত্তর দেওয়া হয়নি" }
            }

            BookQuestionFormat.MULTIPLE_SELECT -> {
                val isCorrect = selectedOptionIndices == question.correctOptionIndices
                val summary = selectedOptionIndices
                    .sorted()
                    .mapNotNull { question.optionsBn.getOrNull(it) }
                    .joinToString("; ")
                isCorrect to summary.ifEmpty { "কোনো বিকল্প নির্বাচন করা হয়নি" }
            }

            BookQuestionFormat.NUMERICAL_INPUT,
            BookQuestionFormat.FILL_IN_THE_BLANK,
            BookQuestionFormat.SHORT_ANSWER -> {
                // Allow either selecting from quick-chips (if provided) or typing text/numbers
                if (selectedOptionIndices.isNotEmpty() && userTextInput.isBlank()) {
                    val chosen = selectedOptionIndices.first()
                    val chosenText = question.optionsBn.getOrElse(chosen) { "" }
                    val isOptionCorrect = selectedOptionIndices == question.correctOptionIndices ||
                        isTextEquivalent(chosenText, question.correctTextAnswer)
                    isOptionCorrect to chosenText
                } else {
                    val trimmed = userTextInput.trim()
                    val isTextCorrect = isTextEquivalent(trimmed, question.correctTextAnswer)
                    isTextCorrect to trimmed.ifEmpty { "উত্তর দেওয়া হয়নি" }
                }
            }
        }
    }

    fun isTextEquivalent(userRaw: String, expectedRaw: String): Boolean {
        if (userRaw.isBlank() || expectedRaw.isBlank()) return false
        val uNorm = BengaliNumberFormatter.toEnglishDigits(userRaw.trim()).lowercase()
        val eNorm = BengaliNumberFormatter.toEnglishDigits(expectedRaw.trim()).lowercase()
        if (uNorm == eNorm) return true

        // Check numeric equivalence if both parse as numbers
        val uNum = uNorm.replace(",", "").toDoubleOrNull()
        val eNum = eNorm.replace(",", "").toDoubleOrNull()
        if (uNum != null && eNum != null && abs(uNum - eNum) < 0.01) {
            return true
        }
        return SharedMathEngine.isAnswerCorrect(userRaw, expectedRaw)
    }

    private fun buildMatchingPairsForChapter(chapter: BookChapterSpec): List<Pair<String, String>> {
        val pairs = mutableListOf<Pair<String, String>>()
        chapter.formulas.take(3).forEach { f ->
            pairs.add(f.titleBn to f.expressionBn)
        }
        if (pairs.size < 3) {
            pairs.add("অধ্যায় শিরোনাম" to chapter.titleBn)
        }
        if (pairs.size < 3) {
            pairs.add("পাঠ্যবই পৃষ্ঠা পরিসর" to "পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)}–${BengaliNumberFormatter.toBengaliDigits(chapter.printedPageEnd)}")
        }
        return pairs.take(3)
    }

    private fun buildGeneratedNumericalQuestion(
        bookId: BookId,
        chapter: BookChapterSpec
    ): BookPracticeQuestion {
        val ch = chapter.chapterNumber
        // Deterministic curriculum-specific numerical problem per book & chapter
        val (prompt, answerStr, steps, hint) = when (bookId) {
            BookId.CLASS_6 -> when (ch) {
                1 -> Quad("১২ এবং ১৮ এর ল.সা.গু. কত?", "36", listOf("১২ = ২ × ২ × ৩", "১৮ = ২ × ৩ × ৩", "ল.সা.গু. = ২ × ২ × ৩ × ৩ = ৩৬"), "১২ ও ১৮ এর সাধারণ ও অসাধারণ মৌলিক উৎপাদক গুণ কর।")
                2 -> Quad("২৫ টাকার ২০% সমান কত টাকা?", "5", listOf("২৫ এর ২০% = ২৫ × (২০/১০০)", "= ৫০০ / ১০০ = ৫ টাকা"), "সংখ্যাটিকে ২০/১০০ দ্বারা গুণ কর।")
                3 -> Quad("(-১৫) + (+২৫) এর মান কত?", "10", listOf("বিপরীত চিহ্নযুক্ত সংখ্যার ক্ষেত্রে বড় পরমমান থেকে ছোট পরমমান বিয়োগ হয়", "২৫ - ১৫ = ১০"), "২৫ থেকে ১৫ বিয়োগ কর।")
                4 -> Quad("x = 4 এবং y = 3 হলে 3x + 2y এর মান কত?", "18", listOf("3x + 2y = 3(4) + 2(3)", "= 12 + 6 = 18"), "x = 4 ও y = 3 বসিয়ে যোগ কর।")
                5 -> Quad("2x - 5 = 11 সমীকরণে x এর মান কত?", "8", listOf("2x = 11 + 5 = 16", "x = 16 / 2 = 8"), "পক্ষান্তর বিধি প্রয়োগ করে 5 ডানপাশে যোগ কর।")
                6 -> Quad("৫৫° কোণের পূরক কোণের মান কত ডিগ্রি?", "35", listOf("পূরক কোণদ্বয়ের সমষ্টি ৯০°", "নির্ণেয় কোণ = ৯০° - ৫৫° = ৩৫°"), "৯০° থেকে ৫৫° বিয়োগ কর।")
                7 -> Quad("৪ সেমি ব্যাসার্ধবিশিষ্ট বৃত্তের ব্যাস কত সেমি?", "8", listOf("বৃত্তের ব্যাস = ২ × ব্যাসার্ধ", "= ২ × ৪ = ৮ সেমি"), "ব্যাসার্ধকে ২ দিয়ে গুণ কর।")
                else -> Quad("১০, ১২, ১৪, ১৬, ১৮ উপাত্তগুলোর গড় কত?", "14", listOf("যোগফল = ১০ + ১২ + ১৪ + ১৬ + ১৮ = ৭০", "গড় = ৭০ ÷ ৫ = ১৪"), "সবগুলো সংখ্যা যোগ করে ৫ দিয়ে ভাগ কর।")
            }
            BookId.CLASS_7 -> when (ch) {
                1 -> Quad("২৩০৪ এর বর্গমূল কত?", "48", listOf("৪০² = ১৬০০ এবং ৫০² = ২৫০০", "একক অঙ্ক ৪ এবং ৪৮ × ৪৮ = ২৩০৪"), "ভাগ প্রক্রিয়ায় ২৩০৪ এর বর্গমূল নির্ণয় কর।")
                2 -> Quad("৫০০ টাকায় একটি পণ্য কিনে ৫৬০ টাকায় বিক্রয় করলে শতকরা কত লাভ হবে?", "12", listOf("লাভ = ৫৬০ - ৫০০ = ৬০ টাকা", "শতকরা লাভ = (৬০ / ৫০০) × ১০০ = ১২%"), "(লাভ ÷ ক্রয়মূল্য) × ১০০ সূত্র ব্যবহার কর।")
                3 -> Quad("একটি ত্রিভুজের ভূমি ১২ মিটার ও উচ্চতা ৮ মিটার হলে ক্ষেত্রফল কত বর্গমিটার?", "48", listOf("ত্রিভুজের ক্ষেত্রফল = ½ × ভূমি × উচ্চতা", "= ½ × ১২ × ৮ = ৪৮ বর্গমিটার"), "½ × ভূমি × উচ্চতা সূত্র প্রয়োগ কর।")
                4 -> Quad("12a³b² কে 3ab দ্বারা ভাগ করলে ভাগফলতে সাংখ্যিক সহগ কত হবে?", "4", listOf("12 ÷ 3 = 4", "এবং a³b² ÷ ab = a²b; সাংখ্যিক সহগ = 4"), "১২ কে ৩ দ্বারা ভাগ কর।")
                5 -> Quad("a + b = 7 এবং ab = 10 হলে a² + b² এর মান কত?", "29", listOf("a² + b² = (a + b)² - 2ab", "= 7² - 2(10) = 49 - 20 = 29"), "(a+b)² - 2ab অনুসিদ্ধান্ত ব্যবহার কর।")
                6 -> Quad("(x² - 9) কে (x + 3) দ্বারা ভাগ করলে এবং x = 10 হলে ভাগফলের মান কত?", "7", listOf("(x² - 9)/(x + 3) = (x+3)(x-3)/(x+3) = x - 3", "x = 10 বসালে 10 - 3 = 7"), "x² - 3² = (x+3)(x-3) সূত্রে উৎপাদকে বিশ্লেষণ কর।")
                7 -> Quad("3x + 4 = x + 16 সমীকরণে x এর মান কত?", "6", listOf("3x - x = 16 - 4", "2x = 12 ⇒ x = 6"), "চলকগুলোকে বামপক্ষে একত্র কর।")
                8 -> Quad("দুইটি সমান্তরাল সরলরেখাকে একটি ছেদক ছেদ করলে একান্তর কোণ একটি ৬৫° হলে অপর একান্তর কোণ কত ডিগ্রি?", "65", listOf("সমান্তরাল রেখার একান্তর কোণদ্বয় পরস্পর সমান", "সুতরাং অপর একান্তর কোণ = ৬৫°"), "একান্তর কোণগুলো পরস্পর সমান।")
                9 -> Quad("একটি ত্রিভুজের দুইটি কোণ ৫০° ও ৬০° হলে তৃতীয় কোণটি কত ডিগ্রি?", "70", listOf("ত্রিভুজের তিন কোণের সমষ্টি ১৮০°", "তৃতীয় কোণ = ১৮০° - (৫০° + ৬০°) = ৭০°"), "১৮০° থেকে দুই কোণের যোগফল বিয়োগ কর।")
                10 -> Quad("সমকোণী ত্রিভুজের অতিভুজের দৈর্ঘ্য ১৪ সেমি হলে সমকোণের বিপরীত মধ্যমার দৈর্ঘ্য কত সেমি?", "7", listOf("সমকোণী ত্রিভুজের অতিভুজের মধ্যমা অতিভুজের অর্ধেক", "= ১৪ ÷ ২ = ৭ সেমি"), "অতিভুজকে ২ দ্বারা ভাগ কর।")
                else -> Quad("কোনো উপাত্তের সর্বোচ্চ মান ৮৫ এবং সর্বনিম্ন মান ৩৬ হলে পরিসর কত?", "50", listOf("পরিসর = (সর্বোচ্চ মান - সর্বনিম্ন মান) + ১", "= (৮৫ - ৩৬) + ১ = ৪৯ + ১ = ৫০"), "বিয়োগফলের সাথে ১ যোগ কর।")
            }
            BookId.CLASS_8 -> when (ch) {
                1 -> Quad("প্রথম ১০টি ক্রমিক বিজোড় স্বাভাবিক সংখ্যার যোগফল কত?", "100", listOf("প্রথম k সংখ্যক বিজোড় স্বাভাবিক সংখ্যার যোগফল = k²", "সুতরাং ১০² = ১০০"), "সূত্র: যোগফল = k²।")
                2 -> Quad("বার্ষিক ১০% মুনাফায় ৩০০০ টাকার ২ বছরের সরল মুনাফা কত টাকা?", "600", listOf("I = Prn = ৩০০০ × (১০/১০০) × ২", "= ৬০০ টাকা"), "I = Prn সূত্র প্রয়োগ কর।")
                3 -> Quad("৪ ঘনমিটার আয়তনের একটি চৌবাচ্চায় কত লিটার পানি ধরবে?", "4000", listOf("১ ঘনমিটার = ১০০০ লিটার", "৪ ঘনমিটার = ৪ × ১০০০ = ৪০০০ লিটার"), "১ ঘনমিটার = ১০০০ লিটার।")
                4 -> Quad("a + b = 4 এবং ab = 3 হলে a³ + b³ এর মান কত?", "28", listOf("a³ + b³ = (a + b)³ - 3ab(a + b)", "= 4³ - 3 × 3 × 4 = 64 - 36 = 28"), "(a+b)³ - 3ab(a+b) অনুসিদ্ধান্তে মান বসাও।")
                5 -> Quad("x = 5 এবং y = 2 হলে (x² - y²) / (x - y) এর মান কত?", "7", listOf("(x² - y²) / (x - y) = x + y", "= 5 + 2 = 7"), "লবকে (x+y)(x-y) আকারে বিশ্লেষণ কর।")
                6 -> Quad("x + y = 12 এবং x - y = 4 হলে x এর মান কত?", "8", listOf("সমীকরণ দুটি যোগ করে পাই: 2x = 16", "অতএব x = 8"), "সমীকরণ দুটি যোগ করে ২ দিয়ে ভাগ কর।")
                7 -> Quad("একটি সেটের উপাদান সংখ্যা ৪ হলে এর মোট উপসেট সংখ্যা কতটি?", "16", listOf("উপসেট সংখ্যা = 2ⁿ", "= 2⁴ = 16"), "সূত্র 2ⁿ ব্যবহার কর।")
                8 -> Quad("একটি রম্বসের কর্ণদ্বয় ১০ সেমি ও ৮ সেমি হলে ক্ষেত্রফল কত বর্গ সেমি?", "40", listOf("রম্বসের ক্ষেত্রফল = ½ × d₁ × d₂", "= ½ × ১০ × ৮ = ৪০ বর্গ সেমি"), "কর্ণদ্বয়ের গুণফলকে ২ দিয়ে ভাগ কর।")
                9 -> Quad("সমকোণী ত্রিভুজের লম্ব ৯ সেমি এবং ভূমি ১২ সেমি হলে অতিভুজ কত সেমি?", "15", listOf("অতিভুজ = √(৯² + ১২²) = √(৮১ + ১৪৪)", "= √২২৫ = ১৫ সেমি"), "পিথাগোরাসের উপপাদ্য c = √(a² + b²) ব্যবহার কর।")
                10 -> Quad("বৃত্তের ব্যাসার্ধ ১০ সেমি এবং কেন্দ্র থেকে জ্যা-এর লম্ব দূরত্ব ৬ সেমি হলে জ্যা-টির দৈর্ঘ্য কত সেমি?", "16", listOf("জ্যা-এর অর্ধেক = √(১০² - ৬²) = √৬৪ = ৮ সেমি", "সম্পূর্ণ জ্যা = ২ × ৮ = ১৬ সেমি"), "সমকোণী ত্রিভুজ থেকে জ্যা-এর অর্ধেক বের করে দ্বিগুণ কর।")
                else -> Quad("৭২ জন শিক্ষার্থীর মধ্যে ২৪ জন ফুটবল পছন্দ করলে পাইচিত্রে তাদের কেন্দ্রীয় কোণ কত ডিগ্রি?", "120", listOf("কেন্দ্রীয় কোণ = (২৪ / ৭২) × ৩৬০°", "= (১/৩) × ৩৬০° = ১২০°"), "(উপাত্ত ÷ মোট) × ৩৬০° সূত্র প্রয়োগ কর।")
            }
            BookId.CLASS_9_10 -> when (ch) {
                1 -> Quad("0.3̇ (3 এর ওপর পৌনঃপুনিক) কে সাধারণ ভগ্নাংশে লঘিষ্ঠ আকারে প্রকাশ করলে হর কত হবে?", "3", listOf("0.3̇ = 3/9 = 1/3", "লঘিষ্ঠ ভগ্নাংশ 1/3 এর হর = 3"), "3/9 কে লঘিষ্ঠ আকারে প্রকাশ কর।")
                2 -> Quad("f(x) = x² + 2x - 3 হলে f(3) এর মান কত?", "12", listOf("f(3) = 3² + 2(3) - 3", "= 9 + 6 - 3 = 12"), "ফাংশনে x = 3 বসিয়ে হিসাব কর।")
                3 -> Quad("x + 1/x = 4 হলে x² + 1/x² এর মান কত?", "14", listOf("x² + 1/x² = (x + 1/x)² - 2", "= 4² - 2 = 16 - 2 = 14"), "(x + 1/x)² - 2 অনুসিদ্ধান্ত ব্যবহার কর।")
                4 -> Quad("log_3 81 এর মান কত?", "4", listOf("81 = 3⁴", "সুতরাং log_3 (3⁴) = 4 log_3 3 = 4"), "81 কে 3 এর ঘাত আকারে প্রকাশ কর।")
                5 -> Quad("x² - 7x + 12 = 0 সমীকরণের বৃহত্তর মূলটি কত?", "4", listOf("(x - 3)(x - 4) = 0", "মূলদ্বয় 3 এবং 4; বৃহত্তর মূল = 4"), "12 কে উৎপাদকে ভেঙে মধ্যপদ বিশ্লেষণ কর।")
                6 -> Quad("ΔABC এ AB ও AC বাহুর মধ্যবিন্দু D ও E এবং DE = 7 সেমি হলে BC বাহুর দৈর্ঘ্য কত সেমি?", "14", listOf("উপপাদ্য ১৫ অনুসারে DE = ½ BC", "অতএব BC = 2 × 7 = 14 সেমি"), "ভূমি BC মধ্যবিন্দু সংযোজক রেখাংশের দ্বিগুণ।")
                7 -> Quad("একটি সমবাহু ত্রিভুজের পরিসীমা 24 সেমি হলে এর প্রত্যেক বাহুর দৈর্ঘ্য কত সেমি?", "8", listOf("সমবাহু ত্রিভুজের তিন বাহু সমান", "এক বাহু = 24 ÷ 3 = 8 সেমি"), "পরিসীমাকে ৩ দিয়ে ভাগ কর।")
                8 -> Quad("একই চাপের ওপর দণ্ডায়মান বৃত্তস্থ কোণ 42° হলে কেন্দ্রস্থ কোণ কত ডিগ্রি?", "84", listOf("কেন্দ্রস্থ কোণ = 2 × বৃত্তস্থ কোণ", "= 2 × 42° = 84°"), "বৃত্তস্থ কোণকে ২ দ্বারা গুণ কর।")
                9 -> Quad("tan θ = 1 হলে সূক্ষ্মকোণ θ এর মান কত ডিগ্রি?", "45", listOf("আমরা জানি tan 45° = 1", "সুতরাং θ = 45°"), "কোন আদর্শ কোণের ট্যানজেন্ট মান 1 তা মনে কর।")
                10 -> Quad("ভূমির কোনো বিন্দু থেকে 15 মিটার উঁচু একটি খুঁটির চূড়ার উন্নতি কোণ 45° হলে খুঁটির পাদদেশ থেকে ঐ বিন্দুর দূরত্ব কত মিটার?", "15", listOf("tan 45° = উচ্চতা / দূরত্ব ⇒ 1 = 15 / দূরত্ব", "অতএব দূরত্ব = 15 মিটার"), "45° কোণে লম্ব ও ভূমি পরস্পর সমান।")
                11 -> Quad("3, x, 27 ক্রমিক সমানুপাতী হলে ধনাত্মক x এর মান কত?", "9", listOf("ক্রমিক সমানুপাতে x² = 3 × 27 = 81", "অতএব x = √81 = 9"), "মধ্যসমানুপাতী x = √(প্রথম × তৃতীয়)।")
                12 -> Quad("x + y = 9 এবং x - y = 5 হলে y এর মান কত?", "2", listOf("১ম সমীকরণ থেকে ২য় সমীকরণ বিয়োগ করে পাই: 2y = 4", "অতএব y = 2"), "y = (যোগফল - বিয়োগফল) ÷ ২।")
                13 -> Quad("1 + 2 + 3 + ... + 20 সমান্তর ধারাটির সমষ্টি কত?", "210", listOf("প্রথম n স্বাভাবিক সংখ্যার সমষ্টি = n(n+1)/2", "= (20 × 21) / 2 = 210"), "n(n+1)/2 সূত্রে n = 20 বসাও।")
                14 -> Quad("একটি সুষম পঞ্চভুজের ঘূর্ণন প্রতিসমতার কোণ কত ডিগ্রি?", "72", listOf("সুষম পঞ্চভুজের বাহু সংখ্যা n = 5", "ঘূর্ণন কোণ = 360° ÷ 5 = 72°"), "৩৬০° কে ৫ দ্বারা ভাগ কর।")
                15 -> Quad("একই ভূমি ও একই সমান্তরাল রেখাযুগলের মধ্যে অবস্থিত সামান্তরিকের ক্ষেত্রফল 50 বর্গ সেমি হলে ত্রিভুজক্ষেত্রের ক্ষেত্রফল কত বর্গ সেমি?", "25", listOf("উপপাদ্য ৩৭ অনুসারে ত্রিভুজক্ষেত্রের ক্ষেত্রফল সামান্তরিকের অর্ধেক", "= 50 ÷ 2 = 25 বর্গ সেমি"), "সামান্তরিকের ক্ষেত্রফলকে ২ দিয়ে ভাগ কর।")
                16 -> Quad("একটি ঘনকের এক ধারের দৈর্ঘ্য 4 সেমি হলে এর আয়তন কত ঘন সেমি?", "64", listOf("ঘনকের আয়তন = a³", "= 4³ = 64 ঘন সেমি"), "বাহুর দৈর্ঘ্যকে ঘন (Cube) কর।")
                else -> Quad("কোনো শ্রেণির নিম্নসীমা 40 এবং উচ্চসীমা 50 হলে শ্রেণির মধ্যমান কত?", "45", listOf("শ্রেণির মধ্যমান = (নিম্নসীমা + উচ্চসীমা) / 2", "= (40 + 50) / 2 = 45"), "সীমা দুইটির যোগফলকে ২ দিয়ে ভাগ কর।")
            }
        }

        val numVal = answerStr.toIntOrNull() ?: 10
        val options = listOf(
            BengaliNumberFormatter.toBengaliDigits(numVal),
            BengaliNumberFormatter.toBengaliDigits(numVal + 2),
            BengaliNumberFormatter.toBengaliDigits((numVal - 3).coerceAtLeast(1)),
            BengaliNumberFormatter.toBengaliDigits(numVal * 2)
        )

        return BookPracticeQuestion(
            id = "${bookId.id}_c${ch}_num_1",
            bookId = bookId,
            chapterNumber = ch,
            lessonId = chapter.lessons.firstOrNull()?.id,
            format = BookQuestionFormat.NUMERICAL_INPUT,
            difficultyMode = BookPracticeMode.STANDARD,
            provenance = BookQuestionProvenance.GENERATED,
            promptBn = prompt,
            optionsBn = options,
            correctOptionIndices = setOf(0),
            correctTextAnswer = answerStr,
            hintBn = hint,
            stepByStepSolutionBn = steps,
            explanationBn = steps.joinToString(" ⇒ ")
        )
    }

    private fun buildFillInTheBlankQuestion(
        bookId: BookId,
        chapter: BookChapterSpec
    ): BookPracticeQuestion {
        val chNumBn = BengaliNumberFormatter.toBengaliDigits(chapter.chapterNumber)
        val firstFormula = chapter.formulas.firstOrNull()
        val prompt = if (firstFormula != null) {
            "শূন্যস্থান পূরণ কর: ${firstFormula.titleBn} অনুযায়ী সম্পর্কটি হলো ______।"
        } else {
            "শূন্যস্থান পূরণ কর: ${bookId.titleBn}-এর অধ্যায় $chNumBn-এর নাম হলো ______।"
        }
        val answer = firstFormula?.expressionBn ?: chapter.titleBn
        val options = listOf(
            answer,
            "অসংজ্ঞায়িত রাশি",
            "শূন্য (০)",
            "অসীম (∞)"
        )
        return BookPracticeQuestion(
            id = "${bookId.id}_c${chapter.chapterNumber}_fib_1",
            bookId = bookId,
            chapterNumber = chapter.chapterNumber,
            lessonId = chapter.lessons.firstOrNull()?.id,
            format = BookQuestionFormat.FILL_IN_THE_BLANK,
            difficultyMode = BookPracticeMode.BASIC,
            provenance = BookQuestionProvenance.CONCEPT_DERIVED,
            promptBn = prompt,
            optionsBn = options,
            correctOptionIndices = setOf(0),
            correctTextAnswer = answer,
            hintBn = firstFormula?.explanationBn ?: chapter.summaryBn,
            stepByStepSolutionBn = listOf(
                "পাঠ্যবইয়ের অধ্যায় $chNumBn (${chapter.titleBn}) অনুসারে শূন্যস্থানে বসবে: $answer"
            ),
            explanationBn = "সঠিক উত্তর: $answer"
        )
    }

    private fun buildShortAnswerQuestion(
        bookId: BookId,
        chapter: BookChapterSpec
    ): BookPracticeQuestion {
        val ex = chapter.workedExamples.firstOrNull()
        val prompt = if (ex != null) {
            "সংক্ষিপ্ত উত্তর দাও: ${ex.problemStatementBn}"
        } else {
            "সংক্ষিপ্ত উত্তর দাও: ‘${chapter.titleBn}’ অধ্যায়টি পাঠ্যবইয়ের কত পৃষ্ঠা থেকে শুরু হয়েছে?"
        }
        val answer = ex?.finalAnswerBn ?: BengaliNumberFormatter.toBengaliDigits(chapter.printedPageStart)
        val options = listOf(
            answer,
            "১",
            "০",
            "নির্ণয় করা সম্ভব নয়"
        )
        return BookPracticeQuestion(
            id = "${bookId.id}_c${chapter.chapterNumber}_sa_1",
            bookId = bookId,
            chapterNumber = chapter.chapterNumber,
            lessonId = chapter.lessons.firstOrNull()?.id,
            format = BookQuestionFormat.SHORT_ANSWER,
            difficultyMode = BookPracticeMode.CHAPTER_EXERCISE,
            provenance = BookQuestionProvenance.ORIGINAL_TEXTBOOK,
            promptBn = prompt,
            optionsBn = options,
            correctOptionIndices = setOf(0),
            correctTextAnswer = answer,
            hintBn = ex?.stepsBn?.firstOrNull() ?: "সূচিপত্র ও অধ্যায়ের প্রারম্ভিক পৃষ্ঠা দেখ।",
            stepByStepSolutionBn = ex?.stepsBn ?: listOf("অধ্যায়টি পাঠ্যবইয়ের পৃষ্ঠা $answer থেকে শুরু হয়েছে।"),
            explanationBn = "নির্ণেয় সংক্ষিপ্ত উত্তর: $answer",
            sourcePrintedPage = ex?.printedPage ?: chapter.printedPageStart
        )
    }

    private fun buildDiagramQuestion(
        bookId: BookId,
        chapter: BookChapterSpec
    ): BookPracticeQuestion {
        val labType = chapter.linkedLabTypes.firstOrNull() ?: BookInteractiveLabType.NUMBER_LINE_LAB
        val th = chapter.theoremsOrConstructions.firstOrNull()
        val prompt = if (th != null) {
            "চিত্রভিত্তিক জ্যামিতিক বিশ্লেষণ (${th.titleBn}): নিচের কোন জ্যামিতিক সিদ্ধান্তটি সঠিক?"
        } else {
            "চিত্র ও ল্যাব বিশ্লেষণ (${labType.titleBn}): ‘${chapter.titleBn}’ অধ্যায়ের জ্যামিতিক/সংখ্যারেখা চিত্রে কোন বৈশিষ্ট্যটি ফুটে ওঠে?"
        }
        val correctStatement = th?.statementBn ?: chapter.summaryBn
        return BookPracticeQuestion(
            id = "${bookId.id}_c${chapter.chapterNumber}_diag_1",
            bookId = bookId,
            chapterNumber = chapter.chapterNumber,
            lessonId = chapter.lessons.firstOrNull()?.id,
            format = BookQuestionFormat.GEOMETRY_DIAGRAM,
            difficultyMode = BookPracticeMode.ADVANCED,
            provenance = if (th != null) BookQuestionProvenance.ORIGINAL_TEXTBOOK else BookQuestionProvenance.CONCEPT_DERIVED,
            promptBn = prompt,
            optionsBn = listOf(
                correctStatement,
                "চিত্রের সকল বাহু ও কোণ সর্বদা শূন্য ডিগ্রি হয়",
                "চিত্রে কোনো গাণিতিক অনুপাত বা প্রতিসমতা বজায় থাকে না",
                "কেবল ঋণাত্মক দৈর্ঘ্যের ক্ষেত্রেই চিত্রটি প্রযোজ্য"
            ),
            correctOptionIndices = setOf(0),
            correctTextAnswer = correctStatement,
            hintBn = "${labType.titleBn} ব্যবহার করে চিত্রের বাহু, কোণ বা সংখ্যারেখার পরিবর্তন পর্যবেক্ষণ কর।",
            stepByStepSolutionBn = th?.stepsBn ?: listOf(
                "চিত্রভিত্তিক বিশ্লেষণ থেকে দেখা যায়:",
                correctStatement
            ),
            explanationBn = correctStatement,
            diagramTag = labType.id,
            sourcePrintedPage = th?.printedPage
        )
    }

    private data class Quad(
        val first: String,
        val second: String,
        val third: List<String>,
        val fourth: String
    )
}
