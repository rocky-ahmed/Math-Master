package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.math.BengaliNumberFormatter
import com.example.math.CameraPreset3D
import com.example.math.GeoShapeType
import com.example.math.Math3DEngine
import com.example.math.OrbitCameraState
import com.example.math.ProjectedScene2D
import com.example.math.Solid3DShapeType
import com.example.math.SolidAnatomyHighlight
import com.example.math.VisualKnowledgeCheckProvider
import com.example.math.VisualMathEngine
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.MathTopAppBar
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedUiState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Explorer Sections
 * Note: Ordinals 0..3 are preserved for backward compatibility with navigation state restoration tests,
 * and SOLID_3D is added as ordinal 4 (as well as embedded inside GeometryExplorerTab).
 */
enum class VisualExplorerSection(val titleBn: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    GEOMETRY("জ্যামিতি এক্সপ্লোরার", Icons.Default.SquareFoot),
    ALGEBRA("ভিজ্যুয়াল বীজগণিত", Icons.Default.Balance),
    NUMBER_LINE("সংখ্যা রেখা ও ভগ্নাংশ", Icons.Default.LinearScale),
    FORMULAS("সূত্র ভিজ্যুয়ালাইজার", Icons.Default.Functions),
    SOLID_3D("৩ডি ঘনবস্তু স্টুডিও", Icons.Default.Straighten)
}

@Composable
fun VisualExplorerScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedSection by rememberManagedUiState(viewModel, "visual_explorer:selectedSection", VisualExplorerSection.GEOMETRY)

    Column(modifier = modifier.fillMaxSize()) {
        MathTopAppBar(
            title = "ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার",
            subtitle = "২ডি ও ৩ডি ইন্টারঅ্যাক্টিভ ডায়াগ্রাম, নেট আনফোল্ডিং এবং গণিত ল্যাব",
            onBackClick = { viewModel.navigateBack() }
        )

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSection.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().testTag("visual_explorer_tabs")
        ) {
            VisualExplorerSection.entries.forEach { section ->
                Tab(
                    selected = selectedSection == section,
                    onClick = { selectedSection = section },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = section.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(text = section.titleBn, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_${section.name.lowercase()}")
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Main Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSection) {
                VisualExplorerSection.GEOMETRY -> GeometryExplorerTab(viewModel = viewModel)
                VisualExplorerSection.ALGEBRA -> VisualAlgebraTab(viewModel = viewModel)
                VisualExplorerSection.NUMBER_LINE -> NumberLineTab(viewModel = viewModel)
                VisualExplorerSection.FORMULAS -> VisualFormulaTab(viewModel = viewModel)
                VisualExplorerSection.SOLID_3D -> Solid3DExplorerTab(viewModel = viewModel)
            }
        }

        // Interactive Mini Knowledge Check & Cross-Module Practice Links
        VisualKnowledgeCheckAndLinksBar(
            tabIndex = selectedSection.ordinal.coerceAtMost(3),
            viewModel = viewModel
        )
    }
}

@Composable
private fun VisualKnowledgeCheckAndLinksBar(
    tabIndex: Int,
    viewModel: MathViewModel
) {
    val questions = remember(tabIndex) { VisualKnowledgeCheckProvider.getKnowledgeChecksForTab(tabIndex) }
    var expanded by rememberManagedUiState(viewModel, "visual_explorer:kc_expanded:$tabIndex", false)
    var qIdx by rememberManagedUiState(viewModel, "visual_explorer:kc_qIdx:$tabIndex", 0)
    var chosenOpt by remember(tabIndex, qIdx) { mutableStateOf<Int?>(null) }

    val currentQ = questions.getOrNull(qIdx.coerceIn(0, (questions.size - 1).coerceAtLeast(0)))
    val invariantNoteBn = remember(tabIndex) {
        when (tabIndex) {
            0 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): যেকোনো সদৃশ জ্যামিতিক আকৃতির বাহু k গুণ বৃদ্ধি পেলে পরিসীমা k গুণ, ক্ষেত্রফল k² গুণ এবং ৩ডি আয়তন k³ গুণ বৃদ্ধি পায়।"
            1 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সমীকরণের উভয় পক্ষে একই সংখ্যা যোগ, বিয়োগ বা অশূন্য সংখ্যা দ্বারা গুণ-ভাগ করলে সমতার ভারসাম্য অপরিবর্তিত থাকে।"
            2 -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): সংখ্যারেখায় যেকোনো দুটি মূলদ সংখ্যার মধ্যবিন্দু (a+b)/2 নিজেও একটি মূলদ সংখ্যা (অসীম ঘনত্ব ধর্ম)।"
            else -> "গাণিতিক ইনভ্যারিয়েন্ট (Guided Discovery): (a+b)² বর্গক্ষেত্রটি চারটি অংশে বিভক্ত: একটি a² বর্গ, একটি b² বর্গ এবং দুটি ab আয়তক্ষেত্র।"
        }
    }

    val topicIdForTab = when (tabIndex) {
        0 -> "VISUAL_GEOMETRY"
        1 -> "VISUAL_ALGEBRA"
        2 -> "VISUAL_NUMBER_LINE"
        else -> "VISUAL_FORMULAS"
    }

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_knowledge_check_bar")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = invariantNoteBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .testTag("visual_guided_discovery_banner")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ধারণা যাচাই (Mini Knowledge Check) ও অনুশীলন লিংক",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_toggle_visual_kc")
                ) {
                    Text(
                        text = if (expanded) "সংক্ষিপ্ত করুন" else "কুইজ ও অনুশীলন খুলুন",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = expanded && currentQ != null) {
                if (currentQ != null) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentQ.questionBn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (questions.size > 1) {
                                OutlinedButton(
                                    onClick = {
                                        qIdx = (qIdx + 1) % questions.size
                                        chosenOpt = null
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("btn_next_visual_kc")
                                ) {
                                    Text(
                                        text = "পরবর্তী প্রশ্ন (${BengaliNumberFormatter.toBengaliDigits((qIdx + 1).toString())}/${BengaliNumberFormatter.toBengaliDigits(questions.size.toString())})",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(currentQ.optionsBn.size) { idx ->
                                val opt = currentQ.optionsBn[idx]
                                FilterChip(
                                    selected = chosenOpt == idx,
                                    onClick = {
                                        chosenOpt = idx
                                        // BUG-58 & BUG-44 FIX: Record Visual Math knowledge check completion with section title
                                        val sectionTitleBn = when (tabIndex) {
                                            0 -> "ভিজ্যুয়াল গণিত: জ্যামিতি ল্যাব"
                                            1 -> "ভিজ্যুয়াল গণিত: বীজগণিত ভারসাম্য"
                                            2 -> "ভিজ্যুয়াল গণিত: সংখ্যারেখা ও ভগ্নাংশ"
                                            3 -> "ভিজ্যুয়াল গণিত: সূত্রের প্রমাণ"
                                            else -> "ভিজ্যুয়াল গণিত: ৩ডি ঘনবস্তু স্টুডিও"
                                        }
                                        viewModel.recordVisualMathCheckpoint(
                                            topicId = topicIdForTab,
                                            topicTitleBn = sectionTitleBn,
                                            isCorrect = (idx == currentQ.correctIndex)
                                        )
                                    },
                                    label = { Text(opt, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("visual_kc_opt_$idx")
                                )
                            }
                        }

                        if (chosenOpt != null) {
                            val isRight = chosenOpt == currentQ.correctIndex
                            Text(
                                text = if (isRight) "সঠিক উত্তর! ${currentQ.explanationBn}" else "সঠিক উত্তর: ${currentQ.optionsBn[currentQ.correctIndex]}। ${currentQ.explanationBn}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isRight) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.startQuiz(
                                        topic = currentQ.relatedQuizTopic,
                                        count = 10
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_visual_link_quiz"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("সম্পর্কিত কুইজ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.FormulaLibrary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_visual_link_formula"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("সূত্র লাইব্রেরি", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.ProblemSolver) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_visual_link_solver"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("সমস্যা সমাধানকারী", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// A. GEOMETRY EXPLORER TAB (2D + 3D SOLID STUDIO + 2D TRANSFORMATIONS)
// =============================================================================

@Composable
fun GeometryExplorerTab(viewModel: MathViewModel) {
    var selectedShape by rememberManagedUiState(viewModel, "visual_explorer:tab_0:selectedShape", GeoShapeType.RECTANGLE)

    // Sliders / Parameters State
    var baseParam by rememberManagedUiState(viewModel, "visual_explorer:tab_0:baseParam", 10.0)
    var heightParam by rememberManagedUiState(viewModel, "visual_explorer:tab_0:heightParam", 6.0)
    var useExactTriangleSides by rememberManagedUiState(viewModel, "visual_explorer:tab_0:useExactTriangleSides", false)
    var sideAParam by rememberManagedUiState(viewModel, "visual_explorer:tab_0:sideAParam", 7.0)
    var sideCParam by rememberManagedUiState(viewModel, "visual_explorer:tab_0:sideCParam", 7.0)
    var zoomScale by rememberManagedUiState(viewModel, "visual_explorer:tab_0:zoomScale", 1.0f)
    val listState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_0")
    val shapeRowState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_0:shape_row")

    val calcResult = remember(selectedShape, baseParam, heightParam, useExactTriangleSides, sideAParam, sideCParam) {
        when (selectedShape) {
            GeoShapeType.TRIANGLE -> {
                if (useExactTriangleSides) {
                    VisualMathEngine.calculateTriangle(baseParam, heightParam, sideAParam, sideCParam)
                } else {
                    VisualMathEngine.calculateTriangle(baseParam, heightParam)
                }
            }
            GeoShapeType.RECTANGLE -> VisualMathEngine.calculateRectangle(baseParam, heightParam)
            GeoShapeType.SQUARE -> VisualMathEngine.calculateSquare(baseParam)
            GeoShapeType.CIRCLE -> VisualMathEngine.calculateCircle(baseParam)
            GeoShapeType.ANGLES -> {
                val ang = VisualMathEngine.calculateAngle(baseParam)
                com.example.math.GeometryCalculationResult(
                    shapeType = GeoShapeType.ANGLES,
                    primaryArea = ang.degrees,
                    primaryPerimeterOrCircumference = ang.complementDegrees ?: 0.0,
                    isPerimeterExact = true,
                    perimeterNoteBn = "সম্পূরক কোণ: ${ang.supplementDegrees?.let { VisualMathEngine.formatNumberBn(it, 1) } ?: "প্রযোজ্য নয়"}°",
                    formulaExpressionBn = "${ang.angleTypeBn}: θ = ${VisualMathEngine.formatNumberBn(ang.degrees, 1)}°",
                    explanationStepsBn = ang.explanationStepsBn
                )
            }
        }
    }

    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("geometry_explorer_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Interactive 3D Solid Geometry Studio Card
        item {
            Interactive3DSolidStudioCard(
                viewModel = viewModel,
                statePrefix = "visual_explorer:tab_0:solid3d"
            )
        }

        // 1. 2D Shape Selector Chips
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "২ডি জ্যামিতিক আকৃতি নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        state = shapeRowState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(GeoShapeType.entries) { shape ->
                            FilterChip(
                                selected = selectedShape == shape,
                                onClick = {
                                    selectedShape = shape
                                    if (shape == GeoShapeType.ANGLES && baseParam < 20.0) {
                                        baseParam = 60.0
                                    } else if (shape != GeoShapeType.ANGLES && baseParam > 20.0) {
                                        baseParam = 10.0
                                    }
                                },
                                label = { Text(shape.titleBn, fontWeight = FontWeight.Medium) },
                                leadingIcon = if (selectedShape == shape) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.testTag("geo_shape_${shape.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        // 2. Interactive 2D Scaled Canvas Diagram
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedShape.titleBn} (${selectedShape.formulaSummaryBn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        // Zoom Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.6f) },
                                modifier = Modifier.size(38.dp).testTag("geo_zoom_out")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "${BengaliNumberFormatter.toBengaliDigits((zoomScale * 100).toInt().toString())}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { zoomScale = (zoomScale + 0.15f).coerceAtMost(1.6f) },
                                modifier = Modifier.size(38.dp).testTag("geo_zoom_in")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.tertiary
                    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .testTag("geometry_canvas_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            drawGridBackground(gridColor)
                            drawGeometryShape(
                                shapeType = selectedShape,
                                param1 = baseParam,
                                param2 = heightParam,
                                zoom = zoomScale,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                labelColorArgb = onSurfaceArgb,
                                accentColorArgb = primaryArgb,
                                useExactTriangleSides = useExactTriangleSides,
                                sideA = sideAParam,
                                sideC = sideCParam
                            )
                        }
                    }
                }
            }
        }

        // 3. Interactive Dimension Sliders
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "পরিমাপ পরিবর্তন করুন (Interactive Controls)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val param1Label = when (selectedShape) {
                        GeoShapeType.TRIANGLE -> "ভূমি (Base b)"
                        GeoShapeType.RECTANGLE -> "দৈর্ঘ্য (Length a)"
                        GeoShapeType.SQUARE -> "বাহুর দৈর্ঘ্য (Side a)"
                        GeoShapeType.CIRCLE -> "ব্যাসার্ধ (Radius r)"
                        GeoShapeType.ANGLES -> "কোণের মান (Angle θ°)"
                    }

                    DimensionSliderRow(
                        label = param1Label,
                        value = baseParam,
                        valueRange = if (selectedShape == GeoShapeType.ANGLES) 5f..355f else 2f..20f,
                        onValueChange = { baseParam = it.toDouble() },
                        testTag = "geo_slider_param1"
                    )

                    if (selectedShape == GeoShapeType.TRIANGLE || selectedShape == GeoShapeType.RECTANGLE) {
                        val param2Label = if (selectedShape == GeoShapeType.TRIANGLE) "উচ্চতা (Height h)" else "প্রস্থ (Width b)"
                        DimensionSliderRow(
                            label = param2Label,
                            value = heightParam,
                            valueRange = 2f..16f,
                            onValueChange = { heightParam = it.toDouble() },
                            testTag = "geo_slider_param2"
                        )
                    }

                    // BUG-56 FIX: Optional exact 3-side perimeter controls for Triangle
                    if (selectedShape == GeoShapeType.TRIANGLE) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ত্রিভুজের তিন বাহু নির্দিষ্ট করুন (সঠিক পরিসীমার জন্য)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "চালু করলে a, b, c তিন বাহুর সমষ্টি দিয়ে পরিসীমা নির্ণীত হবে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = useExactTriangleSides,
                                onCheckedChange = { useExactTriangleSides = it },
                                modifier = Modifier.testTag("geo_triangle_exact_sides_switch")
                            )
                        }

                        if (useExactTriangleSides) {
                            DimensionSliderRow(
                                label = "বাম বাহু (Side a)",
                                value = sideAParam,
                                valueRange = 2f..20f,
                                onValueChange = { sideAParam = it.toDouble() },
                                testTag = "geo_slider_triangle_side_a"
                            )
                            DimensionSliderRow(
                                label = "ডান বাহু (Side c)",
                                value = sideCParam,
                                valueRange = 2f..20f,
                                onValueChange = { sideCParam = it.toDouble() },
                                testTag = "geo_slider_triangle_side_c"
                            )
                        }
                    }
                }
            }
        }

        // 4. Real-Time Mathematical Metrics & Step-by-Step Proof
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("geo_calculation_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!calcResult.isValid) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = calcResult.errorMessageBn ?: "অবৈধ জ্যামিতিক মান",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricHighlightBox(
                            title = if (selectedShape == GeoShapeType.ANGLES) "প্রদত্ত কোণ (θ)" else "ক্ষেত্রফল (Area)",
                            value = if (selectedShape == GeoShapeType.ANGLES) {
                                "${VisualMathEngine.formatNumberBn(calcResult.primaryArea, 1)}°"
                            } else {
                                VisualMathEngine.formatNumberBn(calcResult.primaryArea, 2)
                            },
                            unit = if (selectedShape == GeoShapeType.ANGLES) "ডিগ্রি" else "বর্গ একক",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f).testTag("geo_metric_area")
                        )

                        val perimTitle = when (selectedShape) {
                            GeoShapeType.CIRCLE -> "পরিধি (Circumference)"
                            GeoShapeType.ANGLES -> "পূরক কোণ (90° - θ)"
                            else -> "পরিসীমা (Perimeter)"
                        }
                        val perimValueText = if (calcResult.isPerimeterExact) {
                            if (selectedShape == GeoShapeType.ANGLES) {
                                "${VisualMathEngine.formatNumberBn(calcResult.primaryPerimeterOrCircumference, 1)}°"
                            } else {
                                VisualMathEngine.formatNumberBn(calcResult.primaryPerimeterOrCircumference, 2)
                            }
                        } else {
                            "অনির্ধারিত"
                        }
                        val perimUnitText = if (calcResult.isPerimeterExact) {
                            if (selectedShape == GeoShapeType.ANGLES) "ডিগ্রি" else "একক"
                        } else {
                            "তিন বাহু প্রয়োজন"
                        }

                        MetricHighlightBox(
                            title = perimTitle,
                            value = perimValueText,
                            unit = perimUnitText,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.weight(1f).testTag("geo_metric_perimeter")
                        )
                    }

                    calcResult.perimeterNoteBn?.let { note ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "নোট: $note",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "গাণিতিক সূত্র: ${calcResult.formulaExpressionBn}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = "ধাপে ধাপে ব্যাখ্যা (Step-by-Step Analysis):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    calcResult.explanationStepsBn.forEachIndexed { idx, step ->
                        StepItemRow(stepNumber = idx + 1, stepText = step)
                    }
                }
            }
        }

        // 5. BUG-55 FIX: Interactive 2D Coordinate Transformation Explorer Card
        item {
            CoordinateTransformation2DCard(
                viewModel = viewModel,
                labelColorArgb = onSurfaceArgb,
                accentColorArgb = primaryArgb
            )
        }
    }
}

