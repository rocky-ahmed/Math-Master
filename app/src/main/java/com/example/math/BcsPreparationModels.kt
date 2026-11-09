package com.example.math

import com.example.data.model.TopicAnalyticsEntity

/**
 * Explicit BCS Preliminary Mathematical Reasoning Syllabus Version.
 *
 * - [BCS_50TH_CURRENT]: Official 50th BCS Preliminary Test Syllabus published by BPSC
 *   on 9 December 2025 (20 total marks, 5 units × 4 marks each). This is the DEFAULT
 *   active syllabus across the application.
 * - [BCS_LEGACY_15]: Historical/archived 35th–49th BCS Preliminary syllabus
 *   (15 total marks, 5 units × 3 marks each), retained only as a clearly labeled
 *   legacy reference option.
 */
enum class BcsSyllabusVersion(
    val id: String,
    val titleBn: String,
    val shortBadgeBn: String,
    val totalMarks: Int,
    val marksPerUnit: Int,
    val publishedDateBn: String,
    val authorityBn: String,
    val officialSourceUrl: String,
    val officialPdfUrl: String,
    val isDefault: Boolean,
    val descriptionBn: String
) {
    BCS_50TH_CURRENT(
        id = "BCS_50TH_2025",
        titleBn = "৫০তম বিসিএস প্রিলিমিনারি সিলেবাস (বর্তমান ডিফল্ট)",
        shortBadgeBn = "৫০তম বিসিএস • ২০ নম্বর",
        totalMarks = 20,
        marksPerUnit = 4,
        publishedDateBn = "৯ ডিসেম্বর ২০২৫",
        authorityBn = "বাংলাদেশ সরকারী কর্ম কমিশন (BPSC)",
        officialSourceUrl = "https://bpsc.gov.bd/pages/psc-exams/",
        officialPdfUrl = "https://objectstorage.ap-dcc-gazipur-1.oraclecloud15.com/n/axvjbnqprylg/b/V2Ministry/o/office-bpsc/2024/12/be22c1ddda60414fad98b0a82080ef9a.pdf",
        isDefault = true,
        descriptionBn = "পিএসসি কর্তৃক ৯ ডিসেম্বর ২০২৫ তারিখে প্রকাশিত ৫০তম বিসিএস প্রিলিমিনারি সিলেবাস অনুযায়ী গাণিতিক যুক্তি (Mathematical Reasoning) অংশে মোট ২০ নম্বর (৫টি ইউনিট × ৪ নম্বর) বরাদ্দ।"
    ),
    BCS_LEGACY_15(
        id = "BCS_LEGACY_35_49",
        titleBn = "প্রাক্তন বিসিএস সিলেবাস (৩৫তম–৪৯তম • আর্কাইভড)",
        shortBadgeBn = "প্রাক্তন সিলেবাস • ১৫ নম্বর (আর্কাইভড)",
        totalMarks = 15,
        marksPerUnit = 3,
        publishedDateBn = "৩৫তম–৪৯তম বিসিএস (আর্কাইভড)",
        authorityBn = "বাংলাদেশ সরকারী কর্ম কমিশন (BPSC)",
        officialSourceUrl = "https://bpsc.gov.bd/pages/psc-exams/",
        officialPdfUrl = "",
        isDefault = false,
        descriptionBn = "ঐতিহাসিক তুলনা ও পুরাতন প্রশ্নপত্র অনুশীলনের জন্য সংরক্ষিত ৩৫তম–৪৯তম বিসিএস প্রিলিমিনারি সিলেবাস (মোট ১৫ নম্বর, প্রতি ইউনিটে ৩ নম্বর)।"
    )
}

/**
 * Categorizes negative-marking options so custom practice penalties and historical exam rules
 * are never conflated with the 50th BCS syllabus document.
 */
enum class BcsNegativeMarkingCategory(val descriptionBn: String) {
    CUSTOM_PRACTICE_DEFAULT("অ্যাপের ডিফল্ট অনুশীলন সেটিং (নেগেটিভ মার্কিং বন্ধ)"),
    CUSTOM_PRACTICE_PENALTY("অ্যাপের কাস্টম অনুশীলন পেনাল্টি (পিএসসির অফিশিয়াল নিয়ম নয়)"),
    HISTORICAL_BCS_REFERENCE("পূর্ববর্তী বিসিএস প্রিলিমিনারি পরীক্ষার ঐতিহাসিক রেফারেন্স ও কাস্টম অনুশীলন অপশন (৫০তম বিসিএস সিলেবাস নথিতে পৃথকভাবে উল্লেখিত নয়)")
}

/**
 * Explicit negative-marking configuration presets for BCS Mock & Custom Examinations.
 */
enum class BcsNegativeMarkingOption(
    val penaltyPerWrong: Double,
    val labelBn: String,
    val category: BcsNegativeMarkingCategory,
    val isOfficial50thSyllabusRule: Boolean = false
) {
    NONE(
        penaltyPerWrong = 0.0,
        labelBn = "বন্ধ (০.০০ • ডিফল্ট অনুশীলন)",
        category = BcsNegativeMarkingCategory.CUSTOM_PRACTICE_DEFAULT,
        isOfficial50thSyllabusRule = false
    ),
    CUSTOM_QUARTER(
        penaltyPerWrong = 0.25,
        labelBn = "-০.২৫ (কাস্টম অনুশীলন পেনাল্টি)",
        category = BcsNegativeMarkingCategory.CUSTOM_PRACTICE_PENALTY,
        isOfficial50thSyllabusRule = false
    ),
    HISTORICAL_HALF(
        penaltyPerWrong = 0.50,
        labelBn = "-০.৫০ (ঐতিহাসিক বিসিএস রেফারেন্স / কাস্টম)",
        category = BcsNegativeMarkingCategory.HISTORICAL_BCS_REFERENCE,
        isOfficial50thSyllabusRule = false
    )
}

/**
 * Official BPSC Preliminary Syllabus Units for Mathematical Reasoning (গাণিতিক যুক্তি).
 *
 * Under the current 50th BCS Preliminary syllabus (published 9 December 2025):
 * Total Marks = 20 (5 Units × 4 Marks each).
 * Under the archived Legacy syllabus (35th–49th BCS):
 * Total Marks = 15 (5 Units × 3 Marks each).
 */
enum class BcsSyllabusUnit(
    val id: String,
    val unitNumber: Int,
    val unitNumberBn: String,
    val titleBn: String,
    val titleEn: String,
    val officialMarks: Int, // Default 50th BCS marks (4)
    val legacyMarks: Int,   // Legacy 35th–49th BCS marks (3)
    val allocatedMarksBn: String, // Default 50th BCS label ("৪ নম্বর")
    val legacyAllocatedMarksBn: String, // Legacy label ("৩ নম্বর")
    val isOfficialBpscSyllabus: Boolean = true,
    val officialTopicsBn: String,
    val topicsBn: List<String>,
    val mappedQuizTopic: QuizTopic
) {
    UNIT_1_ARITHMETIC(
        id = "UNIT_1_ARITHMETIC",
        unitNumber = 1,
        unitNumberBn = "১ম অধ্যায় (ইউনিট ১)",
        titleBn = "বাস্তব সংখ্যা, পাটিগণিত, শতকরা ও মুনাফা",
        titleEn = "Real Numbers, LCM/GCD, Percentage, Interest, Ratio & Profit-Loss",
        officialMarks = 4,
        legacyMarks = 3,
        allocatedMarksBn = "৪ নম্বর",
        legacyAllocatedMarksBn = "৩ নম্বর",
        isOfficialBpscSyllabus = true,
        officialTopicsBn = "বাস্তব সংখ্যা, ল.সা.গু, গ.সা.গু, শতকরা, সরল ও যৌগিক মুনাফা, অনুপাত ও সমানুপাত, লাভ ও ক্ষতি।",
        topicsBn = listOf(
            "বাস্তব সংখ্যা (Real Numbers)",
            "ল.সা.গু ও গ.সা.গু (LCM & GCD)",
            "শতকরা (Percentage)",
            "সরল ও যৌগিক মুনাফা (Simple & Compound Profit/Interest)",
            "অনুপাত ও সমানুপাত (Ratio & Proportion)",
            "লাভ ও ক্ষতি (Profit & Loss)"
        ),
        mappedQuizTopic = QuizTopic.ARITHMETIC_PERCENTAGE
    ),
    UNIT_2_ALGEBRA(
        id = "UNIT_2_ALGEBRA",
        unitNumber = 2,
        unitNumberBn = "২য় অধ্যায় (ইউনিট ২)",
        titleBn = "বীজগণিতীয় সূত্রাবলি, সমীকরণ ও অসমতা",
        titleEn = "Algebraic Formulas, Factorization, Equations & Inequalities",
        officialMarks = 4,
        legacyMarks = 3,
        allocatedMarksBn = "৪ নম্বর",
        legacyAllocatedMarksBn = "৩ নম্বর",
        isOfficialBpscSyllabus = true,
        officialTopicsBn = "বীজগণিতীয় সূত্রাবলি, বহুপদীর উৎপাদক বিশ্লেষণ, সরল ও দ্বিঘাত সমীকরণ, সরল ও দ্বিঘাত অসমতা, দুই বা ততোধিক চলকের সহসমীকরণ।",
        topicsBn = listOf(
            "বীজগণিতীয় সূত্রাবলি (Algebraic Formulas)",
            "বহুপদীর উৎপাদক বিশ্লেষণ (Polynomial Factorization)",
            "সরল ও দ্বিঘাত সমীকরণ (Linear & Quadratic Equations)",
            "সরল ও দ্বিঘাত অসমতা (Linear & Quadratic Inequalities)",
            "দুই বা ততোধিক চলকের সহসমীকরণ (Simultaneous Equations)"
        ),
        mappedQuizTopic = QuizTopic.ALGEBRA
    ),
    UNIT_3_EXPONENTS_SERIES(
        id = "UNIT_3_EXPONENTS_SERIES",
        unitNumber = 3,
        unitNumberBn = "৩য় অধ্যায় (ইউনিট ৩)",
        titleBn = "সূচক, লগারিদম এবং সমান্তর ও গুণোত্তর ধারা",
        titleEn = "Indices, Logarithms, Arithmetic & Geometric Sequences/Series",
        officialMarks = 4,
        legacyMarks = 3,
        allocatedMarksBn = "৪ নম্বর",
        legacyAllocatedMarksBn = "৩ নম্বর",
        isOfficialBpscSyllabus = true,
        officialTopicsBn = "সূচক ও লগারিদম, সমান্তর ও গুণোত্তর অনুক্রম এবং ধারা।",
        topicsBn = listOf(
            "সূচক (Indices / Exponents)",
            "লগারিদম (Logarithms)",
            "সমান্তর অনুক্রম ও ধারা (Arithmetic Sequences & Series)",
            "গুণোত্তর অনুক্রম ও ধারা (Geometric Sequences & Series)"
        ),
        mappedQuizTopic = QuizTopic.SQUARES_CUBES
    ),
    UNIT_4_GEOMETRY_MENSURATION(
        id = "UNIT_4_GEOMETRY_MENSURATION",
        unitNumber = 4,
        unitNumberBn = "৪র্থ অধ্যায় (ইউনিট ৪)",
        titleBn = "রেখা, কোণ, ত্রিভুজ, বৃত্ত, পিথাগোরাস ও পরিমিতি",
        titleEn = "Lines, Angles, Triangles, Quadrilaterals, Pythagoras, Circles & Mensuration",
        officialMarks = 4,
        legacyMarks = 3,
        allocatedMarksBn = "৪ নম্বর",
        legacyAllocatedMarksBn = "৩ নম্বর",
        isOfficialBpscSyllabus = true,
        officialTopicsBn = "রেখা, কোণ, ত্রিভুজ ও চতুর্ভুজ সংক্রান্ত উপপাদ্য, পিথাগোরাসের উপপাদ্য, বৃত্ত সংক্রান্ত উপপাদ্য, পরিমিতি (সরলক্ষেত্র ও ঘনবস্তু)।",
        topicsBn = listOf(
            "রেখা, কোণ ও ত্রিভুজ (Lines, Angles & Triangles)",
            "চতুর্ভুজ সংক্রান্ত উপপাদ্য (Quadrilaterals)",
            "পিথাগোরাসের উপপাদ্য (Pythagorean Theorem)",
            "বৃত্ত সংক্রান্ত উপপাদ্য (Circle Theorems)",
            "পরিমিতি: সরলক্ষেত্র ও ঘনবস্তু (Mensuration of Plane & Solid Figures)"
        ),
        mappedQuizTopic = QuizTopic.GEOMETRY_MENSURATION
    ),
    UNIT_5_SETS_COMBINATORICS_PROBABILITY(
        id = "UNIT_5_SETS_COMBINATORICS_PROBABILITY",
        unitNumber = 5,
        unitNumberBn = "৫ম অধ্যায় (ইউনিট ৫)",
        titleBn = "সেট, বিন্যাস, সমাবেশ, পরিসংখ্যান ও সম্ভাব্যতা",
        titleEn = "Sets, Permutations, Combinations, Statistics & Probability",
        officialMarks = 4,
        legacyMarks = 3,
        allocatedMarksBn = "৪ নম্বর",
        legacyAllocatedMarksBn = "৩ নম্বর",
        isOfficialBpscSyllabus = true,
        officialTopicsBn = "সেট, বিন্যাস ও সমাবেশ, পরিসংখ্যান ও সম্ভাব্যতা।",
        topicsBn = listOf(
            "সেট ও ভেনচিত্র (Sets & Venn Diagrams)",
            "বিন্যাস (Permutations)",
            "সমাবেশ (Combinations)",
            "পরিসংখ্যান (Statistics)",
            "সম্ভাব্যতা (Probability)"
        ),
        mappedQuizTopic = QuizTopic.NUMBER_SYSTEM_DIVISIBILITY
    );

    fun marksForVersion(version: BcsSyllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT): Int =
        if (version == BcsSyllabusVersion.BCS_50TH_CURRENT) officialMarks else legacyMarks

    fun allocatedMarksBn(version: BcsSyllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT): String =
        if (version == BcsSyllabusVersion.BCS_50TH_CURRENT) allocatedMarksBn else legacyAllocatedMarksBn
}

/**
 * Distinguishes verified historical BCS exam questions from syllabus-aligned practice questions.
 */
enum class BcsQuestionSourceType(val badgeBn: String) {
    OFFICIAL_PREVIOUS_BCS("বিগত বিসিএস প্রশ্ন (যাচাইকৃত)"),
    SYLLABUS_PRACTICE("সিলেবাস অনুশীলন মডেল")
}

/**
 * Provenance verification status for every BCS question.
 */
enum class BcsVerificationStatus(val badgeBn: String) {
    VERIFIED_HISTORICAL("যাচাইকৃত ঐতিহাসিক বিসিএস প্রশ্ন"),
    SYLLABUS_PRACTICE_MODEL("সিলেবাস-সম্মত অনুশীলন মডেল"),
    UNVERIFIED_EXCLUDED("অযাচাইকৃত (মূল তালিকা থেকে বাদ)")
}

/**
 * Audit record documenting the provenance verification of questions previously labeled OFFICIAL_PREVIOUS_BCS.
 * Maps each [originalQuestionId] unambiguously to its unique active [resolvedQuestionId].
 */
data class BcsProvenanceAuditEntry(
    val originalQuestionId: String,
    val resolvedQuestionId: String,
    val originalClaimedRefBn: String,
    val questionSummaryBn: String,
    val auditAction: String, // "VERIFIED_RETAINED", "PROVENANCE_CORRECTED", "RECLASSIFIED_TO_PRACTICE"
    val finalSourceType: BcsQuestionSourceType,
    val finalVerificationStatus: BcsVerificationStatus,
    val verifiedExamNumber: Int?,
    val verifiedExamYear: Int?,
    val auditNotesBn: String
)

