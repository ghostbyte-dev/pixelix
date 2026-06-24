package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.general.WidgetService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class NotificationsViewModel @Inject constructor(
    private val notificationService: NotificationService,
    private val platform: Platform
) : ViewModel() {

    var notificationsState by mutableStateOf(NotificationsState())
    var filter by mutableStateOf(NotificationsFilterEnum.All)

    init {
        getNotificationsFirstLoad(false)
    }

    private fun getNotificationsFirstLoad(refreshing: Boolean) {
        notificationService.getNotifications().onEach { result ->
            notificationsState = when (result) {
                is Resource.Success -> {
                    val endReached = (result.data.data.size ?: 0) == 0
                    NotificationsState(notifications = result.data.data, endReached = endReached, nextId = result.data.next)
                }

                is Resource.Error -> {
                    NotificationsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    notificationsState.copy(isLoading = true, isRefreshing = refreshing)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNotificationsPaginated() {
        if (notificationsState.notifications.isNotEmpty() && !notificationsState.isLoading && !notificationsState.endReached && notificationsState.nextId != null) {
            notificationService.getNotifications(notificationsState.nextId).onEach { result ->
                notificationsState = when (result) {
                    is Resource.Success -> {
                        val endReached = result.data.data.isEmpty() || result.data.next == null
                        NotificationsState(
                            notifications = notificationsState.notifications + (result.data.data),
                            endReached = endReached,
                            nextId = result.data.next
                        )
                    }

                    is Resource.Error -> {
                        NotificationsState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        notificationsState.copy(isLoading = true, isRefreshing = false)

                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun removeNotification(notification: Notification) {
        notificationsState = notificationsState.copy(
            notifications = notificationsState.notifications.filter { it.id != notification.id }
        )
    }

    fun changeFilter(selectedFilter: NotificationsFilterEnum) {
        filter = selectedFilter
    }

    fun refresh() {
        getNotificationsFirstLoad(true)
    }

    fun pinWidget() {
        platform.pinWidget()
    }
}