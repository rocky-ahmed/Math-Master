package com.example.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

// ============================================================================
// 1. NINE-STAGE BENGALI PEDAGOGICAL FLOW ENGINE (INTRODUCE -> RECAP)
// ============================================================================

enum class LearningStageType(
    val stageNumber: Int,
    val titleBn: String,
    val subtitleBn: String,
    val badgeEn: String
) {
    INTRODUCE(1, "১. ভূমিকা", "বাস্তব জীবনের সংযোগ ও মূল প্রশ্ন", "Introduce"),
    VISUALIZE(2, "২. দৃশ্যায়ন", "ইন্টারেক্টিভ ২ডি/৩ডি মডেলে পর্যবেক্ষণ", "Visualize"),
    EXPLORE(3, "৩. অনুসন্ধান", "স্লাইডার ও প্যারামিটার পরিবর্তন করে পরীক্ষা", "Explore"),
    EXPLAIN(4, "৪. কেন এমন হয়?", "জ্যামিতিক ও গাণিতিক যুক্তির ব্যাখ্যা", "Explain"),
    FORMULA(5, "৫. গাণিতিক সূত্র", "চলক ও এককসহ পূর্ণাঙ্গ সূত্র", "Formula"),
    WORKED_EXAMPLE(6, "৬. সমাধানকৃত উদাহরণ", "ধাপে ধাপে বাস্তব মানের হিসাব", "Example"),
    PRACTICE(7, "৭. নিজে অনুশীলন করুন", "লাইভ ইনপুট ও তাৎক্ষণিক যাচাই", "Practice"),
    KNOWLEDGE_CHECK(8, "৮. ধারণা যাচাই", "বহুনির্বাচনী প্রশ্ন ও ভুল বিশ্লেষণ", "Check"),
    RECAP(9, "৯. সারসংক্ষেপ", "মূল শিক্ষা ও পরবর্তী ধাপ", "Recap")
}

data class StagePracticeChallenge(
    val promptBn: String,
    val expectedValue: Double,
    val tolerance: Double = 0.25,
    val unitBn: String,
    val hintBn: String,
    val solutionExplanationBn: String
)

data class LearningStageContent(
    val stage: LearningStageType,
    val headlineBn: String,
    val bodyBn: String,
    val keyPointsBn: List<String>,
    val interactiveActionHintBn: String,
    val recommendedPresetLabelBn: String? = null
)

data class StageKnowledgeCheckQuestion(
    val id: String,
    val questionBn: String,
    val optionsBn: List<String>,
    val correctIndex: Int,
    val explanationBn: String
)

data class TopicNineStageGuide(
    val topicId: String,
    val topicTitleBn: String,
    val stages: List<LearningStageContent>,
    val practiceChallenge: StagePracticeChallenge,
    val knowledgeCheckQuestion: StageKnowledgeCheckQuestion,
    val recapTakeawaysBn: List<String>
)

// ============================================================================
// 2. TRIGONOMETRY UNIT CIRCLE & HEIGHT-DISTANCE ENGINE
// ============================================================================

data class TrigUnitCircleState(
    val angleDegrees: Double,
    val angleRadians: Double,
    val sinValue: Double,
    val cosValue: Double,
    val tanValue: Double?,
    val quadrantNumber: Int,
    val quadrantNameBn: String,
    val referenceAngleDeg: Double,
    val pythagoreanIdentitySum: Double,
    val signSummaryBn: String,
    val stepsBn: List<String>
)

data class TrigElevationAnalysis(
    val angleDegrees: Double,
    val baseDistance: Double,
    val oppositeHeight: Double,
    val hypotenuseLength: Double,
    val explanationBn: String
)

// ============================================================================
// 3. 3D COORDINATE & VECTOR STUDIO ENGINE
// ============================================================================

data class Point3DCoord(
    val x: Double,
    val y: Double,
    val z: Double
)

data class Coord3DAnalysis(
    val pointA: Point3DCoord,
    val pointB: Point3DCoord,
    val deltaX: Double,
    val deltaY: Double,
    val deltaZ: Double,
    val distance3D: Double,
    val xyProjectionDistance: Double,
    val midpoint: Point3DCoord,
    val vectorMagnitudeAB: Double,
    val directionCosines: Triple<Double, Double, Double>,
    val stepsBn: List<String>
)

// ============================================================================
// 4. 2D FUNCTION & 3D SURFACE (z = f(x, y)) ENGINE
// ============================================================================

enum class Surface3DPreset(
    val id: String,
    val titleBn: String,
    val formulaDisplay: String,
    val descriptionBn: String
) {
    PARABOLOID(
        id = "paraboloid",
        titleBn = "উপবৃত্তাকার প্যারাবোলয়েড (বাটি আকৃতি)",
        formulaDisplay = "z = a(x² + y²) / 4",
        descriptionBn = "স্যাটেলাইট ডিশ ও প্রতিফলক টেলিস্কোপের ৩ডি বক্রতল যেখানে একটি সর্বনিম্ন বিন্দু (Global Minimum) থাকে।"
    ),
    SADDLE_HYPERBOLIC(
        id = "saddle",
        titleBn = "হাইপারবোলিক প্যারাবোলয়েড (স্যাডল তল)",
        formulaDisplay = "z = a(x² - y²) / 4",
        descriptionBn = "এক্স-অক্ষ বরাবর ঊর্ধ্বমুখী এবং ওয়াই-অক্ষ বরাবর নিম্নমুখী বক্রতল; মূলবিন্দুতে স্যাডল বিন্দু (Saddle Point) তৈরি করে।"
    ),
    RADIAL_WAVE(
        id = "radial_wave",
        titleBn = "ত্রিমাত্রিক তরঙ্গ তল (3D Ripple)",
        formulaDisplay = "z = a · cos(b · √(x² + y²))",
        descriptionBn = "পানিতে ঢিল ফেললে কেন্দ্র থেকে চারদিকে যে সমকেন্দ্রিক ত্রিমাত্রিক তরঙ্গ ছড়িয়ে পড়ে তার গাণিতিক রূপ।"
    ),
    INCLINED_PLANE(
        id = "inclined_plane",
        titleBn = "ত্রিমাত্রিক সমতল (Linear Plane)",
        formulaDisplay = "z = (a·x + b·y) / 3",
        descriptionBn = "দুই চলকের রৈখিক ফাংশন যা ৩ডি স্থানাঙ্ক ব্যবস্থায় একটি ঢালু সমতল গঠন করে।"
    )
}

data class Quadratic2DAnalysis(
    val a: Double,
    val b: Double,
    val c: Double,
    val vertexX: Double,
    val vertexY: Double,
    val discriminant: Double,
    val realRoots: List<Double>,
    val yIntercept: Double,
    val opensUpward: Boolean,
    val formulaTextBn: String,
    val stepsBn: List<String>
)

data class Surface3DMeshAnalysis(
    val preset: Surface3DPreset,
    val paramA: Double,
    val paramB: Double,
    val zAtOrigin: Double,
    val minZ: Double,
    val maxZ: Double,
    val faces: List<Face3D>,
    val lines: List<Line3D>,
    val explanationBn: String
)

