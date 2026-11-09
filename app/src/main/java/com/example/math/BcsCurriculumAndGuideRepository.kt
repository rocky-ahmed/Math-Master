package com.example.math

/**
 * 19-Part Comprehensive Bengali Guide Lesson for BCS Preparation (Prelim + Written Code 008).
 */
data class BcsTopicGuideLesson(
    val topicId: String,
    val unit: BcsSyllabusUnit,
    val domainTitleBn: String,
    val topicTitleBn: String,
    val topicSubtitleBn: String,
    val isOfficialSyllabus: Boolean = true,
    // 19-Part Pedagogy System
    val conceptIntroBn: String,
    val learningObjectivesBn: List<String>,
    val prerequisiteKnowledgeBn: String,
    val detailedExplanationBn: String,
    val importantDefinitionsBn: List<String>,
    val rulesAndPropertiesBn: List<String>,
    val formulaExplanationsBn: List<String>,
    val workedExamplesBn: List<String>,
    val standardMethodBn: String,
    val smartShortcutMethodBn: String,
    val shortcutProofAndWhyItWorksBn: String,
    val commonExamTrapsBn: List<String>,
    val commonMistakesWarningBn: String,
    val fastCalculationTechniquesBn: List<String>,
    val quickRevisionSummaryBn: List<String>,
    val miniCheckQuestionBn: String,
    val miniCheckOptionsBn: List<String>,
    val miniCheckCorrectIndex: Int,
    val miniCheckExplanationBn: String,
    // Deep Integration Links with Math Master Core Modules
    val linkedNctbBookId: BookId? = null,
    val linkedNctbChapterNumber: Int? = null,
    val linkedFormulaId: String? = null,
    val linkedSolverTopicId: String? = null,
    val linkedLabType: BookInteractiveLabType? = null,
    // Extended 19-Part & 9-Domain Metadata
    val mathDomain: BcsMathDomain = BcsMathDomain.ARITHMETIC,
    val authorityBadge: CurriculumAuthorityBadge = CurriculumAuthorityBadge.OFFICIAL_BPSC_PRELIM_SYLLABUS,
    val simpleConceptExplanationBn: String = conceptIntroBn,
    val writtenCode008RubricStepsBn: List<String> = listOf(
        "ধাপ ১ (১.০ নম্বর): প্রদত্ত তথ্য, চলক ও শর্তাবলি স্পষ্টভাবে বাংলায় উল্লেখ করা।",
        "ধাপ ২ (১.৫ নম্বর): প্রাসঙ্গিক গাণিতিক সূত্র বা সমীকরণ গঠন ও প্রতিস্থাপন।",
        "ধাপ ৩ (১.৫ নম্বর): মধ্যবর্তী ধাপগুলো সংক্ষিপ্ত না করে পূর্ণাঙ্গ সমাধান প্রদর্শন।",
        "ধাপ ৪ (১.০ নম্বর): এককসহ চূড়ান্ত উত্তর লিখন ও দ্রুত শুদ্ধি পরীক্ষা।"
    ),
    val practicalApplicationBn: String = "বিসিএস প্রিলিমিনারি (৩৫ নম্বর) এবং লিখিত গাণিতিক যুক্তি কোড ০০৮ (৫০ নম্বর) উভয় পরীক্ষায় সরাসরি ও প্রয়োগমূলক প্রশ্ন সমাধানে অপরিহার্য।"
)

object BcsCurriculumAndGuideRepository {

