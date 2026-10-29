package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.runtime.Composable

@Composable
expect fun MapComposable(
    lat: Double,
    lon: Double
)