package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.delay
import me.tatarka.inject.annotations.Inject

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    // Kept so the app can close the OAuth tab after receiving the callback.
    private var authPopup: JsAny? = null

    actual fun toSafeUri(platformFile: PlatformFile): KmpUri = platformFile.toKmpUri()

    actual fun prepareAuthBrowser(host: String, backendType: BackendType): Boolean {
        clearPreparedAuthData()
        val launcherUrl = buildLauncherUrl(host, backendType.name.lowercase(), redirectUrl)
        authPopup = openInNewTab(launcherUrl)
        return authPopup != null && !isWindowClosed(authPopup)
    }

    actual suspend fun consumePreparedAuthData(): PreparedAuthData? {
        repeat(AUTH_PREPARATION_ATTEMPTS) {
            takePreparedAuthData()?.let {
                clearPreparedAuthData()
                return it
            }
            delay(AUTH_PREPARATION_POLL_MS)
        }
        error("Timed out while preparing the OAuth login page")
    }

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

    actual fun hasPushNotificationPermission(): Boolean {
        return false
    }

    actual fun openAppSettings() {
    }
}

private fun buildLauncherUrl(host: String, backendType: String, redirectUrl: String): String = js(
    """{
        var params = new URLSearchParams({
            host: host,
            backend: backendType,
            redirect_uri: redirectUrl
        });
        return window.location.origin + '/oauth-launcher.html?' + params.toString();
    }"""
)

private fun openInNewTab(url: String): JsAny? = js("window.open(url, '_blank')")

private fun isWindowClosed(win: JsAny?): Boolean =
    js("win == null || win.closed")

private fun clearPreparedAuthData(): Unit = js(
    """{
        window.localStorage.removeItem('pixelix-oauth-client-id');
        window.localStorage.removeItem('pixelix-oauth-client-secret');
    }"""
)

private fun takePreparedAuthData(): PreparedAuthData? {
    val clientId = readLocalStorage("pixelix-oauth-client-id") ?: return null
    return PreparedAuthData(clientId, readLocalStorage("pixelix-oauth-client-secret"))
}

private fun readLocalStorage(key: String): String? =
    js("window.localStorage.getItem(key)")

private const val AUTH_PREPARATION_ATTEMPTS = 120
private const val AUTH_PREPARATION_POLL_MS = 250L

// The OAuth authorize page opens in a popup; the callback page posts the redirect URL back to
// this window (wired up in createWebAppComponent) and the coroutine in the main tab resumes.
internal actual val Platform.redirectUrl: String
    get() = "${windowOrigin()}/oauth-callback.html"

private fun windowOrigin(): String = js("window.location.origin")

private fun closeWindow(win: JsAny?): Unit =
    js("{ if (win) { try { win.close(); } catch (e) {} } }")