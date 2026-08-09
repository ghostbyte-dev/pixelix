package com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses

import com.daniebeler.pfpixelix.domain.model.Lens

data class LensesState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val lenses: List<Lens> = emptyList(),
    val error: String = "",
    val page: Int = 1,
    val endReached: Boolean = false
)