// ============================================================================
// 5. ALGEBRA TILES & FRACTION OPERATIONS ENGINE
// ============================================================================

data class AlgebraTilesAnalysis(
    val xSquaredCount: Int,
    val xPositiveCount: Int,
    val xNegativeCount: Int,
    val unitPositiveCount: Int,
    val unitNegativeCount: Int,
    val netXCount: Int,
    val netUnitCount: Int,
    val zeroPairsX: Int,
    val zeroPairsUnit: Int,
    val simplifiedExpressionBn: String,
    val factorizedFormBn: String?,
    val evaluatedValueAtX: Double,
    val xValue: Int,
    val stepsBn: List<String>
)

enum class FractionOpType(
    val symbol: String,
    val titleBn: String
) {
    ADD("+", "যোগ (+)"),
    SUBTRACT("−", "বিয়োগ (−)"),
    MULTIPLY("×", "গুণ (×)"),
    DIVIDE("÷", "ভাগ (÷)")
}

data class FractionOperationAnalysis(
    val num1: Int,
    val den1: Int,
    val num2: Int,
    val den2: Int,
    val op: FractionOpType,
    val lcmDenominator: Int,
    val convertedNum1: Int,
    val convertedNum2: Int,
    val rawResultNum: Int,
    val rawResultDen: Int,
    val simplifiedNum: Int,
    val simplifiedDen: Int,
    val mixedWhole: Int,
    val mixedRemNum: Int,
    val decimalValue: Double,
    val stepsBn: List<String>
)

object VisualMathStudioEngine {

    private fun fmtBn(value: Double, decimals: Int = 2): String {
        val safe = if (value.isFinite()) value else 0.0
        val factor = 10.0.pow(decimals)
        val rounded = (safe * factor).roundToInt() / factor
        val text = if (abs(rounded - rounded.roundToInt()) < 1e-6) {
            rounded.roundToInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.${decimals}f", rounded)
                .trimEnd('0')
                .trimEnd('.')
        }
        return BengaliNumberFormatter.toBengaliDigits(text)
    }

    private fun fmtIntBn(value: Int): String = BengaliNumberFormatter.toBengaliDigits(value)

    // ------------------------------------------------------------------------
    // 1. NINE-STAGE BENGALI GUIDE FOR 3D SOLIDS & 2D LABS
    // ------------------------------------------------------------------------

