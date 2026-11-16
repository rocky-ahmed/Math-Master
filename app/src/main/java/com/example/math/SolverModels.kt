package com.example.math

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The 16 mathematical problem-solving categories for Module 04 (Problem Solver).
 */
enum class SolverTopic(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    BASIC_ARITHMETIC(
        id = "basic_arithmetic",
        titleBn = "মৌলিক পাটিগণিত ও সরল (BODMAS)",
        titleEn = "Basic Arithmetic & BODMAS",
        descriptionBn = "যোগ, বিয়োগ, গুণ, ভাগ এবং বন্ধনীযুক্ত জটিল সরল অংকের ধাপভিত্তিক সমাধান",
        primaryColor = Color(0xFF2563EB),
        secondaryColor = Color(0xFFDBEAFE)
    ),
    FRACTIONS_DECIMALS(
        id = "fractions_decimals",
        titleBn = "ভগ্নাংশ ও দশমিক",
        titleEn = "Fractions & Decimals",
        descriptionBn = "সাধারণ ও মিশ্র ভগ্নাংশের যোগ, বিয়োগ, গুণ, ভাগ, সমহর ও দশমিক রূপান্তর",
        primaryColor = Color(0xFF0D9488),
        secondaryColor = Color(0xFFCCFBF1)
    ),
    PERCENTAGE(
        id = "percentage",
        titleBn = "শতকরা (Percentage)",
        titleEn = "Percentage",
        descriptionBn = "শতকরা মান, বৃদ্ধি/হ্রাস, ক্রমাগত শতকরা ও ব্যবহারিক সমস্যা সমাধান",
        primaryColor = Color(0xFF7C3AED),
        secondaryColor = Color(0xFFEDE9FE)
    ),
    RATIO_PROPORTION(
        id = "ratio_proportion",
        titleBn = "অনুপাত ও সমানুপাত",
        titleEn = "Ratio & Proportion",
        descriptionBn = "রাশির আনুপাতিক বণ্টন, সরল অনুপাত, ব্যস্ত অনুপাত ও সমানুপাতী নির্ণয়",
        primaryColor = Color(0xFFEA580C),
        secondaryColor = Color(0xFFFFEDD5)
    ),
    PROFIT_LOSS(
        id = "profit_loss",
        titleBn = "লাভ ও ক্ষতি (Profit & Loss)",
        titleEn = "Profit & Loss",
        descriptionBn = "ক্রয়মূল্য, বিক্রয়মূল্য, শতকরা লাভ/ক্ষতি, ধার্যমূল্য ও ছাড়ের হিসাব",
        primaryColor = Color(0xFF059669),
        secondaryColor = Color(0xFFD1FAE5)
    ),
    AVERAGE(
        id = "average",
        titleBn = "গড় (Average)",
        titleEn = "Average",
        descriptionBn = "একাধিক সংখ্যার গড়, সমষ্টি, গড় পরিবর্তন ও অজানা রাশির মান নির্ণয়",
        primaryColor = Color(0xFF0284C7),
        secondaryColor = Color(0xFFE0F2FE)
    ),
    SIMPLE_INTEREST(
        id = "simple_interest",
        titleBn = "সরল ও চক্রবৃদ্ধি মুনাফা (Interest)",
        titleEn = "Simple & Compound Interest",
        descriptionBn = "সরল ও চক্রবৃদ্ধি মুনাফা, আসল, বার্ষিক হার, সময় ও সবৃদ্ধিমূল নির্ণয়",
        primaryColor = Color(0xFFD97706),
        secondaryColor = Color(0xFFFEF3C7)
    ),
    TIME_AND_WORK(
        id = "time_work",
        titleBn = "সময় ও কাজ (Time & Work)",
        titleEn = "Time & Work",
        descriptionBn = "যৌথ কাজের হিসাব, দৈনিক কার্যক্ষমতা ও চৌবাচ্চা-নল সংক্রান্ত অংক",
        primaryColor = Color(0xFF4F46E5),
        secondaryColor = Color(0xFFE0E7FF)
    ),
    SPEED_DISTANCE_TIME(
        id = "speed_distance_time",
        titleBn = "গতিবেগ, দূরত্ব ও সময়",
        titleEn = "Speed, Distance & Time",
        descriptionBn = "গতি, অতিক্রান্ত দূরত্ব, সময়, km/h থেকে m/s রূপান্তর ও ট্রেন সমস্যা",
        primaryColor = Color(0xFFE11D48),
        secondaryColor = Color(0xFFFFE4E6)
    ),
    NUMBER_SYSTEM_DIVISIBILITY(
        id = "number_divisibility",
        titleBn = "সংখ্যা পদ্ধতি ও বিভাজ্যতা",
        titleEn = "Number System & Divisibility",
        descriptionBn = "২ থেকে ১২ দ্বারা বিভাজ্যতার শর্ত, নিঃশেষে বিভাজ্য যাচাই ও ভাগশেষ নির্ণয়",
        primaryColor = Color(0xFF0891B2),
        secondaryColor = Color(0xFFCFFAFE)
    ),
    HCF_LCM(
        id = "hcf_lcm",
        titleBn = "গ.সা.গু ও ল.সা.গু (HCF & LCM)",
        titleEn = "HCF & LCM",
        descriptionBn = "মৌলিক উৎপাদক ও ইউক্লিডীয় প্রক্রিয়ায় গ.সা.গু ও ল.সা.গু নির্ণয় ও সম্পর্ক",
        primaryColor = Color(0xFF9333EA),
        secondaryColor = Color(0xFFF3E8FF)
    ),
    SQUARES_CUBES_ROOTS(
        id = "squares_cubes",
        titleBn = "বর্গ, বর্গমূল, ঘন ও ঘনমূল",
        titleEn = "Squares, Cubes & Roots",
        descriptionBn = "বর্গ, ঘন, মৌলিক উৎপাদকে ও ভাগ প্রক্রিয়ায় বর্গমূল ও ঘনমূলের নিখুঁত মান",
        primaryColor = Color(0xFFB45309),
        secondaryColor = Color(0xFFFDE68A)
    ),
    LINEAR_EQUATIONS(
        id = "linear_equations",
        titleBn = "সরল সমীকরণ ও অসমতা",
        titleEn = "Linear Equations, Inequalities & Systems",
        descriptionBn = "এক চলক সমীকরণ, পরমমান অসমতা, ২×২ ও ৩×৩ সহসমীকরণ এবং শুদ্ধি পরীক্ষা",
        primaryColor = Color(0xFFBE185D),
        secondaryColor = Color(0xFFFCE7F3)
    ),
    QUADRATIC_EQUATIONS(
        id = "quadratic_equations",
        titleBn = "দ্বিঘাত সমীকরণ ও অসমতা (ax² + bx + c)",
        titleEn = "Quadratic Equations & Inequalities",
        descriptionBn = "নিশ্চায়ক (D = b² - 4ac) বিশ্লেষণ, মূলের প্রকৃতি, দ্বিঘাত সূত্র, শীর্ষবিন্দু ও অসমতা",
        primaryColor = Color(0xFF6D28D9),
        secondaryColor = Color(0xFFEDE9FE)
    ),
    FACTORIZATION(
        id = "factorization",
        titleBn = "উৎপাদকে বিশ্লেষণ (Factorization)",
        titleEn = "Prime & Algebraic Factorization",
        descriptionBn = "সংখ্যার মৌলিক উৎপাদকে বিশ্লেষণ, মিডল-টার্ম ব্রেকআপ এবং বর্গান্তরের উৎপাদক",
        primaryColor = Color(0xFF047857),
        secondaryColor = Color(0xFFD1FAE5)
    ),
    SIMPLIFICATION(
        id = "simplification",
        titleBn = "বীজগণিতীয় ও করণী সরলীকরণ",
        titleEn = "Expression & Radical Simplification",
        descriptionBn = "বর্গমূল করণী (√n), ভগ্নাংশ লঘিষ্ঠকরণ এবং সদৃশ বীজগণিতীয় পদের সরলীকরণ",
        primaryColor = Color(0xFF0369A1),
        secondaryColor = Color(0xFFE0F2FE)
    );

    fun getIcon(): ImageVector = when (this) {
        BASIC_ARITHMETIC -> Icons.Default.Calculate
        FRACTIONS_DECIMALS -> Icons.Default.PieChart
        PERCENTAGE -> Icons.Default.Percent
        RATIO_PROPORTION -> Icons.Default.SwapHoriz
        PROFIT_LOSS -> Icons.Default.MonetizationOn
        AVERAGE -> Icons.Default.Equalizer
        SIMPLE_INTEREST -> Icons.AutoMirrored.Filled.TrendingUp
        TIME_AND_WORK -> Icons.Default.Work
        SPEED_DISTANCE_TIME -> Icons.Default.Speed
        NUMBER_SYSTEM_DIVISIBILITY -> Icons.Default.Numbers
        HCF_LCM -> Icons.Default.ViewInAr
        SQUARES_CUBES_ROOTS -> Icons.Default.Straighten
        LINEAR_EQUATIONS -> Icons.Default.Functions
        QUADRATIC_EQUATIONS -> Icons.Default.Timeline
        FACTORIZATION -> Icons.Default.AccountTree
        SIMPLIFICATION -> Icons.Default.AutoFixHigh
    }
}

