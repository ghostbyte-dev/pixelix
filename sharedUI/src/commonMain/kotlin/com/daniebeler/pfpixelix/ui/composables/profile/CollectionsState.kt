package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.Collection


data class CollectionsState(
    val isLoading: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val endReached: Boolean = false,
    val error: String = ""
)
