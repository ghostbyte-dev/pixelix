package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.AppIconService
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class PreferencesViewModel(
    private val authService: AuthService,
    private val platform: Platform,
    appIconService: AppIconService,
    session: Session
) : ViewModel() {
    val capabilities: Capabilities = session.capabilities.value
    val backendType: BackendType = session.backendType.value
    val appIcon = appIconService.currentIcon
    val versionName = platform.getAppVersion()

    fun logout() {
        viewModelScope.launch {
            authService.deleteSession()
        }
    }

    fun openMoreSettingsPage() {
        val customUrl = if (backendType == BackendType.PIXELFED) "settings/home" else "account"
        authService.getCurrentSession()?.let {
            platform.openUrl(it.serverUrl + customUrl)
        }
    }

    fun openRepostSettings() {
        authService.getCurrentSession()?.let {
            platform.openUrl("${it.serverUrl}settings/timeline")
        }
    }

    fun openDeleteAccountPage() {
        val customUrl =
            if (backendType == BackendType.PIXELFED) "settings/remove/request/permanent" else "account"

        authService.getCurrentSession()?.let {
            platform.openUrl(it.serverUrl + customUrl)
        }
    }
}