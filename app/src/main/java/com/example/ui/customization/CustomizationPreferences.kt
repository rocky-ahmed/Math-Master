package com.example.ui.customization

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * WCAG 2.1 contrast validation and hex color utility enforcing:
 * - Explicit opaque parentSurface invariant (`alpha == 1.0f`, no silent white/black defaults)
 * - Exact sRGB transfer-function linearization (`pow(2.4)`)
 * - Conservative text contrast threshold `>= 4.5:1` for all text across Bengali & English typography
 * - Non-text UI component / indicator threshold `>= 3.0:1`
 * - Hue-preserving + deterministic neutral fallback auto-adjustment (`suggestAccessibleColor`)
 */
object ColorUtils {
    const val REQUIRED_TEXT_CONTRAST_RATIO = 4.5
    const val REQUIRED_NON_TEXT_CONTRAST_RATIO = 3.0

    val PRESET_PALETTES = listOf(
        Color(0xFF1E3A8A) to "রয়েল ব্লু",
        Color(0xFF0F766E) to "ডিপ টিল",
        Color(0xFFB45309) to "অ্যাম্বার",
        Color(0xFF7C3AED) to "পার্পল",
        Color(0xFFE11D48) to "ক্রিমসন",
        Color(0xFF059669) to "এমেরাল্ড",
        Color(0xFFD97706) to "গোল্ডেন",
        Color(0xFF2563EB) to "ওশান ব্লু",
        Color(0xFF4F46E5) to "ইন্ডিগো",
        Color(0xFF0891B2) to "সায়ান",
        Color(0xFF1F2937) to "ডার্ক স্লেট",
        Color(0xFFDC2626) to "রেড"
    )

    fun parseHexColor(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return try {
            val cleanHex = hex.trim().removePrefix("#")
            val colorInt = when (cleanHex.length) {
                6 -> android.graphics.Color.parseColor("#$cleanHex")
                8 -> android.graphics.Color.parseColor("#$cleanHex")
                3 -> {
                    val expanded = "${cleanHex[0]}${cleanHex[0]}${cleanHex[1]}${cleanHex[1]}${cleanHex[2]}${cleanHex[2]}"
                    android.graphics.Color.parseColor("#$expanded")
                }
                else -> return null
            }
            Color(colorInt)
        } catch (_: Exception) {
            null
        }
    }

    fun normalizeHexOrNull(hex: String?): String? {
        if (hex.isNullOrBlank()) return null
        val clean = hex.trim().removePrefix("#")
        if (!clean.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) return null
        return when (clean.length) {
            3 -> {
                val expanded = "${clean[0]}${clean[0]}${clean[1]}${clean[1]}${clean[2]}${clean[2]}"
                "#${expanded.uppercase()}"
            }
            6, 8 -> "#${clean.uppercase()}"
            else -> null
        }
    }

    fun toHex(color: Color): String {
        val alpha = (color.alpha * 255).toInt().coerceIn(0, 255)
        val red = (color.red * 255).toInt().coerceIn(0, 255)
        val green = (color.green * 255).toInt().coerceIn(0, 255)
        val blue = (color.blue * 255).toInt().coerceIn(0, 255)
        return if (alpha < 255) {
            String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
        } else {
            String.format("#%02X%02X%02X", red, green, blue)
        }
    }

    fun isValidHex(hex: String): Boolean {
        val clean = hex.trim().removePrefix("#")
        return (clean.length == 3 || clean.length == 6 || clean.length == 8) &&
            clean.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
    }

    fun compositeOver(foreground: Color, opaqueBackground: Color): Color {
        require(opaqueBackground.alpha >= 0.999f) {
            "opaqueBackground must be opaque (alpha == 1.0f), was ${opaqueBackground.alpha}"
        }
        val a = foreground.alpha.coerceIn(0f, 1f)
        if (a >= 0.999f) {
            return foreground.copy(alpha = 1f)
        }
        val inv = 1f - a
        return Color(
            red = (foreground.red * a + opaqueBackground.red * inv).coerceIn(0f, 1f),
            green = (foreground.green * a + opaqueBackground.green * inv).coerceIn(0f, 1f),
            blue = (foreground.blue * a + opaqueBackground.blue * inv).coerceIn(0f, 1f),
            alpha = 1f
        )
    }