// =============================================================================
// DEDICATED 3D SOLID GEOMETRY STUDIO TAB & REUSABLE STUDIO CARD
// =============================================================================

@Composable
fun Solid3DExplorerTab(viewModel: MathViewModel) {
    val listState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_4")
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("solid_3d_explorer_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Interactive3DSolidStudioCard(
                viewModel = viewModel,
                statePrefix = "visual_explorer:tab_4:solid3d"
            )
        }
    }
}

@Composable
fun Interactive3DSolidStudioCard(
    viewModel: MathViewModel,
    statePrefix: String = "visual_explorer:solid3d"
) {
    var selectedSolid by rememberManagedUiState(viewModel, "$statePrefix:type", Solid3DShapeType.CUBE)
    var pitchDeg by rememberManagedUiState(viewModel, "$statePrefix:pitch", CameraPreset3D.ISOMETRIC.pitchDeg)
    var yawDeg by rememberManagedUiState(viewModel, "$statePrefix:yaw", CameraPreset3D.ISOMETRIC.yawDeg)
    var zoom3D by rememberManagedUiState(viewModel, "$statePrefix:zoom", 1.0f)
    var unfoldProgress by rememberManagedUiState(viewModel, "$statePrefix:unfold", 0.0f)
    var volumeFillRatio by rememberManagedUiState(viewModel, "$statePrefix:fill", 0.0f)
    var highlightMode by rememberManagedUiState(viewModel, "$statePrefix:highlight", SolidAnatomyHighlight.ALL)

    // Dimension parameters
    var param1 by rememberManagedUiState(viewModel, "$statePrefix:p1", selectedSolid.defaultP1)
    var param2 by rememberManagedUiState(viewModel, "$statePrefix:p2", selectedSolid.defaultP2)
    var param3 by rememberManagedUiState(viewModel, "$statePrefix:p3", selectedSolid.defaultP3)

    val mensuration = remember(selectedSolid, param1, param2, param3) {
        Math3DEngine.calculateSolidMetrics(selectedSolid, param1, param2, param3)
    }

    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_3d_solid_studio_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ইন্টারঅ্যাক্টিভ ৩ডি ঘনবস্তু ও নেট স্টুডিও (3D Solid Studio)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "৩৬০° ঘূর্ণন, ৩ডি→২ডি নেট আনফোল্ডিং, আয়তন পূর্ণতা এবং পরিমিতি বিশ্লেষণ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1. 3D Solid Type Selector Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().testTag("solid_3d_selector_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Solid3DShapeType.entries) { solid ->
                    FilterChip(
                        selected = selectedSolid == solid,
                        onClick = {
                            selectedSolid = solid
                            param1 = solid.defaultP1
                            param2 = solid.defaultP2
                            param3 = solid.defaultP3
                        },
                        label = {
                            Text(
                                text = solid.titleBn,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = if (selectedSolid == solid) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier.testTag("solid_3d_chip_${solid.name.lowercase()}")
                    )
                }
            }

            // 2. Camera Presets & Reset Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(CameraPreset3D.entries) { preset ->
                    FilterChip(
                        selected = abs(pitchDeg - preset.pitchDeg) < 2f && abs(yawDeg - preset.yawDeg) < 2f,
                        onClick = {
                            pitchDeg = preset.pitchDeg
                            yawDeg = preset.yawDeg
                        },
                        label = { Text(preset.titleBn, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("solid_3d_cam_${preset.name.lowercase()}")
                    )
                }
                item {
                    OutlinedButton(
                        onClick = {
                            pitchDeg = CameraPreset3D.ISOMETRIC.pitchDeg
                            yawDeg = CameraPreset3D.ISOMETRIC.yawDeg
                            zoom3D = 1.0f
                            unfoldProgress = 0.0f
                            volumeFillRatio = 0.0f
                            highlightMode = SolidAnatomyHighlight.ALL
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("solid_3d_reset_btn")
                    ) {
                        Text("রিসেট ভিউ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. Interactive 3D Viewport Canvas (Drag-to-Orbit + Perspective + Painter's Algorithm)
            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .pointerInput(selectedSolid) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            yawDeg = ((yawDeg + dragAmount.x * 0.45f) % 360f + 360f) % 360f
                            pitchDeg = (pitchDeg - dragAmount.y * 0.45f).coerceIn(-80f, 80f)
                        }
                    }
                    .testTag("solid_3d_viewport_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    drawGridBackground(gridColor)
                    val (faces, internalLines) = Math3DEngine.buildSolidMesh(
                        shape = selectedSolid,
                        p1 = param1,
                        p2 = param2,
                        p3 = param3,
                        unfoldProgress = unfoldProgress,
                        volumeFillFraction = volumeFillRatio
                    )
                    val scene = Math3DEngine.projectScene(
                        faces = faces,
                        internalLines = internalLines,
                        camera = OrbitCameraState(
                            pitchDeg = pitchDeg,
                            yawDeg = yawDeg,
                            zoom = zoom3D
                        ),
                        canvasWidth = size.width,
                        canvasHeight = size.height
                    )
                    drawProjected3DScene(
                        scene = scene,
                        highlightMode = highlightMode,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        labelColorArgb = onSurfaceArgb,
                        accentColorArgb = primaryArgb
                    )
                }

                // Overlay badge showing current rotation & mode
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Pitch: ${BengaliNumberFormatter.toBengaliDigits(pitchDeg.toInt().toString())}° | Yaw: ${BengaliNumberFormatter.toBengaliDigits(yawDeg.toInt().toString())}° (আঙুল টেনে ঘোরান)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Topology Badge (F / E / V)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "তল (F): ${BengaliNumberFormatter.toBengaliDigits(mensuration.facesCount.toString())} | ধার (E): ${BengaliNumberFormatter.toBengaliDigits(mensuration.edgesCount.toString())} | শীর্ষ (V): ${BengaliNumberFormatter.toBengaliDigits(mensuration.verticesCount.toString())}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 4. Anatomy Highlight Selector Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(SolidAnatomyHighlight.entries) { mode ->
                    FilterChip(
                        selected = highlightMode == mode,
                        onClick = { highlightMode = mode },
                        label = { Text(mode.titleBn, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("solid_3d_highlight_${mode.name.lowercase()}")
                    )
                }
            }

            // 5. 3D-to-2D Net Unfolding & Volume Fill Sliders + Accessible Pitch/Yaw Sliders
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedSolid.supportsNetUnfold) {
                        DimensionSliderRow(
                            label = "৩ডি → ২ডি নেট আনফোল্ডিং (Surface Net Unfold %)",
                            value = (unfoldProgress * 100f).toDouble(),
                            valueRange = 0f..100f,
                            onValueChange = { unfoldProgress = (it / 100f).coerceIn(0f, 1f) },
                            testTag = "solid_3d_unfold_slider"
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("sphere_net_limitation_banner")
                        ) {
                            Text(
                                text = "গাণিতিক সীমাবদ্ধতা (Theorema Egregium): গোলকের গউসীয় বক্রতা ধনাত্মক (K = 1/r² > 0) হওয়ায় ঘনকের মতো এর কোনো বিকৃতিহীন (Distortion-free) সমতল বহুভুজ নেট নেই। এর পৃষ্ঠতলের ক্ষেত্রফল ৪টি মহাবৃত্তের ক্ষেত্রফলের (4πr²) সমান।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    DimensionSliderRow(
                        label = "উচ্চতা অনুযায়ী পূর্ণতা (Height Fill Level %)",
                        value = (volumeFillRatio * 100f).toDouble(),
                        valueRange = 0f..100f,
                        onValueChange = { volumeFillRatio = (it / 100f).coerceIn(0f, 1f) },
                        testTag = "solid_3d_fill_slider"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "উল্লম্ব কোণ (Pitch: ${BengaliNumberFormatter.toBengaliDigits(pitchDeg.toInt().toString())}°)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Slider(
                                value = pitchDeg,
                                onValueChange = { pitchDeg = it },
                                valueRange = -80f..80f,
                                modifier = Modifier.testTag("solid_3d_pitch_slider")
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "অনুভূমিক কোণ (Yaw: ${BengaliNumberFormatter.toBengaliDigits(yawDeg.toInt().toString())}°)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Slider(
                                value = yawDeg,
                                onValueChange = { yawDeg = it },
                                valueRange = 0f..360f,
                                modifier = Modifier.testTag("solid_3d_yaw_slider")
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Solid-specific dimension sliders
                    DimensionSliderRow(
                        label = selectedSolid.param1LabelBn,
                        value = param1,
                        valueRange = 1.5f..10f,
                        onValueChange = { param1 = it.toDouble() },
                        testTag = "solid_3d_param_1"
                    )

                    selectedSolid.param2LabelBn?.let { label2 ->
                        DimensionSliderRow(
                            label = label2,
                            value = param2,
                            valueRange = 1.5f..10f,
                            onValueChange = { param2 = it.toDouble() },
                            testTag = "solid_3d_param_2"
                        )
                    }

                    selectedSolid.param3LabelBn?.let { label3 ->
                        DimensionSliderRow(
                            label = label3,
                            value = param3,
                            valueRange = 1.5f..10f,
                            onValueChange = { param3 = it.toDouble() },
                            testTag = "solid_3d_param_3"
                        )
                    }
                }
            }

            // 6. 3D Mensuration Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricHighlightBox(
                    title = "আয়তন (Volume V)",
                    value = VisualMathEngine.formatNumberBn(mensuration.volume, 2),
                    unit = "ঘন একক",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("solid_3d_metric_volume")
                )
                MetricHighlightBox(
                    title = "সমগ্রতলের ক্ষেত্রফল (TSA)",
                    value = VisualMathEngine.formatNumberBn(mensuration.totalSurfaceArea, 2),
                    unit = "বর্গ একক",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("solid_3d_metric_tsa")
                )
                MetricHighlightBox(
                    title = mensuration.secondaryMetricLabelBn,
                    value = VisualMathEngine.formatNumberBn(mensuration.secondaryMetricValue, 2),
                    unit = "একক",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("solid_3d_metric_aux")
                )
            }

            if (volumeFillRatio > 0.01f) {
                val fillAnalysis = Math3DEngine.calculateVolumeFill(
                    shape = selectedSolid,
                    p1 = param1,
                    p2 = param2,
                    p3 = param3,
                    heightFillFraction = volumeFillRatio
                )
                val heightPctBn = VisualMathEngine.formatNumberBn(fillAnalysis.heightFillFraction * 100.0, 1)
                val volPctBn = VisualMathEngine.formatNumberBn(fillAnalysis.volumeFillFraction * 100.0, 2)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("solid_3d_volume_fill_summary")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "আংশিক পূর্ণ আয়তন ($heightPctBn% উচ্চতা → প্রকৃত আয়তন $volPctBn%): V_fill = ${VisualMathEngine.formatNumberBn(fillAnalysis.filledVolume, 2)} ঘন একক",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${fillAnalysis.formulaRelationBn} — ${fillAnalysis.explanationBn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Text(
                text = "${mensuration.volumeFormulaBn}  |  ${mensuration.surfaceAreaFormulaBn}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (unfoldProgress > 0.1f) mensuration.netExplanationBn else mensuration.conceptSummaryBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            mensuration.calculationStepsBn.forEachIndexed { idx, step ->
                StepItemRow(stepNumber = idx + 1, stepText = step)
            }
        }
    }
}

private fun DrawScope.drawProjected3DScene(
    scene: ProjectedScene2D,
    highlightMode: SolidAnatomyHighlight,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    labelColorArgb: Int,
    accentColorArgb: Int
) {
    val faceAlphaMultiplier = when (highlightMode) {
        SolidAnatomyHighlight.ALL -> 0.74f
        SolidAnatomyHighlight.FACES -> 0.88f
        SolidAnatomyHighlight.EDGES, SolidAnatomyHighlight.VERTICES, SolidAnatomyHighlight.DIAGONAL_OR_AXIS -> 0.22f
    }

    scene.facesBackToFront.forEach { face ->
        if (face.points.size >= 3) {
            val path = Path().apply {
                moveTo(face.points[0].x, face.points[0].y)
                for (i in 1 until face.points.size) {
                    lineTo(face.points[i].x, face.points[i].y)
                }
                close()
            }
            val roleColor = when (face.colorRole) {
                0 -> primaryColor
                1 -> secondaryColor
                2 -> tertiaryColor
                else -> Color(0xFF0EA5E9)
            }
            val alpha = if (face.isVolumeFill) 0.58f else faceAlphaMultiplier
            val shadedColor = Color(
                red = (roleColor.red * face.lightIntensity).coerceIn(0f, 1f),
                green = (roleColor.green * face.lightIntensity).coerceIn(0f, 1f),
                blue = (roleColor.blue * face.lightIntensity).coerceIn(0f, 1f),
                alpha = alpha
            )
            drawPath(path = path, color = shadedColor, style = Fill)

            val strokeAlpha = if (highlightMode == SolidAnatomyHighlight.EDGES) 1.0f else 0.78f
            val strokeWidth = if (highlightMode == SolidAnatomyHighlight.EDGES) 3.5f else 1.8f
            drawPath(
                path = path,
                color = roleColor.copy(alpha = strokeAlpha),
                style = Stroke(width = strokeWidth)
            )
        }
    }

    // Draw internal axes / diagonals / slant height lines
    scene.internalLines.forEach { line ->
        val isHighlighted = highlightMode == SolidAnatomyHighlight.DIAGONAL_OR_AXIS || highlightMode == SolidAnatomyHighlight.ALL
        drawLine(
            color = if (isHighlighted) Color(0xFFDC2626) else Color(0xFFDC2626).copy(alpha = 0.45f),
            start = Offset(line.start.x, line.start.y),
            end = Offset(line.end.x, line.end.y),
            strokeWidth = if (highlightMode == SolidAnatomyHighlight.DIAGONAL_OR_AXIS) 5f else 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
        line.labelBn?.let { lbl ->
            val midX = (line.start.x + line.end.x) / 2f
            val midY = (line.start.y + line.end.y) / 2f
            drawCanvasText(lbl, midX + 12f, midY - 8f, Color(0xFFDC2626).toArgb(), 24f)
        }
    }

    // Draw Vertices when highlighted or in ALL mode
    if (highlightMode == SolidAnatomyHighlight.VERTICES || highlightMode == SolidAnatomyHighlight.ALL) {
        val vertexRadius = if (highlightMode == SolidAnatomyHighlight.VERTICES) 7.5f else 4.0f
        scene.uniqueVertices.forEach { v ->
            drawCircle(
                color = tertiaryColor,
                radius = vertexRadius,
                center = Offset(v.x, v.y)
            )
        }
    }

    // Draw Face Labels when in FACES highlight mode
    if (highlightMode == SolidAnatomyHighlight.FACES) {
        scene.facesBackToFront.takeLast(6).forEach { face ->
            val lbl = face.labelBn
            if (!lbl.isNullOrBlank()) {
                drawCanvasText(lbl, face.center2D.x, face.center2D.y, labelColorArgb, 24f)
            }
        }
    }
}

// =============================================================================
// BUG-55 FIX: INTERACTIVE 2D COORDINATE TRANSFORMATION CARD
// =============================================================================

@Composable
private fun CoordinateTransformation2DCard(
    viewModel: MathViewModel,
    labelColorArgb: Int,
    accentColorArgb: Int
) {
    var pointX by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trans_x", 3.0)
    var pointY by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trans_y", 4.0)
    var transformType by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trans_type", "reflect_x")
    var translateDelta by rememberManagedUiState(viewModel, "visual_explorer:tab_0:trans_dx", 2.0)

    val result = remember(pointX, pointY, transformType, translateDelta) {
        VisualMathEngine.calculate2DTransformation(pointX, pointY, transformType, translateDelta)
    }

    val transformOptions = listOf(
        "reflect_x" to "x-অক্ষে প্রতিফলন",
        "reflect_y" to "y-অক্ষে প্রতিফলন",
        "rotate_90" to "৯০° ঘূর্ণন",
        "rotate_180" to "১৮০° ঘূর্ণন",
        "translate" to "স্থানান্তর (Translate)"
    )

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("transformation_2d_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "স্থানাঙ্ক রূপান্তর ল্যাব (2D Coordinate Transformations)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "প্রতিফলন (Reflection), ঘূর্ণন (Rotation) ও স্থানান্তর (Translation) সরাসরি গ্রাফে দেখুন",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(transformOptions) { (key, labelBn) ->
                    FilterChip(
                        selected = transformType == key,
                        onClick = { transformType = key },
                        label = { Text(labelBn, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("trans_type_$key")
                    )
                }
            }

            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    drawGridBackground(gridColor)
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val unitPx = (min(size.width, size.height) / 14f).coerceAtLeast(8f)

                    // Axes
                    drawLine(axisColor, Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2f)
                    drawLine(axisColor, Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2f)

                    val origScreen = Offset(cx + pointX.toFloat() * unitPx, cy - pointY.toFloat() * unitPx)
                    val transScreen = Offset(cx + result.transformedX.toFloat() * unitPx, cy - result.transformedY.toFloat() * unitPx)

                    // Dashed connector
                    drawLine(
                        color = tertiaryColor.copy(alpha = 0.7f),
                        start = origScreen,
                        end = transScreen,
                        strokeWidth = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )

                    // Original point P
                    drawCircle(color = primaryColor, radius = 8f, center = origScreen)
                    drawCanvasText(
                        "P(${VisualMathEngine.formatNumberBn(pointX, 0)},${VisualMathEngine.formatNumberBn(pointY, 0)})",
                        (origScreen.x + 38f).coerceIn(40f, size.width - 40f),
                        (origScreen.y - 12f).coerceIn(20f, size.height - 12f),
                        accentColorArgb,
                        24f
                    )

                    // Transformed point P'
                    drawCircle(color = Color(0xFFDC2626), radius = 8f, center = transScreen)
                    drawCanvasText(
                        "P'(${VisualMathEngine.formatNumberBn(result.transformedX, 0)},${VisualMathEngine.formatNumberBn(result.transformedY, 0)})",
                        (transScreen.x + 42f).coerceIn(40f, size.width - 40f),
                        (transScreen.y + 22f).coerceIn(20f, size.height - 12f),
                        Color(0xFFDC2626).toArgb(),
                        24f
                    )
                }
            }

            DimensionSliderRow(
                label = "বিন্দু x স্থানাঙ্ক (x)",
                value = pointX,
                valueRange = -5f..5f,
                onValueChange = { pointX = it.toInt().toDouble() },
                testTag = "trans_slider_x"
            )
            DimensionSliderRow(
                label = "বিন্দু y স্থানাঙ্ক (y)",
                value = pointY,
                valueRange = -5f..5f,
                onValueChange = { pointY = it.toInt().toDouble() },
                testTag = "trans_slider_y"
            )

            if (transformType == "translate") {
                DimensionSliderRow(
                    label = "সরণ মান (Translation Δ)",
                    value = translateDelta,
                    valueRange = -4f..4f,
                    onValueChange = { translateDelta = it.toInt().toDouble() },
                    testTag = "trans_slider_dx"
                )
            }

            Text(
                text = "${result.ruleFormulaBn} — ${result.explanationBn}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun DrawScope.drawGridBackground(gridColor: Color) {
    val step = 24.dp.toPx()
    var x = 0f
    while (x <= size.width) {
        drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(color = gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
        y += step
    }
}

private fun DrawScope.drawGeometryShape(
    shapeType: GeoShapeType,
    param1: Double,
    param2: Double,
    zoom: Float,
    primaryColor: Color,
    secondaryColor: Color,
    labelColorArgb: Int,
    accentColorArgb: Int,
    useExactTriangleSides: Boolean = false,
    sideA: Double = param2,
    sideC: Double = param2
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxCanvasDim = min(size.width, size.height) * 0.72f

    when (shapeType) {
        GeoShapeType.RECTANGLE -> {
            // BUG-56 FIX: Apply zoomScale to Rectangle
            val maxInput = max(param1, param2).coerceAtLeast(1.0).toFloat()
            val scale = (maxCanvasDim / maxInput) * zoom.coerceIn(0.6f, 1.5f)
            val rectW = (param1.toFloat() * scale).coerceIn(40f, size.width * 0.92f)
            val rectH = (param2.toFloat() * scale).coerceIn(40f, size.height * 0.88f)
            val topLeft = Offset(cx - rectW / 2f, cy - rectH / 2f)

            drawRect(
                color = primaryColor.copy(alpha = 0.18f),
                topLeft = topLeft,
                size = Size(rectW, rectH)
            )
            drawRect(
                color = primaryColor,
                topLeft = topLeft,
                size = Size(rectW, rectH),
                style = Stroke(width = 4f)
            )

            drawCanvasText(
                text = "a = ${VisualMathEngine.formatNumberBn(param1, 1)}",
                x = cx,
                y = (topLeft.y + rectH + 26f).coerceAtMost(size.height - 4f),
                colorArgb = accentColorArgb
            )
            drawCanvasText(
                text = "b = ${VisualMathEngine.formatNumberBn(param2, 1)}",
                x = (topLeft.x + rectW + 42f).coerceAtMost(size.width - 36f),
                y = cy,
                colorArgb = accentColorArgb
            )
        }

        GeoShapeType.SQUARE -> {
            // BUG-56 FIX: Apply zoomScale to Square
            val sidePx = ((param1.toFloat() / 20f) * maxCanvasDim * zoom).coerceIn(55f, maxCanvasDim * 1.15f)
            val topLeft = Offset(cx - sidePx / 2f, cy - sidePx / 2f)

            drawRect(
                color = primaryColor.copy(alpha = 0.18f),
                topLeft = topLeft,
                size = Size(sidePx, sidePx)
            )
            drawRect(
                color = primaryColor,
                topLeft = topLeft,
                size = Size(sidePx, sidePx),
                style = Stroke(width = 4f)
            )
            drawLine(
                color = secondaryColor,
                start = topLeft,
                end = Offset(topLeft.x + sidePx, topLeft.y + sidePx),
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )

            drawCanvasText(
                text = "a = ${VisualMathEngine.formatNumberBn(param1, 1)}",
                x = cx,
                y = (topLeft.y + sidePx + 26f).coerceAtMost(size.height - 4f),
                colorArgb = accentColorArgb
            )
        }

        GeoShapeType.TRIANGLE -> {
            // BUG-56 FIX: Apply zoomScale and render true 3-side geometry when useExactTriangleSides is active
            val maxInput = max(param1, max(param2, if (useExactTriangleSides) max(sideA, sideC) else 1.0)).coerceAtLeast(1.0).toFloat()
            val scale = (maxCanvasDim / maxInput) * zoom.coerceIn(0.6f, 1.5f)
            val basePx = (param1.toFloat() * scale).coerceIn(50f, size.width * 0.90f)
            val heightPx = (param2.toFloat() * scale).coerceIn(50f, size.height * 0.84f)

            val bottomLeft = Offset(cx - basePx / 2f, cy + heightPx / 2f)
            val bottomRight = Offset(cx + basePx / 2f, cy + heightPx / 2f)

            val apexOffsetXFraction = if (useExactTriangleSides && param1 > 0.0 &&
                (sideA + param1 > sideC) && (sideA + sideC > param1) && (param1 + sideC > sideA)
            ) {
                val xProj = (sideA * sideA + param1 * param1 - sideC * sideC) / (2.0 * param1)
                (xProj / param1).toFloat().coerceIn(0.05f, 0.95f)
            } else {
                0.5f
            }

            val apexX = (bottomLeft.x + basePx * apexOffsetXFraction).coerceIn(24f, size.width - 24f)
            val topVertex = Offset(apexX, cy - heightPx / 2f)
            val footOfAltitude = Offset(apexX, cy + heightPx / 2f)

            val path = Path().apply {
                moveTo(topVertex.x, topVertex.y)
                lineTo(bottomLeft.x, bottomLeft.y)
                lineTo(bottomRight.x, bottomRight.y)
                close()
            }

            drawPath(path = path, color = primaryColor.copy(alpha = 0.18f), style = Fill)
            drawPath(path = path, color = primaryColor, style = Stroke(width = 4f))

            drawLine(
                color = secondaryColor,
                start = topVertex,
                end = footOfAltitude,
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )

            drawCanvasText(
                text = "b = ${VisualMathEngine.formatNumberBn(param1, 1)}",
                x = cx,
                y = (bottomLeft.y + 26f).coerceAtMost(size.height - 4f),
                colorArgb = accentColorArgb
            )
            drawCanvasText(
                text = "h = ${VisualMathEngine.formatNumberBn(param2, 1)}",
                x = (apexX + 34f).coerceAtMost(size.width - 36f),
                y = cy + 6f,
                colorArgb = secondaryColor.toArgb()
            )
            if (useExactTriangleSides) {
                drawCanvasText(
                    text = "a = ${VisualMathEngine.formatNumberBn(sideA, 1)}",
                    x = ((bottomLeft.x + topVertex.x) / 2f - 28f).coerceAtLeast(28f),
                    y = (bottomLeft.y + topVertex.y) / 2f,
                    colorArgb = labelColorArgb
                )
                drawCanvasText(
                    text = "c = ${VisualMathEngine.formatNumberBn(sideC, 1)}",
                    x = ((bottomRight.x + topVertex.x) / 2f + 28f).coerceAtMost(size.width - 28f),
                    y = (bottomRight.y + topVertex.y) / 2f,
                    colorArgb = labelColorArgb
                )
            }
        }

        GeoShapeType.CIRCLE -> {
            val radiusPx = ((param1.toFloat() / 20f) * (maxCanvasDim / 2f) * zoom).coerceIn(32f, maxCanvasDim * 0.58f)
            val center = Offset(cx, cy)

            drawCircle(
                color = primaryColor.copy(alpha = 0.18f),
                radius = radiusPx,
                center = center
            )
            drawCircle(
                color = primaryColor,
                radius = radiusPx,
                center = center,
                style = Stroke(width = 4f)
            )
            drawCircle(color = secondaryColor, radius = 5f, center = center)
            drawLine(
                color = secondaryColor,
                start = center,
                end = Offset(center.x + radiusPx, center.y),
                strokeWidth = 3.5f
            )

            drawCanvasText(
                text = "r = ${VisualMathEngine.formatNumberBn(param1, 1)}",
                x = center.x + radiusPx / 2f,
                y = center.y - 12f,
                colorArgb = secondaryColor.toArgb()
            )
        }

        GeoShapeType.ANGLES -> {
            val armLength = (maxCanvasDim * 0.48f * zoom).coerceIn(55f, maxCanvasDim * 0.65f)
            val origin = Offset(cx - armLength * 0.3f, cy + armLength * 0.3f)
            val rad = Math.toRadians(param1)
            val endArm1 = Offset(origin.x + armLength, origin.y)
            val endArm2 = Offset(
                origin.x + (armLength * cos(rad)).toFloat(),
                origin.y - (armLength * sin(rad)).toFloat()
            )

            drawLine(color = primaryColor, start = origin, end = endArm1, strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(color = secondaryColor, start = origin, end = endArm2, strokeWidth = 5f, cap = StrokeCap.Round)
            drawArc(
                color = primaryColor.copy(alpha = 0.28f),
                startAngle = 0f,
                sweepAngle = -param1.toFloat(),
                useCenter = true,
                topLeft = Offset(origin.x - 45f, origin.y - 45f),
                size = Size(90f, 90f)
            )
            drawCanvasText(
                text = "θ = ${VisualMathEngine.formatNumberBn(param1, 1)}°",
                x = origin.x + 65f,
                y = origin.y - 25f,
                colorArgb = labelColorArgb
            )
        }
    }
}

private fun DrawScope.drawCanvasText(
    text: String,
    x: Float,
    y: Float,
    colorArgb: Int = android.graphics.Color.DKGRAY,
    textSizePx: Float = 28f
) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = colorArgb
            textSize = textSizePx
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        drawText(text, x, y, paint)
    }
}

// =============================================================================
// B. VISUAL ALGEBRA TAB (BALANCE-SCALE EQUATION SOLVER WITH TILT PHYSICS)
// =============================================================================

@Composable
fun VisualAlgebraTab(viewModel: MathViewModel) {
    var coeffA by rememberManagedUiState(viewModel, "visual_explorer:tab_1:coeffA", 2.0)
    var constB by rememberManagedUiState(viewModel, "visual_explorer:tab_1:constB", 4.0)
    var rhsC by rememberManagedUiState(viewModel, "visual_explorer:tab_1:rhsC", 14.0)
    var activeStepIndex by rememberManagedUiState(viewModel, "visual_explorer:tab_1:activeStepIndex", 0)
    var useTrialX by rememberManagedUiState(viewModel, "visual_explorer:tab_1:useTrialX", false)
    var trialX by rememberManagedUiState(viewModel, "visual_explorer:tab_1:trialX", 5.0)
    val listState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_1")

    val algebraResult = remember(coeffA, constB, rhsC) {
        VisualMathEngine.solveVisualAlgebra(coeffA, constB, rhsC)
    }

    val safeStepIdx = activeStepIndex.coerceIn(0, (algebraResult.steps.size - 1).coerceAtLeast(0))
    val currentStep = algebraResult.steps.getOrNull(safeStepIdx)

    val presets = listOf(
        Triple(2.0, 4.0, 14.0) to "2x + 4 = 14",
        Triple(3.0, 3.0, 18.0) to "3x + 3 = 18",
        Triple(4.0, -2.0, 14.0) to "4x - 2 = 14",
        Triple(5.0, 5.0, 30.0) to "5x + 5 = 30",
        Triple(2.0, -6.0, 10.0) to "2x - 6 = 10"
    )

    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("visual_algebra_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Preset Selector + Custom Equation Sliders (BUG-58 FIX)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "সমীকরণ নির্বাচন বা কাস্টমাইজ করুন (ax + b = c):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().testTag("algebra_presets_row"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(presets.size) { index ->
                            val (triple, label) = presets[index]
                            val isSelected = coeffA == triple.first && constB == triple.second && rhsC == triple.third
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    coeffA = triple.first
                                    constB = triple.second
                                    rhsC = triple.third
                                    activeStepIndex = 0
                                    trialX = ((triple.third - triple.second) / triple.first)
                                },
                                label = { Text(label, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("algebra_preset_$index")
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    DimensionSliderRow(
                        label = "সহগ a (x-এর গুণিতক)",
                        value = coeffA,
                        valueRange = 1f..8f,
                        onValueChange = {
                            coeffA = it.toInt().coerceAtLeast(1).toDouble()
                            activeStepIndex = 0
                        },
                        testTag = "algebra_slider_a"
                    )
                    DimensionSliderRow(
                        label = "ধ্রুবক b (বাম পক্ষের ধ্রুবক)",
                        value = constB,
                        valueRange = -10f..12f,
                        onValueChange = {
                            constB = it.toInt().toDouble()
                            activeStepIndex = 0
                        },
                        testTag = "algebra_slider_b"
                    )
                    DimensionSliderRow(
                        label = "ডান পক্ষ c (RHS)",
                        value = rhsC,
                        valueRange = -6f..36f,
                        onValueChange = {
                            rhsC = it.toInt().toDouble()
                            activeStepIndex = 0
                        },
                        testTag = "algebra_slider_c"
                    )
                }
            }
        }

        // 2. Balance Scale Visual Canvas with Real Tilt Physics (BUG-58 FIX)
        item {
            val activeXForTilt = if (useTrialX) trialX else algebraResult.solutionX
            val lhsVal = coeffA * activeXForTilt + constB
            val rhsVal = rhsC
            val diff = rhsVal - lhsVal
            val tiltAngleDeg = if (useTrialX) (diff.toFloat() * 1.8f).coerceIn(-16f, 16f) else 0f

            val displayXCount = when (safeStepIdx) {
                0, 1 -> coeffA.toInt().coerceAtLeast(1)
                else -> 1
            }
            val displayLeftConst = if (safeStepIdx == 0) constB.toInt() else 0
            val displayRightConst = when (safeStepIdx) {
                0 -> rhsC.toInt()
                1 -> (rhsC - constB).toInt()
                else -> algebraResult.solutionX.toInt()
            }

            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "সমতার পাল্লা (Equation Balance Scale)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentStep?.let { "${it.leftExprBn}  =  ${it.rightExprBn}" } ?: "${VisualMathEngine.formatNumberBn(coeffA, 0)}x + ${VisualMathEngine.formatNumberBn(constB, 0)} = ${VisualMathEngine.formatNumberBn(rhsC, 0)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.testTag("algebra_active_equation_text")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val scaleBeamColor = MaterialTheme.colorScheme.primary
                    val xBoxColor = MaterialTheme.colorScheme.tertiary
                    val constBoxColor = MaterialTheme.colorScheme.secondary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .testTag("algebra_balance_canvas"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            drawBalanceScale(
                                xCount = displayXCount,
                                leftConst = displayLeftConst,
                                rightConst = displayRightConst,
                                tiltAngleDeg = tiltAngleDeg,
                                beamColor = scaleBeamColor,
                                xBoxColor = xBoxColor,
                                constColor = constBoxColor,
                                labelColorArgb = onSurfaceArgb
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "নিজে x-এর মান বসিয়ে পাল্লার ভারসাম্য পরীক্ষা করুন:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = useTrialX,
                            onCheckedChange = {
                                useTrialX = it
                                if (!it) trialX = algebraResult.solutionX
                            },
                            modifier = Modifier.testTag("algebra_trial_x_switch")
                        )
                    }

                    if (useTrialX) {
                        DimensionSliderRow(
                            label = "পরীক্ষামূলক x (Trial x)",
                            value = trialX,
                            valueRange = -5f..15f,
                            onValueChange = { trialX = (Math.round(it * 2.0) / 2.0) },
                            testTag = "algebra_trial_x_slider"
                        )
                        val isBalanced = abs(diff) < 0.05
                        Text(
                            text = if (isBalanced) {
                                "পাল্লা সম্পূর্ণ সমতলে আছে! LHS (${VisualMathEngine.formatNumberBn(lhsVal, 1)}) = RHS (${VisualMathEngine.formatNumberBn(rhsVal, 1)})"
                            } else {
                                "পাল্লা কাত হয়ে আছে: LHS = ${VisualMathEngine.formatNumberBn(lhsVal, 1)}, RHS = ${VisualMathEngine.formatNumberBn(rhsVal, 1)} (পার্থক্য: ${VisualMathEngine.formatNumberBn(abs(diff), 1)})"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isBalanced) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Stepper Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                            enabled = activeStepIndex > 0,
                            modifier = Modifier.testTag("algebra_prev_step")
                        ) {
                            Text("পূর্ববর্তী ধাপ")
                        }

                        Text(
                            text = "ধাপ ${BengaliNumberFormatter.toBengaliDigits((safeStepIdx + 1).toString())} / ${BengaliNumberFormatter.toBengaliDigits(algebraResult.steps.size.toString())}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = { if (activeStepIndex < algebraResult.steps.size - 1) activeStepIndex++ },
                            enabled = activeStepIndex < algebraResult.steps.size - 1,
                            modifier = Modifier.testTag("algebra_next_step")
                        ) {
                            Text("পরবর্তী ধাপ")
                        }
                    }
                }
            }
        }

        // 3. Transformation Steps & Verification
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "সমীকরণ সমাধানের ধাপসমূহ (Balance Operations):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    algebraResult.steps.forEachIndexed { idx, step ->
                        val isCurrent = idx == safeStepIdx
                        Surface(
                            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeStepIndex = idx }
                                .testTag("algebra_step_card_$idx")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ধাপ ${BengaliNumberFormatter.toBengaliDigits(step.stepNumber.toString())}: ${step.balanceActionBn}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${step.leftExprBn} = ${step.rightExprBn}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.explanationBn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = algebraResult.verificationBn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 4. Interactive Linear Graph (y = mx + c) Explorer Card
        item {
            LinearGraphExplorerCard(
                viewModel = viewModel,
                labelColorArgb = onSurfaceArgb
            )
        }
    }
}

@Composable
private fun LinearGraphExplorerCard(
    viewModel: MathViewModel,
    labelColorArgb: Int
) {
    // BUG-55 FIX: Use rememberManagedUiState instead of volatile remember
    var slopeM by rememberManagedUiState(viewModel, "visual_explorer:tab_1:slopeM", 2.0)
    var interceptC by rememberManagedUiState(viewModel, "visual_explorer:tab_1:interceptC", -2.0)
    var sampleX by rememberManagedUiState(viewModel, "visual_explorer:tab_1:sampleX", 2.0)

    val eval = remember(slopeM, interceptC, sampleX) {
        VisualMathEngine.evaluateLinearGraph(slopeM, interceptC, sampleX)
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("linear_graph_explorer_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "সরলরেখার গ্রাফ ও ঢাল এক্সপ্লোরার (y = mx + c)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = eval.equationBn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )

            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val axisColor = MaterialTheme.colorScheme.outline

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val unitPx = min(size.width, size.height) / 14f

                    drawLine(axisColor, Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2f)
                    drawLine(axisColor, Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2f)

                    val x1 = -6f
                    val y1 = (slopeM * x1 + interceptC).toFloat()
                    val x2 = 6f
                    val y2 = (slopeM * x2 + interceptC).toFloat()

                    drawLine(
                        color = primaryColor,
                        start = Offset(cx + x1 * unitPx, cy - y1 * unitPx),
                        end = Offset(cx + x2 * unitPx, cy - y2 * unitPx),
                        strokeWidth = 4f
                    )

                    val px = cx + sampleX.toFloat() * unitPx
                    val py = cy - eval.sampleY.toFloat() * unitPx
                    drawCircle(
                        color = tertiaryColor,
                        radius = 7f,
                        center = Offset(px.coerceIn(0f, size.width), py.coerceIn(0f, size.height))
                    )
                    drawCanvasText(
                        "(${VisualMathEngine.formatNumberBn(sampleX, 0)}, ${VisualMathEngine.formatNumberBn(eval.sampleY, 1)})",
                        (px + 38f).coerceIn(40f, size.width - 40f),
                        (py - 10f).coerceIn(20f, size.height - 10f),
                        labelColorArgb,
                        22f
                    )
                }
            }

            DimensionSliderRow(
                label = "ঢাল (Slope m)",
                value = slopeM,
                valueRange = -4f..4f,
                onValueChange = { slopeM = (Math.round(it * 2.0) / 2.0) },
                testTag = "linear_graph_slider_m"
            )
            DimensionSliderRow(
                label = "y-ছেদক (Intercept c)",
                value = interceptC,
                valueRange = -5f..5f,
                onValueChange = { interceptC = it.toInt().toDouble() },
                testTag = "linear_graph_slider_c"
            )

            eval.stepsBn.forEachIndexed { idx, step ->
                StepItemRow(stepNumber = idx + 1, stepText = step)
            }
        }
    }
}