    fun buildNineStageGuideForSolid(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double,
        p3: Double
    ): TopicNineStageGuide {
        val metrics = Math3DEngine.calculateSolidMetrics(shape, p1, p2, p3)
        val sliceInfo = Math3DEngine.calculateCrossSection(
            shape = shape,
            p1 = p1,
            p2 = p2,
            p3 = p3,
            sliceMode = CrossSectionSliceMode.HORIZONTAL,
            slicePosition = 0.5f
        )
        val formulaSummary = "${metrics.volumeFormulaBn} | ${metrics.surfaceAreaFormulaBn}"
        val baseAreaApprox = (metrics.totalSurfaceArea - metrics.lateralSurfaceArea).coerceAtLeast(0.0)

        val stages = listOf(
            LearningStageContent(
                stage = LearningStageType.INTRODUCE,
                headlineBn = "${shape.titleBn} — বাস্তব জীবনে কোথায় দেখা যায়?",
                bodyBn = "${shape.subtitleBn}। দৈনন্দিন স্থাপত্য, প্রকৌশল, পানির ট্যাংক ও পাত্রের ধারণক্ষমতা নির্ণয়ে ${shape.titleBn}-এর পৃষ্ঠতল ও আয়তন জানা অপরিহার্য।",
                keyPointsBn = listOf(
                    "বস্তুটির তল (F=${fmtIntBn(metrics.facesCount)}), ধার (E=${fmtIntBn(metrics.edgesCount)}) ও শীর্ষবিন্দু (V=${fmtIntBn(metrics.verticesCount)}) কীভাবে ত্রিমাত্রিক স্থান দখল করে তা চেনা।",
                    "সমগ্র তলের ক্ষেত্রফল (রঙ বা মোড়ানোর খরচ) এবং আয়তন (ধারণক্ষমতা)-এর পার্থক্য বোঝা।"
                ),
                interactiveActionHintBn = "৩ডি ভিউপোর্টে আঙুল দিয়ে ঘুরিয়ে বস্তুটির সবগুলো তল লক্ষ্য করুন।",
                recommendedPresetLabelBn = "আইসোমেট্রিক ভিউ"
            ),
            LearningStageContent(
                stage = LearningStageType.VISUALIZE,
                headlineBn = "ত্রিমাত্রিক গঠন ও জাল (Net) দৃশ্যায়ন",
                bodyBn = if (shape.supportsNetUnfold) {
                    "${shape.titleBn}-কে কব্জা (Hinge) বরাবর খুললে এটি কীভাবে একটি দ্বিমাত্রিক সমতল জাল (2D Net)-এ পরিণত হয় তা দেখুন: ${metrics.netExplanationBn}"
                } else {
                    "${shape.titleBn}-এর গাউসীয় বক্রতা অশূন্য হওয়ায় এটি ভাঁজ বা প্রসারণ ছাড়া সমতল কাগজে সম্পূর্ণ খোলা যায় না (Theorema Egregium)।"
                },
                keyPointsBn = listOf(
                    "সমগ্র তলের ক্ষেত্রফল = ${fmtBn(metrics.totalSurfaceArea)} বর্গ একক",
                    "পার্শ্বতল / বক্রতলের ক্ষেত্রফল = ${fmtBn(metrics.lateralSurfaceArea)} বর্গ একক",
                    "ভূমি / প্রান্ত তলের মোট ক্ষেত্রফল = ${fmtBn(baseAreaApprox)} বর্গ একক"
                ),
                interactiveActionHintBn = "নেট আনফোল্ড স্লাইডার ০% থেকে ১০০% পর্যন্ত টেনে তলগুলোর অবস্থান দেখুন।",
                recommendedPresetLabelBn = "নেট আনফোল্ড ৫০%"
            ),
            LearningStageContent(
                stage = LearningStageType.EXPLORE,
                headlineBn = "মাপ পরিবর্তন, আয়তন পূর্ণতা ও প্রস্থচ্ছেদ পরীক্ষা",
                bodyBn = "মাত্রা পরিবর্তন করলে আয়তন রৈখিকভাবে নয়, ঘনমাত্রায় (Cubic scale) পরিবর্তিত হয়। এছাড়া তল বরাবর কাটলে কী প্রস্থচ্ছেদ (${sliceInfo.sectionShapeNameBn}) পাওয়া যায় তা পরীক্ষা করুন।",
                keyPointsBn = listOf(
                    "বর্তমান আয়তন V = ${fmtBn(metrics.volume)} ঘন একক",
                    "মধ্যবিন্দুতে অনুভূমিক প্রস্থচ্ছেদ: ${sliceInfo.sectionShapeNameBn} (ক্ষেত্রফল ≈ ${fmtBn(sliceInfo.sectionArea)} বর্গ একক)"
                ),
                interactiveActionHintBn = "ভলিউম ফিল ও ক্রস-সেকশন স্লাইডার ব্যবহার করে উচ্চতার সাথে আয়তন ও ছেদতলের পরিবর্তন দেখুন।",
                recommendedPresetLabelBn = "ক্রস-সেকশন ও ভলিউম ল্যাব"
            ),
            LearningStageContent(
                stage = LearningStageType.EXPLAIN,
                headlineBn = "কেন এই সূত্রটি কাজ করে? (জ্যামিতিক যুক্তি)",
                bodyBn = when (shape) {
                    Solid3DShapeType.CUBE, Solid3DShapeType.CUBOID, Solid3DShapeType.TRIANGULAR_PRISM, Solid3DShapeType.HEXAGONAL_PRISM, Solid3DShapeType.CYLINDER ->
                        "যেসব ঘনবস্তুর নিচ থেকে ওপর পর্যন্ত প্রস্থচ্ছেদ সর্বত্র সমান (সুষম প্রিজম ও সিলিন্ডার), তাদের আয়তন হলো ভূমির ক্ষেত্রফল × উচ্চতা (Cavalieri's Principle)।"
                    Solid3DShapeType.CONE, Solid3DShapeType.SQUARE_PYRAMID ->
                        "একই ভূমি ও একই উচ্চতাবিশিষ্ট প্রিজম বা সিলিন্ডারের তুলনায় শীর্ষবিন্দুতে মিলিত পিরামিড বা শঙ্কুর প্রস্থচ্ছেদ উচ্চতার বর্গের অনুপাতে কমে, ফলে আয়তন ঠিক ১/৩ অংশ হয়।"
                    Solid3DShapeType.SPHERE, Solid3DShapeType.HEMISPHERE ->
                        "আর্কিমিডিসের প্রমাণ অনুসারে, r ব্যাসার্ধের অর্ধগোলকের যেকোনো উচ্চতায় অনুভূমিক প্রস্থচ্ছেদের ক্ষেত্রফল হলো π(r² - y²), যা সমমাপের সিলিন্ডার থেকে শঙ্কু বাদ দিলে প্রাপ্ত ক্ষেত্রফলের সমান।"
                    Solid3DShapeType.FRUSTUM ->
                        "একটি পূর্ণ শঙ্কু থেকে উপরের ছোট শঙ্কুটি সমান্তরাল তলে কেটে বাদ দিলে ফ্রাস্টাম (ছিন্ন শঙ্কু) তৈরি হয়; তাই এর আয়তনে R², Rr এবং r²—এই তিনটি পদই থাকে।"
                },
                keyPointsBn = metrics.calculationStepsBn,
                interactiveActionHintBn = "কর্ণ / অক্ষ ও উচ্চতা হাইলাইট চালু করে অভ্যন্তরীণ সমকোণী ত্রিভুজ ও ব্যাসার্ধের সম্পর্কটি লক্ষ্য করুন।"
            ),
            LearningStageContent(
                stage = LearningStageType.FORMULA,
                headlineBn = "${shape.titleBn}-এর প্রমিত গাণিতিক সূত্রাবলি",
                bodyBn = formulaSummary,
                keyPointsBn = listOf(
                    metrics.volumeFormulaBn,
                    metrics.surfaceAreaFormulaBn,
                    "${metrics.lateralAreaFormulaBn} | ${metrics.secondaryMetricLabelBn} = ${fmtBn(metrics.secondaryMetricValue)}"
                ),
                interactiveActionHintBn = "সূত্রের প্রতিটি প্রতীকের সাথে ৩ডি মডেলের রঙিন রেখাগুলো মিলিয়ে নিন।"
            ),
            LearningStageContent(
                stage = LearningStageType.WORKED_EXAMPLE,
                headlineBn = "সমাধানকৃত উদাহরণ (বর্তমান মাপ অনুযায়ী)",
                bodyBn = "আপনার নির্বাচিত মাপ (${fmtBn(p1)}, ${fmtBn(p2)}, ${fmtBn(p3)}) বসিয়ে ধাপে ধাপে হিসাব:",
                keyPointsBn = metrics.calculationStepsBn,
                interactiveActionHintBn = "স্লাইডার পরিবর্তন করলে এই সমাধানটি তাৎক্ষণিকভাবে নতুন মানের জন্য আপডেট হবে।"
            ),
            LearningStageContent(
                stage = LearningStageType.PRACTICE,
                headlineBn = "নিজে অনুশীলন করুন (হাতে-কলমে হিসাব)",
                bodyBn = "নিচের প্রশ্নটির উত্তর নিজে হিসাব করে ইনপুট বক্সে লিখুন এবং যাচাই করুন:",
                keyPointsBn = listOf(
                    "বর্তমান বস্তুর আয়তন নির্ণয় করুন।",
                    "দশমিকের পর দুই ঘর পর্যন্ত আসন্ন মান গ্রহণযোগ্য।"
                ),
                interactiveActionHintBn = "নিচের ইনপুট ঘরে সংখ্যাটি লিখে 'উত্তর যাচাই' বোতামে চাপুন।"
            ),
            LearningStageContent(
                stage = LearningStageType.KNOWLEDGE_CHECK,
                headlineBn = "ধারণা যাচাই (বহুনির্বাচনী প্রশ্ন)",
                bodyBn = "সূত্র মুখস্থের বদলে ধারণাটি কতটুকু স্পষ্ট হয়েছে তা যাচাই করুন।",
                keyPointsBn = listOf(
                    "সঠিক উত্তর দিলে আপনার ভিজ্যুয়াল ম্যাথ প্রগ্রেস চেকপয়েন্ট সংরক্ষিত হবে।"
                ),
                interactiveActionHintBn = "নিচের অপশনগুলো থেকে সঠিক উত্তরটি বেছে নিন।"
            ),
            LearningStageContent(
                stage = LearningStageType.RECAP,
                headlineBn = "সারসংক্ষেপ ও মূল শিক্ষা",
                bodyBn = "${shape.titleBn} অধ্যায়ের মূল গাণিতিক ধারণাগুলো এক নজরে:",
                keyPointsBn = listOf(
                    "সূত্র: $formulaSummary",
                    "বর্তমান আয়তন = ${fmtBn(metrics.volume)} ঘন একক ও সমগ্র তল = ${fmtBn(metrics.totalSurfaceArea)} বর্গ একক।",
                    "প্রস্থচ্ছেদ: অনুভূমিক তলে কাটলে '${sliceInfo.sectionShapeNameBn}' পাওয়া যায়।"
                ),
                interactiveActionHintBn = "অন্য একটি ঘনবস্তু বেছে নিয়ে তুলনামূলক বিশ্লেষণ চালিয়ে যান।"
            )
        )

        val practiceChallenge = StagePracticeChallenge(
            promptBn = "বর্তমান মাপে ${shape.titleBn}-এর আয়তন (Volume) কত ঘন একক? (সূত্র ব্যবহার করে লিখুন)",
            expectedValue = metrics.volume,
            tolerance = max(0.35, metrics.volume * 0.03),
            unitBn = "ঘন একক",
            hintBn = "সূত্র: $formulaSummary। ধাপ ৬-এর হিসাবের সাহায্য নিতে পারেন।",
            solutionExplanationBn = metrics.calculationStepsBn.joinToString(" | ")
        )

        val checkQuestion = buildSolidKnowledgeCheck(shape)

        return TopicNineStageGuide(
            topicId = "solid_3d_${shape.id}",
            topicTitleBn = shape.titleBn,
            stages = stages,
            practiceChallenge = practiceChallenge,
            knowledgeCheckQuestion = checkQuestion,
            recapTakeawaysBn = listOf(
                "${shape.titleBn}-এর সমগ্র তল = প্রতিটি অংশের ক্ষেত্রফলের সমষ্টি।",
                "মাপ k গুণ বাড়ালে ক্ষেত্রফল k² গুণ এবং আয়তন k³ গুণ বৃদ্ধি পায়।",
                "আংশিক উচ্চতায় তরল পূর্ণ করলে অ-রৈখিক (Non-linear) ঘনবস্তুতে আয়তন উচ্চতার সমানুপাতিক হয় না।"
            )
        )
    }

