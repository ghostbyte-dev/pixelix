package com.daniebeler.pfpixelix.ui.composables.states

import androidx.compose.ui.graphics.vector.ImageVector

data class EmptyState(
    val icon: ImageVector? = null,
    val heading: String = "",
    val message: String = "",
    val buttonText: String = "",
    val onClick: () -> Unit = { }
)
