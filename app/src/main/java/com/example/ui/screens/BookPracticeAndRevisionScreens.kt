package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.math.BengaliNumberFormatter
import com.example.math.BookId
import com.example.math.BookPracticeMode
import com.example.math.BookQuestionFormat
import com.example.ui.MathViewModel
import com.example.ui.rememberManagedLazyListState
import com.example.ui.rememberManagedScrollState
import com.example.ui.rememberManagedUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPracticeScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookPracticeSession.collectAsStateWithLifecycle()
    val currentQuestion = session.currentQuestion

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${session.bookId.shortLabelBn} • ${session.mode.titleBn}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        val chText = session.chapterNumber?.let { "অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(it)}" } ?: "সব অধ্যায়"
                        Text(
                            text = "$chText • সঠিক: ${BengaliNumberFormatter.toBengaliDigits(session.correctCount)} | ভুল: ${BengaliNumberFormatter.toBengaliDigits(session.wrongCount)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_practice_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (session.isCompleted || currentQuestion == null) {
            BookPracticeResultView(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            return@Scaffold
        }

        val practiceListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_practice:${session.bookId.id}:${session.currentIndex}"
        )
        val modeStripScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_practice:${session.bookId.id}:mode_strip"
        )
        LazyColumn(
            state = practiceListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_practice_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Practice Mode Switcher Strip
            item(key = "practice_mode_switcher") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(modeStripScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BookPracticeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = session.mode == mode,
                            onClick = {
                                viewModel.startBookPractice(
                                    bookId = session.bookId,
                                    chapterNumber = session.chapterNumber,
                                    mode = mode
                                )
                            },
                            label = { Text(mode.titleBn) },
                            modifier = Modifier.testTag("switch_practice_mode_${mode.id}")
                        )
                    }
                }
            }

            // 2. Question Progress & Provenance / Format Badges
            item(key = "question_header_badges") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রশ্ন ${BengaliNumberFormatter.toBengaliDigits(session.currentIndex + 1)} / ${BengaliNumberFormatter.toBengaliDigits(session.questions.size)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = currentQuestion.format.labelBn,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                val pageSuffix = currentQuestion.sourcePrintedPage?.let {
                                    " (পৃ. ${BengaliNumberFormatter.toBengaliDigits(it)})"
                                } ?: ""
                                Text(
                                    text = "${currentQuestion.provenance.badgeBn}$pageSuffix",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (session.currentIndex + 1).toFloat() / session.questions.size.coerceAtLeast(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }

            // 3. Question Prompt & Matching Pairs Display (if MATCHING_PAIRS)
            item(key = "question_prompt_card") {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_practice_question_card"),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = currentQuestion.promptBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (currentQuestion.matchingPairs.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "মিলকরণের উপাদানসমূহ:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    currentQuestion.matchingPairs.forEachIndexed { idx, pair ->
                                        Text(
                                            text = "${BengaliNumberFormatter.toBengaliDigits(idx + 1)}. ${pair.first}  ↔  ${pair.second}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Input / Options Area supporting all 10 formats
            val requiresTextBox = currentQuestion.format in setOf(
                BookQuestionFormat.NUMERICAL_INPUT,
                BookQuestionFormat.FILL_IN_THE_BLANK,
                BookQuestionFormat.SHORT_ANSWER
            )

            if (requiresTextBox) {
                item(key = "question_text_input") {
                    OutlinedTextField(
                        value = session.userTextInput,
                        onValueChange = { viewModel.setBookPracticeTextInput(it) },
                        enabled = !session.isAnswerSubmitted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("book_practice_text_input"),
                        label = { Text("উত্তর লিখুন (বাংলা বা ইংরেজি সংখ্যায়) অথবা নিচের অপশন বাছুন") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            items(currentQuestion.optionsBn.indices.toList(), key = { "opt_$it" }) { idx ->
                val optionText = currentQuestion.optionsBn[idx]
                val isSelected = session.selectedOptionIndices.contains(idx)
                val isCorrectOption = currentQuestion.correctOptionIndices.contains(idx)

                val containerColor = when {
                    session.isAnswerSubmitted && isCorrectOption -> Color(0xFF2E7D32).copy(alpha = 0.18f)
                    session.isAnswerSubmitted && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = containerColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !session.isAnswerSubmitted) {
                            viewModel.toggleBookPracticeOption(idx)
                        }
                        .testTag("book_practice_option_$idx")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentQuestion.format == BookQuestionFormat.MULTIPLE_SELECT) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = null
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // 5. Hint Toggle & Step-by-Step Solution after submission
            item(key = "question_hint_and_actions") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { viewModel.toggleBookPracticeHint() },
                            label = { Text(if (session.showHint) "ইঙ্গিত লুকান" else "ইঙ্গিত (Hint) দেখুন") },
                            leadingIcon = {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("book_practice_hint_btn")
                        )

                        if (!session.isAnswerSubmitted) {
                            Button(
                                onClick = { viewModel.submitBookPracticeAnswer() },
                                enabled = session.selectedOptionIndices.isNotEmpty() || session.userTextInput.isNotBlank(),
                                modifier = Modifier.testTag("book_practice_submit_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("উত্তর জমা দিন", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.nextBookPracticeQuestion() },
                                modifier = Modifier.testTag("book_practice_next_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (session.currentIndex + 1 < session.questions.size) "পরবর্তী প্রশ্ন"
                                    else "ফলাফল দেখুন",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (session.showHint) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "💡 ইঙ্গিত: ${currentQuestion.hintBn}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    if (session.isAnswerSubmitted) {
                        val isCorrect = session.isCurrentCorrect == true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_practice_explanation_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) {
                                    Color(0xFF2E7D32).copy(alpha = 0.14f)
                                } else {
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "✅ অভিনন্দন! আপনার উত্তর সঠিক।"
                                    else "❌ উত্তরটি সঠিক হয়নি (ভুল সংশোধনী খাতায় ও রিভিশন শিডিউলে যুক্ত হয়েছে)।",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "সঠিক উত্তর: ${currentQuestion.correctTextAnswer}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = "ধাপে ধাপে সমাধান:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                currentQuestion.stepByStepSolutionBn.forEachIndexed { i, step ->
                                    Text(
                                        text = "ধাপ ${BengaliNumberFormatter.toBengaliDigits(i + 1)}: $step",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookPracticeResultView(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookPracticeSession.collectAsStateWithLifecycle()
    val total = session.questions.size.coerceAtLeast(1)
    val accuracy = (session.correctCount * 100) / total

    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("book_practice_result_card"),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    text = "অনুশীলন সেশন সম্পন্ন!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${session.bookId.titleBn} • ${session.mode.titleBn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "স্কোর: ${BengaliNumberFormatter.toBengaliDigits(session.correctCount)} / ${BengaliNumberFormatter.toBengaliDigits(total)} (${BengaliNumberFormatter.toBengaliDigits(accuracy)}%)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.startBookPractice(
                                bookId = session.bookId,
                                chapterNumber = session.chapterNumber,
                                mode = session.mode
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পুনরায় অনুশীলন")
                    }
                    OutlinedButton(
                        onClick = { viewModel.openBookRevision(session.bookId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ভুল সংশোধনী খাতা")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRevisionScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val selectedBookId by viewModel.selectedBookId.collectAsStateWithLifecycle()
    val mistakes by viewModel.bookMistakes.collectAsStateWithLifecycle()
    val notes by viewModel.bookNotes.collectAsStateWithLifecycle()
    val spacedRevisions by viewModel.bookSpacedRevisions.collectAsStateWithLifecycle()
    val readingStates by viewModel.bookReadingStates.collectAsStateWithLifecycle()

    var activeTab by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_revision:activeTab",
        defaultValue = 0
    ) // 0: Spaced Revision (1,3,7,14d), 1: Mistake Notebook, 2: Personal Notes, 3: Bookmarked Pages
    var filterBookId by rememberManagedUiState<BookId?>(
        viewModel = viewModel,
        stateKey = "book_revision:filterBookId",
        defaultValue = null
    ) // null = All Books
    var newNoteTitle by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_revision:newNoteTitle",
        defaultValue = ""
    )
    var newNoteBody by rememberManagedUiState(
        viewModel = viewModel,
        stateKey = "book_revision:newNoteBody",
        defaultValue = ""
    )

    val filteredSpacedRevisions = remember(spacedRevisions, filterBookId) {
        if (filterBookId == null) spacedRevisions else spacedRevisions.filter { it.bookId == filterBookId!!.id }
    }
    val filteredMistakes = remember(mistakes, filterBookId) {
        if (filterBookId == null) mistakes else mistakes.filter { it.bookId == filterBookId!!.id }
    }
    val filteredNotes = remember(notes, filterBookId) {
        if (filterBookId == null) notes else notes.filter { it.bookId == filterBookId!!.id }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "রিভিশন, ভুল সংশোধনী ও নোটবুক",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "১, ৩, ৭ ও ১৪ দিনের স্পেসড রিভিশন এবং ব্যক্তিগত নোট",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_revision_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        }
    ) { innerPadding ->
        val revisionListState = rememberManagedLazyListState(
            viewModel = viewModel,
            stateKey = "book_revision:tab_$activeTab"
        )
        val bookFilterScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_revision:book_filter_row"
        )
        val tabRowScrollState = rememberManagedScrollState(
            viewModel = viewModel,
            stateKey = "book_revision:tab_row"
        )
        LazyColumn(
            state = revisionListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("book_revision_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Book Filter Row (All Books vs Specific Class Book)
            item(key = "revision_book_filter") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(bookFilterScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterBookId == null,
                        onClick = { filterBookId = null },
                        label = { Text("সব বই", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("revision_filter_all")
                    )
                    BookId.entries.forEach { bId ->
                        FilterChip(
                            selected = filterBookId == bId,
                            onClick = {
                                filterBookId = bId
                                viewModel.selectBook(bId)
                            },
                            label = { Text(bId.shortLabelBn, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("revision_filter_${bId.id}")
                        )
                    }
                }
            }

            // Tab Bar
            item(key = "revision_tabs") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabRowScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0 to "স্পেসড রিভিশন (${BengaliNumberFormatter.toBengaliDigits(filteredSpacedRevisions.size)})",
                        1 to "ভুল সংশোধনী খাতা (${BengaliNumberFormatter.toBengaliDigits(filteredMistakes.size)})",
                        2 to "ব্যক্তিগত নোট (${BengaliNumberFormatter.toBengaliDigits(filteredNotes.size)})",
                        3 to "বুকমার্ককৃত পৃষ্ঠা"
                    ).forEach { (idx, label) ->
                        FilterChip(
                            selected = activeTab == idx,
                            onClick = { activeTab = idx },
                            label = { Text(label, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("revision_tab_$idx")
                        )
                    }
                }
            }

            when (activeTab) {
                0 -> {
                    item(key = "spaced_revision_info") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "বিজ্ঞানসম্মত স্পেসড রিভিশন শিডিউল: ১ দিন → ৩ দিন → ৭ দিন → ১৪ দিন",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "দুর্বল পাঠ, ভুল হওয়া প্রশ্ন এবং চিহ্নিত সূত্রগুলো এই তালিকায় স্বয়ংক্রিয়ভাবে যুক্ত হয়।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    if (filteredSpacedRevisions.isEmpty()) {
                        item(key = "empty_revisions") {
                            Text(
                                text = "বর্তমানে রিভিশন তালিকায় কোনো আইটেম নেই। অধ্যায় থেকে সূত্র যুক্ত করুন বা অনুশীলন শুরু করুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(filteredSpacedRevisions, key = { it.itemId }) { rev ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("spaced_rev_card_${rev.itemId}"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "ব্যবধান: ${BengaliNumberFormatter.toBengaliDigits(rev.intervalDays)} দিন (ধাপ ${BengaliNumberFormatter.toBengaliDigits(rev.intervalStepIndex + 1)}/৪)",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "পুনরাবৃত্তি: ${BengaliNumberFormatter.toBengaliDigits(rev.completedReviewsCount)} বার",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = rev.titleBn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = rev.summaryBn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.completeSpacedRevisionReview(rev.itemId, rememberedWell = true) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_rev_remembered_${rev.itemId}"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("মনে আছে (পরবর্তী ধাপ)")
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.completeSpacedRevisionReview(rev.itemId, rememberedWell = false) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_rev_reset_${rev.itemId}"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("আবার ১ দিনে রিসেট")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    if (filteredMistakes.isEmpty()) {
                        item(key = "empty_mistakes") {
                            Text(
                                text = "ভুল সংশোধনী খাতায় এখনো কোনো প্রশ্ন নেই। অনুশীলনে কোনো প্রশ্ন ভুল হলে তা স্বয়ংক্রিয়ভাবে এখানে সংরক্ষিত হবে।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(filteredMistakes, key = { it.mistakeId }) { mistake ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mistake_card_${mistake.mistakeId}"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${BookId.fromId(mistake.bookId).shortLabelBn} • অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(mistake.chapterNumber)} • ভুল হয়েছে ${BengaliNumberFormatter.toBengaliDigits(mistake.mistakeCount)} বার",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (mistake.isResolved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { viewModel.deleteBookMistake(mistake.mistakeId) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "মুছুন")
                                        }
                                    }
                                    val provenanceBadge = runCatching {
                                        com.example.math.BookQuestionProvenance.valueOf(mistake.sourceProvenance).badgeBn
                                    }.getOrDefault("মূল পাঠ্যবইয়ের প্রশ্ন")
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = provenanceBadge,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Text(
                                        text = mistake.questionPromptBn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "আপনার উত্তর: ${mistake.userAnswerBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "সঠিক উত্তর: ${mistake.correctAnswerBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "ব্যাখ্যা: ${mistake.explanationBn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.markBookMistakeResolved(mistake.mistakeId, !mistake.isResolved)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (mistake.isResolved) "পুনরায় অনুশীলন তালিকায় নিন" else "সমাধানকৃত হিসেবে চিহ্নিত করুন")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Add Personal Note Box
                    item(key = "add_personal_note_box") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "নতুন ব্যক্তিগত নোট তৈরি করুন (${selectedBookId.shortLabelBn})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedTextField(
                                    value = newNoteTitle,
                                    onValueChange = { newNoteTitle = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("revision_note_title_input"),
                                    label = { Text("নোটের শিরোনাম") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newNoteBody,
                                    onValueChange = { newNoteBody = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("revision_note_body_input"),
                                    label = { Text("নোটের বিস্তারিত বিবরণ বা সূত্র") },
                                    minLines = 2
                                )
                                Button(
                                    onClick = {
                                        if (newNoteTitle.isNotBlank() || newNoteBody.isNotBlank()) {
                                            viewModel.saveBookPersonalNote(
                                                bookId = selectedBookId,
                                                noteTitleBn = newNoteTitle,
                                                noteBodyBn = newNoteBody
                                            )
                                            newNoteTitle = ""
                                            newNoteBody = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("revision_save_note_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("নোট সংরক্ষণ করুন")
                                }
                            }
                        }
                    }

                    items(filteredNotes, key = { it.id }) { note ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("personal_note_card_${note.id}"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${BookId.fromId(note.bookId).shortLabelBn} • অধ্যায় ${BengaliNumberFormatter.toBengaliDigits(note.chapterNumber)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.deleteBookPersonalNote(note.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "মুছুন")
                                    }
                                }
                                Text(
                                    text = note.noteTitleBn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = note.noteBodyBn,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // Bookmarked Textbook Pages across all 4 books (or filtered book)
                    val bookmarkedPages = readingStates
                        .filter { filterBookId == null || it.bookId == filterBookId!!.id }
                        .flatMap { state ->
                            val bId = BookId.fromId(state.bookId)
                            state.bookmarkedPagesCsv.split(",")
                                .mapNotNull { it.trim().toIntOrNull() }
                                .map { page -> bId to page }
                        }

                    if (bookmarkedPages.isEmpty()) {
                        item(key = "empty_page_bookmarks") {
                            Text(
                                text = "কোনো পাঠ্যবই পৃষ্ঠা এখনো বুকমার্ক করা হয়নি। পিডিএফ রিডার থেকে পছন্দের পৃষ্ঠা বুকমার্ক করুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(bookmarkedPages, key = { "${it.first.id}_p_${it.second}" }) { (bId, page) ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.openBookPdfReader(bookId = bId, printedPage = page)
                                    },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${bId.titleBn} — পাঠ্যবই পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(page)}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "পিডিএফ পৃষ্ঠা ${BengaliNumberFormatter.toBengaliDigits(bId.printedToPdfPage(page))}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