data class BcsMathQuestion(
    val id: String,
    val legacyIdAliases: Set<String> = emptySet(),
    val unit: BcsSyllabusUnit,
    val topicBn: String,
    val sourceType: BcsQuestionSourceType,
    val examReferenceBn: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM,
    val shortcutTitleBn: String,
    val formulaUsedBn: String,
    val shortcutTechniqueBn: String,
    val whenToUseShortcutBn: String,
    val standardStepByStepBn: List<String>,
    val commonMistakeWarningBn: String,
    val bcsExamNumber: Int? = null,
    val examYear: Int? = null,
    val questionPaperRefBn: String = "গাণিতিক যুক্তি অংশ (প্রিলিমিনারি এমসিকিউ)",
    val sourceTitleBn: String = "BPSC বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ",
    val sourceUrlOrPubRef: String = "https://bpsc.gov.bd/pages/psc-exams/",
    val provenanceEvidenceBn: String = "",
    val verificationStatus: BcsVerificationStatus = if (sourceType == BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS) {
        BcsVerificationStatus.VERIFIED_HISTORICAL
    } else {
        BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
    }
) {
    val correctAnswerBn: String
        get() = optionsBn.getOrElse(correctIndex) { optionsBn.firstOrNull().orEmpty() }
}

data class BcsWeakUnitAnalysis(
    val unit: BcsSyllabusUnit,
    val officialMarks: Int,
    val allocatedMarksBn: String,
    val accuracyPercentage: Double,
    val isWeak: Boolean,
    val statusBn: String,
    val recommendationBn: String
)

object BcsPreparationRepository {

    val defaultSyllabusVersion: BcsSyllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT

    /**
     * Default official Mathematical Reasoning marks under the current 50th BCS Preliminary syllabus (20 marks).
     */
    const val totalOfficialMarks: Int = 20

    val negativeMarkingOptions: List<BcsNegativeMarkingOption> = BcsNegativeMarkingOption.entries

    fun getTotalOfficialMarks(version: BcsSyllabusVersion = defaultSyllabusVersion): Int =
        version.totalMarks

    fun getUnitMarks(
        unit: BcsSyllabusUnit,
        version: BcsSyllabusVersion = defaultSyllabusVersion
    ): Int = unit.marksForVersion(version)