/**
 * Three distinct educational learning modes.
 */
enum class SolverMode(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String
) {
    LEARNING(
        id = "learning",
        titleBn = "লার্নিং মোড",
        titleEn = "Learning Mode",
        descriptionBn = "প্রতিটি হিসাবের বিশদ বাংলা ব্যাখ্যা, কেন এই ধাপ করা হলো এবং শুদ্ধি পরীক্ষা"
    ),
    QUICK(
        id = "quick",
        titleBn = "কুইক সলিউশন",
        titleEn = "Quick Solution",
        descriptionBn = "সংক্ষিপ্ত হিসাবের ধাপ ও দ্রুত ফলাফল সরাসরি দেখার মোড"
    ),
    BCS(
        id = "bcs",
        titleBn = "বিসিএস প্রস্তুতি",
        titleEn = "BCS Practice",
        descriptionBn = "বিসিএস ও চাকরির পরীক্ষার শর্টকাট টেকনিক, সময় সাশ্রয়ী কৌশল ও গুরুত্বপূর্ণ টিপস"
    )
}

/**
 * Explicit exactness classification for mathematical outputs.
 */
enum class CalculationExactness(val badgeBn: String, val descriptionBn: String) {
    EXACT("নির্ভুল মান (Exact)", "পূর্ণসংখ্যা বা মূলদ ভগ্নাংশে শতভাগ নিখুঁত গাণিতিক মান"),
    APPROXIMATE("আসন্ন মান (Approximate)", "দশমিক বা অমূলদ সংখ্যার আসন্ন মান (প্রকৃত প্রতীকী মানসহ)")
}