private fun DrawScope.drawBalanceScale(
    xCount: Int,
    leftConst: Int,
    rightConst: Int,
    tiltAngleDeg: Float,
    beamColor: Color,
    xBoxColor: Color,
    constColor: Color,
    labelColorArgb: Int
) {
    val cx = size.width / 2f
    val baseY = size.height * 0.88f
    val pivotY = size.height * 0.56f
    val beamHalfWidth = size.width * 0.38f

    // Fulcrum triangle
    val fulcrumPath = Path().apply {
        moveTo(cx, pivotY)
        lineTo(cx - 26f, baseY)
        lineTo(cx + 26f, baseY)
        close()
    }
    drawPath(path = fulcrumPath, color = beamColor.copy(alpha = 0.75f), style = Fill)

    // Rotate beam and pans around fulcrum pivot
    rotate(degrees = tiltAngleDeg, pivot = Offset(cx, pivotY)) {
        drawLine(
            color = beamColor,
            start = Offset(cx - beamHalfWidth, pivotY),
            end = Offset(cx + beamHalfWidth, pivotY),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )

        val leftPanCenter = Offset(cx - beamHalfWidth * 0.72f, pivotY - 12f)
        val rightPanCenter = Offset(cx + beamHalfWidth * 0.72f, pivotY - 12f)
        val panWidth = size.width * 0.34f

        drawLine(
            color = beamColor,
            start = Offset(leftPanCenter.x - panWidth / 2f, leftPanCenter.y),
            end = Offset(leftPanCenter.x + panWidth / 2f, leftPanCenter.y),
            strokeWidth = 5f
        )
        drawLine(
            color = beamColor,
            start = Offset(rightPanCenter.x - panWidth / 2f, rightPanCenter.y),
            end = Offset(rightPanCenter.x + panWidth / 2f, rightPanCenter.y),
            strokeWidth = 5f
        )

        val boxW = 34f
        val boxH = 34f
        val totalLeftItems = xCount + if (leftConst != 0) 1 else 0
        val startLeftX = leftPanCenter.x - (totalLeftItems * (boxW + 6f)) / 2f

        for (i in 0 until xCount.coerceAtMost(5)) {
            val bx = startLeftX + i * (boxW + 6f)
            val by = leftPanCenter.y - boxH - 4f
            drawRect(color = xBoxColor, topLeft = Offset(bx, by), size = Size(boxW, boxH))
            drawCanvasText("x", bx + boxW / 2f, by + boxH * 0.68f, android.graphics.Color.WHITE, 24f)
        }

        if (leftConst != 0) {
            val bx = startLeftX + xCount.coerceAtMost(5) * (boxW + 6f)
            val by = leftPanCenter.y - boxH - 4f
            drawRect(color = constColor, topLeft = Offset(bx, by), size = Size(boxW + 12f, boxH))
            val sign = if (leftConst > 0) "+" else ""
            drawCanvasText(
                "$sign${BengaliNumberFormatter.toBengaliDigits(leftConst.toString())}",
                bx + (boxW + 12f) / 2f,
                by + boxH * 0.68f,
                android.graphics.Color.WHITE,
                22f
            )
        }

        val rBoxW = 64f
        val rBoxH = 40f
        val rBx = rightPanCenter.x - rBoxW / 2f
        val rBy = rightPanCenter.y - rBoxH - 4f
        drawRect(color = constColor, topLeft = Offset(rBx, rBy), size = Size(rBoxW, rBoxH))
        drawCanvasText(
            BengaliNumberFormatter.toBengaliDigits(rightConst.toString()),
            rBx + rBoxW / 2f,
            rBy + rBoxH * 0.68f,
            android.graphics.Color.WHITE,
            26f
        )
    }

    drawCanvasText("বাম পক্ষ (LHS)", cx - beamHalfWidth * 0.72f, baseY - 4f, labelColorArgb, 22f)
    drawCanvasText("ডান পক্ষ (RHS)", cx + beamHalfWidth * 0.72f, baseY - 4f, labelColorArgb, 22f)
}

