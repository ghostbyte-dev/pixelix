package com.daniebeler.pfpixelix.ui.composables.newpost

import com.daniebeler.pfpixelix.domain.model.MediaAttachment

data class MediaUploadState(
    val isLoading: Boolean = false,
    val mediaAttachments: List<MediaAttachment> = emptyList(),
    val error: String = ""
)
