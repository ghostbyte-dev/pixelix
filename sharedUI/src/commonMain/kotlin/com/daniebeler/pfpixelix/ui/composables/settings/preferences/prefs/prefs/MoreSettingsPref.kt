package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.more_settings
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.settings

@Composable
fun MoreSettingsPref(openUrl: () -> Unit) {
    SettingPref(
        leadingIcon = Res.drawable.settings,
        title = stringResource(Res.string.more_settings),
        trailingContent = Res.drawable.open,
        onClick = openUrl
    )
}

@Composable
private fun MoreSettingsPrefPreview() {
    MoreSettingsPref(openUrl = {
        Logger.v("URL opened: url")
    })
}