// =============================================================================
// C. NUMBER LINE & FRACTION VISUALIZER TAB
// =============================================================================

@Composable
fun NumberLineTab(viewModel: MathViewModel) {
    var numerator by rememberManagedUiState(viewModel, "visual_explorer:tab_2:numerator", 3)
    var denominator by rememberManagedUiState(viewModel, "visual_explorer:tab_2:denominator", 4)
    var numberLineMarker by rememberManagedUiState(viewModel, "visual_explorer:tab_2:numberLineMarker", 0.75f)
    val listState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_2")

    val fractionModel = remember(numerator, denominator) {
        VisualMathEngine.analyzeFraction(numerator, denominator)
    }

    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("number_line_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Fraction Pie & Bar Comparison Card (BUG-54 FIX: Multi-Whole Improper Fractions!)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ভগ্নাংশ ও দশমিকের ভিজ্যুয়াল তুলনা",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val numBn = BengaliNumberFormatter.toBengaliDigits(numerator.toString())
                    val denBn = BengaliNumberFormatter.toBengaliDigits(denominator.toString())
                    val decBn = VisualMathEngine.formatNumberBn(fractionModel.decimalValue, 3)
                    val pctBn = VisualMathEngine.formatNumberBn(fractionModel.decimalValue * 100.0, 1)

                    Text(
                        text = "$numBn / $denBn  =  $decBn  ($pctBn%)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.testTag("fraction_summary_headline")
                    )

                    if (numerator > denominator) {
                        val wholePart = numerator / denominator
                        val remPart = numerator % denominator
                        val mixedText = if (remPart == 0) {
                            "পূর্ণ সংখ্যা: ${BengaliNumberFormatter.toBengaliDigits(wholePart.toString())}টি সম্পূর্ণ অংশ"
                        } else {
                            "মিশ্র ভগ্নাংশ: ${BengaliNumberFormatter.toBengaliDigits(wholePart.toString())} পূর্ণ ${BengaliNumberFormatter.toBengaliDigits(remPart.toString())}/${BengaliNumberFormatter.toBengaliDigits(denominator.toString())}"
                        }
                        Text(
                            text = mixedText,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.tertiary
                    val emptySliceColor = MaterialTheme.colorScheme.surfaceVariant

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .testTag("fraction_visual_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                            drawFractionPieAndBar(
                                num = numerator,
                                den = denominator,
                                filledColor = primaryColor,
                                accentColor = secondaryColor,
                                emptyColor = emptySliceColor,
                                labelColorArgb = onSurfaceArgb
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Numerator & Denominator Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "লব (Numerator): ${BengaliNumberFormatter.toBengaliDigits(numerator.toString())}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = numerator.toFloat(),
                                onValueChange = {
                                    numerator = it.toInt().coerceIn(0, 12)
                                    numberLineMarker = (numerator.toFloat() / denominator.coerceAtLeast(1).toFloat()).coerceIn(-5f, 5f)
                                },
                                valueRange = 0f..12f,
                                steps = 11,
                                modifier = Modifier.testTag("slider_numerator")
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "হর (Denominator): ${BengaliNumberFormatter.toBengaliDigits(denominator.toString())}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = denominator.toFloat(),
                                onValueChange = {
                                    denominator = it.toInt().coerceIn(1, 12)
                                    numberLineMarker = (numerator.toFloat() / denominator.toFloat()).coerceIn(-5f, 5f)
                                },
                                valueRange = 1f..12f,
                                steps = 10,
                                modifier = Modifier.testTag("slider_denominator")
                            )
                        }
                    }
                }
            }
        }

        // 2. Interactive Number Line Explorer (-5 to +5)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "সংখ্যা রেখা (Interactive Number Line)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "মান: ${VisualMathEngine.formatNumberBn(numberLineMarker.toDouble(), 2)}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    val axisColor = MaterialTheme.colorScheme.onSurface
                    val markerColor = MaterialTheme.colorScheme.primary
                    val zeroColor = MaterialTheme.colorScheme.tertiary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .testTag("number_line_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
                            drawInteractiveNumberLine(
                                markerValue = numberLineMarker,
                                axisColor = axisColor,
                                markerColor = markerColor,
                                zeroColor = zeroColor,
                                labelColorArgb = onSurfaceArgb
                            )
                        }
                    }

                    Slider(
                        value = numberLineMarker,
                        onValueChange = { numberLineMarker = it },
                        valueRange = -5f..5f,
                        modifier = Modifier.fillMaxWidth().testTag("slider_number_line")
                    )

                    Text(
                        text = when {
                            abs(numberLineMarker) < 0.02f -> "শূন্য (০): এটি ধনাত্মক ও ঋণাত্মক সংখ্যার মধ্যবিন্দু (Origin)।"
                            numberLineMarker > 0f -> "ধনাত্মক সংখ্যা (+${VisualMathEngine.formatNumberBn(numberLineMarker.toDouble(), 2)}): শূন্যের ডানদিকে অবস্থিত। পরমমান = ${VisualMathEngine.formatNumberBn(abs(numberLineMarker).toDouble(), 2)}।"
                            else -> "ঋণাত্মক সংখ্যা (${VisualMathEngine.formatNumberBn(numberLineMarker.toDouble(), 2)}): শূন্যের বামদিকে অবস্থিত। পরমমান (Distance from 0) = ${VisualMathEngine.formatNumberBn(abs(numberLineMarker).toDouble(), 2)}।"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Detailed Fraction Analysis Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ভগ্নাংশের বৈশিষ্ট্য: ${fractionModel.fractionTypeBn}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "লঘিষ্ঠ রূপ (Simplified): ${BengaliNumberFormatter.toBengaliDigits(fractionModel.simplifiedNumerator.toString())} / ${BengaliNumberFormatter.toBengaliDigits(fractionModel.simplifiedDenominator.toString())}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = fractionModel.comparisonExplanationBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // 4. Ratio, Percentage & Visual Sequence Pattern Card
        item {
            RatioPercentageAndPatternCard(viewModel = viewModel)
        }
    }
}

