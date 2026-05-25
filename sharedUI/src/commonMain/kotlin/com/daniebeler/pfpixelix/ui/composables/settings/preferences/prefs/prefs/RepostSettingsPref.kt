package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.repost_settings
import pixelix.app.generated.resources.repost


@Composable
fun RepostSettingsPref(openUrl: () -> Unit) {
    SettingPref(
        leadingIcon = Res.drawable.repost,
        title = stringResource(Res.string.repost_settings),
        trailingContent = Res.drawable.open,
        onClick = openUrl
    )
}
