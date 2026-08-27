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
import pixelix.app.generated.resources.browser
import pixelix.app.generated.resources.use_in_app_browser

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UseInAppBrowserPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.useInAppBrowser) }
    LaunchedEffect(state.value) {
        prefs.useInAppBrowser = state.value
    }
    SwitchPref(
        icon = Res.drawable.browser,
        title = stringResource(Res.string.use_in_app_browser),
        shapes = ListItemDefaults.segmentedShapes(index = 3, count = 5),
        state = state
    )
}