@Composable
private fun RatioPercentageAndPatternCard(viewModel: MathViewModel) {
    // BUG-55 FIX: Use rememberManagedUiState instead of volatile remember
    var patternType by rememberManagedUiState(viewModel, "visual_explorer:tab_2:patternType", "square")
    val ratioAnalysis = remember {
        VisualMathEngine.analyzeRatioAndPercentage(3, 2, 60)
    }
    val patternModel = remember(patternType) {
        VisualMathEngine.generateVisualPattern(patternType, termCount = 5)
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("ratio_and_pattern_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "অনুপাত, শতকরা ও সংখ্যা প্যাটার্ন ভিজ্যুয়ালাইজার",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = ratioAnalysis.explanationBn.joinToString(" "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("square" to "বর্গ সংখ্যা", "triangular" to "ত্রিভুজাকার", "fibonacci" to "ফিবোনাচ্চি").forEach { (key, label) ->
                    FilterChip(
                        selected = patternType == key,
                        onClick = { patternType = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Text(
                text = "${patternModel.titleBn} (${patternModel.formulaBn}): ${
                    patternModel.terms.joinToString(", ") { BengaliNumberFormatter.toBengaliDigits(it.toString()) }
                }",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = patternModel.explanationBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun DrawScope.drawFractionPieAndBar(
    num: Int,
    den: Int,
    filledColor: Color,
    accentColor: Color,
    emptyColor: Color,
    labelColorArgb: Int
) {
    val safeDen = den.coerceAtLeast(1)
    // BUG-54 FIX: Support improper fractions (num > den) by rendering multiple wholes!
    val totalWholes = if (num <= safeDen) 1 else ceil(num.toDouble() / safeDen.toDouble()).toInt().coerceAtMost(4)

    val leftAreaWidth = size.width * 0.44f
    val pieCenterY = size.height / 2f

    if (totalWholes == 1) {
        val pieCenter = Offset(leftAreaWidth * 0.52f, pieCenterY)
        val pieRadius = min(leftAreaWidth, size.height) * 0.38f
        drawSingleFractionPie(pieCenter, pieRadius, num.coerceIn(0, safeDen), safeDen, filledColor, emptyColor, labelColorArgb)
    } else {
        val cols = 2
        val rows = if (totalWholes <= 2) 1 else 2
        val cellW = leftAreaWidth / cols
        val cellH = (size.height - 24f) / rows
        val pieRadius = (min(cellW, cellH) * 0.38f).coerceAtLeast(16f)

        var remainingSlices = num
        for (w in 0 until totalWholes) {
            val r = w / cols
            val c = w % cols
            val center = Offset(c * cellW + cellW / 2f, r * cellH + cellH / 2f + 6f)
            val filledInThisWhole = remainingSlices.coerceIn(0, safeDen)
            remainingSlices = (remainingSlices - safeDen).coerceAtLeast(0)
            drawSingleFractionPie(
                center = center,
                radius = pieRadius,
                filledSlices = filledInThisWhole,
                den = safeDen,
                filledColor = if (filledInThisWhole == safeDen) filledColor else accentColor,
                emptyColor = emptyColor,
                labelColorArgb = labelColorArgb,
                showCaption = false
            )
        }
        drawCanvasText(
            text = "মোট ${BengaliNumberFormatter.toBengaliDigits(totalWholes.toString())}টি একক বৃত্ত",
            x = leftAreaWidth * 0.5f,
            y = size.height - 2f,
            colorArgb = labelColorArgb,
            textSizePx = 20f
        )
    }

    val barLeft = size.width * 0.48f
    val barWidth = size.width * 0.48f
    val totalBarHeight = if (totalWholes == 1) 42f else (110f / totalWholes).coerceIn(22f, 36f)
    val gapY = 8f
    val startBarY = (size.height - (totalWholes * totalBarHeight + (totalWholes - 1) * gapY)) / 2f
    val segWidth = barWidth / safeDen

    var remainingBarSlices = num
    for (w in 0 until totalWholes) {
        val barTop = startBarY + w * (totalBarHeight + gapY)
        val filledInThisBar = remainingBarSlices.coerceIn(0, safeDen)
        remainingBarSlices = (remainingBarSlices - safeDen).coerceAtLeast(0)

        for (i in 0 until safeDen) {
            val sx = barLeft + i * segWidth
            drawRect(
                color = if (i < filledInThisBar) {
                    if (w == 0) accentColor.copy(alpha = 0.85f) else filledColor.copy(alpha = 0.80f)
                } else {
                    emptyColor
                },
                topLeft = Offset(sx, barTop),
                size = Size(segWidth, totalBarHeight)
            )
            drawRect(
                color = android.graphics.Color.GRAY.let { Color(it) },
                topLeft = Offset(sx, barTop),
                size = Size(segWidth, totalBarHeight),
                style = Stroke(width = 2f)
            )
        }
    }

    drawCanvasText(
        text = "বার মডেল (${BengaliNumberFormatter.toBengaliDigits(num.toString())}/${BengaliNumberFormatter.toBengaliDigits(safeDen.toString())})",
        x = barLeft + barWidth / 2f,
        y = (startBarY + totalWholes * (totalBarHeight + gapY) + 20f).coerceAtMost(size.height - 2f),
        colorArgb = labelColorArgb,
        textSizePx = 22f
    )
}

private fun DrawScope.drawSingleFractionPie(
    center: Offset,
    radius: Float,
    filledSlices: Int,
    den: Int,
    filledColor: Color,
    emptyColor: Color,
    labelColorArgb: Int,
    showCaption: Boolean = true
) {
    val sweepPerSlice = 360f / den
    for (i in 0 until den) {
        val startAngle = -90f + i * sweepPerSlice
        val isFilled = i < filledSlices
        drawArc(
            color = if (isFilled) filledColor.copy(alpha = 0.85f) else emptyColor,
            startAngle = startAngle,
            sweepAngle = sweepPerSlice,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f)
        )
        drawArc(
            color = android.graphics.Color.WHITE.let { Color(it) },
            startAngle = startAngle,
            sweepAngle = sweepPerSlice,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = 2f)
        )
    }
    drawCircle(
        color = filledColor,
        radius = radius,
        center = center,
        style = Stroke(width = 3f)
    )
    if (showCaption) {
        drawCanvasText("বৃত্তাকার মডেল", center.x, center.y + radius + 24f, labelColorArgb, 22f)
    }
}

private fun DrawScope.drawInteractiveNumberLine(
    markerValue: Float,
    axisColor: Color,
    markerColor: Color,
    zeroColor: Color,
    labelColorArgb: Int
) {
    val cy = size.height * 0.55f
    val startX = 0f
    val endX = size.width
    val totalWidth = endX - startX

    drawLine(
        color = axisColor,
        start = Offset(startX, cy),
        end = Offset(endX, cy),
        strokeWidth = 4f
    )

    for (tick in -5..5) {
        val ratio = (tick + 5f) / 10f
        val tx = startX + ratio * totalWidth
        val tickHeight = if (tick == 0) 16f else 10f
        drawLine(
            color = if (tick == 0) zeroColor else axisColor,
            start = Offset(tx, cy - tickHeight),
            end = Offset(tx, cy + tickHeight),
            strokeWidth = if (tick == 0) 4f else 2f
        )
        drawCanvasText(
            text = BengaliNumberFormatter.toBengaliDigits(tick.toString()),
            x = tx,
            y = cy + 34f,
            colorArgb = labelColorArgb,
            textSizePx = 22f
        )
    }

    val clamped = markerValue.coerceIn(-5f, 5f)
    val markerRatio = (clamped + 5f) / 10f
    val mx = startX + markerRatio * totalWidth
    val zeroX = startX + 0.5f * totalWidth

    drawLine(
        color = markerColor.copy(alpha = 0.5f),
        start = Offset(zeroX, cy),
        end = Offset(mx, cy),
        strokeWidth = 10f,
        cap = StrokeCap.Round
    )

    drawCircle(
        color = markerColor,
        radius = 11f,
        center = Offset(mx, cy)
    )
}

// =============================================================================
// D. FORMULA VISUALIZER TAB (VISUAL PROOFS & STEP-BY-STEP CALCULATIONS)
// =============================================================================

@Composable
fun VisualFormulaTab(viewModel: MathViewModel) {
    val formulas = VisualMathEngine.formulaVisualizerList
    var selectedFormulaId by rememberManagedUiState(viewModel, "visual_explorer:tab_3:selectedFormulaId", formulas.first().id)
    val selectedFormula = formulas.find { it.id == selectedFormulaId } ?: formulas.first()

    var inputA by rememberManagedUiState(viewModel, "visual_explorer:tab_3:inputA:${selectedFormula.id}", selectedFormula.param1Default)
    var inputB by rememberManagedUiState(viewModel, "visual_explorer:tab_3:inputB:${selectedFormula.id}", selectedFormula.param2Default)
    var inputC by rememberManagedUiState(viewModel, "visual_explorer:tab_3:inputC:${selectedFormula.id}", selectedFormula.param3Default)
    val listState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_3")
    val formulaChipRowState = rememberManagedLazyListState(viewModel, "visual_explorer:tab_3:chips")

    val calcResult = remember(selectedFormula, inputA, inputB, inputC) {
        selectedFormula.calculate(inputA, inputB, inputC)
    }

    val onSurfaceArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("visual_formula_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Formula Selector Chips
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ভিজ্যুয়াল সূত্র নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        state = formulaChipRowState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(formulas, key = { it.id }) { item ->
                            FilterChip(
                                selected = item.id == selectedFormula.id,
                                onClick = { selectedFormulaId = item.id },
                                label = { Text(item.titleBn, fontWeight = FontWeight.Medium) },
                                modifier = Modifier.testTag("vis_formula_${item.id}")
                            )
                        }
                    }
                }
            }
        }

        // 2. Visual Proof / Geometric Partition Diagram
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedFormula.titleBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = selectedFormula.formulaExpression,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.tertiary
                    val accentColor = MaterialTheme.colorScheme.secondary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(225.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .testTag("formula_visual_canvas")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            when (selectedFormula.id) {
                                "vis_alg_sq_plus" -> drawSquareOfSumVisual(inputA, inputB, primaryColor, secondaryColor, accentColor)
                                "vis_pythagoras" -> drawPythagorasVisual(inputA, inputB, primaryColor, secondaryColor, onSurfaceArgb)
                                "vis_simple_interest" -> drawSimpleInterestVisual(inputA, calcResult.outputValue, primaryColor, secondaryColor, onSurfaceArgb)
                                // BUG-53 FIX: Render a true 3D projected Cube via Math3DEngine instead of drawCircle!
                                "vis_cube_vol" -> drawCubeVolumeVisual3D(inputA, primaryColor, accentColor, secondaryColor, onSurfaceArgb, primaryArgb)
                                "vis_circle_area" -> drawCircleAreaVisual(inputA, primaryColor, secondaryColor, onSurfaceArgb)
                                else -> drawGenericBarVisual(inputA, inputB, calcResult.outputValue, primaryColor, secondaryColor, onSurfaceArgb)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = calcResult.diagramNotesBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Interactive Parameter Sliders
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val rangeA = selectedFormula.param1Min.toFloat()..selectedFormula.param1Max.toFloat()
                    DimensionSliderRow(
                        label = selectedFormula.param1NameBn,
                        value = inputA,
                        valueRange = rangeA,
                        onValueChange = {
                            inputA = if (selectedFormula.id == "vis_simple_interest") {
                                (it.toInt() / 100 * 100).toDouble()
                            } else {
                                it.toInt().toDouble()
                            }
                        },
                        testTag = "vis_formula_slider_a"
                    )

                    selectedFormula.param2NameBn?.let { labelB ->
                        val rangeB = selectedFormula.param2Min.toFloat()..selectedFormula.param2Max.toFloat()
                        DimensionSliderRow(
                            label = labelB,
                            value = inputB,
                            valueRange = rangeB,
                            onValueChange = { inputB = it.toInt().toDouble() },
                            testTag = "vis_formula_slider_b"
                        )
                    }

                    selectedFormula.param3NameBn?.let { labelC ->
                        val rangeC = selectedFormula.param3Min.toFloat()..selectedFormula.param3Max.toFloat()
                        DimensionSliderRow(
                            label = labelC,
                            value = inputC,
                            valueRange = rangeC,
                            onValueChange = { inputC = it.toInt().toDouble() },
                            testTag = "vis_formula_slider_c"
                        )
                    }
                }
            }
        }

        // 4. Step-by-Step Result Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("vis_formula_result_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricHighlightBox(
                        title = calcResult.outputLabelBn,
                        value = VisualMathEngine.formatNumberBn(calcResult.outputValue, 2),
                        unit = "",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "ভিজ্যুয়াল গাণিতিক বিশ্লেষণ:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    calcResult.calculationStepsBn.forEachIndexed { idx, step ->
                        StepItemRow(stepNumber = idx + 1, stepText = step)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSquareOfSumVisual(
    a: Double,
    b: Double,
    colorA: Color,
    colorB: Color,
    colorAB: Color
) {
    val total = (a + b).coerceAtLeast(1.0).toFloat()
    val boxSize = min(size.width, size.height) * 0.82f
    val startX = (size.width - boxSize) / 2f
    val startY = (size.height - boxSize) / 2f

    val aPx = (a.toFloat() / total) * boxSize
    val bPx = (b.toFloat() / total) * boxSize

    drawRect(color = colorA.copy(alpha = 0.75f), topLeft = Offset(startX, startY), size = Size(aPx, aPx))
    drawCanvasText("a²", startX + aPx / 2f, startY + aPx / 2f + 8f, android.graphics.Color.WHITE, 26f)

    drawRect(color = colorAB.copy(alpha = 0.65f), topLeft = Offset(startX + aPx, startY), size = Size(bPx, aPx))
    drawCanvasText("ab", startX + aPx + bPx / 2f, startY + aPx / 2f + 8f, android.graphics.Color.WHITE, 22f)

    drawRect(color = colorAB.copy(alpha = 0.65f), topLeft = Offset(startX, startY + aPx), size = Size(aPx, bPx))
    drawCanvasText("ab", startX + aPx / 2f, startY + aPx + bPx / 2f + 8f, android.graphics.Color.WHITE, 22f)

    drawRect(color = colorB.copy(alpha = 0.80f), topLeft = Offset(startX + aPx, startY + aPx), size = Size(bPx, bPx))
    drawCanvasText("b²", startX + aPx + bPx / 2f, startY + aPx + bPx / 2f + 8f, android.graphics.Color.WHITE, 22f)

    drawRect(color = android.graphics.Color.WHITE.let { Color(it) }, topLeft = Offset(startX, startY), size = Size(boxSize, boxSize), style = Stroke(width = 3f))
}

