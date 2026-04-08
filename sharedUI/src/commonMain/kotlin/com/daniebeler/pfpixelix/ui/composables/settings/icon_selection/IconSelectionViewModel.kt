package com.daniebeler.pfpixelix.ui.composables.settings.icon_selection

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.icon.AppIconService
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.DrawableResource

@Inject
class IconSelectionViewModel(
    val appIconService: AppIconService
) : ViewModel() {
    val icons = appIconService.icons
    val selectedIcon = appIconService.currentIcon

    fun changeIcon(icon: DrawableResource) {
        appIconService.selectIcon(icon)
    }
}