    /**
     * Complete provenance audit log of all 20 audited records (the 14 original items from Pass 1
     * plus the 6 items reclassified in Pass 2 where multi-source verbatim BCS paper confirmation
     * was not met). Every entry maps to a unique [originalQuestionId] and a unique [resolvedQuestionId].
     */
    val provenanceAuditLog: List<BcsProvenanceAuditEntry> = listOf(
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_37_geom_1",
            resolvedQuestionId = "bcs_hist_37_geom_sq_diag",
            originalClaimedRefBn = "৩৭তম বিসিএস প্রিলিমিনারি (২০১৬)",
            questionSummaryBn = "একটি বর্গক্ষেত্রের কর্ণের দৈর্ঘ্য 4√2 একক হলে ওই বর্গক্ষেত্রের ক্ষেত্রফল কত বর্গ একক?",
            auditAction = "VERIFIED_RETAINED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 37,
            verifiedExamYear = 2016,
            auditNotesBn = "৩৭তম বিসিএস প্রিলিমিনারি (৩০ সেপ্টেম্বর ২০১৬) প্রশ্নপত্রের হুবহু যাচাইকৃত প্রশ্ন।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_45_arith_1",
            resolvedQuestionId = "bcs_hist_26_arith_consec_sq",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "দুটি ক্রমিক পূর্ণসংখ্যা নির্ণয় করুন, যাদের বর্গের অন্তর ৪৭ — বড় সংখ্যাটি কত?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 26,
            verifiedExamYear = 2005,
            auditNotesBn = "প্রশ্নটি ৪৫তম বিসিএসের নয়; এটি ২৬তম (বিশেষ) বিসিএস প্রিলিমিনারি (২০০৫) পরীক্ষার যাচাইকৃত প্রশ্ন। ক্যানোনিকাল আইডি bcs_hist_26_arith_consec_sq নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_40_arith_1",
            resolvedQuestionId = "bcs_hist_12_arith_sugar_inc",
            originalClaimedRefBn = "৪০তম বিসিএস প্রিলিমিনারি (২০১৯)",
            questionSummaryBn = "চিনির মূল্য ২৫% বৃদ্ধি পাওয়ায় চিনির ব্যবহার শতকরা কত কমালে ব্যয় বৃদ্ধি পাবে না?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 12,
            verifiedExamYear = 1991,
            auditNotesBn = "এই হুবহু প্রশ্নটি ১০ম বিসিএস (১৯৮৯) ও ১২তম বিসিএস প্রিলিমিনারি (১৯৯১) পরীক্ষায় এসেছে। ক্যানোনিকাল আইডি bcs_hist_12_arith_sugar_inc নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_44_alg_1",
            resolvedQuestionId = "bcs_hist_12_alg_factor_2x2",
            originalClaimedRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২০২২)",
            questionSummaryBn = "2x² - x - 15 এর উৎপাদক বিশ্লেষণ কোনটি?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 12,
            verifiedExamYear = 1991,
            auditNotesBn = "প্রশ্নটি ১২তম বিসিএস প্রিলিমিনারি (১৯৯১) পরীক্ষার হুবহু যাচাইকৃত প্রশ্ন; ক্যানোনিকাল আইডি bcs_hist_12_alg_factor_2x2 নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_38_exp_1",
            resolvedQuestionId = "bcs_hist_25_series_sum_99",
            originalClaimedRefBn = "৩৮তম বিসিএস প্রিলিমিনারি (২০১৭)",
            questionSummaryBn = "১ + ২ + ৩ + ... + ৯৯ = কত?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 25,
            verifiedExamYear = 2004,
            auditNotesBn = "প্রশ্নটি ২৫তম বিসিএস প্রিলিমিনারি (২০০৪) পরীক্ষার হুবহু যাচাইকৃত প্রশ্ন; ক্যানোনিকাল আইডি bcs_hist_25_series_sum_99 নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_36_set_1",
            resolvedQuestionId = "bcs_hist_12_prob_3coins",
            originalClaimedRefBn = "৩৬তম বিসিএস প্রিলিমিনারি (২০১৬)",
            questionSummaryBn = "তিনটি মুদ্রা একসাথে একবার নিক্ষেপ করা হলে সবগুলো একই ফলাফল আসার সম্ভাবনা কত?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 12,
            verifiedExamYear = 1991,
            auditNotesBn = "প্রশ্নটি ১২তম বিসিএস প্রিলিমিনারি (১৯৯১) পরীক্ষার যাচাইকৃত প্রশ্ন; ক্যানোনিকাল আইডি bcs_hist_12_prob_3coins নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_35_set_1",
            resolvedQuestionId = "bcs_hist_40_comb_6players",
            originalClaimedRefBn = "৩৫তম বিসিএস প্রিলিমিনারি (২০১৫)",
            questionSummaryBn = "৬ জন খেলোয়াড়কে সমান সংখ্যক দুইটি দলে কতভাবে বিভক্ত করা যায়?",
            auditAction = "PROVENANCE_CORRECTED",
            finalSourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            finalVerificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL,
            verifiedExamNumber = 40,
            verifiedExamYear = 2019,
            auditNotesBn = "প্রশ্নটি ৪০তম বিসিএস প্রিলিমিনারি (৩ মে ২০১৯) পরীক্ষার হুবহু যাচাইকৃত প্রশ্ন; ক্যানোনিকাল আইডি bcs_hist_40_comb_6players নির্ধারণ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_44_arith_1",
            resolvedQuestionId = "bcs_prac_u1_simple_interest_double",
            originalClaimedRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২০২২)",
            questionSummaryBn = "কোনো আসল ৩ বছরে সরল মুনাফায় তার দ্বিগুণ হলে বার্ষিক মুনাফার হার কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "৪৪তম বিসিএস প্রিলিমিনারি প্রশ্নপত্রে এই হুবহু প্রশ্নটি পাওয়া যায়নি; তাই এটিকে SYLLABUS_PRACTICE (bcs_prac_u1_simple_interest_double) হিসেবে পুনঃশ্রেণিবদ্ধ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_43_arith_1",
            resolvedQuestionId = "bcs_prac_u1_loss_20pct",
            originalClaimedRefBn = "৪৩তম বিসিএস প্রিলিমিনারি (২০২১)",
            questionSummaryBn = "একটি দ্রব্য ৪০০ টাকায় ক্রয় করে ২০% ক্ষতিতে বিক্রয় করা হলে বিক্রয়মূল্য কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "৪৩তম বিসিএস প্রিলিমিনারির হুবহু প্রশ্ন না হওয়ায় এটিকে SYLLABUS_PRACTICE (bcs_prac_u1_loss_20pct) হিসেবে পুনঃশ্রেণিবদ্ধ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_45_alg_1",
            resolvedQuestionId = "bcs_prac_u2_cube_reciprocal",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "যদি x + 1/x = 3 হয়, তবে x³ + 1/x³ এর মান কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "এটি সাধারণ বীজগণিতীয় অনুসিদ্ধান্তের অনুশীলন প্রশ্ন; তাই এটিকে SYLLABUS_PRACTICE (bcs_prac_u2_cube_reciprocal)-এ স্থানান্তর করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_41_alg_1",
            resolvedQuestionId = "bcs_prac_u2_sq_reciprocal",
            originalClaimedRefBn = "৪১তম বিসিএস প্রিলিমিনারি (২০২১)",
            questionSummaryBn = "যদি x - 1/x = 4 হয়, তবে x² + 1/x² এর মান কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "৪১তম বিসিএসের হুবহু প্রশ্ন ছিল 'a+b=7, ab=12 হলে 1/a²+1/b² কত?'; তাই এটিকে SYLLABUS_PRACTICE (bcs_prac_u2_sq_reciprocal) হিসেবে পুনঃশ্রেণিবদ্ধ করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_44_exp_1",
            resolvedQuestionId = "bcs_prac_u3_log_base_x",
            originalClaimedRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২০২২)",
            questionSummaryBn = "log_x (1/8) = -3/2 হলে x এর মান কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "লগারিদমের এই রূপটি অনুশীলন মডেল হিসেবে SYLLABUS_PRACTICE (bcs_prac_u3_log_base_x)-এ স্থানান্তর করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_45_geom_1",
            resolvedQuestionId = "bcs_prac_u4_equilateral_area",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "একটি সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য ২ সেমি হলে এর ক্ষেত্রফল কত বর্গ সেমি?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "৪৪তম বিসিএসে সমবাহু ত্রিভুজের উচ্চতা নির্ণয় এসেছিল; তাই ক্ষেত্রফল সংস্করণটিকে SYLLABUS_PRACTICE (bcs_prac_u4_equilateral_area)-এ রাখা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_prev_45_set_1",
            resolvedQuestionId = "bcs_prac_u5_venn_union",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "যদি n(A) = 20, n(B) = 30 এবং n(A ∩ B) = 10 হয়, তবে n(A ∪ B) এর মান কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "এটি ভেনচিত্রের অনুশীলন মডেল হিসেবে SYLLABUS_PRACTICE (bcs_prac_u5_venn_union)-এ স্থানান্তর করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_44_arith_loss",
            resolvedQuestionId = "bcs_prac_u1_cost_from_10pct_loss",
            originalClaimedRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২০২২)",
            questionSummaryBn = "একটি দ্রব্য ১৮০ টাকায় বিক্রয় করায় ১০% ক্ষতি হলে, দ্রব্যটির ক্রয়মূল্য কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "চাকরি পরীক্ষার প্রচলিত প্রশ্ন হলেও ৪৪তম বিসিএস মূল প্রশ্নপত্রে স্বতন্ত্রভাবে নিশ্চিত না হওয়ায় কঠোর প্রোভেন্যান্স নীতিতে SYLLABUS_PRACTICE-এ স্থানান্তরিত।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_45_alg_factor",
            resolvedQuestionId = "bcs_prac_u2_factor_x2_4x_5",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "x² - 4x - 5 এর উৎপাদক নিচের কোনটি?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "সাধারণ বীজগণিতীয় উৎপাদক প্রশ্ন; ৪৫তম বিসিএস মূল প্রশ্নপত্রে স্বতন্ত্রভাবে নিশ্চিত না হওয়ায় SYLLABUS_PRACTICE-এ স্থানান্তরিত।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_45_alg_lcm_gcd",
            resolvedQuestionId = "bcs_prac_u2_lcm_gcd_prod",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "x²y + xy² এবং x² + xy রাশি দুইটির ল.সা.গু এবং গ.সা.গু-এর গুণফল কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "বীজগণিতীয় ল.সা.গু ও গ.সা.গু অনুশীলন প্রশ্ন; ৪৫তম বিসিএস মূল প্রশ্নপত্রে স্বতন্ত্রভাবে নিশ্চিত না হওয়ায় SYLLABUS_PRACTICE-এ স্থানান্তরিত।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_43_exp_log",
            resolvedQuestionId = "bcs_prac_u3_log_sum_45",
            originalClaimedRefBn = "৪৩তম বিসিএস প্রিলিমিনারি (২০২১)",
            questionSummaryBn = "2 log₂(3) + log₂(5) এর মান নিচের কোনটি?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "৪৩তম বিসিএসের মূল প্রশ্ন ছিল '2^(log₂ 3 + log₂ 5) = 15' (যা bcs_hist_43_exp_power_log হিসেবে সংরক্ষিত); এই রূপটিকে SYLLABUS_PRACTICE-এ স্থানান্তর করা হয়েছে।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_45_geom_angle",
            resolvedQuestionId = "bcs_prac_u4_tan_sq_angle",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "যদি সূক্ষ্মকোণ θ-এর জন্য 1 + tan²θ = 4 হয়, তবে θ এর মান কত?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "ত্রিকোণমিতিক অভেদ অনুশীলন প্রশ্ন; ৪৫তম বিসিএস মূল প্রশ্নপত্রে স্বতন্ত্রভাবে নিশ্চিত না হওয়ায় SYLLABUS_PRACTICE-এ স্থানান্তরিত।"
        ),
        BcsProvenanceAuditEntry(
            originalQuestionId = "bcs_ver_45_set_builder",
            resolvedQuestionId = "bcs_prac_u5_set_builder_nat",
            originalClaimedRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (২০২৩)",
            questionSummaryBn = "A = {x ∈ ℕ : x² - 5x - 14 = 0} হলে, A নিচের কোনটি?",
            auditAction = "RECLASSIFIED_TO_PRACTICE",
            finalSourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            finalVerificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL,
            verifiedExamNumber = null,
            verifiedExamYear = null,
            auditNotesBn = "সেট গঠন পদ্ধতির অনুশীলন প্রশ্ন; ৪৫তম বিসিএস মূল প্রশ্নপত্রে স্বতন্ত্রভাবে নিশ্চিত না হওয়ায় SYLLABUS_PRACTICE-এ স্থানান্তরিত।"
        )
    )

    /**
     * Verified historical BCS Preliminary Mathematical Reasoning questions (22 total) with
     * multi-source traceable exam provenance. Every item has a unique canonical `bcs_hist_<examNo>_*` ID
     * matching its [BcsMathQuestion.bcsExamNumber].
     */
    val verifiedPreviousBcsQuestions: List<BcsMathQuestion> = listOf(
        // ==================== UNIT 1: ARITHMETIC & REAL NUMBERS (6 Verified Questions) ====================
        BcsMathQuestion(
            id = "bcs_hist_45_arith_ratio",
            legacyIdAliases = setOf("bcs_ver_45_arith_ratio"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "অনুপাত ও সমানুপাত",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩)",
            questionBn = "যদি x : y = ২ : ৩ এবং y : z = ৫ : ৭ হয়, তবে x : y : z = কত?",
            optionsBn = listOf("৬ : ১৫ : ২১", "১০ : ১৫ : ২১", "২ : ৫ : ৭", "১০ : ১২ : ২১"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "ধারাবাহিক অনুপাতের দ-গুণন (N-Rule) কৌশল",
            formulaUsedBn = "x : y : z = (x₁·y₂) : (y₁·y₂) : (y₁·z₂)",
            shortcutTechniqueBn = "দ-গুণন পদ্ধতি: (২×৫) : (৩×৫) : (৩×৭) = ১০ : ১৫ : ২১।",
            whenToUseShortcutBn = "যখন দুটি ক্রমিক অনুপাত x : y এবং y : z দেওয়া থাকে এবং ধারাবাহিক অনুপাত x : y : z বের করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত অনুপাত দুটি হলো x : y = ২ : ৩ এবং y : z = ৫ : ৭।",
                "উভয় অনুপাতে সাধারণ চলক y-এর মান সমান করার জন্য প্রথম অনুপাতকে ৫ দ্বারা এবং দ্বিতীয় অনুপাতকে ৩ দ্বারা গুণ করি।",
                "x : y = (২×৫) : (৩×৫) = ১০ : ১৫ এবং y : z = (৫×৩) : (৭×৩) = ১৫ : ২১।",
                "অতএব, x : y : z = ১০ : ১৫ : ২১।"
            ),
            commonMistakeWarningBn = "সরাসরি ২ : ১৫ : ৭ বা y-এর পদ সমান না করে অনুপাত লেখা ভুল।",
            bcsExamNumber = 45,
            examYear = 2023,
            questionPaperRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২৩)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 45th Preliminary Question Archive (19 May 2023)",
            provenanceEvidenceBn = "১৯ মে ২০২৩ তারিখে অনুষ্ঠিত ৪৫তম বিসিএস প্রিলিমিনারি পরীক্ষার গাণিতিক যুক্তি অংশে ধারাবাহিক অনুপাত নির্ণয়ের এই প্রশ্নটি এসেছে (উত্তর: ১০ : ১৫ : ২১)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_45_arith_salary_pct",
            legacyIdAliases = setOf("bcs_ver_45_arith_percent"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "শতকরা ও লাভ-ক্ষতি",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩)",
            questionBn = "জাহিদ সাহেবের বেতন ১০% কমানোর পর হ্রাসকৃত বেতন ১০% বাড়ানো হলে তার শতকরা কতটুকু ক্ষতি হয়?",
            optionsBn = listOf("০% (অপরিবর্তিত)", "১% ক্ষতি", "২% ক্ষতি", "১০% ক্ষতি"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "সমহারে হ্রাস-বৃদ্ধির বর্গীয় ক্ষতি সূত্র",
            formulaUsedBn = "শতকরা ক্ষতি = (x² / ১০০)%",
            shortcutTechniqueBn = "একই হারে (x%) একবার হ্রাস ও একবার বৃদ্ধি পেলে সর্বদা (x² / ১০০)% ক্ষতি হয়। এখানে (১০² / ১০০)% = ১% ক্ষতি।",
            whenToUseShortcutBn = "যখন কোনো মান প্রথমে x% বাড়িয়ে পরে x% কমানো হয় অথবা প্রথমে x% কমিয়ে পরে x% বাড়ানো হয়।",
            standardStepByStepBn = listOf(
                "ধরি, প্রাথমিক বেতন = ১০০ টাকা।",
                "১০% কমানোর পর হ্রাসকৃত বেতন হয় = ১০০ - ১০ = ৯০ টাকা।",
                "এরপর ৯০ টাকার ওপর ১০% বাড়ানো হলে বৃদ্ধি পায় = ৯০ × (১০/১০০) = ৯ টাকা।",
                "নতুন বেতন = ৯০ + ৯ = ৯৯ টাকা। সুতরাং ক্ষতি = ১০০ - ৯৯ = ১ টাকা বা ১% ক্ষতি।"
            ),
            commonMistakeWarningBn = "১০% কমে ১০% বাড়লে বেতন অপরিবর্তিত (০%) থাকে মনে করা সবচেয়ে সাধারণ ভুল।",
            bcsExamNumber = 45,
            examYear = 2023,
            questionPaperRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২৩)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 45th Preliminary Question Archive (19 May 2023)",
            provenanceEvidenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) প্রশ্নপত্রে 'জাহিদ সাহেবের বেতন ১০% কমানোর পর হ্রাসকৃত বেতন ১০% বাড়ানো হলে তার কতটুকু ক্ষতি হলো?' হুবহু এসেছে (উত্তর: ১% ক্ষতি)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_41_arith_compound",
            legacyIdAliases = setOf("bcs_ver_41_arith_compound"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "সরল ও যৌগিক মুনাফা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১)",
            questionBn = "বার্ষিক ১০% মুনাফায় ৮০০ টাকার ২ বছরের চক্রবৃদ্ধি মূলধন কত?",
            optionsBn = listOf("৯৪০ টাকা", "৯৬০ টাকা", "৯৬৮ টাকা", "৯৮০ টাকা"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "২ বছরের কার্যকর চক্রবৃদ্ধি হারের কৌশল",
            formulaUsedBn = "C = P(1 + r/100)² বা কার্যকর বৃদ্ধি = (2r + r²/100)%",
            shortcutTechniqueBn = "১০% হারে ২ বছরের কার্যকর চক্রবৃদ্ধি বৃদ্ধি = ২১%। তাই চক্রবৃদ্ধি মূলধন = ৮০০ × ১.২১ = ৯৬৮ টাকা।",
            whenToUseShortcutBn = "যখন ২ বছরের চক্রবৃদ্ধি মূলধন বা চক্রবৃদ্ধি মুনাফা দ্রুত নির্ণয় করতে হয়।",
            standardStepByStepBn = listOf(
                "চক্রবৃদ্ধি মূলধনের সূত্র C = P(1 + r/100)ⁿ, যেখানে P = ৮০০ টাকা, r = ১০%, n = ২ বছর।",
                "C = ৮০০ × (১ + ১০/১০০)² = ৮০০ × (১১/১০)²।",
                "C = ৮০০ × (১২১/১০০) = ৮ × ১২১ = ৯৬৮ টাকা।"
            ),
            commonMistakeWarningBn = "সরল মুনাফার সূত্র প্রয়োগ করে ৮০০ + ১৬০ = ৯৬০ টাকা উত্তর করা ভুল।",
            bcsExamNumber = 41,
            examYear = 2021,
            questionPaperRefBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) — গাণিতিক যুক্তি অংশ (প্রশ্ন ১৩/১৫)",
            sourceTitleBn = "BPSC ৪১তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://w3classroom.com / BPSC 41st Preliminary Question Solution (19 March 2021)",
            provenanceEvidenceBn = "১৯ মার্চ ২০২১ তারিখে অনুষ্ঠিত ৪১তম বিসিএস প্রিলিমিনারি পরীক্ষার গাণিতিক যুক্তি অংশে 'বার্ষিক ১০% মুনাফায় ৮০০ টাকার ২ বছরের চক্রবৃদ্ধি মূলধন কত?' হুবহু এসেছে (উত্তর: ৯৬৮ টাকা)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_41_arith_sugar_drop",
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "শতকরা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১)",
            questionBn = "চিনির মূল্য ১০% কমে যাওয়ায় চিনির ব্যবহার শতকরা কত ভাগ বাড়ালে চিনি বাবদ খরচ একই থাকবে?",
            optionsBn = listOf("৮ ১/৩%", "১০%", "১১ ১/৯%", "১২ ১/২%"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "মূল্য হ্রাসে ব্যবহার বৃদ্ধির শতকরা সূত্র",
            formulaUsedBn = "ব্যবহার বৃদ্ধি% = [r / (১০০ - r)] × ১০০%",
            shortcutTechniqueBn = "ব্যবহার বৃদ্ধি% = [১০ / (১০০ - ১০)] × ১০০% = ১০০/৯% = ১১ ১/৯%।",
            whenToUseShortcutBn = "যখন পণ্যের মূল্য r% কমে যাওয়ায় মোট খরচ অপরিবর্তিত রাখতে ব্যবহারের শতকরা বৃদ্ধি চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "পূর্বমূল্য ১০০ টাকা হলে ১০% হ্রাসে বর্তমান মূল্য = ৯০ টাকা।",
                "খরচ অপরিবর্তিত রাখতে ৯০ টাকায় চিনির ব্যবহার বাড়াতে হবে = ১০ টাকার সমপরিমাণ।",
                "শতকরা ব্যবহার বৃদ্ধি = (১০ / ৯০) × ১০০% = ১০০/৯% = ১১ ১/৯%।"
            ),
            commonMistakeWarningBn = "১০% হ্রাস দেখে সরাসরি ১০% বৃদ্ধি বা ১০/১১০ হিসাব করা ভুল।",
            bcsExamNumber = 41,
            examYear = 2021,
            questionPaperRefBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) — গাণিতিক যুক্তি অংশ (প্রশ্ন ৯/১৫)",
            sourceTitleBn = "BPSC ৪১তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 41st Preliminary Question Solution (19 March 2021)",
            provenanceEvidenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) প্রশ্নপত্রে 'চিনির মূল্য ১০% কমে যাওয়ায় চিনির ব্যবহার শতকরা কত ভাগ বাড়ালে চিনি বাবদ খরচ একই থাকবে?' হুবহু এসেছে (উত্তর: ১১ ১/৯%)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_26_arith_consec_sq",
            legacyIdAliases = setOf("bcs_prev_45_arith_1"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "বাস্তব সংখ্যা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "২৬তম বিসিএস প্রিলিমিনারি (২০০৫)",
            questionBn = "দুটি ক্রমিক পূর্ণসংখ্যা নির্ণয় করুন, যাদের বর্গের অন্তর ৪৭ — সংখ্যা দুটির বড় সংখ্যাটি কত?",
            optionsBn = listOf("২২", "২৩", "২৪", "২৫"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "ক্রমিক সংখ্যার বর্গের অন্তরের শর্টকাট",
            formulaUsedBn = "বড় সংখ্যা = (বর্গের অন্তর + ১) ÷ ২",
            shortcutTechniqueBn = "দুটি ক্রমিক সংখ্যার বর্গের অন্তর D হলে বড় সংখ্যা = (৪৭ + ১) / ২ = ২৪ এবং ছোট সংখ্যা = (৪৭ - ১) / ২ = ২৩।",
            whenToUseShortcutBn = "যখন দুটি ক্রমিক পূর্ণসংখ্যার বর্গের অন্তর দেওয়া থাকে।",
            standardStepByStepBn = listOf(
                "ধরি, ছোট সংখ্যাটি x এবং বড় ক্রমিক সংখ্যাটি (x + 1)।",
                "প্রশ্নমতে, (x + 1)² - x² = ৪৭ => x² + 2x + 1 - x² = ৪৭।",
                "2x + 1 = ৪৭ => 2x = ৪৬ => x = ২৩।",
                "অতএব ক্রমিক সংখ্যা দুটি ২৩ ও ২৪ এবং বড় সংখ্যাটি ২৪।"
            ),
            commonMistakeWarningBn = "(D - 1) / 2 করলে ছোট সংখ্যাটি (২৩) পাওয়া যায়; প্রশ্নে বড় সংখ্যা চাওয়া হলে +১ যোগ করতে হবে।",
            bcsExamNumber = 26,
            examYear = 2005,
            questionPaperRefBn = "২৬তম (বিশেষ) বিসিএস প্রিলিমিনারি (২০০৫) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ২৬তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ (২০০৫)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 26th Special BCS Preliminary Archive (2005)",
            provenanceEvidenceBn = "২৬তম বিসিএস প্রিলিমিনারি (২০০৫) পরীক্ষায় 'দুটি ক্রমিক পূর্ণসংখ্যা নির্ণয় করুন, যাদের বর্গের অন্তর ৪৭' প্রশ্নটি এসেছে (সংখ্যা দুটি ২৩ এবং ২৪)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_12_arith_sugar_inc",
            legacyIdAliases = setOf("bcs_prev_40_arith_1"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "শতকরা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "১০ম ও ১২তম বিসিএস প্রিলিমিনারি (১৯৮৯, ১৯৯১)",
            questionBn = "চিনির মূল্য ২৫% বৃদ্ধি পাওয়ায় কোনো পরিবারের চিনির ব্যবহার শতকরা কত কমালে চিনি বাবদ ব্যয় বৃদ্ধি পাবে না?",
            optionsBn = listOf("১৫%", "২০%", "২৫%", "৩০%"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "ব্যয় অপরিবর্তিত রাখার শতকরা হ্রাস সূত্র",
            formulaUsedBn = "ব্যবহার হ্রাস% = [r / (১০০ + r)] × ১০০%",
            shortcutTechniqueBn = "ব্যবহার হ্রাস% = [২৫ / (১০০ + ২৫)] × ১০০% = (২৫ / ১২৫) × ১০০% = ২০%।",
            whenToUseShortcutBn = "যখন পণ্যের মূল্য r% বৃদ্ধির ফলে মোট ব্যয় অপরিবর্তিত রাখতে ব্যবহারের শতকরা হ্রাস বের করতে হয়।",
            standardStepByStepBn = listOf(
                "পূর্বমূল্য ১০০ টাকা হলে ২৫% বৃদ্ধিতে বর্তমান মূল্য = ১২৫ টাকা।",
                "ব্যয় অপরিবর্তিত রাখতে ১২৫ টাকায় ব্যবহার কমাতে হবে = ২৫ টাকার চিনি।",
                "শতকরা ব্যবহার হ্রাস = (২৫ / ১২৫) × ১০০% = ২০%।"
            ),
            commonMistakeWarningBn = "২৫% বৃদ্ধি দেখে সরাসরি ২৫% কমানো উত্তর করা ভুল।",
            bcsExamNumber = 12,
            examYear = 1991,
            questionPaperRefBn = "১০ম বিসিএস (১৯৮৯) ও ১২তম বিসিএস প্রিলিমিনারি (১৯৯১) — গণিত অংশ",
            sourceTitleBn = "BPSC ১০ম ও ১২তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 10th & 12th Preliminary Question Archive",
            provenanceEvidenceBn = "১০ম বিসিএস (১৯৮৯) এবং ১২তম বিসিএস প্রিলিমিনারি (১৯৯১) উভয় পরীক্ষায় চিনির মূল্য ২৫% বৃদ্ধিতে ব্যবহার ২০% কমানোর এই প্রশ্নটি হুবহু এসেছে।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),

        // ==================== UNIT 2: ALGEBRA (4 Verified Questions) ====================
        BcsMathQuestion(
            id = "bcs_hist_45_alg_identity",
            legacyIdAliases = setOf("bcs_ver_45_alg_identity"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় সূত্রাবলি ও সমীকরণ",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩)",
            questionBn = "(x + 5)² = x² + bx + c সমীকরণে b ও c এর মান কত হলে সমীকরণটি অভেদ (Identity) হবে?",
            optionsBn = listOf("b = 5, c = 25", "b = 10, c = 25", "b = 10, c = 10", "b = 25, c = 10"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "বর্গ অভেদের সহগ তুলনা কৌশল",
            formulaUsedBn = "(x + a)² = x² + 2ax + a²",
            shortcutTechniqueBn = "(x + a)² = x² + 2ax + a² অভেদে b = 2a = 2×5 = 10 এবং c = a² = 5² = 25।",
            whenToUseShortcutBn = "যখন দুটি বহুপদী অভেদ হওয়ার শর্তে অজানা সহগ নির্ণয় করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "বামপক্ষকে (a + b)² = a² + 2ab + b² সূত্রে বিস্তৃত করে পাই: (x + 5)² = x² + 2·x·5 + 5² = x² + 10x + 25।",
                "ডানপক্ষ x² + bx + c-এর সাথে সহগ তুলনা করে পাই: b = 10 এবং c = 25।"
            ),
            commonMistakeWarningBn = "2ab পদের ২ গুণ করতে ভুলে b = 5 লেখা সাধারণ ভুল।",
            bcsExamNumber = 45,
            examYear = 2023,
            questionPaperRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২৩)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 45th Preliminary Question Archive (19 May 2023)",
            provenanceEvidenceBn = "১৯ মে ২০২৩ তারিখে অনুষ্ঠিত ৪৫তম বিসিএস প্রিলিমিনারি পরীক্ষায় '(x + 5)² = x² + bx + c সমীকরণে b ও c এর মান কত হলে সমীকরণটি অভেদ হবে?' হুবহু এসেছে (উত্তর: b = 10, c = 25)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_41_alg_recip_sq",
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় সূত্রাবলি",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১)",
            questionBn = "a + b = 7 এবং ab = 12 হলে, 1/a² + 1/b² এর মান কত?",
            optionsBn = listOf("3/25", "25/144", "31/144", "49/144"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "বিপরীত বর্গের সমষ্টির ল.সা.গু ও অনুসিদ্ধান্ত কৌশল",
            formulaUsedBn = "1/a² + 1/b² = [(a + b)² - 2ab] / (ab)²",
            shortcutTechniqueBn = "a+b = 7 ও ab = 12 থেকে সংখ্যাদ্বয় ৩ ও ৪। তাই 1/3² + 1/4² = 1/9 + 1/16 = 25/144।",
            whenToUseShortcutBn = "যখন a + b এবং ab দেওয়া থাকে এবং 1/a² + 1/b² এর মান চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত রাশি: 1/a² + 1/b² = (a² + b²) / (ab)²।",
                "লবে অনুসিদ্ধান্ত বসিয়ে পাই: [(a + b)² - 2ab] / (ab)²।",
                "মান বসিয়ে পাই: (7² - 2×12) / 12² = (49 - 24) / 144 = 25/144।"
            ),
            commonMistakeWarningBn = "(a + b)² = 49 কে সরাসরি লবে রেখে 49/144 উত্তর করা ভুল।",
            bcsExamNumber = 41,
            examYear = 2021,
            questionPaperRefBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) — গাণিতিক যুক্তি অংশ (প্রশ্ন ১২/১৫)",
            sourceTitleBn = "BPSC ৪১তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://myexaminer.net / BPSC 41st Preliminary Question Solution (19 March 2021)",
            provenanceEvidenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) পরীক্ষায় 'a + b = 7 এবং ab = 12 হলে, 1/a² + 1/b² এর মান কত?' হুবহু এসেছে (উত্তর: 25/144)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_12_alg_factor_2x2",
            legacyIdAliases = setOf("bcs_prev_44_alg_1"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বহুপদীর উৎপাদক বিশ্লেষণ",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১)",
            questionBn = "2x² - x - 15 এর উৎপাদক বিশ্লেষণ কোনটি?",
            optionsBn = listOf("(2x + 5)(x - 3)", "(2x - 5)(x + 3)", "(2x - 3)(x + 5)", "(2x + 3)(x - 5)"),
            correctIndex = 0,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "মূল বসিয়ে উৎপাদক যাচাই",
            formulaUsedBn = "ax² + bx + c = (px + q)(rx + s)",
            shortcutTechniqueBn = "x = 3 বসালে 2(9) - 3 - 15 = 0 হয়; তাই (x - 3) একটি উৎপাদক এবং ধ্রুবক পদ -15 পেতে অপর উৎপাদকটি (2x + 5)।",
            whenToUseShortcutBn = "এমসিকিউ পরীক্ষায় দ্বিঘাত রাশির উৎপাদক দ্রুত যাচাই করার সময়।",
            standardStepByStepBn = listOf(
                "প্রান্তীয় পদদ্বয়ের গুণফল = 2 × (-15) = -30।",
                "মধ্যপদ -x কে ভাঙলে পাই: -6x + 5x (কারণ -6 × 5 = -30 এবং -6 + 5 = -1)।",
                "2x² - 6x + 5x - 15 = 2x(x - 3) + 5(x - 3) = (2x + 5)(x - 3)।"
            ),
            commonMistakeWarningBn = "চিহ্ন ভুল করে (2x - 5)(x + 3) নিলে মধ্যপদ +x হয়ে যায়।",
            bcsExamNumber = 12,
            examYear = 1991,
            questionPaperRefBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১) — গণিত অংশ",
            sourceTitleBn = "BPSC ১২তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ (১৯৯১)",
            sourceUrlOrPubRef = "https://bcsmate.com / BPSC 12th Preliminary Question Solution (1991)",
            provenanceEvidenceBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১) পরীক্ষায় '2x² - x - 15 এর উৎপাদক কোনটি?' হুবহু এসেছে (উত্তর: (x - 3)(2x + 5))।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_10_alg_cube_sum",
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় সূত্রাবলি",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "১০ম বিসিএস প্রিলিমিনারি (১৯৮৯) ও ৪৪তম বিসিএস (২০২২)",
            questionBn = "যদি a + b + c = 0 হয়, তবে a³ + b³ + c³ এর মান কত?",
            optionsBn = listOf("0", "abc", "3abc", "a²b²c²"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "শূন্য সমষ্টির ঘন অভেদ কৌশল",
            formulaUsedBn = "a³ + b³ + c³ - 3abc = (a + b + c)(a² + b² + c² - ab - bc - ca)",
            shortcutTechniqueBn = "a + b + c = 0 হলে সরাসরি a³ + b³ + c³ = 3abc।",
            whenToUseShortcutBn = "যখন তিনটি রাশির যোগফল শূন্য (a + b + c = 0) হয় এবং তাদের ঘনের সমষ্টি চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "দেওয়া আছে, a + b + c = 0 => a + b = -c।",
                "উভয় পক্ষকে ঘন করে পাই: (a + b)³ = (-c)³ => a³ + b³ + 3ab(a + b) = -c³।",
                "a + b = -c বসিয়ে পাই: a³ + b³ + 3ab(-c) = -c³ => a³ + b³ + c³ = 3abc।"
            ),
            commonMistakeWarningBn = "a + b + c = 0 দেখে ঘনের যোগফলও 0 মনে করা সাধারণ ভুল।",
            bcsExamNumber = 10,
            examYear = 1989,
            questionPaperRefBn = "১০ম বিসিএস প্রিলিমিনারি (১৯৮৯) ও ৪৪তম বিসিএস প্রিলিমিনারি (২০২২) — গণিত অংশ",
            sourceTitleBn = "BPSC ১০ম ও ৪৪তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 10th & 44th Preliminary Question Archive",
            provenanceEvidenceBn = "১০ম বিসিএস প্রিলিমিনারি (১৯৮৯) এবং পরবর্তীতে ৪৩তম/৪৪তম বিসিএস প্রিলিমিনারিতে 'a + b + c = 0 হলে a³ + b³ + c³ এর মান কত?' প্রশ্নটি এসেছে (উত্তর: 3abc)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),

        // ==================== UNIT 3: INDICES, LOGARITHMS & SERIES (4 Verified Questions) ====================
        BcsMathQuestion(
            id = "bcs_hist_45_exp_eq",
            legacyIdAliases = setOf("bcs_ver_45_exp_eq"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "সূচক (Indices)",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩)",
            questionBn = "যদি 2^(x + 7) = 4^(x + 2) হয়, তবে x এর মান কত?",
            optionsBn = listOf("2", "3", "4", "5"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "সমভিত্তিক ঘাত সমীকরণ কৌশল",
            formulaUsedBn = "a^m = (a^k)^n => m = k·n",
            shortcutTechniqueBn = "ভিত্তি ২-এ রূপান্তর করে ঘাত সমীকরণ: x + 7 = 2(x + 2) => x + 7 = 2x + 4 => x = 3।",
            whenToUseShortcutBn = "যখন সূচকীয় সমীকরণের উভয় পক্ষকে একই মৌলিক ভিত্তিতে (যেমন ২, ৩ বা ৫) প্রকাশ করা যায়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত সমীকরণ: 2^(x + 7) = 4^(x + 2)।",
                "ডানপক্ষকে ২-এর ঘাতে প্রকাশ করি: 4^(x + 2) = (2²)^(x + 2) = 2^(2x + 4)।",
                "উভয় পক্ষের ভিত্তি ২ সমান হওয়ায় ঘাত তুলনা করে পাই: x + 7 = 2x + 4।",
                "অতএব, 2x - x = 7 - 4 => x = 3।"
            ),
            commonMistakeWarningBn = "4^(x + 2) কে 2^(2x + 2) লেখা ভুল; ২ দিয়ে পুরো (x + 2) কে গুণ করতে হবে।",
            bcsExamNumber = 45,
            examYear = 2023,
            questionPaperRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২৩)",
            sourceUrlOrPubRef = "https://bcsmate.com / BPSC 45th Preliminary Question Solution (19 May 2023)",
            provenanceEvidenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) প্রশ্নপত্রে '2^(x+7) = 4^(x+2) হলে x এর মান কত?' হুবহু এসেছে (উত্তর: 3)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_44_exp_geom_mean",
            legacyIdAliases = setOf("bcs_ver_44_exp_geom_mean"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "গুণোত্তর অনুক্রম ও ধারা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৪তম বিসিএস প্রিলিমিনারি (২৭ মে ২০২২)",
            questionBn = "১৮ এবং ৭২ এর গুণোত্তর গড় (Geometric Mean) কোনটি?",
            optionsBn = listOf("২৪", "৩৬", "৪৫", "৪৮"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "গুণোত্তর গড়ের উৎপাদক বর্গমূল কৌশল",
            formulaUsedBn = "গুণোত্তর গড় = √(a × b)",
            shortcutTechniqueBn = "১৮ × ৭২ গুণ না করে ৭২-কে ১৮×৪ লিখুন: √(১৮ × ১৮ × ৪) = ১৮ × ২ = ৩৬।",
            whenToUseShortcutBn = "যখন দুটি সংখ্যার গুণোত্তর গড় বা গুণোত্তর ধারার মধ্যপদ নির্ণয় করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "দুটি ধনাত্মক সংখ্যা a ও b-এর গুণোত্তর গড় = √(a × b)।",
                "এখানে a = ১৮ এবং b = ৭২।",
                "গুণোত্তর গড় = √(১৮ × ৭২) = √১২৯৬ = ৩৬।"
            ),
            commonMistakeWarningBn = "সমান্তর গড় (১৮ + ৭২)/২ = ৪৫ বের করা ভুল; গুণোত্তর গড়ের জন্য বর্গমূল নিতে হবে।",
            bcsExamNumber = 44,
            examYear = 2022,
            questionPaperRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২৭ মে ২০২২) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৪তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২২)",
            sourceUrlOrPubRef = "https://bcsmate.com / BPSC 44th Preliminary Question Solution (27 May 2022)",
            provenanceEvidenceBn = "২৭ মে ২০২২ তারিখে অনুষ্ঠিত ৪৪তম বিসিএস প্রিলিমিনারি পরীক্ষায় '১৮ এবং ৭২ এর গুণোত্তর গড় কোনটি?' হুবহু এসেছে (উত্তর: ৩৬)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_43_exp_power_log",
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "সূচক ও লগারিদম",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৩তম বিসিএস প্রিলিমিনারি (২৯ অক্টোবর ২০২১)",
            questionBn = "2^(log₂ 3 + log₂ 5) এর মান কত?",
            optionsBn = listOf("8", "15", "16", "30"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "ভিত্তি-লগারিদম বিপরীত অভেদ (a^(log_a M) = M)",
            formulaUsedBn = "a^(log_a M + log_a N) = a^(log_a (MN)) = M × N",
            shortcutTechniqueBn = "ঘাত অংশে log₂ 3 + log₂ 5 = log₂(3×5) = log₂ 15; আর 2^(log₂ 15) = 15।",
            whenToUseShortcutBn = "যখন সূচকের ভিত্তি এবং ঘাতে থাকা লগারিদমের ভিত্তি একই (এখানে ২) হয়।",
            standardStepByStepBn = listOf(
                "প্রথমে সূচকের লগারিদম অংশ সরল করি: log₂ 3 + log₂ 5 = log₂(3 × 5) = log₂ 15।",
                "প্রদত্ত রাশিটি দাঁড়ায়: 2^(log₂ 15)।",
                "লগারিদমের মৌলিক অভেদ a^(log_a M) = M অনুযায়ী 2^(log₂ 15) = 15।"
            ),
            commonMistakeWarningBn = "3 + 5 = 8 যোগ করে বা ২ দিয়ে গুণ করে ৩০ লেখা ভুল।",
            bcsExamNumber = 43,
            examYear = 2021,
            questionPaperRefBn = "৪৩তম বিসিএস প্রিলিমিনারি (২৯ অক্টোবর ২০২১) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৩তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://bcsmate.com / BPSC 43rd Preliminary Question Solution (29 October 2021)",
            provenanceEvidenceBn = "২৯ অক্টোবর ২০২১ তারিখে অনুষ্ঠিত ৪৩তম বিসিএস প্রিলিমিনারি পরীক্ষায় '2^(log₂ 3 + log₂ 5) এর মান কত?' হুবহু এসেছে (উত্তর: ১৫)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_25_series_sum_99",
            legacyIdAliases = setOf("bcs_prev_38_exp_1"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "সমান্তর অনুক্রম ও ধারা",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "২৫তম বিসিএস প্রিলিমিনারি (২০০৪)",
            questionBn = "১ + ২ + ৩ + ৪ + ... + ৯৯ = কত?",
            optionsBn = listOf("৪৮৫০", "৪৯৫০", "৫০৫০", "৪৯০০"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "স্বাভাবিক সংখ্যার সমষ্টির গাউস সূত্র",
            formulaUsedBn = "S = n(n + 1) / 2",
            shortcutTechniqueBn = "১ থেকে ৯৯ পর্যন্ত যোগফল = ৯৯ × ৫০ = ৪৯৫০ (অথবা ১ থেকে ১০০ এর যোগফল ৫০৫০ থেকে ১০০ বিয়োগ করলে ৪৯৫০)।",
            whenToUseShortcutBn = "১ থেকে শুরু হওয়া যেকোনো ক্রমিক স্বাভাবিক সংখ্যার ধারার সমষ্টি নির্ণয়ে।",
            standardStepByStepBn = listOf(
                "প্রথম n সংখ্যক স্বাভাবিক সংখ্যার সমষ্টি S = n(n + 1) / 2।",
                "এখানে শেষ পদ n = ৯৯।",
                "অতএব সমষ্টি = ৯৯ × (৯৯ + ১) / ২ = ৯৯ × ১০০ / ২ = ৯৯ × ৫০ = ৪৯৫০।"
            ),
            commonMistakeWarningBn = "১ থেকে ১০০-এর মুখস্থ যোগফল ৫০৫০ বসিয়ে দেওয়া সাধারণ ভুল; এখানে শেষ পদ ৯৯।",
            bcsExamNumber = 25,
            examYear = 2004,
            questionPaperRefBn = "২৫তম বিসিএস প্রিলিমিনারি (২০০৪) — গণিত অংশ",
            sourceTitleBn = "BPSC ২৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ (২০০৪)",
            sourceUrlOrPubRef = "https://bcsmate.com / BPSC 25th Preliminary Question Archive (2004)",
            provenanceEvidenceBn = "২৫তম বিসিএস প্রিলিমিনারি (২০০৪) পরীক্ষায় '1 + 2 + 3 + 4 + ... + 99 = কত?' প্রশ্নটি হুবহু এসেছে (উত্তর: ৪৯৫০)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),

        // ==================== UNIT 4: GEOMETRY & MENSURATION (4 Verified Questions) ====================
        BcsMathQuestion(
            id = "bcs_hist_44_geom_equilateral_h",
            legacyIdAliases = setOf("bcs_ver_44_geom_equilateral"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "রেখা, কোণ ও ত্রিভুজ",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৪তম বিসিএস প্রিলিমিনারি (২৭ মে ২০২২)",
            questionBn = "একটি সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য ২ সে.মি. এবং উচ্চতা x সে.মি. হলে, x এর মান কোনটি?",
            optionsBn = listOf("√2", "√3", "2", "2√3"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "সমবাহু ত্রিভুজের উচ্চতার সরাসরি সূত্র",
            formulaUsedBn = "উচ্চতা h = (√3 / 2) × বাহু",
            shortcutTechniqueBn = "উচ্চতা h = (√3/2)a = (√3/2)×2 = √3 সে.মি.।",
            whenToUseShortcutBn = "যখন সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য থেকে সরাসরি উচ্চতা বা মধ্যমা নির্ণয় করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "সমবাহু ত্রিভুজের বাহু a হলে উচ্চতার সূত্র h = (√3 / 2) × a।",
                "এখানে বাহুর দৈর্ঘ্য a = ২ সে.মি. এবং উচ্চতা = x সে.মি.।",
                "অতএব x = (√3 / 2) × ২ = √3।"
            ),
            commonMistakeWarningBn = "ক্ষেত্রফলের সূত্র (√3/4)a² এর সাথে উচ্চতার সূত্র (√3/2)a গুলিয়ে ফেলা।",
            bcsExamNumber = 44,
            examYear = 2022,
            questionPaperRefBn = "৪৪তম বিসিএস প্রিলিমিনারি (২৭ মে ২০২২) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৪তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২২)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 44th Preliminary Question Solution (27 May 2022)",
            provenanceEvidenceBn = "৪৪তম বিসিএস প্রিলিমিনারি (২৭ মে ২০২২) পরীক্ষায় 'একটি সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য ২ সে.মি. এবং উচ্চতা x সে.মি. হলে, x এর মান কোনটি?' হুবহু এসেছে (উত্তর: √3)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_41_geom_inscribed_tri",
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "বৃত্ত ও সমবাহু ত্রিভুজ",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১)",
            questionBn = "৬ সে.মি. ব্যাসার্ধ বিশিষ্ট বৃত্তের অন্তঃস্থ একটি সমবাহু ত্রিভুজের ক্ষেত্রফল কত?",
            optionsBn = listOf("9√3 বর্গ সে.মি.", "18√3 বর্গ সে.মি.", "27√3 বর্গ সে.মি.", "36√3 বর্গ সে.মি."),
            correctIndex = 2,
            difficulty = PracticeDifficulty.HARD,
            shortcutTitleBn = "পরিব্যাসার্ধ R থেকে অন্তঃস্থ সমবাহু ত্রিভুজের ক্ষেত্রফল",
            formulaUsedBn = "ক্ষেত্রফল = (3√3 / 4) × R²",
            shortcutTechniqueBn = "বৃত্তের ব্যাসার্ধ R = 6 হলে অন্তঃস্থ সমবাহু ত্রিভুজের ক্ষেত্রফল = (3√3 / 4) × 6² = (3√3 / 4) × 36 = 27√3 বর্গ সে.মি.।",
            whenToUseShortcutBn = "যখন বৃত্তের ব্যাসার্ধ R দেওয়া থাকে এবং বৃত্তে অন্তর্লিখিত সমবাহু ত্রিভুজের ক্ষেত্রফল বের করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "বৃত্তের ব্যাসার্ধ R = 6 সে.মি.। বৃত্তে অন্তর্লিখিত সমবাহু ত্রিভুজের বাহু a হলে R = a / √3।",
                "সুতরাং সমবাহু ত্রিভুজটির বাহু a = R√3 = 6√3 সে.মি.।",
                "সমবাহু ত্রিভুজের ক্ষেত্রফল = (√3 / 4) × a² = (√3 / 4) × (6√3)² = (√3 / 4) × 108 = 27√3 বর্গ সে.মি.।"
            ),
            commonMistakeWarningBn = "বাহু a = 6 ধরে (√3/4)×36 = 9√3 বের করা ভুল, কারণ ৬ হলো পরিবৃত্তের ব্যাসার্ধ R।",
            bcsExamNumber = 41,
            examYear = 2021,
            questionPaperRefBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) — গাণিতিক যুক্তি অংশ (প্রশ্ন ১০/১৫)",
            sourceTitleBn = "BPSC ৪১তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 41st Preliminary Question Solution (19 March 2021)",
            provenanceEvidenceBn = "১৯ মার্চ ২০২১ তারিখে অনুষ্ঠিত ৪১তম বিসিএস প্রিলিমিনারি পরীক্ষায় '৬ সে.মি. ব্যাসার্ধ বিশিষ্ট বৃত্তের অন্তঃস্থ একটি সমবাহু ত্রিভুজের ক্ষেত্রফল কত?' হুবহু এসেছে (উত্তর: 27√3 বর্গ সে.মি.)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_37_geom_sq_diag",
            legacyIdAliases = setOf("bcs_prev_37_geom_1"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "বর্গক্ষেত্র ও পিথাগোরাস",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৩৭তম বিসিএস প্রিলিমিনারি (৩০ সেপ্টেম্বর ২০১৬)",
            questionBn = "একটি বর্গক্ষেত্রের কর্ণের দৈর্ঘ্য 4√2 একক হলে, ঐ বর্গক্ষেত্রের ক্ষেত্রফল কত বর্গ একক?",
            optionsBn = listOf("8", "16", "24", "32"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "কর্ণ থেকে সরাসরি বর্গক্ষেত্রের ক্ষেত্রফল",
            formulaUsedBn = "ক্ষেত্রফল = (কর্ণ)² ÷ ২",
            shortcutTechniqueBn = "বর্গক্ষেত্রের ক্ষেত্রফল = (কর্ণ)² / 2 = (4√2)² / 2 = 32 / 2 = 16 বর্গ একক।",
            whenToUseShortcutBn = "যখন বর্গক্ষেত্রের কর্ণের দৈর্ঘ্য সরাসরি দেওয়া থাকে এবং ক্ষেত্রফল বের করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "বর্গক্ষেত্রের এক বাহু a হলে কর্ণের দৈর্ঘ্য d = a√2।",
                "প্রশ্নমতে, a√2 = 4√2 => a = 4 একক।",
                "অতএব বর্গক্ষেত্রের ক্ষেত্রফল = a² = 4² = 16 বর্গ একক।"
            ),
            commonMistakeWarningBn = "(4√2)² = 32 বের করার পর ২ দিয়ে ভাগ করতে ভুলে যাওয়া।",
            bcsExamNumber = 37,
            examYear = 2016,
            questionPaperRefBn = "৩৭তম বিসিএস প্রিলিমিনারি (৩০ সেপ্টেম্বর ২০১৬) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৩৭তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০১৬)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 37th Preliminary Question Solution (30 September 2016)",
            provenanceEvidenceBn = "৩০ সেপ্টেম্বর ২০১৬ তারিখে অনুষ্ঠিত ৩৭তম বিসিএস প্রিলিমিনারি পরীক্ষায় 'একটি বর্গক্ষেত্রের কর্ণের দৈর্ঘ্য 4√2 একক হলে, ঐ বর্গক্ষেত্রের ক্ষেত্রফল কত বর্গ একক?' হুবহু এসেছে (উত্তর: ১৬ বর্গ একক)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_32_geom_equilateral_inc",
            legacyIdAliases = setOf("bcs_ver_32_geom_equilateral_inc"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "ত্রিভুজ ও পরিমিতি",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৩২তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০১২)",
            questionBn = "একটি সমবাহু ত্রিভুজের বাহুর প্রত্যেকটির দৈর্ঘ্য ২ মিটার বাড়ালে এর ক্ষেত্রফল 3√3 বর্গমিটার বেড়ে যায়। সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য কত?",
            optionsBn = listOf("১ মিটার", "২ মিটার", "৩ মিটার", "৪ মিটার"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "সমবাহু ত্রিভুজের ক্ষেত্রফল বৃদ্ধির অন্তর কৌশল",
            formulaUsedBn = "(a + d)² - a² = 4k (যখন ক্ষেত্রফল বৃদ্ধি = k√3)",
            shortcutTechniqueBn = "(a + 2)² - a² = 4 × 3 => 4a + 4 = 12 => 4a = 8 => a = 2 মিটার।",
            whenToUseShortcutBn = "যখন সমবাহু ত্রিভুজের বাহু নির্দিষ্ট একক বাড়ালে ক্ষেত্রফল k√3 বৃদ্ধি পায়।",
            standardStepByStepBn = listOf(
                "ধরি, সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য = a মিটার। প্রাথমিক ক্ষেত্রফল = (√3/4)a²।",
                "বাহু ২ মিটার বাড়ালে নতুন ক্ষেত্রফল = (√3/4)(a + 2)²।",
                "প্রশ্নমতে, (√3/4)[(a + 2)² - a²] = 3√3।",
                "উভয় পক্ষকে (√3/4) দ্বারা ভাগ করে পাই: (a² + 4a + 4) - a² = 12 => 4a + 4 = 12 => 4a = 8 => a = 2 মিটার।"
            ),
            commonMistakeWarningBn = "(√3/4) পক্ষান্তর করার সময় ৪ গুণ না করে ভাগ করে ফেলা।",
            bcsExamNumber = 32,
            examYear = 2012,
            questionPaperRefBn = "৩২তম (বিশেষ) বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০১২) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৩২তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ (২০১২)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 32nd Preliminary Question Archive (2012)",
            provenanceEvidenceBn = "৩২তম বিসিএস প্রিলিমিনারি (২০১২) পরীক্ষায় সমবাহু ত্রিভুজের বাহু ২ মিটার বাড়ালে ক্ষেত্রফল 3√3 বর্গমিটার বৃদ্ধির এই প্রশ্নটি হুবহু এসেছে (উত্তর: ২ মিটার)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),

        // ==================== UNIT 5: SETS, COMBINATORICS, STATISTICS & PROBABILITY (4 Verified Questions) ====================
        BcsMathQuestion(
            id = "bcs_hist_45_perm_digits",
            legacyIdAliases = setOf("bcs_ver_45_perm_digits"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "বিন্যাস (Permutations)",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩)",
            questionBn = "0, 1, 2, 3, 4 অংকগুলি দ্বারা কতগুলি পাঁচ অংকের অর্থপূর্ণ সংখ্যা গঠন করা যাবে (অংক পুনরাবৃত্তি না করে)?",
            optionsBn = listOf("24টি", "96টি", "120টি", "240টি"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "শূন্য (0) বাদে প্রথম স্থানের বিন্যাস কৌশল",
            formulaUsedBn = "(n - 1) × (n - 1)! বা n! - (n - 1)!",
            shortcutTechniqueBn = "মোট বিন্যাস 5! = ১২০ থেকে প্রথম ঘরে ০ রেখে গঠিত 4! = ২৪ বিয়োগ করুন: ১২০ - ২৪ = ৯৬টি।",
            whenToUseShortcutBn = "যখন প্রদত্ত n-সংখ্যক অংকের মধ্যে 0 থাকে এবং সব অংক নিয়ে n-অংকের অর্থপূর্ণ সংখ্যা গঠন করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "৫টি অংক (0, 1, 2, 3, 4) দিয়ে পাঁচ অংকের অর্থপূর্ণ সংখ্যা গঠনের ক্ষেত্রে প্রথম স্থানে 0 বসতে পারে না।",
                "তাই প্রথম স্থানটি (1, 2, 3, 4)-এর যেকোনো ১টি দ্বারা ৪ উপায়ে পূরণ করা যায়।",
                "বাকি ৪টি স্থান অবশিষ্ট ৪টি অংক দ্বারা 4! = ২৪ উপায়ে পূরণ করা যায়।",
                "অতএব মোট অর্থপূর্ণ সংখ্যা = ৪ × ২৪ = ৯৬টি।"
            ),
            commonMistakeWarningBn = "সরাসরি 5! = 120 উত্তর করা ভুল, কারণ শুরুতে 0 থাকলে সংখ্যাটি চার অংকের হয়ে যায়।",
            bcsExamNumber = 45,
            examYear = 2023,
            questionPaperRefBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪৫তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২৩)",
            sourceUrlOrPubRef = "https://livemcq.com / BPSC 45th Preliminary Question Solution (19 May 2023)",
            provenanceEvidenceBn = "৪৫তম বিসিএস প্রিলিমিনারি (১৯ মে ২০২৩) পরীক্ষায় '0, 1, 2, 3, 4 অংকগুলি দ্বারা কতগুলি পাঁচ অংকের অর্থপূর্ণ সংখ্যা গঠন করা যাবে?' প্রশ্নটি এসেছে (উত্তর: ৯৬টি)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_41_comb_committee",
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "সমাবেশ (Combinations)",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১)",
            questionBn = "৫ জন পুরুষ ও ৪ জন মহিলার একটি দল থেকে একজন পুরুষ ও দুজন মহিলা নিয়ে কত প্রকারে একটি কমিটি গঠন করা যাবে?",
            optionsBn = listOf("১০ প্রকারে", "২০ প্রকারে", "৩০ প্রকারে", "৪০ প্রকারে"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "স্বাধীন দল বাছাইয়ের গুণন সমাবেশ সূত্র",
            formulaUsedBn = "মোট উপায় = ⁵C₁ × ⁴C₂",
            shortcutTechniqueBn = "⁵C₁ × ⁴C₂ = ৫ × [(৪×৩)/২] = ৫ × ৬ = ৩০ প্রকারে।",
            whenToUseShortcutBn = "যখন দুটি ভিন্ন গ্রুপ (পুরুষ ও মহিলা) থেকে নির্দিষ্ট সংখ্যক সদস্য নিয়ে কমিটি গঠন করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "৫ জন পুরুষ থেকে ১ জন পুরুষ বাছাই করার উপায় = ⁵C₁ = ৫।",
                "৪ জন মহিলা থেকে ২ জন মহিলা বাছাই করার উপায় = ⁴C₂ = (৪ × ৩) / (২ × ১) = ৬।",
                "গণনার গুণন বিধি অনুযায়ী মোট কমিটি গঠনের উপায় = ৫ × ৬ = ৩০ প্রকারে।"
            ),
            commonMistakeWarningBn = "৫ এবং ৬ গুন না করে যোগ করে ১১ করা ভুল।",
            bcsExamNumber = 41,
            examYear = 2021,
            questionPaperRefBn = "৪১তম বিসিএস প্রিলিমিনারি (১৯ মার্চ ২০২১) — গাণিতিক যুক্তি অংশ (প্রশ্ন ২/১৫)",
            sourceTitleBn = "BPSC ৪১তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০২১)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 41st Preliminary Question Solution (19 March 2021)",
            provenanceEvidenceBn = "১৯ মার্চ ২০২১ তারিখে অনুষ্ঠিত ৪১তম বিসিএস প্রিলিমিনারি পরীক্ষায় '৫ জন পুরুষ ও ৪ জন মহিলার একটি দল থেকে একজন পুরুষ ও দুজন মহিলা নিয়ে কত প্রকারে একটি কমিটি গঠন করা যাবে?' হুবহু এসেছে (উত্তর: ৩০ প্রকারে)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_40_comb_6players",
            legacyIdAliases = setOf("bcs_prev_35_set_1"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "সমাবেশ (Combinations)",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "৪০তম বিসিএস প্রিলিমিনারি (৩ মে ২০১৯)",
            questionBn = "৬ জন খেলোয়াড়কে সমান সংখ্যক দুইটি দলে কতভাবে বিভক্ত করা যায়?",
            optionsBn = listOf("১০ ভাবে", "১৫ ভাবে", "২০ ভাবে", "৩০ ভাবে"),
            correctIndex = 0,
            difficulty = PracticeDifficulty.HARD,
            shortcutTitleBn = "সমান সংখ্যক নামহীন দলে বিভাজনের সমাবেশ সূত্র",
            formulaUsedBn = "উপায় = ²ⁿCₙ ÷ 2!",
            shortcutTechniqueBn = "সমান সংখ্যক ২টি নামহীন দলে ভাগ করার সূত্র = ⁶C₃ / 2! = ২০ / ২ = ১০ ভাবে।",
            whenToUseShortcutBn = "যখন 2n সংখ্যক বস্তুকে সমান আকারের দুটি নামহীন দলে বিভক্ত করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "৬ জন খেলোয়াড়কে সমান ২টি দলে ভাগ করলে প্রতি দলে খেলোয়াড় হবে ৩ জন।",
                "৬ জন থেকে ৩ জন বাছাইয়ের উপায় = ⁶C₃ = (৬ × ৫ × ৪) / (৩ × ২ × ১) = ২০।",
                "যেহেতু দল দুটি নামহীন ও সমান আকারের, তাই প্রতিটি বিভাজন ২ বার গণনা হয়েছে।",
                "অতএব মোট দল গঠনের উপায় = ২০ / ২! = ১০ ভাবে।"
            ),
            commonMistakeWarningBn = "2! দিয়ে ভাগ না করে সরাসরি ⁶C₃ = ২০ উত্তর করা সবচেয়ে প্রচলিত ভুল।",
            bcsExamNumber = 40,
            examYear = 2019,
            questionPaperRefBn = "৪০তম বিসিএস প্রিলিমিনারি (৩ মে ২০১৯) — গাণিতিক যুক্তি অংশ",
            sourceTitleBn = "BPSC ৪০তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র (২০১৯)",
            sourceUrlOrPubRef = "https://myexaminer.net / BPSC 40th Preliminary Question Solution (3 May 2019)",
            provenanceEvidenceBn = "৩ মে ২০১৯ তারিখে অনুষ্ঠিত ৪০তম বিসিএস প্রিলিমিনারি পরীক্ষায় '৬ জন খেলোয়াড়কে সমান সংখ্যক দুটি দলে কতভাবে বিভক্ত করা যায়?' হুবহু এসেছে (উত্তর: ১০ ভাবে)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        ),
        BcsMathQuestion(
            id = "bcs_hist_12_prob_3coins",
            legacyIdAliases = setOf("bcs_prev_36_set_1"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "সম্ভাব্যতা (Probability)",
            sourceType = BcsQuestionSourceType.OFFICIAL_PREVIOUS_BCS,
            examReferenceBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১)",
            questionBn = "তিনটি মুদ্রা একসাথে একবার নিক্ষেপ করা হলে সবগুলো একই ফলাফল (সব হেড বা সব টেল) আসার সম্ভাবনা কত?",
            optionsBn = listOf("১/৮", "১/৪", "৩/৮", "১/২"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "মুদ্রা নিক্ষেপে সমফলাফল সম্ভাবনার সূত্র",
            formulaUsedBn = "সম্ভাবনা = ২ / ২ⁿ = ১ / ২ⁿ⁻¹",
            shortcutTechniqueBn = "n সংখ্যক মুদ্রায় সবগুলো একই ফলাফল আসার সম্ভাবনা = 1 / 2ⁿ⁻¹। এখানে n = 3 হওয়ায় 1 / 2² = ১/৪।",
            whenToUseShortcutBn = "যখন একাধিক মুদ্রা নিক্ষেপে সবগুলো একই পিঠ (সব H বা সব T) আসার সম্ভাবনা চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "৩টি মুদ্রা একসাথে নিক্ষেপ করলে মোট নমুনাবিন্দু = 2³ = ৮টি।",
                "সবগুলো একই ফলাফল আসার অনুকূল ঘটনা ২টি: {HHH, TTT}।",
                "অতএব নির্ণেয় সম্ভাবনা = ২ / ৮ = ১/৪।"
            ),
            commonMistakeWarningBn = "শুধু HHH ধরে ১/৮ উত্তর করা ভুল, কারণ সব টেল (TTT)-ও একই ফলাফল।",
            bcsExamNumber = 12,
            examYear = 1991,
            questionPaperRefBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১) — গণিত অংশ",
            sourceTitleBn = "BPSC ১২তম বিসিএস প্রিলিমিনারি প্রশ্নপত্র আর্কাইভ (১৯৯১)",
            sourceUrlOrPubRef = "https://sattacademy.com / BPSC 12th Preliminary Question Archive (1991)",
            provenanceEvidenceBn = "১২তম বিসিএস প্রিলিমিনারি (১৯৯১) পরীক্ষায় তিনটি মুদ্রা নিক্ষেপে একই ফলাফল আসার সম্ভাবনার এই প্রশ্নটি এসেছে (উত্তর: ১/৪)।",
            verificationStatus = BcsVerificationStatus.VERIFIED_HISTORICAL
        )
    )

    /**
     * Syllabus-aligned BCS model practice questions (18 total: 13 reclassified during the two-pass
     * provenance audit + 5 syllabus practice items). Every item has a canonical `bcs_prac_*` ID
     * and [BcsQuestionSourceType.SYLLABUS_PRACTICE].
     */
    val syllabusAlignedPracticeQuestions: List<BcsMathQuestion> = listOf(
        BcsMathQuestion(
            id = "bcs_prac_u1_simple_interest_double",
            legacyIdAliases = setOf("bcs_prev_44_arith_1"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "সরল ও যৌগিক মুনাফা",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ১)",
            questionBn = "কোনো আসল ৩ বছরে সরল মুনাফায় তার দ্বিগুণ হলে বার্ষিক মুনাফার হার কত?",
            optionsBn = listOf("২৫%", "৩০%", "৩৩.৩৩%", "৫০%"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "আসল m-গুণ হওয়ার মুনাফার হার সূত্র",
            formulaUsedBn = "r = [(m - 1) × 100] ÷ n %",
            shortcutTechniqueBn = "আসল n বছরে m গুণ হলে হার r = [(২ - ১) × ১০০] / ৩ = ৩৩.৩৩%।",
            whenToUseShortcutBn = "যখন সরল মুনাফায় কোনো আসল নির্দিষ্ট সময়ে কয়গুণ হয় তা বলা থাকে।",
            standardStepByStepBn = listOf(
                "ধরি আসল P = ১০০ টাকা। ৩ বছরে মুনাফা-আসলে দ্বিগুণ হলে A = ২০০ টাকা।",
                "সুতরাং ৩ বছরের মুনাফা I = ২০০ - ১০০ = ১০০ টাকা।",
                "বার্ষিক মুনাফার হার r = (I × ১০০) / (P × n) = (১০০ × ১০০) / (১০০ × ৩) = ৩৩.৩৩%।"
            ),
            commonMistakeWarningBn = "দ্বিগুণ দেখে m = ২ সরাসরি ১০০-এর সাথে গুণ করে ২০০/৩% বের করা ভুল; মুনাফা হলো (m - 1) গুণ।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (পাটিগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u1_loss_20pct",
            legacyIdAliases = setOf("bcs_prev_43_arith_1"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "লাভ ও ক্ষতি",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ১)",
            questionBn = "একটি দ্রব্য ৪০০ টাকায় ক্রয় করে ২০% ক্ষতিতে বিক্রয় করা হলে বিক্রয়মূল্য কত?",
            optionsBn = listOf("৩০০ টাকা", "৩২০ টাকা", "৩৪০ টাকা", "৩৬০ টাকা"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "শতকরা ক্ষতি থেকে সরাসরি বিক্রয়মূল্য",
            formulaUsedBn = "বিক্রয়মূল্য = ক্রয়মূল্য × (১০০ - ক্ষতি%) ÷ ১০০",
            shortcutTechniqueBn = "২০% ক্ষতিতে বিক্রয়মূল্য = ক্রয়মূল্যের ৮০% = ৪০০ × ০.৮ = ৩২০ টাকা।",
            whenToUseShortcutBn = "ক্রয়মূল্য ও শতকরা লাভ/ক্ষতির হার দেওয়া থাকলে সরাসরি বিক্রয়মূল্য নির্ণয়ে।",
            standardStepByStepBn = listOf(
                "ক্রয়মূল্য = ৪০০ টাকা এবং ক্ষতির হার = ২০%।",
                "ক্ষতির পরিমাণ = ৪০০ × (২০ / ১০০) = ৮০ টাকা।",
                "বিক্রয়মূল্য = ক্রয়মূল্য - ক্ষতি = ৪০০ - ৮০ = ৩২০ টাকা।"
            ),
            commonMistakeWarningBn = "৪০০ থেকে সরাসরি ২০ বিয়োগ করে ৩৮০ টাকা লেখা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (পাটিগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u1_cost_from_10pct_loss",
            legacyIdAliases = setOf("bcs_ver_44_arith_loss"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "লাভ ও ক্ষতি",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ১)",
            questionBn = "একটি দ্রব্য ১৮০ টাকায় বিক্রয় করায় ১০% ক্ষতি হলে, দ্রব্যটির ক্রয়মূল্য কত?",
            optionsBn = listOf("১৬০ টাকা", "১৯৮ টাকা", "২০০ টাকা", "২২০ টাকা"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "ক্ষতির হার থেকে সরাসরি ক্রয়মূল্য নির্ণয়",
            formulaUsedBn = "ক্রয়মূল্য = বিক্রয়মূল্য × ১০০ / (১০০ - ক্ষতির হার)",
            shortcutTechniqueBn = "১০% ক্ষতি মানে বিক্রয়মূল্য ক্রয়মূল্যের ৯০%। তাই ক্রয়মূল্য = (১৮০ / ৯০) × ১০০ = ২০০ টাকা।",
            whenToUseShortcutBn = "যখন বিক্রয়মূল্য এবং শতকরা লাভ বা ক্ষতির হার দেওয়া থাকে এবং ক্রয়মূল্য নির্ণয় করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "১০% ক্ষতিতে বিক্রয়মূল্য = ১০০ - ১০ = ৯০ টাকা (যখন ক্রয়মূল্য ১০০ টাকা)।",
                "বিক্রয়মূল্য ৯০ টাকা হলে ক্রয়মূল্য = ১০০ টাকা।",
                "বিক্রয়মূল্য ১৮০ টাকা হলে ক্রয়মূল্য = (১০০ × ১৮০) / ৯০ = ২০০ টাকা।"
            ),
            commonMistakeWarningBn = "১৮০ টাকার ১০% (১৮ টাকা) সরাসরি যোগ করে ১৯৮ টাকা উত্তর করা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (পাটিগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u1_lcm_from_ratio",
            legacyIdAliases = setOf("bcs_prac_arith_lcm"),
            unit = BcsSyllabusUnit.UNIT_1_ARITHMETIC,
            topicBn = "ল.সা.গু ও গ.সা.গু",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ১)",
            questionBn = "দুটি সংখ্যার অনুপাত ৩ : ৪ এবং তাদের গ.সা.গু ৫ হলে সংখ্যা দুটির ল.সা.গু কত?",
            optionsBn = listOf("২০", "৪৫", "৬০", "৭৫"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "অনুপাত ও গ.সা.গু থেকে সরাসরি ল.সা.গু",
            formulaUsedBn = "ল.সা.গু = a × b × গ.সা.গু",
            shortcutTechniqueBn = "ল.সা.গু = অনুপাতদ্বয়ের গুণফল × গ.সা.গু = ৩ × ৪ × ৫ = ৬০।",
            whenToUseShortcutBn = "যখন দুটি সহমৌলিক অনুপাত (a : b) এবং তাদের গ.সা.গু দেওয়া থাকে।",
            standardStepByStepBn = listOf(
                "অনুপাত ৩ : ৪ এবং গ.সা.গু ৫ হওয়ায় সংখ্যা দুটি যথাক্রমে ৩×৫ = ১৫ এবং ৪×৫ = ২০।",
                "১৫ ও ২০-এর ল.সা.গু = ৬০।"
            ),
            commonMistakeWarningBn = "৫ কে দুইবার গুণ করে ৩ × ৪ × ৫ × ৫ = ৩০০ করা ভুল; গ.সা.গু একবারই গুণ হবে।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (পাটিগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u2_cube_reciprocal",
            legacyIdAliases = setOf("bcs_prev_45_alg_1"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় সূত্রাবলি",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ২)",
            questionBn = "যদি x + 1/x = 3 হয়, তবে x³ + 1/x³ এর মান কত?",
            optionsBn = listOf("9", "18", "27", "36"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "ঘনের বীজগণিতীয় মান নির্ণয় শর্টকাট",
            formulaUsedBn = "x³ + 1/x³ = k³ - 3k",
            shortcutTechniqueBn = "x + 1/x = k হলে x³ + 1/x³ = k³ - 3k = 3³ - 3×3 = 18।",
            whenToUseShortcutBn = "যখন x + 1/x = k দেওয়া থাকে এবং x³ + 1/x³ এর মান চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত মান x + 1/x = 3।",
                "ঘনের অনুসিদ্ধান্ত অনুযায়ী: x³ + 1/x³ = (x + 1/x)³ - 3·x·(1/x)·(x + 1/x)।",
                "মান বসিয়ে পাই: 3³ - 3(3) = 27 - 9 = 18।"
            ),
            commonMistakeWarningBn = "+ চিহ্ন দেখে k³ + 3k = 27 + 9 = 36 করে ফেলা সাধারণ ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (বীজগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u2_sq_reciprocal",
            legacyIdAliases = setOf("bcs_prev_41_alg_1"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় সূত্রাবলি",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ২)",
            questionBn = "যদি x - 1/x = 4 হয়, তবে x² + 1/x² এর মান কত?",
            optionsBn = listOf("14", "16", "18", "20"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "বর্গের সমষ্টির অনুসিদ্ধান্ত কৌশল",
            formulaUsedBn = "x² + 1/x² = k² + 2 (যখন x - 1/x = k)",
            shortcutTechniqueBn = "x - 1/x = k হলে x² + 1/x² = k² + 2 = 4² + 2 = 18।",
            whenToUseShortcutBn = "যখন বিয়োগফল (x - 1/x) দেওয়া থাকে এবং বর্গের সমষ্টি চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "অনুসিদ্ধান্ত অনুযায়ী x² + 1/x² = (x - 1/x)² + 2·x·(1/x)।",
                "মান বসিয়ে পাই: 4² + 2 = 16 + 2 = 18।"
            ),
            commonMistakeWarningBn = "k² - 2 = 14 করা ভুল; বিয়োগফল দেওয়া থাকলে বর্গের সূত্রে +2 হয়।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (বীজগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u2_factor_x2_4x_5",
            legacyIdAliases = setOf("bcs_ver_45_alg_factor"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বহুপদীর উৎপাদক বিশ্লেষণ",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ২)",
            questionBn = "x² - 4x - 5 এর উৎপাদক নিচের কোনটি?",
            optionsBn = listOf("(x - 5)(x + 1)", "(x + 5)(x - 1)", "(x - 5)(x - 1)", "(x + 5)(x + 1)"),
            correctIndex = 0,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "মূল বসিয়ে উৎপাদক যাচাই (Zero-Root Test)",
            formulaUsedBn = "x² + (a+b)x + ab = (x + a)(x + b)",
            shortcutTechniqueBn = "x = 5 বসালে 5² - 4(5) - 5 = 0 হয়, তাই (x - 5) একটি উৎপাদক; এবং ধ্রুবক পদ -5 পেতে অপর উৎপাদকটি (x + 1)।",
            whenToUseShortcutBn = "যখন বহুপদীর উৎপাদক এমসিকিউ অপশন থেকে দ্রুত যাচাই করতে হয়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত রাশি = x² - 4x - 5।",
                "মধ্যপদ বিশ্লেষণ অনুযায়ী: (-5) × (+1) = -5 এবং (-5) + (+1) = -4।",
                "x² - 5x + x - 5 = x(x - 5) + 1(x - 5) = (x - 5)(x + 1)।"
            ),
            commonMistakeWarningBn = "-4x দেখে (x + 5)(x - 1) নির্বাচন করলে মধ্যপদ +4x হয়ে যাবে, যা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (বীজগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u2_lcm_gcd_prod",
            legacyIdAliases = setOf("bcs_ver_45_alg_lcm_gcd"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "বীজগণিতীয় রাশির ল.সা.গু ও গ.সা.গু",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ২)",
            questionBn = "x²y + xy² এবং x² + xy রাশি দুইটির ল.সা.গু এবং গ.সা.গু-এর গুণফল কত?",
            optionsBn = listOf("x²y(x + y)", "xy(x + y)²", "x²y(x + y)²", "x³y²(x + y)"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "ল.সা.গু × গ.সা.গু = রাশিদ্বয়ের গুণফল উপপাদ্য",
            formulaUsedBn = "ল.সা.গু × গ.সা.গু = ১ম রাশি × ২য় রাশি",
            shortcutTechniqueBn = "আলাদাভাবে ল.সা.গু ও গ.সা.গু বের না করে সরাসরি রাশি দুটি গুণ করুন: xy(x + y) · x(x + y) = x²y(x + y)²।",
            whenToUseShortcutBn = "যখন সরাসরি দুটি বীজগণিতীয় রাশির ল.সা.গু ও গ.সা.গু-এর গুণফল জানতে চাওয়া হয়।",
            standardStepByStepBn = listOf(
                "১ম রাশি = x²y + xy² = xy(x + y) এবং ২য় রাশি = x² + xy = x(x + y)।",
                "আমরা জানি, দুটি রাশির ল.সা.গু × গ.সা.গু = রাশি দুইটির গুণফল।",
                "অতএব গুণফল = xy(x + y) × x(x + y) = x²y(x + y)²।"
            ),
            commonMistakeWarningBn = "শুধু ল.সা.গু xy(x + y) বা শুধু গ.সা.গু x(x + y) উত্তর হিসেবে বেছে নেওয়া ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (বীজগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u2_abs_inequality",
            legacyIdAliases = setOf("bcs_prac_alg_ineq"),
            unit = BcsSyllabusUnit.UNIT_2_ALGEBRA,
            topicBn = "সরল ও দ্বিঘাত অসমতা",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ২)",
            questionBn = "|2x - 3| < 5 অসমতাটির সমাধান সেট কোনটি?",
            optionsBn = listOf("-1 < x < 4", "-4 < x < 1", "x < 4", "1 < x < 4"),
            correctIndex = 0,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "পরমমান অসমতার দ্বিমুখী সীমা কৌশল",
            formulaUsedBn = "|ax + b| < c <=> -c < ax + b < c",
            shortcutTechniqueBn = "প্রান্তবিন্দু দুটি বের করুন: 2x - 3 = 5 => x = 4 এবং 2x - 3 = -5 => x = -1; তাই সীমাটি -1 < x < 4।",
            whenToUseShortcutBn = "যখন |ax + b| < c আকারের রৈখিক পরমমান অসমতা সমাধান করতে হয়।",
            standardStepByStepBn = listOf(
                "পরমমান অসমতার নিয়ম অনুযায়ী |A| < b হলে -b < A < b।",
                "সুতরাং -5 < 2x - 3 < 5।",
                "সব পক্ষে ৩ যোগ করে পাই: -2 < 2x < 8।",
                "২ দ্বারা ভাগ করে পাই: -1 < x < 4।"
            ),
            commonMistakeWarningBn = "শুধু 2x - 3 < 5 সমাধান করে x < 4 উত্তর করা ভুল; ঋণাত্মক সীমা -5 বিবেচনা করা আবশ্যক।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (বীজগণিত)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u3_log_base_x",
            legacyIdAliases = setOf("bcs_prev_44_exp_1"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "লগারিদম (Logarithms)",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৩)",
            questionBn = "log_x (1/8) = -3/2 হলে x এর মান কত?",
            optionsBn = listOf("2", "4", "8", "16"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "লগারিদম থেকে সূচক রূপান্তর কৌশল",
            formulaUsedBn = "log_x(N) = p <=> x^p = N",
            shortcutTechniqueBn = "অপশন টেস্ট: x = 4 বসালে 4^(-3/2) = (2²)^(-3/2) = 2⁻³ = 1/8 মিলে যায়।",
            whenToUseShortcutBn = "লগারিদমের ভিত্তি x নির্ণয়ের প্রশ্নে পূর্ণবর্গ অপশন থাকলে সরাসরি বসিয়ে পরীক্ষা করা সবচেয়ে দ্রুত।",
            standardStepByStepBn = listOf(
                "লগারিদমের সংজ্ঞা অনুযায়ী x^(-3/2) = 1/8 = 2⁻³।",
                "উভয় পক্ষের ঘাত (-1/3) নিলে পাই: x^(1/2) = 2¹ = 2।",
                "বর্গ করে পাই: x = 2² = 4।"
            ),
            commonMistakeWarningBn = "x^(1/2) = 2 থেকে বর্গ না করে x = 2 উত্তর করা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (সূচক ও লগারিদম)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u3_log_sum_45",
            legacyIdAliases = setOf("bcs_ver_43_exp_log"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "লগারিদম (Logarithms)",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৩)",
            questionBn = "2 log₂(3) + log₂(5) এর মান নিচের কোনটি?",
            optionsBn = listOf("log₂(11)", "log₂(15)", "log₂(30)", "log₂(45)"),
            correctIndex = 3,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "লগারিদমের ঘাত ও গুণফলের সমন্বিত সূত্র",
            formulaUsedBn = "k·log_b(M) + log_b(N) = log_b(M^k · N)",
            shortcutTechniqueBn = "k·log(a) + log(b) = log(aᵏ · b) = log₂(3² × 5) = log₂(45)।",
            whenToUseShortcutBn = "যখন একই ভিত্তিবিশিষ্ট একাধিক লগারিদমের যোগফলকে একক লগারিদমে প্রকাশ করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "লগারিদমের ঘাত সূত্র অনুযায়ী: 2 log₂(3) = log₂(3²) = log₂(9)।",
                "এখন যোগফলের সূত্র log_b(M) + log_b(N) = log_b(MN) প্রয়োগ করি।",
                "log₂(9) + log₂(5) = log₂(9 × 5) = log₂(45)।"
            ),
            commonMistakeWarningBn = "2 log₂(3) কে log₂(2×3) = log₂(6) ধরে 6×5 = 30 করা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (সূচক ও লগারিদম)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u3_gp_7th_term",
            legacyIdAliases = setOf("bcs_prac_exp_gp"),
            unit = BcsSyllabusUnit.UNIT_3_EXPONENTS_SERIES,
            topicBn = "গুণোত্তর অনুক্রম ও ধারা",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৩)",
            questionBn = "২ + ৪ + ৮ + ১৬ + ... গুণোত্তর ধারাটির ৭ম পদ কত?",
            optionsBn = listOf("৬৪", "১২৮", "২৫৬", "৫১২"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "গুণোত্তর ধারার n-তম পদের ঘাত কৌশল",
            formulaUsedBn = "n-তম পদ = a·r^(n - 1)",
            shortcutTechniqueBn = "ধারাটি ২-এর ঘাতের ধারা (2¹, 2², 2³...), তাই ৭ম পদ সরাসরি 2⁷ = ১২৮।",
            whenToUseShortcutBn = "যখন গুণোত্তর ধারার প্রথম পদ ও অনুপাত উভয়ই ২ বা ৩-এর ঘাত হয়।",
            standardStepByStepBn = listOf(
                "প্রথম পদ a = ২ এবং সাধারণ অনুপাত r = ৪/২ = ২।",
                "n-তম পদ = a·rⁿ⁻¹।",
                "৭ম পদ = ২ × ২⁷⁻¹ = ২ × ২⁶ = ২ × ৬৪ = ১২৮।"
            ),
            commonMistakeWarningBn = "rⁿ সূত্র ধরে ২ × ২⁷ = ২৫৬ বের করা ভুল; সূত্রে ঘাত হয় (n - 1)।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (সূচক ও ধারা)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u4_equilateral_area",
            legacyIdAliases = setOf("bcs_prev_45_geom_1"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "ত্রিভুজ ও পরিমিতি",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৪)",
            questionBn = "একটি সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য ২ সেমি হলে এর ক্ষেত্রফল কত বর্গ সেমি?",
            optionsBn = listOf("√3", "2√3", "4√3", "√3 / 2"),
            correctIndex = 0,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "সমবাহু ত্রিভুজের ক্ষেত্রফল সূত্র",
            formulaUsedBn = "ক্ষেত্রফল = (√3 / 4) × a²",
            shortcutTechniqueBn = "বাহু a = 2 হলে a² = 4 সরাসরি হরের 4-কে কেটে দেয়, ফলে ক্ষেত্রফল = √3।",
            whenToUseShortcutBn = "যেকোনো সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য দেওয়া থাকলে ক্ষেত্রফল নির্ণয়ে।",
            standardStepByStepBn = listOf(
                "সমবাহু ত্রিভুজের বাহুর দৈর্ঘ্য a = ২ সেমি।",
                "সমবাহু ত্রিভুজের ক্ষেত্রফলের সূত্র = (√3 / 4) × a²।",
                "ক্ষেত্রফল = (√3 / 4) × ২² = (√3 / 4) × ৪ = √3 বর্গ সেমি।"
            ),
            commonMistakeWarningBn = "(√3 / 2) × a² ব্যবহার করলে উত্তর দ্বিগুণ (2√3) হয়ে যাবে, যা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (জ্যামিতি ও পরিমিতি)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u4_tan_sq_angle",
            legacyIdAliases = setOf("bcs_ver_45_geom_angle"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "কোণ ও পিথাগোরাস অনুপাত",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৪)",
            questionBn = "যদি সূক্ষ্মকোণ θ-এর জন্য 1 + tan²θ = 4 হয়, তবে θ এর মান কত?",
            optionsBn = listOf("30°", "45°", "60°", "90°"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "সমকোণী ত্রিভুজের কোণ অনুপাত কৌশল",
            formulaUsedBn = "1 + tan²θ = sec²θ বা tan²θ = 4 - 1 = 3",
            shortcutTechniqueBn = "tan²θ = 3 => tanθ = √3 => θ = 60°।",
            whenToUseShortcutBn = "যখন সমকোণী ত্রিভুজের কোণ বা মৌলিক ত্রিকোণমিতিক অভেদ থেকে কোণের মান নির্ণয় করতে হয়।",
            standardStepByStepBn = listOf(
                "প্রদত্ত সমীকরণ: 1 + tan²θ = 4 => tan²θ = 4 - 1 = 3।",
                "সূক্ষ্মকোণ θ-এর জন্য বর্গমূল নিয়ে পাই: tanθ = √3।",
                "যেহেতু tan 60° = √3, তাই θ = 60°।"
            ),
            commonMistakeWarningBn = "tan 30° = 1/√3 এর সাথে tan 60° = √3 গুলিয়ে 30° উত্তর করা।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (জ্যামিতি ও পরিমিতি)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u4_circle_central_angle",
            legacyIdAliases = setOf("bcs_prac_geom_circle"),
            unit = BcsSyllabusUnit.UNIT_4_GEOMETRY_MENSURATION,
            topicBn = "বৃত্ত সংক্রান্ত উপপাদ্য",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৪)",
            questionBn = "বৃত্তের একই চাপের ওপর দণ্ডায়মান বৃত্তস্থ কোণ ৫৫° হলে কেন্দ্রস্থ কোণের মান কত?",
            optionsBn = listOf("২৭.৫°", "৫৫°", "১১০°", "১২৫°"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "কেন্দ্রস্থ ও বৃত্তস্থ কোণের দ্বিগুণ উপপাদ্য",
            formulaUsedBn = "কেন্দ্রস্থ কোণ = ২ × বৃত্তস্থ কোণ",
            shortcutTechniqueBn = "কেন্দ্রস্থ কোণ = ২ × বৃত্তস্থ কোণ = ২ × ৫৫° = ১১০°।",
            whenToUseShortcutBn = "যখন একই চাপের ওপর বৃত্তস্থ কোণ ও কেন্দ্রস্থ কোণের যেকোনো একটি দেওয়া থাকে।",
            standardStepByStepBn = listOf(
                "বৃত্তের উপপাদ্য অনুযায়ী, একই চাপের ওপর দণ্ডায়মান কেন্দ্রস্থ কোণ বৃত্তস্থ কোণের দ্বিগুণ।",
                "অতএব কেন্দ্রস্থ কোণ = ২ × ৫৫° = ১১০°।"
            ),
            commonMistakeWarningBn = "অর্ধেক করে ২৭.৫° বা সম্পূরক কোণ ১২৫° নেওয়া ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (জ্যামিতি ও পরিমিতি)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u5_venn_union",
            legacyIdAliases = setOf("bcs_prev_45_set_1"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "সেট ও ভেনচিত্র",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৫)",
            questionBn = "যদি n(A) = 20, n(B) = 30 এবং n(A ∩ B) = 10 হয়, তবে n(A ∪ B) এর মান কত?",
            optionsBn = listOf("30", "40", "50", "60"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "ভেনচিত্র সংযোগ সেটের বর্জন-সংযোজন সূত্র",
            formulaUsedBn = "n(A ∪ B) = n(A) + n(B) - n(A ∩ B)",
            shortcutTechniqueBn = "মোট = প্রথম + দ্বিতীয় - উভয় = ২০ + ৩০ - ১০ = ৪০।",
            whenToUseShortcutBn = "যখন দুটি সেটের উপাদান সংখ্যা এবং তাদের সাধারণ (ছেদ) উপাদান সংখ্যা দেওয়া থাকে।",
            standardStepByStepBn = listOf(
                "সেটের সংযোগ সূত্র: n(A ∪ B) = n(A) + n(B) - n(A ∩ B)।",
                "মান বসিয়ে পাই: n(A ∪ B) = ২০ + ৩০ - ১০ = ৪০।"
            ),
            commonMistakeWarningBn = "সাধারণ অংশ ১০ বিয়োগ না করে সরাসরি ২০ + ৩০ = ৫০ লেখা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (সেট ও সম্ভাব্যতা)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u5_set_builder_nat",
            legacyIdAliases = setOf("bcs_ver_45_set_builder"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "সেট ও ভেনচিত্র",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৫)",
            questionBn = "A = {x ∈ ℕ : x² - 5x - 14 = 0} হলে, A নিচের কোনটি?",
            optionsBn = listOf("{-2, 7}", "{2, -7}", "{7}", "{-2}"),
            correctIndex = 2,
            difficulty = PracticeDifficulty.MEDIUM,
            shortcutTitleBn = "স্বাভাবিক সংখ্যার ডোমেইন ফিল্টার কৌশল",
            formulaUsedBn = "ℕ = {1, 2, 3, ...} (শুধু ধনাত্মক পূর্ণসংখ্যা)",
            shortcutTechniqueBn = "x ∈ ℕ মানে শুধু ধনাত্মক পূর্ণসংখ্যা। অপশনগুলোর মধ্যে শুধু {7}-এ ধনাত্মক মান আছে এবং 7² - 5(7) - 14 = 0 সত্য।",
            whenToUseShortcutBn = "যখন সেট গঠন পদ্ধতিতে ডোমেইন x ∈ ℕ দেওয়া থাকে।",
            standardStepByStepBn = listOf(
                "প্রদত্ত সমীকরণ: x² - 5x - 14 = 0।",
                "উৎপাদকে বিশ্লেষণ করে পাই: x² - 7x + 2x - 14 = 0 => (x - 7)(x + 2) = 0।",
                "সুতরাং x = 7 অথবা x = -2।",
                "কিন্তু শর্তানুযায়ী x ∈ ℕ (স্বাভাবিক সংখ্যা), তাই x = -2 গ্রহণযোগ্য নয়। অতএব A = {7}।"
            ),
            commonMistakeWarningBn = "x ∈ ℕ শর্তটি লক্ষ্য না করে উভয় মূল {-2, 7} নির্বাচন করা সবচেয়ে সাধারণ ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (সেট ও সম্ভাব্যতা)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        ),
        BcsMathQuestion(
            id = "bcs_prac_u5_median_even",
            legacyIdAliases = setOf("bcs_prac_stat_mean"),
            unit = BcsSyllabusUnit.UNIT_5_SETS_COMBINATORICS_PROBABILITY,
            topicBn = "পরিসংখ্যান (Statistics)",
            sourceType = BcsQuestionSourceType.SYLLABUS_PRACTICE,
            examReferenceBn = "৫০তম বিসিএস সিলেবাস অনুশীলন মডেল (ইউনিট ৫)",
            questionBn = "৪, ৮, ৬, ১২, ১০, ১৪ সংখ্যাগুলোর মধ্যক (Median) কত?",
            optionsBn = listOf("৮", "৯", "১০", "১১"),
            correctIndex = 1,
            difficulty = PracticeDifficulty.EASY,
            shortcutTitleBn = "জোড় সংখ্যক উপাত্তের মধ্যক কৌশল",
            formulaUsedBn = "মধ্যক = (n/2 তম পদ + (n/2 + 1) তম পদ) ÷ 2",
            shortcutTechniqueBn = "মানের ক্রমে সাজিয়ে মাঝখানের দুটি সংখ্যার গড়: (৮ + ১০)/২ = ৯।",
            whenToUseShortcutBn = "যখন অবিন্যস্ত উপাত্তের মধ্যক নির্ণয় করতে বলা হয়।",
            standardStepByStepBn = listOf(
                "উপাত্তগুলোকে মানের ঊর্ধ্বক্রমে সাজাই: ৪, ৬, ৮, ১০, ১২, ১৪।",
                "মোট পদসংখ্যা n = ৬ (জোড়)।",
                "মধ্যক = ৩য় ও ৪র্থ পদের গড় = (৮ + ১০) / ২ = ৯।"
            ),
            commonMistakeWarningBn = "মানের ক্রমে না সাজিয়ে সরাসরি মাঝের দুটি সংখ্যা (৬ ও ১২) নিয়ে গড় করা ভুল।",
            sourceTitleBn = "৫০তম বিসিএস সিলেবাস অনুশীলন ব্যাংক (পরিসংখ্যান)",
            verificationStatus = BcsVerificationStatus.SYLLABUS_PRACTICE_MODEL
        )
    )

    val excludedUnverifiedQuestions: List<BcsMathQuestion> = emptyList()

    val allBcsQuestions: List<BcsMathQuestion> =
        (verifiedPreviousBcsQuestions + syllabusAlignedPracticeQuestions)
            .filter { it.verificationStatus != BcsVerificationStatus.UNVERIFIED_EXCLUDED }

    fun findQuestionByIdOrAlias(idOrAlias: String): BcsMathQuestion? {
        return allBcsQuestions.firstOrNull { q ->
            q.id == idOrAlias || idOrAlias in q.legacyIdAliases
        }
    }

    fun getQuestionsByUnit(
        unit: BcsSyllabusUnit? = null,
        sourceFilter: BcsQuestionSourceType? = null
    ): List<BcsMathQuestion> {
        return allBcsQuestions.filter { q ->
            (unit == null || q.unit == unit) &&
                (sourceFilter == null || q.sourceType == sourceFilter)
        }
    }

    /**
     * Generates a balanced BCS Preliminary mock or unit practice question set.
     *
     * - Under [BcsSyllabusVersion.BCS_50TH_CURRENT] (default): Full mock has 20 questions / 20 marks
     *   (4 questions from each of the 5 official BPSC units).
     * - Under [BcsSyllabusVersion.BCS_LEGACY_15] (archived): Full mock has 15 questions / 15 marks
     *   (3 questions from each of the 5 units).
     */
    fun generateBcsExamQuestions(
        count: Int = defaultSyllabusVersion.totalMarks,
        difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM,
        unit: BcsSyllabusUnit? = null,
        includePreviousBcs: Boolean = true,
        syllabusVersion: BcsSyllabusVersion = defaultSyllabusVersion
    ): List<QuizQuestion> {
        val sourceFilter = if (includePreviousBcs) null else BcsQuestionSourceType.SYLLABUS_PRACTICE
        val pool = getQuestionsByUnit(unit = unit, sourceFilter = sourceFilter)
            .ifEmpty { allBcsQuestions }

        val selected = if (unit == null && count >= BcsSyllabusUnit.values().size) {
            val basePerUnit = if (count == syllabusVersion.totalMarks) {
                syllabusVersion.marksPerUnit
            } else {
                (count / BcsSyllabusUnit.values().size).coerceAtLeast(1)
            }
            val picked = mutableListOf<BcsMathQuestion>()
            BcsSyllabusUnit.values().forEach { u ->
                val unitPool = pool.filter { it.unit == u }.shuffled()
                picked.addAll(unitPool.take(basePerUnit))
            }
            if (picked.size < count) {
                val usedIds = picked.map { it.id }.toSet()
                picked.addAll(pool.filter { it.id !in usedIds }.shuffled().take(count - picked.size))
            }
            picked.take(count)
        } else {
            pool.shuffled().take(count)
        }

        val finalSelection = if (selected.size < count && pool.isNotEmpty()) {
            val result = selected.toMutableList()
            var idx = 0
            while (result.size < count) {
                val base = pool[idx % pool.size]
                result.add(base.copy(id = "${base.id}_rep_${result.size}"))
                idx++
            }
            result
        } else {
            selected
        }

        return finalSelection.mapIndexed { index, q ->
            val unitMarks = q.unit.marksForVersion(syllabusVersion)
            val stepsJoined = q.standardStepByStepBn.mapIndexed { i, s ->
                "ধাপ ${(i + 1).toBengaliDigits()}: $s"
            }.joinToString("\n")
            QuizQuestion(
                id = "bcs_exam_${index + 1}_${q.id}",
                questionBn = "[${q.examReferenceBn}] ${q.questionBn}",
                optionsBn = q.optionsBn,
                correctIndex = q.correctIndex,
                explanationBn = "$stepsJoined\n\n⚡ বিসিএস শর্টকাট কৌশল (${q.shortcutTitleBn}): ${q.shortcutTechniqueBn}\n📐 সূত্র: ${q.formulaUsedBn}\n📌 কখন প্রযোজ্য: ${q.whenToUseShortcutBn}\n⚠️ সাধারণ ভুল সতর্কতা: ${q.commonMistakeWarningBn}\n📚 সিলেবাস বরাদ্দ: ${q.unit.titleBn} (${unitMarks.toBengaliDigits()} নম্বর • ${syllabusVersion.shortBadgeBn})",
                topicTitleBn = "${q.unit.unitNumberBn}: ${q.unit.titleBn}",
                questionType = QuizQuestionType.MCQ,
                correctAnswerText = q.correctAnswerBn,
                difficulty = difficulty
            )
        }
    }

    fun analyzeBcsWeakUnits(
        topicStats: List<TopicAnalyticsEntity>,
        syllabusVersion: BcsSyllabusVersion = defaultSyllabusVersion
    ): List<BcsWeakUnitAnalysis> {
        return BcsSyllabusUnit.values().map { unit ->
            val matching = topicStats.filter {
                it.topicKey.equals(unit.mappedQuizTopic.name, ignoreCase = true) ||
                    it.topicKey.equals("BCS_${unit.id}", ignoreCase = true) ||
                    it.topicTitleBn.contains(unit.mappedQuizTopic.titleBn)
            }
            val totalAttempted = matching.sumOf { it.totalAttempted }
            val totalCorrect = matching.sumOf { it.totalCorrect }
            val acc = if (totalAttempted > 0) (totalCorrect.toDouble() / totalAttempted.toDouble()) * 100.0 else 0.0
            val isWeak = totalAttempted == 0 || acc < 65.0
            val unitMarks = unit.marksForVersion(syllabusVersion)
            val unitMarksBn = unit.allocatedMarksBn(syllabusVersion)

            val statusBn = when {
                totalAttempted == 0 -> "এখনও অনুশীলন করা হয়নি ($unitMarksBn)"
                acc < 60.0 -> "দুর্বল অধ্যায় (${acc.toInt().toBengaliDigits()}% নির্ভুলতা)"
                acc < 80.0 -> "মাঝারি প্রস্তুতি (${acc.toInt().toBengaliDigits()}% নির্ভুলতা)"
                else -> "শক্তিশালী প্রস্তুতি (${acc.toInt().toBengaliDigits()}% নির্ভুলতা)"
            }

            val recBn = when {
                totalAttempted == 0 -> "${unit.unitNumberBn} ($unitMarksBn)-এর যাচাইকৃত বিগত বিসিএস প্রশ্ন ও শর্টকাট অনুশীলন শুরু করুন।"
                acc < 65.0 -> "${unit.titleBn} অধ্যায়ে পূর্ণ $unitMarksBn নিশ্চিত করতে শর্টকাট কৌশল ও সাধারণ ভুল সতর্কতা রিভিশন দিন।"
                else -> "${unit.titleBn} অধ্যায়ে আপনার প্রস্তুতি ভালো। নিয়মিত ${syllabusVersion.totalMarks.toBengaliDigits()} নম্বরের বিসিএস মক টেস্ট দিয়ে গতি ধরে রাখুন।"
            }

            BcsWeakUnitAnalysis(
                unit = unit,
                officialMarks = unitMarks,
                allocatedMarksBn = unitMarksBn,
                accuracyPercentage = acc,
                isWeak = isWeak,
                statusBn = statusBn,
                recommendationBn = recBn
            )
        }
    }

    /**
     * Generates a complete question set for any of the 14 [BcsPracticeMode] modes or 6 [BcsExamProfile] profiles,
     * seamlessly combining Mathematical Reasoning and Mental Ability questions where appropriate.
     */
    fun generateBcsModeExamQuestions(
        mode: BcsPracticeMode,
        examProfile: BcsExamProfile = BcsExamProfile.BCS_50TH_PRELIM,
        unit: BcsSyllabusUnit? = null,
        difficulty: PracticeDifficulty = PracticeDifficulty.MEDIUM,
        unresolvedMistakeIds: Set<String> = emptySet(),
        weakUnits: Set<BcsSyllabusUnit> = emptySet()
    ): List<QuizQuestion> {
        fun convertMentalToQuiz(q: BcsMentalAbilityQuestion, idx: Int): QuizQuestion {
            return QuizQuestion(
                id = "bcs_ma_exam_${idx + 1}_${q.id}",
                questionBn = "[${q.examReferenceBn} • ${q.category.titleBn}] ${q.questionBn}",
                optionsBn = q.optionsBn,
                correctIndex = q.correctIndex,
                explanationBn = "${q.explanationBn}\n\n⚡ শর্টকাট কৌশল: ${q.shortcutTipBn}\n🏷️ উৎস স্তর: ${q.provenanceTier.badgeBn}",
                topicTitleBn = "মানসিক দক্ষতা: ${q.category.titleBn}",
                questionType = QuizQuestionType.MCQ,
                correctAnswerText = q.correctAnswerBn,
                difficulty = q.difficulty
            )
        }

        return when (mode) {
            BcsPracticeMode.SUBJECT_WISE_MOCK_MENTAL,
            BcsPracticeMode.BCS_WRITTEN_009_PRACTICE -> {
                val targetCount = mode.defaultQuestionCount
                val maPool = BcsCurriculumAndGuideRepository.mentalAbilityQuestions.shuffled()
                val expanded = mutableListOf<BcsMentalAbilityQuestion>()
                var i = 0
                while (expanded.size < targetCount && maPool.isNotEmpty()) {
                    val base = maPool[i % maPool.size]
                    expanded.add(if (i < maPool.size) base else base.copy(id = "${base.id}_rep_$i"))
                    i++
                }
                expanded.mapIndexed { idx, q -> convertMentalToQuiz(q, idx) }
            }

            BcsPracticeMode.BCS_50TH_PRELIM_35_MOCK -> {
                val mathPart = generateBcsExamQuestions(
                    count = 20,
                    difficulty = difficulty,
                    includePreviousBcs = true,
                    syllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT
                )
                val maPool = BcsCurriculumAndGuideRepository.mentalAbilityQuestions.shuffled()
                val maPart = (0 until 15).map { idx ->
                    val base = maPool[idx % maPool.size]
                    convertMentalToQuiz(base, idx)
                }
                mathPart + maPart
            }

            BcsPracticeMode.BCS_51ST_SPECIAL_20_MOCK -> {
                val mathPart = generateBcsExamQuestions(
                    count = 10,
                    difficulty = difficulty,
                    includePreviousBcs = true
                )
                val maPool = BcsCurriculumAndGuideRepository.mentalAbilityQuestions.shuffled()
                val maPart = (0 until 10).map { idx ->
                    val base = maPool[idx % maPool.size]
                    convertMentalToQuiz(base, idx)
                }
                mathPart + maPart
            }

            BcsPracticeMode.COMBINED_MOCK -> {
                val mathCount = if (examProfile.mathMarks > 0 && examProfile.mentalAbilityMarks == 0) 20 else 18
                val mentalCount = if (examProfile.mentalAbilityMarks > 0 && examProfile.mathMarks == 0) 20 else 12
                val mathPart = generateBcsExamQuestions(count = mathCount, difficulty = difficulty)
                val maPool = BcsCurriculumAndGuideRepository.mentalAbilityQuestions.shuffled()
                val maPart = (0 until mentalCount).map { idx ->
                    val base = maPool[idx % maPool.size]
                    convertMentalToQuiz(base, idx)
                }
                mathPart + maPart
            }

            BcsPracticeMode.PREVIOUS_QUESTION_PRACTICE -> {
                val prevPool = verifiedPreviousBcsQuestions.filter { unit == null || it.unit == unit }
                    .ifEmpty { verifiedPreviousBcsQuestions }
                    .shuffled()
                    .take(mode.defaultQuestionCount)
                prevPool.mapIndexed { idx, q ->
                    QuizQuestion(
                        id = "bcs_prev_${idx + 1}_${q.id}",
                        questionBn = "[${q.examReferenceBn}] ${q.questionBn}",
                        optionsBn = q.optionsBn,
                        correctIndex = q.correctIndex,
                        explanationBn = q.standardStepByStepBn.joinToString("\n") + "\n⚡ শর্টকাট: ${q.shortcutTechniqueBn}",
                        topicTitleBn = "${q.unit.unitNumberBn}: ${q.unit.titleBn}",
                        questionType = QuizQuestionType.MCQ,
                        correctAnswerText = q.correctAnswerBn,
                        difficulty = q.difficulty
                    )
                }
            }

            BcsPracticeMode.MISTAKE_PRACTICE -> {
                val matched = allBcsQuestions.filter { q ->
                    unresolvedMistakeIds.any { mId -> mId.endsWith(q.id) || mId == q.id }
                }.ifEmpty { allBcsQuestions.shuffled().take(mode.defaultQuestionCount) }
                matched.take(mode.defaultQuestionCount).mapIndexed { idx, q ->
                    QuizQuestion(
                        id = "bcs_mistake_retry_${idx + 1}_${q.id}",
                        questionBn = "[ভুল সংশোধন • ${q.examReferenceBn}] ${q.questionBn}",
                        optionsBn = q.optionsBn,
                        correctIndex = q.correctIndex,
                        explanationBn = q.standardStepByStepBn.joinToString("\n") + "\n⚠️ সতর্কতা: ${q.commonMistakeWarningBn}",
                        topicTitleBn = "${q.unit.unitNumberBn}: ${q.unit.titleBn}",
                        questionType = QuizQuestionType.MCQ,
                        correctAnswerText = q.correctAnswerBn,
                        difficulty = q.difficulty
                    )
                }
            }

            BcsPracticeMode.WEAK_TOPIC_PRACTICE -> {
                val targetUnit = weakUnits.firstOrNull() ?: unit
                generateBcsExamQuestions(
                    count = mode.defaultQuestionCount,
                    difficulty = difficulty,
                    unit = targetUnit,
                    includePreviousBcs = true
                )
            }

            else -> {
                generateBcsExamQuestions(
                    count = mode.defaultQuestionCount,
                    difficulty = difficulty,
                    unit = unit,
                    includePreviousBcs = true
                )
            }
        }
    }
}
