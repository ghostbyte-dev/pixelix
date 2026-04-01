package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SwitchPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.swipe_arrows
import pixelix.app.generated.resources.swipe_between_tabs

@Composable
fun SwipeBetweenTimelines() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.enableSwipeBetweenTabs) }
    LaunchedEffect(state.value) {
        prefs.enableSwipeBetweenTabs = state.value
    }
    SwitchPref(
        leadingIcon =  Res.drawable.swipe_arrows,
        title = stringResource(Res.string.swipe_between_tabs),
        state = state
    )
}