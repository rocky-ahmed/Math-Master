package com.example.math

enum class BookId(
    val id: String,
    val classLabelBn: String,
    val displayTitleBn: String,
    val subtitleBn: String,
    val assetPdfPath: String,
    val totalPdfPages: Int,
    val totalPrintedPages: Int,
    val pdfPageOffset: Int,
    val answerKeyStartPrintedPage: Int,
    val answerKeyEndPrintedPage: Int,
    val totalChaptersCount: Int,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val badgeBn: String
) {
    CLASS_6(
        id = "class_6",
        classLabelBn = "ষষ্ঠ শ্রেণি",
        displayTitleBn = "ষষ্ঠ শ্রেণির গণিত",
        subtitleBn = "পাটিগণিত, বীজগণিত, জ্যামিতি ও তথ্য-উপাত্ত (৮টি অধ্যায়)",
        assetPdfPath = "books/class_6.pdf",
        totalPdfPages = 161,
        totalPrintedPages = 156,
        pdfPageOffset = 5,
        answerKeyStartPrintedPage = 150,
        answerKeyEndPrintedPage = 155,
        totalChaptersCount = 8,
        primaryColorHex = "#059669",
        secondaryColorHex = "#10B981",
        badgeBn = "৮টি অধ্যায় • ১৬১ পৃষ্ঠা"
    ),
    CLASS_7(
        id = "class_7",
        classLabelBn = "সপ্তম শ্রেণি",
        displayTitleBn = "সপ্তম শ্রেণির গণিত",
        subtitleBn = "মূলদ-অমূলদ, সমানুপাত, বীজগণিত, ত্রিভুজ ও উপাত্ত (১১টি অধ্যায়)",
        assetPdfPath = "books/class_7.pdf",
        totalPdfPages = 181,
        totalPrintedPages = 176,
        pdfPageOffset = 5,
        answerKeyStartPrintedPage = 170,
        answerKeyEndPrintedPage = 175,
        totalChaptersCount = 11,
        primaryColorHex = "#2563EB",
        secondaryColorHex = "#4F46E5",
        badgeBn = "১১টি অধ্যায় • ১৮১ পৃষ্ঠা"
    ),
    CLASS_8(
        id = "class_8",
        classLabelBn = "অষ্টম শ্রেণি",
        displayTitleBn = "অষ্টম শ্রেণির গণিত",
        subtitleBn = "প্যাটার্ন, মুনাফা, সেট, চতুর্ভুজ, পিথাগোরাস ও বৃত্ত (১১টি অধ্যায়)",
        assetPdfPath = "books/class_8.pdf",
        totalPdfPages = 190,
        totalPrintedPages = 185,
        pdfPageOffset = 5,
        answerKeyStartPrintedPage = 175,
        answerKeyEndPrintedPage = 184,
        totalChaptersCount = 11,
        primaryColorHex = "#D97706",
        secondaryColorHex = "#EA580C",
        badgeBn = "১১টি অধ্যায় • ১৯০ পৃষ্ঠা"
    ),
    CLASS_9_10(
        id = "class_9_10",
        classLabelBn = "নবম-দশম শ্রেণি",
        displayTitleBn = "নবম ও দশম শ্রেণির গণিত",
        subtitleBn = "বাস্তব সংখ্যা, সেট-ফাংশন, বীজগণিত, জ্যামিতি, ত্রিকোণমিতি, পরিমিতি ও পরিসংখ্যান (১৭টি অধ্যায়)",
        assetPdfPath = "books/class_9_10.pdf",
        totalPdfPages = 362,
        totalPrintedPages = 357,
        pdfPageOffset = 5,
        answerKeyStartPrintedPage = 345,
        answerKeyEndPrintedPage = 354,
        totalChaptersCount = 17,
        primaryColorHex = "#BE123C",
        secondaryColorHex = "#E11D48",
        badgeBn = "১৭টি অধ্যায় • ৩৬২ পৃষ্ঠা"
    );

    val titleBn: String get() = displayTitleBn
    val shortLabelBn: String get() = classLabelBn
    val classLevelBn: String get() = classLabelBn
    val totalChapters: Int get() = totalChaptersCount
    val maxPrintedPage: Int get() = totalPrintedPages
    val assetFileName: String get() = assetPdfPath

    val editionMetadata: BookEditionMetadata
        get() = when (this) {
            CLASS_6 -> BookEditionMetadata(
                editionId = "NCTB_2026_CLASS_6",
                academicYearBn = "২০২৬ শিক্ষাবর্ষ (সংশোধিত সংস্করণ)",
                curriculumRegimeBn = "জাতীয় শিক্ষাক্রম ২০১২ (২০২৫–২০২৬ পুনর্বহালকৃত অনুশীলনী ও সৃজনশীল কাঠামো)",
                publisherBn = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB), বাংলাদেশ",
                verificationBadgeBn = "১০০% পৃষ্ঠা-ম্যাপকৃত ও অফলাইন যাচাইকৃত",
                offlineModeNoteBn = "হাইব্রিড রিডার: অফলাইন অপ্টিমাইজড PDF + পূর্ণাঙ্গ নেটিভ বাংলা টেক্সট ও সূত্র রেন্ডারার"
            )
            CLASS_7 -> BookEditionMetadata(
                editionId = "NCTB_2026_CLASS_7",
                academicYearBn = "২০২৬ শিক্ষাবর্ষ (সংশোধিত সংস্করণ)",
                curriculumRegimeBn = "জাতীয় শিক্ষাক্রম ২০১২ (২০২৫–২০২৬ পুনর্বহালকৃত অনুশীলনী ও উপপাদ্য কাঠামো)",
                publisherBn = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB), বাংলাদেশ",
                verificationBadgeBn = "১০০% পৃষ্ঠা-ম্যাপকৃত ও অফলাইন যাচাইকৃত",
                offlineModeNoteBn = "হাইব্রিড রিডার: অফলাইন অপ্টিমাইজড PDF + পূর্ণাঙ্গ নেটিভ বাংলা টেক্সট ও সূত্র রেন্ডারার"
            )
            CLASS_8 -> BookEditionMetadata(
                editionId = "NCTB_2026_CLASS_8",
                academicYearBn = "২০২৬ শিক্ষাবর্ষ (সংশোধিত সংস্করণ)",
                curriculumRegimeBn = "জাতীয় শিক্ষাক্রম ২০১২ (২০২৫–২০২৬ পুনর্বহালকৃত অনুশীলনী ও জ্যামিতি কাঠামো)",
                publisherBn = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB), বাংলাদেশ",
                verificationBadgeBn = "১০০% পৃষ্ঠা-ম্যাপকৃত ও অফলাইন যাচাইকৃত",
                offlineModeNoteBn = "হাইব্রিড রিডার: অফলাইন অপ্টিমাইজড PDF + পূর্ণাঙ্গ নেটিভ বাংলা টেক্সট ও সূত্র রেন্ডারার"
            )
            CLASS_9_10 -> BookEditionMetadata(
                editionId = "NCTB_2026_CLASS_9_10",
                academicYearBn = "২০২৬ শিক্ষাবর্ষ (এসএসসি সাধারণ গণিত)",
                curriculumRegimeBn = "জাতীয় শিক্ষাক্রম ২০১২ (২০২৫–২০২৬ পুনর্বহালকৃত ১৭ অধ্যায় পূর্ণাঙ্গ সংস্করণ)",
                publisherBn = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB), বাংলাদেশ",
                verificationBadgeBn = "১০০% পৃষ্ঠা-ম্যাপকৃত • বিসিএস ভিত্তি সংযুক্ত",
                offlineModeNoteBn = "হাইব্রিড রিডার: অফলাইন অপ্টিমাইজড PDF + পূর্ণাঙ্গ নেটিভ বাংলা টেক্সট ও সূত্র রেন্ডারার"
            )
        }

    fun printedToPdfPage(printedPage: Int): Int =
        (printedPage + pdfPageOffset).coerceIn(1, totalPdfPages)

    fun pdfToPrintedPage(pdfPage: Int): Int =
        (pdfPage - pdfPageOffset).coerceIn(1, totalPrintedPages)

    companion object {
        fun fromId(id: String): BookId =
            values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CLASS_6
    }
}

