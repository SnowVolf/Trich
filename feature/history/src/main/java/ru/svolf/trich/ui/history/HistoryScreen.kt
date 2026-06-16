package ru.svolf.trich.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.svolf.trich.ui.history.composition.HistoryItem
import ru.svolf.trich.uikit.R
import ru.svolf.trich.uikit.components.FloatingToolbar

@Composable
fun HistoryScreen(
    onNavigateToThread: (String, Int) -> Unit,
    onBoardsClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = stringResource(R.string.history_title),
                onBoardsClick = onBoardsClick,
                onHistoryClick = { }, // Current
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
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.threads.isEmpty()) {
                Text(
                    stringResource(R.string.history_empty),
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
                    )
                ) {
                    items(state.threads) { thread ->
                        HistoryItem(
                            thread = thread,
                            onClick = { onNavigateToThread(thread.board, thread.threadNum) }
                        )
                    }
                }
            }
        }
    }
}
