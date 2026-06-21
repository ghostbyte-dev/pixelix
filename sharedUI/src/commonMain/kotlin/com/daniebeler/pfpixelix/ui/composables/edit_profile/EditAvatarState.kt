package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.ui.graphics.ImageBitmap

data class EditAvatarState(
    val newAvatar: ImageBitmap? = null,
    val newUploadedAvatar: ImageBitmap? = null,
    val isLoading: Boolean = false,
    val error: String = "",
)
