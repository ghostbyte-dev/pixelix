package com.daniebeler.pfpixelix.ui.composables.states

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomLoader

@Composable
fun LoadingComposable(isLoading: Boolean) {
    if (isLoading) {
        LoadingComposable()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingComposable(modifier: Modifier = Modifier.fillMaxSize(), size: Dp = 80.dp, color: Color = LoadingIndicatorDefaults.indicatorColor) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        LoadingIndicator(Modifier.size(size), color = color)
    }
}
