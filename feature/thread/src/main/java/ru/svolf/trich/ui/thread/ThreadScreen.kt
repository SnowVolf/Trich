package ru.svolf.trich.ui.thread

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.svolf.trich.ui.thread.composition.PostCard
import ru.svolf.trich.ui.thread.composition.PostingBar
import ru.svolf.trich.ui.thread.model.CaptchaState
import ru.svolf.trich.uikit.R
import ru.svolf.trich.uikit.components.AnimatedAppearance
import ru.svolf.trich.uikit.components.EmojiCaptchaDialog
import ru.svolf.trich.uikit.components.FloatingButton
import ru.svolf.trich.uikit.components.FloatingToolbar

@Composable
fun ThreadScreen(
    board: String,
    threadNum: Int,
    scrollToPost: Int? = null,
    onBackClick: () -> Unit,
    onBoardsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToGallery: (List<String>, Int) -> Unit,
    onNavigateToBoard: (String) -> Unit,
    viewModel: ThreadViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    var hasScrolledToTarget by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(
            false
        )
    }

    LaunchedEffect(board, threadNum) {
        viewModel.load(board, threadNum)
    }

    LaunchedEffect(state.posts, scrollToPost) {
        if (!hasScrolledToTarget && scrollToPost != null && state.posts.isNotEmpty()) {
            // Find index of the target post
            val targetIndex = scrollToPost - 1
            if (targetIndex in state.posts.indices) {
                listState.animateScrollToItem(targetIndex)
                hasScrolledToTarget = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveDraftOnExit()
        }
    }

    Scaffold(
        topBar = {
            if (state.isSearchActive) {
                ru.svolf.trich.uikit.components.SearchToolbar(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onCloseClick = { viewModel.toggleSearch() }
                )
            } else {
                FloatingToolbar(
                    title = state.threadTitle.ifBlank {
                        stringResource(
                            R.string.thread_title,
                            threadNum
                        )
                    },
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
                            icon = androidx.compose.material.icons.Icons.Default.Search,
                            onClick = { viewModel.toggleSearch() }
                        )
                        FloatingButton(
                            icon = if (state.isFavorite) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            onClick = { viewModel.toggleFavorite() }
                        )
                    }
                )
            }
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
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .padding(top = padding.calculateTopPadding()),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    )
                ) {
                    val displayPosts = state.filteredPosts
                    itemsIndexed(displayPosts) { displayIndex, post ->
                        val originalIndex = state.posts.indexOf(post)
                        val postIndexInThread = originalIndex + 1
                        if (!state.isSearchActive && postIndexInThread == state.bumpLimit + 1) {
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
                        AnimatedAppearance(index = displayIndex) {
                            PostCard(
                                post = post,
                                board = board,
                                onNavigateToGallery = onNavigateToGallery,
                                postIndexInThread = postIndexInThread,
                                fontSize = state.fontSize
                            )
                        }
                    }
                    if (displayPosts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AnimatedAppearance(index = displayPosts.size) {
                                Button(
                                    onClick = { /* Show thread info */ },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        androidx.compose.material.icons.Icons.Default.Info,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Информация о треде")
                                }
                            }
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = state.hasNewPosts,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it }),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = padding.calculateBottomPadding() + 16.dp)
            ) {
                androidx.compose.material3.ExtendedFloatingActionButton(
                    onClick = { viewModel.loadNewPosts() },
                    icon = {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Refresh,
                            contentDescription = "Новые посты"
                        )
                    },
                    text = { Text("Есть новые посты (${state.newPostsCount})") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    if (state.captchaState is CaptchaState.Showing) {
        val captcha = state.captchaState as CaptchaState.Showing
        EmojiCaptchaDialog(
            captchaImage = captcha.image,
            keyboard = captcha.keyboard,
            isLoading = false,
            onEmojiSelected = { index ->
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
