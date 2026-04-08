package com.daniebeler.pfpixelix.domain.service.icon

import org.jetbrains.compose.resources.DrawableResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.app_icon_02

class DesktopAppIconManager : AppIconManager {

    override fun getCurrentIcon(): DrawableResource {
        return Res.drawable.app_icon_02
    }

    override fun setCustomIcon(icon: DrawableResource) {}
}