private fun DrawScope.drawPythagorasVisual(
    a: Double,
    b: Double,
    primaryColor: Color,
    secondaryColor: Color,
    labelColorArgb: Int
) {
    val maxIn = max(a, b).coerceAtLeast(1.0).toFloat()
    val scale = (min(size.width, size.height) * 0.65f) / maxIn
    val hPx = (a.toFloat() * scale).coerceAtLeast(40f)
    val bPx = (b.toFloat() * scale).coerceAtLeast(40f)

    val originX = (size.width - bPx) / 2f
    val originY = (size.height + hPx) / 2f

    val pTop = Offset(originX, originY - hPx)
    val pCorner = Offset(originX, originY)
    val pRight = Offset(originX + bPx, originY)

    val path = Path().apply {
        moveTo(pTop.x, pTop.y)
        lineTo(pCorner.x, pCorner.y)
        lineTo(pRight.x, pRight.y)
        close()
    }
    drawPath(path = path, color = primaryColor.copy(alpha = 0.22f), style = Fill)
    drawPath(path = path, color = primaryColor, style = Stroke(width = 4f))

    drawRect(
        color = secondaryColor,
        topLeft = Offset(pCorner.x, pCorner.y - 14f),
        size = Size(14f, 14f),
        style = Stroke(width = 2.5f)
    )

    drawCanvasText("a=${VisualMathEngine.formatNumberBn(a, 0)}", pCorner.x - 28f, pCorner.y - hPx / 2f, labelColorArgb, 22f)
    drawCanvasText("b=${VisualMathEngine.formatNumberBn(b, 0)}", pCorner.x + bPx / 2f, pCorner.y + 24f, labelColorArgb, 22f)
    drawCanvasText("অতিভুজ c", (pTop.x + pRight.x) / 2f + 28f, (pTop.y + pRight.y) / 2f - 8f, secondaryColor.toArgb(), 22f)
}

