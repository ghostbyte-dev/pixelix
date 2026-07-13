package com.daniebeler.pfpixelix.domain.service.platform

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.MyApplication
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.widget.notifications.NotificationWidgetReceiver
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import me.tatarka.inject.annotations.Inject
import java.io.File

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    actual fun toSafeUri(platformFile: PlatformFile): KmpUri {
        val file = File(platformFile.path)

        val authority = "${context.packageName}.fileprovider"

        val contentUri = FileProvider.getUriForFile(
            context,
            authority,
            file
        )

        return contentUri
    }

    actual fun openUrl(url: String) {
        val activity = MyApplication.currentActivity?.get()
        if (activity != null) {
            if (prefs.useInAppBrowser) {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(activity, Uri.parse(url))
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    actual fun shareText(text: String) {
        val activity = MyApplication.currentActivity?.get()
        if (activity != null) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            activity.startActivity(shareIntent)
        }
    }

    actual fun dismissBrowser() {}

        actual fun getAppVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Throwable) {
            Logger.e("appVersionName", e)
            null
        }.orEmpty()
    }

    actual fun pinWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val myProvider = ComponentName(context, NotificationWidgetReceiver::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(myProvider, null, null)
        }
    }
}