    private fun buildSolidKnowledgeCheck(shape: Solid3DShapeType): StageKnowledgeCheckQuestion {
        return when (shape) {
            Solid3DShapeType.CUBE -> StageKnowledgeCheckQuestion(
                id = "check_solid_cube",
                questionBn = "একটি ঘনকের ধারের দৈর্ঘ্য ২ গুণ করা হলে এর আয়তন কত গুণ হবে?",
                optionsBn = listOf("২ গুণ", "৪ গুণ", "৬ গুণ", "৮ গুণ"),
                correctIndex = 3,
                explanationBn = "ঘনকের আয়তন V = a³। ধার 2a হলে নতুন আয়তন (2a)³ = 8a³, অর্থাৎ ৮ গুণ।"
            )
            Solid3DShapeType.CUBOID -> StageKnowledgeCheckQuestion(
                id = "check_solid_cuboid",
                questionBn = "আয়তাকার ঘনবস্তুর বিপরীত তলগুলোর ক্ষেত্রফল কেমন হয়?",
                optionsBn = listOf("পরস্পর সমান", "সবসময় অসমান", "দ্বিগুণ", "অর্ধেক"),
                correctIndex = 0,
                explanationBn = "আয়তাকার ঘনবস্তুর ৩ জোড়া বিপরীত আয়তাকার তল পরস্পর সর্বসম ও সমান ক্ষেত্রফলবিশিষ্ট (2(lw + lh + wh))।"
            )
            Solid3DShapeType.CYLINDER -> StageKnowledgeCheckQuestion(
                id = "check_solid_cylinder",
                questionBn = "সিলিন্ডারের বক্রতলকে লম্বালম্বি কেটে সমতলে মেলে ধরলে কোন আকৃতি পাওয়া যায়?",
                optionsBn = listOf("ত্রিভুজ", "বৃত্ত", "আয়তক্ষেত্র (প্রস্থ 2πr, উচ্চতা h)", "ট্রাপিজিয়াম"),
                correctIndex = 2,
                explanationBn = "সিলিন্ডারের বক্রতল খুললে একটি আয়তক্ষেত্র পাওয়া যায় যার দৈর্ঘ্য ভূমির পরিধি 2πr এবং প্রস্থ উচ্চতা h।"
            )
            Solid3DShapeType.CONE -> StageKnowledgeCheckQuestion(
                id = "check_solid_cone",
                questionBn = "একই ব্যাসার্ধ r এবং একই উচ্চতা h বিশিষ্ট সিলিন্ডারের তুলনায় শঙ্কুর আয়তন কত?",
                optionsBn = listOf("সমান", "অর্ধেক (১/২)", "এক-তৃতীয়াংশ (১/৩)", "এক-চতুর্থাংশ (১/৪)"),
                correctIndex = 2,
                explanationBn = "শঙ্কুর আয়তন V = (1/3)πr²h, যা সমমাপের সিলিন্ডারের আয়তনের ঠিক ১/৩ অংশ।"
            )
            Solid3DShapeType.SPHERE -> StageKnowledgeCheckQuestion(
                id = "check_solid_sphere",
                questionBn = "r ব্যাসার্ধের একটি গোলকের সমগ্র পৃষ্ঠতলের ক্ষেত্রফল একই ব্যাসার্ধের কয়টি বৃত্তের ক্ষেত্রফলের সমান?",
                optionsBn = listOf("২টি বৃত্ত", "৩টি বৃত্ত", "৪টি বৃত্ত (4πr²)", "৬টি বৃত্ত"),
                correctIndex = 2,
                explanationBn = "আর্কিমিডিসের উপপাদ্য অনুযায়ী গোলকের পৃষ্ঠতলের ক্ষেত্রফল 4πr², যা ৪টি মহাবৃত্তের (πr²) সমান।"
            )
            Solid3DShapeType.TRIANGULAR_PRISM -> StageKnowledgeCheckQuestion(
                id = "check_solid_tri_prism",
                questionBn = "ত্রিভুজাকার প্রিজমের মোট কয়টি তল (Faces) থাকে?",
                optionsBn = listOf("৪টি", "৫টি (২টি ত্রিভুজ ও ৩টি আয়তক্ষেত্র)", "৬টি", "৮টি"),
                correctIndex = 1,
                explanationBn = "ত্রিভুজাকার প্রিজমের উপরে ও নিচে ২টি ত্রিভুজাকার ভূমি এবং পাশে ৩টি আয়তাকার পার্শ্বতলসহ মোট ৫টি তল থাকে।"
            )
            Solid3DShapeType.HEMISPHERE -> StageKnowledgeCheckQuestion(
                id = "check_solid_hemisphere",
                questionBn = "r ব্যাসার্ধের নিরেট অর্ধগোলকের সমগ্র তলের ক্ষেত্রফলের সূত্র কোনটি?",
                optionsBn = listOf("2πr²", "3πr² (বক্রতল 2πr² + সমতল ভূমি πr²)", "4πr²", "πr²"),
                correctIndex = 1,
                explanationBn = "নিরেট অর্ধগোলকের বক্রতল 2πr² এবং নিচের বৃত্তাকার সমতল πr² মিলে মোট ক্ষেত্রফল 3πr²।"
            )
            Solid3DShapeType.SQUARE_PYRAMID -> StageKnowledgeCheckQuestion(
                id = "check_solid_pyramid",
                questionBn = "বর্গাকার পিরামিডের ভূমি সমান্তরাল তলে কাটলে প্রস্থচ্ছেদটি কোন আকৃতির হয়?",
                optionsBn = listOf("বৃত্ত", "বর্গক্ষেত্র", "ষড়ভুজ", "রম্বস"),
                correctIndex = 1,
                explanationBn = "বর্গাকার পিরামিডকে ভূমির সমান্তরাল যেকোনো উচ্চতায় কাটলে ভূমির সদৃশ একটি ছোট বর্গক্ষেত্র পাওয়া যায়।"
            )
            Solid3DShapeType.HEXAGONAL_PRISM -> StageKnowledgeCheckQuestion(
                id = "check_solid_hex_prism",
                questionBn = "সুষম ষড়ভুজাকার প্রিজমের পার্শ্বতল কয়টি এবং কী আকৃতির?",
                optionsBn = listOf("৬টি ত্রিভুজ", "৬টি আয়তক্ষেত্র", "৮টি বর্গক্ষেত্র", "৫টি আয়তক্ষেত্র"),
                correctIndex = 1,
                explanationBn = "ষড়ভুজের ৬টি বাহু থেকে ৬টি উল্লম্ব আয়তাকার পার্শ্বতল তৈরি হয়।"
            )
            Solid3DShapeType.FRUSTUM -> StageKnowledgeCheckQuestion(
                id = "check_solid_frustum",
                questionBn = "ফ্রাস্টাম (ছিন্ন শঙ্কু)-এর তির্যক উচ্চতা l নির্ণয়ের সঠিক সূত্র কোনটি?",
                optionsBn = listOf("√(R² + h²)", "√(h² + (R - r)²)", "R + r + h", "√(h² + (R + r)²)"),
                correctIndex = 1,
                explanationBn = "ফ্রাস্টামের উল্লম্ব প্রস্থচ্ছেদে সমকোণী ত্রিভুজের ভূমি হয় (R - r) এবং উচ্চতা h; তাই l = √(h² + (R - r)²) ।",
            )
        }
    }