/**
 * Verified 5-Category Provenance Taxonomy for BCS & curriculum content.
 * Prevents unverified exam-year claims.
 */
enum class BcsProvenanceCategory(
    val badgeBn: String,
    val detailBn: String
) {
    OFFICIAL_BPSC_SYLLABUS(
        badgeBn = "সরকারি BPSC সিলেবাস টপিক",
        detailBn = "বাংলাদেশ সরকারি কর্ম কমিশন (BPSC) প্রিলিমিনারি ও লিখিত পরীক্ষার নির্ধারিত পাঠ্যসূচিভুক্ত"
    ),
    CURATED_BCS_PATTERN(
        badgeBn = "বিসিএস প্রশ্ন-ধাঁচ ভিত্তিক (Curated Pattern)",
        detailBn = "বিগত বিসিএস প্রিলিমিনারি ও লিখিত পরীক্ষার গাণিতিক যুক্তির প্রশ্ন-প্যাটার্ন অনুসারে প্রস্তুত"
    ),
    ADAPTED_PRACTICE(
        badgeBn = "অনুশীলনের জন্য রূপান্তরিত (Adapted Practice)",
        detailBn = "প্রতিযোগিতামূলক পরীক্ষার সমমানের সংখ্যাগত অনুশীলনের জন্য রূপান্তরিত সমস্যা"
    ),
    NCTB_CURRICULUM_CONCEPT(
        badgeBn = "NCTB বোর্ড পাঠ্যক্রম ভিত্তি",
        detailBn = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (৬ষ্ঠ–১০ম শ্রেণি) গণিত বইয়ের মৌলিক ধারণা"
    ),
    SPEED_CALCULATION_TECHNIQUE(
        badgeBn = "দ্রুত গণনা কৌশল (Speed Technique)",
        detailBn = "এমসিকিউ পরীক্ষায় ৩০–৪৫ সেকেন্ডে উত্তর যাচাইয়ের বীজগণিতীয় সংক্ষিপ্ত পদ্ধতি"
    )
}

