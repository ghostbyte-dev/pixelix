package com.daniebeler.pfpixelix.ui.composables.mention

import com.daniebeler.pfpixelix.domain.model.PostContext

data class PostContextState (
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val postContext: PostContext? = null,
    val error: String = ""
)