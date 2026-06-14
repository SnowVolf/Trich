package com.example.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uikit.R
import com.example.db.FavoriteThread
import com.example.uikit.components.AnimatedAppearance
import com.example.uikit.components.FloatingToolbar
import com.example.uikit.components.FloatingButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    onNavigateToThread: (String, Int) -> Unit,
    onBackClick: () -> Unit,
    onBoardsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = stringResource(R.string.favorites_title),
                onBackClick = onBackClick,
                onBoardsClick = onBoardsClick,
                onHistoryClick = onHistoryClick,
                onDraftsClick = onDraftsClick,
                onFavoritesClick = { }, // Current
                onSettingsClick = onSettingsClick,
                additionalActions = {
                    if (state.favorites.isNotEmpty()) {
                        FloatingButton(icon = Icons.Default.Delete, onClick = {
                            if (state.isSelectionMode) {
                                viewModel.deleteSelected()
                            } else {
                                viewModel.clearAllFavorites()
                            }
                        })
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.favorites.isEmpty()) {
                Text(
                    text = stringResource(R.string.favorites_empty),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = padding.calculateTopPadding())
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(state.favorites) { index, thread ->
                        AnimatedAppearance(index = index) {
                            FavoriteItem(
                                thread = thread,
                                isSelected = state.selectedIds.contains(thread.id),
                                onClick = {
                                    if (state.isSelectionMode) {
                                        viewModel.toggleSelection(thread.id)
                                    } else {
                                        onNavigateToThread(thread.board, thread.threadNum)
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSelection(thread.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteItem(
    thread: FavoriteThread,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(
                0x0AFFFFFF
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val title = thread.title.trim().ifBlank { "Тред №${thread.threadNum}" }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "/${thread.board}/ — №${thread.threadNum}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
