package com.example.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

enum class Solid3DShapeType(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val param1LabelBn: String,
    val param2LabelBn: String?,
    val param3LabelBn: String?,
    val defaultP1: Double,
    val defaultP2: Double,
    val defaultP3: Double,
    val supportsNetUnfold: Boolean
) {
    CUBE(
        id = "cube",
        titleBn = "ঘনক (Cube)",
        subtitleBn = "৬টি সমান বর্গাকার তলবিশিষ্ট সুষম ঘনবস্তু",
        param1LabelBn = "ধার (Side a)",
        param2LabelBn = null,
        param3LabelBn = null,
        defaultP1 = 6.0,
        defaultP2 = 6.0,
        defaultP3 = 6.0,
        supportsNetUnfold = true
    ),
    CUBOID(
        id = "cuboid",
        titleBn = "আয়তাকার ঘনবস্তু (Cuboid)",
        subtitleBn = "দৈর্ঘ্য, প্রস্থ ও উচ্চতাবিশিষ্ট ৬ তলের প্রিজম",
        param1LabelBn = "দৈর্ঘ্য (Length l)",
        param2LabelBn = "প্রস্থ (Width w)",
        param3LabelBn = "উচ্চতা (Height h)",
        defaultP1 = 8.0,
        defaultP2 = 5.0,
        defaultP3 = 6.0,
        supportsNetUnfold = true
    ),
    CYLINDER(
        id = "cylinder",
        titleBn = "বেলন / সিলিন্ডার (Cylinder)",
        subtitleBn = "২টি বৃত্তাকার ভূমি ও ১টি বক্রতলবিশিষ্ট ঘনবস্তু",
        param1LabelBn = "ব্যাসার্ধ (Radius r)",
        param2LabelBn = "উচ্চতা (Height h)",
        param3LabelBn = null,
        defaultP1 = 4.0,
        defaultP2 = 8.0,
        defaultP3 = 1.0,
        supportsNetUnfold = true
    ),
    CONE(
        id = "cone",
        titleBn = "কোণক / শঙ্কু (Cone)",
        subtitleBn = "বৃত্তাকার ভূমি ও শীর্ষবিন্দুবিশিষ্ট ত্রিমাত্রিক শঙ্কু",
        param1LabelBn = "ভূমির ব্যাসার্ধ (Radius r)",
        param2LabelBn = "উচ্চতা (Height h)",
        param3LabelBn = null,
        defaultP1 = 5.0,
        defaultP2 = 8.0,
        defaultP3 = 1.0,
        supportsNetUnfold = true
    ),
    SPHERE(
        id = "sphere",
        titleBn = "গোলক (Sphere)",
        subtitleBn = "কেন্দ্র থেকে সমদূরবর্তী বিন্দুসমূহের সুষম বক্রতল",
        param1LabelBn = "ব্যাসার্ধ (Radius r)",
        param2LabelBn = null,
        param3LabelBn = null,
        defaultP1 = 5.0,
        defaultP2 = 5.0,
        defaultP3 = 5.0,
        supportsNetUnfold = false
    ),
    TRIANGULAR_PRISM(
        id = "triangular_prism",
        titleBn = "ত্রিভুজাকার প্রিজম (Triangular Prism)",
        subtitleBn = "২টি ত্রিভুজাকার ভূমি ও ৩টি আয়তাকার পার্শ্বতল",
        param1LabelBn = "ত্রিভুজ ভূমি (Base b)",
        param2LabelBn = "ত্রিভুজ উচ্চতা (Height h)",
        param3LabelBn = "প্রিজম দৈর্ঘ্য (Length L)",
        defaultP1 = 6.0,
        defaultP2 = 5.0,
        defaultP3 = 8.0,
        supportsNetUnfold = true
    )
}

enum class CameraPreset3D(val id: String, val titleBn: String, val pitchDeg: Float, val yawDeg: Float) {
    ISOMETRIC("iso", "আইসোমেট্রিক", 26f, -38f),
    FRONT("front", "সামনে", 6f, 0f),
    TOP("top", "উপরে", 82f, 0f),
    SIDE("side", "পাশ থেকে", 10f, -85f)
}

enum class SolidAnatomyHighlight(val id: String, val titleBn: String) {
    ALL("all", "সম্পূর্ণ গঠন"),
    FACES("faces", "তলসমূহ (Faces)"),
    EDGES("edges", "ধারসমূহ (Edges)"),
    VERTICES("vertices", "শীর্ষবিন্দু (Vertices)"),
    DIAGONAL_OR_AXIS("axis", "কর্ণ / অক্ষ ও উচ্চতা")
}

data class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)

    fun dot(other: Vec3): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vec3): Vec3 = Vec3(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x
    )

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalized(): Vec3 {
        val len = length()
        return if (len > 1e-6f) Vec3(x / len, y / len, z / len) else Vec3(0f, 0f, 1f)
    }

    companion object {
        fun lerp(a: Vec3, b: Vec3, t: Float): Vec3 {
            val clamped = t.coerceIn(0f, 1f)
            return Vec3(
                x = a.x + (b.x - a.x) * clamped,
                y = a.y + (b.y - a.y) * clamped,
                z = a.z + (b.z - a.z) * clamped
            )
        }
    }
}

data class Face3D(
    val id: String,
    val vertices: List<Vec3>,
    val colorRole: Int = 0, // 0=primary, 1=secondary, 2=tertiary, 3=volumeFill
    val labelBn: String? = null,
    val isVolumeFill: Boolean = false
) {
    val centroid: Vec3
        get() {
            if (vertices.isEmpty()) return Vec3(0f, 0f, 0f)
            var sx = 0f
            var sy = 0f
            var sz = 0f
            vertices.forEach {
                sx += it.x
                sy += it.y
                sz += it.z
            }
            val n = vertices.size.toFloat()
            return Vec3(sx / n, sy / n, sz / n)
        }

    val normal: Vec3
        get() {
            if (vertices.size < 3) return Vec3(0f, 0f, 1f)
            val e1 = vertices[1] - vertices[0]
            val e2 = vertices[2] - vertices[0]
            return e1.cross(e2).normalized()
        }
}

data class Line3D(
    val start: Vec3,
    val end: Vec3,
    val labelBn: String? = null,
    val isInternalAxis: Boolean = false
)

data class OrbitCameraState(
    val pitchDeg: Float = 26f,
    val yawDeg: Float = -38f,
    val zoom: Float = 1.0f,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val isOrthographic: Boolean = false
)

data class ProjectedPoint2D(
    val x: Float,
    val y: Float,
    val depthZ: Float
)

data class ProjectedFace2D(
    val id: String,
    val points: List<ProjectedPoint2D>,
    val averageDepthZ: Float,
    val lightIntensity: Float,
    val isFrontFacing: Boolean,
    val colorRole: Int,
    val labelBn: String?,
    val center2D: ProjectedPoint2D,
    val isVolumeFill: Boolean
)

data class ProjectedLine2D(
    val start: ProjectedPoint2D,
    val end: ProjectedPoint2D,
    val labelBn: String?,
    val isInternalAxis: Boolean
)

