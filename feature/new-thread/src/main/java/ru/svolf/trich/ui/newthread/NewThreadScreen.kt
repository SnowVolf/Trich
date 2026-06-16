package ru.svolf.trich.ui.newthread

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.svolf.trich.uikit.components.EmojiCaptchaDialog
import ru.svolf.trich.uikit.components.FloatingToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewThreadScreen(
    board: String,
    onBackClick: () -> Unit,
    viewModel: NewThreadViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.onImagesAttached(uris)
    }

    LaunchedEffect(board) {
        viewModel.init(board)
    }

    LaunchedEffect(state.postSuccess) {
        if (state.postSuccess) {
            onBackClick() // Возвращаемся в список тредов при успехе
        }
    }

    Scaffold(
        topBar = {
            FloatingToolbar(
                title = "Создать тред: /$board/",
                onBackClick = onBackClick,
                onBoardsClick = { },
                onHistoryClick = { },
                onDraftsClick = { },
                onFavoritesClick = { },
                onSettingsClick = { }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.subject,
                    onValueChange = viewModel::onSubjectChanged,
                    label = { Text("Заголовок треда") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.comment,
                    onValueChange = viewModel::onCommentChanged,
                    label = { Text("Содержание треда") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Прикрепить изображения")
                }

                if (state.attachedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.attachedImages) { uri ->
                            Box(contentAlignment = Alignment.TopEnd) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.removeImage(uri) },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(4.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.postingError != null) {
                    Text(
                        text = stringResource(R.string.send_error, state.postingError!!),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = viewModel::sendPostPressed,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isPosting && state.comment.isNotBlank()
                ) {
                    if (state.isPosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Создать тред")
                    }
                }
            } // Close Column tag

            if (state.isPosting || state.captchaLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } // Close Box tag
    } // Close Scaffold tag

    if (state.showCaptcha) {
        EmojiCaptchaDialog(
            captchaImage = state.captchaImage,
            keyboard = state.captchaKeyboard,
            isLoading = state.captchaLoading,
            onEmojiSelected = viewModel::onCaptchaEmojiSelected,
            onDismiss = viewModel::dismissCaptcha
        )
    }
}
