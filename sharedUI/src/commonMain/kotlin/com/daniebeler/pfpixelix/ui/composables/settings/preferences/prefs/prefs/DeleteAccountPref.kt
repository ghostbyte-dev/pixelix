package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.delete_account
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.trash


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeleteAccountPref(openUrl: () -> Unit) {
    SettingPref(
        icon = Res.drawable.trash,
        title = stringResource(Res.string.delete_account),
        trailingContent = {
            Icon(
                imageVector = vectorResource(Res.drawable.open), contentDescription = null
            )
        },
        shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
        onClick = openUrl,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
}
