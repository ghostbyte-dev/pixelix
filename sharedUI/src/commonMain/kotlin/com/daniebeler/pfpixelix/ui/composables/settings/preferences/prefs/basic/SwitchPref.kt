package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchPref(
    title: String,
    shapes: ListItemShapes,
    icon: DrawableResource,
    desc: String? = null,
    state: MutableState<Boolean>,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    var value by state

    SettingPref(
        title = title,
        desc = desc,
        shapes = shapes,
        icon = icon,
        trailingContent = {
            Switch(checked = value, onCheckedChange = { value = it })
        },
        onClick = { value = !value },
        containerColor = color

    )
}