    // ------------------------------------------------------------------------
    // 2. TRIGONOMETRY UNIT CIRCLE & ELEVATION CALCULATIONS
    // ------------------------------------------------------------------------

    fun analyzeUnitCircle(angleDegreesInput: Double): TrigUnitCircleState {
        val safeDeg = if (angleDegreesInput.isFinite()) {
            ((angleDegreesInput % 360.0) + 360.0) % 360.0
        } else {
            45.0
        }
        val rad = safeDeg * PI / 180.0
        val sinVal = sin(rad)
        val cosVal = cos(rad)
        val tanVal = if (abs(cosVal) < 1e-5) null else sinVal / cosVal

        val quadrant = when {
            safeDeg <= 90.0 -> 1
            safeDeg <= 180.0 -> 2
            safeDeg <= 270.0 -> 3
            else -> 4
        }
        val quadrantNameBn = when (quadrant) {
            1 -> "১ম চতুর্ভাগ (All Positive: sin+, cos+, tan+)"
            2 -> "২য় চতুর্ভাগ (Sine Positive: sin+, cos−, tan−)"
            3 -> "৩য় চতুর্ভাগ (Tan Positive: sin−, cos−, tan+)"
            else -> "৪র্থ চতুর্ভাগ (Cos Positive: sin−, cos+, tan−)"
        }
        val refAngle = when (quadrant) {
            1 -> safeDeg
            2 -> 180.0 - safeDeg
            3 -> safeDeg - 180.0
            else -> 360.0 - safeDeg
        }
        val identitySum = sinVal * sinVal + cosVal * cosVal
        val tanDisplay = tanVal?.let { fmtBn(it, 3) } ?: "অসংজ্ঞায়িত (∞)"

        return TrigUnitCircleState(
            angleDegrees = safeDeg,
            angleRadians = rad,
            sinValue = sinVal,
            cosValue = cosVal,
            tanValue = tanVal,
            quadrantNumber = quadrant,
            quadrantNameBn = quadrantNameBn,
            referenceAngleDeg = refAngle,
            pythagoreanIdentitySum = identitySum,
            signSummaryBn = quadrantNameBn,
            stepsBn = listOf(
                "কোণ θ = ${fmtBn(safeDeg, 1)}° = ${fmtBn(rad, 3)} রেডিয়ান (সূক্ষ্ম রেফারেন্স কোণ = ${fmtBn(refAngle, 1)}°)",
                "একক বৃত্তে বিন্দু P(cosθ, sinθ) = (${fmtBn(cosVal, 3)}, ${fmtBn(sinVal, 3)})",
                "tanθ = sinθ / cosθ = $tanDisplay",
                "পিথাগোরীয় অভেদ: sin²θ + cos²θ = (${fmtBn(sinVal, 3)})² + (${fmtBn(cosVal, 3)})² = ${fmtBn(identitySum, 2)}"
            )
        )
    }

    fun analyzeTrigElevation(angleDegreesInput: Double, baseDistanceInput: Double): TrigElevationAnalysis {
        val deg = if (angleDegreesInput.isFinite()) angleDegreesInput.coerceIn(5.0, 80.0) else 45.0
        val base = if (baseDistanceInput.isFinite()) baseDistanceInput.coerceIn(1.0, 200.0) else 20.0
        val rad = deg * PI / 180.0
        val height = base * tan(rad)
        val hyp = base / cos(rad)
        return TrigElevationAnalysis(
            angleDegrees = deg,
            baseDistance = base,
            oppositeHeight = height,
            hypotenuseLength = hyp,
            explanationBn = "ভূমি d = ${fmtBn(base)} একক এবং উন্নতি কোণ θ = ${fmtBn(deg, 1)}° হলে উচ্চতা h = d·tanθ = ${fmtBn(height)} একক এবং দৃষ্টিরেখা (অতিভুজ) = ${fmtBn(hyp)} একক।"
        )
    }

    // ------------------------------------------------------------------------
    // 3. 3D COORDINATE & VECTOR STUDIO CALCULATIONS
    // ------------------------------------------------------------------------

    fun analyze3DCoordinates(
        a: Point3DCoord,
        b: Point3DCoord
    ): Coord3DAnalysis {
        val ax = if (a.x.isFinite()) a.x.coerceIn(-10.0, 10.0) else 0.0
        val ay = if (a.y.isFinite()) a.y.coerceIn(-10.0, 10.0) else 0.0
        val az = if (a.z.isFinite()) a.z.coerceIn(-10.0, 10.0) else 0.0

        val bx = if (b.x.isFinite()) b.x.coerceIn(-10.0, 10.0) else 3.0
        val by = if (b.y.isFinite()) b.y.coerceIn(-10.0, 10.0) else 4.0
        val bz = if (b.z.isFinite()) b.z.coerceIn(-10.0, 10.0) else 5.0

        val dx = bx - ax
        val dy = by - ay
        val dz = bz - az
        val xyDist = sqrt(dx * dx + dy * dy)
        val dist3D = sqrt(dx * dx + dy * dy + dz * dz)
        val mid = Point3DCoord((ax + bx) / 2.0, (ay + by) / 2.0, (az + bz) / 2.0)

        val dirCosines = if (dist3D > 1e-6) {
            Triple(dx / dist3D, dy / dist3D, dz / dist3D)
        } else {
            Triple(0.0, 0.0, 0.0)
        }

        return Coord3DAnalysis(
            pointA = Point3DCoord(ax, ay, az),
            pointB = Point3DCoord(bx, by, bz),
            deltaX = dx,
            deltaY = dy,
            deltaZ = dz,
            distance3D = dist3D,
            xyProjectionDistance = xyDist,
            midpoint = mid,
            vectorMagnitudeAB = dist3D,
            directionCosines = dirCosines,
            stepsBn = listOf(
                "বিন্দু A(${fmtBn(ax)}, ${fmtBn(ay)}, ${fmtBn(az)}) এবং B(${fmtBn(bx)}, ${fmtBn(by)}, ${fmtBn(bz)})",
                "অক্ষ বরাবর পার্থক্য: Δx = ${fmtBn(dx)}, Δy = ${fmtBn(dy)}, Δz = ${fmtBn(dz)}",
                "৩ডি দূরত্ব d = √(Δx² + Δy² + Δz²) = √(${fmtBn(dx * dx)} + ${fmtBn(dy * dy)} + ${fmtBn(dz * dz)}) = ${fmtBn(dist3D)} একক",
                "মধ্যবিন্দু M = ((${fmtBn(ax)}+${fmtBn(bx)})/২, (${fmtBn(ay)}+${fmtBn(by)})/২, (${fmtBn(az)}+${fmtBn(bz)})/২) = (${fmtBn(mid.x)}, ${fmtBn(mid.y)}, ${fmtBn(mid.z)})",
                "XY-সমতলে অভিক্ষেপ d_xy = √(Δx² + Δy²) = ${fmtBn(xyDist)} একক; দিক কোসাইন (Direction Cosines): (cos α=${fmtBn(dirCosines.first)}, cos β=${fmtBn(dirCosines.second)}, cos γ=${fmtBn(dirCosines.third)})"
            )
        )
    }

