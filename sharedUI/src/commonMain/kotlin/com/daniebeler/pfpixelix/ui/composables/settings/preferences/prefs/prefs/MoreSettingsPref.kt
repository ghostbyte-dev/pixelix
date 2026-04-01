package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.more_settings
import pixelix.app.generated.resources.open_outline
import pixelix.app.generated.resources.settings_outline

@Composable
fun MoreSettingsPref(openUrl: () -> Unit) {
    SettingPref(
        leadingIcon = Res.drawable.settings_outline,
        title = stringResource(Res.string.more_settings),
        trailingContent = Res.drawable.open_outline,
        onClick = openUrl
    )
}

@Composable
private fun MoreSettingsPrefPreview() {
    MoreSettingsPref(openUrl = {
        Logger.v("URL opened: url")
    })
}
