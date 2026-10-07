package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MaxWidthTopBar(
    maxWidth: Dp = 800.dp,
    hasBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    Surface(
        color = if (hasBackground) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            Box(Modifier.widthIn(max = maxWidth).fillMaxWidth()) {
                content()
            }
        }
    }
}