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
import pixelix.app.generated.resources.swipe_gesture
import pixelix.app.generated.resources.swipe_between_tabs

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwipeBetweenTimelines() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.enableSwipeBetweenTabs) }
    LaunchedEffect(state.value) {
        prefs.enableSwipeBetweenTabs = state.value
    }
    SwitchPref(
        icon =  Res.drawable.swipe_gesture,
        title = stringResource(Res.string.swipe_between_tabs),
        shapes = ListItemDefaults.segmentedShapes(index = 2, count = 7),
        state = state
    )
}