package com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.AppIconService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class AboutPixelixViewModel(
    private val platform: Platform,
    private val appIconService: AppIconService
) : ViewModel() {
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