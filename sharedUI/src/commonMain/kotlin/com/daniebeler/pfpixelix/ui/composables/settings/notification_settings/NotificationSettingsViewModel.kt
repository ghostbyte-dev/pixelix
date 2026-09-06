package com.daniebeler.pfpixelix.ui.composables.settings.notification_settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import me.tatarka.inject.annotations.Inject

@Inject
class NotificationSettingsViewModel(
    private val platform: Platform
): ViewModel() {
    var hasPushNotificationPermission by mutableStateOf(platform.hasPushNotificationPermission())

    fun refreshPermissionState() {
        hasPushNotificationPermission = platform.hasPushNotificationPermission()
    }

    fun openAppSettings() {
        platform.openAppSettings()
    }
}