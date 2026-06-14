package com.example.ui.boards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.uikit.components.AnimatedAppearance
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.boards.composition.BoardItem
import com.example.uikit.components.FloatingToolbar
import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.res.stringResource
import com.example.uikit.R

@Composable
fun BoardsScreen(
    onNavigateToBoard: (String) -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: BoardsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = stringResource(R.string.boards_title),
                onBoardsClick = { }, // Current
                onHistoryClick = onHistoryClick,
                onDraftsClick = onDraftsClick,
                onFavoritesClick = onFavoritesClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = stringResource(R.string.error_prefix, state.error ?: ""),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .padding(top = padding.calculateTopPadding()),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = padding.calculateTopPadding() + 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    ) {
                        itemsIndexed(state.boards) { index, board ->
                            AnimatedAppearance(index = index) {
                                BoardItem(board = board, onClick = {
                                    onNavigateToBoard(board.id)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
