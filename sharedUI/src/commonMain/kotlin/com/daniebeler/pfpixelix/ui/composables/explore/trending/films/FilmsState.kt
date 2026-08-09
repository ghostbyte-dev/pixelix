package com.daniebeler.pfpixelix.ui.composables.explore.trending.films

import com.daniebeler.pfpixelix.domain.model.Film

data class FilmsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val films: List<Film> = emptyList(),
    val error: String = "",
    val page: Int = 1,
    val endReached: Boolean = false
)
