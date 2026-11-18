package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.BengaliNumberFormatter
import com.example.ui.MathViewModel
import com.example.ui.components.MathTopAppBar
import com.example.ui.customization.AvailableIcon
import com.example.ui.customization.ColorUtils
import com.example.ui.customization.CustomLayoutType
import com.example.ui.customization.CustomModuleRenderer
import com.example.ui.customization.CustomFontWeight
import com.example.ui.customization.HomeModuleItem
import com.example.ui.customization.SectionKey
import com.example.ui.customization.resolveEffectiveGridColumns
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedUiState
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOnSurface
import com.example.ui.theme.DarkOnSurfaceVariant
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.LightOnSurfaceVariant
import com.example.ui.theme.LightOutline
import com.example.ui.theme.LightSurface
import com.example.ui.theme.MathSpacing
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.discardCustomizationPreview()
        viewModel.navigateBack()
    }

    val previewState by viewModel.previewCustomizationState.collectAsStateWithLifecycle()
    val savedState by viewModel.customizationState.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberManagedUiState(viewModel, "customize:selectedTabIndex", 0)
    var selectedSectionKey by rememberManagedUiState(viewModel, "customize:selectedSectionKey", SectionKey.TABLES)
    var showResetAllDialog by remember { mutableStateOf(false) }
    var showSavedSnackbar by remember { mutableStateOf(false) }
    val tabListState = rememberManagedLazyListState(viewModel, "customize:tab_$selectedTabIndex")

    val isSystemDark = isSystemInDarkTheme()
    val isDarkActive = when (currentThemeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemDark
    }

    val tabs = listOf(
        "লেআউট",
        "টাইপোগ্রাফি",
        "আইকন",
        "সেকশন ভিত্তিক",
        "গ্লোবাল থিম",
        "লাইভ প্রিভিউ"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MathTopAppBar(
            title = "অ্যাপ কাস্টমাইজেশন",
            subtitle = "লেআউট, আইকন, ফন্ট ও রঙের পূর্ণ নিয়ন্ত্রণ",
            onBackClick = {
                viewModel.discardCustomizationPreview()
                viewModel.navigateBack()
            },
            actions = {
                IconButton(
                    onClick = { showResetAllDialog = true },
                    modifier = Modifier.testTag("btn_reset_all")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "রিসেট অল",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        // Top Action Bar: Save Changes, Live Status & Discard
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.discardCustomizationPreview()
                        viewModel.navigateBack()
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_cancel_customization")
                ) {
                    Text("বাতিল / পূর্বাবস্থা", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        viewModel.saveCustomization()
                        showSavedSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_save_customization")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সংরক্ষণ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showSavedSnackbar) {
            Surface(
                color = Color(0xFF065F46),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .testTag("customize_saved_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "✓ পরিবর্তনসমূহ সফলভাবে সংরক্ষিত হয়েছে!",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showSavedSnackbar = false },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("ঠিক আছে", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("custom_tab_$index")
                )
            }
        }

        // Tab Content
        LazyColumn(
            state = tabListState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("customize_list"),
            contentPadding = MathSpacing.screenListContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Category 1: Layout Customization
                    item {
                        LayoutCustomizationSection(
                            previewState = previewState,
                            onUpdateGlobal = { updater -> viewModel.updatePreviewGlobal(updater) },
                            onResetCategory = { viewModel.resetPreviewCategory("LAYOUT") }
                        )
                    }
                }
                1 -> {
                    // Category 2: Typography
                    item {
                        TypographyCustomizationSection(
                            previewState = previewState,
                            onUpdateGlobal = { updater -> viewModel.updatePreviewGlobal(updater) },
                            onResetCategory = { viewModel.resetPreviewCategory("TYPOGRAPHY") }
                        )
                    }
                }
                2 -> {
                    // Category 3: Icon Customization
                    item {
                        IconCustomizationSection(
                            previewState = previewState,
                            selectedSection = selectedSectionKey,
                            onSelectSection = { selectedSectionKey = it },
                            onUpdateSection = { key, updater -> viewModel.updatePreviewSection(key, updater) },
                            onResetSection = { key -> viewModel.resetPreviewSection(key) }
                        )
                    }
                }
                3 -> {
                    // Category 4: Independent Section Appearance
                    item {
                        SectionAppearanceCustomizationSection(
                            previewState = previewState,
                            isDarkTheme = isDarkActive,
                            selectedSection = selectedSectionKey,
                            onSelectSection = { selectedSectionKey = it },
                            onUpdateSection = { key, updater -> viewModel.updatePreviewSection(key, updater) },
                            onResetSection = { key -> viewModel.resetPreviewSection(key) },
                            onResetAllSections = { viewModel.resetPreviewCategory("SECTIONS") }
                        )
                    }
                }
                4 -> {
                    // Category 5: Global Theme & Colors
                    item {
                        GlobalThemeCustomizationSection(
                            previewState = previewState,
                            themeMode = currentThemeMode,
                            onSetThemeMode = { viewModel.setThemeMode(it) },
                            onUpdateGlobal = { updater -> viewModel.updatePreviewGlobal(updater) },
                            onResetCategory = { viewModel.resetPreviewCategory("GLOBAL_THEME") }
                        )
                    }
                }
                5 -> {
                    // Category 6: Live Preview
                    item {
                        LivePreviewSection(previewState = previewState)
                    }
                }
            }

            // Always render Live Preview at the bottom of every configuration tab for real-time feedback
            if (selectedTabIndex != 5) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "লাইভ প্রিভিউ (তাৎক্ষণিক প্রতিফলন):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LivePreviewSection(previewState = previewState)
                }
            }
        }
    }

    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            title = { Text("সমস্ত কাস্টমাইজেশন রিসেট করবেন?") },
            text = {
                Text(
                    "এটি অ্যাপের সকল কাস্টম লেআউট, রঙ, ফন্ট ও আইকনকে মূল ডিফল্ট অবস্থায় ফিরিয়ে নেবে। " +
                    "আপনার সেভ করা বুকমার্ক ও কুইজের রেকর্ড সম্পূর্ণ নিরাপদ থাকবে।"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllCustomizations()
                        showResetAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// ==========================================
// CATEGORY 1: LAYOUT CUSTOMIZATION
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LayoutCustomizationSection(
    previewState: com.example.ui.customization.AppCustomizationState,
    onUpdateGlobal: ((com.example.ui.customization.GlobalCustomConfig) -> com.example.ui.customization.GlobalCustomConfig) -> Unit,
    onResetCategory: () -> Unit
) {
    val global = previewState.global
    val density = LocalDensity.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val contentWidthDp = (maxWidth - 32.dp).coerceAtLeast(0.dp).value.toInt()
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "১. হোম স্ক্রিন লেআউট শৈলী",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onResetCategory,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_layout_category")
                    ) {
                        Text("ডিফল্ট", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Layout Style Chips (FlowRow wraps cleanly on 320dp screens)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CustomLayoutType.values().forEach { type ->
                        FilterChip(
                            selected = global.layoutType == type,
                            onClick = { onUpdateGlobal { it.copy(layoutType = type) } },
                            label = { Text(type.displayNameBn.split(" ")[0], fontSize = 11.sp) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("chip_layout_${type.name.lowercase()}")
                        )
                    }
                }

                if (global.layoutType == CustomLayoutType.GRID) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val effectiveCols = resolveEffectiveGridColumns(
                        requestedColumns = global.gridColumns,
                        availableGridContentWidthDp = contentWidthDp,
                        horizontalGapDp = global.horizontalGapDp,
                        fontScale = density.fontScale,
                        interfaceScalePercent = global.interfaceScalePercent
                    )
                    Text(
                        text = "গ্রিড কলাম সংখ্যা: ${BengaliNumberFormatter.toBn(global.gridColumns)} কলাম",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = global.gridColumns.toFloat(),
                        onValueChange = { onUpdateGlobal { g -> g.copy(gridColumns = it.toInt()) } },
                        valueRange = 1f..4f,
                        steps = 2,
                        modifier = Modifier.testTag("slider_grid_columns")
                    )
                    if (effectiveCols < global.gridColumns) {
                        Text(
                            text = "নোট: বর্তমান স্ক্রিন প্রস্থ ও ফন্ট স্কেলে স্পষ্টতা বজায় রাখতে সর্বোচ্চ ${BengaliNumberFormatter.toBn(effectiveCols)} কলাম দেখানো হবে।",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("grid_columns_capped_info")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Corner Radius Slider
                Text(
                    text = "কার্ড কর্নার রাউন্ড (কোণ বাঁকা): ${BengaliNumberFormatter.toBn(global.cornerRadiusDp)} dp",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = global.cornerRadiusDp.toFloat(),
                    onValueChange = { onUpdateGlobal { g -> g.copy(cornerRadiusDp = it.toInt()) } },
                    valueRange = 4f..32f,
                    modifier = Modifier.testTag("slider_corner_radius")
                )

                // Horizontal & Vertical Gaps
                Text(
                    text = "কার্ডের মধ্যবর্তী ফাঁকা (Spacing): ${BengaliNumberFormatter.toBn(global.verticalGapDp)} dp",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = global.verticalGapDp.toFloat(),
                    onValueChange = { onUpdateGlobal { g -> g.copy(verticalGapDp = it.toInt(), horizontalGapDp = it.toInt()) } },
                    valueRange = 4f..24f,
                    modifier = Modifier.testTag("slider_card_gap")
                )

                // Global Icon Size Slider (Unwired Field #1)
                Text(
                    text = "গ্লোবাল আইকনের আকার: ${BengaliNumberFormatter.toBn(global.iconSizeDp)} dp",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = global.iconSizeDp.toFloat().coerceIn(20f, 44f),
                    onValueChange = { onUpdateGlobal { g -> g.copy(iconSizeDp = it.toInt()) } },
                    valueRange = 20f..44f,
                    modifier = Modifier.testTag("slider_global_icon_size")
                )

                // Global Icon-to-Text Spacing Slider (Unwired Field #2)
                Text(
                    text = "আইকন ও লেখার মধ্যবর্তী ফাঁকা: ${BengaliNumberFormatter.toBn(global.iconTextSpacingDp)} dp",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = global.iconTextSpacingDp.toFloat().coerceIn(8f, 28f),
                    onValueChange = { onUpdateGlobal { g -> g.copy(iconTextSpacingDp = it.toInt()) } },
                    valueRange = 8f..28f,
                    modifier = Modifier.testTag("slider_global_icon_text_spacing")
                )

                // Content Alignment Center Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "কার্ডের লেখা মাঝে সারিবদ্ধ (Center Align)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = global.contentAlignmentCenter,
                        onCheckedChange = { onUpdateGlobal { g -> g.copy(contentAlignmentCenter = it) } },
                        modifier = Modifier.testTag("switch_center_align")
                    )
                }

                // Show Border Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "কার্ডের চারপাশে আউটলাইন বর্ডার",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = global.showBorder,
                        onCheckedChange = { onUpdateGlobal { g -> g.copy(showBorder = it, borderThicknessDp = if (it) 1 else 0) } },
                        modifier = Modifier.testTag("switch_show_border")
                    )
                }
            }
        }
    }
}

