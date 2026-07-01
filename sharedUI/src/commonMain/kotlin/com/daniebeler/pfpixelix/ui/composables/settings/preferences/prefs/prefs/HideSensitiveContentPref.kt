package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SwitchPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.eye_off
import pixelix.app.generated.resources.blur
import pixelix.app.generated.resources.hide_sensitive_content
import pixelix.app.generated.resources.blur_sensitive_content

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HideSensitiveContentPref() {
    val prefs = LocalAppComponent.current.preferences
    val hideState = remember { mutableStateOf(prefs.hideSensitiveContent) }
    LaunchedEffect(hideState.value) {
        prefs.hideSensitiveContent = hideState.value
    }

    val blurState = remember { mutableStateOf(prefs.blurSensitiveContent) }
    LaunchedEffect(blurState.value) {
        prefs.blurSensitiveContent = blurState.value
    }
    Column {
        SwitchPref(
            icon = Res.drawable.eye_off,
            title = stringResource(Res.string.hide_sensitive_content),
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 7),
            state = hideState
        )

        AnimatedVisibility(
            visible = !hideState.value,
            enter = expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
            exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
        ) {
            Box(modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap)) {
                SwitchPref(
                    icon = Res.drawable.blur,
                    title = stringResource(Res.string.blur_sensitive_content),
                    shapes = ListItemDefaults.segmentedShapes(index = 1, count = 7),
                    state = blurState
                )
            }
        }
    }
}