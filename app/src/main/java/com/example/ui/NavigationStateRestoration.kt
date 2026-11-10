package com.example.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime. snapshotFlow

/**
 * Reusable application-wide scroll and UI context restoration hooks (BUG-36 / BUG-37 / BUG-38 / BUG-39).
 *
 * Bridges Jetpack Compose [rememberSaveable] with [MathViewModel]'s destination-scoped state
 * registry so that scroll positions (firstVisibleItemIndex + exact pixel offset) and browsing
 * context (selected internal tabs, active filters, search queries, expanded items) survive:
 * - Forward navigation to child/grandchild screens and Back navigation
 * - Bottom-navigation tab switches
 * - Internal module tab switches (conditional composition removal/reinsertion)
 * - Unrelated recompositions (e.g., Study Session timer ticks)
 * - Activity recreation / configuration changes
 */

@Composable
fun rememberManagedLazyListState(
    viewModel: MathViewModel,
    stateKey: String
): LazyListState {
    val initialScrollPosition = remember(viewModel, stateKey) {
        viewModel.getScrollPosition(stateKey)
    }
    val saveableKey = "$stateKey:${initialScrollPosition.firstVisibleItemIndex}:${initialScrollPosition.firstVisibleItemScrollOffset}"
    val listState = rememberSaveable(
        stateKey,
        initialScrollPosition.firstVisibleItemIndex,
        initialScrollPosition.firstVisibleItemScrollOffset,
        saver = LazyListState.Saver,
        key = saveableKey
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
            initialScrollPosition.firstVisibleItemIndex == 0 &&
                initialScrollPosition.firstVisibleItemScrollOffset == 0
        )
    }

    val totalItemsCount = listState.layoutInfo.totalItemsCount
    LaunchedEffect(stateKey, totalItemsCount) {
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
    val saveableKey = "$stateKey:${initialScrollPosition.firstVisibleItemIndex}:${initialScrollPosition.firstVisibleItemScrollOffset}"
    val gridState = rememberSaveable(
        stateKey,
        initialScrollPosition.firstVisibleItemIndex,
        initialScrollPosition.firstVisibleItemScrollOffset,
        saver = LazyGridState.Saver,
        key = saveableKey
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
            initialScrollPosition.firstVisibleItemIndex == 0 &&
                initialScrollPosition.firstVisibleItemScrollOffset == 0
        )
    }

    val totalItemsCount = gridState.layoutInfo.totalItemsCount
    LaunchedEffect(stateKey, totalItemsCount) {
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
    val saveableKey = "$stateKey:0:$initialOffset"
    val scrollState = rememberSaveable(
        stateKey,
        initialOffset,
        saver = ScrollState.Saver,
        key = saveableKey
    ) {
        ScrollState(initial = initialOffset)
    }

    val hasActivelyScrolled = remember(scrollState, stateKey) {
        mutableStateOf(false)
    }
    val hasRestoredAfterLayout = remember(scrollState, stateKey) {
        mutableStateOf(initialOffset == 0)
    }

    val maxScrollValue = scrollState.maxValue
    LaunchedEffect(stateKey, maxScrollValue) {
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
    val saveableKey = "$stateKey:${initialValue?.hashCode() ?: 0}"
    val state = rememberSaveable(
        stateKey,
        initialValue,
        saver = managedUiStateSaver(),
        key = saveableKey
    ) {
        mutableStateOf(initialValue)
    }

    val hasMutatedInUi = remember(state, stateKey) {
        mutableStateOf(false)
    }

    LaunchedEffect(stateKey, state.value) {
        if (state.value != initialValue) {
            hasMutatedInUi.value = true
            viewModel.updateUiContextState(stateKey, state.value)
        } else if (hasMutatedInUi.value) {
            viewModel.updateUiContextState(stateKey, state.value)
        }
    }

    DisposableEffect(state, stateKey, viewModel) {
        onDispose {
            if (state.value != initialValue || hasMutatedInUi.value) {
                viewModel.updateUiContextState(stateKey, state.value)
            }
        }
    }

    return state
}

private const val NULL_STATE_SENTINEL = "__MATH_MASTER_NULL_STATE__"

private fun <T> managedUiStateSaver(): Saver<MutableState<T>, Any> = Saver(
    save = { state ->
        when (val v = state.value) {
            null -> NULL_STATE_SENTINEL
            is Set<*> -> ArrayList(v)
            is List<*> -> ArrayList(v)
            is Int, is Long, is Float, is Double, is Boolean, is String, is Enum<*> -> v
            is java.io.Serializable -> v
            else -> v.toString()
        }
    },
    restore = { saved ->
        @Suppress("UNCHECKED_CAST")
        val restored: T = when (saved) {
            NULL_STATE_SENTINEL -> null as T
            is ArrayList<*> -> saved.toSet() as T
            else -> saved as T
        }
        mutableStateOf(restored)
    }
)
