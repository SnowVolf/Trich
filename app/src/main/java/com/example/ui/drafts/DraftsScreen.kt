package com.example.ui.drafts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.drafts.composition.DraftItem
import com.example.ui.shared.FloatingButton
import com.example.ui.shared.FloatingToolbar
import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.res.stringResource
import com.example.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraftsScreen(
    onNavigateToThread: (String, Int) -> Unit,
    onBoardsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: DraftsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDrafts()
    }

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = stringResource(R.string.drafts_title),
                onBoardsClick = onBoardsClick,
                onHistoryClick = onHistoryClick,
                onDraftsClick = { }, // Current
                onFavoritesClick = onFavoritesClick,
                onSettingsClick = null,
                additionalActions = {
                    if (state.drafts.isNotEmpty()) {
                        FloatingButton(icon = Icons.Default.Delete, onClick = {
                            if (state.isSelectionMode) {
                                viewModel.deleteSelected()
                            } else {
                                viewModel.clearAllDrafts()
                            }
                        })
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.drafts.isEmpty()) {
                Text(stringResource(R.string.drafts_empty), modifier = Modifier.align(Alignment.Center).padding(top = padding.calculateTopPadding()))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    items(state.drafts) { draft ->
                        DraftItem(
                            draft = draft,
                            isSelected = state.selectedIds.contains(draft.id),
                            onClick = {
                                if (state.isSelectionMode) {
                                    viewModel.toggleSelection(draft.id)
                                } else {
                                    onNavigateToThread(draft.board, draft.threadNum)
                                }
                            },
                            onLongClick = {
                                viewModel.toggleSelection(draft.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
