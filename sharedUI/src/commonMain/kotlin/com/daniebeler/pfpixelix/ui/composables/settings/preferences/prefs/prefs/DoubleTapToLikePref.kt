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
import pixelix.app.generated.resources.double_tap_to_like
import pixelix.app.generated.resources.heart

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DoubleTapToLike() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.enableDoubleTapToLike) }
    LaunchedEffect(state.value) {
        prefs.enableDoubleTapToLike = state.value
    }
    SwitchPref(
        icon = Res.drawable.heart,
        title = stringResource(Res.string.double_tap_to_like),
        shapes = ListItemDefaults.segmentedShapes(index = 3, count = 5),
        state = state
    )
}