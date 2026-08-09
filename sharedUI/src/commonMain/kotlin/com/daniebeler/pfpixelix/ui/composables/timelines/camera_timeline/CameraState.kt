package com.daniebeler.pfpixelix.ui.composables.timelines.camera_timeline

import com.daniebeler.pfpixelix.domain.model.Tag

data class CameraState(
    val isLoading: Boolean = false,
    val camera: String,
    val error: String = ""
)
