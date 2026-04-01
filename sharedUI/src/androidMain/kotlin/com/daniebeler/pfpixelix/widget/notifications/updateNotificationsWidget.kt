package com.daniebeler.pfpixelix.widget.notifications

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationStoreItem
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationsStore

suspend fun updateNotificationsWidget(notifications: List<NotificationStoreItem>, context: Context, error: String = "") {
    val notificationsStore = NotificationsStore(
        notifications = notifications,
        error = error
    )
    updateNotificationWidget(context, notificationsStore)
}

suspend fun updateNotificationsWidgetRefreshing(context: Context) {
    updateNotificationWidget(context, NotificationsStore(refreshing = true))
}

private suspend fun updateNotificationWidget(context: Context, notificationsStore: NotificationsStore) {
    GlanceAppWidgetManager(context).getGlanceIds(NotificationsWidget::class.java)
        .forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = CustomNotificationsStateDefinition,
                glanceId = glanceId
            ) {
                notificationsStore
            }
        }
    NotificationsWidget().updateAll(context)
}