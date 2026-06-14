package com.example.ui.threadlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.uikit.components.AnimatedAppearance
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.ThreadSummary
import com.example.uikit.components.FloatingToolbar
import com.example.uikit.components.HtmlText
import com.example.uikit.components.AttachmentImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ThreadListScreen(
    board: String,
    onBackClick: () -> Unit,
    onNavigateToThread: (String, Int) -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNewThreadClick: (String) -> Unit,
    viewModel: ThreadListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(board) {
        viewModel.loadThreads(board)
    }

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = "Список тредов: /$board/",
                onBackClick = onBackClick,
                onBoardsClick = onBackClick,
                onHistoryClick = onHistoryClick,
                onDraftsClick = onDraftsClick,
                onFavoritesClick = onFavoritesClick,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            if (state.canCreateThread) {
                androidx.compose.material3.FloatingActionButton(onClick = { onNewThreadClick(board) }) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Add,
                        contentDescription = "Создать тред"
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding())) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .padding(top = padding.calculateTopPadding()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ошибка загрузки тредов: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadThreads(board, true) }) {
                        Text("Повторить")
                    }
                }
            } else if (state.threads.isEmpty()) {
                Text(
                    text = "Похоже, в этой доске нет активных тредов.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .padding(top = padding.calculateTopPadding())
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(state.threads) { index, thread ->
                        AnimatedAppearance(index = index) {
                            ThreadCard(
                                thread = thread,
                                onClick = { onNavigateToThread(board, thread.num) },
                                fontSize = state.fontSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThreadCard(
    thread: ThreadSummary,
    onClick: () -> Unit,
    fontSize: Int = 16,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0AFFFFFF))
    ) {
        Column {
            val file = thread.files.firstOrNull()
            if (file != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clipToBounds()
                ) {
                    AttachmentImage(
                        url = file.path,
                        onClick = onClick,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    // Title over image
                    val cleanSubject = thread.subject.trim().ifBlank { "Без темы" }
                    HtmlText(
                        html = "<b>$cleanSubject</b>",
                        textColor = Color.White,
                        textSize = fontSize.toFloat() + 2f,
                        maxLines = 2,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }
            } else {
                // Title when no image
                val cleanSubject = thread.subject.trim().ifBlank { "Без темы" }
                HtmlText(
                    html = "<b>$cleanSubject</b>",
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textSize = fontSize.toFloat() + 2f,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                // Thread description/comment up to 5 lines
                val cleanComment = thread.comment.trim().ifBlank { "..." }
                HtmlText(
                    html = cleanComment,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textSize = fontSize.toFloat(),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "№ ${thread.num}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
