package com.example.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.example.math.AlgebraFormulaProvider
import com.example.math.BcsExamProfile
import com.example.math.BcsSyllabusVersion
import com.example.math.BookCurriculumRepository
import com.example.math.BookId
import com.example.math.BookInteractiveLabType
import org.json.JSONArray
import org.json.JSONObject

/**
 * Paired navigation back-stack frame holding both the destination [Screen] and its own
 * per-frame [NavigationContextSnapshot] (typed route arguments + identity keys).
 */
data class NavigationFrame(
    val screen: Screen,
    val snapshot: NavigationContextSnapshot
)

/**
 * Decoded navigation stack bundle produced by [ScreenCodec].
 */
data class DecodedNavigationState(
    val frames: List<NavigationFrame>,
    val nextInstanceCounter: Int
)

/**
 * Canonical versioned JSON codec (`schemaVersion = 1`) for persisting and restoring the
 * navigation back-stack across Android OS process death via [androidx.lifecycle.SavedStateHandle].
 *
 * - Stores an explicit structured `"args"` JSON object per frame for all 31 [Screen] routes
 *   (11 regular parameterized routes + 1 transient [Screen.BookPractice] route carrying typed args).
 * - Never reconstructs typed route arguments by splitting [NavigationContextSnapshot.routeKey].
 * - Preserves special characters and Bengali Unicode inside arguments via `org.json`.
 * - Validates and falls back deterministically on corrupt payloads or missing/invalid args.
 */
object ScreenCodec {
    const val SCHEMA_VERSION = 1

    private val allScreens: List<Screen> = listOf(
        Screen.Home,
        Screen.Tables,
        Screen.TableDetail,
        Screen.Primes,
        Screen.SquaresCubes,
        Screen.Formulas,
        Screen.FormulaDetail,
        Screen.FormulaLibrary,
        Screen.PracticeLab,
        Screen.PracticeLabPlay,
        Screen.MentalMath,
        Screen.MentalMathPlay,
        Screen.NumberSystem,
        Screen.NumberSystemPractice,
        Screen.Quizzes,
        Screen.QuizPlay,
        Screen.Bookmarks,
        Screen.PracticeTutor,
        Screen.Dashboard,
        Screen.Customize,
        Screen.ProblemSolver,
        Screen.VisualExplorer,
        Screen.BcsPreparation,
        Screen.BookHub,
        Screen.BookDetail,
        Screen.BookChapterOverview,
        Screen.BookLesson,
        Screen.BookPractice,
        Screen.BookPdfReader,
        Screen.BookInteractiveLab,
        Screen.BookRevision
    )

    private val screenByRoute: Map<String, Screen> = allScreens.associateBy { it.route }

    internal var encodeStackObserverForTesting: (() -> Unit)? = null

    fun encodeStack(
        frames: List<NavigationFrame>,
        nextInstanceCounter: Int = frames.size
    ): String {
        encodeStackObserverForTesting?.invoke()
        val safeFrames = if (frames.isEmpty()) {
            listOf(
                NavigationFrame(
                    screen = Screen.Home,
                    snapshot = NavigationContextSnapshot(
                        screen = Screen.Home,
                        routeKey = Screen.Home.route,
                        screenInstanceId = "${Screen.Home.route}#0"
                    )
                )
            )
        } else {
            frames
        }

        val jsonFrames = JSONArray()
        safeFrames.forEachIndexed { index, frame ->
            val snap = frame.snapshot
            val routeKey = snap.routeKey.ifBlank { computeRouteKey(frame.screen, snap) }
            val instanceId = snap.screenInstanceId.ifBlank { "$routeKey#$index" }
            val argsObj = encodeArgsForScreen(frame.screen, snap)
            val frameObj = JSONObject().apply {
                put("route", frame.screen.route)
                put("routeKey", routeKey)
                put("screenInstanceId", instanceId)
                put("args", argsObj)
            }
            jsonFrames.put(frameObj)
        }

        return JSONObject().apply {
            put("schemaVersion", SCHEMA_VERSION)
            put("nextInstanceCounter", nextInstanceCounter.coerceAtLeast(safeFrames.size))
            put("frames", jsonFrames)
        }.toString()
    }

