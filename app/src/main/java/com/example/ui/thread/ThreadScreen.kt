package com.example.ui.thread

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ui.shared.AnimatedAppearance
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.ui.thread.composition.EmojiCaptchaDialog
import com.example.ui.thread.composition.PostCard
import com.example.ui.thread.composition.PostingBar
import com.example.ui.thread.model.CaptchaState
import com.example.ui.shared.FloatingToolbar
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.ui.res.stringResource
import com.example.R

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.example.ui.shared.FloatingButton

import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon

@Composable
fun ThreadScreen(
    board: String,
    threadNum: Int,
    onBackClick: () -> Unit,
    onBoardsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToGallery: (List<String>, Int) -> Unit,
    onNavigateToBoard: (String) -> Unit,
    viewModel: ThreadViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(board, threadNum) {
        viewModel.load(board, threadNum)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveDraftOnExit()
        }
    }

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = state.threadTitle.ifBlank { stringResource(R.string.thread_title, threadNum) },
                onBackClick = onBackClick,
                customDropdownItems = { closeMenu ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Доска /$board/") },
                        onClick = {
                            closeMenu()
                            onNavigateToBoard(board)
                        }
                    )
                },
                additionalActions = {
                     FloatingButton(
                         icon = if (state.isFavorite) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                         onClick = { viewModel.toggleFavorite() }
                     )
                }
            )
        },
        bottomBar = {
            PostingBar(
                draftText = state.draftText,
                onTextChange = viewModel::updateDraftText,
                onSendClick = { viewModel.startPosting() }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = stringResource(R.string.error_prefix, state.error ?: ""),
                    modifier = Modifier.align(Alignment.Center).padding(16.dp).padding(top = padding.calculateTopPadding()),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp, 
                        end = 16.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    )
                ) {
                    itemsIndexed(state.posts) { index, post ->
                        val postIndexInThread = index + 1
                        if (postIndexInThread == state.bumpLimit + 1) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = androidx.compose.ui.graphics.Color.Red
                                )
                                Text(
                                    text = "Бамплимит",
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = androidx.compose.ui.graphics.Color.Red
                                )
                            }
                        }
                        AnimatedAppearance(index = index) {
                            PostCard(
                                post = post,
                                board = board,
                                onNavigateToGallery = onNavigateToGallery,
                                postIndexInThread = postIndexInThread
                            )
                        }
                    }
                    if (state.posts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AnimatedAppearance(index = state.posts.size) {
                                Button(
                                    onClick = { /* Show thread info */ },
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Icon(androidx.compose.material.icons.Icons.Default.Info, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Информация о треде")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.captchaState is CaptchaState.Showing) {
        val captcha = state.captchaState as CaptchaState.Showing
        EmojiCaptchaDialog(
            image = captcha.image,
            keyboard = captcha.keyboard,
            onEmojiClick = { index ->
                viewModel.clickCaptcha(captcha.id, index)
            },
            onDismiss = { viewModel.hideCaptcha() }
        )
    } else if (state.captchaState is CaptchaState.Loading) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { },
            title = { Text(stringResource(R.string.captcha_loading)) },
            text = { CircularProgressIndicator() }
        )
    }
}
