package com.daniebeler.pfpixelix.widget.notifications.work_manager

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.FileProvider.getUriForFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil3.imageLoader
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.bitmapConfig
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.widget.latest_image.updateLatestImageWidget
import com.daniebeler.pfpixelix.widget.latest_image.updateLatestImageWidgetRefreshing
import com.daniebeler.pfpixelix.widget.notifications.updateNotificationsWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last

class LatestImageTask(
    context: Context,
    workerParams: WorkerParameters,
    private val appComponent: AppComponent
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val context = appComponent.context
        val authService = appComponent.authService
        val widgetService = appComponent.widgetService

        try {
            updateLatestImageWidgetRefreshing(context)
            authService.openSessionIfExist()
            if (authService.activeUser.firstOrNull() == null) {
                updateNotificationsWidget(
                    emptyList(),
                    context,
                    "you have to be logged in to an account"
                )
                return Result.failure()
            }
            val res = widgetService.getLatestImage().last()
            if (res is Resource.Success && res.data.mediaAttachments.first().previewUrl != null) {

                val bitmap = getBitmap(context, res.data.mediaAttachments.first().previewUrl!!)
                updateLatestImageWidget(bitmap, res.data.id, context)
            } else {
                throw Exception()
            }
        } catch (e: Throwable) {
            if (runAttemptCount < 4) {
                updateLatestImageWidget(
                    null,
                    "",
                    context,
                    "an error occurred, retrying in ${NotificationWorkManagerRetrySeonds * (runAttemptCount + 1)} seconds"
                )
                return Result.retry()
            }
            updateLatestImageWidget(null, "", context, "an unexpected error occurred")
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
                Dispatchers.IO
            ).transformations(RoundedCornersTransformation(82f)) // Built-in Coil rounding
            .bitmapConfig(Bitmap.Config.ARGB_8888).build() // Forces software bitmap.build()

        val result = context.imageLoader.execute(request)

        val bitmap: Bitmap? = if (result is SuccessResult) {
            result.image.toBitmap()
        } else {
            null
        }
        return bitmap;
    }
}