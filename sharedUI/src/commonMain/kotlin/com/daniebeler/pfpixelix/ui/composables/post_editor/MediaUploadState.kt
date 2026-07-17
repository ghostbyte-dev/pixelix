package com.daniebeler.pfpixelix.ui.composables.post_editor

import com.daniebeler.pfpixelix.domain.model.MediaAttachment

data class MediaUploadState(
    val isLoading: Boolean = false,
    val mediaAttachments: List<MediaAttachment> = emptyList(),
    val error: String = ""
)