data class BookEditionMetadata(
    val editionId: String,
    val academicYearBn: String,
    val curriculumRegimeBn: String,
    val publisherBn: String,
    val verificationBadgeBn: String,
    val offlineModeNoteBn: String
)

enum class BookInteractiveLabType(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val supportedClassesBn: String
) {
    NUMBER_SYSTEM_EXPLORER(
        id = "lab_number_system",
        titleBn = "সংখ্যা পদ্ধতি ও সংখ্যারেখা ল্যাব",
        subtitleBn = "স্থানীয় মান, মৌলিক উৎপাদক, গ.সা.গু./ল.সা.গু., সংখ্যারেখা ও মূলদ-অমূলদ সংখ্যা",
        supportedClassesBn = "৬ষ্ঠ, ৭ম ও ৯ম-১০ম শ্রেণি"
    ),
    FRACTION_AND_RATIO_LAB(
        id = "lab_fraction_ratio",
        titleBn = "ভগ্নাংশ ও অনুপাত ভিজ্যুয়াল ল্যাব",
        subtitleBn = "সমতুল ভগ্নাংশ, ভগ্নাংশের তুলনা, অনুপাত, সমানুপাত ও যোজন-বিয়োজন",
        supportedClassesBn = "৬ষ্ঠ, ৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    PROFIT_LOSS_PERCENTAGE_CALC(
        id = "lab_profit_loss_interest",
        titleBn = "লাভ-ক্ষতি, শতকরা ও মুনাফা ক্যালকুলেটর",
        subtitleBn = "শতকরা, লাভ-ক্ষতি, বাট্টা, ভ্যাট, সরল মুনাফা (I=Prn) ও চক্রবৃদ্ধি মুনাফা",
        supportedClassesBn = "৬ষ্ঠ, ৭ম ও ৮ম শ্রেণি"
    ),
    PATTERN_AND_SEQUENCE_LAB(
        id = "lab_pattern_sequence",
        titleBn = "প্যাটার্ন ও সসীম ধারা ল্যাব",
        subtitleBn = "ফিবোনাক্কি, ম্যাজিক বর্গ, জ্যামিতিক প্যাটার্ন, সমান্তর ও গুণোত্তর ধারা",
        supportedClassesBn = "৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    ALGEBRA_INTERACTIVE_LAB(
        id = "lab_algebra",
        titleBn = "বীজগাণিতিক রাশি ও সূত্র ল্যাব",
        subtitleBn = "সহগ-ঘাত বিশ্লেষণ, রাশির মান নির্ণয়, বর্গ-ঘনের সূত্র, উৎপাদক ও সূচক-লগারিদম",
        supportedClassesBn = "৬ষ্ঠ, ৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    EQUATION_SOLVER_VISUALIZER(
        id = "lab_equation_solver",
        titleBn = "সমীকরণ ও সহসমীকরণ সমাধান ভিজ্যুয়ালাইজার",
        subtitleBn = "এক চলকবিশিষ্ট সমীকরণ, দ্বিঘাত সমীকরণ এবং প্রতিস্থাপন/অপনয়ন/আড়গুণন সহসমীকরণ",
        supportedClassesBn = "৬ষ্ঠ, ৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    GEOMETRY_INTERACTIVE_LAB(
        id = "lab_geometry",
        titleBn = "জ্যামিতি, উপপাদ্য ও সম্পাদ্য ল্যাব",
        subtitleBn = "কোণ, সমান্তরাল রেখা, ত্রিভুজ, চতুর্ভুজ, বৃত্ত এবং ধাপে ধাপে উপপাদ্য ও সম্পাদ্য",
        supportedClassesBn = "৬ষ্ঠ, ৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    PYTHAGORAS_THEOREM_VISUALIZER(
        id = "lab_pythagoras",
        titleBn = "পিথাগোরাসের উপপাদ্য ভিজ্যুয়ালাইজার",
        subtitleBn = "সমকোণী ত্রিভুজের ক্ষেত্রফল প্রমাণ (a² + b² = c²) ও অজানা বাহু নির্ণয়",
        supportedClassesBn = "৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    TRIGONOMETRY_HEIGHT_DISTANCE_LAB(
        id = "lab_trigonometry",
        titleBn = "ত্রিকোণমিতি ও দূরত্ব-উচ্চতা ল্যাব",
        subtitleBn = "sin, cos, tan অনুপাত, আদর্শ কোণের মান (0°–90°), উন্নতি ও অবনতি কোণ সিমুলেটর",
        supportedClassesBn = "৯ম-১০ম শ্রেণি"
    ),
    MENSURATION_CALCULATOR(
        id = "lab_mensuration",
        titleBn = "পরিমাপ ও পরিমিতি ক্যালকুলেটর",
        subtitleBn = "আয়ত, বর্গ, ত্রিভুজ, ট্রাপিজিয়াম, বৃত্তকলা, বেলন, কোণক ও গোলকের ক্ষেত্রফল-আয়তন",
        supportedClassesBn = "৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    DATA_AND_STATISTICS_LAB(
        id = "lab_statistics",
        titleBn = "তথ্য, উপাত্ত ও পরিসংখ্যান ল্যাব",
        subtitleBn = "ট্যালি ও গণসংখ্যা সারণি, গড়, মধ্যক, প্রচুরক, আয়তলেখ, পাইচিত্র ও অজিভ রেখা",
        supportedClassesBn = "৬ষ্ঠ, ৭ম, ৮ম ও ৯ম-১০ম শ্রেণি"
    ),
    SET_AND_FUNCTION_VISUALIZER(
        id = "lab_set_function",
        titleBn = "সেট, ভেনচিত্র ও ফাংশন ভিজ্যুয়ালাইজার",
        subtitleBn = "সংযোগ (A∪B), ছেদ (A∩B), অন্তর (A\\B), পূরক (A'), শক্তি সেট ও ডোমেন-রেঞ্জ",
        supportedClassesBn = "৮ম ও ৯ম-১০ম শ্রেণি"
    );

    companion object {
        val NUMBER_LINE_LAB: BookInteractiveLabType get() = NUMBER_SYSTEM_EXPLORER
        val PRIME_FACTOR_SIEVE_LAB: BookInteractiveLabType get() = FRACTION_AND_RATIO_LAB
        val PRIME_FACTORIZATION_SIEVE: BookInteractiveLabType get() = NUMBER_SYSTEM_EXPLORER
        val RATIO_PROPORTION_SCALER: BookInteractiveLabType get() = FRACTION_AND_RATIO_LAB
        val ALGEBRA_IDENTITY_LAB: BookInteractiveLabType get() = ALGEBRA_INTERACTIVE_LAB
        val EQUATION_BALANCER_LAB: BookInteractiveLabType get() = EQUATION_SOLVER_VISUALIZER
        val GEOMETRY_CANVAS_LAB: BookInteractiveLabType get() = GEOMETRY_INTERACTIVE_LAB
        val PROFIT_INTEREST_LAB: BookInteractiveLabType get() = PROFIT_LOSS_PERCENTAGE_CALC
        val SET_VENN_LAB: BookInteractiveLabType get() = SET_AND_FUNCTION_VISUALIZER
        val CIRCLE_THEOREM_LAB: BookInteractiveLabType get() = PYTHAGORAS_THEOREM_VISUALIZER
        val CIRCLE_CHORD_TANGENT_THEOREM: BookInteractiveLabType get() = GEOMETRY_INTERACTIVE_LAB
        val TRIG_UNIT_CIRCLE_LAB: BookInteractiveLabType get() = TRIGONOMETRY_HEIGHT_DISTANCE_LAB
        val TRIGONOMETRY_UNIT_CIRCLE: BookInteractiveLabType get() = TRIGONOMETRY_HEIGHT_DISTANCE_LAB
        val COORDINATE_GRAPH_LAB: BookInteractiveLabType get() = EQUATION_SOLVER_VISUALIZER
        val COORDINATE_PLANE_EXPLORER: BookInteractiveLabType get() = EQUATION_SOLVER_VISUALIZER
        val SERIES_PROGRESSION_LAB: BookInteractiveLabType get() = PATTERN_AND_SEQUENCE_LAB
        val MENSURATION_3D_LAB: BookInteractiveLabType get() = MENSURATION_CALCULATOR
        val MENSURATION_3D_SURFACE_VOLUME: BookInteractiveLabType get() = MENSURATION_CALCULATOR
        val STATISTICS_HISTOGRAM_LAB: BookInteractiveLabType get() = DATA_AND_STATISTICS_LAB
    }
}