    fun decodeStack(
        rawJson: String?,
        sanitizeTransientOnRestore: Boolean = true
    ): DecodedNavigationState {
        if (rawJson.isNullOrBlank()) {
            return defaultHomeState()
        }

        return try {
            val root = JSONObject(rawJson)
            val schemaVersion = root.optInt("schemaVersion", -1)
            if (schemaVersion <= 0) {
                return defaultHomeState()
            }
            val framesArray = root.optJSONArray("frames") ?: return defaultHomeState()
            val rawCounter = root.optInt("nextInstanceCounter", framesArray.length())

            val decodedFrames = mutableListOf<NavigationFrame>()
            for (i in 0 until framesArray.length()) {
                val frameObj = framesArray.optJSONObject(i) ?: continue
                val route = frameObj.optString("route", "").trim()
                val rawRouteKey = frameObj.optString("routeKey", "")
                val rawInstanceId = frameObj.optString("screenInstanceId", "")
                val args = frameObj.optJSONObject("args") ?: JSONObject()

                val decodedFrame = decodeSingleFrame(
                    route = route,
                    rawRouteKey = rawRouteKey,
                    rawInstanceId = rawInstanceId,
                    fallbackIndex = i,
                    args = args
                ) ?: continue

                if (sanitizeTransientOnRestore) {
                    val sanitized = sanitizeFrameForProcessDeath(decodedFrame)
                    if (sanitized != null) {
                        appendDeduplicated(decodedFrames, sanitized)
                    }
                } else {
                    decodedFrames.add(decodedFrame)
                }
            }

            if (decodedFrames.isEmpty()) {
                defaultHomeState()
            } else {
                if (decodedFrames.first().screen != Screen.Home) {
                    decodedFrames.add(
                        0,
                        NavigationFrame(
                            screen = Screen.Home,
                            snapshot = NavigationContextSnapshot(
                                screen = Screen.Home,
                                routeKey = Screen.Home.route,
                                screenInstanceId = "${Screen.Home.route}#0"
                            )
                        )
                    )
                }
                DecodedNavigationState(
                    frames = decodedFrames,
                    nextInstanceCounter = rawCounter.coerceAtLeast(decodedFrames.size)
                )
            }
        } catch (_: Exception) {
            defaultHomeState()
        }
    }

    fun computeRouteKey(screen: Screen, snapshot: NavigationContextSnapshot): String {
        return when (screen) {
            is Screen.TableDetail -> "${Screen.TableDetail.route}:${snapshot.tableNumber}"
            is Screen.FormulaDetail -> "${Screen.FormulaDetail.route}:${snapshot.formulaId}"
            is Screen.FormulaLibrary -> snapshot.selectedLibraryFormulaId
                ?.takeIf { it.isNotBlank() }
                ?.let { "${Screen.FormulaLibrary.route}:$it" }
                ?: Screen.FormulaLibrary.route
            is Screen.PracticeTutor -> "${Screen.PracticeTutor.route}:${snapshot.tutorNumber}"
            is Screen.BcsPreparation -> "${Screen.BcsPreparation.route}:${snapshot.bcsExamProfile.id}:${snapshot.bcsSyllabusVersion.id}"
            is Screen.BookDetail -> "${Screen.BookDetail.route}:${snapshot.bookId.id}"
            is Screen.BookChapterOverview -> "${Screen.BookChapterOverview.route}:${snapshot.bookId.id}:${snapshot.bookChapterNumber}"
            is Screen.BookLesson -> "${Screen.BookLesson.route}:${snapshot.bookId.id}:${snapshot.bookChapterNumber}:${snapshot.bookLessonId}"
            is Screen.BookPractice -> "${Screen.BookPractice.route}:${snapshot.bookId.id}:${snapshot.bookChapterNumber}:${snapshot.bookLessonId}"
            is Screen.BookPdfReader -> "${Screen.BookPdfReader.route}:${snapshot.bookId.id}:${snapshot.bookPrintedPage}"
            is Screen.BookInteractiveLab -> "${Screen.BookInteractiveLab.route}:${snapshot.bookId.id}:${snapshot.bookChapterNumber}:${snapshot.bookLabType.id}"
            is Screen.BookRevision -> "${Screen.BookRevision.route}:${snapshot.bookId.id}"
            else -> screen.route
        }
    }

