package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.AlertTopSection
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.MuteOptionRow
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.mute
import pixelix.app.generated.resources.mute_account
import pixelix.app.generated.resources.mute_consequence_1
import pixelix.app.generated.resources.mute_consequence_2
import pixelix.app.generated.resources.mute_consequence_3
import pixelix.app.generated.resources.mute_consequence_4
import pixelix.app.generated.resources.mute_consequence_5
import pixelix.app.generated.resources.unmute_account
import pixelix.app.generated.resources.unmute_caps

@Composable
fun MuteAccountAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: (userMuteRequest: UserMuteRequest) -> Unit,
    mutedAccount: MutedAccount?,
    capabilities: Capabilities
) {
    val isMuted = mutedAccount?.muteOptions?.mute == true
    val showAdvanced = capabilities.profile.showAdvancedMuteOptions
    var muteOptions by remember(mutedAccount) {
        mutableStateOf(mutedAccount?.muteOptions ?: UserMuteRequest())
    }

    AlertDialog(title = {
        Text(text = stringResource(if (isMuted) Res.string.unmute_account else Res.string.mute_account))
    }, text = {
        if (mutedAccount == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column {
                AlertTopSection(account = mutedAccount.account)
                HorizontalDivider(Modifier.padding(vertical = 12.dp))

                if (isMuted) {
                    // Unmute: no extra options needed
                } else if (showAdvanced) {
                    Text(
                        text = "Mute options",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MuteOptionRow(
                        label = "Mute Statuses",
                        checked = muteOptions.muteStatuses,
                        onCheckedChange = { muteOptions = muteOptions.copy(muteStatuses = it) })
                    MuteOptionRow(
                        label = "Mute Reblogs",
                        checked = muteOptions.muteReblogs,
                        onCheckedChange = { muteOptions = muteOptions.copy(muteReblogs = it) })
                    MuteOptionRow(
                        label = "Mute Notifications",
                        checked = muteOptions.muteNotifications,
                        onCheckedChange = {
                            muteOptions = muteOptions.copy(muteNotifications = it)
                        })
                    MuteOptionRow(
                        label = "Remove Statuses From Timeline",
                        checked = muteOptions.removeStatusesFromTimeline,
                        onCheckedChange = {
                            muteOptions = muteOptions.copy(removeStatusesFromTimeline = it)
                        })
                    MuteOptionRow(
                        label = "Remove Reblogs From Timeline",
                        checked = muteOptions.removeReblogsFromTimeline,
                        onCheckedChange = {
                            muteOptions = muteOptions.copy(removeReblogsFromTimeline = it)
                        })
                } else {
                    Text(text = stringResource(Res.string.mute_consequence_1))
                    Text(text = stringResource(Res.string.mute_consequence_2))
                    Text(text = stringResource(Res.string.mute_consequence_3))
                    Text(text = stringResource(Res.string.mute_consequence_4))
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    Text(text = stringResource(Res.string.mute_consequence_5))
                }
            }
        }
    }, onDismissRequest = { onDismissRequest() }, confirmButton = {
        TextButton(onClick = {
            val request = when {
                isMuted -> UserMuteRequest(mute = false)
                showAdvanced -> muteOptions
                else -> UserMuteRequest(mute = true)
            }
            onConfirmation(request)
        }) {
            Text(stringResource(if (isMuted) Res.string.unmute_caps else Res.string.mute))
        }
    }, dismissButton = {
        TextButton(onClick = { onDismissRequest() }) {
            Text(stringResource(Res.string.cancel))
        }
    })
}