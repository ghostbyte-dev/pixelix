package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppAccentColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomAccentColorPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.accentColor) }
    LaunchedEffect(state.value) {
        prefs.accentColor = state.value
    }

    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5
    SegmentedListItem(
        onClick = {},
        shapes = ListItemDefaults.segmentedShapes(index = 6, count = 8),
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                CustomAccentColorPrefItem(
                    AppAccentColor.GREEN,
                    isDarkTheme = dark,
                    state = state
                )
                CustomAccentColorPrefItem(
                    AppAccentColor.RED,
                    isDarkTheme = dark,
                    state = state
                )
                CustomAccentColorPrefItem(
                    AppAccentColor.BLUE,
                    isDarkTheme = dark,
                    state = state
                )
                CustomAccentColorPrefItem(
                    AppAccentColor.YELLOW,
                    isDarkTheme = dark,
                    state = state
                )
                CustomAccentColorPrefItem(
                    AppAccentColor.PINK,
                    isDarkTheme = dark,
                    state = state
                )
                CustomAccentColorPrefItem(
                    AppAccentColor.WHITE,
                    isDarkTheme = dark,
                    state = state
                )
            }
        }
    }
}

@Composable
fun CustomAccentColorPrefItem(color: AppAccentColor, isDarkTheme: Boolean, state: MutableState<String>) {
    if (color.name == state.value) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier.height(20.dp).width(20.dp).clip(CircleShape)
                    .background(color.getColor(isDarkTheme = isDarkTheme)))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(
                        width = 2.dp,
                        color = color.getColor(isDarkTheme = isDarkTheme),
                        shape = CircleShape
                    )
            )
        }
    } else {
        Box(
            Modifier.height(32.dp).width(32.dp).clip(CircleShape)
                .background(color.getColor(isDarkTheme = isDarkTheme))
                .clickable { state.value = color.name })
    }
}