    private fun encodeArgsForScreen(screen: Screen, snap: NavigationContextSnapshot): JSONObject {
        val args = JSONObject()
        when (screen) {
            is Screen.TableDetail -> {
                args.put("tableNumber", snap.tableNumber)
            }
            is Screen.FormulaDetail -> {
                args.put("formulaId", snap.formulaId)
            }
            is Screen.FormulaLibrary -> {
                if (snap.selectedLibraryFormulaId != null) {
                    args.put("selectedLibraryFormulaId", snap.selectedLibraryFormulaId)
                } else {
                    args.put("selectedLibraryFormulaId", JSONObject.NULL)
                }
            }
            is Screen.PracticeTutor -> {
                args.put("tutorNumber", snap.tutorNumber)
            }
            is Screen.BcsPreparation -> {
                args.put("bcsExamProfileId", snap.bcsExamProfile.id)
                args.put("bcsSyllabusVersion", snap.bcsSyllabusVersion.id)
            }
            is Screen.BookDetail, is Screen.BookRevision -> {
                args.put("bookId", snap.bookId.id)
            }
            is Screen.BookChapterOverview -> {
                args.put("bookId", snap.bookId.id)
                args.put("bookChapterNumber", snap.bookChapterNumber)
            }
            is Screen.BookLesson, is Screen.BookPractice -> {
                args.put("bookId", snap.bookId.id)
                args.put("bookChapterNumber", snap.bookChapterNumber)
                args.put("bookLessonId", snap.bookLessonId)
            }
            is Screen.BookPdfReader -> {
                args.put("bookId", snap.bookId.id)
                args.put("bookPrintedPage", snap.bookPrintedPage)
            }
            is Screen.BookInteractiveLab -> {
                args.put("bookId", snap.bookId.id)
                args.put("bookChapterNumber", snap.bookChapterNumber)
                args.put("bookLabType", snap.bookLabType.id)
            }
            else -> {
                // Non-parameterized and empty-arg routes keep an explicit empty args object `{}`
            }
        }
        return args
    }