/**
 * Four-tier educational progress taxonomy separating self-reported confidence from verified mastery.
 */
enum class SolverLearningState(
    val id: String,
    val badgeBn: String,
    val descriptionBn: String
) {
    SELF_REPORTED_CONFIDENCE(
        id = "self_reported_confidence",
        badgeBn = "স্ব-মূল্যায়িত আস্থা (Self-Reported Confidence)",
        descriptionBn = "শিক্ষার্থী ধাপগুলো পড়ে 'বুঝেছি' হিসেবে চিহ্নিত করেছেন"
    ),
    WORKED_EXAMPLE_STUDIED(
        id = "worked_example_studied",
        badgeBn = "সমাধানকৃত উদাহরণ অধ্যয়ন (Worked Example Studied)",
        descriptionBn = "ধাপভিত্তিক সমাধান, সূত্র ও শুদ্ধি পরীক্ষা সম্পূর্ণ পর্যবেক্ষণ করা হয়েছে"
    ),
    INDEPENDENT_PRACTICE_VERIFIED(
        id = "independent_practice_verified",
        badgeBn = "স্বতন্ত্র অনুশীলন যাচাইকৃত (Independent Practice Verified)",
        descriptionBn = "অনুরূপ অনুশীলন প্রশ্নের সঠিক উত্তর নিজে সমাধান করে যাচাই করেছেন"
    ),
    DEMONSTRATED_TOPIC_MASTERY(
        id = "demonstrated_topic_mastery",
        badgeBn = "প্রমাণিত বিষয়-দক্ষতা (Demonstrated Topic Mastery)",
        descriptionBn = "একাধিক স্তরের (সহজ, মধ্যম, চ্যালেঞ্জ) অনুশীলন সফলভাবে সম্পন্ন করেছেন"
    )
}

/**
 * Interest sub-mode for Module 04 Topic 07 (Simple vs. Compound Interest).
 */
enum class InterestCalculationMode(
    val id: String,
    val titleBn: String,
    val subtitleBn: String
) {
    SIMPLE("simple", "সরল মুনাফা (I = Prn)", "প্রতি বছর শুধুমাত্র প্রারম্ভিক আসলের ওপর মুনাফা"),
    COMPOUND("compound", "চক্রবৃদ্ধি মুনাফা (A = P(1+r/m)^mt)", "প্রতি পর্ব শেষে মুনাফা আসলের সাথে যুক্ত হয়ে নতুন আসল গঠন")
}

/**
 * Inline interactive visual model types for Learning Mode.
 */
enum class VisualDiagramType {
    NUMBER_LINE,
    INEQUALITY_NUMBER_LINE,
    FRACTION_BAR,
    PERCENTAGE_BAR,
    RATIO_SEGMENTS,
    PROFIT_LOSS_BAR,
    INTEREST_GROWTH_BAR,
    WORK_RATE_BAR,
    MOTION_TRACK,
    EUCLIDEAN_STRIP,
    FACTOR_VENN,
    SQUARE_GRID,
    ALGEBRA_BALANCE,
    PARABOLA_PLOT,
    FACTOR_AREA_GRID
}

/**
 * Specification for an inline expandable visual representation in Learning Mode.
 */
data class VisualModelSpec(
    val diagramType: VisualDiagramType,
    val titleBn: String,
    val subtitleBn: String = "",
    val primaryValues: List<Double> = emptyList(),
    val secondaryValues: List<Double> = emptyList(),
    val labelsBn: List<String> = emptyList(),
    val equationLeftBn: String? = null,
    val equationRightBn: String? = null,
    val accessibilitySummaryBn: String
)

/**
 * Section 1 & 2 of Learning Mode: Plain-Bengali problem understanding & prerequisite concept.
 */
data class ProblemUnderstanding(
    val summaryBn: String,
    val givenItemsBn: List<Pair<String, String>>,
    val requiredTargetBn: String,
    val prerequisiteConceptBn: String,
    val realLifeContextBn: String? = null
)

/**
 * Individual variable definition inside a formula breakdown card.
 */