data class BookFormulaSpec(
    val id: String,
    val titleBn: String,
    val expressionBn: String,
    val variablesExplanationBn: String,
    val exampleApplicationBn: String = ""
) {
    val explanationBn: String
        get() = if (exampleApplicationBn.isNotBlank()) {
            "$variablesExplanationBn (উদাহরণ: $exampleApplicationBn)"
        } else {
            variablesExplanationBn
        }
}

data class BookWorkedExample(
    val id: String,
    val titleBn: String,
    val problemStatementBn: String,
    val stepsBn: List<String>,
    val finalAnswerBn: String,
    val conceptNoteBn: String = "",
    val printedPageRef: Int = 1
) {
    constructor(
        id: String,
        titleBn: String,
        problemStatementBn: String,
        stepsBn: List<String>,
        finalAnswerBn: String,
        printedPage: Int
    ) : this(
        id = id,
        titleBn = titleBn,
        problemStatementBn = problemStatementBn,
        stepsBn = stepsBn,
        finalAnswerBn = finalAnswerBn,
        conceptNoteBn = "",
        printedPageRef = printedPage
    )

    val printedPage: Int get() = printedPageRef
}

data class BookTheoremOrConstruction(
    val id: String,
    val typeLabelBn: String,
    val numberLabelBn: String,
    val statementBn: String,
    val givenDataBn: String = "",
    val stepsOrProofBn: List<String> = emptyList(),
    val conclusionBn: String = "",
    val printedPageRef: Int = 1
) {
    constructor(
        id: String,
        titleBn: String,
        typeLabelBn: String,
        statementBn: String,
        stepsBn: List<String>,
        printedPage: Int
    ) : this(
        id = id,
        typeLabelBn = typeLabelBn,
        numberLabelBn = titleBn,
        statementBn = statementBn,
        givenDataBn = "",
        stepsOrProofBn = stepsBn,
        conclusionBn = "",
        printedPageRef = printedPage
    )

    val titleBn: String get() = numberLabelBn
    val stepsBn: List<String> get() = stepsOrProofBn
    val printedPage: Int get() = printedPageRef
}

