package com.daniebeler.pfpixelix.ui.composables

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

@Composable
fun CustomLoader(sizeFactor: Float = 1f) {
    // Create animatables for the alpha values of the squares
    val alphas = List(5) { remember { Animatable(1f) } }

    // Launch the animation in a loop
    LaunchedEffect(Unit) {
        alphas.forEachIndexed { index, animatable ->
            // Start each square's animation with a slight delay
            val delay = index * 133L
            launch {
                delay(delay)
                while (true) {
                    animatable.animateTo(
                        targetValue = 0.3f, animationSpec = tween(
                            durationMillis = 400, easing = FastOutSlowInEasing
                        )
                    )
                    animatable.animateTo(
                        targetValue = 1f, animationSpec = tween(
                            durationMillis = 400, easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), modifier = Modifier
            .width((36 * sizeFactor).dp)
            .height((48 * sizeFactor).dp)
    ) {

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[0].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[1].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(Color.Transparent)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[1].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(Color.Transparent)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[3].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(Color.Transparent)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[3].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[4].value))
            )
        }

        item {
            Box(
                modifier = Modifier
                    .size((12 * sizeFactor).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alphas[3].value))
            )
        }
    }

}