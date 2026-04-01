package com.daniebeler.pfpixelix.widget.latest_image

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import coil3.Bitmap
import com.daniebeler.pfpixelix.widget.notifications.models.LatestImageStore

suspend fun updateLatestImageWidget(bitmap: Bitmap?, postId: String, context: Context, error: String = "") {
    val latestImageStore = LatestImageStore(
        latestImageBitmap = bitmap,
        postId = postId,
        error = error
    )
    updateLatestImageWidget(latestImageStore, context)
}
suspend fun updateLatestImageWidgetRefreshing(context: Context) {
    val latestImageStore = LatestImageStore(
        refreshing = true,
    )
    updateLatestImageWidget(latestImageStore, context)
}

private suspend fun updateLatestImageWidget(latestImageStore: LatestImageStore, context: Context) {
    GlanceAppWidgetManager(context).getGlanceIds(LatestImageWidget::class.java)
        .forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = CustomLatestImageStateDefinition,
                glanceId = glanceId
            ) {
                latestImageStore
            }
        }
    LatestImageWidget().updateAll(context)
}
