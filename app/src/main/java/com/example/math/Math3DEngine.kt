package com.example.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
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
    ),
    HEMISPHERE(
        id = "hemisphere",
        titleBn = "অর্ধগোলক (Hemisphere)",
        subtitleBn = "১টি সমতল বৃত্তাকার ভূমি ও ১টি অর্ধগোলীয় বক্রতল",
        param1LabelBn = "ব্যাসার্ধ (Radius r)",
        param2LabelBn = null,
        param3LabelBn = null,
        defaultP1 = 5.0,
        defaultP2 = 5.0,
        defaultP3 = 5.0,
        supportsNetUnfold = false
    ),
    SQUARE_PYRAMID(
        id = "square_pyramid",
        titleBn = "বর্গাকার পিরামিড (Square Pyramid)",
        subtitleBn = "১টি বর্গাকার ভূমি ও ৪টি সমদ্বিবাহু ত্রিভুজাকার পার্শ্বতল",
        param1LabelBn = "ভূমির বাহু (Base a)",
        param2LabelBn = "উচ্চতা (Height h)",
        param3LabelBn = null,
        defaultP1 = 6.0,
        defaultP2 = 7.0,
        defaultP3 = 1.0,
        supportsNetUnfold = true
    ),
    HEXAGONAL_PRISM(
        id = "hexagonal_prism",
        titleBn = "ষড়ভুজাকার প্রিজম (Hexagonal Prism)",
        subtitleBn = "২টি সুষম ষড়ভুজাকার ভূমি ও ৬টি আয়তাকার পার্শ্বতল",
        param1LabelBn = "ষড়ভুজ বাহু (Side a)",
        param2LabelBn = "উচ্চতা (Height h)",
        param3LabelBn = null,
        defaultP1 = 4.0,
        defaultP2 = 7.0,
        defaultP3 = 1.0,
        supportsNetUnfold = true
    ),
    FRUSTUM(
        id = "frustum",
        titleBn = "ছিন্ন কোণক / ফ্রাস্টাম (Conical Frustum)",
        subtitleBn = "২টি সমান্তরাল বৃত্তাকার তল ও ১টি হেলানো বক্রতল",
        param1LabelBn = "বড় ব্যাসার্ধ (Bottom R)",
        param2LabelBn = "ছোট ব্যাসার্ধ (Top r)",
        param3LabelBn = "উচ্চতা (Height h)",
        defaultP1 = 6.0,
        defaultP2 = 3.0,
        defaultP3 = 7.0,
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

enum class CrossSectionSliceMode(val id: String, val titleBn: String) {
    NONE("none", "ছেদ বন্ধ (Off)"),
    HORIZONTAL("horizontal", "অনুভূমিক ছেদ (Horizontal)"),
    VERTICAL("vertical", "উল্লম্ব ছেদ (Vertical)"),
    ANGLED("angled", "তির্যক ছেদ (Angled)")
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
    val colorRole: Int = 0, // 0=primary, 1=secondary, 2=tertiary, 3=volumeFill, 4=crossSection
    val labelBn: String? = null,
    val isVolumeFill: Boolean = false,
    val isCrossSection: Boolean = false
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
    val isVolumeFill: Boolean,
    val isCrossSection: Boolean = false
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

data class CrossSectionAnalysis(
    val shapeType: Solid3DShapeType,
    val sliceMode: CrossSectionSliceMode,
    val slicePosition: Double,
    val sectionShapeNameBn: String,
    val sectionArea: Double,
    val sectionPerimeter: Double,
    val formulaBn: String,
    val explanationBn: String,
    val contour2D: List<Pair<Float, Float>>
)

object Math3DEngine {

    private val lightDirection = Vec3(-0.45f, 0.65f, 0.62f).normalized()

    private fun sanitizeDim(value: Double, fallback: Double = 1.0): Double {
        if (value.isNaN() || value.isInfinite()) return fallback.coerceAtLeast(0.5)
        return value.coerceIn(0.5, 500.0)
    }

    fun rotateAroundAxis(point: Vec3, pivot: Vec3, axisUnit: Vec3, angleRad: Float): Vec3 {
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
     * Distinguishes linear cross-section solids (Cube, Cuboid, Cylinder, Hexagonal Prism) from non-linear
     * cross-section solids (Cone, Sphere, Hemisphere, Square Pyramid, Triangular Prism, Frustum).
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
            Solid3DShapeType.CUBE,
            Solid3DShapeType.CUBOID,
            Solid3DShapeType.CYLINDER,
            Solid3DShapeType.HEXAGONAL_PRISM -> {
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

            Solid3DShapeType.CONE,
            Solid3DShapeType.SQUARE_PYRAMID -> {
                val oneMinusP = 1.0 - p
                val baseDownFrac = (1.0 - oneMinusP * oneMinusP * oneMinusP).coerceIn(0.0, 1.0)
                val apexDownFrac = (p * p * p).coerceIn(0.0, 1.0)
                val filledVol = totalVol * baseDownFrac
                val apexDownVol = totalVol * apexDownFrac
                val vPctBn = VisualMathEngine.formatNumberBn(baseDownFrac * 100.0, 2)
                val invPctBn = VisualMathEngine.formatNumberBn(apexDownFrac * 100.0, 2)
                val shapeNameBn = if (shape == Solid3DShapeType.CONE) "কোণকের" else "পিরামিডের"
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = baseDownFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = false,
                    formulaRelationBn = "ভূমি নিচে (Frustum): V_fill/V = 1 - (1 - p)³ | শীর্ষ নিচে: V_inv/V = p³",
                    explanationBn = "$shapeNameBn প্রস্থচ্ছেদ উচ্চতার বর্গের সমানুপাতিক: $hPctBn% উচ্চতায় (ভূমি নিচে) প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক); আর শীর্ষবিন্দু নিচে থাকলে হয় $invPctBn% (${fmt(apexDownVol)} ঘন একক)।",
                    invertedApexDownVolumeFraction = apexDownFrac,
                    invertedApexDownVolume = apexDownVol
                )
            }

            Solid3DShapeType.SPHERE -> {
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

            Solid3DShapeType.HEMISPHERE -> {
                // Resting on flat base of radius r: V_fill(p) = int_0^(pr) pi(r^2 - y^2) dy = pi*r^3*(p - p^3/3).
                // Dividing by V = (2/3)*pi*r^3 gives V_fill / V = p*(3 - p^2)/2.
                val baseDownFrac = (p * (3.0 - p * p) / 2.0).coerceIn(0.0, 1.0)
                val domeDownFrac = (p * p * (3.0 - p) / 2.0).coerceIn(0.0, 1.0)
                val filledVol = totalVol * baseDownFrac
                val domeDownVol = totalVol * domeDownFrac
                val vPctBn = VisualMathEngine.formatNumberBn(baseDownFrac * 100.0, 2)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = baseDownFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = false,
                    formulaRelationBn = "সমতল ভূমি নিচে: V_fill/V = p(3 - p²)/2",
                    explanationBn = "অর্ধগোলকের প্রশস্ত বৃত্তাকার ভূমি নিচে থাকায় $hPctBn% উচ্চতায় প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক)।",
                    invertedApexDownVolumeFraction = domeDownFrac,
                    invertedApexDownVolume = domeDownVol
                )
            }

            Solid3DShapeType.TRIANGULAR_PRISM -> {
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

            Solid3DShapeType.FRUSTUM -> {
                val rBottom = sanitizeDim(p1, shape.defaultP1)
                val rTop = sanitizeDim(p2, shape.defaultP2)
                val rAtP = rBottom + (rTop - rBottom) * p
                val denom = (rBottom * rBottom + rBottom * rTop + rTop * rTop).coerceAtLeast(1e-6)
                val numer = p * (rBottom * rBottom + rBottom * rAtP + rAtP * rAtP)
                val volFrac = (numer / denom).coerceIn(0.0, 1.0)
                val filledVol = totalVol * volFrac

                // Inverted frustum (narrow top radius rTop at bottom, wide rBottom at top)
                val rInvAtP = rTop + (rBottom - rTop) * p
                val invNumer = p * (rTop * rTop + rTop * rInvAtP + rInvAtP * rInvAtP)
                val invFrac = (invNumer / denom).coerceIn(0.0, 1.0)
                val invVol = totalVol * invFrac

                val vPctBn = VisualMathEngine.formatNumberBn(volFrac * 100.0, 2)
                VolumeFillAnalysis(
                    shapeType = shape,
                    heightFillFraction = p,
                    volumeFillFraction = volFrac,
                    totalVolume = totalVol,
                    filledVolume = filledVol,
                    isLinearWithHeight = abs(rBottom - rTop) < 1e-4,
                    formulaRelationBn = "r(p) = R + (r - R)p; V_fill/V = p(R² + R·r(p) + r(p)²)/(R² + Rr + r²)",
                    explanationBn = "ছিন্ন কোণকের $hPctBn% উচ্চতায় মধ্যবর্তী ব্যাসার্ধ r(p) = ${fmt(rAtP)} একক এবং প্রকৃত পূর্ণ আয়তন $vPctBn% (${fmt(filledVol)} ঘন একক)।",
                    invertedApexDownVolumeFraction = invFrac,
                    invertedApexDownVolume = invVol
                )
            }
        }
    }

    /**
     * Computes exact analytical 2D cross-section properties when a cutting plane slices the 3D solid.
     * Supports full normalized slice position `s in [0.0, 1.0]`, including boundary and tangent cases.
     */
    fun calculateCrossSection(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double = shape.defaultP2,
        p3: Double = shape.defaultP3,
        sliceMode: CrossSectionSliceMode,
        slicePosition: Float = 0.5f
    ): CrossSectionAnalysis {
        val a = sanitizeDim(p1, shape.defaultP1)
        val b = sanitizeDim(p2, shape.defaultP2)
        val c = sanitizeDim(p3, shape.defaultP3)
        val s = if (slicePosition.isNaN() || slicePosition.isInfinite()) 0.5 else slicePosition.toDouble().coerceIn(0.0, 1.0)
        val fmt = { v: Double -> VisualMathEngine.formatNumberBn(v, 2) }

        if (sliceMode == CrossSectionSliceMode.NONE) {
            return CrossSectionAnalysis(
                shapeType = shape,
                sliceMode = sliceMode,
                slicePosition = s,
                sectionShapeNameBn = "ছেদ নিষ্ক্রিয়",
                sectionArea = 0.0,
                sectionPerimeter = 0.0,
                formulaBn = "—",
                explanationBn = "প্রস্থচ্ছেদ দেখতে অনুভূমিক, উল্লম্ব বা তির্যক ছেদ নির্বাচন করুন।",
                contour2D = emptyList()
            )
        }

        return when (shape) {
            Solid3DShapeType.CUBE -> {
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL, CrossSectionSliceMode.VERTICAL -> {
                        val area = a * a
                        val perim = 4.0 * a
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "বর্গক্ষেত্র (Square)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = a² = ${fmt(area)} বর্গ একক",
                            explanationBn = "ঘনককে যেকোনো তলের সমান্তরালে কাটলে সর্বদা a = ${fmt(a)} বাহুবিশিষ্ট একটি সর্বসম বর্গক্ষেত্র পাওয়া যায়।",
                            contour2D = listOf(-0.7f to -0.7f, 0.7f to -0.7f, 0.7f to 0.7f, -0.7f to 0.7f)
                        )
                    }
                    CrossSectionSliceMode.ANGLED -> {
                        val diag = a * sqrt(2.0)
                        val area = a * diag
                        val perim = 2.0 * (a + diag)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "কর্ণীয় আয়তক্ষেত্র (Diagonal Rectangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = a × (a√2) = √2a² = ${fmt(area)} বর্গ একক",
                            explanationBn = "ঘনককে তির্যক বা কর্ণ বরাবর ছেদ করলে দৈর্ঘ্য a√2 = ${fmt(diag)} এবং প্রস্থ a = ${fmt(a)} বিশিষ্ট একটি আয়তক্ষেত্র তৈরি হয়।",
                            contour2D = listOf(-0.82f to -0.58f, 0.82f to -0.58f, 0.82f to 0.58f, -0.82f to 0.58f)
                        )
                    }
                    else -> error("Unreachable")
                }
            }

            Solid3DShapeType.CUBOID -> {
                val (w1, w2, label) = when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> Triple(a, b, "ভূমির সমান্তরাল আয়তক্ষেত্র (l × w)")
                    CrossSectionSliceMode.VERTICAL -> Triple(a, c, "উল্লম্ব আয়তক্ষেত্র (l × h)")
                    else -> Triple(sqrt(a * a + b * b), c, "তির্যক কর্ণীয় আয়তক্ষেত্র (√(l²+w²) × h)")
                }
                val area = w1 * w2
                val perim = 2.0 * (w1 + w2)
                CrossSectionAnalysis(
                    shapeType = shape,
                    sliceMode = sliceMode,
                    slicePosition = s,
                    sectionShapeNameBn = label,
                    sectionArea = area,
                    sectionPerimeter = perim,
                    formulaBn = "A_slice = ${fmt(w1)} × ${fmt(w2)} = ${fmt(area)} বর্গ একক",
                    explanationBn = "আয়তাকার ঘনবস্তুর এই ছেদে ${fmt(w1)} ও ${fmt(w2)} বাহুবিশিষ্ট একটি আয়তক্ষেত্র উৎপন্ন হয়েছে যার ক্ষেত্রফল ${fmt(area)} বর্গ একক।",
                    contour2D = listOf(-0.8f to -0.55f, 0.8f to -0.55f, 0.8f to 0.55f, -0.8f to 0.55f)
                )
            }

            Solid3DShapeType.CYLINDER -> {
                val r = a
                val h = b
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val area = PI * r * r
                        val perim = 2.0 * PI * r
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "বৃত্ত (Circle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = πr² = ${fmt(area)} বর্গ একক",
                            explanationBn = "বেলনকে ভূমির সমান্তরালে যেকোনো উচ্চতায় কাটলে r = ${fmt(r)} ব্যাসার্ধের সমান বৃত্ত পাওয়া যায় (ক্যাভালিয়ারির নীতি)।",
                            contour2D = buildCircleContour2D(0.72f, 0.72f)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val d = abs(2.0 * s - 1.0) * r
                        val chordWidth = 2.0 * sqrt((r * r - d * d).coerceAtLeast(0.0))
                        val area = chordWidth * h
                        val perim = if (chordWidth > 1e-6) 2.0 * (chordWidth + h) else 0.0
                        val halfW = (0.65f * (chordWidth / (2.0 * r)).toFloat()).coerceAtLeast(0.05f)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = if (d < 1e-4) "অক্ষীয় আয়তক্ষেত্র (Axial Rectangle)" else "জ্যা-আয়তক্ষেত্র (Chord Rectangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "w(s) = 2√(r² - d²) = ${fmt(chordWidth)}, A = w(s) × h = ${fmt(area)} বর্গ একক",
                            explanationBn = "বেলনের অক্ষ থেকে d = ${fmt(d)} একক দূরত্বে উল্লম্বভাবে কাটলে প্রস্থ ${fmt(chordWidth)} এবং উচ্চতা h = ${fmt(h)} বিশিষ্ট আয়তক্ষেত্র পাওয়া যায়।",
                            contour2D = listOf(-halfW to -0.75f, halfW to -0.75f, halfW to 0.75f, -halfW to 0.75f)
                        )
                    }
                    else -> {
                        val semiMajor = r / cos(PI / 6.0)
                        val area = PI * r * semiMajor
                        val perim = 2.0 * PI * sqrt((r * r + semiMajor * semiMajor) / 2.0)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "উপবৃত্ত (Ellipse)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = π · r · (r sec 30°) = ${fmt(area)} বর্গ একক",
                            explanationBn = "বেলনকে ৩০° তির্যক কোণে কাটলে একটি উপবৃত্ত (Ellipse) তৈরি হয় যার ক্ষুদ্র অক্ষ 2r এবং বৃহৎ অক্ষ 2r·sec(30°)।",
                            contour2D = buildCircleContour2D(0.84f, 0.58f)
                        )
                    }
                }
            }

            Solid3DShapeType.CONE -> {
                val r = a
                val h = b
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val rSlice = (r * (1.0 - s)).coerceAtLeast(0.0)
                        val area = PI * rSlice * rSlice
                        val perim = 2.0 * PI * rSlice
                        val rad2D = (0.78f * (1.0 - s).coerceIn(0.05, 1.0)).toFloat()
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = if (rSlice <= 1e-6) "শীর্ষবিন্দু (Apex Point)" else "কনিক বৃত্ত (Conic Circle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "r(s) = r(1 - s) = ${fmt(rSlice)}, A = π·r(s)² = ${fmt(area)}",
                            explanationBn = "ভূমি থেকে ${VisualMathEngine.formatNumberBn(s * 100.0, 0)}% উচ্চতায় কোণকের প্রস্থচ্ছেদ একটি বৃত্ত যার ব্যাসার্ধ r(1 - s) = ${fmt(rSlice)} একক।",
                            contour2D = buildCircleContour2D(rad2D, rad2D)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val area = r * h
                        val slant = sqrt(r * r + h * h)
                        val perim = 2.0 * r + 2.0 * slant
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "সমদ্বিবাহু ত্রিভুজ (Isosceles Triangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = ½ × (2r) × h = r·h = ${fmt(area)} বর্গ একক",
                            explanationBn = "কোণকের শীর্ষবিন্দু ও অক্ষ বরাবর উল্লম্ব ছেদ করলে ভূমি 2r = ${fmt(2 * r)} এবং উচ্চতা h = ${fmt(h)} বিশিষ্ট সমদ্বিবাহু ত্রিভুজ পাওয়া যায়।",
                            contour2D = listOf(-0.72f to 0.72f, 0.72f to 0.72f, 0f to -0.75f)
                        )
                    }
                    else -> {
                        val rMid = r * (1.0 - s.coerceIn(0.05, 0.95) * 0.5)
                        val semiMajor = rMid / cos(PI / 6.0)
                        val area = PI * rMid * semiMajor
                        val perim = 2.0 * PI * sqrt((rMid * rMid + semiMajor * semiMajor) / 2.0)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "কনিক উপবৃত্ত (Conic Section: Ellipse)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_ellipse = π·a·b ≈ ${fmt(area)} বর্গ একক",
                            explanationBn = "আ্যাপোলোনিয়াসের কনিক তত্ত্ব অনুসারে কোণককে তির্যক সমতলে কাটলে উপবৃত্ত (Ellipse), পরাবৃত্ত (Parabola) বা অধিবৃত্ত (Hyperbola) উৎপন্ন হয়।",
                            contour2D = buildCircleContour2D(0.82f, 0.54f)
                        )
                    }
                }
            }

            Solid3DShapeType.SPHERE -> {
                val r = a
                val dNorm = abs(2.0 * s - 1.0)
                val d = dNorm * r
                val rSlice = sqrt((r * r - d * d).coerceAtLeast(0.0))
                val area = PI * rSlice * rSlice
                val perim = 2.0 * PI * rSlice
                val isGreatCircle = d < 0.08 * r
                val rad2D = (0.75f * (rSlice / r).coerceIn(0.05, 1.0)).toFloat()
                CrossSectionAnalysis(
                    shapeType = shape,
                    sliceMode = sliceMode,
                    slicePosition = s,
                    sectionShapeNameBn = when {
                        rSlice <= 1e-6 -> "স্পর্শবিন্দু (Tangent Point)"
                        isGreatCircle -> "মহাবৃত্ত (Great Circle)"
                        else -> "ক্ষুদ্র বৃত্ত (Small Circle)"
                    },
                    sectionArea = area,
                    sectionPerimeter = perim,
                    formulaBn = "r_c = √(r² - d²) = ${fmt(rSlice)}, A = πr_c² = ${fmt(area)}",
                    explanationBn = "কেন্দ্র থেকে d = ${fmt(d)} একক দূরত্বে গোলককে ছেদ করলে পিথাগোরাসের সূত্রানুসারে r_c = √(r² - d²) = ${fmt(rSlice)} ব্যাসার্ধের বৃত্ত পাওয়া যায়।",
                    contour2D = buildCircleContour2D(rad2D, rad2D)
                )
            }

            Solid3DShapeType.HEMISPHERE -> {
                val r = a
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val d = s * r
                        val rSlice = sqrt((r * r - d * d).coerceAtLeast(0.0))
                        val area = PI * rSlice * rSlice
                        val perim = 2.0 * PI * rSlice
                        val isBaseGreatCircle = s < 0.05
                        val rad2D = (0.75f * (rSlice / r).coerceIn(0.05, 1.0)).toFloat()
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = when {
                                rSlice <= 1e-6 -> "মেরু স্পর্শবিন্দু (Pole Tangent Point)"
                                isBaseGreatCircle -> "ভূমি মহাবৃত্ত (Base Great Circle)"
                                else -> "সমাক্ষ ক্ষুদ্র বৃত্ত (Parallel Circle)"
                            },
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "r(s) = r√(1 - s²) = ${fmt(rSlice)}, A = π·r(s)² = ${fmt(area)}",
                            explanationBn = "অর্ধগোলকের সমতল ভূমি থেকে h = ${fmt(d)} একক উচ্চতায় অনুভূমিক ছেদ করলে r_c = √(r² - h²) = ${fmt(rSlice)} ব্যাসার্ধের বৃত্ত পাওয়া যায়।",
                            contour2D = buildCircleContour2D(rad2D, rad2D)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val d = abs(2.0 * s - 1.0) * r
                        val rSlice = sqrt((r * r - d * d).coerceAtLeast(0.0))
                        val area = 0.5 * PI * rSlice * rSlice
                        val perim = if (rSlice > 1e-6) (PI + 2.0) * rSlice else 0.0
                        val semiPts = (0..18).map { i ->
                            val ang = PI * (i.toDouble() / 18.0)
                            val rad = (0.72f * (rSlice / r).coerceIn(0.05, 1.0)).toFloat()
                            (rad * cos(ang).toFloat()) to (-rad * sin(ang).toFloat() + 0.35f)
                        }
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "অর্ধবৃত্ত (Semicircle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_semi = ½πr_c² = ${fmt(area)}, P = (π + 2)r_c = ${fmt(perim)}",
                            explanationBn = "অর্ধগোলককে ভূমির লম্ব বরাবর ছেদ করলে r_c = ${fmt(rSlice)} ব্যাসার্ধের একটি অর্ধবৃত্ত (ক্ষেত্রফল ½πr_c² = ${fmt(area)}) উৎপন্ন হয়।",
                            contour2D = semiPts
                        )
                    }
                    else -> {
                        val d = (s * 0.7) * r
                        val rSlice = sqrt((r * r - d * d).coerceAtLeast(0.04))
                        val area = PI * rSlice * rSlice
                        val perim = 2.0 * PI * rSlice
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "তির্যক বৃত্তীয় ছেদ (Tilted Circular Cap Section)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "r_c = √(r² - d²) = ${fmt(rSlice)}, A = πr_c² = ${fmt(area)}",
                            explanationBn = "অর্ধগোলকের তির্যক সমতল ছেদে r_c = ${fmt(rSlice)} ব্যাসার্ধের বৃত্তাকার প্রস্থচ্ছেদ পাওয়া যায়।",
                            contour2D = buildCircleContour2D(0.75f, 0.65f)
                        )
                    }
                }
            }

            Solid3DShapeType.TRIANGULAR_PRISM -> {
                val baseB = a
                val triH = b
                val lenL = c
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val widthAtS = (baseB * (1.0 - s)).coerceAtLeast(0.0)
                        val area = widthAtS * lenL
                        val perim = if (widthAtS > 1e-6) 2.0 * (widthAtS + lenL) else 2.0 * lenL
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "আয়তক্ষেত্র (Horizontal Rectangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "b(s) = b(1 - s) = ${fmt(widthAtS)}, A = b(s) × L = ${fmt(area)}",
                            explanationBn = "ভূমির সমান্তরালে কাটলে প্রস্থ b(1 - s) = ${fmt(widthAtS)} এবং দৈর্ঘ্য L = ${fmt(lenL)} বিশিষ্ট আয়তক্ষেত্র পাওয়া যায়।",
                            contour2D = listOf(-0.75f to -0.5f, 0.75f to -0.5f, 0.75f to 0.5f, -0.75f to 0.5f)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val area = 0.5 * baseB * triH
                        val slant = sqrt((baseB / 2.0) * (baseB / 2.0) + triH * triH)
                        val perim = baseB + 2.0 * slant
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "ত্রিভুজ (Triangular Cross-Section)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = ½ × b × h = ${fmt(area)} বর্গ একক",
                            explanationBn = "ত্রিভুজাকার প্রিজমের প্রান্ততলের সমান্তরালে ছেদ করলে ভূমির সর্বসম ত্রিভুজ (ক্ষেত্রফল ${fmt(area)} বর্গ একক) পাওয়া যায়।",
                            contour2D = listOf(-0.72f to 0.68f, 0.72f to 0.68f, 0f to -0.68f)
                        )
                    }
                    else -> {
                        val hAngled = triH / cos(PI / 6.0)
                        val area = 0.5 * baseB * hAngled
                        val slant = sqrt((baseB / 2.0) * (baseB / 2.0) + hAngled * hAngled)
                        val perim = baseB + 2.0 * slant
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "তির্যক প্রসারিত ত্রিভুজ (Tilted Triangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "h' = h·sec(30°) = ${fmt(hAngled)}, A = ½·b·h' = ${fmt(area)}",
                            explanationBn = "প্রিজমকে ৩০° তির্যক কোণে ছেদ করলে ত্রিভুজের কার্যকর উচ্চতা h·sec(30°) = ${fmt(hAngled)} এককে উন্নীত হয়।",
                            contour2D = listOf(-0.72f to 0.74f, 0.72f to 0.74f, 0f to -0.74f)
                        )
                    }
                }
            }

            Solid3DShapeType.SQUARE_PYRAMID -> {
                val baseA = a
                val h = b
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val sideAtS = (baseA * (1.0 - s)).coerceAtLeast(0.0)
                        val area = sideAtS * sideAtS
                        val perim = 4.0 * sideAtS
                        val halfSide = (0.65f * (1.0 - s).coerceIn(0.05, 1.0)).toFloat()
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = if (sideAtS <= 1e-6) "শীর্ষবিন্দু (Apex Point)" else "সদৃশ বর্গক্ষেত্র (Scaled Square)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "a(s) = a(1 - s) = ${fmt(sideAtS)}, A = a(s)² = ${fmt(area)}",
                            explanationBn = "পিরামিডের ভূমির সমান্তরালে s = ${VisualMathEngine.formatNumberBn(s * 100.0, 0)}% উচ্চতায় ছেদ করলে বাহু (1-s) অনুপাতে কমে ${fmt(sideAtS)} এককের বর্গক্ষেত্র তৈরি করে।",
                            contour2D = listOf(-halfSide to -halfSide, halfSide to -halfSide, halfSide to halfSide, -halfSide to halfSide)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val area = 0.5 * baseA * h
                        val slant = sqrt((baseA / 2.0) * (baseA / 2.0) + h * h)
                        val perim = baseA + 2.0 * slant
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "সমদ্বিবাহু ত্রিভুজ (Axial Triangle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = ½ × a × h = ${fmt(area)} বর্গ একক",
                            explanationBn = "পিরামিডের শীর্ষবিন্দু ও ভূমির কেন্দ্র বরাবর উল্লম্ব ছেদে ভূমি a = ${fmt(baseA)} ও উচ্চতা h = ${fmt(h)} বিশিষ্ট ত্রিভুজ পাওয়া যায়।",
                            contour2D = listOf(-0.72f to 0.72f, 0.72f to 0.72f, 0f to -0.75f)
                        )
                    }
                    else -> {
                        val sMid = s.coerceIn(0.1, 0.9)
                        val wBot = baseA * (1.0 - sMid * 0.6)
                        val wTop = baseA * (1.0 - (sMid * 0.6 + 0.35).coerceAtMost(0.95))
                        val hTrap = (h * 0.35) / cos(PI / 6.0)
                        val area = 0.5 * (wBot + wTop) * hTrap
                        val leg = sqrt(((wBot - wTop) / 2.0) * ((wBot - wTop) / 2.0) + hTrap * hTrap)
                        val perim = wBot + wTop + 2.0 * leg
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "সমদ্বিবাহু ট্রাপিজিয়াম (Tilted Trapezoid)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_trap = ½(b₁ + b₂)h' = ${fmt(area)} বর্গ একক",
                            explanationBn = "বর্গাকার পিরামিডকে তির্যক সমতলে ছেদ করলে অসমান সমান্তরাল বাহুবিশিষ্ট একটি সমদ্বিবাহু ট্রাপিজিয়াম উৎপন্ন হয়।",
                            contour2D = listOf(-0.74f to 0.65f, 0.74f to 0.65f, 0.44f to -0.65f, -0.44f to -0.65f)
                        )
                    }
                }
            }

            Solid3DShapeType.HEXAGONAL_PRISM -> {
                val sideA = a
                val h = b
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val area = 1.5 * sqrt(3.0) * sideA * sideA
                        val perim = 6.0 * sideA
                        val hexPts = (0 until 6).map { i ->
                            val ang = i * (PI / 3.0)
                            (0.75f * cos(ang).toFloat()) to (0.75f * sin(ang).toFloat())
                        }
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "সুষম ষড়ভুজ (Regular Hexagon)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_hex = (3√3 / 2)a² = ${fmt(area)} বর্গ একক",
                            explanationBn = "ষড়ভুজাকার প্রিজমকে ভূমির সমান্তরালে কাটলে ৬টি সমবাহু ত্রিভুজের সমষ্টি (3√3/2)a² = ${fmt(area)} বর্গ এককের সুষম ষড়ভুজ পাওয়া যায়।",
                            contour2D = hexPts
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val diagWidth = 2.0 * sideA
                        val area = diagWidth * h
                        val perim = 2.0 * (diagWidth + h)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "প্রধান কর্ণীয় আয়তক্ষেত্র (2a × h)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_slice = 2a × h = ${fmt(diagWidth)} × ${fmt(h)} = ${fmt(area)} বর্গ একক",
                            explanationBn = "ষড়ভুজের বিপরীত শীর্ষবিন্দুর দূরত্ব 2a = ${fmt(diagWidth)} হওয়ায় অক্ষীয় উল্লম্ব ছেদে 2a × h আয়তক্ষেত্র পাওয়া যায়।",
                            contour2D = listOf(-0.68f to -0.72f, 0.68f to -0.72f, 0.68f to 0.72f, -0.68f to 0.72f)
                        )
                    }
                    else -> {
                        val baseHexArea = 1.5 * sqrt(3.0) * sideA * sideA
                        val area = baseHexArea / cos(PI / 6.0)
                        val slantEdge = sideA * sqrt(1.0 + 0.75 * kotlin.math.tan(PI / 6.0) * kotlin.math.tan(PI / 6.0))
                        val perim = 2.0 * sideA + 4.0 * slantEdge
                        val hexPts = (0 until 6).map { i ->
                            val ang = i * (PI / 3.0)
                            (0.84f * cos(ang).toFloat()) to (0.66f * sin(ang).toFloat())
                        }
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "তির্যক ষড়ভুজ (Stretched Hexagon)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_angled = A_hex · sec(30°) = 3a² = ${fmt(area)} বর্গ একক",
                            explanationBn = "ষড়ভুজাকার প্রিজমকে ৩০° তির্যক কোণে কাটলে ক্ষেত্রফল sec(30°) গুণ বৃদ্ধি পেয়ে 3a² = ${fmt(area)} বর্গ একক হয়।",
                            contour2D = hexPts
                        )
                    }
                }
            }

            Solid3DShapeType.FRUSTUM -> {
                val rBottom = a
                val rTop = b
                val h = c
                when (sliceMode) {
                    CrossSectionSliceMode.HORIZONTAL -> {
                        val rSlice = rBottom + (rTop - rBottom) * s
                        val area = PI * rSlice * rSlice
                        val perim = 2.0 * PI * rSlice
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "মধ্যবর্তী বৃত্ত (Intermediate Circle)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "r(s) = R + (r - R)s = ${fmt(rSlice)}, A = π·r(s)² = ${fmt(area)}",
                            explanationBn = "ছিন্ন কোণকের s = ${VisualMathEngine.formatNumberBn(s * 100.0, 0)}% উচ্চতায় ব্যাসার্ধ রৈখিকভাবে পরিবর্তিত হয়ে r(s) = ${fmt(rSlice)} একক হয়।",
                            contour2D = buildCircleContour2D(0.72f, 0.72f)
                        )
                    }
                    CrossSectionSliceMode.VERTICAL -> {
                        val area = (rBottom + rTop) * h
                        val slant = sqrt((rBottom - rTop) * (rBottom - rTop) + h * h)
                        val perim = 2.0 * rBottom + 2.0 * rTop + 2.0 * slant
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "সমদ্বিবাহু ট্রাপিজিয়াম (Isosceles Trapezoid)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_trap = ½(2R + 2r)h = (R + r)h = ${fmt(area)} বর্গ একক",
                            explanationBn = "ছিন্ন কোণককে অক্ষ বরাবর উল্লম্বভাবে কাটলে সমান্তরাল বাহু 2R = ${fmt(2 * rBottom)} ও 2r = ${fmt(2 * rTop)} বিশিষ্ট সমদ্বিবাহু ট্রাপিজিয়াম পাওয়া যায়।",
                            contour2D = listOf(-0.78f to 0.68f, 0.78f to 0.68f, 0.45f to -0.68f, -0.45f to -0.68f)
                        )
                    }
                    else -> {
                        val rSlice = rBottom + (rTop - rBottom) * s
                        val semiMajor = rSlice / cos(PI / 6.0)
                        val area = PI * rSlice * semiMajor
                        val perim = 2.0 * PI * sqrt((rSlice * rSlice + semiMajor * semiMajor) / 2.0)
                        CrossSectionAnalysis(
                            shapeType = shape,
                            sliceMode = sliceMode,
                            slicePosition = s,
                            sectionShapeNameBn = "তির্যক উপবৃত্ত (Tilted Ellipse)",
                            sectionArea = area,
                            sectionPerimeter = perim,
                            formulaBn = "A_ellipse = π · r(s) · r(s)sec(30°) = ${fmt(area)} বর্গ একক",
                            explanationBn = "ছিন্ন কোণককে ৩০° তির্যক কোণে ছেদ করলে r(s) = ${fmt(rSlice)} ক্ষুদ্র ব্যাসার্ধবিশিষ্ট একটি উপবৃত্ত পাওয়া যায়।",
                            contour2D = buildCircleContour2D(0.82f, 0.62f)
                        )
                    }
                }
            }
        }
    }

    private fun buildCircleContour2D(rx: Float, ry: Float, segments: Int = 28): List<Pair<Float, Float>> {
        return (0 until segments).map { i ->
            val a = (i.toDouble() / segments) * 2.0 * PI
            (rx * cos(a).toFloat()) to (ry * sin(a).toFloat())
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

            Solid3DShapeType.HEMISPHERE -> {
                val r = a
                val baseArea = PI * r * r
                val curvedArea = 2.0 * baseArea
                val totArea = 3.0 * baseArea
                val vol = (2.0 / 3.0) * PI * r * r * r
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = curvedArea,
                    secondaryMetricValue = baseArea,
                    secondaryMetricLabelBn = "সমতল ভূমির ক্ষেত্রফল (πr²)",
                    verticesCount = 0,
                    edgesCount = 1,
                    facesCount = 2,
                    volumeFormulaBn = "আয়তন V = ⅔πr³",
                    surfaceAreaFormulaBn = "সমগ্র তল A = 3πr²",
                    lateralAreaFormulaBn = "বক্রতলের ক্ষেত্রফল = 2πr²",
                    calculationStepsBn = listOf(
                        "অর্ধগোলকের ব্যাসার্ধ r = ${fmt(r)} একক",
                        "সমতল বৃত্তাকার ভূমির ক্ষেত্রফল = πr² = ${fmt(baseArea)} বর্গ একক",
                        "বক্রতলের ক্ষেত্রফল (গম্বুজ অংশ) = 2πr² = ২ × ${fmt(baseArea)} = ${fmt(curvedArea)} বর্গ একক",
                        "সমগ্র তলের ক্ষেত্রফল = 2πr² + πr² = 3πr² = ${fmt(totArea)} বর্গ একক",
                        "আয়তন V = ⅔πr³ = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "পূর্ণ গোলককে কেন্দ্রগামী সমতলে সমদ্বিখণ্ডিত করলে অর্ধগোলক তৈরি হয়। এর বক্রতল 2πr² এবং সমতল বৃত্তাকার ভূমি πr² মিলে মোট ক্ষেত্রফল 3πr²।",
                    netExplanationBn = "গাণিতিক সীমাবদ্ধতা (Theorema Egregium): অর্ধগোলকের সমতল ভূমিটি (πr²) একটি আদর্শ বৃত্ত হলেও এর গম্বুজাকার বক্রতলের (2πr²) গউসীয় বক্রতা ধনাত্মক (K = 1/r² > 0) হওয়ায় তা বিকৃতিহীনভাবে সমতলে মেলে ধরা যায় না।"
                )
            }

            Solid3DShapeType.SQUARE_PYRAMID -> {
                val baseA = a
                val h = b
                val slantL = sqrt((baseA / 2.0) * (baseA / 2.0) + h * h)
                val baseArea = baseA * baseA
                val latArea = 2.0 * baseA * slantL
                val totArea = baseArea + latArea
                val vol = (baseArea * h) / 3.0
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = slantL,
                    secondaryMetricLabelBn = "হেলানো উন্নতি l = √((a/2)² + h²)",
                    verticesCount = 5,
                    edgesCount = 8,
                    facesCount = 5,
                    volumeFormulaBn = "আয়তন V = ⅓a²h",
                    surfaceAreaFormulaBn = "সমগ্র তল A = a² + 2al",
                    lateralAreaFormulaBn = "পার্শ্বতলের ক্ষেত্রফল = 2al",
                    calculationStepsBn = listOf(
                        "বর্গাকার ভূমির বাহু a = ${fmt(baseA)} একক এবং লম্ব উচ্চতা h = ${fmt(h)} একক",
                        "ভূমির ক্ষেত্রফল = a² = ${fmt(baseArea)} বর্গ একক",
                        "হেলানো উন্নতি l = √((a/2)² + h²) = √(${fmt((baseA / 2.0) * (baseA / 2.0))} + ${fmt(h * h)}) = ${fmt(slantL)} একক",
                        "৪টি ত্রিভুজাকার পার্শ্বতলের ক্ষেত্রফল = 4 × (½ × a × l) = 2al = ${fmt(latArea)} বর্গ একক",
                        "সমগ্র তলের ক্ষেত্রফল = a² + 2al = ${fmt(totArea)} বর্গ একক এবং আয়তন V = ⅓a²h = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "বর্গাকার পিরামিডে ১টি বর্গাকার ভূমি এবং ৪টি সমদ্বিবাহু ত্রিভুজাকার পার্শ্বতল শীর্ষবিন্দুতে (Apex) মিলিত হয় (V - E + F = ৫ - ৮ + ৫ = ২)।",
                    netExplanationBn = "পিরামিডের নেট উন্মোচন করলে মাঝখানে ১টি বর্গক্ষেত্র (a²) এবং চার বাহুতে কব্জা বরাবর ৪টি সমদ্বিবাহু ত্রিভুজ (প্রতিটি ½al) তারকার মতো ছড়িয়ে পড়ে।"
                )
            }

            Solid3DShapeType.HEXAGONAL_PRISM -> {
                val sideA = a
                val h = b
                val baseArea = 1.5 * sqrt(3.0) * sideA * sideA
                val latArea = 6.0 * sideA * h
                val totArea = 2.0 * baseArea + latArea
                val vol = baseArea * h
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = baseArea,
                    secondaryMetricLabelBn = "ষড়ভুজ ভূমির ক্ষেত্রফল (3√3/2)a²",
                    verticesCount = 12,
                    edgesCount = 18,
                    facesCount = 8,
                    volumeFormulaBn = "আয়তন V = (3√3/2)a²h",
                    surfaceAreaFormulaBn = "সমগ্র তল A = 3√3a² + 6ah",
                    lateralAreaFormulaBn = "পার্শ্বতল = 6ah",
                    calculationStepsBn = listOf(
                        "সুষম ষড়ভুজের বাহু a = ${fmt(sideA)} একক এবং উচ্চতা h = ${fmt(h)} একক",
                        "ষড়ভুজ ভূমির ক্ষেত্রফল = ६ × (√3/4)a² = (3√3/2)a² = ${fmt(baseArea)} বর্গ একক",
                        "৬টি আয়তাকার পার্শ্বতলের ক্ষেত্রফল = 6ah = ৬ × ${fmt(sideA)} × ${fmt(h)} = ${fmt(latArea)} বর্গ একক",
                        "সমগ্র পৃষ্ঠতলের ক্ষেত্রফল = 2 × ${fmt(baseArea)} + ${fmt(latArea)} = ${fmt(totArea)} বর্গ একক",
                        "আয়তন V = ভূমির ক্ষেত্রফল × উচ্চতা = ${fmt(baseArea)} × ${fmt(h)} = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "সুষম ষড়ভুজাকার প্রিজমে ২টি ষড়ভুজ ভূমি ও ৬টি আয়তাকার পার্শ্বতল (মোট ৮টি তল, ১৮টি ধার ও ১২টি শীর্ষবিন্দু; অয়লার: ১২ - ১৮ + ৮ = ২) রয়েছে।",
                    netExplanationBn = "নেট উন্মোচন করলে ৬টি আয়তাকার পার্শ্বতল (প্রতিটি a×h) ৯০° ঘুরে সমতলে ছড়িয়ে পড়ে এবং উপরে-নিচে ২টি সুষম ষড়ভুজ যুক্ত থাকে।"
                )
            }

            Solid3DShapeType.FRUSTUM -> {
                val rBottom = a
                val rTop = b
                val h = c
                val slantL = sqrt((rBottom - rTop) * (rBottom - rTop) + h * h)
                val botArea = PI * rBottom * rBottom
                val topArea = PI * rTop * rTop
                val latArea = PI * (rBottom + rTop) * slantL
                val totArea = latArea + botArea + topArea
                val vol = (PI * h / 3.0) * (rBottom * rBottom + rBottom * rTop + rTop * rTop)
                Solid3DCalculationResult(
                    shapeType = shape,
                    volume = vol,
                    totalSurfaceArea = totArea,
                    lateralSurfaceArea = latArea,
                    secondaryMetricValue = slantL,
                    secondaryMetricLabelBn = "হেলানো উন্নতি l = √((R - r)² + h²)",
                    verticesCount = 0,
                    edgesCount = 2,
                    facesCount = 3,
                    volumeFormulaBn = "আয়তন V = ⅓πh(R² + Rr + r²)",
                    surfaceAreaFormulaBn = "সমগ্র তল A = π(R + r)l + π(R² + r²)",
                    lateralAreaFormulaBn = "বক্রতল = π(R + r)l",
                    calculationStepsBn = listOf(
                        "নিচের ব্যাসার্ধ R = ${fmt(rBottom)}, উপরের ব্যাসার্ধ r = ${fmt(rTop)}, উচ্চতা h = ${fmt(h)} একক",
                        "হেলানো উন্নতি l = √((R - r)² + h²) = √(${fmt((rBottom - rTop) * (rBottom - rTop))} + ${fmt(h * h)}) = ${fmt(slantL)} একক",
                        "বক্রতলের ক্ষেত্রফল = π(R + r)l = ৩.১৪১৬ × ${fmt(rBottom + rTop)} × ${fmt(slantL)} = ${fmt(latArea)} বর্গ একক",
                        "সমগ্র তলের ক্ষেত্রফল = ${fmt(latArea)} + ${fmt(botArea)} + ${fmt(topArea)} = ${fmt(totArea)} বর্গ একক",
                        "আয়তন V = ⅓πh(R² + Rr + r²) = ${fmt(vol)} ঘন একক"
                    ),
                    conceptSummaryBn = "কোণকের শীর্ষভাগ ভূমির সমান্তরাল সমতলে কেটে সরিয়ে নিলে ছিন্ন কোণক বা ফ্রাস্টাম (যেমন বালতি বা গ্লাস) তৈরি হয়।",
                    netExplanationBn = "ছিন্ন কোণকের নেট উন্মোচন করলে ১টি বলয়-বৃত্তকলা (Annular Sector) এবং উপরে-নিচে πr² ও πR² ক্ষেত্রফলের ২টি বৃত্তাকার তল পাওয়া যায়।"
                )
            }
        }
    }

    /**
     * Generates the 3D mesh faces and internal anatomical axis lines for the given solid,
     * supporting 3D-to-2D Net Unfolding (`unfoldProgress` 0f..1f), Volume Fill (`volumeFillFraction` 0f..1f),
     * and optional 3D Plane Cross-Section Slicing (`sliceMode`, `slicePosition`).
     */
    fun buildSolidMesh(
        shape: Solid3DShapeType,
        p1: Double,
        p2: Double = shape.defaultP2,
        p3: Double = shape.defaultP3,
        unfoldProgress: Float = 0f,
        volumeFillFraction: Float = 0f,
        sliceMode: CrossSectionSliceMode = CrossSectionSliceMode.NONE,
        slicePosition: Float = 0.5f
    ): Pair<List<Face3D>, List<Line3D>> {
        val safeP1 = sanitizeDim(p1, shape.defaultP1)
        val safeP2 = sanitizeDim(p2, shape.defaultP2)
        val safeP3 = sanitizeDim(p3, shape.defaultP3)
        val safeUnfoldInput = if (unfoldProgress.isNaN() || unfoldProgress.isInfinite()) 0f else unfoldProgress
        val safeFillInput = if (volumeFillFraction.isNaN() || volumeFillFraction.isInfinite()) 0f else volumeFillFraction
        val unfold = if (shape.supportsNetUnfold) safeUnfoldInput.coerceIn(0f, 1f) else 0f
        val fill = safeFillInput.coerceIn(0f, 1f)
        val safeSlicePos = if (slicePosition.isNaN() || slicePosition.isInfinite()) 0.5f else slicePosition.coerceIn(0.1f, 0.9f)

        val (baseFaces, baseLines) = when (shape) {
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

            Solid3DShapeType.HEMISPHERE -> buildHemisphereMesh(
                rRaw = safeP1,
                fill = fill
            )

            Solid3DShapeType.SQUARE_PYRAMID -> buildSquarePyramidAndNetMesh(
                aRaw = safeP1,
                hRaw = safeP2,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )

            Solid3DShapeType.HEXAGONAL_PRISM -> buildHexagonalPrismAndNetMesh(
                aRaw = safeP1,
                hRaw = safeP2,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )

            Solid3DShapeType.FRUSTUM -> buildFrustumAndNetMesh(
                rBotRaw = safeP1,
                rTopRaw = safeP2,
                hRaw = safeP3,
                unfold = unfold,
                fill = if (unfold <= 0.02f) fill else 0f
            )
        }

        if (sliceMode == CrossSectionSliceMode.NONE || unfold > 0.05f) {
            return baseFaces to baseLines
        }

        val sliceFace = buildCrossSectionPlaneFace3D(shape, sliceMode, safeSlicePos)
        val combinedFaces = if (sliceFace != null) baseFaces + sliceFace else baseFaces
        return combinedFaces to baseLines
    }

    private fun buildCrossSectionPlaneFace3D(
        shape: Solid3DShapeType,
        sliceMode: CrossSectionSliceMode,
        s: Float
    ): Face3D? {
        val yLevel = -0.8f + 1.6f * s
        return when (sliceMode) {
            CrossSectionSliceMode.NONE -> null
            CrossSectionSliceMode.HORIZONTAL -> {
                val scale = when (shape) {
                    Solid3DShapeType.CONE, Solid3DShapeType.SQUARE_PYRAMID -> (1f - s * 0.85f).coerceAtLeast(0.18f)
                    Solid3DShapeType.FRUSTUM -> (1f - s * 0.42f).coerceAtLeast(0.35f)
                    Solid3DShapeType.SPHERE -> sqrt((1f - (s * 2f - 1f) * (s * 2f - 1f)).coerceAtLeast(0.1f))
                    Solid3DShapeType.HEMISPHERE -> sqrt((1f - s * s).coerceAtLeast(0.1f))
                    else -> 0.95f
                }
                val isCircular = shape in setOf(
                    Solid3DShapeType.CYLINDER,
                    Solid3DShapeType.CONE,
                    Solid3DShapeType.SPHERE,
                    Solid3DShapeType.HEMISPHERE,
                    Solid3DShapeType.FRUSTUM
                )
                val verts = if (isCircular) {
                    val segs = 24
                    val rad = 1.05f * scale
                    (0 until segs).map { i ->
                        val a = (i.toFloat() / segs) * 2f * PI.toFloat()
                        Vec3(rad * sin(a), yLevel, rad * cos(a))
                    }
                } else if (shape == Solid3DShapeType.HEXAGONAL_PRISM) {
                    val rad = 1.05f
                    (0 until 6).map { i ->
                        val a = (i.toFloat() / 6f) * 2f * PI.toFloat()
                        Vec3(rad * sin(a), yLevel, rad * cos(a))
                    }
                } else {
                    val hw = 1.02f * scale
                    listOf(
                        Vec3(-hw, yLevel, hw),
                        Vec3(hw, yLevel, hw),
                        Vec3(hw, yLevel, -hw),
                        Vec3(-hw, yLevel, -hw)
                    )
                }
                Face3D(
                    id = "cross_section_horiz",
                    vertices = verts,
                    colorRole = 4,
                    labelBn = "ছেদতল (Cross-Section)",
                    isCrossSection = true
                )
            }
            CrossSectionSliceMode.VERTICAL -> {
                val zOffset = (s - 0.5f) * 0.6f
                val verts = when (shape) {
                    Solid3DShapeType.CONE, Solid3DShapeType.SQUARE_PYRAMID, Solid3DShapeType.TRIANGULAR_PRISM -> listOf(
                        Vec3(-1.0f, -0.85f, zOffset),
                        Vec3(1.0f, -0.85f, zOffset),
                        Vec3(0f, 0.90f, zOffset)
                    )
                    Solid3DShapeType.FRUSTUM -> listOf(
                        Vec3(-1.05f, -0.85f, zOffset),
                        Vec3(1.05f, -0.85f, zOffset),
                        Vec3(0.62f, 0.85f, zOffset),
                        Vec3(-0.62f, 0.85f, zOffset)
                    )
                    else -> listOf(
                        Vec3(-1.02f, -0.88f, zOffset),
                        Vec3(1.02f, -0.88f, zOffset),
                        Vec3(1.02f, 0.88f, zOffset),
                        Vec3(-1.02f, 0.88f, zOffset)
                    )
                }
                Face3D(
                    id = "cross_section_vert",
                    vertices = verts,
                    colorRole = 4,
                    labelBn = "উল্লম্ব ছেদতল",
                    isCrossSection = true
                )
            }
            CrossSectionSliceMode.ANGLED -> {
                val tilt = 0.42f
                val segs = 20
                val verts = (0 until segs).map { i ->
                    val a = (i.toFloat() / segs) * 2f * PI.toFloat()
                    val x = 0.95f * sin(a)
                    val z = 0.95f * cos(a)
                    val y = (yLevel * 0.5f) + x * tilt
                    Vec3(x, y, z)
                }
                Face3D(
                    id = "cross_section_angled",
                    vertices = verts,
                    colorRole = 4,
                    labelBn = "তির্যক ছেদতল",
                    isCrossSection = true
                )
            }
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

        val bFL = Vec3(-hx, -hy, hz)
        val bFR = Vec3(hx, -hy, hz)
        val bBR = Vec3(hx, -hy, -hz)
        val bBL = Vec3(-hx, -hy, -hz)
        val tFL = Vec3(-hx, hy, hz)
        val tFR = Vec3(hx, hy, hz)
        val tBR = Vec3(hx, hy, -hz)
        val tBL = Vec3(-hx, hy, -hz)

        val bottomClosed = listOf(bBL, bBR, bFR, bFL)
        val frontClosed = listOf(bFL, bFR, tFR, tFL)
        val backClosed = listOf(bBR, bBL, tBL, tBR)
        val leftClosed = listOf(bBL, bFL, tFL, tBL)
        val rightClosed = listOf(bFR, bBR, tBR, tFR)
        val topClosed = listOf(tFL, tFR, tBR, tBL)

        val theta = unfold * (PI.toFloat() / 2f)

        val frontVertices = frontClosed.map { pt ->
            rotateAroundAxis(pt, bFL, Vec3(1f, 0f, 0f), theta)
        }
        val backVertices = backClosed.map { pt ->
            rotateAroundAxis(pt, bBR, Vec3(-1f, 0f, 0f), theta)
        }
        val leftVertices = leftClosed.map { pt ->
            rotateAroundAxis(pt, bBL, Vec3(0f, 0f, 1f), theta)
        }
        val rightVertices = rightClosed.map { pt ->
            rotateAroundAxis(pt, bFR, Vec3(0f, 0f, -1f), theta)
        }
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
        val sectorAngleTotal = ((2f * PI.toFloat() * r) / slant.coerceAtLeast(0.01f)).coerceAtMost(1.999f * PI.toFloat())

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

    private fun buildHemisphereMesh(
        rRaw: Double,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val r = 1.25f
        val baseY = -0.55f
        val latBands = 7
        val lonBands = 20
        val faces = mutableListOf<Face3D>()
        val fillTopY = baseY + r * fill

        for (lat in 0 until latBands) {
            val theta0 = (PI.toFloat() / 2f) * (lat.toFloat() / latBands)
            val theta1 = (PI.toFloat() / 2f) * ((lat + 1).toFloat() / latBands)
            val y0 = baseY + r * sin(theta0)
            val r0 = r * cos(theta0)
            val y1 = baseY + r * sin(theta1)
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
                        id = "hemi_${lat}_$lon",
                        vertices = listOf(p00, p10, p11, p01),
                        colorRole = if (isFilledLayer) 3 else if ((lat + lon) % 2 == 0) 0 else 1,
                        isVolumeFill = isFilledLayer
                    )
                )
            }
        }

        // Flat circular base of the hemisphere (area = pi * r^2)
        val baseVerts = (0 until lonBands).reversed().map { lon ->
            val phi = 2f * PI.toFloat() * (lon.toFloat() / lonBands)
            Vec3(r * sin(phi), baseY, r * cos(phi))
        }
        faces.add(Face3D("hemi_base", baseVerts, colorRole = 2, labelBn = "πr²"))

        if (fill > 0.01f) {
            val localH = r * fill
            val capR = sqrt((r * r - localH * localH).coerceAtLeast(0.01f)) * 0.96f
            val capVerts = (0 until lonBands).map { lon ->
                val phi = 2f * PI.toFloat() * (lon.toFloat() / lonBands)
                Vec3(capR * sin(phi), fillTopY, capR * cos(phi))
            }
            faces.add(Face3D("hemi_fill_cap", capVerts, colorRole = 3, isVolumeFill = true))
        }

        val lines = listOf(
            Line3D(Vec3(0f, baseY, 0f), Vec3(r, baseY, 0f), labelBn = "r=${VisualMathEngine.formatNumberBn(rRaw)}", isInternalAxis = true),
            Line3D(Vec3(0f, baseY, 0f), Vec3(0f, baseY + r, 0f), labelBn = "h=r", isInternalAxis = true)
        )
        return faces to lines
    }

    private fun buildSquarePyramidAndNetMesh(
        aRaw: Double,
        hRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(aRaw, hRaw).coerceAtLeast(1.0).toFloat()
        val a = ((aRaw.toFloat() / maxVal) * 1.65f).coerceIn(0.75f, 1.75f)
        val h = ((hRaw.toFloat() / maxVal) * 1.85f).coerceIn(0.8f, 1.9f)
        val ha = a / 2f
        val hy = h / 2f

        val bFL = Vec3(-ha, -hy, ha)
        val bFR = Vec3(ha, -hy, ha)
        val bBR = Vec3(ha, -hy, -ha)
        val bBL = Vec3(-ha, -hy, -ha)
        val apex = Vec3(0f, hy, 0f)

        val baseClosed = listOf(bBL, bBR, bFR, bFL)
        val frontTriClosed = listOf(bFL, bFR, apex)
        val rightTriClosed = listOf(bFR, bBR, apex)
        val backTriClosed = listOf(bBR, bBL, apex)
        val leftTriClosed = listOf(bBL, bFL, apex)

        // True rigid hinge rotation around each base edge into the y = -hy plane
        val dihedralAlpha = atan2(h, ha)
        val unfoldAngle = (PI.toFloat() - dihedralAlpha) * unfold

        val frontVerts = frontTriClosed.map { pt -> rotateAroundAxis(pt, bFL, Vec3(1f, 0f, 0f), unfoldAngle) }
        val rightVerts = rightTriClosed.map { pt -> rotateAroundAxis(pt, bFR, Vec3(0f, 0f, -1f), unfoldAngle) }
        val backVerts = backTriClosed.map { pt -> rotateAroundAxis(pt, bBR, Vec3(-1f, 0f, 0f), unfoldAngle) }
        val leftVerts = leftTriClosed.map { pt -> rotateAroundAxis(pt, bBL, Vec3(0f, 0f, 1f), unfoldAngle) }

        val faces = mutableListOf(
            Face3D("pyr_base", baseClosed, colorRole = 2, labelBn = "a²"),
            Face3D("pyr_front", frontVerts, colorRole = 0, labelBn = "a=${VisualMathEngine.formatNumberBn(aRaw)}"),
            Face3D("pyr_right", rightVerts, colorRole = 1),
            Face3D("pyr_back", backVerts, colorRole = 0),
            Face3D("pyr_left", leftVerts, colorRole = 1)
        )

        if (fill > 0.01f) {
            val fillY = -hy + h * fill
            val hs = (ha * (1f - fill)).coerceAtLeast(0.03f) * 0.96f
            faces.add(
                Face3D(
                    id = "pyr_fill_top",
                    vertices = listOf(
                        Vec3(-hs, fillY, hs),
                        Vec3(hs, fillY, hs),
                        Vec3(hs, fillY, -hs),
                        Vec3(-hs, fillY, -hs)
                    ),
                    colorRole = 3,
                    isVolumeFill = true
                )
            )
        }

        val slantRaw = sqrt((aRaw / 2.0) * (aRaw / 2.0) + hRaw * hRaw)
        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hy, 0f), apex, labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, -hy, ha), apex, labelBn = "l=${VisualMathEngine.formatNumberBn(slantRaw)}", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    private fun buildHexagonalPrismAndNetMesh(
        aRaw: Double,
        hRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(aRaw * 2.0, hRaw).coerceAtLeast(1.0).toFloat()
        val r = ((aRaw.toFloat() / maxVal) * 1.25f).coerceIn(0.45f, 1.05f)
        val h = ((hRaw.toFloat() / maxVal) * 1.85f).coerceIn(0.75f, 1.85f)
        val hy = h / 2f

        val botRing = (0 until 6).map { i ->
            val ang = (i.toFloat() / 6f) * 2f * PI.toFloat()
            Vec3(r * sin(ang), -hy, r * cos(ang))
        }
        val topRing = (0 until 6).map { i ->
            val ang = (i.toFloat() / 6f) * 2f * PI.toFloat()
            Vec3(r * sin(ang), hy, r * cos(ang))
        }

        val faces = mutableListOf<Face3D>()
        faces.add(Face3D("hex_bot", botRing.reversed(), colorRole = 2, labelBn = "ভূমি"))

        val hingeTheta = unfold * (PI.toFloat() / 2f)

        for (i in 0 until 6) {
            val next = (i + 1) % 6
            val b0 = botRing[i]
            val b1 = botRing[next]
            val t1 = topRing[next]
            val t0 = topRing[i]
            val axis = (b1 - b0).normalized()
            val closedSide = listOf(b0, b1, t1, t0)
            val unfoldedSide = closedSide.map { pt -> rotateAroundAxis(pt, b0, axis, hingeTheta) }
            faces.add(
                Face3D(
                    id = "hex_side_$i",
                    vertices = unfoldedSide,
                    colorRole = if (i % 2 == 0) 0 else 1,
                    labelBn = if (i == 0) "h=${VisualMathEngine.formatNumberBn(hRaw)}" else null
                )
            )
        }

        // Top hexagon hinges around edge 0 (t0 -> t1) and then with side 0 around (b0 -> b1)
        val b0 = botRing[0]
        val b1 = botRing[1]
        val t0 = topRing[0]
        val edgeAxis = (b1 - b0).normalized()
        val unfoldedTop = topRing.map { pt ->
            val step1 = rotateAroundAxis(pt, t0, edgeAxis, hingeTheta)
            rotateAroundAxis(step1, b0, edgeAxis, hingeTheta)
        }
        faces.add(Face3D("hex_top", unfoldedTop, colorRole = 2, labelBn = "উপরের তল"))

        if (fill > 0.01f) {
            val fillY = -hy + h * fill
            val rFill = r * 0.96f
            val fillCap = (0 until 6).map { i ->
                val ang = (i.toFloat() / 6f) * 2f * PI.toFloat()
                Vec3(rFill * sin(ang), fillY, rFill * cos(ang))
            }
            faces.add(Face3D("hex_fill_cap", fillCap, colorRole = 3, isVolumeFill = true))
        }

        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hy, 0f), Vec3(0f, hy, 0f), labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, -hy, 0f), botRing[0], labelBn = "a=${VisualMathEngine.formatNumberBn(aRaw)}", isInternalAxis = true)
            )
        } else emptyList()

        return faces to lines
    }

    private fun buildFrustumAndNetMesh(
        rBotRaw: Double,
        rTopRaw: Double,
        hRaw: Double,
        unfold: Float,
        fill: Float
    ): Pair<List<Face3D>, List<Line3D>> {
        val maxVal = max(max(rBotRaw, rTopRaw) * 2.0, hRaw).coerceAtLeast(1.0).toFloat()
        val rBot = ((rBotRaw.toFloat() / maxVal) * 1.35f).coerceIn(0.55f, 1.25f)
        val rTop = ((rTopRaw.toFloat() / maxVal) * 1.35f).coerceIn(0.25f, 1.05f)
        val h = ((hRaw.toFloat() / maxVal) * 1.85f).coerceIn(0.75f, 1.85f)
        val hy = h / 2f
        val slant = sqrt((rBot - rTop) * (rBot - rTop) + h * h)
        val netTopY = -hy + slant
        val segments = 24
        val faces = mutableListOf<Face3D>()

        for (i in 0 until segments) {
            val a0 = (i.toFloat() / segments) * 2f * PI.toFloat() - PI.toFloat()
            val a1 = ((i + 1).toFloat() / segments) * 2f * PI.toFloat() - PI.toFloat()

            val closedQuad = listOf(
                Vec3(rBot * sin(a0), -hy, rBot * cos(a0)),
                Vec3(rBot * sin(a1), -hy, rBot * cos(a1)),
                Vec3(rTop * sin(a1), hy, rTop * cos(a1)),
                Vec3(rTop * sin(a0), hy, rTop * cos(a0))
            )

            val uBot0 = ((i.toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * rBot)
            val uBot1 = (((i + 1).toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * rBot)
            val uTop0 = ((i.toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * rTop)
            val uTop1 = (((i + 1).toFloat() / segments) - 0.5f) * (2f * PI.toFloat() * rTop)

            val netQuad = listOf(
                Vec3(uBot0, -hy, rBot),
                Vec3(uBot1, -hy, rBot),
                Vec3(uTop1, netTopY, rBot),
                Vec3(uTop0, netTopY, rBot)
            )

            faces.add(
                Face3D(
                    id = "frustum_side_$i",
                    vertices = closedQuad.indices.map { idx -> Vec3.lerp(closedQuad[idx], netQuad[idx], unfold) },
                    colorRole = if (i % 2 == 0) 0 else 1
                )
            )
        }

        val hingeAngle = unfold * (PI.toFloat() / 2f)
        val topPivot = Vec3(0f, hy, rTop)
        val botPivot = Vec3(0f, -hy, rBot)
        val topShift = Vec3(0f, (netTopY - hy) * unfold, (rBot - rTop) * unfold)

        val topVertices = (0 until segments).map { i ->
            val a = (i.toFloat() / segments) * 2f * PI.toFloat()
            val closedPt = Vec3(rTop * sin(a), hy, rTop * cos(a))
            rotateAroundAxis(closedPt, topPivot, Vec3(-1f, 0f, 0f), hingeAngle) + topShift
        }
        val botVertices = (0 until segments).reversed().map { i ->
            val a = (i.toFloat() / segments) * 2f * PI.toFloat()
            val closedPt = Vec3(rBot * sin(a), -hy, rBot * cos(a))
            rotateAroundAxis(closedPt, botPivot, Vec3(1f, 0f, 0f), hingeAngle)
        }

        faces.add(Face3D("frustum_top", topVertices, colorRole = 2, labelBn = "πr²"))
        faces.add(Face3D("frustum_bot", botVertices, colorRole = 2, labelBn = "πR²"))

        if (fill > 0.01f) {
            val fillY = -hy + h * fill
            val rFill = (rBot + (rTop - rBot) * fill) * 0.96f
            val fillCap = (0 until segments).map { i ->
                val a = (i.toFloat() / segments) * 2f * PI.toFloat()
                Vec3(rFill * sin(a), fillY, rFill * cos(a))
            }
            faces.add(Face3D("frustum_fill_cap", fillCap, colorRole = 3, isVolumeFill = true))
        }

        val slantRaw = sqrt((rBotRaw - rTopRaw) * (rBotRaw - rTopRaw) + hRaw * hRaw)
        val lines = if (unfold <= 0.05f) {
            listOf(
                Line3D(Vec3(0f, -hy, 0f), Vec3(0f, hy, 0f), labelBn = "h=${VisualMathEngine.formatNumberBn(hRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, -hy, 0f), Vec3(rBot, -hy, 0f), labelBn = "R=${VisualMathEngine.formatNumberBn(rBotRaw)}", isInternalAxis = true),
                Line3D(Vec3(0f, hy, 0f), Vec3(rTop, hy, 0f), labelBn = "r=${VisualMathEngine.formatNumberBn(rTopRaw)}", isInternalAxis = true),
                Line3D(Vec3(rBot, -hy, 0f), Vec3(rTop, hy, 0f), labelBn = "l=${VisualMathEngine.formatNumberBn(slantRaw)}", isInternalAxis = true)
            )
        } else emptyList()

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

        val baseAngle = atan2(hy, hbx)
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
            val x1 = v.x * cosY + v.z * sinY
            val z1 = -v.x * sinY + v.z * cosY
            val y1 = v.y

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
            val sy = cy - viewPt.y * baseScale * perspFactor
            return ProjectedPoint2D(sx, sy, viewPt.z)
        }

        val uniqueViewVertices = mutableListOf<ProjectedPoint2D>()

        val nonFillFaces = faces.filter { !it.isVolumeFill && !it.isCrossSection && it.vertices.isNotEmpty() }
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

            if (!face.isVolumeFill && !face.isCrossSection && uniqueViewVertices.size < 64) {
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
            val outwardNormal = if (!face.isVolumeFill && !face.isCrossSection && fromMeshCenter.length() > 1e-4f && rawNormal.dot(fromMeshCenter) < 0f) {
                Vec3(-rawNormal.x, -rawNormal.y, -rawNormal.z)
            } else {
                rawNormal
            }

            val isFrontFacing = outwardNormal.z >= -0.05f
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
                isVolumeFill = face.isVolumeFill,
                isCrossSection = face.isCrossSection
            )
        }.sortedBy { it.averageDepthZ }

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
