package com.daniebeler.pfpixelix.ui.composables.post_editor

import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.utils.EmptyKmpUri
import com.daniebeler.pfpixelix.utils.KmpUri

data class AddMediaError(
    val type: AddMediaErrorType = AddMediaErrorType.NONE,
    val title: String = "",
    val description: String = "",
    val uri: KmpUri = EmptyKmpUri,
    val metadata: MediaAttachmentMetadataRequest? = null
)

enum class AddMediaErrorType {
    TOO_BIG_MEDIA,
    ERROR,
    NONE
}