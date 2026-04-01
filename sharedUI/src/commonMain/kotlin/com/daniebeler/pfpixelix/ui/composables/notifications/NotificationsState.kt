package com.daniebeler.pfpixelix.ui.composables.notifications

import com.daniebeler.pfpixelix.domain.model.Notification

data class NotificationsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val error: String = ""
)