package com.daniebeler.pfpixelix.widget.notifications.work_manager

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

public val NotificationWorkManagerRetrySeonds: Long = 15
class NotificationsWorkManager(private val context: Context) {
    fun executePeriodic() = enqueuePeriodicWorker()
    fun executeOnce() = startWorkerOnce()

    private fun startWorkerOnce() {
        val uploadWorkerRequest: WorkRequest =
            OneTimeWorkRequestBuilder<NotificationsTask>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
        WorkManager.getInstance(context).enqueue(uploadWorkerRequest)
    }

    private fun enqueuePeriodicWorker() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notifications_widget_task",
            ExistingPeriodicWorkPolicy.KEEP, buildRequest()
        )
    }

    private fun buildRequest(): PeriodicWorkRequest {
        // 1 day
        return PeriodicWorkRequestBuilder<NotificationsTask>(
            15, TimeUnit.MINUTES
        ).addTag("notifications_widget_task").setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL, NotificationWorkManagerRetrySeonds, TimeUnit.SECONDS
        ).build()
    }

    fun cancelWork() {
        WorkManager.getInstance(context).cancelUniqueWork("notifications_widget_task")
    }
}