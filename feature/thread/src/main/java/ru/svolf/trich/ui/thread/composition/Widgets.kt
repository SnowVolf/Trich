package ru.svolf.trich.ui.thread.composition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.svolf.trich.domain.model.Post
import ru.svolf.trich.uikit.R
import ru.svolf.trich.uikit.components.AttachmentImage
import ru.svolf.trich.uikit.components.HtmlText
import ru.svolf.trich.uikit.components.VideoPlayer

@Composable
fun PostCard(
    post: Post,
    board: String,
    onNavigateToGallery: (List<String>, Int) -> Unit,
    postIndexInThread: Int = -1,
    fontSize: Int = 14,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val onLinkClick: (String) -> Unit = { url ->
        val parsed = if (url.startsWith("/")) "https://2ch.su$url" else url
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            android.net.Uri.parse(parsed)
        )
        context.startActivity(intent)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Color(0x0AFFFFFF)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (postIndexInThread > 0) {
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
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
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
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
                    textSize = fontSize.toFloat(),
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
                                val modifier = Modifier
                                    .weight(1f)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))

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
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse(fileUrl)
                                            )
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
                textSize = fontSize.toFloat(),
                isSelectable = true,
                quoteBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                quoteTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onLinkClick = onLinkClick
            )
        }
    }
}