data class BookLessonSection(
    val lessonId: String,
    val sectionCodeBn: String,
    val titleBn: String,
    val printedPage: Int,
    val printedPageEndValue: Int = printedPage,
    val conceptExplanationBn: String,
    val keyDefinitionsBn: List<String>,
    val keyRulesBn: List<String>,
    val visualDiagramNoteBn: String = "",
    val workedExampleTitleBn: String = "",
    val workedExampleStepsBn: List<String> = emptyList(),
    val workedExampleAnswerBn: String = "",
    val practicalApplicationBn: String = "",
    val quickMemoryTipsListBn: List<String> = emptyList(),
    val commonMistakesListBn: List<String> = emptyList(),
    val miniCheckQuestionBn: String,
    val miniCheckOptionsBn: List<String>,
    val miniCheckCorrectIndex: Int,
    val miniCheckExplanationBn: String
) {
    constructor(
        id: String,
        code: String,
        titleBn: String,
        printedPageStart: Int,
        printedPageEnd: Int,
        conceptExplanationBn: String,
        keyPointsBn: List<String>,
        stepByStepRulesBn: List<String>,
        quickMemoryTipsBn: List<String>,
        commonMistakesBn: List<String>,
        miniCheckQuestionBn: String,
        miniCheckOptionsBn: List<String>,
        miniCheckCorrectIndex: Int,
        miniCheckExplanationBn: String
    ) : this(
        lessonId = id,
        sectionCodeBn = code,
        titleBn = titleBn,
        printedPage = printedPageStart,
        printedPageEndValue = printedPageEnd,
        conceptExplanationBn = conceptExplanationBn,
        keyDefinitionsBn = keyPointsBn,
        keyRulesBn = stepByStepRulesBn,
        quickMemoryTipsListBn = quickMemoryTipsBn,
        commonMistakesListBn = commonMistakesBn,
        miniCheckQuestionBn = miniCheckQuestionBn,
        miniCheckOptionsBn = miniCheckOptionsBn,
        miniCheckCorrectIndex = miniCheckCorrectIndex,
        miniCheckExplanationBn = miniCheckExplanationBn
    )

    val id: String get() = lessonId
    val code: String get() = sectionCodeBn.removePrefix("পাঠ").trim()
    val printedPageStart: Int get() = printedPage
    val printedPageEnd: Int get() = printedPageEndValue
    val keyPointsBn: List<String> get() = keyDefinitionsBn
    val stepByStepRulesBn: List<String> get() = keyRulesBn
    val quickMemoryTipsBn: List<String>
        get() = if (quickMemoryTipsListBn.isNotEmpty()) {
            quickMemoryTipsListBn
        } else {
            listOfNotNull(practicalApplicationBn.takeIf { it.isNotBlank() }, visualDiagramNoteBn.takeIf { it.isNotBlank() })
        }
    val commonMistakesBn: List<String> get() = commonMistakesListBn
}