data class ProjectedScene2D(
    val facesBackToFront: List<ProjectedFace2D>,
    val uniqueVertices: List<ProjectedPoint2D>,
    val internalLines: List<ProjectedLine2D>
)

data class Solid3DCalculationResult(
    val shapeType: Solid3DShapeType,
    val volume: Double,
    val totalSurfaceArea: Double,
    val lateralSurfaceArea: Double,
    val secondaryMetricValue: Double,
    val secondaryMetricLabelBn: String,
    val verticesCount: Int,
    val edgesCount: Int,
    val facesCount: Int,
    val volumeFormulaBn: String,
    val surfaceAreaFormulaBn: String,
    val lateralAreaFormulaBn: String,
    val calculationStepsBn: List<String>,
    val conceptSummaryBn: String,
    val netExplanationBn: String
)

data class VolumeFillAnalysis(
    val shapeType: Solid3DShapeType,
    val heightFillFraction: Double,
    val volumeFillFraction: Double,
    val totalVolume: Double,
    val filledVolume: Double,
    val isLinearWithHeight: Boolean,
    val formulaRelationBn: String,
    val explanationBn: String,
    val invertedApexDownVolumeFraction: Double? = null,
    val invertedApexDownVolume: Double? = null
)

object Math3DEngine {

    private val lightDirection = Vec3(-0.45f, 0.65f, 0.62f).normalized()

    private fun sanitizeDim(value: Double, fallback: Double = 1.0): Double {
        if (value.isNaN() || value.isInfinite()) return fallback.coerceAtLeast(0.5)
        return value.coerceIn(0.5, 500.0)
    }

    private fun rotateAroundAxis(point: Vec3, pivot: Vec3, axisUnit: Vec3, angleRad: Float): Vec3 {
        val k = axisUnit.normalized()
        val v = point - pivot
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        val kCrossV = k.cross(v)
        val kDotV = k.dot(v)
        val rotated = Vec3(
            x = v.x * cosA + kCrossV.x * sinA + k.x * kDotV * (1f - cosA),
            y = v.y * cosA + kCrossV.y * sinA + k.y * kDotV * (1f - cosA),
            z = v.z * cosA + kCrossV.z * sinA + k.z * kDotV * (1f - cosA)
        )
        return pivot + rotated
    }

    /**
     * Computes exact mathematical volume at a given vertical height fill fraction p = h_fill / H (in 0..1).
     * Distinguishes linear cross-section solids (Cube, Cuboid, Cylinder) from non-linear cross-section solids
     * (Cone, Sphere, Triangular Prism).
     */
    fun calculateVolumeFill(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double = shape.defaultP2,
        p3: Double = shape.defaultP3,
        heightFillFraction: Float
    ): VolumeFillAnalysis {
        val p = if (heightFillFraction.isNaN() || heightFillFraction.isInfinite()) {
            0.0
        } else {
            heightFillFraction.toDouble().coerceIn(0.0, 1.0)
        }
        val metrics = calculateSolidMetrics(shape, p1, p2, p3)
        val totalVol = metrics.volume
        val fmt = { v: Double -> VisualMathEngine.formatNumberBn(v, 2) }
        val hPctBn = VisualMathEngine.formatNumberBn(p * 100.0, 1)

        return when (shape) {
            Solid3DShapeType.CUBE, Solid3DShapeType.CUBOID, Solid3DShapeType.CYLINDER -> {
                val volFrac = p
                val filledVol = totalVol * volFrac
                val vPctBn = VisualMathEngine.formatNumberBn(volFrac * 100.0, 1)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = volFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = true,
                    formulaRelationBn = "V(p) = p × V (সুষম প্রস্থচ্ছেদ)",
                    explanationBn = "$hPctBn% উচ্চতায় পূর্ণ করলে সুষম প্রস্থচ্ছেদের কারণে ঠিক $vPctBn% আয়তন (${fmt(filledVol)} ঘন একক) পূর্ণ হয়।"
                )
            }

            Solid3DShapeType.CONE -> {
                // Base at bottom (frustum from base up to height p*h): V_fill / V = 1 - (1 - p)^3
                // Inverted cone (apex at bottom, filled to height p*h): V_inv / V = p^3
                val oneMinusP = 1.0 - p
                val baseDownFrac = (1.0 - oneMinusP * oneMinusP * oneMinusP).coerceIn(0.0, 1.0)
                val apexDownFrac = (p * p * p).coerceIn(0.0, 1.0)
                val filledVol = totalVol * baseDownFrac
                val apexDownVol = totalVol * apexDownFrac
                val vPctBn = VisualMathEngine.formatNumberBn(baseDownFrac * 100.0, 2)
                val invPctBn = VisualMathEngine.formatNumberBn(apexDownFrac * 100.0, 2)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = baseDownFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = false,
                    formulaRelationBn = "ভূমি নিচে (Frustum): V_fill/V = 1 - (1 - p)³ | শীর্ষ নিচে: V_inv/V = p³",
                    explanationBn = "কোণকের প্রস্থচ্ছেদ উচ্চতার সাথে পরিবর্তিত হয়: $hPctBn% উচ্চতায় (ভূমি নিচে) প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক); আর শীর্ষবিন্দু নিচে থাকলে হয় $invPctBn% (${fmt(apexDownVol)} ঘন একক)।",
                    invertedApexDownVolumeFraction = apexDownFrac,
                    invertedApexDownVolume = apexDownVol
                )
            }

