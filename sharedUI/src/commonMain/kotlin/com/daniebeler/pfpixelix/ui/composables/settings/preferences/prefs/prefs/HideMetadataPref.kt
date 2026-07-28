package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SwitchPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.camera
import pixelix.app.generated.resources.hide_camera_metadata

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HideMetadataPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.hideMetadata) }
    LaunchedEffect(state.value) {
        prefs.hideMetadata = state.value
    }
    SwitchPref(
        icon = Res.drawable.camera,
        title = stringResource(Res.string.hide_camera_metadata),
        shapes = ListItemDefaults.segmentedShapes(index = 1, count = 7),
        state = state
    )
}