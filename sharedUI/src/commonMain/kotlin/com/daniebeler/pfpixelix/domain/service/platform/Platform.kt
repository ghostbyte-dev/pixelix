package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.PlatformFile
import me.tatarka.inject.annotations.Inject

@Inject
expect class Platform(
    context: KmpContext,
    prefs: UserPreferences
) {
    fun toSafeUri(platformFile: PlatformFile): KmpUri

    /** Starts browser-side OAuth during the user gesture. Non-web platforms do nothing. */
    fun prepareAuthBrowser(host: String, backendType: BackendType): Boolean

    /** Returns data prepared by the web launcher, or null when OAuth must start in the app. */
    suspend fun consumePreparedAuthData(): PreparedAuthData?
    fun openUrl(url: String)
    fun dismissBrowser()
    fun shareText(text: String)
    fun getAppVersion(): String
    fun pinWidget()
}

data class PreparedAuthData(
    val clientId: String,
    val clientSecret: String?
)

internal expect val Platform.redirectUrl: String