    /**
     * All 19-Part Curriculum Guide Lessons covering all 9 complete BPSC Mathematical Reasoning domains.
     */
    val mathGuideLessons: List<BcsTopicGuideLesson> = listOf(
        // Domain 1: Arithmetic - Real Numbers
        BcsTopicGuideLesson(
            topicId = "bcs_math_real_numbers",
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            domainTitleBn = "১. পাটিগণিত ও বাস্তব সংখ্যা",
            topicTitleBn = "বাস্তব সংখ্যা ও বিভাজ্যতা",
            topicSubtitleBn = "মূলদ, অমূলদ সংখ্যা, মৌলিক সংখ্যা ও বিভাজ্যতার নিয়মের পূর্ণাঙ্গ গাইড",
            mathDomain = BcsMathDomain.ARITHMETIC,
            conceptIntroBn = "বাস্তব সংখ্যা হলো সকল মূলদ ও অমূলদ সংখ্যার সমষ্টি। বিসিএস পরীক্ষায় প্রায়শই নিচের কোনটি মূলদ বা অমূলদ সংখ্যা তা বাছাই করতে বলা হয়।",
            learningObjectivesBn = listOf(
                "মূলদ ও অমূলদ সংখ্যার মধ্যে পার্থক্য চিহ্নিত করা",
                "পৌনঃপুনিক দশমিক ভগ্নাংশকে সাধারণ ভগ্নাংশে রূপান্তর করা",
                "২, ৩, ৪, ৫, ৬, ৮, ৯, ১১ দ্বারা বিভাজ্যতার দ্রুত নিয়ম প্রয়োগ করা"
            ),
            prerequisiteKnowledgeBn = "স্বাভাবিক সংখ্যা, পূর্ণসংখ্যা ও ভগ্নাংশের প্রাথমিক ধারণা।",
            detailedExplanationBn = "যে সকল সংখ্যাকে দুটি পূর্ণসংখ্যার অনুপাত (p/q, যেখানে q ≠ 0) আকারে প্রকাশ করা যায়, তারা মূলদ সংখ্যা (Rational Numbers)। পূর্ণবর্গ নয় এমন যেকোনো সংখ্যার বর্গমূল (যেমন √২, √৩, √৫, √৭) অমূলদ সংখ্যা। পাই (π) ও অয়লারের ধ্রুবক (e) অমূলদ সংখ্যা।",
            importantDefinitionsBn = listOf(
                "মূলদ সংখ্যা: যে সংখ্যাকে p/q আকারে লেখা যায় (যেমন: ৩/৪, ৫, ০, ০.৩৩৩...)",
                "অমূলদ সংখ্যা: যে সংখ্যাকে p/q আকারে লেখা যায় না এবং যার দশমিক রূপ অসীম অনাবৃত্ত (যেমন: √২, π)",
                "পরস্পর সহমৌলিক: দুটি সংখ্যার সাধারণ গ.সা.গু ১ হলে তারা পরস্পর সহমৌলিক।"
            ),
            rulesAndPropertiesBn = listOf(
                "মূলদ + অমূলদ = অমূলদ সংখ্যা।",
                "অশূন্য মূলদ × অমূলদ = অমূলদ সংখ্যা।",
                "যেকোনো দুটি বাস্তব সংখ্যার মধ্যে অসংখ্য মূলদ ও অমূলদ সংখ্যা থাকে।"
            ),
            formulaExplanationsBn = listOf(
                "পৌনঃপুনিক রূপান্তর: ০.p = p/৯, ০.pq = pq/৯৯, ০.ab̄c = (abc - ab) / ৯০০।"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ ১: ০.৩̅ কে ভগ্নাংশে প্রকাশ করুন।\nসমাধান: ৩/৯ = ১/৩।",
                "উদাহরণ ২: ৩ ও ৪ এর মধ্যে দুটি অমূলদ সংখ্যা লিখুন।\nসমাধান: √১০ (≈ ৩.১৬) এবং √১১ (≈ ৩.৩১)।"
            ),
            standardMethodBn = "পৌনঃপুনিক সংখ্যাকে সমীকরণ ধরে (যেমন x = ০.৩̅ => ১০x = ৩.৩̅) বিয়োগ করে x = ৩/৯ = ১/৩ পাওয়া যায়।",
            smartShortcutMethodBn = "শর্টকাট: লব = সম্পূর্ণ সংখ্যা থেকে অনাবৃত্ত অংশ বিয়োগ; হর = যতটি আবৃত্ত ততটি ৯, এরপর যতটি অনাবৃত্ত ততটি ০।",
            shortcutProofAndWhyItWorksBn = "দশমিক স্থানান্তরের গুণিতক থেকে পূর্ববর্তী মান বিয়োগ করলে অনাবৃত্ত লেজ বিলুপ্ত হয়ে যায়। ফলে ৯, ৯৯ বা ৯০০ এর ভাগফল স্বয়ংক্রিয়ভাবে তৈরি হয়।",
            commonExamTrapsBn = listOf(
                "২২/৭ একটি মূলদ সংখ্যা (p/q রূপ), কিন্তু π একটি অমূলদ সংখ্যা। ২২/৭ হলো π-এর আসন্ন মান মাত্র!",
                "০ একটি মূলদ সংখ্যা কারণ ০ = ০/১ লেখা যায়।"
            ),
            commonMistakesWarningBn = "রুট দেখলেই অমূলদ ভাববেন না; যেমন √১৬ = ৪, যা একটি মূলদ সংখ্যা।",
            fastCalculationTechniquesBn = listOf(
                "১১ দ্বারা বিভাজ্যতা: বিজোড় স্থানের অঙ্কগুলোর যোগফল এবং জোড় স্থানের অঙ্কগুলোর যোগফলের অন্তর ০ অথবা ১১-এর গুণিতক হলে সংখ্যাটি ১১ দ্বারা বিভাজ্য।"
            ),
            quickRevisionSummaryBn = listOf(
                "p/q আকারে প্রকাশযোগ্য = মূলদ",
                "পূর্ণবর্গ ছাড়া অন্য সংখ্যার রুট = অমূলদ",
                "π এবং e = অমূলদ"
            ),
            miniCheckQuestionBn = "নিচের কোনটি অমূলদ সংখ্যা?",
            miniCheckOptionsBn = listOf("√০.১৬", "√২", "০.৩̅", "২২/৭"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "√২ পূর্ণবর্গ নয়, তাই এটি অমূলদ। √০.১৬ = ০.৪ = ২/৫ (মূলদ), ০.৩̅ = ১/৩ (মূলদ) এবং ২২/৭ (মূলদ)।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 1,
            linkedFormulaId = null,
            linkedSolverTopicId = "NUMBER_SYSTEM",
            linkedLabType = null
        ),

        // Domain 1: Arithmetic - Ratio & Percentage
        BcsTopicGuideLesson(
            topicId = "bcs_math_percentage_ratio",
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            domainTitleBn = "১. পাটিগণিত ও বাস্তব সংখ্যা",
            topicTitleBn = "শতকরা, অনুপাত ও লাভ-ক্ষতি",
            topicSubtitleBn = "ধারাবাহিক অনুপাত, হ্রাস-বৃদ্ধি, ক্রয়-বিক্রয় ও মুনাফার দ্রুত সমাধান",
            conceptIntroBn = "শতকরা ও অনুপাত বিসিএস প্রিলিমিনারিতে প্রতি বছর কমপক্ষে ২টি প্রশ্ন নিশ্চিত করে।",
            learningObjectivesBn = listOf(
                "ধারাবাহিক অনুপাত (N-Rule বা দ-পদ্ধতি) তাৎক্ষণিক বের করা",
                "দামের বৃদ্ধি/হ্রাসে খরচের ভারসাম্য বজায় রাখার সূত্র প্রয়োগ",
                "সরল ও চক্রবৃদ্ধি মুনাফার পার্থক্য ও সূত্র জানা"
            ),
            prerequisiteKnowledgeBn = "ভগ্নাংশ ও সাধারণ গুণ-ভাগ।",
            detailedExplanationBn = "শতকরা (Percentage) মানে প্রতি শতে কত। ক্রয়মূল্যকে সর্বদা ১০০% ধরে হিসাব করলে লাভ-ক্ষতির জটিল অঙ্ক মুহূর্তেই সমাধান করা যায়।",
            importantDefinitionsBn = listOf(
                "ক্রয়মূল্য: যে মূল্যে দ্রব্য কেনা হয় (ভিত্তি = ১০০%)",
                "বিক্রয়মূল্য: ক্রয়মূল্য + লাভ অথবা ক্রয়মূল্য - ক্ষতি",
                "ধারাবাহিক অনুপাত: A:B এবং B:C দেওয়া থাকলে A:B:C গঠন।"
            ),
            rulesAndPropertiesBn = listOf(
                "লাভ বা ক্ষতি সর্বদা ক্রয়মূল্যের উপর নির্ধারিত হয়।",
                "কোনো জিনিসের দাম r% বাড়লে খরচ অপরিবর্তিত রাখতে ব্যবহার কমাতে হবে [r / (১০০ + r)] × ১০০%।"
            ),
            formulaExplanationsBn = listOf(
                "ব্যবহার হ্রাস = [r / (১০০ + r)] × ১০০%",
                "সমহারে হ্রাস-বৃদ্ধি হলে ক্ষতি = (x² / ১০০)%",
                "সরল মুনাফা: I = Pnr",
                "চক্রবৃদ্ধি মূলধন: C = P(১ + r)ⁿ"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: চিনির মূল্য ২৫% বৃদ্ধি পেল। চিনির ব্যবহার শতকরা কত কমালে খরচ বৃদ্ধি পাবে না?\nসমাধান: [২৫ / (১০০ + ২৫)] × ১০০% = (২৫/১২৫) × ১০০% = ২০%।"
            ),
            standardMethodBn = "ধরি পূর্বের মূল্য ১০০ টাকা। বর্তমান মূল্য ১২৫ টাকা। ১২৫ টাকায় কমাতে হবে ২৫ টাকা, সুতরাং ১০০ টাকায় কমাতে হবে ২০ টাকা।",
            smartShortcutMethodBn = "শর্টকাট: বৃদ্ধির ক্ষেত্রে [r / (১০০ + r)] × ১০০%; হ্রাসের ক্ষেত্রে [r / (১০০ - r)] × ১০০%।",
            shortcutProofAndWhyItWorksBn = "মোট ব্যয় = মূল্য × পরিমাণ। ব্যয় ধ্রুবক রাখতে মূল্যের অনুপাত ১২৫/১০০ হলে পরিমাণের অনুপাত হতে হবে ১০০/১২৫, অর্থাৎ হ্রাস ২৫/১২৫ = ১/৫ = ২০%।",
            commonExamTrapsBn = listOf(
                "বিক্রয়মূল্যের উপর লাভ বলা থাকলে প্রথমে ক্রয়মূল্য বের করে নিতে হবে।"
            ),
            commonMistakesWarningBn = "১০% বাড়িয়ে আবার ১০% কমালে পূর্বের মূল্যে ফেরে না, বরং ১% ক্ষতি হয়!",
            fastCalculationTechniquesBn = listOf(
                "দ-গুণন পদ্ধতি: A:B এবং B:C থাকলে (A×B):(B×B):(B×C)।"
            ),
            quickRevisionSummaryBn = listOf(
                "দাম ২৫% বাড়লে ব্যবহার ২০% কমাতে হবে",
                "দাম ২০% বাড়লে ব্যবহার ১৬.৬৭% কমাতে হবে",
                "একই হারে হ্রাস-বৃদ্ধিতে সর্বদা x²/১০০ % ক্ষতি"
            ),
            miniCheckQuestionBn = "একটি দ্রব্যের মূল্য ১০% বৃদ্ধি পেয়ে পরে ১০% হ্রাস পেলে সামগ্রিক পরিবর্তন কত?",
            miniCheckOptionsBn = listOf("০% পরিবর্তন", "১% ক্ষতি", "১% লাভ", "২% ক্ষতি"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "সূত্র: (১০² / ১০০)% = ১% ক্ষতি।",
            linkedNctbBookId = BookId.CLASS_8,
            linkedNctbChapterNumber = 2,
            linkedFormulaId = null,
            linkedSolverTopicId = "ARITHMETIC",
            linkedLabType = BookInteractiveLabType.PROFIT_LOSS_PERCENTAGE_CALC
        ),

        // Domain 2: Algebra - Formulas & Equations
        BcsTopicGuideLesson(
            topicId = "bcs_math_algebra_formulas",
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            domainTitleBn = "২. বীজগণিতীয় সূত্রাবলি ও সমীকরণ",
            topicTitleBn = "বর্গ, ঘন, উৎপাদক ও দ্বিঘাত সমীকরণ",
            topicSubtitleBn = "মান নির্ণয়ের অনুসিদ্ধান্ত, উৎপাদক বিশ্লেষণ এবং সমীকরণ সমাধানের কৌশল",
            mathDomain = BcsMathDomain.ALGEBRA,
            conceptIntroBn = "বীজগণিতীয় অনুসিদ্ধান্ত ও দ্বিঘাত সমীকরণ থেকে বিসিএসে প্রতি বছর ৪-৫টি সরাসরি প্রশ্ন আসে।",
            learningObjectivesBn = listOf(
                "বর্গ ও ঘনের অনুসিদ্ধান্ত ব্যবহার করে দ্রুত মান নির্ণয়",
                "মধ্যপদ বিভাজন (Middle-term factor) দ্রুত করা",
                "দ্বিঘাত সমীকরণ ax² + bx + c = 0 এর মূলের প্রকৃতি ও সমাধান জানা"
            ),
            prerequisiteKnowledgeBn = "বীজগণিতীয় প্রতীক ও চিহ্নের গুণ-ভাগ।",
            detailedExplanationBn = "মান নির্ণয়ের অঙ্কে সরাসরি সূত্র প্রয়োগ না করে উপযুক্ত অনুসিদ্ধান্ত প্রয়োগ করতে হয়। যেমন x + 1/x = a হলে x² + 1/x² = a² - 2 এবং x³ + 1/x³ = a³ - 3a।",
            importantDefinitionsBn = listOf(
                "অনুসিদ্ধান্ত: মূল সূত্র থেকে সিদ্ধান্ত আকারে পাওয়া মান নির্ণয়ের সম্পর্ক।",
                "দ্বিঘাত সমীকরণ: যে সমীকরণে চলকের সর্বোচ্চ ঘাত ২।",
                "নিশ্চায়ক (Discriminant): D = b² - 4ac।"
            ),
            rulesAndPropertiesBn = listOf(
                "D > 0 হলে মূলদ্বয় বাস্তব ও অসমান।",
                "D = 0 হলে মূলদ্বয় বাস্তব ও সমান।",
                "D < 0 হলে মূলদ্বয় জটিল/অবাস্তব।"
            ),
            formulaExplanationsBn = listOf(
                "x² + 1/x² = (x + 1/x)² - 2",
                "x³ + 1/x³ = (x + 1/x)³ - 3(x + 1/x)",
                "দ্বিঘাত সূত্র: x = [-b ± √(b² - 4ac)] / (2a)"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: x + 1/x = 3 হলে x³ + 1/x³ = কত?\nসমাধান: ৩³ - ৩(৩) = ২৭ - ৯ = ১৮।"
            ),
            standardMethodBn = "উভয়পক্ষকে ঘন করে: (x + 1/x)³ = ৩³ => x³ + 1/x³ + ৩(x)(1/x)(x + 1/x) = ২৭ => x³ + 1/x³ + ৯ = ২৭ => ১৮।",
            smartShortcutMethodBn = "শর্টকাট: x + 1/x = k হলে x³ + 1/x³ = k³ - 3k। এখানে ৩³ - ৩(৩) = ১৮।",
            shortcutProofAndWhyItWorksBn = "(a + b)³ = a³ + b³ + 3ab(a + b) সূত্রে ab = x·(1/x) = 1 হওয়ায় ৩ab পদটি ৩ এ পরিণত হয়।",
            commonExamTrapsBn = listOf(
                "চিহ্নের সতর্কতা: x - 1/x = k হলে x³ - 1/x³ = k³ + 3k (প্লাস হবে)।"
            ),
            commonMistakesWarningBn = "x² - 1/x² বের করতে বললে বর্গ নয়, বরং (x+1/x)(x-1/x) করতে হবে!",
            fastCalculationTechniquesBn = listOf(
                "যদি x + 1/x = √3 হয় তবে সর্বদা x⁶ = -1 এবং x⁶ + 1 = 0।"
            ),
            quickRevisionSummaryBn = listOf(
                "k = 3 হলে x³ + 1/x³ = 18",
                "k = 4 হলে x³ + 1/x³ = 52",
                "k = 5 হলে x³ + 1/x³ = 110"
            ),
            miniCheckQuestionBn = "যদি x - 1/x = 4 হয়, তবে x² + 1/x² এর মান কত?",
            miniCheckOptionsBn = listOf("১৪", "১৬", "১৮", "১২"),
            miniCheckCorrectIndex = 2,
            miniCheckExplanationBn = "x² + 1/x² = (x - 1/x)² + ২ = ৪² + ২ = ১৬ + ২ = ১৮।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 3,
            linkedFormulaId = "alg_cube_1",
            linkedSolverTopicId = "ALGEBRA",
            linkedLabType = BookInteractiveLabType.ALGEBRA_INTERACTIVE_LAB
        ),

        // Domain 3: Exponents, Logarithms & Series
        BcsTopicGuideLesson(
            topicId = "bcs_math_log_series",
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            domainTitleBn = "৩. সূচক, লগারিদম ও ধারা",
            topicTitleBn = "সূচক, লগারিদমের ধর্ম ও সমান্তর-গুণোত্তর ধারা",
            topicSubtitleBn = "লগারিদম রূপান্তর, সূচকীয় সমীকরণ এবং n-তম পদ ও সমষ্টির হিসাব",
            mathDomain = BcsMathDomain.SEQUENCES_SERIES,
            conceptIntroBn = "বিসিএস প্রিলিমিনারিতে লগারিদমের মান নির্ণয় এবং সমান্তর/গুণোত্তর ধারার সমষ্টি থেকে নিশ্চিত প্রশ্ন থাকে।",
            learningObjectivesBn = listOf(
                "log_a (b) = x রূপান্তর করে b = aˣ বের করা",
                "লগারিদমের ভিত্তি পরিবর্তন ও গুণ-ভাগের সূত্র প্রয়োগ",
                "সমান্তর ও গুণোত্তর ধারার n-তম পদ ও n পদের সমষ্টি নির্ণয়"
            ),
            prerequisiteKnowledgeBn = "ঘাত ও সূচকের মৌলিক নিয়মাবলি।",
            detailedExplanationBn = "যদি aˣ = N হয়, তবে x = log_a(N)। মনে রাখবেন লগের ভিত্তি কখনো ১ বা ঋণাত্মক হতে পারে না (a > 0, a ≠ 1)।",
            importantDefinitionsBn = listOf(
                "লগারিদম: সূচকের বিপরীত প্রক্রিয়া।",
                "সমান্তর ধারা: যে ধারার পরপর দুটি পদের অন্তর সর্বদা সমান।",
                "গুণোত্তর ধারা: যে ধারার পরপর দুটি পদের অনুপাত সর্বদা সমান।"
            ),
            rulesAndPropertiesBn = listOf(
                "log(MN) = log M + log N",
                "log(M/N) = log M - log N",
                "log(Mᵖ) = p log M",
                "log_a(a) = 1, log_a(1) = 0"
            ),
            formulaExplanationsBn = listOf(
                "সমান্তর n-তম পদ: a + (n - 1)d",
                "সমান্তর সমষ্টি: Sₙ = (n/2)[2a + (n - 1)d]",
                "১+২+৩+...+n = [n(n+1)] / 2",
                "গুণোত্তর n-তম পদ: arⁿ⁻¹"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: ১ + ২ + ৩ + ... + ৫০ = কত?\nসমাধান: [৫০ × ৫১] / ২ = ২৫ × ৫১ = ১২৭৫।"
            ),
            standardMethodBn = "ধারাটির প্রথম পদ a = ১, সাধারণ অন্তর d = ১, পদসংখ্যা n = ৫০। সমষ্টি S = (৫০/২)[২(১) + (৫০-১)১] = ২৫[৫১] = ১২৭৫।",
            smartShortcutMethodBn = "স্বাভাবিক সংখ্যার সমষ্টি সূত্র: [শেষ পদ × (শেষ পদ + ১)] / ২। ৫০ × ৫১ / ২ = ১২৭৫।",
            shortcutProofAndWhyItWorksBn = "গাউসের জোড় পদ্ধতি: প্রথম ও শেষ পদের যোগফল (১+৫০=৫১), দ্বিতীয় ও শেষপূর্ব পদের যোগফল (২+৪৯=৫১)—এভাবে n/২ সংখ্যক জোড়া তৈরি হয়।",
            commonExamTrapsBn = listOf(
                "log_x (1/9) = -2 হলে x⁻² = 1/9 => 1/x² = 1/9 => x = 3 (ঋণাত্মক ৩ গ্রাহ্য নয় কারণ ভিত্তি ধনাত্মক)।"
            ),
            commonMistakesWarningBn = "লগ যোগ থাকলে গুণ হয়, কিন্তু log(M + N) কখনো log M + log N নয়!",
            fastCalculationTechniquesBn = listOf(
                "বিজোড় সংখ্যার সমষ্টি: ১ + ৩ + ৫ + ... n টি পদের সমষ্টি = n²।"
            ),
            quickRevisionSummaryBn = listOf(
                "১ থেকে ৫০ পর্যন্ত যোগফল = ১২৭৫",
                "১ থেকে ১০০ পর্যন্ত যোগফল = ৫০৫০",
                "log_a(1) = 0 সর্বদা"
            ),
            miniCheckQuestionBn = "log₂ (16) এর মান কত?",
            miniCheckOptionsBn = listOf("২", "৩", "৪", "৮"),
            miniCheckCorrectIndex = 2,
            miniCheckExplanationBn = "১৬ = ২⁴, সুতরাং log₂(২⁴) = ৪ log₂(২) = ৪ × ১ = ৪।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 4,
            linkedFormulaId = "sq_sum_n",
            linkedSolverTopicId = "SERIES",
            linkedLabType = null
        ),

        // Domain 4: Geometry & Mensuration
        BcsTopicGuideLesson(
            topicId = "bcs_math_geometry_mensuration",
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            domainTitleBn = "৪. জ্যামিতি ও পরিমিতি",
            topicTitleBn = "ত্রিভুজ, চতুর্ভুজ, বৃত্ত ও পিথাগোরাসের উপপাদ্য",
            topicSubtitleBn = "রেখা, কোণ, পিথাগোরাসের উপপাদ্য, বৃত্তের জ্যা ও স্পর্শক উপপাদ্য",
            mathDomain = BcsMathDomain.GEOMETRY,
            conceptIntroBn = "জ্যামিতি অংশের চিত্র ও উপপাদ্য ভিত্তিক সমস্যাগুলো চিত্র একে সেকেন্ডে সমাধান করা যায়।",
            learningObjectivesBn = listOf(
                "পিথাগোরাসের ট্রিপলেট (৩-৪-৫, ৫-১২-১৩, ৮-১৫-১৭ ইত্যাদি) মুখস্থ প্রয়োগ",
                "সমবাহু, সমদ্বিবাহু ও বিষমবাহু ত্রিভুজের ক্ষেত্রফল নির্ণয়",
                "বৃত্তের পরিধি, ক্ষেত্রফল ও চাপের দৈর্ঘ্য বের করা"
            ),
            prerequisiteKnowledgeBn = "কোণ ও রেখার প্রাথমিক ধারণা।",
            detailedExplanationBn = "সমকোণী ত্রিভুজে অতিভুজ² = লম্ব² + ভূমি²। পিথাগোরিয়ান ত্রয়ী মনে রাখলে পরীক্ষার খাতায় বর্গমূল করার প্রয়োজন হয় না।",
            importantDefinitionsBn = listOf(
                "সমবাহু ত্রিভুজ: যে ত্রিভুজের তিনটি বাহুই সমান (ক্ষেত্রফল = (√৩/৪)a²)",
                "বৃত্তের ক্ষেত্রফল: πr² এবং পরিধি = ২πr",
                "বর্গক্ষেত্রের কর্ণ: d = a√২"
            ),
            rulesAndPropertiesBn = listOf(
                "ত্রিভুজের তিন কোণের সমষ্টি ১৮০ ডিগ্রি।",
                "বৃত্তস্থ কোণ কেন্দ্রস্থ কোণের অর্ধেক।",
                "অর্ধবৃত্তস্থ কোণ সর্বদা এক সমকোণ (৯০°)।"
            ),
            formulaExplanationsBn = listOf(
                "সমবাহু ত্রিভুজের ক্ষেত্রফল = (√৩/৪) a²",
                "বর্গক্ষেত্রের ক্ষেত্রফল = a² = d² / ২ (কর্ণ d হলে)",
                "রম্বসের ক্ষেত্রফল = (১/২) × d₁ × d₂"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: একটি বর্গক্ষেত্রের কর্ণের দৈর্ঘ্য 4√2 একক হলে এর ক্ষেত্রফল কত?\nসমাধান: ক্ষেত্রফল = d² / ২ = (4√2)² / ২ = ৩২ / ২ = ১৬ বর্গ একক।"
            ),
            standardMethodBn = "ধরি বাহু a। কর্ণ a√2 = 4√2 => a = 4। অতএব ক্ষেত্রফল = a² = ৪² = ১৬ বর্গ একক।",
            smartShortcutMethodBn = "কর্ণ দেওয়া থাকলে সরাসরি ক্ষেত্রফল = (কর্ণ)² / ২। (4√2)² / ২ = ৩২ / ২ = ১৬।",
            shortcutProofAndWhyItWorksBn = "পিথাগোরাস অনুযায়ী a² + a² = d² => 2a² = d² => a² = d²/2।",
            commonExamTrapsBn = listOf(
                "বৃত্তের ব্যাস নাকি ব্যাসার্ধ দ্বিগুণ করা হয়েছে তা খেয়াল করুন। ব্যাসার্ধ দ্বিগুণ হলে ক্ষেত্রফল ৪ গুণ বাড়ে।"
            ),
            commonMistakesWarningBn = "ত্রিভুজের ক্ষেত্রফলে (১/২) × ভূমি × উচ্চতা ভুলে ১/২ বাদ দিয়ে ফেলা সাধারণ ভুল।",
            fastCalculationTechniquesBn = listOf(
                "পিথাগোরাসের ট্রিপলেট: ৩-৪-৫, ৫-১২-১৩, ৮-১৫-১৭, ৭-২৪-২৫।"
            ),
            quickRevisionSummaryBn = listOf(
                "বর্গের ক্ষেত্রফল = (কর্ণ)² / ২",
                "সমবাহুর ক্ষেত্রফল = (√৩/৪) a²",
                "অর্ধবৃত্তস্থ কোণ = ৯০°"
            ),
            miniCheckQuestionBn = "একটি সমবাহু ত্রিভুজের প্রতিটি বাহুর দৈর্ঘ্য ২ একক হলে এর ক্ষেত্রফল কত?",
            miniCheckOptionsBn = listOf("√৩ বর্গ একক", "২√৩ বর্গ একক", "৪ বর্গ একক", "১ বর্গ একক"),
            miniCheckCorrectIndex = 0,
            miniCheckExplanationBn = "ক্ষেত্রফল = (√৩/৪) × ২² = (√৩/৪) × ৪ = √৩ বর্গ একক।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 16,
            linkedFormulaId = "geo_pythagoras",
            linkedSolverTopicId = "GEOMETRY",
            linkedLabType = BookInteractiveLabType.PYTHAGORAS_THEOREM_VISUALIZER
        ),

        // Domain 5: Sets, Combinatorics & Probability
        BcsTopicGuideLesson(
            topicId = "bcs_math_sets_prob",
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            domainTitleBn = "৮. সেট, বিন্যাস, সমাবেশ ও সম্ভাব্যতা",
            topicTitleBn = "সেট ও ভেনচিত্র, বিন্যাস (ⁿPᵣ), সমাবেশ (ⁿCᵣ) ও সম্ভাব্যতা",
            topicSubtitleBn = "বাছাই, সাজানো, সম্ভাব্যতা নির্ণয় ও ভেনচিত্র সমাধান",
            mathDomain = BcsMathDomain.SETS_COUNTING_PROBABILITY,
            conceptIntroBn = "বিন্যাস ও সমাবেশ বিসিএসের সবচেয়ে গুরুত্বপূর্ণ আধুনিক গণিত অংশ। ক্রম গুরুত্বপূর্ণ হলে বিন্যাস, শুধু বাছাই হলে সমাবেশ।",
            learningObjectivesBn = listOf(
                "বিন্যাস (Permutation) ও সমাবেশ (Combination) এর পার্থক্য বোঝা",
                "ⁿPᵣ এবং ⁿCᵣ সূত্র মুখস্থ ও দ্রুত কাটাকাটি",
                "মুদ্রা, ছক্কা ও তাস সংক্রান্ত সম্ভাব্যতা নির্ভুল নির্ণয়"
            ),
            prerequisiteKnowledgeBn = "ফ্যাক্টোরিয়াল (Factorial: n!) এর ধারণা।",
            detailedExplanationBn = "n বস্তুর মধ্য থেকে r বস্তু নিয়ে সাজানোর উপায় ⁿPᵣ = n! / (n - r)!। আর দল গঠন বা বাছাই করার উপায় ⁿCᵣ = n! / [r!(n - r)!] = ⁿPᵣ / r!।",
            importantDefinitionsBn = listOf(
                "সম্ভাব্যতা: অনুকূল ফলাফল সংখ্যা / মোট সম্ভাব্য ফলাফল সংখ্যা।",
                "ভেনচিত্র সংযোগ: n(A ∪ B) = n(A) + n(B) - n(A ∩ B)।"
            ),
            rulesAndPropertiesBn = listOf(
                "ⁿCᵣ = ⁿCₙ₋ᵣ (যেমন ¹⁰C₈ = ¹⁰C₂)",
                "যেকোনো ঘটনার সম্ভাব্যতা ০ থেকে ১ এর মধ্যে থাকে (0 ≤ P(E) ≤ 1)।"
            ),
            formulaExplanationsBn = listOf(
                "ⁿC₂ = [n(n - 1)] / 2",
                "ⁿC₃ = [n(n - 1)(n - 2)] / 6",
                "মুদ্রা n বার নিক্ষেপে মোট ফলাফল = 2ⁿ"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: ৬ জন ব্যক্তি একে অপরের সাথে করমর্দন করলে মোট করমর্দন সংখ্যা কত?\nসমাধান: ⁶C₂ = (৬ × ৫) / ২ = ১৫ টি।"
            ),
            standardMethodBn = "দুজনের মধ্যে একটি করমর্দন হয়। ৬ জন থেকে ২ জন বাছাইয়ের উপায় ⁶C₂ = ৬! / (২! ৪!) = (৬ × ৫) / ২ = ১৫।",
            smartShortcutMethodBn = "হ্যান্ডশেক বা ম্যাচের সংখ্যা সূত্র: n(n - 1) / 2। ৬ × ৫ / ২ = ১৫।",
            shortcutProofAndWhyItWorksBn = "প্রত্যেক ব্যক্তি বাকি (n-1) জনের সাথে হাত মিলায়। যেহেতু A ও B এর হ্যান্ডশেক একই, তাই ২ দিয়ে ভাগ করতে হয়।",
            commonExamTrapsBn = listOf(
                "বিন্যাস ও সমাবেশের পার্থক্য: 'কমিটি গঠন' বা 'দল তৈরি' বললে সমাবেশ (C); 'শব্দের অক্ষর সাজানো' বা 'সংখ্যার পদ তৈরি' বললে বিন্যাস (P)।"
            ),
            commonMistakesWarningBn = "সম্ভাব্যতার মান কখনো ১ এর বেশি বা ঋণাত্মক হতে পারে না।",
            fastCalculationTechniquesBn = listOf(
                "¹⁰C₇ হিসাব না করে ¹⁰C₃ হিসাব করুন: (১০ × ৯ × ৮) / (৩ × ২ × ১) = ১২০।"
            ),
            quickRevisionSummaryBn = listOf(
                "হ্যান্ডশেক = n(n-1)/2",
                "মুদ্রা ৩ বার নিক্ষেপে মোট নমুনা = ৮",
                "ⁿC₀ = 1, ⁿC₁ = n, ⁿCₙ = 1"
            ),
            miniCheckQuestionBn = "তিনটি মুদ্রা একসাথে একবার নিক্ষেপ করা হলে সবগুলোতেই 'Head' আসার সম্ভাবনা কত?",
            miniCheckOptionsBn = listOf("১/২", "১/৪", "১/৮", "৩/৮"),
            miniCheckCorrectIndex = 2,
            miniCheckExplanationBn = "মোট ফলাফল ২³ = ৮টি। কেবল (H, H, H) ১টি অনুকূল ফলাফল। সম্ভাবনা = ১/৮।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 7,
            linkedFormulaId = null,
            linkedSolverTopicId = "PERMUTATION_COMBINATION",
            linkedLabType = BookInteractiveLabType.SET_AND_FUNCTION_VISUALIZER
        ),

        // Domain 1B: Arithmetic - Unitary Method, Time-Work, Pipes, Boats & Trains
        BcsTopicGuideLesson(
            topicId = "bcs_math_unitary_work_speed",
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            domainTitleBn = "১. পাটিগণিত ও ঐকিক নিয়ম",
            topicTitleBn = "ঐকিক নিয়ম: সময়-কাজ, নল-চৌবাচ্চা, নৌকা ও ট্রেন",
            topicSubtitleBn = "একক সময়ের কাজ, স্রোতের অনুকূল-প্রতিকূল বেগ এবং ট্রেনের আপেক্ষিক বেগ",
            mathDomain = BcsMathDomain.ARITHMETIC,
            conceptIntroBn = "ঐকিক নিয়ম, কাজ ও সময়ের অঙ্ক বিসিএস প্রিলিমিনারি এবং লিখিত কোড ০০৮ উভয় ক্ষেত্রেই অত্যন্ত গুরুত্বপূর্ণ।",
            learningObjectivesBn = listOf(
                "দুই বা ততোধিক ব্যক্তির একত্রে কাজ করার সময় দ্রুত নির্ণয় করা",
                "নল ও চৌবাচ্চার পূর্ণ বা খালি হওয়ার সময় হিসাব করা",
                "স্রোতের অনুকূল ও প্রতিকূল বেগ থেকে নৌকা ও স্রোতের প্রকৃত বেগ বের করা"
            ),
            prerequisiteKnowledgeBn = "ল.সা.গু ও ভগ্নাংশের যোগ-বিয়োগ।",
            detailedExplanationBn = "কাজের অঙ্কে মোট কাজকে ১ অংশ বা দিনগুলোর ল.সা.গু (LCM) একক ধরে নিলে ভগ্নাংশ ছাড়াই মুখে মুখে উত্তর বের করা যায়। নৌকার অঙ্কে অনুকূল বেগ u = v + s এবং প্রতিকূল বেগ d = v - s।",
            importantDefinitionsBn = listOf(
                "অনুকূল বেগ: নৌকার বেগ + স্রোতের বেগ (u = v + s)",
                "প্রতিকূল বেগ: নৌকার বেগ - স্রোতের বেগ (d = v - s)",
                "আপেক্ষিক বেগ: বিপরীত দিকে চলমান দুটি ট্রেনের বেগের যোগফল; একই দিকে চললে বিয়োগফল।"
            ),
            rulesAndPropertiesBn = listOf(
                "নৌকার বেগ v = (অনুকূল বেগ + প্রতিকূল বেগ) / ২",
                "স্রোতের বেগ s = (অনুকূল বেগ - প্রতিকূল বেগ) / ২",
                "কিমি/ঘণ্টা কে মিটার/সেকেন্ডে নিতে ৫/১৮ দ্বারা গুণ করতে হয়।"
            ),
            formulaExplanationsBn = listOf(
                "দুইজন একত্রে কাজের সময় T = (x × y) / (x + y)",
                "একটি নল পূর্ণ করে x মিনিটে, অপরটি খালি করে y মিনিটে: T = (x × y) / (y - x)",
                "গড় দ্রুতি (যাতায়াত দূরত্ব সমান হলে) = (2xy) / (x + y)"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: ক একটি কাজ ১০ দিনে এবং খ ১৫ দিনে করতে পারে। তারা একত্রে কত দিনে কাজটি শেষ করবে?\nসমাধান: T = (১০ × ১৫) / (১০ + ১৫) = ১৫০ / ২৫ = ৬ দিনে।"
            ),
            standardMethodBn = "ক ১ দিনে করে ১/১০ অংশ, খ ১ দিনে করে ১/১৫ অংশ। একত্রে ১ দিনে করে (১/১০ + ১/১৫) = ৫/৩০ = ১/৬ অংশ। সুতরাং সম্পূর্ণ কাজ করে ৬ দিনে।",
            smartShortcutMethodBn = "ল.সা.গু পদ্ধতি: ১০ ও ১৫ এর ল.সা.গু ৩০ (মোট কাজ ৩০টি চেয়ার)। ক বানায় ৩টি/দিন, খ বানায় ২টি/দিন। একত্রে ৫টি/দিন => ৩০/৫ = ৬ দিন!",
            shortcutProofAndWhyItWorksBn = "ভগ্নাংশের হরগুলোর ল.সা.গু দিয়ে গুণ করলে ভগ্নাংশ পূর্ণসংখ্যায় রূপান্তরিত হয়, যা মানসিক হিসাবকে ৩ গুণ দ্রুত করে।",
            commonExamTrapsBn = listOf(
                "যাতায়াতের গড় বেগ কখনো (x + y)/2 নয়! দূরত্ব সমান হলে গড় বেগ = 2xy / (x + y)।"
            ),
            commonMistakesWarningBn = "ট্রেন যখন সেতু বা প্ল্যাটফর্ম অতিক্রম করে, তখন অতিক্রান্ত দূরত্ব = ট্রেনের দৈর্ঘ্য + সেতুর দৈর্ঘ্য।",
            fastCalculationTechniquesBn = listOf(
                "৫৪ কিমি/ঘণ্টা = ৫৪ × (৫/১৮) = ১৫ মিটার/সেকেন্ড।"
            ),
            quickRevisionSummaryBn = listOf(
                "একত্রে কাজ = xy / (x + y)",
                "নৌকার বেগ = (u + d) / 2",
                "১ কিমি/ঘণ্টা = ৫/১৮ মি/সে"
            ),
            miniCheckQuestionBn = "ক একটি কাজ ১২ দিনে এবং খ ২৪ দিনে করতে পারে। তারা একত্রে কাজটি কত দিনে শেষ করবে?",
            miniCheckOptionsBn = listOf("৬ দিনে", "৮ দিনে", "৯ দিনে", "১০ দিনে"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "T = (১২ × ২৪) / (১২ + ২৪) = ২৮৮ / ৩৬ = ৮ দিনে।",
            linkedNctbBookId = BookId.CLASS_8,
            linkedNctbChapterNumber = 2,
            linkedFormulaId = null,
            linkedSolverTopicId = "ARITHMETIC",
            linkedLabType = BookInteractiveLabType.PROFIT_LOSS_PERCENTAGE_CALC
        ),

        // Domain 5 (Mensuration): 2D & 3D Mensuration
        BcsTopicGuideLesson(
            topicId = "bcs_math_mensuration_3d",
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            domainTitleBn = "৫. পরিমিতি (দ্বিমাত্রিক ও ত্রিমাত্রিক)",
            topicTitleBn = "পরিমিতি: ক্ষেত্রফল, ঘনবস্তু, সিলিন্ডার, কোণক ও গোলক",
            topicSubtitleBn = "আয়তাকার ঘনবস্তু, ঘনক, বেলন (Cylinder), কোণক (Cone) ও গোলকের পৃষ্ঠতল ও আয়তন",
            mathDomain = BcsMathDomain.MENSURATION,
            authorityBadge = CurriculumAuthorityBadge.OFFICIAL_BPSC_PRELIM_SYLLABUS,
            conceptIntroBn = "পরিমিতি অংশে দ্বিমাত্রিক ক্ষেত্রের পাশাপাশি ঘনক, সিলিন্ডার ও গোলকের আয়তন ও সমগ্রতলের ক্ষেত্রফল থেকে নিয়মিত প্রশ্ন আসে।",
            learningObjectivesBn = listOf(
                "আয়তাকার ঘনবস্তু ও ঘনকের কর্ণ, সমগ্রতলের ক্ষেত্রফল ও আয়তন নির্ণয়",
                "সমবৃত্তভূমিক বেলন ও কোণকের বক্রতলের ক্ষেত্রফল ও আয়তনের সূত্র প্রয়োগ",
                "গোলকের পৃষ্ঠতলের ক্ষেত্রফল (4πr²) ও আয়তন (4/3 πr³) নির্ণয়"
            ),
            prerequisiteKnowledgeBn = "বর্গ, ঘন এবং বৃত্তের ক্ষেত্রফল ও পরিধির সূত্র।",
            detailedExplanationBn = "ঘনবস্তুর ৩টি মাত্রা থাকে (দৈর্ঘ্য a, প্রস্থ b, উচ্চতা c)। এর আয়তন = abc ঘন একক, সমগ্রতলের ক্ষেত্রফল = 2(ab + bc + ca) বর্গ একক এবং কর্ণ = √(a² + b² + c²)।",
            importantDefinitionsBn = listOf(
                "ঘনক (Cube): যে আয়তাকার ঘনবস্তুর দৈর্ঘ্য, প্রস্থ ও উচ্চতা সমান (আয়তন = a³, সমগ্রতল = 6a², কর্ণ = a√3)",
                "বেলন বা সিলিন্ডার: আয়তন = πr²h, বক্রতল = 2πrh, সমগ্রতল = 2πr(r + h)",
                "কোণক (Cone): হেলানো উচ্চতা l = √(r² + h²), আয়তন = (1/3)πr²h"
            ),
            rulesAndPropertiesBn = listOf(
                "একই ব্যাসার্ধ ও উচ্চতাবিশিষ্ট কোণকের আয়তন সিলিন্ডারের আয়তনের এক-তৃতীয়াংশ (১/৩)।",
                "গোলকের ব্যাসার্ধ r গুণ বৃদ্ধি পেলে ক্ষেত্রফল r² গুণ এবং আয়তন r³ গুণ বৃদ্ধি পায়।"
            ),
            formulaExplanationsBn = listOf(
                "ঘনকের কর্ণ = a√3 এবং সমগ্রতলের ক্ষেত্রফল = 6a²",
                "গোলকের পৃষ্ঠতলের ক্ষেত্রফল = 4πr² এবং আয়তন = (4/3)πr³",
                "বিষমবাহু ত্রিভুজের ক্ষেত্রফল (হেরনের সূত্র) = √[s(s-a)(s-b)(s-c)]"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: একটি ঘনকের সমগ্রতলের ক্ষেত্রফল ৯৬ বর্গমিটার হলে এর কর্ণের দৈর্ঘ্য কত?\nসমাধান: 6a² = ৯৬ => a² = ১৬ => a = ৪ মিটার। কর্ণ = a√3 = 4√3 মিটার।"
            ),
            standardMethodBn = "সমগ্রতল 6a² = 96 থেকে বাহু a = 4 নির্ণয় করে কর্ণের সূত্র a√3 এ বসিয়ে 4√3 মিটার পাওয়া যায়।",
            smartShortcutMethodBn = "কর্ণ = √(সমগ্রতল / ২) = √(৯৬ / ২) = √৪৮ = 4√3 মিটার!",
            shortcutProofAndWhyItWorksBn = "কর্ণ d = a√3 => d² = 3a² = (6a²) / 2 = সমগ্রতল / ২।",
            commonExamTrapsBn = listOf(
                "গোলকের ব্যাস দেওয়া থাকলে আগে ২ দিয়ে ভাগ করে ব্যাসার্ধ (r) বের করে নিতে ভুলবেন না।"
            ),
            commonMistakesWarningBn = "বেলনের বক্রতলের ক্ষেত্রফল (2πrh) এবং সমগ্রতলের ক্ষেত্রফল (2πr(r+h)) গুলিয়ে ফেলবেন না।",
            fastCalculationTechniquesBn = listOf(
                "ঘনকের কর্ণ দেওয়া থাকলে আয়তন = (কর্ণ / √3)³।"
            ),
            quickRevisionSummaryBn = listOf(
                "ঘনকের সমগ্রতল = 6a², কর্ণ = a√3",
                "কোণকের আয়তন = (1/3)πr²h",
                "গোলকের আয়তন = (4/3)πr³"
            ),
            miniCheckQuestionBn = "একটি ঘনকের এক ধারের দৈর্ঘ্য ৩ মিটার হলে এর সমগ্রতলের ক্ষেত্রফল কত?",
            miniCheckOptionsBn = listOf("২৭ বর্গমিটার", "৩৬ বর্গমিটার", "৫৪ বর্গমিটার", "১৮ বর্গমিটার"),
            miniCheckCorrectIndex = 2,
            miniCheckExplanationBn = "সমগ্রতলের ক্ষেত্রফল = 6a² = ৬ × ৩² = ৬ × ৯ = ৫৪ বর্গমিটার।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 16,
            linkedFormulaId = "geo_pythagoras",
            linkedSolverTopicId = "GEOMETRY",
            linkedLabType = BookInteractiveLabType.MENSURATION_3D_SURFACE_VOLUME
        ),

        // Domain 6: Cartesian Geometry (Written Code 008 + Prelim Coordinate Bridge)
        BcsTopicGuideLesson(
            topicId = "bcs_math_cartesian_geometry",
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            domainTitleBn = "৬. স্থানাঙ্ক জ্যামিতি (Cartesian Geometry)",
            topicTitleBn = "স্থানাঙ্ক জ্যামিতি: দূরত্ব, ঢাল ও সরলরেখার সমীকরণ",
            topicSubtitleBn = "দুই বিন্দুর দূরত্ব, সরলরেখার ঢাল, সমান্তরাল ও লম্ব শর্ত এবং ত্রিভুজের ক্ষেত্রফল",
            isOfficialSyllabus = true,
            mathDomain = BcsMathDomain.CARTESIAN_GEOMETRY,
            authorityBadge = CurriculumAuthorityBadge.OFFICIAL_BPSC_WRITTEN_008,
            conceptIntroBn = "বিসিএস লিখিত (কোড ০০৮) সিলেবাসের অন্যতম প্রধান অধ্যায় স্থানাঙ্ক জ্যামিতি এবং প্রিলিমিনারিতেও বিন্দুর দূরত্ব ও ঢাল থেকে প্রশ্ন আসে।",
            learningObjectivesBn = listOf(
                "দুইটি বিন্দু P(x₁, y₁) এবং Q(x₂, y₂) এর মধ্যবর্তী দূরত্ব নির্ণয়",
                "সরলরেখার ঢাল m = (y₂ - y₁)/(x₂ - x₁) এবং অক্ষদ্বয়ের ছেদক নির্ণয়",
                "দুইটি সরলরেখা সমান্তরাল (m₁ = m₂) ও লম্ব (m₁·m₂ = -1) হওয়ার শর্ত প্রয়োগ"
            ),
            prerequisiteKnowledgeBn = "সংখ্যা রেখা, চতুর্ভাগ (Quadrants) ও পিথাগোরাসের উপপাদ্য।",
            detailedExplanationBn = "কার্তেসীয় সমতলে মূলবিন্দু (0,0) সাপেক্ষে যেকোনো বিন্দুর অবস্থান (x, y) দ্বারা প্রকাশ করা হয়। ভুজ (Abscissa) হলো x-স্থানাঙ্ক (y-অক্ষ থেকে দূরত্ব) এবং কোটি (Ordinate) হলো y-স্থানাঙ্ক (x-অক্ষ থেকে দূরত্ব)।",
            importantDefinitionsBn = listOf(
                "ভুজ ও কোটি: (x, y) বিন্দুর x কে ভুজ এবং y কে কোটি বলে।",
                "ঢাল (Slope): সরলরেখাটি x-অক্ষের ধনাত্মক দিকের সাথে যে কোণ θ উৎপন্ন করে তার tangent মান (m = tanθ)।",
                "মধ্যবিন্দু: ((x₁ + x₂)/2, (y₁ + y₂)/2)।"
            ),
            rulesAndPropertiesBn = listOf(
                "x-অক্ষের ওপর যেকোনো বিন্দুর কোটি y = 0; y-অক্ষের ওপর ভুজ x = 0।",
                "দুটি রেখা সমান্তরাল হলে তাদের ঢাল সমান (m₁ = m₂)।",
                "দুটি রেখা পরস্পর লম্ব হলে ঢালদ্বয়ের গুণফল -1 (m₁ × m₂ = -1)।"
            ),
            formulaExplanationsBn = listOf(
                "দূরত্ব d = √[(x₂ - x₁)² + (y₂ - y₁)²]",
                "মূলবিন্দু (0,0) থেকে (x,y) এর দূরত্ব = √(x² + y²)",
                "ঢাল আকৃতির সমীকরণ: y = mx + c",
                "উভয় অক্ষকে ছেদকারী রেখার সমীকরণ: x/a + y/b = 1"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: (3, 4) বিন্দুটি মূলবিন্দু থেকে কত দূরে অবস্থিত?\nসমাধান: d = √(৩² + ৪²) = √(৯ + ১৬) = √২৫ = ৫ একক।"
            ),
            standardMethodBn = "দূরত্বের সূত্র d = √[(x₂ - x₁)² + (y₂ - y₁)²] বসিয়ে পাই √[(3-0)² + (4-0)²] = √25 = 5 একক।",
            smartShortcutMethodBn = "পিথাগোরিয়ান ত্রয়ী (3, 4, 5) দেখামাত্র মূলবিন্দু থেকে দূরত্ব = ৫ একক!",
            shortcutProofAndWhyItWorksBn = "ভুজ ও কোটি পরস্পর লম্ব হওয়ায় মূলবিন্দু ও প্রদত্ত বিন্দুর সংযোজক রেখাংশ সমকোণী ত্রিভুজের অতিভুজ গঠন করে।",
            commonExamTrapsBn = listOf(
                "কোনো বিন্দু থেকে x-অক্ষের দূরত্ব হলো |y| (কোটির পরম মান), এবং y-অক্ষের দূরত্ব হলো |x| (ভুজের পরম মান)!"
            ),
            commonMistakesWarningBn = "(4, -3) বিন্দু থেকে x-অক্ষের দূরত্ব -3 নয়, দূরত্ব সর্বদা ধনাত্মক অর্থাৎ ৩ একক।",
            fastCalculationTechniquesBn = listOf(
                "ax + by + c = 0 রেখার ঢাল সরাসরি m = -a/b।"
            ),
            quickRevisionSummaryBn = listOf(
                "দূরত্ব = √(Δx² + Δy²)",
                "ঢাল m = Δy / Δx",
                "লম্ব রেখার শর্ত: m₁m₂ = -1"
            ),
            miniCheckQuestionBn = "(-3, 4) বিন্দুটি থেকে x-অক্ষের দূরত্ব কত একক?",
            miniCheckOptionsBn = listOf("৩ একক", "৪ একক", "৫ একক", "৭ একক"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "x-অক্ষ থেকে কোনো বিন্দুর দূরত্ব তার কোটির (y-স্থানাঙ্কের) পরম মানের সমান, অর্থাৎ |4| = ৪ একক।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 15,
            linkedFormulaId = null,
            linkedSolverTopicId = "GEOMETRY",
            linkedLabType = BookInteractiveLabType.COORDINATE_PLANE_EXPLORER
        ),

        // Domain 7: Trigonometry & Distance-Height (Written Code 008 + Complementary)
        BcsTopicGuideLesson(
            topicId = "bcs_math_trigonometry_distance",
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            domainTitleBn = "৭. ত্রিকোণমিতি ও দূরত্ব-উচ্চতা",
            topicTitleBn = "ত্রিকোণমিতিক অনুপাত, অভেদাবলি এবং দূরত্ব ও উচ্চতা",
            topicSubtitleBn = "sin, cos, tan এর আদর্শ মান, মৌলিক অভেদ এবং উন্নতি-অবনতি কোণের অঙ্ক",
            isOfficialSyllabus = true,
            mathDomain = BcsMathDomain.TRIGONOMETRY,
            authorityBadge = CurriculumAuthorityBadge.OFFICIAL_BPSC_WRITTEN_008,
            conceptIntroBn = "বিসিএস লিখিত (কোড ০০৮) পরীক্ষায় দূরত্ব ও উচ্চতা থেকে প্রতি বছর ৫ নম্বরের ১-২টি প্রশ্ন এবং প্রিলিমিনারিতে কোণের মান থেকে প্রশ্ন আসে।",
            learningObjectivesBn = listOf(
                "০°, ৩০°, ৪৫°, ৬০° ও ৯০° কোণের ত্রিকোণমিতিক অনুপাত মুখস্থ প্রয়োগ",
                "sin²θ + cos²θ = 1, sec²θ - tan²θ = 1, cosec²θ - cot²θ = 1 অভেদের ব্যবহার",
                "উন্নতি কোণ ও অবনতি কোণ ব্যবহার করে মিনার বা গাছের উচ্চতা নির্ণয়"
            ),
            prerequisiteKnowledgeBn = "সমকোণী ত্রিভুজের লম্ব, ভূমি ও অতিভুজের ধারণা।",
            detailedExplanationBn = "সূক্ষ্মকোণ θ এর বিপরীত বাহু লম্ব, সমকোণের বিপরীত বাহু অতিভুজ এবং সংলগ্ন বাহু ভূমি। উন্নতি কোণ ৩০°, ৪৫° ও ৬০° এর ক্ষেত্রে বাহুর অনুপাত মনে রাখলে ৫ সেকেন্ডে উত্তর বের হয়।",
            importantDefinitionsBn = listOf(
                "sinθ = লম্ব / অতিভুজ, cosθ = ভূমি / অতিভুজ, tanθ = লম্ব / ভূমি",
                "উন্নতি কোণ: অনুভূমিক রেখার উপরের কোনো বিন্দুর কোণ।",
                "অবনতি কোণ: অনুভূমিক রেখার নিচের কোনো বিন্দুর কোণ।"
            ),
            rulesAndPropertiesBn = listOf(
                "উন্নতি কোণ ৪৫° হলে লম্ব ও ভূমি সর্বদা সমান হয় (উচ্চতা = ছায়ার দৈর্ঘ্য)।",
                "উন্নতি কোণ ৬০° হলে লম্ব = ভূমি × √3।",
                "উন্নতি কোণ ৩০° হলে ভূমি = লম্ব × √3।"
            ),
            formulaExplanationsBn = listOf(
                "sin²θ + cos²θ = 1",
                "sec²θ - tan²θ = 1 => (secθ + tanθ)(secθ - tanθ) = 1",
                "tan 30° = 1/√3, tan 45° = 1, tan 60° = √3"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: একটি মিনারের পাদদেশ থেকে ২০ মিটার দূরে ভূতলের কোনো বিন্দুতে মিনারের চূড়ার উন্নতি কোণ ৪৫° হলে মিনারের উচ্চতা কত?\nসমাধান: tan 45° = উচ্চতা / ২০ => ১ = উচ্চতা / ২০ => উচ্চতা = ২০ মিটার।"
            ),
            standardMethodBn = "সমকোণী ত্রিভুজে tanθ = লম্ব/ভূমি বসিয়ে tan 45° = h/20 => h = 20 মিটার।",
            smartShortcutMethodBn = "৩০°-৬০°-৯০° ত্রিভুজের বাহুর অনুপাত ১ : √3 : ২ এবং ৪৫°-৪৫°-৯০° ত্রিভুজে ১ : ১ : √2।",
            shortcutProofAndWhyItWorksBn = "tan 45° = 1 হওয়ায় লম্ব ও ভূমির অনুপাত ১:১ হয়।",
            commonExamTrapsBn = listOf(
                "গাছ ভেঙে মাটি স্পর্শ করলে সম্পূর্ণ গাছের দৈর্ঘ্য = দণ্ডায়মান অংশ (লম্ব) + ভাঙা অংশ (অতিভুজ)!"
            ),
            commonMistakesWarningBn = "sin 30° = 1/2 এবং cos 60° = 1/2, কিন্তু tan 30° = 1/√3 ও tan 60° = √3—মান উল্টে ফেলবেন না।",
            fastCalculationTechniquesBn = listOf(
                "যদি secθ + tanθ = k হয়, তবে secθ - tanθ = 1/k।"
            ),
            quickRevisionSummaryBn = listOf(
                "৪৫° কোণে: উচ্চতা = ভূমি",
                "৬০° কোণে: উচ্চতা = ভূমি × √3",
                "৩০° কোণে: উচ্চতা = ভূমি / √3"
            ),
            miniCheckQuestionBn = "সূর্যের উন্নতি কোণ ৪৫° হলে ১০ মিটার উঁচু একটি খুঁটির ছায়ার দৈর্ঘ্য কত হবে?",
            miniCheckOptionsBn = listOf("৫ মিটার", "১০ মিটার", "১০√৩ মিটার", "২০ মিটার"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "উন্নতি কোণ ৪৫° হলে লম্ব (খুঁটির উচ্চতা) এবং ভূমি (ছায়ার দৈর্ঘ্য) সমান হয়, অর্থাৎ ১০ মিটার।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 9,
            linkedFormulaId = null,
            linkedSolverTopicId = "TRIGONOMETRY",
            linkedLabType = BookInteractiveLabType.TRIGONOMETRY_UNIT_CIRCLE
        ),

        // Domain 9: Statistics & Data Analysis
        BcsTopicGuideLesson(
            topicId = "bcs_math_statistics_data",
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            domainTitleBn = "৯. পরিসংখ্যান ও উপাত্ত বিশ্লেষণ",
            topicTitleBn = "পরিসংখ্যান: গড়, মধ্যক, প্রচুরক ও পরিমিত ব্যবধান",
            topicSubtitleBn = "কেন্দ্রীয় প্রবণতার পরিমাপ, ক্রমযোজিত গণসংখ্যা এবং ভেদাঙ্ক ও পরিমিত ব্যবধান",
            isOfficialSyllabus = true,
            mathDomain = BcsMathDomain.STATISTICS,
            authorityBadge = CurriculumAuthorityBadge.NCTB_FOUNDATION_BRIDGE,
            conceptIntroBn = "কেন্দ্রীয় প্রবণতার পরিমাপ (গড়, মধ্যক, প্রচুরক) এবং উপাত্তের পরিসর থেকে বিসিএস ও সরকারি চাকরি পরীক্ষায় নিয়মিত প্রশ্ন আসে।",
            learningObjectivesBn = listOf(
                "অবিন্যস্ত ও বিন্যস্ত উপাত্তের গাণিতিক গড়, মধ্যক ও প্রচুরক নির্ণয়",
                "পরিসর (Range) ও শ্রেণিসংখ্যা নির্ণয়",
                "ভেদাঙ্ক (Variance) ও পরিমিত ব্যবধান (Standard Deviation) এর সম্পর্ক জানা"
            ),
            prerequisiteKnowledgeBn = "সংখ্যার মানের ঊর্ধ্বক্রম ও গড় নির্ণয়।",
            detailedExplanationBn = "উপাত্তগুলোকে মানের ক্রমানুসারে সাজালে ঠিক মাঝের মানটি হলো মধ্যক (Median) এবং যে মানটি সবচেয়ে বেশিবার থাকে তা হলো প্রচুরক (Mode)।",
            importantDefinitionsBn = listOf(
                "গাণিতিক গড়: উপাত্তসমূহের সমষ্টি / উপাত্তের সংখ্যা।",
                "মধ্যক (Median): n বিজোড় হলে (n+1)/2 তম পদ; n জোড় হলে n/2 ও (n/2 + 1) তম পদের গড়।",
                "প্রচুরক (Mode): সর্বাধিকবার পুনরাবৃত্ত সংখ্যা।"
            ),
            rulesAndPropertiesBn = listOf(
                "পরিমিত ব্যবধান = √(ভেদাঙ্ক) অর্থাৎ ভেদাঙ্ক হলো পরিমিত ব্যবধানের বর্গ।",
                "আদর্শ সম্পর্ক: প্রচুরক = ৩ × মধ্যক - ২ × গড়।"
            ),
            formulaExplanationsBn = listOf(
                "পরিসর = (সর্বোচ্চ মান - সর্বনিম্ন মান) + ১",
                "বিজোড় n এর মধ্যক = (n + 1)/2 তম পদ",
                "প্রচুরক ≈ 3(Median) - 2(Mean)"
            ),
            workedExamplesBn = listOf(
                "উদাহরণ: ৫, ২, ৯, ৫, ৭, ৩, ৫ সংখ্যাগুলোর মধ্যক ও প্রচুরক কত?\nসমাধান: ঊর্ধ্বক্রমে সাজিয়ে: ২, ৩, ৫, ৫, ৫, ৭, ৯। মাঝের (৪র্থ) পদ = ৫ (মধ্যক) এবং সর্বাধিকবার (৩ বার) আছে ৫ (প্রচুরক)।"
            ),
            standardMethodBn = "প্রথমে উপাত্তগুলোকে ছোট থেকে বড় ক্রমে সাজিয়ে পদসংখ্যা n = ৭ (বিজোড়) হওয়ায় (৭+১)/২ = ৪র্থ পদ মধ্যক নির্ণয় করা হয়।",
            smartShortcutMethodBn = "সাজানোর পর দুই পাশ থেকে সমান সংখ্যক পদ কেটে মাঝের পদটি সরাসরি মধ্যক হিসেবে নিন।",
            shortcutProofAndWhyItWorksBn = "মধ্যক অবস্থানগত গড় হওয়ায় প্রান্তীয় মানগুলোর বর্জন মাঝের অবস্থান পরিবর্তন করে না।",
            commonExamTrapsBn = listOf(
                "উপাত্তগুলো মানের ক্রমানুসারে না সাজিয়ে সরাসরি মাঝের সংখ্যাটি মধ্যক নিলে উত্তর ভুল হবে!"
            ),
            commonMistakesWarningBn = "পরিসর নির্ণয়ে (সর্বোচ্চ - সর্বনিম্ন) এর সাথে ১ যোগ করতে হয় (শ্রেণিবিন্যস্ত উপাত্তের ক্ষেত্রে)।",
            fastCalculationTechniquesBn = listOf(
                "১ থেকে n পর্যন্ত ক্রমিক স্বাভাবিক সংখ্যার গড় ও মধ্যক উভয়ই (n + 1) / 2।"
            ),
            quickRevisionSummaryBn = listOf(
                "মধ্যক করার আগে অবশ্যই ক্রমানুসারে সাজাতে হবে",
                "ভেদাঙ্ক = (পরিমিত ব্যবধান)²",
                "Mode = 3×Median - 2×Mean"
            ),
            miniCheckQuestionBn = "৪, ৮, ২, ৬, ১০ সংখ্যাগুলোর মধ্যক কত?",
            miniCheckOptionsBn = listOf("২", "৬", "৮", "৫"),
            miniCheckCorrectIndex = 1,
            miniCheckExplanationBn = "মানের ক্রমানুসারে সাজিয়ে পাই: ২, ৪, ৬, ৮, ১০। মাঝের (৩য়) পদটি হলো ৬।",
            linkedNctbBookId = BookId.CLASS_9_10,
            linkedNctbChapterNumber = 17,
            linkedFormulaId = null,
            linkedSolverTopicId = "STATISTICS",
            linkedLabType = BookInteractiveLabType.STATISTICS_HISTOGRAM_LAB
        )
    )

    /**
     * Concept Guides for all 6 Official BPSC Mental Ability Categories (including Written Code 009 Abstract Reasoning).
     */
    val mentalAbilityCategoryGuides: List<BcsMentalAbilityCategoryGuide> = listOf(
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategory.VERBAL_REASONING,
            corePrinciplesBn = listOf(
                "শব্দজোড়ের মধ্যকার মূল সম্পর্ক (কার্য-কারণ, যন্ত্র-ব্যবহারকারী, সমার্থক/বিপরীতার্থক, অংশ-সমগ্র) প্রথমে এক বাক্যে তৈরি করুন।",
                "বিবৃতি ও সিদ্ধান্ত (Syllogism/Assertion) প্রশ্নে নিজের পূর্বধারণা বাদ দিয়ে কেবল প্রদত্ত তথ্যের যৌক্তিক সীমা বিবেচনা করুন।"
            ),
            solvingRulesBn = listOf(
                "ধাপ ১: প্রদত্ত শব্দজোড় দিয়ে একটি সুনির্দিষ্ট সম্পর্ক-বাক্য গঠন করুন।",
                "ধাপ ২: বিকল্পগুলোর মধ্যে যে জোড়াটি অবিকল একই বাক্য-কাঠামো মেনে চলে সেটি নির্বাচন করুন।"
            ),
            commonTrapsBn = listOf(
                "সম্পর্কের ক্রম উল্টে যাওয়া (যেমন 'লেখক : কলম' এর বদলে 'কাঁচি : দর্জি' নির্বাচন করা)।"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ ভাষাগত যুক্তি, রক্তের সম্পর্ক (Blood Relation) এবং যৌক্তিক অনুমান থেকে ৮–১০ নম্বরের প্রশ্ন থাকে।"
        ),
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategory.PROBLEM_SOLVING,
            corePrinciplesBn = listOf(
                "ঘড়ির কাঁটার মধ্যবর্তী কোণ নির্ণয়ে θ = |(60H - 11M) / 2| সূত্রটি সর্বদা প্রযোজ্য (ফল ১৮০° এর বড় হলে ৩৬০° থেকে বিয়োগ করুন)।",
                "ক্যালেন্ডার অঙ্কে সাধারণ বছরে ১টি অতিরিক্ত দিন (Odd Day) এবং অধিবর্ষে (Leap Year) ২টি অতিরিক্ত দিন যোগ হয়।"
            ),
            solvingRulesBn = listOf(
                "সারি বা র‍্যাংকিং অঙ্কে: মোট ব্যক্তি = (বাম/উপর থেকে অবস্থান + ডান/নিচ থেকে অবস্থান) - ১।",
                "বয়সের অঙ্কে বর্তমান বয়সকে ভিত্তি ধরে পূর্ব বা পরের অনুপাত তুলনা করুন।"
            ),
            commonTrapsBn = listOf(
                "শতাব্দী বছর (যেমন ১৯০০, ২১০০) ৪ দ্বারা বিভাজ্য হলেও ৪০০ দ্বারা বিভাজ্য না হলে অধিবর্ষ (Leap Year) নয়!"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ ক্যালেন্ডার, ঘড়ি, বয়স ও ক্রমবিন্যাস পাজল থেকে ১০–১২টি প্রশ্ন আসে।"
        ),
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategoryGuide::class.let { BcsMentalAbilityCategory.SPELLING_LANGUAGE },
            corePrinciplesBn = listOf(
                "বাংলা একাডেমি প্রমিত বানান রীতি (ই-কার/ঈ-কার, উ-কার/ঊ-কার, ণ-ত্ব ও ষ-ত্ব বিধান) অনুসরণ করুন।",
                "অভিধান ক্রম (Dictionary Order) নির্ণয়ে যুক্তবর্ণকে ভেঙে মূল বর্ণের ক্রম বিবেচনা করতে হয় (যেমন ক্ষ = ক + ষ)।"
            ),
            solvingRulesBn = listOf(
                "ইংরেজি বর্ণমালায় অবস্থান মনে রাখতে EJOTY (5, 10, 15, 20, 25) এবং বিপরীত বর্ণের যোগফল ২৭ সূত্র ব্যবহার করুন।"
            ),
            commonTrapsBn = listOf(
                "মুহুর্মুহু, বিভীষিকা, মরীচিকা, সমীচীন, পিপীলিকা—এই শব্দগুলোর হ্রস্ব/দীর্ঘ স্বরচিহ্নে ভুল হওয়া।"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ বাংলা ও ইংরেজি বানান শুদ্ধি, অ্যানাগ্রাম ও শব্দগঠন থেকে ৬–৮টি প্রশ্ন থাকে।"
        ),
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategory.MECHANICAL_REASONING,
            corePrinciplesBn = listOf(
                "পরস্পর সংযুক্ত দুটি গিয়ার চাকা সর্বদা বিপরীত দিকে ঘোরে; বিজোড় সংখ্যক গিয়ারে ১ম ও শেষ চাকা একই দিকে ঘোরে।",
                "স্থির কপিকলে বলের মান কমে না (যান্ত্রিক সুবিধা ১), কেবল দিক বদলায়; সচল কপিকলে ওজন অর্ধেক বল দিয়ে তোলা যায়।"
            ),
            solvingRulesBn = listOf(
                "লিভারের ভারসাম্য সূত্র: বল × বলবাহুর দৈর্ঘ্য = ভার × ভারবাহুর দৈর্ঘ্য (W₁ × d₁ = W₂ × d₂)।"
            ),
            commonTrapsBn = listOf(
                "বেল্ট দিয়ে যুক্ত দুটি পুলি একই দিকে ঘোরে, কিন্তু ক্রস-বেল্ট (Crossed Belt) থাকলে বিপরীত দিকে ঘোরে!"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ গিয়ার, পুলি, লিভার ও সেন্টার অব গ্র্যাভিটি চিত্রভিত্তিক প্রশ্ন থাকে।"
        ),
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategory.SPACE_RELATION,
            corePrinciplesBn = listOf(
                "দিক নির্ণয়ে মানচিত্রের উপরের দিক উত্তর (N), নিচে দক্ষিণ (S), ডানে পূর্ব (E) এবং বামে পশ্চিম (W); সংক্ষিপ্ততম দূরত্ব নির্ণয়ে পিথাগোরাস প্রয়োগ করুন।",
                "দর্পণ প্রতিবিম্বে (Mirror Image) বাম-ডান পরিবর্তিত হয় (ঘড়ির সময় ১১:৬০ থেকে বিয়োগ); পানির প্রতিবিম্বে উপর-নিচ পরিবর্তিত হয়।",
                "বিমূর্ত যুক্তি (Abstract Reasoning - কোড ০০৯): চিত্রের ঘূর্ণন (45°, 90°, 180°), বাহুর সংখ্যা বৃদ্ধি এবং ছায়াযুক্ত অংশের পর্যায়ক্রম লক্ষ্য করুন।"
            ),
            solvingRulesBn = listOf(
                "ত্রিভুজ গণনায় ভূমি n খণ্ডে বিভক্ত হলে মোট ত্রিভুজ = n(n+1)/2; বর্গক্ষেত্রে কর্ণদ্বয় টানা থাকলে ত্রিভুজ সংখ্যা = খণ্ড সংখ্যা × ২।"
            ),
            commonTrapsBn = listOf(
                "দক্ষিণ দিকে মুখ করে বামে ঘোরা মানে পূর্ব দিকে ঘোরা (পশ্চিম নয়)!"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ স্থানিক সম্পর্ক ও বিমূর্ত চিত্র প্যাটার্ন (Abstract Non-Verbal Reasoning) থেকে ৮–১০টি প্রশ্ন আসে।"
        ),
        BcsMentalAbilityCategoryGuide(
            category = BcsMentalAbilityCategory.NUMERICAL_ABILITY,
            corePrinciplesBn = listOf(
                "সংখ্যার ধারায় প্রথমে পরপর পদের অন্তর (১ম ও ২য় স্তরের পার্থক্য), এরপর গুণিতক (±k) এবং সবশেষে বর্গ/ঘন (n² ± 1, n³ ± 1) পরীক্ষা করুন।",
                "ম্যাট্রিক্স পাজলে সারি বরাবর অথবা কলাম বরাবর যোগফল, গুণফল বা বর্গের অন্তর মিলে কি না দেখুন।"
            ),
            solvingRulesBn = listOf(
                "কোডিং-ডিকোডিংয়ে প্রতিটি অক্ষরের পজিশন ভ্যালু লিখে (+1, -1, +2) বা বিপরীত অক্ষর (27 - pos) মিলিয়ে দেখুন।"
            ),
            commonTrapsBn = listOf(
                "যুগ্ম ধারা (Alternating Series) যেখানে বিজোড় স্থান ও জোড় স্থানের পদগুলো দুটি আলাদা নিয়মে চলে।"
            ),
            writtenCode009NotesBn = "লিখিত কোড ০০৯-এ সংখ্যা ম্যাট্রিক্স, কোডিং-ডিকোডিং ও গাণিতিক পাজল থেকে ১০–১২টি প্রশ্ন থাকে।"
        )
    )

    /**
     * Dedicated Mental Ability Questions across all 6 BPSC Categories with Canvas Diagram Metadata.
     */
    val mentalAbilityQuestions: List<BcsMentalAbilityQuestion> = listOf(
        // 1. Mechanical Reasoning - Gear System
        BcsMentalAbilityQuestion(
            id = "bcs_ma_mech_gear_1",
            category = BcsMentalAbilityCategory.MECHANICAL_REASONING,
            topicBn = "গিয়ার চাকা ও ঘূর্ণন",
            questionBn = "চিত্রের ৩টি পরস্পর সংযুক্ত গিয়ারের মধ্যে প্রথম গিয়ার চাকাটি ঘড়ির কাঁটার দিকে (Clockwise) ঘুরলে ৩য় গিয়ার চাকাটি কোন দিকে ঘুরবে?",
            optionsBn = listOf("ঘড়ির কাঁটার দিকে", "ঘড়ির কাঁটার বিপরীত দিকে", "স্থির থাকবে", "উভয় দিকে"),
            correctIndex = 0,
            explanationBn = "পরস্পর সংযুক্ত গিয়ারে দুটি পাশাপাশি চাকা বিপরীত দিকে ঘোরে। ১ম চাকা ঘড়ির কাঁটার দিকে ঘুরলে ২য় চাকা ঘড়ির বিপরীত দিকে ঘুরবে, এবং ৩য় চাকা আবার ঘড়ির কাঁটার দিকে ঘুরবে।",
            shortcutTipBn = "বিজোড় সংখ্যক সংযুক্ত চাকার ১ম ও শেষ চাকা একই দিকে ঘোরে; জোড় সংখ্যক হলে বিপরীত দিকে ঘোরে।",
            diagramType = BcsDiagramType.GEAR_ROTATION,
            diagramData = "GEARS:3:CW",
            examReferenceBn = "৪০তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 40,
            examYear = 2019
        ),

        // 2. Mechanical Reasoning - Pulley System
        BcsMentalAbilityQuestion(
            id = "bcs_ma_mech_pulley_1",
            category = BcsMentalAbilityCategory.MECHANICAL_REASONING,
            topicBn = "কপিকল ও যান্ত্রিক সুবিধা",
            questionBn = "একটি স্থির কপিকল (Fixed Pulley) ব্যবহার করে ১০০ কেজি ওজনের বস্তু তুলতে ন্যূনতম কত বল প্রয়োগ করতে হবে?",
            optionsBn = listOf("৫০ কেজি", "১০০ কেজি", "২০০ কেজি", "২৫ কেজি"),
            correctIndex = 1,
            explanationBn = "স্থির কপিকলে যান্ত্রিক সুবিধা ১, এটি কেবল বল প্রয়োগের দিক পরিবর্তন করে, বলের পরিমাণ কমায় না। তাই ১০০ কেজি তুলতে ১০০ কেজি বলই লাগবে। চলনশীল কপিকল হলে বল অর্ধেক (৫০ কেজি) হতো।",
            shortcutTipBn = "স্থির কপিকল = বল একই থাকে; ১টি সচল কপিকল = বল অর্ধেক হয়।",
            diagramType = BcsDiagramType.PULLEY_SYSTEM,
            diagramData = "PULLEY:FIXED:100",
            examReferenceBn = "৪১তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 41,
            examYear = 2021
        ),

        // 3. Space Relation - Compass Direction
        BcsMentalAbilityQuestion(
            id = "bcs_ma_space_compass_1",
            category = BcsMentalAbilityCategory.SPACE_RELATION,
            topicBn = "দিক ও দূরত্ব নির্ণয়",
            questionBn = "রাকিব তার বাড়ি থেকে উত্তর দিকে ৪ কিমি হাঁটল, এরপর ডানদিকে মোড় নিয়ে ৩ কিমি হাঁটল। সে শুরুর স্থান থেকে সরলরেখায় কত দূরে আছে?",
            optionsBn = listOf("৭ কিমি", "৫ কিমি", "১ কিমি", "১২ কিমি"),
            correctIndex = 1,
            explanationBn = "উত্তর ও পূর্বের গতিপথ পরস্পরের সমকোণ তৈরি করে। পিথাগোরাসের উপপাদ্য অনুযায়ী দূরত্ব = √(৪² + ৩²) = √(১৬ + ৯) = √২৫ = ৫ কিমি।",
            shortcutTipBn = "পিথাগোরাসের ট্রিপলেট ৩-৪-৫ মনে রাখুন।",
            diagramType = BcsDiagramType.COMPASS_DIRECTION,
            diagramData = "PATH:N4:E3",
            examReferenceBn = "৪৪তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 44,
            examYear = 2022
        ),

        // 4. Space Relation - Triangle Counting
        BcsMentalAbilityQuestion(
            id = "bcs_ma_space_triangles_1",
            category = BcsMentalAbilityCategory.SPACE_RELATION,
            topicBn = "জ্যামিতিক চিত্র গণনা",
            questionBn = "প্রদত্ত চিত্রে একটি বড় ত্রিভুজের শীর্ষবিন্দু থেকে ভূমিতে ৩টি সরলরেখা টেনে ভূমিকে ৪টি অংশে ভাগ করা হলে চিত্রে মোট কয়টি ত্রিভুজ আছে?",
            optionsBn = listOf("৪টি", "৮টি", "১০টি", "১২টি"),
            correctIndex = 2,
            explanationBn = "ভূমি ৪টি খণ্ডে বিভক্ত হলে মোট ত্রিভুজ সংখ্যা = ১ + ২ + ৩ + ৪ = ১০টি।",
            shortcutTipBn = "ত্রিভুজের ভূমি খণ্ড n হলে মোট ত্রিভুজ = n(n+1)/2 = ৪×৫/২ = ১০।",
            diagramType = BcsDiagramType.TRIANGLE_COUNT,
            diagramData = "TRIANGLE:BASE_DIV_4",
            examReferenceBn = "৪৩তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 43,
            examYear = 2021
        ),

        // 5. Verbal Reasoning - Analogy
        BcsMentalAbilityQuestion(
            id = "bcs_ma_verbal_analogy_1",
            category = BcsMentalAbilityCategory.VERBAL_REASONING,
            topicBn = "শব্দজোড় সাদৃশ্য (Analogy)",
            questionBn = "কলম : লেখক :: কাঁচি : ?",
            optionsBn = listOf("দর্জি", "কাপড়", "কাটা", "সুতা"),
            correctIndex = 0,
            explanationBn = "কলম হলো লেখকের প্রধান কাজের হাতিয়ার বা যন্ত্র। তেমনি কাঁচি হলো দর্জির প্রধান হাতিয়ার।",
            shortcutTipBn = "হাতিয়ার : ব্যবহারকারী সম্পর্ক নির্ণয় করুন।",
            diagramType = BcsDiagramType.NONE,
            examReferenceBn = "৪৫তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 45,
            examYear = 2023
        ),

        // 6. Problem Solving - Clock Angle
        BcsMentalAbilityQuestion(
            id = "bcs_ma_problem_clock_1",
            category = BcsMentalAbilityCategory.PROBLEM_SOLVING,
            topicBn = "ঘড়ির কোণ নির্ণয়",
            questionBn = "২টা ১৫ মিনিটে ঘড়ির ঘণ্টার কাঁটা ও মিনিটের কাঁটার মধ্যবর্তী কোণ কত ডিগ্রি?",
            optionsBn = listOf("৩০°", "২২.৫°", "৪৫°", "৩৭.৫°"),
            correctIndex = 1,
            explanationBn = "সূত্র: |(৬০H - ১১M) / ২| = |(৬০×২ - ১১×১৫) / ২| = |(১২০ - ১৬৫) / ২| = |-৪৫ / ২| = ২২.৫ ডিগ্রি।",
            shortcutTipBn = "ঘড়ির কোণের সার্বজনীন সূত্র: θ = |(60H - 11M) / 2|।",
            diagramType = BcsDiagramType.NONE,
            examReferenceBn = "৩৭তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 37,
            examYear = 2016
        ),

        // 7. Spelling & Language - Word Formation
        BcsMentalAbilityQuestion(
            id = "bcs_ma_spelling_lang_1",
            category = BcsMentalAbilityCategory.SPELLING_LANGUAGE,
            topicBn = "শুদ্ধ বানান নির্ণয়",
            questionBn = "নিচের কোন বানানটি শুদ্ধ?",
            optionsBn = listOf("মুহুর্মুহু", "মুহূর্মুহু", "মুহুর্মুহূ", "মুহূর্মুহূ"),
            correctIndex = 0,
            explanationBn = "শুদ্ধ বানান হলো 'মুহুর্মুহু' (উভয় অংশেই হ-এ হ্রস্ব উ-কার)।",
            shortcutTipBn = "মুহুর্মুহু শব্দে সবগুলোই হ্রস্ব উ-কার (ু)।",
            diagramType = BcsDiagramType.NONE,
            examReferenceBn = "৩৮তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 38,
            examYear = 2017
        ),

        // 8. Numerical Ability - Missing Number
        BcsMentalAbilityQuestion(
            id = "bcs_ma_numerical_series_1",
            category = BcsMentalAbilityCategory.NUMERICAL_ABILITY,
            topicBn = "সংখ্যার ধারা ও প্যাটার্ন",
            questionBn = "২, ৩, ৫, ৯, ১৭, ৩৩, ... পরবর্তী সংখ্যাটি কত?",
            optionsBn = listOf("৬৫", "৬৬", "৬৪", "৪৮"),
            correctIndex = 0,
            explanationBn = "অন্তরগুলোর প্যাটার্ন: ৩-২=১, ৫-৩=২, ৯-৫=৪, ১৭-৯=৮, ৩৩-১৭=১৬। পরবর্তী অন্তর হবে ৩২। সুতরাং ৩৩ + ৩২ = ৬৫।",
            shortcutTipBn = "সংখ্যার দ্বিগুণ থেকে ১ বিয়োগ: (৩৩ × ২) - ১ = ৬৫।",
            diagramType = BcsDiagramType.NONE,
            examReferenceBn = "৪২তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 42,
            examYear = 2021
        ),

        // 9. Space Relation - Mirror Reflection
        BcsMentalAbilityQuestion(
            id = "bcs_ma_space_mirror_1",
            category = BcsMentalAbilityCategory.SPACE_RELATION,
            topicBn = "দর্পণ প্রতিবিম্ব ও ঘড়ি",
            questionBn = "একটি দেয়াল ঘড়িতে যখন ৩টা ৪০ মিনিট বাজে, তখন বিপরীত দেয়ালে রক্ষিত আয়নায় ঘড়ির প্রতিবিম্বে কয়টা বাজতে দেখা যাবে?",
            optionsBn = listOf("৮টা ২০ মিনিট", "৯টা ২০ মিনিট", "৮টা ৪০ মিনিট", "৭টা ২০ মিনিট"),
            correctIndex = 0,
            explanationBn = "আয়নায় ঘড়ির প্রতিবিম্বের সময় নির্ণয়ে ১১টা ৬০ মিনিট থেকে প্রদত্ত সময় বিয়োগ করতে হয়: ১১:৬০ - ৩:৪০ = ৮টা ২০ মিনিট।",
            shortcutTipBn = "দর্পণ সময় = ১১:৬০ - প্রকৃত সময়।",
            diagramType = BcsDiagramType.MIRROR_REFLECTION,
            diagramData = "MIRROR:CLOCK:3_40",
            examReferenceBn = "৪৬তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 46,
            examYear = 2024
        ),

        // 10. Numerical Ability - Number Matrix
        BcsMentalAbilityQuestion(
            id = "bcs_ma_numerical_matrix_1",
            category = BcsMentalAbilityCategory.NUMERICAL_ABILITY,
            topicBn = "সংখ্যা ম্যাট্রিক্স ও মিসিং নাম্বার",
            questionBn = "প্রদত্ত ৩×৩ সংখ্যা ম্যাট্রিক্সের ১ম সারি (৪, ৯, ২), ২য় সারি (৩, ৫, ৭) এবং ৩য় সারি (৮, ১, ?) হলে প্রশ্নবোধক স্থানে কত বসবে?",
            optionsBn = listOf("৫", "৬", "৭", "৯"),
            correctIndex = 1,
            explanationBn = "এটি একটি ৩×৩ ম্যাজিক বর্গ যেখানে প্রতিটি সারি, কলাম ও কর্ণের যোগফল ১৫। ৩য় সারিতে ৮ + ১ + ? = ১৫ => ? = ৬।",
            shortcutTipBn = "৩×৩ ম্যাজিক বর্গের ম্যাজিক সমষ্টি সর্বদা ১৫।",
            diagramType = BcsDiagramType.NUMBER_MATRIX,
            diagramData = "MATRIX:4,9,2|3,5,7|8,1,?",
            examReferenceBn = "৩৯তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 39,
            examYear = 2018
        ),

        // 11. Space Relation / Written 009 Abstract Reasoning
        BcsMentalAbilityQuestion(
            id = "bcs_ma_abstract_rotation_1",
            category = BcsMentalAbilityCategory.SPACE_RELATION,
            topicBn = "বিমূর্ত যুক্তি ও চিত্র প্যাটার্ন (কোড ০০৯)",
            questionBn = "একটি বিমূর্ত চিত্রের ধারায় ১ম চিত্রে তীরচিহ্ন উত্তর দিকে, ২য় চিত্রে ঘড়ির কাঁটার দিকে ৯০° ঘুরে পূর্ব দিকে, ৩য় চিত্রে দক্ষিণ দিকে থাকলে ৪র্থ চিত্রে কোন দিকে থাকবে?",
            optionsBn = listOf("পশ্চিম দিকে", "উত্তর দিকে", "উত্তর-পূর্ব দিকে", "দক্ষিণ-পশ্চিম দিকে"),
            correctIndex = 0,
            explanationBn = "প্রতি ধাপে তীরচিহ্নটি ঘড়ির কাঁটার দিকে ৯০° ঘুরছে: উত্তর -> পূর্ব -> দক্ষিণ -> পশ্চিম।",
            shortcutTipBn = "৯০° ঘূর্ণন ধারায় ৪র্থ পদ সর্বদা ২য় পদের বিপরীত দিক হয়।",
            diagramType = BcsDiagramType.COMPASS_DIRECTION,
            diagramData = "ROTATE:90:CW",
            examReferenceBn = "বিসিএস লিখিত মানসিক দক্ষতা (কোড ০০৯)",
            provenanceTier = BcsProvenanceTier.CONCEPT_DERIVED_PRACTICE
        ),

        // 12. Problem Solving - Calendar Leap Year & Day Calculation
        BcsMentalAbilityQuestion(
            id = "bcs_ma_problem_calendar_1",
            category = BcsMentalAbilityCategory.PROBLEM_SOLVING,
            topicBn = "ক্যালেন্ডার ও বার নির্ণয়",
            questionBn = "যদি কোনো সাধারণ বছরের ১লা জানুয়ারি শুক্রবার হয়, তবে ঐ বছরের ৩১শে ডিসেম্বর কী বার হবে?",
            optionsBn = listOf("শুক্রবার", "শনিবার", "রবিবার", "বৃহস্পতিবার"),
            correctIndex = 0,
            explanationBn = "সাধারণ বছরে ৩৬৫ দিন = ৫২ সপ্তাহ + ১ দিন। তাই সাধারণ বছর যে বার দিয়ে শুরু হয়, ঠিক সেই বার দিয়েই শেষ হয় (শুক্রবার)। অধিবর্ষ হলে ১ দিন বেড়ে শনিবার হতো।",
            shortcutTipBn = "সাধারণ বছরের প্রথম দিন = শেষ দিন; অধিবর্ষের শেষ দিন = প্রথম দিন + ১।",
            diagramType = BcsDiagramType.NONE,
            examReferenceBn = "৪০তম বিসিএস মানসিক দক্ষতা",
            provenanceTier = BcsProvenanceTier.VERIFIED_OFFICIAL_PAPER,
            bcsExamNumber = 40,
            examYear = 2019
        )
    )

    fun getLesson(topicId: String): BcsTopicGuideLesson? =
        mathGuideLessons.firstOrNull { it.topicId == topicId }

    fun getLessonsForUnit(unit: BcsSyllabusUnit): List<BcsTopicGuideLesson> =
        mathGuideLessons.filter { it.unit == unit }

    fun getLessonsForDomain(domain: BcsMathDomain): List<BcsTopicGuideLesson> =
        mathGuideLessons.filter { it.mathDomain == domain }

    fun getMentalAbilityGuideForCategory(category: BcsMentalAbilityCategory): BcsMentalAbilityCategoryGuide? =
        mentalAbilityCategoryGuides.firstOrNull { it.category == category }

    fun getMentalAbilityQuestionsForCategory(category: BcsMentalAbilityCategory?): List<BcsMentalAbilityQuestion> =
        if (category == null) mentalAbilityQuestions else mentalAbilityQuestions.filter { it.category == category }
}

/**
 * Weak-Topic Intelligence Alert generated from user accuracy, mistake count, solving speed, and mini-check state.
 */
data class BcsWeakTopicDiagnostic(
    val topicId: String,
    val topicTitleBn: String,
    val subjectTrack: String,
    val masteryPercent: Int,
    val unresolvedMistakeCount: Int,
    val avgTimePerQuestionSeconds: Int,
    val miniCheckPassed: Boolean,
    val reasonBadgesBn: List<String>,
    val recommendedActionBn: String,
    val linkedBookId: BookId? = null,
    val linkedChapterNumber: Int? = null
)

object BcsWeakTopicIntelligenceEngine {
    /**
     * Evaluates topic progress and mistake notebook entries for the active exam profile to surface weak topics.
     */
    fun analyzeWeakTopics(
        examProfileId: String,
        topicProgressList: List<com.example.data.model.BcsTopicProgressEntity>,
        mistakesList: List<com.example.data.model.BcsMistakeEntity>
    ): List<BcsWeakTopicDiagnostic> {
        val profileProgress = topicProgressList.filter {
            it.examProfileId == examProfileId || (examProfileId == "BCS_50TH_PRELIM_2025" && !it.topicId.contains("::"))
        }.associateBy { it.rawTopicId }

        val profileMistakes = mistakesList.filter {
            !it.isResolved && (it.examProfileId == examProfileId || (examProfileId == "BCS_50TH_PRELIM_2025" && !it.questionId.contains("::")))
        }

        val diagnostics = mutableListOf<BcsWeakTopicDiagnostic>()

        BcsCurriculumAndGuideRepository.mathGuideLessons.forEach { lesson ->
            val prog = profileProgress[lesson.topicId]
            val topicMistakes = profileMistakes.filter {
                it.topicId == lesson.topicId || it.topicId == lesson.unit.id
            }
            val unresolvedCount = topicMistakes.sumOf { it.mistakeCount }
            val attempted = prog?.questionsAttempted ?: 0
            val correct = prog?.questionsCorrect ?: 0
            val accuracy = if (attempted > 0) (correct * 100) / attempted else prog?.masteryPercent ?: 0
            val avgSeconds = if (attempted > 0) (prog?.totalTimeSeconds ?: 0) / attempted else 0
            val miniCheckDone = prog?.miniCheckPassed ?: false

            val reasons = mutableListOf<String>()
            if (prog != null && accuracy < 65) {
                reasons.add("কম নির্ভুলতা ($accuracy%)")
            }
            if (unresolvedCount >= 1) {
                reasons.add("পুনরাবৃত্ত ভুল ($unresolvedCount বার)")
            }
            if (avgSeconds > 65) {
                reasons.add("ধীর গতি (${avgSeconds} সে./প্রশ্ন)")
            }
            if (prog != null && prog.isGuideCompleted && !miniCheckDone) {
                reasons.add("মিনি-চেক অসম্পূর্ণ")
            }

            if (reasons.isNotEmpty()) {
                diagnostics.add(
                    BcsWeakTopicDiagnostic(
                        topicId = lesson.topicId,
                        topicTitleBn = lesson.topicTitleBn,
                        subjectTrack = "MATH",
                        masteryPercent = prog?.masteryPercent ?: accuracy,
                        unresolvedMistakeCount = unresolvedCount,
                        avgTimePerQuestionSeconds = avgSeconds,
                        miniCheckPassed = miniCheckDone,
                        reasonBadgesBn = reasons,
                        recommendedActionBn = when {
                            unresolvedCount >= 2 -> "মিসটেক নোটবুক ও এনসিটিবি অধ্যায় ${lesson.linkedNctbChapterNumber ?: 1} রিভিশন দিন"
                            avgSeconds > 65 -> "স্মার্ট শর্টকাট কার্ড পড়ে ১৫ প্রশ্নের স্পিড ড্রিল দিন"
                            else -> "গাইড লেসনের মিনি-চেক সম্পন্ন করে ১০টি টপিক প্রশ্ন চর্চা করুন"
                        },
                        linkedBookId = lesson.linkedNctbBookId,
                        linkedChapterNumber = lesson.linkedNctbChapterNumber
                    )
                )
            }
        }

        return diagnostics.sortedWith(
            compareByDescending<BcsWeakTopicDiagnostic> { it.unresolvedMistakeCount }
                .thenBy { it.masteryPercent }
        )
    }
}

/**
 * Bidirectional Cross-Module Curriculum Bridge between Module 13 (BCS) and Module 14 (NCTB Textbooks),
 * plus Formula Library (Module 05), Problem Solver (Module 09), and Interactive Labs.
 */
data class CrossModuleMappingEntry(
    val bcsTopicId: String,
    val bcsUnit: BcsSyllabusUnit,
    val bcsMathDomain: BcsMathDomain,
    val bcsTopicTitleBn: String,
    val bookId: BookId,
    val chapterNumber: Int,
    val chapterTitleBn: String,
    val formulaId: String?,
    val solverTopicId: String?,
    val labType: BookInteractiveLabType?,
    val authorityBadge: CurriculumAuthorityBadge,
    val bridgeNoteBn: String
)

object CrossModuleCurriculumBridge {
    val mappings: List<CrossModuleMappingEntry> by lazy {
        val guideEntries = BcsCurriculumAndGuideRepository.mathGuideLessons.mapNotNull { lesson ->
            val bId = lesson.linkedNctbBookId ?: return@mapNotNull null
            val chNum = lesson.linkedNctbChapterNumber ?: return@mapNotNull null
            val chTitle = BookCurriculumRepository.getChapter(bId, chNum)?.titleBn ?: "অধ্যায় $chNum"
            CrossModuleMappingEntry(
                bcsTopicId = lesson.topicId,
                bcsUnit = lesson.unit,
                bcsMathDomain = lesson.mathDomain,
                bcsTopicTitleBn = lesson.topicTitleBn,
                bookId = bId,
                chapterNumber = chNum,
                chapterTitleBn = chTitle,
                formulaId = lesson.linkedFormulaId,
                solverTopicId = lesson.linkedSolverTopicId,
                labType = lesson.linkedLabType,
                authorityBadge = lesson.authorityBadge,
                bridgeNoteBn = "${bId.classLevelBn} • $chTitle থেকে বিসিএস ${lesson.mathDomain.titleBn} ভিত্তি"
            )
        }

        val additionalEntries = listOf(
            CrossModuleMappingEntry(
                bcsTopicId = "bcs_math_real_numbers",
                bcsUnit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
                bcsMathDomain = BcsMathDomain.ARITHMETIC,
                bcsTopicTitleBn = "ল.সা.গু, গ.সা.গু ও বিভাজ্যতা",
                bookId = BookId.CLASS_6,
                chapterNumber = 1,
                chapterTitleBn = "স্বাভাবিক সংখ্যা ও ভগ্নাংশ",
                formulaId = null,
                solverTopicId = "NUMBER_SYSTEM",
                labType = BookInteractiveLabType.PRIME_FACTORIZATION_SIEVE,
                authorityBadge = CurriculumAuthorityBadge.NCTB_FOUNDATION_BRIDGE,
                bridgeNoteBn = "ষষ্ঠ শ্রেণি • অধ্যায় ১ থেকে মৌলিক উৎপাদক, ল.সা.গু ও গ.সা.গু ভিত্তি"
            ),
            CrossModuleMappingEntry(
                bcsTopicId = "bcs_math_percentage_ratio",
                bcsUnit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
                bcsMathDomain = BcsMathDomain.ARITHMETIC,
                bcsTopicTitleBn = "অনুপাত, সমানুপাত ও লাভ-ক্ষতি",
                bookId = BookId.CLASS_7,
                chapterNumber = 2,
                chapterTitleBn = "সমানুপাত ও লাভ-ক্ষতি",
                formulaId = null,
                solverTopicId = "ARITHMETIC",
                labType = BookInteractiveLabType.RATIO_PROPORTION_SCALER,
                authorityBadge = CurriculumAuthorityBadge.NCTB_FOUNDATION_BRIDGE,
                bridgeNoteBn = "সপ্তম শ্রেণি • অধ্যায় ২ থেকে ধারাবাহিক অনুপাত ও লাভ-ক্ষতির ভিত্তি"
            ),
            CrossModuleMappingEntry(
                bcsTopicId = "bcs_math_geometry_mensuration",
                bcsUnit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
                bcsMathDomain = BcsMathDomain.GEOMETRY,
                bcsTopicTitleBn = "বৃত্ত সংক্রান্ত উপপাদ্য ও স্পর্শক",
                bookId = BookId.CLASS_8,
                chapterNumber = 10,
                chapterTitleBn = "বৃত্ত",
                formulaId = "geo_pythagoras",
                solverTopicId = "GEOMETRY",
                labType = BookInteractiveLabType.CIRCLE_CHORD_TANGENT_THEOREM,
                authorityBadge = CurriculumAuthorityBadge.NCTB_FOUNDATION_BRIDGE,
                bridgeNoteBn = "অষ্টম শ্রেণি • অধ্যায় ১০ থেকে বৃত্তের জ্যা, ব্যাস ও পরিধির ভিত্তি"
            )
        )
        guideEntries + additionalEntries
    }

    fun getMappingsForBookChapter(bookId: BookId, chapterNumber: Int): List<CrossModuleMappingEntry> {
        val exact = mappings.filter { it.bookId == bookId && it.chapterNumber == chapterNumber }
        if (exact.isNotEmpty()) return exact
        // Fallback by domain/chapter keyword so every NCTB chapter shows its relevant BCS connection
        val chapter = BookCurriculumRepository.getChapter(bookId, chapterNumber)
        val title = chapter?.titleBn.orEmpty()
        return when {
            title.contains("বীজগণিত") || title.contains("সমীকরণ") || title.contains("উৎপাদক") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.ALGEBRA }.take(1)
            title.contains("সূচক") || title.contains("লগারিদম") || title.contains("ধারা") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.SEQUENCES_SERIES }.take(1)
            title.contains("ত্রিকোণমিতি") || title.contains("দূরত্ব ও উচ্চতা") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.TRIGONOMETRY }.take(1)
            title.contains("পরিমিতি") || title.contains("ক্ষেত্রফল") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.MENSURATION }.take(1)
            title.contains("জ্যামিতি") || title.contains("ত্রিভুজ") || title.contains("চতুর্ভুজ") || title.contains("বৃত্ত") || title.contains("পিথাগোরাস") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.GEOMETRY }.take(1)
            title.contains("সেট") || title.contains("সম্ভাব্যতা") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.SETS_COUNTING_PROBABILITY }.take(1)
            title.contains("পরিসংখ্যান") || title.contains("তথ্য ও উপাত্ত") ->
                mappings.filter { it.bcsMathDomain == BcsMathDomain.STATISTICS }.take(1)
            else -> mappings.filter { it.bcsMathDomain == BcsMathDomain.ARITHMETIC }.take(1)
        }
    }

    fun getMappingsForBcsUnit(unit: BcsSyllabusUnit): List<CrossModuleMappingEntry> =
        mappings.filter { it.bcsUnit == unit }
}
