package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BlurOn
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
import pixelix.app.generated.resources.eye_off_outline
import pixelix.app.generated.resources.blur
import pixelix.app.generated.resources.hide_sensitive_content
import pixelix.app.generated.resources.blur_sensitive_content

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
            leadingIcon = Res.drawable.eye_off_outline,
            title = stringResource(Res.string.hide_sensitive_content),
            state = hideState
        )

        AnimatedVisibility(
            modifier = Modifier.padding(top = 8.dp),
            visible = !hideState.value,
            enter = slideInVertically() + fadeIn(),
            exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
        ) {
            SwitchPref(
                leadingIcon = Res.drawable.blur,
                title = stringResource(Res.string.blur_sensitive_content),
                state = blurState
            )
        }
    }
}