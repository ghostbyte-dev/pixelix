package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppAccentColor

@Composable
fun CustomAccentColorPref() {
    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.accentColor) }
    LaunchedEffect(state.value) {
        prefs.accentColor = state.value
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(Modifier.height(32.dp).width(32.dp).clip(CircleShape).background(Color(AppAccentColor.GREEN)).clickable {
                    state.value = AppAccentColor.GREEN
                })
                Box(Modifier.height(32.dp).width(32.dp).clip(CircleShape).background(Color(AppAccentColor.RED)).clickable {
                    state.value = AppAccentColor.RED
                })
                Box(Modifier.height(32.dp).width(32.dp).clip(CircleShape).background(Color(AppAccentColor.BLUE)).clickable {
                    state.value = AppAccentColor.BLUE
                })
                val dark = MaterialTheme.colorScheme.background.luminance() < 0.5
                Box(Modifier.height(32.dp).width(32.dp).clip(CircleShape).background(if (dark) {Color(AppAccentColor.White)} else {Color(0xFF5D5F5F)}).clickable {
                    state.value = AppAccentColor.White
                })}
        }
    }
}