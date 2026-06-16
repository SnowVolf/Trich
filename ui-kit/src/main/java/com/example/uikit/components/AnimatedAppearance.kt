package ru.svolf.trich.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * A beautiful visual container that provides fluid slide-up and fade-in
 * transition animation for list items and elements upon composition entry.
 */
@Composable
fun AnimatedAppearance(
    modifier: Modifier = Modifier,
    index: Int = 0,
    baseDelay: Int = 40,
    maxDelay: Int = 300,
    durationMillis: Int = 350,
    content: @Composable () -> Unit,
) {
    val delay = remember(index) {
        (index * baseDelay).coerceAtMost(maxDelay)
    }
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delay
            )
        ) + slideInVertically(
            initialOffsetY = { 48 },
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delay
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}
