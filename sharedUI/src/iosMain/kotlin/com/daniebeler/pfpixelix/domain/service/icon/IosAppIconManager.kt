package com.daniebeler.pfpixelix.domain.service.icon

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
import platform.UIKit.UIApplication
import platform.UIKit.alternateIconName
import platform.UIKit.setAlternateIconName

class IosAppIconManager : AppIconManager {
    private val iconIds = mapOf(
        Res.drawable.app_icon_00 to "AppIcon_02",
        Res.drawable.app_icon_01 to "AppIcon_01",
        Res.drawable.app_icon_02 to "AppIcon",
        Res.drawable.app_icon_03 to "AppIcon_03",
        Res.drawable.app_icon_05 to "AppIcon_04",
        Res.drawable.app_icon_06 to "AppIcon_05",
        Res.drawable.app_icon_07 to "AppIcon_06",
        Res.drawable.app_icon_08 to "AppIcon_07",
        Res.drawable.app_icon_09 to "AppIcon_08",
    )

    override fun getCurrentIcon(): DrawableResource {
        val currentId = UIApplication.sharedApplication.alternateIconName
        for ((res, id) in iconIds.entries) {
            if (currentId == id) {
                return res
            }
        }
        return Res.drawable.app_icon_02
    }

    override fun setCustomIcon(icon: DrawableResource) {
        if (icon == Res.drawable.app_icon_02) {
            UIApplication.sharedApplication.setAlternateIconName(null, null)
        } else {
            UIApplication.sharedApplication.setAlternateIconName(iconIds[icon]!!, null)
        }
    }
}