            Solid3DShapeType.SPHERE -> {
                // Spherical cap of height h_c = 2*r*p: V_cap / V = p^2 * (3 - 2p) = 3p^2 - 2p^3
                val volFrac = (p * p * (3.0 - 2.0 * p)).coerceIn(0.0, 1.0)
                val filledVol = totalVol * volFrac
                val vPctBn = VisualMathEngine.formatNumberBn(volFrac * 100.0, 2)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = volFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = false,
                    formulaRelationBn = "গোলক টুপি (Spherical Cap): V_fill/V = p²(3 - 2p)",
                    explanationBn = "গোলকের $hPctBn% উচ্চতায় (h_c = 2rp) গোলক-টুপির সূত্রানুসারে প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক)।"
                )
            }

            Solid3DShapeType.TRIANGULAR_PRISM -> {
                // Triangular prism resting on rectangular base b×L with apex ridge at top:
                // Unfilled top triangle has height (1-p)h and width (1-p)b, so area fraction (1-p)^2.
                // Filled bottom trapezoidal prism volume fraction = 1 - (1 - p)^2 = 2p - p^2.
                val volFrac = (2.0 * p - p * p).coerceIn(0.0, 1.0)
                val filledVol = totalVol * volFrac
                val vPctBn = VisualMathEngine.formatNumberBn(volFrac * 100.0, 2)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = volFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = false,
                    formulaRelationBn = "ট্রাপিজিয়াম প্রস্থচ্ছেদ: V_fill/V = 1 - (1 - p)² = 2p - p²",
                    explanationBn = "ত্রিভুজাকার প্রিজমের প্রশস্ত ভূমি নিচে থাকায় $hPctBn% উচ্চতায় প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক)।"
                )
            }
        }
    }

    fun calculateSolidMetrics(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double = shape.defaultP2,
        p3: Double = shape.defaultP3
    ): Solid3DCalculationResult {
        val a = sanitizeDim(p1, shape.defaultP1)
        val b = sanitizeDim(p2, shape.defaultP2)
        val c = sanitizeDim(p3, shape.defaultP3)
        val fmt = { v: Double -> VisualMathEngine.formatNumberBn(v) }

        return when (shape) {
            Solid3DShapeType.CUBE -> {
                val vol = a * a * a
                val latArea = 4.0 * a * a
                val totArea = 6.0 * a * a
                val diag = sqrt(3.0) * a
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = diag,
                    secondaryMetricLabelBn = "কর্ণ (Space Diagonal √3a)",
                    verticesCount = 8,
                    edgesCount = 12,
                    facesCount = 6,
                    volumeFormulaBn = "আয়তন V = a³",
                    surfaceAreaFormulaBn = "সমগ্র তলের ক্ষেত্রফল A = 6a²",
                    lateralAreaFormulaBn = "চার দেয়ালের ক্ষেত্রফল = 4a²",
                    calculationStepsBn = listOf(
                        "দেওয়া আছে, ঘনকের প্রতি ধার a = ${fmt(a)} একক",
                        "১টি বর্গাকার তলের ক্ষেত্রফল = a² = ${fmt(a)}² = ${fmt(a * a)} বর্গ একক",
                        "সমগ্র পৃষ্ঠতলের ক্ষেত্রফল = 6a² = ৬ × ${fmt(a * a)} = ${fmt(totArea)} বর্গ একক",
                        "ঘনকের আয়তন V = a³ = ${fmt(a)}³ = ${fmt(vol)} ঘন একক",
                        "ঘনকের কর্ণ d = √3 × a = ১.৭৩২ × ${fmt(a)} = ${fmt(diag)} একক"
                    ),
                    conceptSummaryBn = "ঘনকের ৬টি তলই সর্বসম বর্গক্ষেত্র। এর ১২টি সমান ধার এবং ৮টি শীর্ষবিন্দু রয়েছে (অয়লারের সূত্র: V - E + F = ৮ - ১২ + ৬ = ২)।",
                    netExplanationBn = "নেট উন্মোচন করলে দেখা যায় ৬টি বর্গাকার তল (প্রতিটি a²) কব্জা (Hinge) বরাবর ৯০° ঘুরে ক্রস আকারে ছড়িয়ে মোট 6a² ক্ষেত্রফল গঠন করে।"
                )
            }

            Solid3DShapeType.CUBOID -> {
                val vol = a * b * c
                val latArea = 2.0 * (a + b) * c
                val totArea = 2.0 * (a * b + b * c + c * a)
                val diag = sqrt(a * a + b * b + c * c)
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = diag,
                    secondaryMetricLabelBn = "কর্ণ √(l² + w² + h²)",
                    verticesCount = 8,
                    edgesCount = 12,
                    facesCount = 6,
                    volumeFormulaBn = "আয়তন V = l × w × h",
                    surfaceAreaFormulaBn = "সমগ্র তল A = 2(lw + wh + hl)",
                    lateralAreaFormulaBn = "পার্শ্বতল = 2(l + w)h",
                    calculationStepsBn = listOf(
                        "দৈর্ঘ্য l = ${fmt(a)}, প্রস্থ w = ${fmt(b)}, উচ্চতা h = ${fmt(c)} একক",
                        "ভূমির ক্ষেত্রফল = l × w = ${fmt(a)} × ${fmt(b)} = ${fmt(a * b)} বর্গ একক",
                        "আয়তন V = ভূমির ক্ষেত্রফল × উচ্চতা = ${fmt(a * b)} × ${fmt(c)} = ${fmt(vol)} ঘন একক",
                        "সমগ্র তলের ক্ষেত্রফল = 2(${fmt(a * b)} + ${fmt(b * c)} + ${fmt(c * a)}) = ${fmt(totArea)} বর্গ একক",
                        "কর্ণ d = √(${fmt(a)}² + ${fmt(b)}² + ${fmt(c)}²) = ${fmt(diag)} একক"
                    ),
                    conceptSummaryBn = "আয়তাকার ঘনবস্তুর বিপরীত তলগুলো পরস্পর সমান ও সমান্তরাল। এতে ৩ জোড়া (মোট ৬টি) আয়তাকার তল থাকে।",
                    netExplanationBn = "নেট উন্মোচন করলে ২টি(l×w), ২টি(w×h) এবং ২টি(h×l) আয়তক্ষেত্র পাওয়া যায়, যাদের যোগফল 2(lw + wh + hl)।"
                )
            }

            Solid3DShapeType.CYLINDER -> {
                val r = a
                val h = b
                val baseArea = PI * r * r
                val curvedArea = 2.0 * PI * r * h
                val totArea = curvedArea + 2.0 * baseArea
                val vol = baseArea * h
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = curvedArea,
                    secondaryMetricValue = baseArea,
                    secondaryMetricLabelBn = "ভূমির ক্ষেত্রফল (πr²)",
                    verticesCount = 0,
                    edgesCount = 2,
                    facesCount = 3,
                    volumeFormulaBn = "আয়তন V = πr²h",
                    surfaceAreaFormulaBn = "সমগ্র তল A = 2πr(r + h)",
                    lateralAreaFormulaBn = "বক্রতলের ক্ষেত্রফল = 2πrh",
                    calculationStepsBn = listOf(
                        "ব্যাসার্ধ r = ${fmt(r)} একক এবং উচ্চতা h = ${fmt(h)} একক",
                        "বৃত্তাকার ভূমির ক্ষেত্রফল = πr² = ৩.১৪১৬ × ${fmt(r)}² = ${fmt(baseArea)} বর্গ একক",
                        "বক্রতলের ক্ষেত্রফল = ভূমির পরিধি (2πr) × উচ্চতা (h) = ${fmt(curvedArea)} বর্গ একক",
                        "সমগ্র পৃষ্ঠতলের ক্ষেত্রফল = 2πrh + 2πr² = ${fmt(totArea)} বর্গ একক",
                        "আয়তন V = πr²h = ${fmt(baseArea)} × ${fmt(h)} = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "বেলন বা সিলিন্ডারের উপরে ও নিচে ২টি সমান বৃত্তাকার তল এবং মাঝখানে ১টি সুষম বক্রতল থাকে।",
                    netExplanationBn = "বেলনের বক্রতলটি লম্বালম্বি কাটলে একটি আয়তক্ষেত্র (দৈর্ঘ্য 2πr ও প্রস্থ h) এবং উপরে-নিচে ২টি স্পর্শক বৃত্ত (πr²) পাওয়া যায়।"
                )
            }

            Solid3DShapeType.CONE -> {
                val r = a
                val h = b
                val slantL = sqrt(r * r + h * h)
                val baseArea = PI * r * r
                val curvedArea = PI * r * slantL
                val totArea = baseArea + curvedArea
                val vol = (baseArea * h) / 3.0
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = curvedArea,
                    secondaryMetricValue = slantL,
                    secondaryMetricLabelBn = "হেলানো উন্নতি l = √(r² + h²)",
                    verticesCount = 1,
                    edgesCount = 1,
                    facesCount = 2,
                    volumeFormulaBn = "আয়তন V = ⅓πr²h",
                    surfaceAreaFormulaBn = "সমগ্র তল A = πr(r + l)",
                    lateralAreaFormulaBn = "বক্রতলের ক্ষেত্রফল = πrl",
                    calculationStepsBn = listOf(
                        "ভূমির ব্যাসার্ধ r = ${fmt(r)} এবং উচ্চতা h = ${fmt(h)} একক",
                        "পিথাগোরাসের উপপাদ্য হতে হেলানো উন্নতি l = √(r² + h²) = √(${fmt(r * r)} + ${fmt(h * h)}) = ${fmt(slantL)} একক",
                        "বক্রতলের ক্ষেত্রফল = πrl = ৩.১৪১৬ × ${fmt(r)} × ${fmt(slantL)} = ${fmt(curvedArea)} বর্গ একক",
                        "সমগ্র তলের ক্ষেত্রফল = πr(r + l) = ${fmt(totArea)} বর্গ একক",
                        "আয়তন V = ⅓πr²h = ⅓ × ${fmt(baseArea)} × ${fmt(h)} = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "সমকোণী ত্রিভুজের লম্বকে অক্ষ ধরে ঘোরালে কোণক বা শঙ্কু তৈরি হয়। সমান ব্যাসার্ধ ও উচ্চতার বেলনের আয়তনের এক-তৃতীয়াংশ হলো কোণকের আয়তন।",
                    netExplanationBn = "কোণকের নেট উন্মোচন করলে ১টি বৃত্তাকার ভূমি (πr²) এবং l ব্যাসার্ধের ১টি বৃত্তকলা (Sector, চাপ 2πr, ক্ষেত্রফল πrl) পাওয়া যায়।"
                )
            }

            Solid3DShapeType.SPHERE -> {
                val r = a
                val greatCircleArea = PI * r * r
                val totArea = 4.0 * PI * r * r
                val vol = (4.0 / 3.0) * PI * r * r * r
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = totArea,
                    secondaryMetricValue = 2.0 * r,
                    secondaryMetricLabelBn = "ব্যাস (Diameter 2r)",
                    verticesCount = 0,
                    edgesCount = 0,
                    facesCount = 1,
                    volumeFormulaBn = "আয়তন V = ⁴⁄₃πr³",
                    surfaceAreaFormulaBn = "পৃষ্ঠতলের ক্ষেত্রফল A = 4πr²",
                    lateralAreaFormulaBn = "বক্রতলের ক্ষেত্রফল = 4πr²",
                    calculationStepsBn = listOf(
                        "গোলকের ব্যাসার্ধ r = ${fmt(r)} একক (ব্যাস d = ${fmt(2.0 * r)} একক)",
                        "মহাবৃত্তের ক্ষেত্রফল = πr² = ${fmt(greatCircleArea)} বর্গ একক",
                        "গোলকের পৃষ্ঠতলের ক্ষেত্রফল = 4πr² = ৪ × ${fmt(greatCircleArea)} = ${fmt(totArea)} বর্গ একক",
                        "গোলকের আয়তন V = ⁴⁄₃πr³ = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "গোলকের পৃষ্ঠতলের ক্ষেত্রফল এর মহাবৃত্তের ক্ষেত্রফলের (πr²) ঠিক ৪ গুণ!",
                    netExplanationBn = "গাণিতিক সীমাবদ্ধতা (Gauss's Theorema Egregium): গোলকের গউসীয় বক্রতা ধনাত্মক (K = 1/r² > 0), আর সমতলের বক্রতা শূন্য (K = 0)। তাই ঘনক বা প্রিজমের মতো গোলকের কোনো বিকৃতিহীন (Distortion-free) সমতল বহুভুজ নেট থাকা অসম্ভব। শিক্ষামূলকভাবে একে ৪টি মহাবৃত্তের ক্ষেত্রফল (4πr²) বা দ্রাঘিমা ফালি (Gores) দ্বারা তুলনা করা হয়।"
                )
            }

            Solid3DShapeType.TRIANGULAR_PRISM -> {
                val baseB = a
                val triH = b
                val lenL = c
                val slantSide = sqrt((baseB / 2.0) * (baseB / 2.0) + triH * triH)
                val triArea = 0.5 * baseB * triH
                val latArea = (baseB + 2.0 * slantSide) * lenL
                val totArea = 2.0 * triArea + latArea
                val vol = triArea * lenL
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = triArea,
                    secondaryMetricLabelBn = "ত্রিভুজ ভূমির ক্ষেত্রফল (½bh)",
                    verticesCount = 6,
                    edgesCount = 9,
                    facesCount = 5,
                    volumeFormulaBn = "আয়তন V = (½ × b × h) × L",
                    surfaceAreaFormulaBn = "সমগ্র তল A = bh + (b + 2s)L",
                    lateralAreaFormulaBn = "পার্শ্বতল = ত্রিভুজের পরিসীমা × L",
                    calculationStepsBn = listOf(
                        "ত্রিভুজ ভূমির ক্ষেত্রফল = ½ × ${fmt(baseB)} × ${fmt(triH)} = ${fmt(triArea)} বর্গ একক",
                        "প্রিজমের আয়তন V = ভূমির ক্ষেত্রফল × দৈর্ঘ্য L = ${fmt(triArea)} × ${fmt(lenL)} = ${fmt(vol)} ঘন একক",
                        "পার্শ্বতলের ক্ষেত্রফল = (${fmt(baseB)} + ২×${fmt(slantSide)}) × ${fmt(lenL)} = ${fmt(latArea)} বর্গ একক",
                        "সমগ্র পৃষ্ঠতলের ক্ষেত্রফল = ২×${fmt(triArea)} + ${fmt(latArea)} = ${fmt(totArea)} বর্গ একক"
                    ),
                    conceptSummaryBn = "ত্রিভুজাকার প্রিজমে ২টি সমান্তরাল ত্রিভুজাকার ভূমি এবং ৩টি আয়তাকার পার্শ্বতল (মোট ৫টি তল, ৯টি ধার ও ৬টি শীর্ষবিন্দু) থাকে।",
                    netExplanationBn = "প্রিজমের নেট উন্মোচন করলে মাঝখানে ৩টি সংযুক্ত আয়তক্ষেত্র (b×L এবং ২টি s×L) এবং দুই প্রান্তে ২টি ত্রিভুজ (½bh) পাওয়া যায়।"
                )
            }
        }
    }

    /**
     * Generates the 3D mesh faces and internal anatomical axis lines for the given solid,
     * supporting both 3D-to-2D Net Unfolding (`unfoldProgress` 0f..1f) and Volume Fill (`volumeFillFraction` 0f..1f).
     */
    fun buildSolidMesh(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double = shape.defaultP2,
        p3: Double = shape.defaultP3,
        unfoldProgress: Float = 0f,
        volumeFillFraction: Float = 0f
    ): Pair<List<Face3D>, List<Line3D>> {
        val safeP1 = sanitizeDim(p1, shape.defaultP1)
        val safeP2 = sanitizeDim(p2, shape.defaultP2)
        val safeP3 = sanitizeDim(p3, shape.defaultP3)
        val safeUnfoldInput = if (unfoldProgress.isNaN() || unfoldProgress.isInfinite()) 0f else unfoldProgress
        val safeFillInput = if (volumeFillFraction.isNaN() || volumeFillFraction.isInfinite()) 0f else volumeFillFraction
        val unfold = if (shape.supportsNetUnfold) safeUnfoldInput.coerceIn(0f, 1f) else 0f
        val fill = safeFillInput.coerceIn(0f, 1f)

        // Normalize dimensions into a balanced [-1.4 .. +1.4] model space so camera framing is always crisp
        return when (shape) {
            Solid3DShapeType.CUBE -> buildBoxAndNetMesh(
                lx = 1.6f,
                ly = 1.6f,
                lz = 1.6f,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f,
                dimLabelA = "a=${VisualMathEngine.formatNumberBn(safeP1)}",
                dimLabelB = null,
                dimLabelC = null
            )

            Solid3DShapeType.CUBOID -> {
                val maxDim = max(safeP1, max(safeP2, safeP3)).coerceAtLeast(1.0).toFloat()
                val lx = ((safeP1.toFloat() / maxDim) * 2.0f).coerceIn(0.6f, 2.1f)
                val lz = ((safeP2.toFloat() / maxDim) * 2.0f).coerceIn(0.6f, 2.1f)
                val ly = ((safeP3.toFloat() / maxDim) * 2.0f).coerceIn(0.6f, 2.1f)
                buildBoxAndNetMesh(
                    lx = lx,
                    ly = ly,
                    lz = lz,
                    unfold = unfold,
                    fill = if (unfold <= 0.02f) fill else 0f,
                    dimLabelA = "l=${VisualMathEngine.formatNumberBn(safeP1)}",
                    dimLabelB = "w=${VisualMathEngine.formatNumberBn(safeP2)}",
                    dimLabelC = "h=${VisualMathEngine.formatNumberBn(safeP3)}"
                )
            }

            Solid3DShapeType.CYLINDER -> buildCylinderAndNetMesh(
                rRaw = safeP1,
                hRaw = safeP2,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )

            Solid3DShapeType.CONE -> buildConeAndNetMesh(
                rRaw = safeP1,
                hRaw = safeP2,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )

            Solid3DShapeType.SPHERE -> buildSphereMesh(
                rRaw = safeP1,
                fill = fill
            )

            Solid3DShapeType.TRIANGULAR_PRISM -> buildTriangularPrismAndNetMesh(
                bRaw = safeP1,
                hRaw = safeP2,
                lRaw = safeP3,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )
        }
    }

    private fun buildBoxAndNetMesh(
        lx: Float,
        ly: Float,
        lz: Float,
        unfold: Float,
        fill: Float,
        dimLabelA: String,
        dimLabelB: String?,
        dimLabelC: String?
    ): Pair<List<Face3D>, List<Line3D>> {
        val hx = lx / 2f
        val hy = ly / 2f
        val hz = lz / 2f

        // 8 corners of the 3D box (y is vertical up, -hy is bottom base)
        val bFL = Vec3(-hx, -hy, hz)
        val bFR = Vec3(hx, -hy, hz)
        val bBR = Vec3(hx, -hy, -hz)
        val bBL = Vec3(-hx, -hy, -hz)
        val tFL = Vec3(-hx, hy, hz)
        val tFR = Vec3(hx, hy, hz)
        val tBR = Vec3(hx, hy, -hz)
        val tBL = Vec3(-hx, hy, -hz)

        // Outward-wound closed 3D faces
        val bottomClosed = listOf(bBL, bBR, bFR, bFL)
        val frontClosed = listOf(bFL, bFR, tFR, tFL)
        val backClosed = listOf(bBR, bBL, tBL, tBR)
        val leftClosed = listOf(bBL, bFL, tFL, tBL)
        val rightClosed = listOf(bFR, bBR, tBR, tFR)
        val topClosed = listOf(tFL, tFR, tBR, tBL)

        // True rigid 3D hinge rotations (preserves exact face dimensions & surface area at every unfold angle)
        val theta = unfold * (PI.toFloat() / 2f)

        // Front hinges around bottom-front edge (bFL -> bFR, +X axis) by +theta
        val frontVertices = frontClosed.map { pt ->
            rotateAroundAxis(pt, bFL, Vec3(1f, 0f, 0f), theta)
        }
        // Back hinges around bottom-back edge (bBR -> bBL, -X axis) by +theta
        val backVertices = backClosed.map { pt ->
            rotateAroundAxis(pt, bBR, Vec3(-1f, 0f, 0f), theta)
        }
        // Left hinges around bottom-left edge (bBL -> bFL, +Z axis) by +theta
        val leftVertices = leftClosed.map { pt ->
            rotateAroundAxis(pt, bBL, Vec3(0f, 0f, 1f), theta)
        }
        // Right hinges around bottom-right edge (bFR -> bBR, -Z axis) by +theta
        val rightVertices = rightClosed.map { pt ->
            rotateAroundAxis(pt, bFR, Vec3(0f, 0f, -1f), theta)
        }
        // Top is attached to the top edge of Front (tFL -> tFR):
        // First rotates by +theta around tFL -> tFR (+X axis) relative to Front, then rotates with Front around bFL -> bFR (+X axis) by +theta
        val topVertices = topClosed.map { pt ->
            val hingedToFront = rotateAroundAxis(pt, tFL, Vec3(1f, 0f, 0f), theta)
            rotateAroundAxis(hingedToFront, bFL, Vec3(1f, 0f, 0f), theta)
        }

        val faces = mutableListOf(
            Face3D("bottom", bottomClosed, colorRole = 0, labelBn = "ভূমি"),
            Face3D("front", frontVertices, colorRole = 0, labelBn = dimLabelA),
            Face3D("back", backVertices, colorRole = 1),
            Face3D("left", leftVertices, colorRole = 1, labelBn = dimLabelB),
            Face3D("right", rightVertices, colorRole = 2, labelBn = dimLabelC),
            Face3D("top", topVertices, colorRole = 2, labelBn = "উপরের তল")
        )

        // Add internal volume fill prism if fill > 0
        if (fill > 0.01f) {
            val fillY = -hy + ly * fill
            val fFL = Vec3(-hx * 0.97f, fillY, hz * 0.97f)
            val fFR = Vec3(hx * 0.97f, fillY, hz * 0.97f)
            val fBR = Vec3(hx * 0.97f, fillY, -hz * 0.97f)
            val fBL = Vec3(-hx * 0.97f, fillY, -hz * 0.97f)
            val fbFL = Vec3(-hx * 0.97f, -hy * 0.98f, hz * 0.97f)
            val fbFR = Vec3(hx * 0.97f, -hy * 0.98f, hz * 0.97f)
            val fbBR = Vec3(hx * 0.97f, -hy * 0.98f, -hz * 0.97f)
            val fbBL = Vec3(-hx * 0.97f, -hy * 0.98f, -hz * 0.97f)
            faces.add(Face3D("fill_top", listOf(fFL, fFR, fBR, fBL), colorRole = 3, isVolumeFill = true))
            faces.add(Face3D("fill_front", listOf(fbFL, fbFR, fFR, fFL), colorRole = 3, isVolumeFill = true))
            faces.add(Face3D("fill_right", listOf(fbFR, fbBR, fBR, fFR), colorRole = 3, isVolumeFill = true))
        }

        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(bBL, tFR, labelBn = "কর্ণ d", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    private fun buildCylinderAndNetMesh(
        rRaw: Double,
        hRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(rRaw * 2.0, hRaw).coerceAtLeast(1.0).toFloat()
        val r = ((rRaw.toFloat() / maxVal) * 1.3f).coerceIn(0.45f, 1.15f)
        val h = ((hRaw.toFloat() / maxVal) * 2.0f).coerceIn(0.8f, 2.0f)
        val hy = h / 2f
        val segments = 24
        val faces = mutableListOf<Face3D>()

        for (i in 0 until segments) {
            val a0 = (i.toFloat() / segments) * (2f * PI.toFloat()) - PI.toFloat()
            val a1 = ((i + 1).toFloat() / segments) * (2f * PI.toFloat()) - PI.toFloat()

            val x0 = r * sin(a0)
            val z0 = r * cos(a0)
            val x1 = r * sin(a1)
            val z1 = r * cos(a1)

            val closedQuad = listOf(
                Vec3(x0, -hy, z0),
                Vec3(x1, -hy, z1),
                Vec3(x1, hy, z1),
                Vec3(x0, hy, z0)
            )

            // Unrolled rectangular strip in net space (z = r tangent plane)
            val u0 = ((i.toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * r)
            val u1 = (((i + 1).toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * r)
            val netQuad = listOf(
                Vec3(u0, -hy, r),
                Vec3(u1, -hy, r),
                Vec3(u1, hy, r),
                Vec3(u0, hy, r)
            )

            faces.add(
                Face3D(
                    id = "cyl_side_$i",
                    vertices = closedQuad.indices.map { idx -> Vec3.lerp(closedQuad[idx], netQuad[idx], unfold) },
                    colorRole = if (i % 2 == 0) 0 else 1,
                    labelBn = if (i == segments / 2) "h=${VisualMathEngine.formatNumberBn(hRaw)}" else null
                )
            )
        }

        // Top & bottom circular polygons hinge-rotate rigidly around their front tangent point (0, ±hy, r)
        val hingeAngle = unfold * (PI.toFloat() / 2f)
        val topPivot = Vec3(0f, hy, r)
        val botPivot = Vec3(0f, -hy, r)

        val topVertices = (0 until segments).map { i ->
            val a = (i.toFloat() / segments) * (2f * PI.toFloat())
            val closedPt = Vec3(r * sin(a), hy, r * cos(a))
            rotateAroundAxis(closedPt, topPivot, Vec3(-1f, 0f, 0f), hingeAngle)
        }
        val botVertices = (0 until segments).reversed().map { i ->
            val a = (i.toFloat() / segments) * (2f * PI.toFloat())
            val closedPt = Vec3(r * sin(a), -hy, r * cos(a))
            rotateAroundAxis(closedPt, botPivot, Vec3(1f, 0f, 0f), hingeAngle)
        }

        faces.add(Face3D("cyl_top", topVertices, colorRole = 2, labelBn = "πr²"))
        faces.add(Face3D("cyl_bot", botVertices, colorRole = 2))

        if (fill > 0.01f) {
            val fillTopY = -hy + h * fill
            val rFill = r * 0.96f
            val fillCap = (0 until segments).map { i ->
                val a = (i.toFloat() / segments) * (2f * PI.toFloat())
                Vec3(rFill * sin(a), fillTopY, rFill * cos(a))
            }
            faces.add(Face3D("cyl_fill_cap", fillCap, colorRole = 3, isVolumeFill = true))
        }

        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hy, 0f), Vec3(0f, hy, 0f), labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, -hy, 0f), Vec3(r, -hy, 0f), labelBn = "r=${VisualMathEngine.formatNumberBn(rRaw)}", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    private fun buildConeAndNetMesh(
        rRaw: Double,
        hRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(rRaw * 2.0, hRaw).coerceAtLeast(1.0).toFloat()
        val r = ((rRaw.toFloat() / maxVal) * 1.35f).coerceIn(0.45f, 1.2f)
        val h = ((hRaw.toFloat() / maxVal) * 2.0f).coerceIn(0.8f, 2.0f)
        val hy = h / 2f
        val slant = sqrt(r * r + h * h)
        val segments = 24
        val faces = mutableListOf<Face3D>()

        val apexClosed = Vec3(0f, hy, 0f)
        val apexNet = Vec3(0f, hy, r)
        val sectorAngleTotal = ((2f * PI.toFloat() * r) / slant.coerceAtLeast(0.2f)).coerceAtMost(1.95f * PI.toFloat())

        for (i in 0 until segments) {
            val a0 = (i.toFloat() / segments) * (2f * PI.toFloat())
            val a1 = ((i + 1).toFloat() / segments) * (2f * PI.toFloat())
            val b0 = Vec3(r * sin(a0), -hy, r * cos(a0))
            val b1 = Vec3(r * sin(a1), -hy, r * cos(a1))

            val s0 = ((i.toFloat() / segments) - 0.5f) * sectorAngleTotal
            val s1 = (((i + 1).toFloat() / segments) - 0.5f) * sectorAngleTotal
            val n0 = Vec3(slant * sin(s0), hy - slant * cos(s0), r)
            val n1 = Vec3(slant * sin(s1), hy - slant * cos(s1), r)

            faces.add(
                Face3D(
                    id = "cone_side_$i",
                    vertices = listOf(
                        Vec3.lerp(b0, n0, unfold),
                        Vec3.lerp(b1, n1, unfold),
                        Vec3.lerp(apexClosed, apexNet, unfold)
                    ),
                    colorRole = if (i % 2 == 0) 0 else 1
                )
            )
        }

        // Unfold circular base so it hinges down into the z = r net plane tangent to the sector arc midpoint (0, hy - slant, r)
        val baseCenterNetY = hy - slant - r
        val botVertices = (0 until segments).reversed().map { i ->
            val a = (i.toFloat() / segments) * (2f * PI.toFloat())
            val closedPt = Vec3(r * sin(a), -hy, r * cos(a))
            val netPt = Vec3(r * sin(a), baseCenterNetY + r * cos(a), r)
            Vec3.lerp(closedPt, netPt, unfold)
        }
        faces.add(Face3D("cone_base", botVertices, colorRole = 2, labelBn = "πr²"))

        if (fill > 0.01f) {
            val fillY = -hy + h * fill
            // Exact geometric taper: at height fraction fill from base, radius is r * (1 - fill)
            val rAtFill = (r * (1f - fill)).coerceAtLeast(0.02f)
            val fillDisc = (0 until segments).map { i ->
                val a = (i.toFloat() / segments) * (2f * PI.toFloat())
                Vec3(rAtFill * sin(a), fillY, rAtFill * cos(a))
            }
            faces.add(Face3D("cone_fill_disc", fillDisc, colorRole = 3, isVolumeFill = true))
        }

        val slantVal = sqrt(rRaw * rRaw + hRaw * hRaw)
        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hy, 0f), apexClosed, labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, -hy, 0f), Vec3(r, -hy, 0f), labelBn = "r=${VisualMathEngine.formatNumberBn(rRaw)}", isInternalAxis = true),
                Line3D(Vec3(r, -hy, 0f), apexClosed, labelBn = "l=${VisualMathEngine.formatNumberBn(slantVal)}", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    private fun buildSphereMesh(
        rRaw: Double,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val r = 1.15f
        val latBands = 10
        val lonBands = 18
        val faces = mutableListOf<Face3D>()
        val fillTopY = -r + 2f * r * fill

        for (lat in 0 until latBands) {
            val theta0 = PI.toFloat() * (lat.toFloat() / latBands - 0.5f)
            val theta1 = PI.toFloat() * ((lat + 1).toFloat() / latBands - 0.5f)
            val y0 = r * sin(theta0)
            val r0 = r * cos(theta0)
            val y1 = r * sin(theta1)
            val r1 = r * cos(theta1)
            val bandMidY = (y0 + y1) * 0.5f

            for (lon in 0 until lonBands) {
                val phi0 = 2f * PI.toFloat() * (lon.toFloat() / lonBands)
                val phi1 = 2f * PI.toFloat() * ((lon + 1).toFloat() / lonBands)

                val p00 = Vec3(r0 * sin(phi0), y0, r0 * cos(phi0))
                val p10 = Vec3(r0 * sin(phi1), y0, r0 * cos(phi1))
                val p11 = Vec3(r1 * sin(phi1), y1, r1 * cos(phi1))
                val p01 = Vec3(r1 * sin(phi0), y1, r1 * cos(phi0))

                val isFilledLayer = fill > 0.01f && bandMidY <= fillTopY + 1e-4f
                faces.add(
                    Face3D(
                        id = "sph_${lat}_$lon",
                        vertices = listOf(p00, p10, p11, p01),
                        colorRole = if (isFilledLayer) 3 else if ((lat + lon) % 2 == 0) 0 else 1,
                        isVolumeFill = isFilledLayer
                    )
                )
            }
        }

        // Add internal horizontal spherical-cap liquid surface disc when partially filled
        if (fill in 0.02f..0.98f) {
            val capY = -r + 2f * r * fill
            val capR = sqrt((r * r - capY * capY).coerceAtLeast(0f)) * 0.97f
            if (capR > 0.03f) {
                val capVerts = (0 until lonBands).map { lon ->
                    val phi = 2f * PI.toFloat() * (lon.toFloat() / lonBands)
                    Vec3(capR * sin(phi), capY, capR * cos(phi))
                }
                faces.add(Face3D("sph_fill_cap", capVerts, colorRole = 3, isVolumeFill = true))
            }
        }

        val lines = listOf(
            Line3D(Vec3(0f, 0f, 0f), Vec3(r, 0f, 0f), labelBn = "r=${VisualMathEngine.formatNumberBn(rRaw)}", isInternalAxis = true),
            Line3D(Vec3(0f, -r, 0f), Vec3(0f, r, 0f), labelBn = "2r", isInternalAxis = true)
        )
        return faces to lines
    }

    private fun buildTriangularPrismAndNetMesh(
        bRaw: Double,
        hRaw: Double,
        lRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(bRaw, max(hRaw, lRaw)).coerceAtLeast(1.0).toFloat()
        val bx = ((bRaw.toFloat() / maxVal) * 1.8f).coerceIn(0.7f, 1.9f)
        val hy = ((hRaw.toFloat() / maxVal) * 1.6f).coerceIn(0.6f, 1.7f)
        val lz = ((lRaw.toFloat() / maxVal) * 1.9f).coerceIn(0.7f, 2.0f)

        val hbx = bx / 2f
        val hhy = hy / 2f
        val hlz = lz / 2f

        val fL = Vec3(-hbx, -hhy, hlz)
        val fR = Vec3(hbx, -hhy, hlz)
        val fTop = Vec3(0f, hhy, hlz)

        val bL = Vec3(-hbx, -hhy, -hlz)
        val bR = Vec3(hbx, -hhy, -hlz)
        val bTop = Vec3(0f, hhy, -hlz)

        val bottomClosed = listOf(bL, bR, fR, fL)
        val leftSlopeClosed = listOf(bL, fL, fTop, bTop)
        val rightSlopeClosed = listOf(fR, bR, bTop, fTop)
        val frontTriClosed = listOf(fL, fR, fTop)
        val backTriClosed = listOf(bR, bL, bTop)

        // Rigid hinge rotations into the y = -hhy bottom plane:
        // Left & right sloped faces make interior angle alpha = atan2(hy, hbx) with the base,
        // so unfolding flat requires rotating outward by (PI - alpha) * unfold.
        val baseAngle = kotlin.math.atan2(hy, hbx)
        val slopeUnfoldAngle = (PI.toFloat() - baseAngle) * unfold
        val triUnfoldAngle = (PI.toFloat() / 2f) * unfold

        val leftSlopeVertices = leftSlopeClosed.map { pt ->
            rotateAroundAxis(pt, bL, Vec3(0f, 0f, 1f), slopeUnfoldAngle)
        }
        val rightSlopeVertices = rightSlopeClosed.map { pt ->
            rotateAroundAxis(pt, fR, Vec3(0f, 0f, -1f), slopeUnfoldAngle)
        }
        val frontTriVertices = frontTriClosed.map { pt ->
            rotateAroundAxis(pt, fL, Vec3(1f, 0f, 0f), triUnfoldAngle)
        }
        val backTriVertices = backTriClosed.map { pt ->
            rotateAroundAxis(pt, bR, Vec3(-1f, 0f, 0f), triUnfoldAngle)
        }

        val faces = mutableListOf(
            Face3D("prism_bottom", bottomClosed, colorRole = 0, labelBn = "b=${VisualMathEngine.formatNumberBn(bRaw)}"),
            Face3D("prism_left", leftSlopeVertices, colorRole = 1),
            Face3D("prism_right", rightSlopeVertices, colorRole = 1, labelBn = "L=${VisualMathEngine.formatNumberBn(lRaw)}"),
            Face3D("prism_front", frontTriVertices, colorRole = 2),
            Face3D("prism_back", backTriVertices, colorRole = 2)
        )

        if (fill > 0.01f) {
            val fillY = -hhy + hy * fill
            // Exact linear horizontal span at height fraction fill from base: hbx * (1 - fill)
            val halfSpanAtFill = (hbx * (1f - fill)).coerceAtLeast(0.02f)
            faces.add(
                Face3D(
                    id = "prism_fill_top",
                    vertices = listOf(
                        Vec3(-halfSpanAtFill, fillY, hlz * 0.96f),
                        Vec3(halfSpanAtFill, fillY, hlz * 0.96f),
                        Vec3(halfSpanAtFill, fillY, -hlz * 0.96f),
                        Vec3(-halfSpanAtFill, fillY, -hlz * 0.96f)
                    ),
                    colorRole = 3,
                    isVolumeFill = true
                )
            )
        }

        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hhy, hlz), fTop, labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    /**
     * Rotates, depth-sorts, illuminates, and projects a 3D solid mesh into 2D Canvas screen space.
     */
    fun projectScene(
        faces: List<Face3D>,
        internalLines: List<Line3D>,
        camera: OrbitCameraState,
        canvasWidth: Float,
        canvasHeight: Float
    ): ProjectedScene2D {
        val safeW = if (canvasWidth.isNaN() || canvasWidth.isInfinite() || canvasWidth <= 1f) 320f else canvasWidth
        val safeH = if (canvasHeight.isNaN() || canvasHeight.isInfinite() || canvasHeight <= 1f) 240f else canvasHeight
        val safePitch = if (camera.pitchDeg.isNaN() || camera.pitchDeg.isInfinite()) 26f else camera.pitchDeg.coerceIn(-89f, 89f)
        val safeYaw = if (camera.yawDeg.isNaN() || camera.yawDeg.isInfinite()) -38f else camera.yawDeg
        val safeZoom = if (camera.zoom.isNaN() || camera.zoom.isInfinite()) 1.0f else camera.zoom.coerceIn(0.45f, 2.5f)
        val safePanX = if (camera.panX.isNaN() || camera.panX.isInfinite()) 0f else camera.panX
        val safePanY = if (camera.panY.isNaN() || camera.panY.isInfinite()) 0f else camera.panY

        val pitchRad = Math.toRadians(safePitch.toDouble()).toFloat()
        val yawRad = Math.toRadians(safeYaw.toDouble()).toFloat()

        val cosP = cos(pitchRad)
        val sinP = sin(pitchRad)
        val cosY = cos(yawRad)
        val sinY = sin(yawRad)

        val cx = safeW / 2f + safePanX
        val cy = safeH / 2f + safePanY
        val baseScale = min(safeW, safeH) * 0.31f * safeZoom
        val cameraDistance = 4.6f

        fun transformToView(v: Vec3): Vec3 {
            // Yaw around Y-axis
            val x1 = v.x * cosY + v.z * sinY
            val z1 = -v.x * sinY + v.z * cosY
            val y1 = v.y

            // Pitch around X-axis
            val y2 = y1 * cosP - z1 * sinP
            val z2 = y1 * sinP + z1 * cosP
            return Vec3(x1, y2, z2)
        }

        fun projectViewPoint(viewPt: Vec3): ProjectedPoint2D {
            val perspFactor = if (camera.isOrthographic) {
                1.0f
            } else {
                val denom = (cameraDistance - viewPt.z).coerceAtLeast(1.2f)
                cameraDistance / denom
            }
            val sx = cx + viewPt.x * baseScale * perspFactor
            // Invert Y because screen Y grows downward while math 3D Y grows upward
            val sy = cy - viewPt.y * baseScale * perspFactor
            return ProjectedPoint2D(sx, sy, viewPt.z)
        }

        val uniqueViewVertices = mutableListOf<ProjectedPoint2D>()

        // Compute mesh center in view space to orient outward normals reliably for closed solids
        val nonFillFaces = faces.filter { !it.isVolumeFill && it.vertices.isNotEmpty() }
        val meshCenterWorld = if (nonFillFaces.isNotEmpty()) {
            var sx = 0f
            var sy = 0f
            var sz = 0f
            var count = 0
            nonFillFaces.forEach { f ->
                f.vertices.forEach { v ->
                    sx += v.x
                    sy += v.y
                    sz += v.z
                    count++
                }
            }
            if (count > 0) Vec3(sx / count, sy / count, sz / count) else Vec3(0f, 0f, 0f)
        } else Vec3(0f, 0f, 0f)
        val meshCenterView = transformToView(meshCenterWorld)

        val projectedFaces = faces.map { face ->
            val viewVerts = face.vertices.map { transformToView(it) }
            val projVerts = viewVerts.map { projectViewPoint(it) }

            if (!face.isVolumeFill && uniqueViewVertices.size < 64) {
                projVerts.forEach { pt ->
                    if (uniqueViewVertices.none { abs(it.x - pt.x) < 3f && abs(it.y - pt.y) < 3f }) {
                        uniqueViewVertices.add(pt)
                    }
                }
            }

            val avgZ = if (viewVerts.isNotEmpty()) viewVerts.map { it.z }.average().toFloat() else 0f
            val centerView = if (viewVerts.isNotEmpty()) {
                Vec3(
                    viewVerts.map { it.x }.average().toFloat(),
                    viewVerts.map { it.y }.average().toFloat(),
                    avgZ
                )
            } else Vec3(0f, 0f, 0f)

            val rawNormal = if (viewVerts.size >= 3) {
                val e1 = viewVerts[1] - viewVerts[0]
                val e2 = viewVerts[2] - viewVerts[0]
                e1.cross(e2).normalized()
            } else {
                Vec3(0f, 0f, 1f)
            }

            val fromMeshCenter = centerView - meshCenterView
            val outwardNormal = if (!face.isVolumeFill && fromMeshCenter.length() > 1e-4f && rawNormal.dot(fromMeshCenter) < 0f) {
                Vec3(-rawNormal.x, -rawNormal.y, -rawNormal.z)
            } else {
                rawNormal
            }

            val isFrontFacing = outwardNormal.z >= -0.05f
            // Directional Lambertian shading (ambient 0.36 + diffuse 0.64 * max(0, N·L))
            val directionalNdotL = outwardNormal.dot(lightDirection).coerceIn(0f, 1f)
            val backScatter = (-outwardNormal.dot(lightDirection)).coerceIn(0f, 1f) * 0.18f
            val intensity = (0.36f + 0.64f * directionalNdotL + backScatter).coerceIn(0.35f, 1.0f)

            ProjectedFace2D(
                id = face.id,
                points = projVerts,
                averageDepthZ = avgZ,
                lightIntensity = intensity,
                isFrontFacing = isFrontFacing,
                colorRole = face.colorRole,
                labelBn = face.labelBn,
                center2D = projectViewPoint(centerView),
                isVolumeFill = face.isVolumeFill
            )
        }.sortedBy { it.averageDepthZ } // Painter's algorithm: draw farthest (smallest Z) first

        val projectedLines = internalLines.map { line ->
            val sv = transformToView(line.start)
            val ev = transformToView(line.end)
            ProjectedLine2D(
                start = projectViewPoint(sv),
                end = projectViewPoint(ev),
                labelBn = line.labelBn,
                isInternalAxis = line.isInternalAxis
            )
        }

        return ProjectedScene2D(
            facesBackToFront = projectedFaces,
            uniqueVertices = uniqueViewVertices,
            internalLines = projectedLines
        )
    }
}

