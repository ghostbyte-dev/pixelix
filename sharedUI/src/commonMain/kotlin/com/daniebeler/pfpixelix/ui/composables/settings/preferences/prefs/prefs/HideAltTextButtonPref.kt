package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

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
import pixelix.app.generated.resources.document_text
import pixelix.app.generated.resources.hide_alt_text_button

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HideAltTextButtonPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.hideAltTextButton) }
    LaunchedEffect(state.value) {
        prefs.hideAltTextButton = state.value
    }
    SwitchPref(
        icon = Res.drawable.document_text,
        title = stringResource(Res.string.hide_alt_text_button),
        shapes = ListItemDefaults.segmentedShapes(index = 1, count = 7),
        state = state
    )
}