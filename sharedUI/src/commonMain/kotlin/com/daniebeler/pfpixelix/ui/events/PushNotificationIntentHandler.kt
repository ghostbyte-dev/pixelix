package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class PushNotificationIntentHandler {
    private val _pendingNotifications = Channel<Unit>(Channel.CONFLATED)
    val pendingNotifications = _pendingNotifications.receiveAsFlow()

    fun onNotificationsOpen() {
        _pendingNotifications.trySend(Unit)
    }
}