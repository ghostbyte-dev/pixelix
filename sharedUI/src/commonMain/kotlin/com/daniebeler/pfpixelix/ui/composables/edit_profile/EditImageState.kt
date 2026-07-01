package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.ui.graphics.ImageBitmap

data class EditImageState(
    val newImage: ImageBitmap? = null,
    val newUploadedImage: ImageBitmap? = null,
    val isLoading: Boolean = false,
    val error: String = "",
)