    fun build3DCoordinateSceneMesh(analysis: Coord3DAnalysis): Pair<List<Face3D>, List<Line3D>> {
        val scale = 0.22f
        val ax = (analysis.pointA.x * scale).toFloat()
        val ay = (analysis.pointA.y * scale).toFloat()
        val az = (analysis.pointA.z * scale).toFloat()

        val bx = (analysis.pointB.x * scale).toFloat()
        val by = (analysis.pointB.y * scale).toFloat()
        val bz = (analysis.pointB.z * scale).toFloat()

        val aVec = Vec3(ax, ay, az)
        val bVec = Vec3(bx, by, bz)
        val midVec = Vec3((ax + bx) * 0.5f, (ay + by) * 0.5f, (az + bz) * 0.5f)

        val faces = mutableListOf<Face3D>()
        val floorY = -1.1f
        faces += Face3D(
            id = "coord_floor",
            vertices = listOf(
                Vec3(-1.4f, floorY, -1.4f),
                Vec3(1.4f, floorY, -1.4f),
                Vec3(1.4f, floorY, 1.4f),
                Vec3(-1.4f, floorY, 1.4f)
            ),
            colorRole = 0,
            labelBn = "সমতল",
            isVolumeFill = true
        )

        val lines = mutableListOf<Line3D>()
        lines += Line3D(Vec3(-1.4f, 0f, 0f), Vec3(1.4f, 0f, 0f), labelBn = "X অক্ষ", isInternalAxis = false)
        lines += Line3D(Vec3(0f, -1.4f, 0f), Vec3(0f, 1.4f, 0f), labelBn = "Y অক্ষ", isInternalAxis = false)
        lines += Line3D(Vec3(0f, 0f, -1.4f), Vec3(0f, 0f, 1.4f), labelBn = "Z অক্ষ", isInternalAxis = false)

        lines += Line3D(aVec, bVec, labelBn = "d = ${fmtBn(analysis.distance3D)}", isInternalAxis = true)
        lines += Line3D(aVec, Vec3(bx, ay, az), labelBn = "Δx", isInternalAxis = true)
        lines += Line3D(Vec3(bx, ay, az), Vec3(bx, by, az), labelBn = "Δy", isInternalAxis = true)
        lines += Line3D(Vec3(bx, by, az), bVec, labelBn = "Δz", isInternalAxis = true)
        lines += Line3D(aVec, Vec3(bx, by, az), labelBn = "d_xy = ${fmtBn(analysis.xyProjectionDistance)}", isInternalAxis = false)
        lines += Line3D(Vec3(0f, 0f, 0f), midVec, labelBn = "M", isInternalAxis = false)

        return faces to lines
    }

    // ------------------------------------------------------------------------
    // 4. 2D QUADRATIC & 3D SURFACE (z = f(x, y)) GRAPHING ENGINE
    // ------------------------------------------------------------------------

    fun analyzeQuadratic2D(aInput: Double, bInput: Double, cInput: Double): Quadratic2DAnalysis {
        val rawA = if (aInput.isFinite()) aInput.coerceIn(-5.0, 5.0) else 1.0
        val a = if (abs(rawA) < 0.1) (if (rawA >= 0.0) 0.1 else -0.1) else rawA
        val b = if (bInput.isFinite()) bInput.coerceIn(-10.0, 10.0) else 0.0
        val c = if (cInput.isFinite()) cInput.coerceIn(-10.0, 10.0) else 0.0

        val vx = -b / (2.0 * a)
        val vy = a * vx * vx + b * vx + c
        val disc = b * b - 4.0 * a * c
        val roots = when {
            disc > 1e-6 -> {
                val sqrtD = sqrt(disc)
                val r1 = (-b - sqrtD) / (2.0 * a)
                val r2 = (-b + sqrtD) / (2.0 * a)
                listOf(min(r1, r2), max(r1, r2))
            }
            abs(disc) <= 1e-6 -> listOf(-b / (2.0 * a))
            else -> emptyList()
        }

        val formulaBn = "y = ${fmtBn(a)}x² + (${fmtBn(b)})x + (${fmtBn(c)})"
        val rootDescBn = when (roots.size) {
            2 -> "২টি বাস্তব মূল: x₁ = ${fmtBn(roots[0])}, x₂ = ${fmtBn(roots[1])}"
            1 -> "১টি বাস্তব স্পর্শবিন্দু মূল: x = ${fmtBn(roots[0])}"
            else -> "কোনো বাস্তব মূল নেই (D < 0, লেখচিত্র X-অক্ষকে ছেদ করে না)"
        }

        return Quadratic2DAnalysis(
            a = a,
            b = b,
            c = c,
            vertexX = vx,
            vertexY = vy,
            discriminant = disc,
            realRoots = roots,
            yIntercept = c,
            opensUpward = a > 0,
            formulaTextBn = formulaBn,
            stepsBn = listOf(
                "দ্বিঘাত ফাংশন: $formulaBn (${if (a > 0) "ঊর্ধ্বমুখী প্যারাবোলা a > 0" else "নিম্নমুখী প্যারাবোলা a < 0"})",
                "শীর্ষবিন্দু (Vertex): x = -b/(2a) = ${fmtBn(vx)}, y = ${fmtBn(vy)} → V(${fmtBn(vx)}, ${fmtBn(vy)})",
                "নিশ্চয়ক (Discriminant) D = b² - 4ac = ${fmtBn(disc)} → $rootDescBn",
                "Y-অক্ষের ছেদবিন্দু (x = 0): (০, ${fmtBn(c)})"
            )
        )
    }

    fun evaluateSurfaceZ(preset: Surface3DPreset, x: Double, y: Double, a: Double, b: Double): Double {
        return when (preset) {
            Surface3DPreset.PARABOLOID -> a * (x * x + y * y) / 4.0
            Surface3DPreset.SADDLE_HYPERBOLIC -> a * (x * x - y * y) / 4.0
            Surface3DPreset.RADIAL_WAVE -> a * 0.55 * cos(b * sqrt(x * x + y * y))
            Surface3DPreset.INCLINED_PLANE -> (a * x + b * y) / 3.0
        }
    }