data class BookChapterSpec(
    val bookId: BookId,
    val chapterNumber: Int,
    val chapterNumeralBn: String,
    val titleBn: String,
    val alternateTitleBn: String = "",
    val printedStartPage: Int,
    val printedEndPage: Int,
    val linkedLab: BookInteractiveLabType,
    val extraLinkedLabs: List<BookInteractiveLabType> = emptyList(),
    val introductionBn: String,
    val learningObjectivesBn: List<String>,
    val prerequisitesBn: List<String> = emptyList(),
    val coreExplanationBn: String = "",
    val definitionsBn: List<Pair<String, String>> = emptyList(),
    val mathematicalRulesBn: List<String> = emptyList(),
    val formulas: List<BookFormulaSpec>,
    val visualIllustrationSummaryBn: String = "",
    val workedExamples: List<BookWorkedExample>,
    val theoremsAndConstructions: List<BookTheoremOrConstruction> = emptyList(),
    val practicalApplicationsBn: List<String> = emptyList(),
    val commonMistakesBn: List<String> = emptyList(),
    val pointsToRememberBn: List<String> = emptyList(),
    val quickRevisionSummaryBn: List<String> = emptyList(),
    val subsections: List<BookLessonSection>,
    val exercisesListBn: List<String>,
    val answerKeySummaryBn: List<String> = emptyList()
) {
    constructor(
        bookId: BookId,
        chapterNumber: Int,
        titleBn: String,
        titleEn: String,
        printedPageStart: Int,
        printedPageEnd: Int,
        summaryBn: String,
        objectivesBn: List<String>,
        formulas: List<BookFormulaSpec>,
        workedExamples: List<BookWorkedExample>,
        theoremsOrConstructions: List<BookTheoremOrConstruction>,
        lessons: List<BookLessonSection>,
        exerciseNamesBn: List<String>,
        linkedLabTypes: List<BookInteractiveLabType>
    ) : this(
        bookId = bookId,
        chapterNumber = chapterNumber,
        chapterNumeralBn = "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(chapterNumber)}",
        titleBn = titleBn,
        alternateTitleBn = titleEn,
        printedStartPage = printedPageStart,
        printedEndPage = printedPageEnd,
        linkedLab = linkedLabTypes.firstOrNull() ?: BookInteractiveLabType.NUMBER_SYSTEM_EXPLORER,
        extraLinkedLabs = linkedLabTypes,
        introductionBn = summaryBn,
        learningObjectivesBn = objectivesBn,
        formulas = formulas,
        workedExamples = workedExamples,
        theoremsAndConstructions = theoremsOrConstructions,
        subsections = lessons,
        exercisesListBn = exerciseNamesBn
    )

    val pdfStartPage: Int get() = bookId.printedToPdfPage(printedStartPage)
    val pdfEndPage: Int get() = bookId.printedToPdfPage(printedEndPage)
    val chapterKey: String get() = "${bookId.id}_ch_$chapterNumber"

    val titleEn: String get() = alternateTitleBn.ifBlank { chapterNumeralBn }
    val printedPageStart: Int get() = printedStartPage
    val printedPageEnd: Int get() = printedEndPage
    val summaryBn: String get() = introductionBn
    val objectivesBn: List<String> get() = learningObjectivesBn
    val theoremsOrConstructions: List<BookTheoremOrConstruction> get() = theoremsAndConstructions
    val lessons: List<BookLessonSection> get() = subsections
    val exerciseNamesBn: List<String> get() = exercisesListBn
    val linkedLabTypes: List<BookInteractiveLabType>
        get() = if (extraLinkedLabs.isNotEmpty()) extraLinkedLabs else listOf(linkedLab)
}