    fun resolveEffectiveBackground(
        background: Color,
        parentSurface: Color,
        rootBackdrop: Color
    ): Color {
        require(rootBackdrop.alpha >= 0.999f) {
            "rootBackdrop must be opaque (alpha == 1.0f), was ${rootBackdrop.alpha}"
        }
        val opaqueParent = if (parentSurface.alpha >= 0.999f) {
            parentSurface.copy(alpha = 1f)
        } else {
            compositeOver(parentSurface, rootBackdrop)
        }
        return compositeOver(background, opaqueParent)
    }

    fun relativeLuminance(color: Color): Double {
        fun linearize(channel: Float): Double {
            val c = channel.toDouble().coerceIn(0.0, 1.0)
            return if (c <= 0.04045) {
                c / 12.92
            } else {
                ((c + 0.055) / 1.055).pow(2.4)
            }
        }
        return 0.2126 * linearize(color.red) +
            0.7152 * linearize(color.green) +
            0.0722 * linearize(color.blue)
    }

    fun calculateContrastRatioOpaque(
        opaqueForeground: Color,
        opaqueBackground: Color
    ): Double {
        require(opaqueForeground.alpha >= 0.999f && opaqueBackground.alpha >= 0.999f) {
            "Both colors must be opaque (alpha == 1.0f); got fg=${opaqueForeground.alpha}, bg=${opaqueBackground.alpha}"
        }
        val l1 = relativeLuminance(opaqueForeground)
        val l2 = relativeLuminance(opaqueBackground)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun calculateContrastRatio(
        foreground: Color,
        background: Color,
        parentSurface: Color
    ): Double {
        require(parentSurface.alpha >= 0.999f) {
            "parentSurface must be opaque (alpha == 1.0f), was ${parentSurface.alpha}"
        }
        val effectiveBg = compositeOver(background, parentSurface)
        val effectiveFg = compositeOver(foreground, effectiveBg)
        return calculateContrastRatioOpaque(effectiveFg, effectiveBg)
    }

    fun hasAdequateContrast(
        foreground: Color,
        background: Color,
        parentSurface: Color,
        minRatio: Double = REQUIRED_TEXT_CONTRAST_RATIO
    ): Boolean {
        return calculateContrastRatio(foreground, background, parentSurface) >= minRatio
    }

    /**
     * Convenience overload when [bg] is already an opaque background surface (`alpha == 1.0f`).
     */
    fun hasAdequateContrast(
        fg: Color,
        bg: Color,
        minRatio: Double = REQUIRED_TEXT_CONTRAST_RATIO
    ): Boolean {
        require(bg.alpha >= 0.999f) {
            "2-arg hasAdequateContrast requires an opaque background (alpha == 1.0f); pass explicit parentSurface for translucent backgrounds"
        }
        return hasAdequateContrast(
            foreground = fg,
            background = bg,
            parentSurface = bg,
            minRatio = minRatio
        )
    }

    fun hasAdequateNonTextContrast(
        indicator: Color,
        background: Color,
        parentSurface: Color
    ): Boolean {
        return hasAdequateContrast(
            foreground = indicator,
            background = background,
            parentSurface = parentSurface,
            minRatio = REQUIRED_NON_TEXT_CONTRAST_RATIO
        )
    }

    /**
     * Produces an accessible foreground color for [background] composited over an explicit opaque
     * [parentSurface]:
     * 1. Returns opaque [foreground] if it already meets [minRatio].
     * 2. Searches darker and lighter HSL lightness branches while preserving hue and saturation,
     *    choosing the candidate with minimum lightness delta that meets [minRatio].
     * 3. Progressively reduces saturation if the saturated hue cannot reach [minRatio].
     * 4. Falls back deterministically to high-contrast neutral tokens (#0F172A, #F8FAFC, #000000, #FFFFFF).
     * 5. Verifies `calculateContrastRatio(result, background, parentSurface) >= minRatio`.
     */
    fun suggestAccessibleColor(
        foreground: Color,
        background: Color,
        parentSurface: Color,
        minRatio: Double = REQUIRED_TEXT_CONTRAST_RATIO
    ): Color {
        require(parentSurface.alpha >= 0.999f) {
            "parentSurface must be opaque (alpha == 1.0f), was ${parentSurface.alpha}"
        }
        val effectiveBg = compositeOver(background, parentSurface)
        val effectiveFg = compositeOver(foreground, effectiveBg)
        if (calculateContrastRatioOpaque(effectiveFg, effectiveBg) >= minRatio) {
            return effectiveFg
        }

        val (hue, sat, lightness0) = rgbToHsl(effectiveFg)
        val satSteps = listOf(sat, sat * 0.7f, sat * 0.4f, sat * 0.15f, 0f).distinct()

        for (s in satSteps) {
            var bestCandidate: Color? = null
            var bestDelta = Float.MAX_VALUE

            // Search darker branch [0f, lightness0] using binary search for closest passing L
            if (calculateContrastRatioOpaque(hslToColor(hue, s, 0f), effectiveBg) >= minRatio) {
                var low = 0f
                var high = lightness0
                var passingL = 0f
                repeat(24) {
                    val mid = (low + high) * 0.5f
                    val cand = hslToColor(hue, s, mid)
                    if (calculateContrastRatioOpaque(cand, effectiveBg) >= minRatio) {
                        passingL = mid
                        low = mid
                    } else {
                        high = mid
                    }
                }
                val cand = hslToColor(hue, s, passingL)
                if (calculateContrastRatioOpaque(cand, effectiveBg) >= minRatio) {
                    bestCandidate = cand
                    bestDelta = abs(lightness0 - passingL)
                }
            }

            // Search lighter branch [lightness0, 1f] using binary search for closest passing L
            if (calculateContrastRatioOpaque(hslToColor(hue, s, 1f), effectiveBg) >= minRatio) {
                var low = lightness0
                var high = 1f
                var passingL = 1f
                repeat(24) {
                    val mid = (low + high) * 0.5f
                    val cand = hslToColor(hue, s, mid)
                    if (calculateContrastRatioOpaque(cand, effectiveBg) >= minRatio) {
                        passingL = mid
                        high = mid
                    } else {
                        low = mid
                    }
                }
                val cand = hslToColor(hue, s, passingL)
                val delta = abs(passingL - lightness0)
                if (calculateContrastRatioOpaque(cand, effectiveBg) >= minRatio && delta < bestDelta) {
                    bestCandidate = cand
                    bestDelta = delta
                }
            }

            // Dense sweep safety net across [0.02..0.98]
            if (bestCandidate == null) {
                for (step in 0..64) {
                    val candL = step / 64f
                    val cand = hslToColor(hue, s, candL)
                    if (calculateContrastRatioOpaque(cand, effectiveBg) >= minRatio) {
                        val delta = abs(candL - lightness0)
                        if (delta < bestDelta) {
                            bestCandidate = cand
                            bestDelta = delta
                        }
                    }
                }
            }

            if (bestCandidate != null && calculateContrastRatio(bestCandidate, background, parentSurface) >= minRatio) {
                return bestCandidate
            }
        }

        // Deterministic neutral fallback guarantee
        val neutralCandidates = listOf(
            Color(0xFF0F172A),
            Color(0xFFF8FAFC),
            Color.Black,
            Color.White
        )
        neutralCandidates.firstOrNull { cand ->
            calculateContrastRatio(cand, background, parentSurface) >= minRatio
        }?.let { return it }

        return neutralCandidates.maxBy { cand ->
            calculateContrastRatio(cand, background, parentSurface)
        }
    }

    private fun rgbToHsl(color: Color): Triple<Float, Float, Float> {
        val r = color.red.coerceIn(0f, 1f)
        val g = color.green.coerceIn(0f, 1f)
        val b = color.blue.coerceIn(0f, 1f)
        val maxC = max(r, max(g, b))
        val minC = min(r, min(g, b))
        val l = (maxC + minC) * 0.5f
        val d = maxC - minC
        if (d < 1e-5f) {
            return Triple(0f, 0f, l)
        }
        val s = if (l > 0.5f) d / (2f - maxC - minC) else d / (maxC + minC)
        val hRaw = when (maxC) {
            r -> (g - b) / d + (if (g < b) 6f else 0f)
            g -> (b - r) / d + 2f
            else -> (r - g) / d + 4f
        }
        val h = (hRaw / 6f).coerceIn(0f, 1f)
        return Triple(h, s.coerceIn(0f, 1f), l.coerceIn(0f, 1f))
    }

    private fun hslToColor(h: Float, s: Float, l: Float): Color {
        val ch = h.coerceIn(0f, 1f)
        val cs = s.coerceIn(0f, 1f)
        val cl = l.coerceIn(0f, 1f)
        if (cs <= 1e-5f) {
            return Color(cl, cl, cl, 1f)
        }
        fun hue2rgb(p: Float, q: Float, tIn: Float): Float {
            var t = tIn
            if (t < 0f) t += 1f
            if (t > 1f) t -= 1f
            return when {
                t < 1f / 6f -> p + (q - p) * 6f * t
                t < 1f / 2f -> q
                t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
                else -> p
            }.coerceIn(0f, 1f)
        }
        val q = if (cl < 0.5f) cl * (1f + cs) else cl + cs - cl * cs
        val p = 2f * cl - q
        val r = hue2rgb(p, q, ch + 1f / 3f)
        val g = hue2rgb(p, q, ch)
        val b = hue2rgb(p, q, ch - 1f / 3f)
        return Color(r, g, b, 1f)
    }
}

/**
 * Persistence layer for user customization preferences using SharedPreferences and Moshi.
 * Ensures zero disruption to Room Database and existing user bookmarks or quiz records.
 */
class CustomizationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("math_customization_prefs", Context.MODE_PRIVATE)
    private val moshi: Moshi by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val sectionMapType by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            SectionCustomConfig::class.java
        )
    }
    private val sectionMapAdapter by lazy(LazyThreadSafetyMode.PUBLICATION) {
        moshi.adapter<Map<String, SectionCustomConfig>>(sectionMapType)
    }
    private val globalAdapter by lazy(LazyThreadSafetyMode.PUBLICATION) {
        moshi.adapter(GlobalCustomConfig::class.java)
    }

    fun loadCustomizationState(): AppCustomizationState {
        val sectionsJson = prefs.getString(KEY_SECTIONS, null)
        val sectionMap = if (!sectionsJson.isNullOrBlank()) {
            try {
                (sectionMapAdapter.fromJson(sectionsJson) ?: emptyMap())
                    .mapValues { (_, cfg) -> cfg.sanitized() }
            } catch (_: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }

        val globalJson = prefs.getString(KEY_GLOBAL, null)
        val hasMigratedToGrid = prefs.getBoolean("has_migrated_default_grid_v2", false)
        val globalConfig = if (!globalJson.isNullOrBlank()) {
            try {
                var loaded = (globalAdapter.fromJson(globalJson) ?: GlobalCustomConfig()).sanitized()
                if (!hasMigratedToGrid && loaded.layoutType == CustomLayoutType.COMFORTABLE_LIST) {
                    loaded = loaded.copy(layoutType = CustomLayoutType.GRID).sanitized()
                    prefs.edit()
                        .putBoolean("has_migrated_default_grid_v2", true)
                        .putString(KEY_GLOBAL, globalAdapter.toJson(loaded))
                        .apply()
                }
                loaded
            } catch (_: Exception) {
                GlobalCustomConfig()
            }
        } else {
            prefs.edit().putBoolean("has_migrated_default_grid_v2", true).apply()
            GlobalCustomConfig()
        }

        return AppCustomizationState(
            global = globalConfig,
            sections = sectionMap
        ).sanitized()
    }

    fun saveCustomizationState(state: AppCustomizationState) {
        val clean = state.sanitized()
        prefs.edit()
            .putString(KEY_GLOBAL, globalAdapter.toJson(clean.global))
            .putString(KEY_SECTIONS, sectionMapAdapter.toJson(clean.sections))
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_GLOBAL = "custom_global_config_v1"
        private const val KEY_SECTIONS = "custom_sections_config_v1"
    }
}
