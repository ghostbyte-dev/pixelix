package com.daniebeler.pfpixelix.widget.latest_image

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.daniebeler.pfpixelix.AppActivity
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.widget.WidgetColors
import com.daniebeler.pfpixelix.widget.latest_image.utils.GetImageProvider
import com.daniebeler.pfpixelix.widget.notifications.models.LatestImageStore
import com.daniebeler.pfpixelix.widget.notifications.work_manager.LatestImageWorkManager


class LatestImageWidget : GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = CustomLatestImageStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        val state = currentState<LatestImageStore>()
        val context = LocalContext.current

        GlanceTheme(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
            else WidgetColors.colors
        ) {
            if (state.latestImageBitmap != null) {
                Image(
                    provider = ImageProvider(state.latestImageBitmap),
                    contentDescription = "latest home timeline picture",
                    modifier = GlanceModifier.fillMaxSize()
                        .clickable(actionStartActivity<AppActivity>())
                )
            } else {
                Column(
                    GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background)
                        .padding(12.dp)
                ) {
                    Row(GlanceModifier.fillMaxWidth()) {
                        Text(
                            text = "Latest Image Widget",
                            style = TextStyle(color = GlanceTheme.colors.onBackground)
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        Box(
                            modifier = GlanceModifier.clickable(
                                onClick = actionRunCallback<RefreshActionLatestImageWidget>()
                            )
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.refresh_icon),
                                contentDescription = "refresh"
                            )
                        }
                    }
                    Spacer(GlanceModifier.height(12.dp))
                    if (state.error.isNotBlank()) {
                        Text(
                            text = state.error, style = TextStyle(color = GlanceTheme.colors.error)
                        )
                    } else if (state.refreshing) {
                        CircularProgressIndicator(color = GlanceTheme.colors.primary)
                    }
                }
            }
        }
    }
}

class RefreshActionLatestImageWidget : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        updateLatestImageWidgetRefreshing(context)
        LatestImageWorkManager(context).executeOnce()
    }
}