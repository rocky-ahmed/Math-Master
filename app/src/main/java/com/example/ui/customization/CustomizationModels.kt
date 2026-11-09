package com.example.ui.customization

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.squareup.moshi.JsonClass

/**
 * Dedicated semantic vector for Multiplication Table Grid with prominent multiplication (×) symbol.
 */
val MultiplicationTableVector: ImageVector by lazy {
    ImageVector.Builder(
        name = "MultiplicationTable",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Outer Table Grid with rounded corners (2x2 Matrix)
        path(fill = SolidColor(Color.White)) {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            close()
            // Top-Left cell cut-out
            moveTo(10.5f, 10.5f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(5.5f)
            verticalLineToRelative(5.5f)
            close()
            // Top-Right cell cut-out
            moveTo(19f, 10.5f)
            horizontalLineToRelative(-7f)
            verticalLineTo(5f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(5.5f)
            close()
            // Bottom-Left cell cut-out
            moveTo(10.5f, 19f)
            horizontalLineTo(5f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(5.5f)
            verticalLineToRelative(7f)
            close()
            // Bottom-Right cell cut-out
            moveTo(19f, 19f)
            horizontalLineToRelative(-7f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(7f)
            close()
        }
        // Prominent Multiplication Sign (×) in Top-Left Cell
        path(fill = SolidColor(Color.White)) {
            moveTo(6.35f, 7.06f)
            lineTo(7.06f, 6.35f)
            lineTo(7.75f, 7.04f)
            lineTo(8.44f, 6.35f)
            lineTo(9.15f, 7.06f)
            lineTo(8.46f, 7.75f)
            lineTo(9.15f, 8.44f)
            lineTo(8.44f, 9.15f)
            lineTo(7.75f, 8.46f)
            lineTo(7.06f, 9.15f)
            lineTo(6.35f, 8.44f)
            lineTo(7.04f, 7.75f)
            close()
        }
        // Factor Elements in Top-Right Cell
        path(fill = SolidColor(Color.White)) {
            moveTo(13.5f, 6.5f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(1.5f)
            horizontalLineToRelative(-4f)
            close()
            moveTo(13.5f, 9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(1.2f)
            horizontalLineToRelative(-3f)
            close()
        }
        // Factor Elements in Bottom-Left Cell
        path(fill = SolidColor(Color.White)) {
            moveTo(6.5f, 13.5f)
            horizontalLineToRelative(1.5f)
            verticalLineToRelative(4f)
            horizontalLineTo(6.5f)
            close()
            moveTo(8.8f, 13.5f)
            horizontalLineToRelative(1.2f)
            verticalLineToRelative(3f)
            horizontalLineTo(8.8f)
            close()
        }
        // Result Product Matrix Dots in Bottom-Right Cell
        path(fill = SolidColor(Color.White)) {
            moveTo(14f, 14f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(17f, 14f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(14f, 17f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(17f, 17f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
        }
    }.build()
}

/**
 * Layout styles for rendering the module items on the Home screen and preview.
 */
enum class CustomLayoutType(val displayNameBn: String) {
    COMFORTABLE_LIST("স্বাচ্ছন্দ্য তালিকা (Comfortable List)"),
    COMPACT_LIST("কম্প্যাক্ট তালিকা (Compact List)"),
    GRID("গ্রিড লেআউট (Grid)"),
    TABLE_LIST("টেবিল স্টাইল (Table/Cards)")
}

/**
 * Global text font weight selection.
 */
enum class CustomFontWeight(val displayNameBn: String) {
    REGULAR("স্বাভাবিক (Regular)"),
    MEDIUM("মাঝারি (Medium)"),
    BOLD("গাঢ় (Bold)")
}

/**
 * Curated list of high-quality local Material Symbols for custom icon selection.
 */
enum class AvailableIcon(val id: String, val displayNameBn: String) {
    SMART_TOY("smart_toy", "এআই সহকারী"),
    BAR_CHART("bar_chart", "পরিসংখ্যান বার"),
    MENU_BOOK("menu_book", "পাঠ্যবই ও গাইড"),
    LOOKS_ONE("looks_one", "মৌলিক ১"),
    STRAIGHTEN("straighten", "পরিমাপক ফিতা"),
    FUNCTIONS("functions", "বীজগণিত ফাংশন"),
    QUIZ("quiz", "কুইজ প্রশ্ন"),
    CALCULATE("calculate", "ক্যালকুলেটর"),
    NUMBERS("numbers", "সংখ্যামালা"),
    SCHOOL("school", "শিক্ষা ও বিসিএস"),
    PSYCHOLOGY("psychology", "মস্তিষ্ক ও মেধা"),
    STAR("star", "তারা / প্রিয়"),
    EMOJI_EVENTS("emoji_events", "ট্রফি ও পুরস্কার"),
    TIMELINE("timeline", "টাইমলাইন গ্রাফ"),
    PIE_CHART("pie_chart", "পাই চার্ট"),
    SPEED("speed", "গতি ও স্পিডো"),
    LIGHTBULB("lightbulb", "উদ্ভাবনী চিন্তা"),
    AUTO_AWESOME("auto_awesome", "ম্যাজিক স্টার"),
    GRADE("grade", "গ্রেড ব্যাজ"),
    BOOKMARK("bookmark", "বুকমার্ক চিহ্ন"),
    // Distinct semantic icons for full Home Grid uniqueness
    MULTIPLICATION_TABLE("multiplication_table", "গুণ ও নামতা গ্রিড"),
    VIEW_IN_AR("view_in_ar", "বর্গ ও ঘন (3D কিউব)"),
    LIBRARY_BOOKS("library_books", "ফর্মুলা সংগ্রহ লাইব্রেরি"),
    SCIENCE("science", "গণিত ল্যাব ও পরীক্ষা"),
    CATEGORY("category", "জ্যামিতিক আকার ও চিত্র");

    fun toImageVector(): ImageVector = when (this) {
        SMART_TOY -> Icons.Default.SmartToy
        BAR_CHART -> Icons.Default.BarChart
        MENU_BOOK -> Icons.AutoMirrored.Filled.MenuBook
        LOOKS_ONE -> Icons.Default.LooksOne
        STRAIGHTEN -> Icons.Default.Straighten
        FUNCTIONS -> Icons.Default.Functions
        QUIZ -> Icons.Default.Quiz
        CALCULATE -> Icons.Default.Calculate
        NUMBERS -> Icons.Default.Numbers
        SCHOOL -> Icons.Default.School
        PSYCHOLOGY -> Icons.Default.Psychology
        STAR -> Icons.Default.Star
        EMOJI_EVENTS -> Icons.Default.EmojiEvents
        TIMELINE -> Icons.Default.Timeline
        PIE_CHART -> Icons.Default.PieChart
        SPEED -> Icons.Default.Speed
        LIGHTBULB -> Icons.Default.Lightbulb
        AUTO_AWESOME -> Icons.Default.AutoAwesome
        GRADE -> Icons.Default.Grade
        BOOKMARK -> Icons.Default.Bookmark
        MULTIPLICATION_TABLE -> MultiplicationTableVector
        VIEW_IN_AR -> Icons.Default.ViewInAr
        LIBRARY_BOOKS -> Icons.AutoMirrored.Filled.LibraryBooks
        SCIENCE -> Icons.Default.Science
        CATEGORY -> Icons.Default.Category
    }

    companion object {
        fun fromId(id: String, defaultIcon: AvailableIcon = MENU_BOOK): AvailableIcon {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: defaultIcon
        }
    }
}

/**
 * Stable identifiers for each home screen section.
 * Exactly 14 modules with 100% unique, non-overlapping default icons.
 */
enum class SectionKey(val id: String, val titleBn: String, val defaultIcon: AvailableIcon) {
    DASHBOARD("section_dashboard", "অগ্রগতি ও পরিসংখ্যান ড্যাশবোর্ড", AvailableIcon.BAR_CHART),
    TABLES("section_tables", "গুণ ও নামতা (১ থেকে ৪০)", AvailableIcon.MULTIPLICATION_TABLE),
    PRIMES("section_primes", "মৌলিক সংখ্যা (১ থেকে ২০০)", AvailableIcon.LOOKS_ONE),
    SQUARES("section_squares", "বর্গ ও ঘন (১ থেকে ২০)", AvailableIcon.VIEW_IN_AR),
    FORMULAS("section_formulas", "বীজগণিতীয় সূত্রাবলী", AvailableIcon.FUNCTIONS),
    FORMULA_LIBRARY("section_formula_library", "ফর্মুলা লাইব্রেরি (বীজগণিত, জ্যামিতি, ক্যালকুলাস)", AvailableIcon.LIBRARY_BOOKS),
    PRACTICE_LAB("section_practice_lab", "গণিত প্র্যাকটিস ল্যাব", AvailableIcon.SCIENCE),
    MENTAL_MATH("section_mental_math", "মেন্টাল ম্যাথ চ্যালেঞ্জ", AvailableIcon.SPEED),
    NUMBER_SYSTEM("section_number_system", "বাস্তব সংখ্যা ও বিভাজ্যতার নিয়ম", AvailableIcon.NUMBERS),
    QUIZZES("section_quizzes", "ইন্টারেক্টিভ কুইজ পরীক্ষা", AvailableIcon.QUIZ),
    PROBLEM_SOLVER("section_problem_solver", "গণিত সমস্যা সমাধানকারী (Problem Solver)", AvailableIcon.CALCULATE),
    VISUAL_EXPLORER("section_visual_explorer", "ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (Visual Math Explorer)", AvailableIcon.CATEGORY),
    BCS_MATH("section_bcs_math", "বিসিএস গাণিতিক যুক্তি (BCS Math Reasoning)", AvailableIcon.SCHOOL),
    BOOK("section_book", "পাঠ্যবই ও গাইড (৬ষ্ঠ থেকে ৯ম-১০ম শ্রেণি)", AvailableIcon.MENU_BOOK)
}

/**
 * Settings specific to a single section.
 * Stored independently per section key.
 */
@JsonClass(generateAdapter = true)
data class SectionCustomConfig(
    val iconId: String? = null,
    val iconColorHex: String? = null,
    val iconSizeDp: Int = 28, // default 28dp (range: 20..44)
    val backgroundHex: String? = null, // Custom card background
    val titleColorHex: String? = null,
    val subtitleColorHex: String? = null,
    val titleFontSizeSp: Int = 16, // range: 13..24
    val subtitleFontSizeSp: Int = 12, // range: 10..18
    val cornerRadiusDp: Int = 16, // range: 4..32
    val borderThicknessDp: Int = 0, // 0 = no border, range: 0..4
    val borderColorHex: String? = null,
    val paddingDp: Int = 16, // internal card padding (range: 8..24)
    val cardHeightDp: Int = 0 // 0 means dynamic wrap content, or min height
)

/**
 * Global customization preferences.
 */
@JsonClass(generateAdapter = true)
data class GlobalCustomConfig(
    val layoutType: CustomLayoutType = CustomLayoutType.GRID,
    val gridColumns: Int = 2, // 1 to 4 columns
    val horizontalGapDp: Int = 14, // 4 to 24
    val verticalGapDp: Int = 14, // 4 to 24
    val cornerRadiusDp: Int = 20, // 4 to 32
    val iconSizeDp: Int = 28, // 20 to 44
    val iconTextSpacingDp: Int = 16, // 8 to 28
    val fontWeight: CustomFontWeight = CustomFontWeight.MEDIUM,
    val interfaceScalePercent: Int = 100, // 80% to 130%
    val titleFontSizeSp: Int = 16, // 13 to 24
    val subtitleFontSizeSp: Int = 12, // 10 to 18
    val contentAlignmentCenter: Boolean = false,
    val borderThicknessDp: Int = 0, // 0 to 4
    val showBorder: Boolean = false,

    // Global Color Customization Overrides (Light & Dark)
    val customAccentHex: String? = null,
    val customPrimaryActionHex: String? = null,
    val customLightBackgroundHex: String? = null,
    val customLightCardSurfaceHex: String? = null,
    val customLightTextColorHex: String? = null,

    val customDarkBackgroundHex: String? = null,
    val customDarkCardSurfaceHex: String? = null,
    val customDarkTextColorHex: String? = null
)

/**
 * Complete app customization state holding both global settings
 * and per-section overrides.
 */
data class AppCustomizationState(
    val global: GlobalCustomConfig = GlobalCustomConfig(),
    val sections: Map<String, SectionCustomConfig> = emptyMap()
) {
    fun getSectionConfig(key: SectionKey): SectionCustomConfig {
        return sections[key.id] ?: SectionCustomConfig()
    }
}
