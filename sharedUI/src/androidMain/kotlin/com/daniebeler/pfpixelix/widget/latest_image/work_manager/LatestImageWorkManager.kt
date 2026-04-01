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

public val LatestImageWorkManagerRetrySeconds: Long = 15

class LatestImageWorkManager(private val context: Context) {
    fun executePeriodic() = enqueuePeriodicWorker()
    fun executeOnce() = startWorkerOnce()

    private fun startWorkerOnce() {
        val uploadWorkerRequest: WorkRequest =
            OneTimeWorkRequestBuilder<LatestImageTask>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
        WorkManager.getInstance(context).enqueue(uploadWorkerRequest)
    }

    private fun enqueuePeriodicWorker() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "latest_image_worker_task",
            // KEEP documentation:
            // If there is existing pending (uncompleted) work with the same unique name, do nothing.
            // Otherwise, insert the newly-specified work.
            ExistingPeriodicWorkPolicy.KEEP, buildRequest()
        )
    }

    private fun buildRequest(): PeriodicWorkRequest {
        // 1 day
        return PeriodicWorkRequestBuilder<LatestImageTask>(
            15, TimeUnit.MINUTES
        ).addTag("latest_image_worker_task").setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL, LatestImageWorkManagerRetrySeconds, TimeUnit.SECONDS
        ).build()
    }

    fun cancelWork() {
        WorkManager.getInstance(context).cancelUniqueWork("latest_image_worker_task")
    }
}