    private fun decodeSingleFrame(
        route: String,
        rawRouteKey: String,
        rawInstanceId: String,
        fallbackIndex: Int,
        args: JSONObject
    ): NavigationFrame? {
        val declaredScreen = screenByRoute[route] ?: return null
        var resolvedScreen: Screen = declaredScreen

        var tableNumber = 1
        var formulaId = "sq_1"
        var selectedLibraryFormulaId: String? = null
        var tutorNumber = 7
        var bcsExamProfile = BcsExamProfile.BCS_50TH_PRELIM
        var bcsSyllabusVersion = BcsSyllabusVersion.BCS_50TH_CURRENT
        var bookId = BookId.CLASS_6
        var bookChapterNumber = 1
        var bookLessonId = "c6_1_l1"
        var bookPrintedPage = 1
        var bookLabType = BookInteractiveLabType.NUMBER_LINE_LAB

        when (declaredScreen) {
            is Screen.TableDetail -> {
                val parsedTable = readStrictIntArg(args, "tableNumber")
                if (parsedTable != null && parsedTable in 1..1000) {
                    tableNumber = parsedTable
                } else {
                    resolvedScreen = Screen.Tables
                }
            }
            is Screen.FormulaDetail -> {
                val parsedFormula = args.optString("formulaId", "").takeIf { !args.isNull("formulaId") }
                if (!parsedFormula.isNullOrBlank() && AlgebraFormulaProvider.getById(parsedFormula) != null) {
                    formulaId = parsedFormula
                } else {
                    resolvedScreen = Screen.Formulas
                }
            }
            is Screen.FormulaLibrary -> {
                val rawLib = if (args.isNull("selectedLibraryFormulaId")) {
                    null
                } else {
                    args.optString("selectedLibraryFormulaId", "").takeIf { it.isNotBlank() }
                }
                selectedLibraryFormulaId = rawLib
            }
            is Screen.PracticeTutor -> {
                val parsedTutor = readStrictIntArg(args, "tutorNumber")
                tutorNumber = if (parsedTutor != null && parsedTutor in 1..1000) parsedTutor else 7
            }
            is Screen.BcsPreparation -> {
                val profileId = args.optString("bcsExamProfileId", "").takeIf { !args.isNull("bcsExamProfileId") }
                val syllabusVer = args.optString("bcsSyllabusVersion", "").takeIf { !args.isNull("bcsSyllabusVersion") }
                bcsExamProfile = BcsExamProfile.entries.firstOrNull {
                    it.id.equals(profileId, ignoreCase = true) || it.name.equals(profileId, ignoreCase = true)
                } ?: BcsExamProfile.BCS_50TH_PRELIM
                bcsSyllabusVersion = BcsSyllabusVersion.entries.firstOrNull {
                    it.id.equals(syllabusVer, ignoreCase = true) || it.name.equals(syllabusVer, ignoreCase = true)
                } ?: if (bcsExamProfile == BcsExamProfile.BCS_LEGACY_PRELIM) {
                    BcsSyllabusVersion.BCS_LEGACY_15
                } else {
                    BcsSyllabusVersion.BCS_50TH_CURRENT
                }
            }
            is Screen.BookDetail, is Screen.BookRevision -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook != null) {
                    bookId = parsedBook
                } else {
                    resolvedScreen = Screen.BookHub
                }
            }
            is Screen.BookChapterOverview -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook == null) {
                    resolvedScreen = Screen.BookHub
                } else {
                    bookId = parsedBook
                    val parsedChapter = readStrictIntArg(args, "bookChapterNumber")
                    if (parsedChapter != null && BookCurriculumRepository.getChapter(parsedBook, parsedChapter) != null) {
                        bookChapterNumber = parsedChapter
                    } else {
                        resolvedScreen = Screen.BookDetail
                    }
                }
            }
            is Screen.BookLesson -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook == null) {
                    resolvedScreen = Screen.BookHub
                } else {
                    bookId = parsedBook
                    val parsedChapter = readStrictIntArg(args, "bookChapterNumber")
                    if (parsedChapter == null || BookCurriculumRepository.getChapter(parsedBook, parsedChapter) == null) {
                        resolvedScreen = Screen.BookDetail
                    } else {
                        bookChapterNumber = parsedChapter
                        val parsedLesson = if (args.isNull("bookLessonId")) "" else args.optString("bookLessonId", "")
                        if (parsedLesson.isNotBlank()) {
                            bookLessonId = parsedLesson
                        } else {
                            bookLessonId = ""
                            resolvedScreen = Screen.BookChapterOverview
                        }
                    }
                }
            }
            is Screen.BookPractice -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook == null) {
                    resolvedScreen = Screen.BookHub
                } else {
                    bookId = parsedBook
                    val parsedChapter = readStrictIntArg(args, "bookChapterNumber")
                    if (parsedChapter == null || BookCurriculumRepository.getChapter(parsedBook, parsedChapter) == null) {
                        resolvedScreen = Screen.BookDetail
                    } else {
                        bookChapterNumber = parsedChapter
                        bookLessonId = if (args.isNull("bookLessonId")) "" else args.optString("bookLessonId", "")
                    }
                }
            }
            is Screen.BookPdfReader -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook == null) {
                    resolvedScreen = Screen.BookHub
                } else {
                    bookId = parsedBook
                    val parsedPage = readStrictIntArg(args, "bookPrintedPage")
                    bookPrintedPage = (parsedPage ?: 1).coerceIn(1, parsedBook.maxPrintedPage)
                    bookChapterNumber = BookCurriculumRepository.getChapterForPrintedPage(parsedBook, bookPrintedPage).chapterNumber
                }
            }
            is Screen.BookInteractiveLab -> {
                val parsedBook = parseBookIdOrNull(args)
                if (parsedBook == null) {
                    resolvedScreen = Screen.BookHub
                } else {
                    bookId = parsedBook
                    val parsedChapter = readStrictIntArg(args, "bookChapterNumber")
                    if (parsedChapter == null || BookCurriculumRepository.getChapter(parsedBook, parsedChapter) == null) {
                        resolvedScreen = Screen.BookDetail
                    } else {
                        bookChapterNumber = parsedChapter
                        val rawLab = if (args.isNull("bookLabType")) "" else args.optString("bookLabType", "")
                        val parsedLab = BookInteractiveLabType.entries.firstOrNull {
                            it.id.equals(rawLab, ignoreCase = true) || it.name.equals(rawLab, ignoreCase = true)
                        }
                        if (parsedLab != null) {
                            bookLabType = parsedLab
                        } else {
                            resolvedScreen = Screen.BookChapterOverview
                        }
                    }
                }
            }
            else -> Unit
        }

        val baseSnapshot = NavigationContextSnapshot(
            screen = resolvedScreen,
            tableNumber = tableNumber,
            formulaId = formulaId,
            selectedLibraryFormulaId = selectedLibraryFormulaId,
            tutorNumber = tutorNumber,
            bcsExamProfile = bcsExamProfile,
            bcsSyllabusVersion = bcsSyllabusVersion,
            bookId = bookId,
            bookChapterNumber = bookChapterNumber,
            bookLessonId = bookLessonId,
            bookPrintedPage = bookPrintedPage,
            bookLabType = bookLabType
        )
        val preservedRouteKey = if (resolvedScreen == declaredScreen && rawRouteKey.isNotBlank()) {
            rawRouteKey
        } else {
            computeRouteKey(resolvedScreen, baseSnapshot)
        }
        val preservedInstanceId = if (resolvedScreen == declaredScreen && rawInstanceId.isNotBlank()) {
            rawInstanceId
        } else {
            "$preservedRouteKey#$fallbackIndex"
        }

        return NavigationFrame(
            screen = resolvedScreen,
            snapshot = baseSnapshot.copy(
                routeKey = preservedRouteKey,
                screenInstanceId = preservedInstanceId
            )
        )
    }

    private fun sanitizeFrameForProcessDeath(frame: NavigationFrame): NavigationFrame? {
        val snap = frame.snapshot
        return when (frame.screen) {
            is Screen.Customize -> null
            is Screen.PracticeLabPlay -> remapFrame(Screen.PracticeLab, snap)
            is Screen.MentalMathPlay -> remapFrame(Screen.MentalMath, snap)
            is Screen.NumberSystemPractice -> remapFrame(Screen.NumberSystem, snap)
            is Screen.QuizPlay -> remapFrame(Screen.Quizzes, snap)
            is Screen.BookPractice -> {
                val targetScreen = if (snap.bookLessonId.isNotBlank()) {
                    Screen.BookLesson
                } else {
                    Screen.BookChapterOverview
                }
                remapFrame(targetScreen, snap)
            }
            else -> frame
        }
    }

    private fun remapFrame(targetScreen: Screen, oldSnap: NavigationContextSnapshot): NavigationFrame {
        val nextBase = oldSnap.copy(screen = targetScreen)
        val newRouteKey = computeRouteKey(targetScreen, nextBase)
        return NavigationFrame(
            screen = targetScreen,
            snapshot = nextBase.copy(
                routeKey = newRouteKey,
                screenInstanceId = "$newRouteKey#sanitized"
            )
        )
    }

    private fun appendDeduplicated(
        list: MutableList<NavigationFrame>,
        incoming: NavigationFrame
    ) {
        val prev = list.lastOrNull()
        if (prev != null && areFramesEquivalent(prev, incoming)) {
            return
        }
        list.add(incoming)
    }

    private fun areFramesEquivalent(a: NavigationFrame, b: NavigationFrame): Boolean {
        if (a.screen != b.screen) return false
        return computeRouteKey(a.screen, a.snapshot) == computeRouteKey(b.screen, b.snapshot)
    }

    private fun parseBookIdOrNull(args: JSONObject): BookId? {
        if (args.isNull("bookId")) return null
        val raw = args.optString("bookId", "").trim()
        if (raw.isEmpty()) return null
        return BookId.entries.firstOrNull {
            it.id.equals(raw, ignoreCase = true) || it.name.equals(raw, ignoreCase = true)
        }
    }

    private fun readStrictIntArg(args: JSONObject, key: String): Int? {
        if (!args.has(key) || args.isNull(key)) return null
        val raw = args.opt(key) ?: return null
        return when (raw) {
            is Number -> {
                val d = raw.toDouble()
                if (d % 1.0 == 0.0) d.toInt() else null
            }
            is String -> raw.trim().toIntOrNull()
            else -> null
        }
    }

    private fun defaultHomeState(): DecodedNavigationState {
        return DecodedNavigationState(
            frames = listOf(
                NavigationFrame(
                    screen = Screen.Home,
                    snapshot = NavigationContextSnapshot(
                        screen = Screen.Home,
                        routeKey = Screen.Home.route,
                        screenInstanceId = "${Screen.Home.route}#0"
                    )
                )
            ),
            nextInstanceCounter = 1
        )
    }
}

