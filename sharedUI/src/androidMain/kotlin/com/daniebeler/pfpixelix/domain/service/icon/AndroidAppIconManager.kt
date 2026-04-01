package com.daniebeler.pfpixelix.domain.service.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import co.touchlab.kermit.Logger
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

class AndroidAppIconManager(
    private val context: Context
) : AppIconManager {
    private val iconIds = mapOf(
        Res.drawable.app_icon_00 to "com.daniebeler.pfpixelix.Icon04",
        Res.drawable.app_icon_01 to "com.daniebeler.pfpixelix.Icon01",
        Res.drawable.app_icon_02 to "com.daniebeler.pfpixelix.Icon02",
        Res.drawable.app_icon_03 to "com.daniebeler.pfpixelix.Icon03",
        Res.drawable.app_icon_05 to "com.daniebeler.pfpixelix.Icon05",
        Res.drawable.app_icon_06 to "com.daniebeler.pfpixelix.Icon06",
        Res.drawable.app_icon_07 to "com.daniebeler.pfpixelix.Icon07",
        Res.drawable.app_icon_08 to "com.daniebeler.pfpixelix.Icon08",
        Res.drawable.app_icon_09 to "com.daniebeler.pfpixelix.Icon09",
    )

    override fun getCurrentIcon(): DrawableResource {
        for ((res, id) in iconIds.entries) {
            val i = context.packageManager.getComponentEnabledSetting(ComponentName(context, id))
            if (i == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return res
            }
        }
        return Res.drawable.app_icon_02
    }

    override fun setCustomIcon(icon: DrawableResource) {
        try {
            val pm = context.packageManager
            for ((res, id) in iconIds.entries) {
                val state =
                    if (res == icon) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    else PackageManager.COMPONENT_ENABLED_STATE_DISABLED

                pm.setComponentEnabledSetting(
                    ComponentName(context, id),
                    state,
                    PackageManager.DONT_KILL_APP
                )
            }
        } catch (e: Error) {
            Logger.e("enableCustomIcon", e)
        }
    }
}