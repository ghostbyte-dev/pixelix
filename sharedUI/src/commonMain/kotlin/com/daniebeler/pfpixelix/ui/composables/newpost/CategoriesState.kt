package com.daniebeler.pfpixelix.ui.composables.newpost

import com.daniebeler.pfpixelix.domain.model.Category

data class CategoriesState(
    var isLoading: Boolean = false,
    var error: String = "",
    var selectedCategory: Category? = null,
    var categories: List<Category> = emptyList()
)
