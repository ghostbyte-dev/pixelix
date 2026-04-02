package com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.icon.AppIconService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import me.tatarka.inject.annotations.Inject

class AboutPixelixViewModel @Inject constructor(
    private val platform: Platform,
    private val appIconService: AppIconService
) : ViewModel() {
    val versionName = platform.getAppVersion()
    val appIcon = appIconService.currentIcon

    fun rateApp() {
        platform.openUrl(
             "https://play.google.com/store/apps/details?id=com.daniebeler.pfpixelix"
        )
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }
}