package com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SwitchPref(
    leadingIcon: DrawableResource,
    title: String,
    desc: String? = null,
    state: MutableState<Boolean>
) {
    var value by state
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Box(modifier = Modifier.padding(end = 14.dp)) {
                Switch(checked = value, onCheckedChange = { value = it })
            }
        },
        onClick = { value = !value }
    )
}