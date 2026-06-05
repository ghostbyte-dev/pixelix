package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.general.AppIconManager
import com.daniebeler.pfpixelix.domain.service.general.AppIconService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.DrawableResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.app_icon_00
import pixelix.app.generated.resources.app_icon_01
import pixelix.app.generated.resources.app_icon_02
import pixelix.app.generated.resources.app_icon_03
import pixelix.app.generated.resources.app_icon_05
import pixelix.app.generated.resources.app_icon_06
import pixelix.app.generated.resources.app_icon_07
import pixelix.app.generated.resources.app_icon_08
import pixelix.app.generated.resources.app_icon_09

@AppSingleton
@Inject
class PixelfedAppIconService(
    private val iconManager: AppIconManager
) : AppIconService {
    override val icons = listOf(
        Res.drawable.app_icon_00,
        Res.drawable.app_icon_01,
        Res.drawable.app_icon_02,
        Res.drawable.app_icon_03,
        Res.drawable.app_icon_05,
        Res.drawable.app_icon_06,
        Res.drawable.app_icon_07,
        Res.drawable.app_icon_08,
        Res.drawable.app_icon_09,
    )

    private val currentIconFlow = MutableStateFlow(iconManager.getCurrentIcon())
    override val currentIcon: StateFlow<DrawableResource> = currentIconFlow

    override fun selectIcon(icon: DrawableResource) {
        iconManager.setCustomIcon(icon)
        currentIconFlow.value = icon
    }
}

