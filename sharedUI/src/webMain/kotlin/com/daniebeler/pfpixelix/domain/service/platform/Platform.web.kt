package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.PlatformFile
import me.tatarka.inject.annotations.Inject

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    // Handle to the OAuth popup opened by [openUrl], so [dismissBrowser] can close it once the
    // redirect has been received. The popup normally closes itself (see oauth-callback.html).
    private var authPopup: JsAny? = null

    actual fun toSafeUri(platformFile: PlatformFile): KmpUri = platformFile.toKmpUri()

    actual fun openUrl(url: String) {
        authPopup = openInNewTab(url)
    }

    actual fun dismissBrowser() {
        closeWindow(authPopup)
        authPopup = null
    }

    actual fun shareText(text: String) {}

    actual fun getAppVersion(): String = "1.0.0"

    actual fun pinWidget() {}
}

private fun openInNewTab(url: String): JsAny? = js("window.open(url, '_blank')")

// The OAuth authorize page opens in a popup; the callback page posts the redirect URL back to
// this window (wired up in createWebAppComponent) and the coroutine in the main tab resumes.
internal actual val Platform.redirectUrl: String
    get() = "${windowOrigin()}/oauth-callback.html"

private fun windowOrigin(): String = js("window.location.origin")

private fun closeWindow(win: JsAny?): Unit =
    js("{ if (win) { try { win.close(); } catch (e) {} } }")