enum class BookQuestionFormat(
    val id: String,
    val labelBn: String
) {
    SINGLE_CHOICE_MCQ("MCQ", "বহুনির্বাচনি (MCQ)"),
    MULTIPLE_SELECT("MULTI_SELECT", "একাধিক সঠিক উত্তর নির্বাচন"),
    TRUE_FALSE("TRUE_FALSE", "সত্য / মিথ্যা নির্ণয়"),
    FILL_IN_THE_BLANK("FILL_BLANK", "শূন্যস্থান পূরণ"),
    NUMERICAL_INPUT("NUMERICAL", "সংখ্যাভিত্তিক উত্তর"),
    SHORT_ANSWER("SHORT_ANSWER", "সংক্ষিপ্ত উত্তর"),
    MATCHING_PAIRS("MATCHING", "বাম-ডান মিলকরণ"),
    FORMULA_IDENTIFICATION("FORMULA_ID", "সূত্র শনাক্তকরণ"),
    STEP_BY_STEP_SOLVING("STEP_SOLVING", "ধাপে ধাপে সমস্যা সমাধান"),
    GEOMETRY_DIAGRAM("GEOMETRY", "জ্যামিতিক চিত্র ও উপপাদ্য ভিত্তিক")
}

enum class BookPracticeMode(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val defaultQuestionCount: Int
) {
    BASIC("BASIC", "মৌলিক অনুশীলন (Basic)", "সংজ্ঞা, সূত্র ও সহজ ধারণা যাচাই", 8),
    STANDARD("STANDARD", "আদর্শ অনুশীলন (Standard)", "পাঠ্যবইয়ের উদাহরণ ও নিয়মিত অঙ্ক", 10),
    ADVANCED("ADVANCED", "উন্নত অনুশীলন (Advanced)", "বহু-ধাপ ও সৃজনশীল গাণিতিক সমস্যা", 10),
    CHAPTER_EXERCISE("CHAPTER_EXERCISE", "অনুশীলনীর প্রশ্ন (Chapter Exercise)", "মূল পাঠ্যবইয়ের অনুশীলনীর হুবহু ও সমমানের প্রশ্ন", 12),
    CHAPTER_TEST("CHAPTER_TEST", "অধ্যায়ভিত্তিক পরীক্ষা (Chapter Test)", "সময়ভিত্তিক পূর্ণাঙ্গ অধ্যায় মূল্যায়ন", 15),
    MIXED_PRACTICE("MIXED_PRACTICE", "মিশ্র অনুশীলন (Mixed Practice)", "সকল প্রশ্ন ধরনের সমন্বিত অনুশীলন", 12),
    WEAK_TOPIC("WEAK_TOPIC", "দুর্বল টপিক ও ভুল সংশোধন (Weak-Topic)", "পূর্বে ভুল করা ও কম নির্ভুলতার প্রশ্নের লক্ষ্যভিত্তিক চর্চা", 10),
    FORMULA_PRACTICE("FORMULA_PRACTICE", "সূত্র ও উপপাদ্য অনুশীলন (Formula Practice)", "অধ্যায়ের সকল গাণিতিক সূত্র ও উপপাদ্য শনাক্তকরণ", 10),
    REVISION_PRACTICE("REVISION_PRACTICE", "দ্রুত রিভিশন অনুশীলন (Revision Practice)", "পরীক্ষার পূর্বে ১/৩/৭/১৪ দিনের স্পেসড রিভিশন ডেক", 10)
}

