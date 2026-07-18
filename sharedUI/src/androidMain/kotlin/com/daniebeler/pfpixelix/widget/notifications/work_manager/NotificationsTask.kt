package com.daniebeler.pfpixelix.widget.notifications.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil3.Bitmap
import coil3.imageLoader
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.utils.io
import com.daniebeler.pfpixelix.utils.timeAgo
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationStoreItem
import com.daniebeler.pfpixelix.widget.notifications.updateNotificationsWidget
import com.daniebeler.pfpixelix.widget.notifications.updateNotificationsWidgetRefreshing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last

class NotificationsTask(
    context: Context,
    workerParams: WorkerParameters,
    private val appComponent: AppComponent
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val context = appComponent.context
        val authService = appComponent.authService
        val widgetService = appComponent.widgetService

        try {
            updateNotificationsWidgetRefreshing(context)
            authService.openSessionIfExist()
            if (authService.activeUser.firstOrNull() == null) {
                updateNotificationsWidget(
                    emptyList(),
                    context,
                    "you have to be logged in to an account"
                )
                return Result.failure()
            }
            val res = widgetService.getNotifications().last()
            if (res is Resource.Success) {
                val notifications = res.data.data.take(10)
                val notificationStoreItems = notifications.map { notification ->

                    val bitmap = notification.account.avatar?.let { getBitmap(context, notification.account.avatar)}
                    NotificationStoreItem(
                        id = notification.id,
                        accountAvatarUrl = notification.account.avatar ?: "",
                        accountAvatarBitmap = bitmap,
                        accountId = notification.account.id,
                        accountUsername = notification.account.displayname ?: notification.account.username,
                        timeAgo = timeAgo(notification.createdAt),
                        type = notification.type,
                    )
                }
                updateNotificationsWidget(notificationStoreItems, context)
            } else {
                throw Exception()
            }
        } catch (e: Throwable) {
            if (runAttemptCount < 4) {
                updateNotificationsWidget(
                    emptyList(),
                    context,
                    "an error occurred, retrying in ${NotificationWorkManagerRetrySeonds * (runAttemptCount + 1)} seconds"
                )
                return Result.retry()
            }
            updateNotificationsWidget(emptyList(), context, "an unexpected error occurred")
            return Result.failure()
        }
        return Result.success()
    }


    private suspend fun getBitmap(context: Context, url: String): Bitmap? {
        val headers = NetworkHeaders.Builder()
            .add(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            )
            .add("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
            .build()


        val request = ImageRequest.Builder(context).httpHeaders(headers).data(url)
            .interceptorCoroutineContext(
                Dispatchers.io
            ).build()

        val result = context.imageLoader.execute(request)

        val bitmap: Bitmap? = if (result is SuccessResult) {
            result.image.toBitmap()
        } else {
            null
        }
        return bitmap;
    }
}