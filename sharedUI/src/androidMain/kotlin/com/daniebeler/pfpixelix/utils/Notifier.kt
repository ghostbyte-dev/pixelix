package com.daniebeler.pfpixelix.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.AppActivity
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.domain.model.PushNotification
import java.util.concurrent.ThreadLocalRandom

class Notifier(var context: Context) {
    private val channelId = context.packageName
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    fun showNotification(
        notification: PushNotification
    ) {
        Logger.d(tag="PushNotification") {
            "show Notification" +
                    "$notification"
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Logger.w(tag="PushNotification") { "Notifications disabled at OS level — not shown" }
            return
        }
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "pixelix://notifications".toUri(),
            context,
            AppActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder =
            Notification.Builder(context, channelId)

        val notification =
            notificationBuilder
                .setTicker(notification.body)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setSmallIcon(R.drawable.ic_launcher_02_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setOnlyAlertOnce(true)
                .setStyle(Notification.BigTextStyle().bigText(notification.body))
                .build()

        val notificationId =
            ThreadLocalRandom.current().nextInt()
        nm.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        val name = context.packageName
        val descriptionText = "Test notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
        nm.createNotificationChannel(channel)
    }
}