    fun build3DSurfaceMesh(
        preset: Surface3DPreset,
        paramAInput: Double,
        paramBInput: Double,
        gridResolution: Int = 10
    ): Surface3DMeshAnalysis {
        val a = if (paramAInput.isFinite()) paramAInput.coerceIn(-2.5, 2.5) else 1.0
        val b = if (paramBInput.isFinite()) paramBInput.coerceIn(0.4, 3.0) else 1.5
        val res = gridResolution.coerceIn(6, 16)
        val domainMax = 2.0
        val spatialScale = 0.65f

        var minZ = Double.POSITIVE_INFINITY
        var maxZ = Double.NEGATIVE_INFINITY

        val grid = Array(res + 1) { ix ->
            val x = -domainMax + (2.0 * domainMax * ix) / res
            Array(res + 1) { iy ->
                val y = -domainMax + (2.0 * domainMax * iy) / res
                val z = evaluateSurfaceZ(preset, x, y, a, b).coerceIn(-2.5, 2.5)
                if (z < minZ) minZ = z
                if (z > maxZ) maxZ = z
                Vec3(
                    x = (x * spatialScale).toFloat(),
                    y = (z * spatialScale).toFloat(),
                    z = (y * spatialScale).toFloat()
                )
            }
        }

        val faces = mutableListOf<Face3D>()
        for (ix in 0 until res) {
            for (iy in 0 until res) {
                val p00 = grid[ix][iy]
                val p10 = grid[ix + 1][iy]
                val p11 = grid[ix + 1][iy + 1]
                val p01 = grid[ix][iy + 1]
                val avgY = (p00.y + p10.y + p11.y + p01.y) * 0.25f
                val role = when {
                    avgY > 0.25f -> 0
                    avgY > -0.25f -> 1
                    else -> 2
                }
                faces += Face3D(
                    id = "surf_${ix}_${iy}",
                    vertices = listOf(p00, p10, p11, p01),
                    colorRole = role
                )
            }
        }

        val lines = listOf(
            Line3D(Vec3(-1.45f, 0f, 0f), Vec3(1.45f, 0f, 0f), labelBn = "X", isInternalAxis = false),
            Line3D(Vec3(0f, -1.35f, 0f), Vec3(0f, 1.35f, 0f), labelBn = "Z=f(x,y)", isInternalAxis = true),
            Line3D(Vec3(0f, 0f, -1.45f), Vec3(0f, 0f, 1.45f), labelBn = "Y", isInternalAxis = false)
        )

        val z0 = evaluateSurfaceZ(preset, 0.0, 0.0, a, b)
        val safeMin = if (minZ.isFinite()) minZ else 0.0
        val safeMax = if (maxZ.isFinite()) maxZ else 0.0

        return Surface3DMeshAnalysis(
            preset = preset,
            paramA = a,
            paramB = b,
            zAtOrigin = z0,
            minZ = safeMin,
            maxZ = safeMax,
            faces = faces,
            lines = lines,
            explanationBn = "${preset.titleBn} (${preset.formulaDisplay}): মূলবিন্দুতে f(0,0) = ${fmtBn(z0)}, ডোমেইনে সর্বনিম্ন z = ${fmtBn(safeMin)} এবং সর্বোচ্চ z = ${fmtBn(safeMax)}।"
        )
    }

    // ------------------------------------------------------------------------
    // 5. ALGEBRA TILES & FRACTION OPERATIONS ENGINE
    // ------------------------------------------------------------------------

    fun analyzeAlgebraTiles(
        xSquaredCountInput: Int,
        xPositiveInput: Int,
        xNegativeInput: Int,
        unitPositiveInput: Int,
        unitNegativeInput: Int,
        xEvalValueInput: Int = 2
    ): AlgebraTilesAnalysis {
        val x2 = xSquaredCountInput.coerceIn(0, 4)
        val xp = xPositiveInput.coerceIn(0, 8)
        val xn = xNegativeInput.coerceIn(0, 8)
        val up = unitPositiveInput.coerceIn(0, 12)
        val un = unitNegativeInput.coerceIn(0, 12)
        val xVal = xEvalValueInput.coerceIn(-5, 10)

        val zeroX = min(xp, xn)
        val zeroUnit = min(up, un)
        val netX = xp - xn
        val netUnit = up - un

        val terms = mutableListOf<String>()
        if (x2 > 0) {
            terms += if (x2 == 1) "x²" else "${fmtIntBn(x2)}x²"
        }
        if (netX != 0) {
            val sign = if (netX > 0 && terms.isNotEmpty()) "+ " else if (netX < 0) "− " else ""
            val absX = abs(netX)
            terms += "$sign${if (absX == 1) "x" else "${fmtIntBn(absX)}x"}"
        }
        if (netUnit != 0 || terms.isEmpty()) {
            val sign = if (netUnit > 0 && terms.isNotEmpty()) "+ " else if (netUnit < 0) "− " else ""
            terms += "$sign${fmtIntBn(abs(netUnit))}"
        }
        val simplified = terms.joinToString(" ")

        var factorized: String? = null
        if (x2 == 1) {
            for (p in -12..12) {
                for (q in p..12) {
                    if (p + q == netX && p * q == netUnit) {
                        val pStr = if (p >= 0) "+ ${fmtIntBn(p)}" else "− ${fmtIntBn(abs(p))}"
                        val qStr = if (q >= 0) "+ ${fmtIntBn(q)}" else "− ${fmtIntBn(abs(q))}"
                        factorized = "(x $pStr)(x $qStr)"
                        break
                    }
                }
                if (factorized != null) break
            }
        } else if (x2 > 1) {
            for (a1 in 1..x2) {
                if (x2 % a1 != 0) continue
                val a2 = x2 / a1
                for (p in -12..12) {
                    for (q in -12..12) {
                        if (p * q == netUnit && a1 * q + a2 * p == netX) {
                            val a1Str = if (a1 == 1) "x" else "${fmtIntBn(a1)}x"
                            val a2Str = if (a2 == 1) "x" else "${fmtIntBn(a2)}x"
                            val pStr = if (p >= 0) "+ ${fmtIntBn(p)}" else "− ${fmtIntBn(abs(p))}"
                            val qStr = if (q >= 0) "+ ${fmtIntBn(q)}" else "− ${fmtIntBn(abs(q))}"
                            factorized = "($a1Str $pStr)($a2Str $qStr)"
                            break
                        }
                    }
                    if (factorized != null) break
                }
                if (factorized != null) break
            }
        } else if (x2 == 0 && netX != 0 && netUnit != 0) {
            val g = gcd(abs(netX), abs(netUnit))
            if (g > 1) {
                val signFactor = if (netX < 0) -g else g
                val innerX = netX / signFactor
                val innerU = netUnit / signFactor
                val xTerm = if (innerX == 1) "x" else "${fmtIntBn(innerX)}x"
                val uTerm = if (innerU >= 0) "+ ${fmtIntBn(innerU)}" else "− ${fmtIntBn(abs(innerU))}"
                val kStr = if (signFactor < 0) "−${fmtIntBn(abs(signFactor))}" else fmtIntBn(signFactor)
                factorized = "$kStr($xTerm $uTerm)"
            }
        }

        val evaluated = x2 * (xVal * xVal).toDouble() + netX * xVal.toDouble() + netUnit.toDouble()

        val steps = mutableListOf(
            "মোট টাইলস: ${fmtIntBn(x2)}টি x², ${fmtIntBn(xp)}টি (+x), ${fmtIntBn(xn)}টি (−x), ${fmtIntBn(up)}টি (+1), ${fmtIntBn(un)}টি (−1)",
            "শূন্য-জোড়া (Zero-Pair Cancellation): ${fmtIntBn(zeroX)} জোড়া (+x, −x) এবং ${fmtIntBn(zeroUnit)} জোড়া (+1, −1) পরস্পরকে বাতিল করেছে।",
            "সরলীকৃত বীজগাণিতিক রাশি: $simplified"
        )
        if (factorized != null) {
            steps += "আয়তাকার ক্ষেত্রফল মডেলে উৎপাদকে বিশ্লেষণ / বণ্টন বিধি: $simplified = $factorized"
        }
        steps += "x = ${fmtIntBn(xVal)} বসালে রাশিটির মান = ${fmtBn(evaluated, 0)}"

        return AlgebraTilesAnalysis(
            xSquaredCount = x2,
            xPositiveCount = xp,
            xNegativeCount = xn,
            unitPositiveCount = up,
            unitNegativeCount = un,
            netXCount = netX,
            netUnitCount = netUnit,
            zeroPairsX = zeroX,
            zeroPairsUnit = zeroUnit,
            simplifiedExpressionBn = simplified,
            factorizedFormBn = factorized,
            evaluatedValueAtX = evaluated,
            xValue = xVal,
            stepsBn = steps
        )
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = abs(a)
        var y = abs(b)
        while (y != 0) {
            val t = y
            y = x % y
            x = t
        }
        return max(1, x)
    }

