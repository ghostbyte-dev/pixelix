package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.open_outline
import pixelix.app.generated.resources.trash
import pixelix.app.generated.resources.delete_account


@Composable
fun DeleteAccountPref(openUrl: () -> Unit) {
    SettingPref(
        leadingIcon = Res.drawable.trash,
        title = stringResource(Res.string.delete_account),
        trailingContent = Res.drawable.open_outline,
        onClick = openUrl,
        textColor = MaterialTheme.colorScheme.error
    )
}
