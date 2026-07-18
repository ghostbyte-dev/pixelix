package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.repost
import pixelix.app.generated.resources.repost_settings


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RepostSettingsPref(openUrl: () -> Unit) {
    SettingPref(
        icon = Res.drawable.repost,
        title = stringResource(Res.string.repost_settings),
        trailingContent = {
            Icon(
                imageVector = vectorResource(Res.drawable.open), contentDescription = null
            )
        },
        shapes = ListItemDefaults.segmentedShapes(index = 6, count = 7),
        onClick = openUrl
    )
}