enum class BookQuestionProvenance(
    val id: String,
    val badgeBn: String,
    val descriptionBn: String
) {
    ORIGINAL_TEXTBOOK(
        id = "ORIGINAL_TEXTBOOK",
        badgeBn = "মূল পাঠ্যবইয়ের প্রশ্ন",
        descriptionBn = "পাঠ্যবইয়ের অনুশীলনী বা উদাহরণ থেকে সরাসরি গৃহীত"
    ),
    CONCEPT_DERIVED(
        id = "CONCEPT_DERIVED",
        badgeBn = "পাঠ্যবইয়ের ধারণাভিত্তিক অনুশীলন",
        descriptionBn = "পাঠ্যবইয়ের নিয়ম ও সূত্রের আলোকে প্রণীত অনুশীলন প্রশ্ন"
    ),
    GENERATED(
        id = "GENERATED",
        badgeBn = "স্বয়ংক্রিয় অতিরিক্ত অনুশীলন",
        descriptionBn = "দক্ষতা বৃদ্ধির জন্য গাণিতিক ইঞ্জিনে তৈরি প্রশ্ন"
    )
}

data class BookPracticeQuestion(
    val id: String,
    val bookId: BookId,
    val chapterNumber: Int,
    val lessonId: String? = null,
    val format: BookQuestionFormat,
    val difficultyMode: BookPracticeMode = BookPracticeMode.STANDARD,
    val provenance: BookQuestionProvenance,
    val promptBn: String,
    val optionsBn: List<String>,
    val correctOptionIndices: Set<Int>,
    val correctTextAnswer: String,
    val matchingPairs: List<Pair<String, String>> = emptyList(),
    val hintBn: String,
    val stepByStepSolutionBn: List<String>,
    val explanationBn: String,
    val diagramTag: String? = null,
    val sourcePrintedPage: Int? = null
)

