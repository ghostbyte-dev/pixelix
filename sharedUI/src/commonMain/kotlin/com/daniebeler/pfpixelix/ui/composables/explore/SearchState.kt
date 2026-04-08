package com.daniebeler.pfpixelix.ui.composables.explore

import com.daniebeler.pfpixelix.domain.model.Search

data class SearchState(
    val isLoading: Boolean = false,
    val searchResult: Search? = null,
    val error: String = ""
)