    fun analyzeFractionOperation(
        num1Input: Int,
        den1Input: Int,
        num2Input: Int,
        den2Input: Int,
        op: FractionOpType
    ): FractionOperationAnalysis {
        val n1 = num1Input.coerceIn(-24, 24)
        val d1 = den1Input.coerceIn(1, 24)
        val n2 = num2Input.coerceIn(-24, 24)
        val d2 = den2Input.coerceIn(1, 24)

        val lcmDen = (d1 * d2) / gcd(d1, d2)
        val conv1 = n1 * (lcmDen / d1)
        val conv2 = n2 * (lcmDen / d2)

        val (rawNumSigned, rawDenSigned) = when (op) {
            FractionOpType.ADD -> (conv1 + conv2) to lcmDen
            FractionOpType.SUBTRACT -> (conv1 - conv2) to lcmDen
            FractionOpType.MULTIPLY -> (n1 * n2) to (d1 * d2)
            FractionOpType.DIVIDE -> {
                val safeN2 = if (n2 == 0) 1 else n2
                (n1 * d2) to (d1 * safeN2)
            }
        }

        val rawNum = if (rawDenSigned < 0) -rawNumSigned else rawNumSigned
        val rawDen = max(1, abs(rawDenSigned))

        val g = gcd(rawNum, rawDen)
        val simpNum = rawNum / g
        val simpDen = max(1, rawDen / g)

        val absSimpNum = abs(simpNum)
        val mixedWhole = (absSimpNum / simpDen) * (if (simpNum < 0) -1 else 1)
        val mixedRem = absSimpNum % simpDen
        val dec = simpNum.toDouble() / simpDen.toDouble()

        val mixedSuffixBn = if (absSimpNum > simpDen && mixedRem != 0) {
            " | মিশ্র ভগ্নাংশ: ${fmtIntBn(mixedWhole)} পূর্ণ ${fmtIntBn(mixedRem)}/${fmtIntBn(simpDen)}"
        } else ""

        val steps = when (op) {
            FractionOpType.ADD, FractionOpType.SUBTRACT -> listOf(
                "ভগ্নাংশ দুটি: ${fmtIntBn(n1)}/${fmtIntBn(d1)} এবং ${fmtIntBn(n2)}/${fmtIntBn(d2)}",
                "হর ${fmtIntBn(d1)} ও ${fmtIntBn(d2)}-এর ল.সা.গু (LCD) = ${fmtIntBn(lcmDen)}",
                "সমহর ভগ্নাংশে রূপান্তর: ${fmtIntBn(conv1)}/${fmtIntBn(lcmDen)} ${op.symbol} ${fmtIntBn(conv2)}/${fmtIntBn(lcmDen)} = ${fmtIntBn(rawNum)}/${fmtIntBn(rawDen)}",
                "লঘিষ্ঠ আকার: ${fmtIntBn(simpNum)}/${fmtIntBn(simpDen)} (দশমিক মান ≈ ${fmtBn(dec)})$mixedSuffixBn"
            )
            FractionOpType.MULTIPLY -> listOf(
                "ভগ্নাংশের গুণ (ক্ষেত্রফল ওভারল্যাপ): (${fmtIntBn(n1)}/${fmtIntBn(d1)}) × (${fmtIntBn(n2)}/${fmtIntBn(d2)})",
                "লব × লব = ${fmtIntBn(n1 * n2)} এবং হর × হর = ${fmtIntBn(d1 * d2)} → ${fmtIntBn(rawNum)}/${fmtIntBn(rawDen)}",
                "লঘিষ্ঠ আকার: ${fmtIntBn(simpNum)}/${fmtIntBn(simpDen)} (দশমিক মান ≈ ${fmtBn(dec)})$mixedSuffixBn"
            )
            FractionOpType.DIVIDE -> listOf(
                "ভগ্নাংশের ভাগ (বিপরীত ভগ্নাংশ দিয়ে গুণ): (${fmtIntBn(n1)}/${fmtIntBn(d1)}) ÷ (${fmtIntBn(n2)}/${fmtIntBn(d2)})",
                "= (${fmtIntBn(n1)}/${fmtIntBn(d1)}) × (${fmtIntBn(d2)}/${fmtIntBn(if (n2 == 0) 1 else n2)}) = ${fmtIntBn(rawNum)}/${fmtIntBn(rawDen)}",
                "লঘিষ্ঠ আকার: ${fmtIntBn(simpNum)}/${fmtIntBn(simpDen)} (দশমিক মান ≈ ${fmtBn(dec)})$mixedSuffixBn"
            )
        }

        return FractionOperationAnalysis(
            num1 = n1,
            den1 = d1,
            num2 = n2,
            den2 = d2,
            op = op,
            lcmDenominator = lcmDen,
            convertedNum1 = conv1,
            convertedNum2 = conv2,
            rawResultNum = rawNum,
            rawResultDen = rawDen,
            simplifiedNum = simpNum,
            simplifiedDen = simpDen,
            mixedWhole = mixedWhole,
            mixedRemNum = mixedRem,
            decimalValue = dec,
            stepsBn = steps
        )
    }
}
