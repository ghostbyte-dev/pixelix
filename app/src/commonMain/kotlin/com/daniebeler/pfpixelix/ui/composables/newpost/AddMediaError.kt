package com.daniebeler.pfpixelix.ui.composables.newpost

import com.daniebeler.pfpixelix.utils.EmptyKmpUri
import com.daniebeler.pfpixelix.utils.KmpUri

data class AddMediaError(
    val type: AddMediaErrorType = AddMediaErrorType.NONE,
    val title: String = "",
    val description: String = "",
    val uri: KmpUri = EmptyKmpUri
)

enum class AddMediaErrorType {
    TOO_BIG_MEDIA,
    ERROR,
    NONE
}