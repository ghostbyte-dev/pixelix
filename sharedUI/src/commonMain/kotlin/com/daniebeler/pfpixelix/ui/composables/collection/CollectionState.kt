package com.daniebeler.pfpixelix.ui.composables.collection

import com.daniebeler.pfpixelix.domain.model.Collection

data class CollectionState(
    val isLoading: Boolean = false,
    val id: String? = null,
    val collection: Collection? = null,
    val error: String = ""
)