package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.are_you_sure_you_want_to_log_out
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.log_out_outline
import pixelix.app.generated.resources.logout
import pixelix.app.generated.resources.logout_questionmark

@Composable
fun LogoutPref(logout: () -> Unit) {
    val showAlert = remember { mutableStateOf(false) }

    LogoutAlert(showAlert, logout)

    SettingPref(
        leadingIcon = Res.drawable.log_out_outline,
        title = stringResource(Res.string.logout),
        trailingContent = null,
        onClick = { showAlert.value = true },
        textColor = MaterialTheme.colorScheme.error
    )
}

@Composable
fun LogoutAlert(show: MutableState<Boolean>, logout: () -> Unit) {
    if (show.value) {
        AlertDialog(title = {
            Text(text = stringResource(Res.string.logout_questionmark))
        }, text = {
            Text(text = stringResource(Res.string.are_you_sure_you_want_to_log_out))
        }, onDismissRequest = {
            show.value = false
        }, confirmButton = {
            TextButton(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    logout()
                }
            }) {
                Text(stringResource(Res.string.logout))
            }
        }, dismissButton = {
            TextButton(onClick = {
                show.value = false
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }
}

@Composable
private fun LogoutAlertPreview() {
    val showAlert = remember { mutableStateOf(true) }
    LogoutAlert(show = showAlert) { }
}