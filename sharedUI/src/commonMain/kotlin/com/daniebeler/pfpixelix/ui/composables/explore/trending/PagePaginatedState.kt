package com.daniebeler.pfpixelix.ui.composables.explore.trending

data class PagePaginatedState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String = ""
)