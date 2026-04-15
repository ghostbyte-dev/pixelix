package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Grid pattern: index into alphas list, or -1 for transparent
private val gridPattern = listOf(0, 1, -1, 1, -1, 3, -1, 3, 4, 3)

private const val ANIMATION_STAGGER_DELAY = 133L
private const val ANIMATION_DURATION_MS = 400

@Composable
fun CustomLoader(sizeFactor: Float = 1f) {
    val alphas = List(5) { remember { Animatable(1f) } }
    val color = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        alphas.forEachIndexed { index, animatable ->
            launch {
                delay(index * ANIMATION_STAGGER_DELAY)
                while (true) {
                    animatable.animateTo(
                        targetValue = 0.3f,
                        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS, easing = FastOutSlowInEasing)
                    )
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val cellSize = (12 * sizeFactor).dp

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.width((36 * sizeFactor).dp).height((48 * sizeFactor).dp)
    ) {
        items(gridPattern.size) { index ->
            val alphaIndex = gridPattern[index]
            Box(
                modifier = Modifier.size(cellSize).background(
                    if (alphaIndex >= 0) color.copy(alpha = alphas[alphaIndex].value)
                    else Color.Transparent
                )
            )
        }
    }
}
