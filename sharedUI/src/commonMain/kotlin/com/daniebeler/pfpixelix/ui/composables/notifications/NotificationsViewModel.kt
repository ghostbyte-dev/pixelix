package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.widget.WidgetService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class NotificationsViewModel @Inject constructor(
    private val widgetService: WidgetService,
    private val platform: Platform
) : ViewModel() {

    var notificationsState by mutableStateOf(NotificationsState())
    var filter by mutableStateOf(NotificationsFilterEnum.All)

    init {
        getNotificationsFirstLoad(false)
    }

    private fun getNotificationsFirstLoad(refreshing: Boolean) {
        widgetService.getNotifications().onEach { result ->
            notificationsState = when (result) {
                is Resource.Success -> {
                    val endReached = (result.data?.size ?: 0) == 0
                    NotificationsState(notifications = result.data ?: emptyList(), endReached = endReached)
                }

                is Resource.Error -> {
                    NotificationsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    NotificationsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        notifications = notificationsState.notifications
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNotificationsPaginated() {
        if (notificationsState.notifications.isNotEmpty() && !notificationsState.isLoading && !notificationsState.endReached) {
            widgetService.getNotifications(notificationsState.notifications.last().id).onEach { result ->
                notificationsState = when (result) {
                    is Resource.Success -> {
                        val endReached = result.data?.size == 0
                        NotificationsState(
                            notifications = notificationsState.notifications + (result.data
                                ?: emptyList()),
                            endReached = endReached
                        )
                    }

                    is Resource.Error -> {
                        NotificationsState(error = result.message ?: "An unexpected error occurred")
                    }

                    is Resource.Loading -> {
                        NotificationsState(
                            isLoading = true,
                            isRefreshing = false,
                            notifications = notificationsState.notifications
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }

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