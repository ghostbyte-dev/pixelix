package com.daniebeler.pfpixelix.widget.latest_image

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.daniebeler.pfpixelix.widget.notifications.work_manager.LatestImageWorkManager

class LatestImageWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LatestImageWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        LatestImageWorkManager(context).executePeriodic()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        LatestImageWorkManager(context).cancelWork()
    }
}