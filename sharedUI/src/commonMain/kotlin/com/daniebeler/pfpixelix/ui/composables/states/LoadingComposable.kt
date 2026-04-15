package com.daniebeler.pfpixelix.ui.composables.states

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomLoader

@Composable
fun LoadingComposable(isLoading: Boolean) {
    if (isLoading) {
        LoadingComposable()
    }
}

@Composable
fun LoadingComposable(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CustomLoader()
    }
}
