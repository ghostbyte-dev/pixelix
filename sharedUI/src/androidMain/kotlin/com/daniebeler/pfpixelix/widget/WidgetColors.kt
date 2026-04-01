package com.daniebeler.pfpixelix.widget


import androidx.compose.material3.darkColorScheme
import androidx.glance.material3.ColorProviders
import com.daniebeler.pfpixelix.ui.theme.primaryDark
import com.daniebeler.pfpixelix.ui.theme.secondaryDark
import com.daniebeler.pfpixelix.ui.theme.tertiaryDark

object WidgetColors {
    val colors = ColorProviders(
        light = DarkColorScheme,
        dark = DarkColorScheme
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    secondary = secondaryDark,
    tertiary = tertiaryDark,
)