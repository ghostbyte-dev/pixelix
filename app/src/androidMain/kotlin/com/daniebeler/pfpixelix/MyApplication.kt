package com.daniebeler.pfpixelix

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.icon.AndroidAppIconManager
import com.daniebeler.pfpixelix.utils.configureLogger
import com.daniebeler.pfpixelix.widget.notifications.work_manager.LatestImageTask
import com.daniebeler.pfpixelix.widget.notifications.work_manager.NotificationsTask
import java.lang.ref.WeakReference


class MyApplication : Application(), Configuration.Provider {

    private val workerFactory: WorkerFactory by lazy { MyWorkerFactory(appComponent) }
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        appComponent = AppComponent.create(
            this,
            AndroidAppIconManager(this)
        )
        SingletonImageLoader.setSafe {
            appComponent.provideImageLoader()
        }
        configureLogger(BuildConfig.DEBUG)
        super.onCreate()
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
        var currentActivity: WeakReference<ComponentActivity>? = null
    }
}

private class MyWorkerFactory(val appComponent: AppComponent): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when(workerClassName) {
        NotificationsTask::class.java.name -> NotificationsTask(
            appContext,
            workerParameters,
            appComponent
        )
        LatestImageTask::class.java.name -> LatestImageTask(
            appContext,
            workerParameters,
            appComponent
        )
        else -> null
    }
}