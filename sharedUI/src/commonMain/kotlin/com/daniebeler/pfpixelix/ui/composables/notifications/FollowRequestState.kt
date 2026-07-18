package com.daniebeler.pfpixelix.ui.composables.notifications

import com.daniebeler.pfpixelix.domain.model.Relationship

data class FollowRequestState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val relationship: Relationship? = null,
    val isAccepting: Boolean = true
)