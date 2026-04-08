package com.daniebeler.pfpixelix.ui.composables.settings.preferences

import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.icon.AppIconService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class PreferencesViewModel(
    private val authService: AuthService,
    private val platform: Platform,
    private val appIconService: AppIconService
) : ViewModel() {
    val appIcon = appIconService.currentIcon
    val versionName = platform.getAppVersion()

    fun logout() {
        viewModelScope.launch {
            authService.deleteSession()
        }
    }

    fun openMoreSettingsPage() {
        authService.getCurrentSession()?.let {
            platform.openUrl("${it.serverUrl}settings/home")
        }
    }

    fun openRepostSettings() {
        authService.getCurrentSession()?.let {
            platform.openUrl("${it.serverUrl}settings/timeline")
        }
    }

    fun openDeleteAccountPage() {
        authService.getCurrentSession()?.let {
            platform.openUrl("${it.serverUrl}settings/remove/request/permanent")
        }
    }
}