// ==========================================
// CATEGORY 2: TYPOGRAPHY CUSTOMIZATION
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypographyCustomizationSection(
    previewState: com.example.ui.customization.AppCustomizationState,
    onUpdateGlobal: ((com.example.ui.customization.GlobalCustomConfig) -> com.example.ui.customization.GlobalCustomConfig) -> Unit,
    onResetCategory: () -> Unit
) {
    val global = previewState.global

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "২. ফন্ট ও টাইপোগ্রাফি",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onResetCategory,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_reset_typography_category")
                ) {
                    Text("ডিফল্ট", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Font Weight selection
            Text(
                text = "ফন্টের পুরুত্ব (Font Weight):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CustomFontWeight.values().forEach { weight ->
                    FilterChip(
                        selected = global.fontWeight == weight,
                        onClick = { onUpdateGlobal { it.copy(fontWeight = weight) } },
                        label = { Text(weight.displayNameBn.split(" ")[0], fontSize = 12.sp) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chip_font_weight_${weight.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title Font Size Slider
            Text(
                text = "শিরোনামের আকার (Title Size): ${BengaliNumberFormatter.toBn(global.titleFontSizeSp)} sp",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = global.titleFontSizeSp.toFloat(),
                onValueChange = { onUpdateGlobal { g -> g.copy(titleFontSizeSp = it.toInt()) } },
                valueRange = 13f..24f,
                modifier = Modifier.testTag("slider_title_font_size")
            )

            // Subtitle Font Size Slider
            Text(
                text = "উপশিরোনামের আকার (Subtitle Size): ${BengaliNumberFormatter.toBn(global.subtitleFontSizeSp)} sp",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = global.subtitleFontSizeSp.toFloat(),
                onValueChange = { onUpdateGlobal { g -> g.copy(subtitleFontSizeSp = it.toInt()) } },
                valueRange = 10f..18f,
                modifier = Modifier.testTag("slider_subtitle_font_size")
            )

            // Interface Scale Percent Slider (Unwired Field #3)
            Text(
                text = "সামগ্রিক ইন্টারফেস স্কেল: ${BengaliNumberFormatter.toBn(global.interfaceScalePercent)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = global.interfaceScalePercent.toFloat().coerceIn(80f, 130f),
                onValueChange = { onUpdateGlobal { g -> g.copy(interfaceScalePercent = it.toInt()) } },
                valueRange = 80f..130f,
                steps = 9,
                modifier = Modifier.testTag("slider_interface_scale")
            )
        }
    }
}

// ==========================================
// CATEGORY 3: ICON CUSTOMIZATION
// ==========================================
@Composable
fun IconCustomizationSection(
    previewState: com.example.ui.customization.AppCustomizationState,
    selectedSection: SectionKey,
    onSelectSection: (SectionKey) -> Unit,
    onUpdateSection: (SectionKey, (com.example.ui.customization.SectionCustomConfig) -> com.example.ui.customization.SectionCustomConfig) -> Unit,
    onResetSection: (SectionKey) -> Unit
) {
    val sectionConfig = previewState.getSectionConfig(selectedSection)
    val effectiveIconSize = sectionConfig.iconSizeDp.takeIf { it > 0 } ?: previewState.global.iconSizeDp

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "৩. সেকশন অনুযায়ী আইকন পরিবর্তন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Section Selector Chips
            Text(text = "সেকশন নির্বাচন করুন:", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SectionKey.values()) { section ->
                    FilterChip(
                        selected = selectedSection == section,
                        onClick = { onSelectSection(section) },
                        label = { Text(section.titleBn.split(" ")[0], fontSize = 11.sp) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chip_sec_${section.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Current Active Icon & Controls
            val activeIcon = sectionConfig.iconId?.let { AvailableIcon.fromId(it) } ?: selectedSection.defaultIcon

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activeIcon.toImageVector(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = selectedSection.titleBn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "বর্তমান আইকন: ${activeIcon.displayNameBn}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = { onResetSection(selectedSection) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_reset_section_icon")
                ) {
                    Text("রিসেট", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Icon Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sectionConfig.iconSizeDp > 0) {
                        "আইকনের আকার: ${BengaliNumberFormatter.toBn(sectionConfig.iconSizeDp)} dp"
                    } else {
                        "আইকনের আকার: গ্লোবাল (${BengaliNumberFormatter.toBn(effectiveIconSize)} dp)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (sectionConfig.iconSizeDp > 0) {
                    TextButton(
                        onClick = { onUpdateSection(selectedSection) { cfg -> cfg.copy(iconSizeDp = 0) } },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_inherit_global_icon_size")
                    ) {
                        Text("গ্লোবাল", fontSize = 11.sp)
                    }
                }
            }
            Slider(
                value = effectiveIconSize.toFloat().coerceIn(20f, 44f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(iconSizeDp = it.toInt()) } },
                valueRange = 20f..44f,
                modifier = Modifier.testTag("slider_icon_size")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Available Icon Grid
            Text(
                text = "নতুন আইকন বেছে নিন:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("icon_picker_row")
            ) {
                items(AvailableIcon.values()) { iconChoice ->
                    val isSelected = activeIcon == iconChoice
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = {
                                    onUpdateSection(selectedSection) { cfg -> cfg.copy(iconId = iconChoice.id) }
                                }
                            )
                            .semantics {
                                contentDescription = iconChoice.displayNameBn
                                stateDescription = if (isSelected) "নির্বাচিত" else "নির্বাচিত নয়"
                            }
                            .testTag("icon_pick_${iconChoice.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = iconChoice.toImageVector(),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CATEGORY 4: SECTION INDEPENDENT CUSTOMIZATION
// ==========================================
@Composable
fun SectionAppearanceCustomizationSection(
    previewState: com.example.ui.customization.AppCustomizationState,
    isDarkTheme: Boolean,
    selectedSection: SectionKey,
    onSelectSection: (SectionKey) -> Unit,
    onUpdateSection: (SectionKey, (com.example.ui.customization.SectionCustomConfig) -> com.example.ui.customization.SectionCustomConfig) -> Unit,
    onResetSection: (SectionKey) -> Unit,
    onResetAllSections: () -> Unit
) {
    val global = previewState.global
    val sectionConfig = previewState.getSectionConfig(selectedSection)
    var hexInput by remember(selectedSection) { mutableStateOf("") }

    // Explicit opaque parentSurface invariant: always resolve active Light/Dark backdrop with alpha == 1.0f
    val activeParentSurface = if (isDarkTheme) {
        ColorUtils.parseHexColor(global.customDarkBackgroundHex) ?: DarkBackground
    } else {
        ColorUtils.parseHexColor(global.customLightBackgroundHex) ?: LightBackground
    }
    val activeDefaultCardSurface = if (isDarkTheme) {
        ColorUtils.parseHexColor(global.customDarkCardSurfaceHex) ?: DarkSurface
    } else {
        ColorUtils.parseHexColor(global.customLightCardSurfaceHex) ?: LightSurface
    }
    val activeDefaultTitleColor = if (isDarkTheme) {
        ColorUtils.parseHexColor(global.customDarkTextColorHex) ?: DarkOnSurface
    } else {
        ColorUtils.parseHexColor(global.customLightTextColorHex) ?: LightOnSurface
    }
    val activeDefaultSubtitleColor = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant

    val sectionBgColor = ColorUtils.parseHexColor(sectionConfig.backgroundHex)
    val effectiveCardBg = ColorUtils.compositeOver(sectionBgColor ?: activeDefaultCardSurface, activeParentSurface)

    val effTitleColor = ColorUtils.parseHexColor(sectionConfig.titleColorHex) ?: activeDefaultTitleColor
    val effSubtitleColor = ColorUtils.parseHexColor(sectionConfig.subtitleColorHex) ?: activeDefaultSubtitleColor
    val customIconColor = ColorUtils.parseHexColor(sectionConfig.iconColorHex)
    val effectiveBorderThickness = if (sectionConfig.borderThicknessDp > 0) {
        sectionConfig.borderThicknessDp
    } else if (global.showBorder) {
        global.borderThicknessDp.coerceAtLeast(1)
    } else {
        0
    }
    val customBorderColor = ColorUtils.parseHexColor(sectionConfig.borderColorHex)

    // Evaluate conservative WCAG 2.1 contrast thresholds (4.5:1 all text, 3.0:1 non-text UI)
    val titleRatio = ColorUtils.calculateContrastRatio(effTitleColor, effectiveCardBg, activeParentSurface)
    val subtitleRatio = ColorUtils.calculateContrastRatio(effSubtitleColor, effectiveCardBg, activeParentSurface)
    val iconRatio = customIconColor?.let {
        ColorUtils.calculateContrastRatio(it, effectiveCardBg, activeParentSurface)
    }
    val borderRatio = if (effectiveBorderThickness > 0 && customBorderColor != null) {
        ColorUtils.calculateContrastRatio(customBorderColor, activeParentSurface, activeParentSurface)
    } else {
        null
    }

    val pendingHexBg = ColorUtils.parseHexColor(hexInput)
    val pendingEffectiveBg = pendingHexBg?.let { ColorUtils.compositeOver(it, activeParentSurface) }
    val pendingTitleRatio = pendingEffectiveBg?.let {
        ColorUtils.calculateContrastRatio(effTitleColor, it, activeParentSurface)
    }

    data class ContrastIssue(val labelBn: String, val ratio: Double, val required: Double)
    val activeIssue: ContrastIssue? = when {
        titleRatio < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO ->
            ContrastIssue("শিরোনামের লেখা", titleRatio, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
        subtitleRatio < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO ->
            ContrastIssue("উপশিরোনামের লেখা", subtitleRatio, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
        iconRatio != null && iconRatio < ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO ->
            ContrastIssue("আইকন রঙ", iconRatio, ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO)
        borderRatio != null && borderRatio < ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO ->
            ContrastIssue("বর্ডার রঙ", borderRatio, ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO)
        pendingTitleRatio != null && pendingTitleRatio < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO ->
            ContrastIssue("প্রস্তাবিত ব্যাকগ্রাউন্ড", pendingTitleRatio, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
        else -> null
    }

    val neutralSwatches = listOf(
        Color(0xFF0F172A) to "গাঢ় স্লেট",
        Color(0xFFFFFFFF) to "সাদা",
        Color(0xFF1E3A8A) to "রয়েল ব্লু",
        Color(0xFF0F766E) to "ডিপ টিল",
        Color(0xFF7C3AED) to "পার্পল",
        Color(0xFFE11D48) to "ক্রিমসন",
        Color(0xFFB45309) to "অ্যাম্বার",
        Color(0xFF93C5FD) to "স্কাই ব্লু",
        Color(0xFF5EEAD4) to "উজ্জ্বল টিল",
        Color(0xFFFCD34D) to "উজ্জ্বল হলুদ"
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "৪. স্বতন্ত্র সেকশন ডিজাইন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = { onResetSection(selectedSection) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_selected_section")
                    ) {
                        Text("সেকশন রিসেট", fontSize = 11.sp)
                    }
                    TextButton(
                        onClick = onResetAllSections,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_all_sections")
                    ) {
                        Text("সব সেকশন রিসেট", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Text(
                text = "নির্দিষ্ট সেকশনের ব্যাকগ্রাউন্ড, টেক্সট, আইকন রঙ, ফন্টের মাপ, বর্ডার ও উচ্চতা সেট করুন।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Select Section
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SectionKey.values()) { sec ->
                    FilterChip(
                        selected = selectedSection == sec,
                        onClick = { onSelectSection(sec) },
                        label = { Text(sec.titleBn.split(" ")[0], fontSize = 11.sp) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chip_appearance_sec_${sec.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Section Background Color Palette Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সেকশন ব্যাকগ্রাউন্ড রঙ:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (sectionConfig.backgroundHex != null) {
                    TextButton(
                        onClick = { onUpdateSection(selectedSection) { cfg -> cfg.copy(backgroundHex = null) } },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_clear_section_bg")
                    ) {
                        Text("ডিফল্ট", fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ColorUtils.PRESET_PALETTES) { (paletteColor, label) ->
                    val hexStr = ColorUtils.toHex(paletteColor)
                    val isSelected = sectionConfig.backgroundHex.equals(hexStr, ignoreCase = true)

                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = {
                                    onUpdateSection(selectedSection) { cfg -> cfg.copy(backgroundHex = hexStr) }
                                }
                            )
                            .semantics {
                                contentDescription = "সেকশন ব্যাকগ্রাউন্ড $label"
                                stateDescription = if (isSelected) "নির্বাচিত" else "নির্বাচিত নয়"
                            }
                            .testTag("sec_bg_swatch_${hexStr.removePrefix("#").lowercase()}"),
                        shape = CircleShape,
                        color = paletteColor,
                        border = if (isSelected) BorderStroke(3.dp, Color.White) else null
                    ) {
                        if (isSelected) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Hex Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { hexInput = it },
                    label = { Text("হেক্স কালার কোড (যেমন #1E3A8A)", fontSize = 11.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_hex_color"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        ColorUtils.normalizeHexOrNull(hexInput)?.let { normalized ->
                            onUpdateSection(selectedSection) { cfg -> cfg.copy(backgroundHex = normalized) }
                            hexInput = ""
                        }
                    },
                    enabled = ColorUtils.isValidHex(hexInput),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_apply_hex_color")
                ) {
                    Text("প্রয়োগ")
                }
            }

            if (activeIssue != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite }
                        .testTag("section_contrast_warning_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "কনট্রাস্ট সতর্কতা",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সতর্কতা (${activeIssue.labelBn}): বর্তমান কনট্রাস্ট ${String.format(Locale.US, "%.2f", activeIssue.ratio)}:১, প্রয়োজন ন্যূনতম ${String.format(Locale.US, "%.1f", activeIssue.required)}:১।",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val targetCardBg = pendingEffectiveBg ?: effectiveCardBg
                                if (pendingEffectiveBg != null) {
                                    ColorUtils.normalizeHexOrNull(hexInput)?.let { normalized ->
                                        onUpdateSection(selectedSection) { cfg -> cfg.copy(backgroundHex = normalized) }
                                        hexInput = ""
                                    }
                                }
                                val adjustedTitle = if (ColorUtils.calculateContrastRatio(effTitleColor, targetCardBg, activeParentSurface) < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO) {
                                    ColorUtils.toHex(
                                        ColorUtils.suggestAccessibleColor(
                                            foreground = effTitleColor,
                                            background = targetCardBg,
                                            parentSurface = activeParentSurface,
                                            minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
                                        )
                                    )
                                } else {
                                    sectionConfig.titleColorHex
                                }
                                val adjustedSubtitle = if (ColorUtils.calculateContrastRatio(effSubtitleColor, targetCardBg, activeParentSurface) < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO) {
                                    ColorUtils.toHex(
                                        ColorUtils.suggestAccessibleColor(
                                            foreground = effSubtitleColor,
                                            background = targetCardBg,
                                            parentSurface = activeParentSurface,
                                            minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
                                        )
                                    )
                                } else {
                                    sectionConfig.subtitleColorHex
                                }
                                val adjustedIcon = if (customIconColor != null && ColorUtils.calculateContrastRatio(customIconColor, targetCardBg, activeParentSurface) < ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO) {
                                    ColorUtils.toHex(
                                        ColorUtils.suggestAccessibleColor(
                                            foreground = customIconColor,
                                            background = targetCardBg,
                                            parentSurface = activeParentSurface,
                                            minRatio = ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO
                                        )
                                    )
                                } else {
                                    sectionConfig.iconColorHex
                                }
                                val adjustedBorder = if (effectiveBorderThickness > 0 && customBorderColor != null && ColorUtils.calculateContrastRatio(customBorderColor, activeParentSurface, activeParentSurface) < ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO) {
                                    ColorUtils.toHex(
                                        ColorUtils.suggestAccessibleColor(
                                            foreground = customBorderColor,
                                            background = activeParentSurface,
                                            parentSurface = activeParentSurface,
                                            minRatio = ColorUtils.REQUIRED_NON_TEXT_CONTRAST_RATIO
                                        )
                                    )
                                } else {
                                    sectionConfig.borderColorHex
                                }
                                onUpdateSection(selectedSection) { cfg ->
                                    cfg.copy(
                                        titleColorHex = adjustedTitle,
                                        subtitleColorHex = adjustedSubtitle,
                                        iconColorHex = adjustedIcon,
                                        borderColorHex = adjustedBorder
                                    )
                                }
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("btn_fix_section_contrast")
                        ) {
                            Text("উপযুক্ত রঙ প্রয়োগ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Title Text Color (Unwired Section #9)
            SectionColorSwatchRow(
                titleBn = "শিরোনামের রঙ (Title Color):",
                selectedHex = sectionConfig.titleColorHex,
                options = neutralSwatches,
                testTagPrefix = "sec_title_color",
                clearTestTag = "btn_clear_section_title_color",
                onSelectHex = { hex -> onUpdateSection(selectedSection) { cfg -> cfg.copy(titleColorHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Text Color (Unwired Section #10)
            SectionColorSwatchRow(
                titleBn = "উপশিরোনামের রঙ (Subtitle Color):",
                selectedHex = sectionConfig.subtitleColorHex,
                options = neutralSwatches,
                testTagPrefix = "sec_subtitle_color",
                clearTestTag = "btn_clear_section_subtitle_color",
                onSelectHex = { hex -> onUpdateSection(selectedSection) { cfg -> cfg.copy(subtitleColorHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Icon Color (Unwired Section #8)
            SectionColorSwatchRow(
                titleBn = "আইকনের রঙ (Icon Color):",
                selectedHex = sectionConfig.iconColorHex,
                options = neutralSwatches,
                testTagPrefix = "sec_icon_color",
                clearTestTag = "btn_clear_section_icon_color",
                onSelectHex = { hex -> onUpdateSection(selectedSection) { cfg -> cfg.copy(iconColorHex = hex) } }
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Section Title Font Size Override (Unwired Section #11)
            val effTitleFontSp = sectionConfig.titleFontSizeSp.takeIf { it > 0 } ?: global.titleFontSizeSp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sectionConfig.titleFontSizeSp > 0) {
                        "সেকশন শিরোনাম ফন্ট: ${BengaliNumberFormatter.toBn(sectionConfig.titleFontSizeSp)} sp"
                    } else {
                        "সেকশন শিরোনাম ফন্ট: গ্লোবাল (${BengaliNumberFormatter.toBn(effTitleFontSp)} sp)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (sectionConfig.titleFontSizeSp > 0) {
                    TextButton(
                        onClick = { onUpdateSection(selectedSection) { cfg -> cfg.copy(titleFontSizeSp = 0) } },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_section_title_font")
                    ) {
                        Text("গ্লোবাল", fontSize = 11.sp)
                    }
                }
            }
            Slider(
                value = effTitleFontSp.toFloat().coerceIn(13f, 24f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(titleFontSizeSp = it.toInt()) } },
                valueRange = 13f..24f,
                modifier = Modifier.testTag("slider_section_title_font_size")
            )

            // Section Subtitle Font Size Override (Unwired Section #12)
            val effSubtitleFontSp = sectionConfig.subtitleFontSizeSp.takeIf { it > 0 } ?: global.subtitleFontSizeSp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sectionConfig.subtitleFontSizeSp > 0) {
                        "সেকশন উপশিরোনাম ফন্ট: ${BengaliNumberFormatter.toBn(sectionConfig.subtitleFontSizeSp)} sp"
                    } else {
                        "সেকশন উপশিরোনাম ফন্ট: গ্লোবাল (${BengaliNumberFormatter.toBn(effSubtitleFontSp)} sp)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (sectionConfig.subtitleFontSizeSp > 0) {
                    TextButton(
                        onClick = { onUpdateSection(selectedSection) { cfg -> cfg.copy(subtitleFontSizeSp = 0) } },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_section_subtitle_font")
                    ) {
                        Text("গ্লোবাল", fontSize = 11.sp)
                    }
                }
            }
            Slider(
                value = effSubtitleFontSp.toFloat().coerceIn(10f, 18f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(subtitleFontSizeSp = it.toInt()) } },
                valueRange = 10f..18f,
                modifier = Modifier.testTag("slider_section_subtitle_font_size")
            )

            // Border Thickness Slider (Unwired Section #13)
            Text(
                text = "সেকশন বর্ডার পুরুত্ব: ${BengaliNumberFormatter.toBn(sectionConfig.borderThicknessDp)} dp",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = sectionConfig.borderThicknessDp.toFloat().coerceIn(0f, 6f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(borderThicknessDp = it.toInt()) } },
                valueRange = 0f..6f,
                steps = 5,
                modifier = Modifier.testTag("slider_section_border_thickness")
            )

            // Border Color Swatches (Unwired Section #14)
            SectionColorSwatchRow(
                titleBn = "বর্ডার রঙ (Border Color):",
                selectedHex = sectionConfig.borderColorHex,
                options = ColorUtils.PRESET_PALETTES + neutralSwatches.take(2),
                testTagPrefix = "sec_border_color",
                clearTestTag = "btn_clear_section_border_color",
                onSelectHex = { hex -> onUpdateSection(selectedSection) { cfg -> cfg.copy(borderColorHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Card Padding Slider
            Text(
                text = "কার্ডের ভেতরের প্যাডিং: ${BengaliNumberFormatter.toBn(sectionConfig.paddingDp)} dp",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = sectionConfig.paddingDp.toFloat().coerceIn(8f, 24f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(paddingDp = it.toInt()) } },
                valueRange = 8f..24f,
                modifier = Modifier.testTag("slider_section_padding")
            )

            // Corner Radius Slider
            Text(
                text = "এই সেকশনের কোণ বাঁকা: ${BengaliNumberFormatter.toBn(sectionConfig.cornerRadiusDp)} dp",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = sectionConfig.cornerRadiusDp.toFloat().coerceIn(4f, 32f),
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(cornerRadiusDp = it.toInt()) } },
                valueRange = 4f..32f,
                modifier = Modifier.testTag("slider_section_corner_radius")
            )

            // Card Min Height Slider (Unwired Section #15)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sectionConfig.cardHeightDp > 0) {
                        "কার্ডের ন্যূনতম উচ্চতা: ${BengaliNumberFormatter.toBn(sectionConfig.cardHeightDp)} dp"
                    } else {
                        "কার্ডের উচ্চতা: স্বয়ংক্রিয় (Auto)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (sectionConfig.cardHeightDp > 0) {
                    TextButton(
                        onClick = { onUpdateSection(selectedSection) { cfg -> cfg.copy(cardHeightDp = 0) } },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_reset_section_card_height")
                    ) {
                        Text("স্বয়ংক্রিয়", fontSize = 11.sp)
                    }
                }
            }
            Slider(
                value = if (sectionConfig.cardHeightDp > 0) sectionConfig.cardHeightDp.toFloat().coerceIn(120f, 240f) else 160f,
                onValueChange = { onUpdateSection(selectedSection) { cfg -> cfg.copy(cardHeightDp = it.toInt()) } },
                valueRange = 120f..240f,
                modifier = Modifier.testTag("slider_section_card_height")
            )
        }
    }
}

@Composable
private fun SectionColorSwatchRow(
    titleBn: String,
    selectedHex: String?,
    options: List<Pair<Color, String>>,
    testTagPrefix: String,
    clearTestTag: String,
    onSelectHex: (String?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titleBn,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (selectedHex != null) {
            TextButton(
                onClick = { onSelectHex(null) },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag(clearTestTag)
            ) {
                Text("ডিফল্ট", fontSize = 11.sp)
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { (color, label) ->
            val hexStr = ColorUtils.toHex(color)
            val isSelected = selectedHex.equals(hexStr, ignoreCase = true)
            val checkTint = if (ColorUtils.relativeLuminance(color) > 0.45) Color(0xFF0F172A) else Color.White
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelectHex(hexStr) }
                    )
                    .semantics {
                        contentDescription = "$titleBn $label"
                        stateDescription = if (isSelected) "নির্বাচিত" else "নির্বাচিত নয়"
                    }
                    .testTag("${testTagPrefix}_${hexStr.removePrefix("#").lowercase()}"),
                shape = CircleShape,
                color = color,
                border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color.Gray)
            ) {
                if (isSelected) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = checkTint, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// CATEGORY 5: GLOBAL THEME & COLORS
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalThemeCustomizationSection(
    previewState: com.example.ui.customization.AppCustomizationState,
    themeMode: AppThemeMode,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onUpdateGlobal: ((com.example.ui.customization.GlobalCustomConfig) -> com.example.ui.customization.GlobalCustomConfig) -> Unit,
    onResetCategory: () -> Unit
) {
    val global = previewState.global

    val lightParent = ColorUtils.parseHexColor(global.customLightBackgroundHex) ?: LightBackground
    val lightCardSurface = ColorUtils.parseHexColor(global.customLightCardSurfaceHex) ?: LightSurface
    val lightTextColor = ColorUtils.parseHexColor(global.customLightTextColorHex) ?: LightOnSurface
    val lightCardContrast = ColorUtils.calculateContrastRatio(lightTextColor, lightCardSurface, lightParent)
    val lightBgContrast = ColorUtils.calculateContrastRatio(lightTextColor, lightParent, lightParent)
    val lightMinContrast = minOf(lightCardContrast, lightBgContrast)
    val hasLightContrastFailure = lightMinContrast < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO

    val darkParent = ColorUtils.parseHexColor(global.customDarkBackgroundHex) ?: DarkBackground
    val darkCardSurface = ColorUtils.parseHexColor(global.customDarkCardSurfaceHex) ?: DarkSurface
    val darkTextColor = ColorUtils.parseHexColor(global.customDarkTextColorHex) ?: DarkOnSurface
    val darkCardContrast = ColorUtils.calculateContrastRatio(darkTextColor, darkCardSurface, darkParent)
    val darkBgContrast = ColorUtils.calculateContrastRatio(darkTextColor, darkParent, darkParent)
    val darkMinContrast = minOf(darkCardContrast, darkBgContrast)
    val hasDarkContrastFailure = darkMinContrast < ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "৫. গ্লোবাল থিম ও রঙের প্যালেট",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onResetCategory,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_reset_global_theme_category")
                ) {
                    Text("ডিফল্ট কালার", fontSize = 12.sp)
                }
            }

            Text(
                text = "লাইট ও ডার্ক মোডের প্রাথমিক, কার্ড সারফেস ও লেখার রঙ কাস্টমাইজ করুন।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // App Theme Mode Selector (System / Light / Dark)
            Text(
                text = "অ্যাপ থিম মোড (Theme Mode):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppThemeMode.values().forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { onSetThemeMode(mode) },
                        label = { Text(mode.titleBn, fontSize = 12.sp) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chip_theme_${mode.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Global Accent Color
            Text(
                text = "অ্যাপের প্রাথমিক রঙ (Primary Theme Color):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ColorUtils.PRESET_PALETTES) { (paletteColor, label) ->
                    val hexStr = ColorUtils.toHex(paletteColor)
                    val isSelected = global.customPrimaryActionHex.equals(hexStr, ignoreCase = true)

                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = {
                                    onUpdateGlobal { it.copy(customPrimaryActionHex = hexStr, customAccentHex = hexStr) }
                                }
                            )
                            .semantics {
                                contentDescription = "প্রাথমিক রঙ $label"
                                stateDescription = if (isSelected) "নির্বাচিত" else "নির্বাচিত নয়"
                            }
                            .testTag("global_primary_swatch_${hexStr.removePrefix("#").lowercase()}"),
                        shape = CircleShape,
                        color = paletteColor,
                        border = if (isSelected) BorderStroke(3.dp, Color.White) else null
                    ) {
                        if (isSelected) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Light Background Palette
            val lightBgOptions = listOf(
                Color(0xFFF8FAFC) to "স্লেট হালকা",
                Color(0xFFF1F5F9) to "হালকা গ্রে",
                Color(0xFFEFF6FF) to "হালকা ব্লু",
                Color(0xFFF0FDF4) to "হালকা সবুজ",
                Color(0xFFFFFBEB) to "হালকা হলুদ"
            )
            SectionColorSwatchRow(
                titleBn = "লাইট মোড ব্যাকগ্রাউন্ড রঙ:",
                selectedHex = global.customLightBackgroundHex,
                options = lightBgOptions,
                testTagPrefix = "light_bg_swatch",
                clearTestTag = "btn_clear_light_bg",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customLightBackgroundHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Light Card Surface Palette (Unwired Global #4)
            val lightCardOptions = listOf(
                Color(0xFFFFFFFF) to "সাদা",
                Color(0xFFF8FAFC) to "স্লেট হালকা",
                Color(0xFFF1F5F9) to "হালকা গ্রে",
                Color(0xFFEFF6FF) to "হালকা ব্লু",
                Color(0xFFECFDF5) to "মিন্ট হালকা",
                Color(0xFFFEF3C7) to "হালকা অ্যাম্বার",
                Color(0xFF1E293B) to "গাঢ় স্লেট"
            )
            SectionColorSwatchRow(
                titleBn = "লাইট মোড কার্ড সারফেস রঙ:",
                selectedHex = global.customLightCardSurfaceHex,
                options = lightCardOptions,
                testTagPrefix = "light_card_swatch",
                clearTestTag = "btn_clear_light_card_surface",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customLightCardSurfaceHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Light Text Color Palette (Unwired Global #5)
            val lightTextOptions = listOf(
                Color(0xFF0F172A) to "ডিপ স্লেট",
                Color(0xFF1E293B) to "চারকোল",
                Color(0xFF1E3A8A) to "রয়েল ব্লু",
                Color(0xFF065F46) to "ডিপ গ্রিন",
                Color(0xFF7C2D12) to "ডার্ক ব্রাউন",
                Color(0xFF94A3B8) to "হালকা গ্রে (সতর্কতা পরীক্ষা)",
                Color(0xFFFFFFFF) to "সাদা"
            )
            SectionColorSwatchRow(
                titleBn = "লাইট মোড লেখার রঙ:",
                selectedHex = global.customLightTextColorHex,
                options = lightTextOptions,
                testTagPrefix = "light_text_swatch",
                clearTestTag = "btn_clear_light_text_color",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customLightTextColorHex = hex) } }
            )

            if (hasLightContrastFailure) {
                Spacer(modifier = Modifier.height(8.dp))
                GlobalContrastWarningBanner(
                    modeLabelBn = "লাইট মোড",
                    ratio = lightMinContrast,
                    warningTag = "contrast_warning_global_light",
                    fixButtonTag = "btn_fix_contrast_global_light",
                    onFixClick = {
                        val fixedText = resolveDualSurfaceAccessibleTextColor(
                            textColor = lightTextColor,
                            cardSurface = lightCardSurface,
                            parentSurface = lightParent
                        )
                        if (fixedText != null) {
                            onUpdateGlobal { it.copy(customLightTextColorHex = ColorUtils.toHex(fixedText)) }
                        } else {
                            // Card and backdrop conflict; align card surface to default LightSurface and compute accessible text
                            val alignedText = ColorUtils.suggestAccessibleColor(
                                foreground = lightTextColor,
                                background = LightSurface,
                                parentSurface = lightParent,
                                minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
                            )
                            onUpdateGlobal {
                                it.copy(
                                    customLightCardSurfaceHex = ColorUtils.toHex(LightSurface),
                                    customLightTextColorHex = ColorUtils.toHex(alignedText)
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Dark Background Palette
            val darkBgOptions = listOf(
                Color(0xFF0B0F19) to "ডিপ নেভি",
                Color(0xFF111827) to "স্লেট ডার্ক",
                Color(0xFF000000) to "পিওর ওলেড ব্ল্যাক",
                Color(0xFF042F2E) to "ডিপ টিল ডার্ক",
                Color(0xFF1E1E24) to "চারকোল"
            )
            SectionColorSwatchRow(
                titleBn = "ডার্ক মোড ব্যাকগ্রাউন্ড রঙ:",
                selectedHex = global.customDarkBackgroundHex,
                options = darkBgOptions,
                testTagPrefix = "dark_bg_swatch",
                clearTestTag = "btn_clear_dark_bg",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customDarkBackgroundHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dark Card Surface Palette (Unwired Global #6)
            val darkCardOptions = listOf(
                Color(0xFF111827) to "স্লেট ডার্ক",
                Color(0xFF1E293B) to "মিড স্লেট",
                Color(0xFF0F172A) to "ডিপ নেভি",
                Color(0xFF042F2E) to "ডিপ টিল",
                Color(0xFF1E1B4B) to "ডিপ ইন্ডিগো",
                Color(0xFF27272A) to "চারকোল",
                Color(0xFFF8FAFC) to "হালকা স্লেট"
            )
            SectionColorSwatchRow(
                titleBn = "ডার্ক মোড কার্ড সারফেস রঙ:",
                selectedHex = global.customDarkCardSurfaceHex,
                options = darkCardOptions,
                testTagPrefix = "dark_card_swatch",
                clearTestTag = "btn_clear_dark_card_surface",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customDarkCardSurfaceHex = hex) } }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dark Text Color Palette (Unwired Global #7)
            val darkTextOptions = listOf(
                Color(0xFFF8FAFC) to "উজ্জ্বল স্লেট",
                Color(0xFFF1F5F9) to "হালকা সাদা",
                Color(0xFF93C5FD) to "স্কাই ব্লু",
                Color(0xFF5EEAD4) to "ইলেকট্রিক টিল",
                Color(0xFFFCD34D) to "উজ্জ্বল অ্যাম্বার",
                Color(0xFF334155) to "ডার্ক স্লেট (সতর্কতা পরীক্ষা)",
                Color(0xFF0F172A) to "কালচে স্লেট"
            )
            SectionColorSwatchRow(
                titleBn = "ডার্ক মোড লেখার রঙ:",
                selectedHex = global.customDarkTextColorHex,
                options = darkTextOptions,
                testTagPrefix = "dark_text_swatch",
                clearTestTag = "btn_clear_dark_text_color",
                onSelectHex = { hex -> onUpdateGlobal { it.copy(customDarkTextColorHex = hex) } }
            )

            if (hasDarkContrastFailure) {
                Spacer(modifier = Modifier.height(8.dp))
                GlobalContrastWarningBanner(
                    modeLabelBn = "ডার্ক মোড",
                    ratio = darkMinContrast,
                    warningTag = "contrast_warning_global_dark",
                    fixButtonTag = "btn_fix_contrast_global_dark",
                    onFixClick = {
                        val fixedText = resolveDualSurfaceAccessibleTextColor(
                            textColor = darkTextColor,
                            cardSurface = darkCardSurface,
                            parentSurface = darkParent
                        )
                        if (fixedText != null) {
                            onUpdateGlobal { it.copy(customDarkTextColorHex = ColorUtils.toHex(fixedText)) }
                        } else {
                            val alignedText = ColorUtils.suggestAccessibleColor(
                                foreground = darkTextColor,
                                background = DarkSurface,
                                parentSurface = darkParent,
                                minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
                            )
                            onUpdateGlobal {
                                it.copy(
                                    customDarkCardSurfaceHex = ColorUtils.toHex(DarkSurface),
                                    customDarkTextColorHex = ColorUtils.toHex(alignedText)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun resolveDualSurfaceAccessibleTextColor(
    textColor: Color,
    cardSurface: Color,
    parentSurface: Color
): Color? {
    val primaryCandidate = ColorUtils.suggestAccessibleColor(
        foreground = textColor,
        background = cardSurface,
        parentSurface = parentSurface,
        minRatio = ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO
    )
    if (ColorUtils.hasAdequateContrast(primaryCandidate, cardSurface, parentSurface, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO) &&
        ColorUtils.hasAdequateContrast(primaryCandidate, parentSurface, parentSurface, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
    ) {
        return primaryCandidate
    }
    val fallbacks = listOf(Color(0xFF0F172A), Color(0xFFF8FAFC), Color.Black, Color.White)
    return fallbacks.firstOrNull { cand ->
        ColorUtils.hasAdequateContrast(cand, cardSurface, parentSurface, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO) &&
            ColorUtils.hasAdequateContrast(cand, parentSurface, parentSurface, ColorUtils.REQUIRED_TEXT_CONTRAST_RATIO)
    }
}

@Composable
private fun GlobalContrastWarningBanner(
    modeLabelBn: String,
    ratio: Double,
    warningTag: String,
    fixButtonTag: String,
    onFixClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite }
            .testTag(warningTag)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "কনট্রাস্ট সতর্কতা",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "সতর্কতা ($modeLabelBn): বর্তমান টেক্সট কনট্রাস্ট ${String.format(Locale.US, "%.2f", ratio)}:১, প্রয়োজন ন্যূনতম ৪.৫:১।",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedButton(
                onClick = onFixClick,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag(fixButtonTag)
            ) {
                Text("উপযুক্ত রঙ প্রয়োগ করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// CATEGORY 6: LIVE PREVIEW
// ==========================================
@Composable
fun LivePreviewSection(
    previewState: com.example.ui.customization.AppCustomizationState
) {
    // Generate safe mock module list reflecting actual existing mathematical sections
    val mockModules = listOf(
        HomeModuleItem(
            sectionKey = SectionKey.TABLES,
            titleBn = "গুণ ও নামতা (১ থেকে ৪০)",
            subtitleBn = "৪টি রেঞ্জে বিভক্ত: ১-১০, ১১-২০, ২১-৩০, ৩১-৪০",
            badgeBn = "৪০ টি নামতা",
            defaultIcon = SectionKey.TABLES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            testTag = "preview_module_tables",
            onClick = {}
        ),
        HomeModuleItem(
            sectionKey = SectionKey.PRIMES,
            titleBn = "মৌলিক সংখ্যা (১ থেকে ২০০)",
            subtitleBn = "১-১০০ (২৫টি) ও ১০১-২০০ (২১টি) বিশ্লেষণ",
            badgeBn = "৪৬ টি মৌলিক",
            defaultIcon = SectionKey.PRIMES.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF0D9488), Color(0xFF0F766E)),
            testTag = "preview_module_primes",
            onClick = {}
        ),
        HomeModuleItem(
            sectionKey = SectionKey.FORMULAS,
            titleBn = "বীজগণিতীয় সূত্রাবলী",
            subtitleBn = "বর্গ ও ঘনের সূত্র, প্রতিপাদন ও অনুসিদ্ধান্ত",
            badgeBn = "ব্যাখ্যা ও সমাধান",
            defaultIcon = SectionKey.FORMULAS.defaultIcon.toImageVector(),
            defaultGradient = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
            testTag = "preview_module_formulas",
            onClick = {}
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "হোম মডিউল লেআউট রূপরেখা:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = previewState.global.layoutType.displayNameBn.split(" ")[0],
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            CustomModuleRenderer(
                modules = mockModules,
                customState = previewState
            )
        }
    }
}
