package com.daniebeler.pfpixelix.ui.composables.explore.trending.categories

import com.daniebeler.pfpixelix.domain.model.Category

data class CategoriesState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String = "",
)
