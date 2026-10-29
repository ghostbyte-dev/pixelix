package com.daniebeler.pfpixelix.ui.composables.settings.icon_selection

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.AppIconService
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import org.jetbrains.compose.resources.DrawableResource

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class IconSelectionViewModel(
    val appIconService: AppIconService
) : ViewModel() {
    val icons = appIconService.icons
    val selectedIcon = appIconService.currentIcon

    fun changeIcon(icon: DrawableResource) {
        appIconService.selectIcon(icon)
    }
}
