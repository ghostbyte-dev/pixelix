package com.daniebeler.pfpixelix.widget.notifications

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.daniebeler.pfpixelix.widget.notifications.work_manager.NotificationsWorkManager

class NotificationWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NotificationsWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        NotificationsWorkManager(context).executePeriodic()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        NotificationsWorkManager(context).cancelWork()
    }
}