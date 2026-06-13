package com.example.ui.thread.composition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import com.example.domain.model.Post
import com.example.ui.shared.HtmlText
import com.example.ui.shared.VideoPlayer

import androidx.compose.ui.res.stringResource
import com.example.R

@Composable
fun EmojiCaptchaDialog(
    image: String,
    keyboard: List<String>,
    onEmojiClick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.captcha_solve)) },
        text = {
            Column {
                Text(stringResource(R.string.captcha_find_emoji))
                Spacer(modifier = Modifier.height(8.dp))
                // image is base64, we need to decode and show it. Since Coil can't natively load raw base64 string without data:image/png;base64,
                // we format it.
                AttachmentImage(
                    url = "data:image/png;base64,\$image",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    itemsIndexed(keyboard) { index, keyImage ->
                        AttachmentImage(
                            url = "data:image/png;base64,\$keyImage",
                            onClick = { onEmojiClick(index) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PostCard(post: Post, board: String, onNavigateToGallery: (List<String>, Int) -> Unit, postIndexInThread: Int = -1) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val onLinkClick: (String) -> Unit = { url ->
        val parsed = if (url.startsWith("/")) "https://2ch.su$url" else url
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(parsed))
        context.startActivity(intent)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0x0AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (postIndexInThread > 0) {
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "$postIndexInThread",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "#${post.num}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (post.subject.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HtmlText(
                    html = "<b>${post.subject}</b>",
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onLinkClick = onLinkClick
                )
            }
            
            if (post.files.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val imageUrls = post.files.map { it.path }
                
                // Max 4 items on imageboard usually, let's use a 2 columns structure
                val chunkedFiles = post.files.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (rowFiles in chunkedFiles) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (file in rowFiles) {
                                val fileUrl = file.path
                                val index = post.files.indexOf(file)
                                val modifier = Modifier.weight(1f).clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                
                                if (file.type == 1 || file.type == 2 || file.type == 3 || file.type == 4) {
                                    AttachmentImage(
                                        url = fileUrl, 
                                        onClick = { onNavigateToGallery(imageUrls, index) },
                                        modifier = modifier
                                    )
                                } else if (file.type == 6 || file.type == 10) { // webm, mp4
                                    VideoPlayer(
                                        videoUrl = fileUrl,
                                        modifier = modifier
                                    )
                                } else {
                                    Text(
                                        stringResource(R.string.attachment_download, file.fullname), 
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = modifier.clickable {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fileUrl))
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                            if (rowFiles.size == 1 && chunkedFiles.size > 1) { // Balance columns if needed
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            HtmlText(
                html = post.comment,
                textColor = MaterialTheme.colorScheme.onSurface,
                isSelectable = true,
                quoteBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                quoteTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onLinkClick = onLinkClick
            )
        }
    }
}
