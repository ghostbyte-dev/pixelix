package com.daniebeler.pfpixelix.domain.service.platform

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import me.tatarka.inject.annotations.Inject
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.URLWithString
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {

    actual fun toSafeUri(platformFile: PlatformFile): KmpUri {
        return platformFile.toKmpUri()
    }

    actual fun prepareAuthBrowser(host: String, backendType: BackendType): Boolean = true

    actual suspend fun consumePreparedAuthData(): PreparedAuthData? = null

    actual fun openUrl(url: String) {
        if (prefs.useInAppBrowser) {
            val safariViewController = SFSafariViewController(uRL = NSURL(string = url))
            dispatch_async(dispatch_get_main_queue()) {
                val topController = getTopViewController()

                topController?.presentViewController(
                    viewControllerToPresent = safariViewController,
                    animated = true,
                    completion = null
                )
            }

        } else {
            UIApplication.sharedApplication.openURL(
                url = URLWithString(url)!!,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }


    fun getTopViewController(base: UIViewController? = UIApplication.sharedApplication.keyWindow?.rootViewController): UIViewController? {
        if (base is UINavigationController) {
            return getTopViewController(base.visibleViewController)
        }
        if (base is UITabBarController) {
            if (base.selectedViewController != null) {
                return getTopViewController(base.selectedViewController)
            }
        }
        if (base?.presentedViewController != null) {
            return getTopViewController(base.presentedViewController)
        }
        return base
    }

    actual fun dismissBrowser() {
        if (prefs.useInAppBrowser) {
            val self = context.viewController
            self.dismissModalViewControllerAnimated(true)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun shareText(text: String) {
        val self = context.viewController
        val vc = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        if (isIpad()) {
            Logger.d("share on iPad")
            vc.popoverPresentationController?.apply {
                sourceView = self.view
                sourceRect = self.view.center.useContents { CGRectMake(x, y, 0.0, 0.0) }
                permittedArrowDirections = 0uL
            }
        }
        self.presentViewController(vc, true, null)
    }

    private fun isIpad(): Boolean {
        val device = UIDevice.currentDevice
        return device.userInterfaceIdiom == UIUserInterfaceIdiomPad
    }

    actual fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString").toString()
    }

    actual fun pinWidget() {}

    actual fun hasPushNotificationPermission(): Boolean {
        return false
    }

    actual fun openAppSettings() {
    }
}
