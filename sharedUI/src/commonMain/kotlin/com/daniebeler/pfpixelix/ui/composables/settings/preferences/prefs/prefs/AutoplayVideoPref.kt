package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SwitchPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.autoplay
import pixelix.app.generated.resources.autoplay_videos

@Composable
fun AutoplayVideoPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.autoplayVideo) }
    LaunchedEffect(state.value) {
        prefs.autoplayVideo = state.value
    }
    SwitchPref(
        leadingIcon =  Res.drawable.autoplay,
        title = stringResource(Res.string.autoplay_videos),
        state = state
    )
}