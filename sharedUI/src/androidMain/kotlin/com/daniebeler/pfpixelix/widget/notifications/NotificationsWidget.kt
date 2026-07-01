package com.daniebeler.pfpixelix.widget.notifications

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.widget.WidgetColors
import com.daniebeler.pfpixelix.widget.latest_image.utils.GetImageProvider
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationStoreItem
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationsStore
import com.daniebeler.pfpixelix.widget.notifications.work_manager.NotificationsWorkManager
import androidx.core.net.toUri
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import com.daniebeler.pfpixelix.AppActivity
import com.daniebeler.pfpixelix.domain.model.NotificationType

//private val destinationKey = ActionParameters.Key<String>(
//    MainActivity.KEY_DESTINATION
//)
//
//private val destinationKeyParam = ActionParameters.Key<String>(
//    MainActivity.KEY_DESTINATION_PARAM
//)

class NotificationsWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE, HORIZONTAL_RECTANGLE, BIG_SQUARE
        )
    )

    override var stateDefinition: GlanceStateDefinition<*> = CustomNotificationsStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // create your AppWidget here
            Content()
        }
    }

    @Composable
    fun Content() {
        val state = currentState<NotificationsStore>()
        val notifications = state.notifications
        val context = LocalContext.current
        val size = LocalSize.current

        GlanceTheme(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
            else WidgetColors.colors
        ) {
            Box(
                GlanceModifier.background(GlanceTheme.colors.background).fillMaxSize()
                    .padding(12.dp), contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = GlanceModifier
//                                    .clickable(
//                                    actionStartActivity<MainActivity>(
//                                        actionParametersOf(
//                                            destinationKey to MainActivity.Companion.StartNavigation.Notifications.toString(),
//                                        )
//                                    )
//                                )
                                , verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (size.height >= BIG_SQUARE.height && size.width >= BIG_SQUARE.width) {
                                    Image(
                                        provider = ImageProvider(R.mipmap.ic_launcher_02_round),
                                        contentDescription = null,
                                        modifier = GlanceModifier.width(50.dp).height(50.dp)
                                            .cornerRadius(50.dp)
                                    )
                                    Spacer(GlanceModifier.width(6.dp))
                                    Text(
                                        text = LocalContext.current.getString(R.string.notifications),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onBackground,
                                            fontSize = 20.sp
                                        )
                                    )

                                } else if (size.height <= BIG_SQUARE.height && size.width <= BIG_SQUARE.width) {
                                    Text(
                                        text = LocalContext.current.getString(R.string.notifications),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onBackground,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                            Spacer(GlanceModifier.defaultWeight())
                            Box(
                                modifier = GlanceModifier.clickable(
                                    onClick = actionRunCallback<RefreshActionNotificationsWidget>()
                                )
                            ) {
                                Image(
                                    provider = ImageProvider(R.drawable.refresh_icon),
                                    contentDescription = "refresh"
                                )
                            }
                        }
                        if (size.height >= BIG_SQUARE.height && size.width >= BIG_SQUARE.width) {
                            Spacer(GlanceModifier.height(12.dp))
                        }
                    }
                    if (state.error.isNotBlank()) {
                        item { Spacer(GlanceModifier.height(12.dp)) }
                        item {
                            Text(
                                text = state.error,
                                style = TextStyle(color = GlanceTheme.colors.error)
                            )
                        }
                    } else if (notifications.isEmpty() || state.refreshing) {
                        item {
                            Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = GlanceTheme.colors.primary)
                            }
                        }
                    } else {
                        items(notifications) {
                            NotificationItem(notification = it, context = context)
                        }
                    }
                }

            }
        }
    }

    @Composable
    private fun NotificationItem(notification: NotificationStoreItem, context: Context) {
        val size = LocalSize.current
        val destinationKey = ActionParameters.Key<String>("navigation_destination")
        val destinationKeyParam = ActionParameters.Key<String>("account_id")
        val usernameKeyParam = ActionParameters.Key<String>("username")
        Box(
            modifier = GlanceModifier.clickable(
                actionStartActivity<AppActivity>(
                    actionParametersOf(
                        destinationKey to "profile",
                        destinationKeyParam to notification.accountId,
                        usernameKeyParam to notification.accountUsername
                    )
                )
            )
        ) {
            Column {
                Spacer(GlanceModifier.height(12.dp))
                Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                    Image(
                        provider = if (notification.accountAvatarBitmap != null) {
                            ImageProvider(notification.accountAvatarBitmap)
                        } else {
                            ImageProvider(R.drawable.default_avatar)
                        },
                        contentDescription = "",
                        modifier = GlanceModifier.height(34.dp).width(34.dp).cornerRadius(34.dp)
                    )

                    Spacer(GlanceModifier.width(6.dp))
                    Column {
                        Row {
                            if (size.width >= BIG_SQUARE.width) {
                                Text(
                                    text = notification.accountUsername, style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = GlanceTheme.colors.onBackground
                                    )
                                )
                                Spacer(GlanceModifier.width(3.dp))
                            }
                            Text(
                                text = LocalContext.current.getString(
                                    getNotificationText(notification.type)
                                ), style = TextStyle(
                                    color = GlanceTheme.colors.onBackground,
                                    fontSize = if (size.width >= BIG_SQUARE.width) {
                                        14.sp
                                    } else {
                                        13.sp
                                    }
                                )
                            )
                        }
                        Text(
                            text = notification.timeAgo,
                            style = TextStyle(color = GlanceTheme.colors.primary, fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }

    private fun getNotificationText(type: NotificationType): Int {
        return when (type) {
            NotificationType.FAVOURITE  -> R.string.liked_your_post
            NotificationType.MENTION -> R.string.mentioned_you
            NotificationType.FOLLOW -> R.string.followed_you
            NotificationType.DIRECT_MESSAGE -> R.string.sent_a_dm
            NotificationType.REBLOG -> R.string.reblogged_your_post
            NotificationType.FOLLOW_REQUEST -> R.string.follow_request
            NotificationType.NEW_COMMENT -> R.string.new_comment
            NotificationType.STATUS -> R.string.new_status
            NotificationType.UPDATE -> R.string.boosted_status_updated
            NotificationType.UNDEFINED -> R.string.notification
        }
    }
}

class RefreshActionNotificationsWidget : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        updateNotificationsWidgetRefreshing(context)
        NotificationsWorkManager(context).executeOnce()
    }
}