package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.Relationship

data class RelationshipState(
    val isLoading: Boolean = false,
    val accountRelationship: Relationship? = null,
    val error: String = ""
)