data class VariableExplanation(
    val symbolBn: String,
    val meaningBn: String,
    val givenValueBn: String? = null
)

/**
 * Section 3 of Learning Mode: Formula breakdown with variable dictionary.
 */
data class FormulaBreakdown(
    val formulaTitleBn: String,
    val formulaExpressionBn: String,
    val plainMeaningBn: String,
    val variables: List<VariableExplanation> = emptyList(),
    val whyApplicableBn: String
)

/**
 * Section 6 of Learning Mode & BCS Mode: Shortcut, derivation, and common traps.
 */
data class ShortcutDetails(
    val shortcutTitleBn: String,
    val shortcutFormulaOrRuleBn: String,
    val whyShortcutWorksBn: String,
    val whenToUseBn: String,
    val whenToAvoidBn: String,
    val commonTrapBn: String,
    val averageTimeSecondsBn: String = "২০–৩০ সেকেন্ড"
)

/**
 * Official BPSC Preliminary (15+15) & Written (50+50) syllabus mapping.
 */
data class BcsSyllabusMapping(
    val preliminarySectionBn: String,
    val preliminaryMarksBn: String = "১৫ নম্বর (গাণিতিক যুক্তি) + ১৫ নম্বর (মানসিক দক্ষতা)",
    val writtenSectionBn: String,
    val writtenMarksBn: String = "৫০ নম্বর (গাণিতিক যুক্তি, কোড 008) + ৫০ নম্বর (মানসিক দক্ষতা, কোড 009)",
    val provenanceCategory: BcsProvenanceCategory = BcsProvenanceCategory.OFFICIAL_BPSC_SYLLABUS,
    val provenanceLabelBn: String = provenanceCategory.badgeBn,
    val examPatternNoteBn: String
)

/**
 * Represents an individual sequential step within a solution.
 * Preserves 100% backward compatibility with the original 5-parameter constructor.
 */
data class SolutionStep(
    val stepIndex: Int,
    val titleBn: String,
    val expressionOrMathBn: String,
    val explanationBn: String,
    val whyThisStepBn: String? = null,
    val expressionBeforeBn: String? = null,
    val expressionAfterBn: String? = null,
    val simpleExplanationBn: String = explanationBn,
    val detailedRuleBn: String? = whyThisStepBn,
    val conceptualAnalogyBn: String? = null,
    val commonMistakeBn: String? = null,
    val hint1Bn: String? = null,
    val hint2Bn: String? = null,
    val expectedStepValueBn: String? = null
)

/**
 * Complete result output of the Math Problem Solver.
 * Preserves 100% backward compatibility with existing callers and unit tests.
 */
data class SolverResult(
    val topic: SolverTopic,
    val mode: SolverMode,
    val originalProblemBn: String,
    val formulaUsedBn: String? = null,
    val steps: List<SolutionStep> = emptyList(),
    val finalAnswerBn: String,
    val verificationBn: String? = null,
    val bcsShortcutBn: String? = null,
    val bcsExamTipBn: String? = null,
    val similarProblemPromptBn: String? = null,
    val isSuccess: Boolean = true,
    val errorMessageBn: String? = null,
    val isExactResult: Boolean = true,
    val approximateResultBn: String? = null,
    val problemUnderstanding: ProblemUnderstanding? = null,
    val formulaBreakdown: FormulaBreakdown? = null,
    val shortcutDetails: ShortcutDetails? = null,
    val bcsSyllabusMapping: BcsSyllabusMapping? = null,
    val visualModelSpec: VisualModelSpec? = null,
    val exactness: CalculationExactness = if (isExactResult) CalculationExactness.EXACT else CalculationExactness.APPROXIMATE
)

/**
 * Sample / Curated problem model for user quick-start chips.
 */
data class CuratedProblemExample(
    val labelBn: String,
    val topic: SolverTopic,
    val problemDescriptionBn: String,
    val defaultInputs: Map<String, String>
)

/**
 * Cross-module syllabus navigation item for BCS Preparation Mode covering
 * Geometry, Mensuration, Trigonometry, Coordinate Geometry, Sets, Combinatorics & Probability.
 */
data class BcsCompanionModuleItem(
    val syllabusAreaBn: String,
    val prelimAndWrittenWeightBn: String,
    val topicsCoveredBn: String,
    val targetModuleNameBn: String,
    val targetRouteType: String
)
