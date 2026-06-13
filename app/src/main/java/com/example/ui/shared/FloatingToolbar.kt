package com.example.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.res.stringResource
import com.example.R

@Composable
fun FloatingToolbar(
    modifier: Modifier = Modifier,
    title: String,
    onBoardsClick: (() -> Unit)? = null,
    onHistoryClick: (() -> Unit)? = null,
    onDraftsClick: (() -> Unit)? = null,
    onFavoritesClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null,
    customDropdownItems: @Composable ((closeMenu: () -> Unit) -> Unit)? = null,
    additionalActions: @Composable (() -> Unit)? = null
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    
    val gradient = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.background.copy(alpha = 0.95f), MaterialTheme.colorScheme.background.copy(alpha = 0.7f), Color.Transparent)
    )

    Row(
        modifier = modifier
            .background(gradient)
            .fillMaxWidth()
            .padding(top = statusBarPadding + 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            FloatingButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)
        } else if (onBoardsClick != null) {
            FloatingButton(icon = Icons.Default.FlashOn, onClick = onBoardsClick) 
        }

        var expanded by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { expanded = true }
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (onBoardsClick != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.boards_title)) },
                            onClick = { onBoardsClick(); expanded = false }
                        )
                    }
                    if (onHistoryClick != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.history_title)) },
                            onClick = { onHistoryClick(); expanded = false }
                        )
                    }
                    if (onFavoritesClick != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.favorites_title)) },
                            onClick = { onFavoritesClick(); expanded = false }
                        )
                    }
                    if (onDraftsClick != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.drafts_title)) },
                            onClick = { onDraftsClick(); expanded = false }
                        )
                    }
                    customDropdownItems?.invoke { expanded = false }
                }
            }
        }
        
        if (onInfoClick != null) {
            FloatingButton(icon = Icons.Default.Info, onClick = onInfoClick)
        }
        if (onSettingsClick != null) {
            FloatingButton(icon = Icons.Default.Settings, onClick = onSettingsClick)
        }
        
        additionalActions?.invoke()
    }
}

@Composable
fun FloatingButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
