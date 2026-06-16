package ru.svolf.trich.ui.history.composition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import  ru.svolf.trich.db.VisitedThread
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

@Composable
fun HistoryItem(thread: VisitedThread, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Color(0x0AFFFFFF)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = thread.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "/${thread.board}/",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Last visited: ${dateFormat.format(Date(thread.lastVisitedDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