private fun DrawScope.drawSimpleInterestVisual(
    principal: Double,
    interest: Double,
    primaryColor: Color,
    secondaryColor: Color,
    labelColorArgb: Int
) {
    val total = (principal + interest).coerceAtLeast(1.0).toFloat()
    val maxBarW = size.width * 0.82f
    val pW = (principal.toFloat() / total) * maxBarW
    val iW = (interest.toFloat() / total) * maxBarW
    val barH = 48f
    val startX = (size.width - maxBarW) / 2f
    val startY = size.height / 2f - barH / 2f

    drawRect(color = primaryColor, topLeft = Offset(startX, startY), size = Size(pW, barH))
    drawRect(color = secondaryColor, topLeft = Offset(startX + pW, startY), size = Size(iW.coerceAtLeast(6f), barH))

    drawCanvasText("আসল (P)", startX + pW / 2f, startY - 12f, labelColorArgb, 22f)
    drawCanvasText("মুনাফা (I)", startX + pW + iW / 2f, startY + barH + 26f, labelColorArgb, 22f)
}

// BUG-53 FIX: True 3D Projected Cube for "vis_cube_vol" Formula Visualization
private fun DrawScope.drawCubeVolumeVisual3D(
    sideA: Double,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    labelColorArgb: Int,
    accentColorArgb: Int
) {
    val (faces, lines) = Math3DEngine.buildSolidMesh(
        shape = Solid3DShapeType.CUBE,
        p1 = sideA.coerceIn(1.5, 10.0),
        unfoldProgress = 0.0f,
        volumeFillFraction = 0.0f
    )
    val scene = Math3DEngine.projectScene(
        faces = faces,
        internalLines = lines,
        camera = OrbitCameraState(pitchDeg = 25f, yawDeg = -38f, zoom = 0.95f),
        canvasWidth = size.width,
        canvasHeight = size.height
    )
    drawProjected3DScene(
        scene = scene,
        highlightMode = SolidAnatomyHighlight.ALL,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        tertiaryColor = tertiaryColor,
        labelColorArgb = labelColorArgb,
        accentColorArgb = accentColorArgb
    )
    drawCanvasText(
        text = "ঘনক: ধার a = ${VisualMathEngine.formatNumberBn(sideA, 1)}, আয়তন V = a³ = ${VisualMathEngine.formatNumberBn(sideA * sideA * sideA, 1)}",
        x = size.width / 2f,
        y = size.height - 4f,
        colorArgb = labelColorArgb,
        textSizePx = 22f
    )
}