/**
 * Reusable application-wide scroll and UI context restoration hooks (BUG-36 / BUG-37 / BUG-38 / BUG-39
 * + Phase 2 Process-Death Bundle Stability).
 */
@Composable
fun rememberManagedLazyListState(
    viewModel: MathViewModel,
    stateKey: String
): LazyListState {
    val initialScrollPosition = remember(viewModel, stateKey) {
        viewModel.getScrollPosition(stateKey)
    }
    val listState = rememberSaveable(
        stateKey,
        saver = LazyListState.Saver,
        key = stateKey
    ) {
        LazyListState(
            firstVisibleItemIndex = initialScrollPosition.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = initialScrollPosition.firstVisibleItemScrollOffset
        )
    }

    val hasActivelyScrolled = remember(listState, stateKey) {
        mutableStateOf(false)
    }
    val hasRestoredAfterDataLoad = remember(listState, stateKey) {
        mutableStateOf(
            (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) ||
                (initialScrollPosition.firstVisibleItemIndex == 0 &&
                    initialScrollPosition.firstVisibleItemScrollOffset == 0)
        )
    }

    // Sync any Bundle-restored scroll offset back into a newly instantiated ViewModel after process death
    LaunchedEffect(stateKey, viewModel) {
        val currentIdx = listState.firstVisibleItemIndex
        val currentOffset = listState.firstVisibleItemScrollOffset
        if (currentIdx > 0 || currentOffset > 0) {
            hasRestoredAfterDataLoad.value = true
            viewModel.updateScrollPosition(
                stateKey = stateKey,
                firstVisibleItemIndex = currentIdx,
                firstVisibleItemScrollOffset = currentOffset
            )
        }
    }

    LaunchedEffect(listState, stateKey, viewModel) {
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collect { totalItemsCount ->
                if (!hasRestoredAfterDataLoad.value && totalItemsCount > 0) {
                    val saved = viewModel.getScrollPosition(stateKey)
                    if (saved.firstVisibleItemIndex > 0 || saved.firstVisibleItemScrollOffset > 0) {
                        val targetIndex = saved.firstVisibleItemIndex.coerceAtMost((totalItemsCount - 1).coerceAtLeast(0))
                        val targetOffset = saved.firstVisibleItemScrollOffset.coerceAtLeast(0)
                        if (listState.firstVisibleItemIndex != targetIndex ||
                            listState.firstVisibleItemScrollOffset != targetOffset
                        ) {
                            listState.scrollToItem(targetIndex, targetOffset)
                        }
                        if (totalItemsCount > saved.firstVisibleItemIndex) {
                            hasRestoredAfterDataLoad.value = true
                        }
                    } else {
                        hasRestoredAfterDataLoad.value = true
                    }
                }
            }
    }

    LaunchedEffect(listState, stateKey, viewModel) {
        snapshotFlow {
            Triple(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset,
                listState.isScrollInProgress
            )
        }.collect { (index, offset, scrolling) ->
            if (scrolling) {
                hasActivelyScrolled.value = true
            }
            if (index > 0 || offset > 0) {
                if (index != initialScrollPosition.firstVisibleItemIndex ||
                    offset != initialScrollPosition.firstVisibleItemScrollOffset
                ) {
                    hasActivelyScrolled.value = true
                }
                hasRestoredAfterDataLoad.value = true
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = index,
                    firstVisibleItemScrollOffset = offset
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterDataLoad.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    DisposableEffect(listState, stateKey, viewModel) {
        onDispose {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (index > 0 || offset > 0) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = index,
                    firstVisibleItemScrollOffset = offset
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterDataLoad.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    return listState
}

@Composable
fun rememberManagedLazyGridState(
    viewModel: MathViewModel,
    stateKey: String
): LazyGridState {
    val initialScrollPosition = remember(viewModel, stateKey) {
        viewModel.getScrollPosition(stateKey)
    }
    val gridState = rememberSaveable(
        stateKey,
        saver = LazyGridState.Saver,
        key = stateKey
    ) {
        LazyGridState(
            firstVisibleItemIndex = initialScrollPosition.firstVisibleItemIndex,
            firstVisibleItemScrollOffset = initialScrollPosition.firstVisibleItemScrollOffset
        )
    }

    val hasActivelyScrolled = remember(gridState, stateKey) {
        mutableStateOf(false)
    }
    val hasRestoredAfterDataLoad = remember(gridState, stateKey) {
        mutableStateOf(
            (gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0) ||
                (initialScrollPosition.firstVisibleItemIndex == 0 &&
                    initialScrollPosition.firstVisibleItemScrollOffset == 0)
        )
    }

    // Sync any Bundle-restored scroll offset back into a newly instantiated ViewModel after process death
    LaunchedEffect(stateKey, viewModel) {
        val currentIdx = gridState.firstVisibleItemIndex
        val currentOffset = gridState.firstVisibleItemScrollOffset
        if (currentIdx > 0 || currentOffset > 0) {
            hasRestoredAfterDataLoad.value = true
            viewModel.updateScrollPosition(
                stateKey = stateKey,
                firstVisibleItemIndex = currentIdx,
                firstVisibleItemScrollOffset = currentOffset
            )
        }
    }

    LaunchedEffect(gridState, stateKey, viewModel) {
        snapshotFlow { gridState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collect { totalItemsCount ->
                if (!hasRestoredAfterDataLoad.value && totalItemsCount > 0) {
                    val saved = viewModel.getScrollPosition(stateKey)
                    if (saved.firstVisibleItemIndex > 0 || saved.firstVisibleItemScrollOffset > 0) {
                        val targetIndex = saved.firstVisibleItemIndex.coerceAtMost((totalItemsCount - 1).coerceAtLeast(0))
                        val targetOffset = saved.firstVisibleItemScrollOffset.coerceAtLeast(0)
                        if (gridState.firstVisibleItemIndex != targetIndex ||
                            gridState.firstVisibleItemScrollOffset != targetOffset
                        ) {
                            gridState.scrollToItem(targetIndex, targetOffset)
                        }
                        if (totalItemsCount > saved.firstVisibleItemIndex) {
                            hasRestoredAfterDataLoad.value = true
                        }
                    } else {
                        hasRestoredAfterDataLoad.value = true
                    }
                }
            }
    }

    LaunchedEffect(gridState, stateKey, viewModel) {
        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress
            )
        }.collect { (index, offset, scrolling) ->
            if (scrolling) {
                hasActivelyScrolled.value = true
            }
            if (index > 0 || offset > 0) {
                if (index != initialScrollPosition.firstVisibleItemIndex ||
                    offset != initialScrollPosition.firstVisibleItemScrollOffset
                ) {
                    hasActivelyScrolled.value = true
                }
                hasRestoredAfterDataLoad.value = true
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = index,
                    firstVisibleItemScrollOffset = offset
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterDataLoad.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    DisposableEffect(gridState, stateKey, viewModel) {
        onDispose {
            val index = gridState.firstVisibleItemIndex
            val offset = gridState.firstVisibleItemScrollOffset
            if (index > 0 || offset > 0) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = index,
                    firstVisibleItemScrollOffset = offset
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterDataLoad.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    return gridState
}

@Composable
fun rememberManagedScrollState(
    viewModel: MathViewModel,
    stateKey: String
): ScrollState {
    val initialOffset = remember(viewModel, stateKey) {
        viewModel.getScrollPosition(stateKey).firstVisibleItemScrollOffset
    }
    val scrollState = rememberSaveable(
        stateKey,
        saver = ScrollState.Saver,
        key = stateKey
    ) {
        ScrollState(initial = initialOffset)
    }

    val hasActivelyScrolled = remember(scrollState, stateKey) {
        mutableStateOf(false)
    }
    val hasRestoredAfterLayout = remember(scrollState, stateKey) {
        mutableStateOf(scrollState.value > 0 || initialOffset == 0)
    }

    LaunchedEffect(stateKey, viewModel) {
        val currentOffset = scrollState.value
        if (currentOffset > 0) {
            hasRestoredAfterLayout.value = true
            viewModel.updateScrollPosition(
                stateKey = stateKey,
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = currentOffset
            )
        }
    }

    LaunchedEffect(scrollState, stateKey, viewModel) {
        snapshotFlow { scrollState.maxValue }
            .distinctUntilChanged()
            .collect { maxScrollValue ->
                if (!hasRestoredAfterLayout.value && maxScrollValue > 0 && maxScrollValue != Int.MAX_VALUE) {
                    val savedOffset = viewModel.getScrollPosition(stateKey).firstVisibleItemScrollOffset
                    if (savedOffset > 0) {
                        val target = savedOffset.coerceIn(0, maxScrollValue)
                        if (scrollState.value != target) {
                            scrollState.scrollTo(target)
                        }
                        if (maxScrollValue >= savedOffset) {
                            hasRestoredAfterLayout.value = true
                        }
                    } else {
                        hasRestoredAfterLayout.value = true
                    }
                }
            }
    }

    LaunchedEffect(scrollState, stateKey, viewModel) {
        snapshotFlow {
            scrollState.value to scrollState.isScrollInProgress
        }.collect { (value, scrolling) ->
            if (scrolling) {
                hasActivelyScrolled.value = true
            }
            if (value > 0) {
                if (value != initialOffset) {
                    hasActivelyScrolled.value = true
                }
                hasRestoredAfterLayout.value = true
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = value
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterLayout.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    DisposableEffect(scrollState, stateKey, viewModel) {
        onDispose {
            val value = scrollState.value
            if (value > 0) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = value
                )
            } else if (hasActivelyScrolled.value && hasRestoredAfterLayout.value) {
                viewModel.updateScrollPosition(
                    stateKey = stateKey,
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            }
        }
    }

    return scrollState
}

@Composable
fun <T> rememberManagedUiState(
    viewModel: MathViewModel,
    stateKey: String,
    defaultValue: T
): MutableState<T> {
    val initialValue = remember(viewModel, stateKey) {
        viewModel.getUiContextState(stateKey, defaultValue)
    }
    val state = rememberSaveable(
        stateKey,
        saver = managedUiStateSaver(),
        key = stateKey
    ) {
        mutableStateOf(initialValue)
    }

    val unsetSentinel = remember { Any() }
    val lastSyncedVmValue = remember(state, stateKey, viewModel) {
        mutableStateOf<Any?>(unsetSentinel)
    }

    // Push local UI updates to ViewModel
    LaunchedEffect(state, stateKey, viewModel) {
        androidx.compose.runtime.snapshotFlow { state.value }
            .distinctUntilChanged()
            .collect { uiVal ->
                val alreadySynced = lastSyncedVmValue.value !== unsetSentinel && uiVal == lastSyncedVmValue.value
                if (!alreadySynced) {
                    val vmHasKey = viewModel.hasUiContextKey(stateKey)
                    if (uiVal != defaultValue || vmHasKey) {
                        lastSyncedVmValue.value = uiVal
                        viewModel.updateUiContextState(stateKey, uiVal)
                    }
                }
            }
    }

    // Subscribe ONLY to this key's isolated slot (0 unrelated collector wakeups)
    LaunchedEffect(state, stateKey, viewModel) {
        viewModel.observeUiContextKey(stateKey).collect { entry ->
            when (entry) {
                is UiContextEntry.Present -> {
                    @Suppress("UNCHECKED_CAST")
                    val typedVmVal = entry.value as T
                    if (lastSyncedVmValue.value === unsetSentinel || typedVmVal != lastSyncedVmValue.value) {
                        lastSyncedVmValue.value = typedVmVal
                        if (state.value != typedVmVal) {
                            state.value = typedVmVal
                        }
                    }
                }
                UiContextEntry.Absent -> {
                    if (lastSyncedVmValue.value === unsetSentinel && state.value != defaultValue) {
                        // Bundle restored a non-default value after process death into a fresh ViewModel
                        lastSyncedVmValue.value = state.value
                        viewModel.updateUiContextState(stateKey, state.value)
                    } else if (lastSyncedVmValue.value !== unsetSentinel) {
                        lastSyncedVmValue.value = unsetSentinel
                    }
                }
            }
        }
    }

    DisposableEffect(state, stateKey, viewModel) {
        onDispose {
            val currentVal = state.value
            if (currentVal != initialValue &&
                (lastSyncedVmValue.value === unsetSentinel || currentVal != lastSyncedVmValue.value)
            ) {
                val vmHasKey = viewModel.hasUiContextKey(stateKey)
                if (currentVal != defaultValue || vmHasKey) {
                    viewModel.updateUiContextState(stateKey, currentVal)
                }
            }
        }
    }

    return state
}

private const val NULL_STATE_SENTINEL = "__MATH_MASTER_NULL_STATE__"
internal const val SET_STATE_SENTINEL = "__MATH_MASTER_SET__"
internal const val LIST_STATE_SENTINEL = "__MATH_MASTER_LIST__"

internal fun <T> managedUiStateSaver(): Saver<MutableState<T>, Any> = Saver(
    save = { state ->
        when (val v = state.value) {
            null -> NULL_STATE_SENTINEL
            is Set<*> -> ArrayList<Any?>(v.size + 1).apply {
                add(SET_STATE_SENTINEL)
                addAll(v)
            }
            is List<*> -> ArrayList<Any?>(v.size + 1).apply {
                add(LIST_STATE_SENTINEL)
                addAll(v)
            }
            is Int, is Long, is Float, is Double, is Boolean, is String, is Enum<*> -> v
            is java.io.Serializable -> v
            else -> v.toString()
        }
    },
    restore = { saved ->
        @Suppress("UNCHECKED_CAST")
        val restored: T = when (saved) {
            NULL_STATE_SENTINEL -> null as T
            is ArrayList<*> -> when (saved.firstOrNull()) {
                SET_STATE_SENTINEL -> saved.drop(1).toCollection(LinkedHashSet()) as T
                LIST_STATE_SENTINEL -> saved.drop(1).toList() as T
                else -> saved.toList() as T
            }
            else -> saved as T
        }
        mutableStateOf(restored)
    }
)