data class BookPracticeSessionState(
    val bookId: BookId = BookId.CLASS_6,
    val chapterNumber: Int? = 1,
    val mode: BookPracticeMode = BookPracticeMode.STANDARD,
    val questions: List<BookPracticeQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndices: Set<Int> = emptySet(),
    val userTextInput: String = "",
    val isAnswerSubmitted: Boolean = false,
    val isCurrentCorrect: Boolean? = null,
    val showHint: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isCompleted: Boolean = false,
    val elapsedSeconds: Int = 0,
    val answeredResults: Map<Int, Boolean> = emptyMap(),
    val selectedOptionsByIndex: Map<Int, Set<Int>> = emptyMap(),
    val userTextInputsByIndex: Map<Int, String> = emptyMap(),
    val sessionStartEpochMs: Long = System.currentTimeMillis()
) {
    val currentQuestion: BookPracticeQuestion?
        get() = questions.getOrNull(currentIndex)
}

enum class BookSearchFilterType(val labelBn: String) {
    ALL("সব"),
    CHAPTER("অধ্যায়"),
    LESSON("পাঠ"),
    FORMULA("সূত্র"),
    EXAMPLE("উদাহরণ"),
    THEOREM("উপপাদ্য/সম্পাদ্য"),
    QUESTION("প্রশ্ন")
}

data class BookSearchResult(
    val id: String,
    val bookId: BookId,
    val chapterNumber: Int,
    val titleBn: String,
    val subtitleBn: String,
    val snippetBn: String,
    val resultType: BookSearchFilterType,
    val lessonId: String? = null,
    val printedPage: Int = 1
)

data class StructuredTextbookPageContent(
    val bookId: BookId,
    val printedPage: Int,
    val pdfPage: Int,
    val chapterNumber: Int,
    val chapterTitleBn: String,
    val sectionHeadingBn: String,
    val keyDefinitionsBn: List<String>,
    val formulasOnPage: List<BookFormulaSpec>,
    val workedExamplesOnPage: List<BookWorkedExample>,
    val exerciseSummaryBn: List<String>,
    val theoremsOnPage: List<BookTheoremOrConstruction> = emptyList(),
    val rulesOnPageBn: List<String> = emptyList(),
    val answerKeyOnPageBn: List<String> = emptyList()
)