private fun DrawScope.drawCircleAreaVisual(
    radius: Double,
    primaryColor: Color,
    secondaryColor: Color,
    labelColorArgb: Int
) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 8f
    val rPx = ((radius.toFloat() / 12f) * (min(size.width, size.height) * 0.42f)).coerceIn(36f, min(size.width, size.height) * 0.42f)

    val sectors = 12
    val sweep = 360f / sectors
    for (i in 0 until sectors) {
        drawArc(
            color = if (i % 2 == 0) primaryColor.copy(alpha = 0.65f) else secondaryColor.copy(alpha = 0.55f),
            startAngle = i * sweep,
            sweepAngle = sweep,
            useCenter = true,
            topLeft = Offset(cx - rPx, cy - rPx),
            size = Size(rPx * 2f, rPx * 2f)
        )
    }
    drawCircle(color = primaryColor, radius = rPx, center = Offset(cx, cy), style = Stroke(width = 3.5f))
    drawLine(color = Color(0xFFDC2626), start = Offset(cx, cy), end = Offset(cx + rPx, cy), strokeWidth = 3.5f)
    drawCanvasText("r = ${VisualMathEngine.formatNumberBn(radius, 1)}", cx + rPx / 2f, cy - 10f, labelColorArgb, 22f)
    drawCanvasText("A = πr² ≈ ${VisualMathEngine.formatNumberBn(PI * radius * radius, 2)}", cx, size.height - 4f, labelColorArgb, 22f)
}

private fun DrawScope.drawGenericBarVisual(
    a: Double,
    b: Double,
    output: Double,
    primaryColor: Color,
    secondaryColor: Color,
    labelColorArgb: Int
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = min(size.width, size.height) * 0.35f
    drawCircle(color = primaryColor.copy(alpha = 0.2f), radius = radius, center = Offset(cx, cy))
    drawCircle(color = secondaryColor, radius = radius, center = Offset(cx, cy), style = Stroke(width = 4f))
    drawCanvasText(VisualMathEngine.formatNumberBn(output, 2), cx, cy + 8f, labelColorArgb, 30f)
}

// =============================================================================
// SHARED UI HELPER COMPONENTS
// =============================================================================

@Composable
private fun DimensionSliderRow(
    label: String,
    value: Double,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = VisualMathEngine.formatNumberBn(value, 1),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = value.toFloat().coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth().testTag(testTag)
        )
    }
}

@Composable
private fun MetricHighlightBox(
    title: String,
    value: String,
    unit: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun StepItemRow(
    stepNumber: Int,
    stepText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = BengaliNumberFormatter.toBengaliDigits(stepNumber.toString()),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Text(
            text = stepText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}
