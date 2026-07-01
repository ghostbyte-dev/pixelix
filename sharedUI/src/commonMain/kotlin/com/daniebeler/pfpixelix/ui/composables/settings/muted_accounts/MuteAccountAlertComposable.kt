package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.AlertTopSection
import com.daniebeler.pfpixelix.utils.formatLocalizedOnlyDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.mute
import pixelix.app.generated.resources.mute_account
import pixelix.app.generated.resources.mute_consequence_1
import pixelix.app.generated.resources.mute_consequence_2
import pixelix.app.generated.resources.mute_consequence_3
import pixelix.app.generated.resources.mute_consequence_4
import pixelix.app.generated.resources.mute_consequence_5
import pixelix.app.generated.resources.optional_end_date
import pixelix.app.generated.resources.unmute_account
import pixelix.app.generated.resources.unmute_caps
import kotlin.time.Instant

@Composable
fun MuteAccountAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: (userMuteRequest: UserMuteRequest) -> Unit,
    mutedAccount: MutedAccount?,
    capabilities: Capabilities
) {
    val isMuted =
        mutedAccount?.muteOptions?.mute == true || mutedAccount?.muteOptions?.muteNotifications == true || mutedAccount?.muteOptions?.muteReblogs == true || mutedAccount?.muteOptions?.muteStatuses == true
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
                        checked = muteOptions.muteStatuses ?: false,
                        onCheckedChange = { muteOptions = muteOptions.copy(muteStatuses = it) })

                    AnimatedVisibility(
                        modifier = Modifier.padding(top = 8.dp),
                        visible = muteOptions.muteStatuses == true,
                        enter = slideInVertically() + fadeIn(),
                        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
                    ) {
                        MuteOptionRow(
                            label = "Remove Statuses From Timeline",
                            checked = muteOptions.removeStatusesFromTimeline ?: false,
                            onCheckedChange = {
                                muteOptions = muteOptions.copy(removeStatusesFromTimeline = it)
                            })
                    }
                    MuteOptionRow(
                        label = "Mute Reblogs",
                        checked = muteOptions.muteReblogs ?: false,
                        onCheckedChange = { muteOptions = muteOptions.copy(muteReblogs = it) })

                    AnimatedVisibility(
                        modifier = Modifier.padding(top = 8.dp),
                        visible = muteOptions.muteReblogs == true,
                        enter = slideInVertically() + fadeIn(),
                        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
                    ) {
                        MuteOptionRow(
                            label = "Remove Reblogs From Timeline",
                            checked = muteOptions.removeReblogsFromTimeline ?: false,
                            onCheckedChange = {
                                muteOptions = muteOptions.copy(removeReblogsFromTimeline = it)
                            })
                    }
                    MuteOptionRow(
                        label = "Mute Notifications",
                        checked = muteOptions.muteNotifications ?: false,
                        onCheckedChange = {
                            muteOptions = muteOptions.copy(muteNotifications = it)
                        })

                    AnimatedVisibility(
                        modifier = Modifier.padding(top = 8.dp),
                        visible = muteOptions.muteReblogs == true || muteOptions.muteStatuses == true || muteOptions.muteNotifications == true,
                        enter = slideInVertically() + fadeIn(),
                        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
                    ) {
                        DatePickerFieldToModal(onDateSelected = {
                            muteOptions = muteOptions.copy(endDate = it)
                        })
                    }
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

@Composable
fun MuteOptionRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun DatePickerFieldToModal(onDateSelected: (Instant?) -> Unit, modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Instant?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { formatLocalizedOnlyDate(it.toString()) } ?: "",
        onValueChange = { },
        label = { Text(stringResource(Res.string.optional_end_date)) },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(vectorResource(Res.drawable.datetime), contentDescription = "Select date")
        },
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                selectedDate = it
                onDateSelected(it)
            },
            onDismiss = { showModal = false }
        )
    }
}

@Composable
fun DatePickerModal(
    onDateSelected: (Instant?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis?.let {
                    Instant.fromEpochMilliseconds(
                        it
                    )
                })
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}