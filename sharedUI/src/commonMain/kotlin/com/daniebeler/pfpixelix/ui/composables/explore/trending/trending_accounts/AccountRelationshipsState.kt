package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import com.daniebeler.pfpixelix.domain.model.Relationship

data class AccountRelationshipsState(
    val isLoading: Boolean = false,
    val accountRelationships: List<Relationship> = emptyList(),
    val error: String = ""
)
