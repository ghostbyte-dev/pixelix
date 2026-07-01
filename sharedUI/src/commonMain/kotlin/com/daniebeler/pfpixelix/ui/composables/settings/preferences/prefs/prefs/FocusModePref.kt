package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SwitchPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.focus_mode
import pixelix.app.generated.resources.square

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FocusModePref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.focusMode) }
    LaunchedEffect(state.value) {
        prefs.focusMode = state.value
    }
    SwitchPref(
        icon =  Res.drawable.square,
        title = stringResource(Res.string.focus_mode),
        shapes = ListItemDefaults.segmentedShapes(index = 2, count = 7),
        state = state
    )
}