package com.daniebeler.pfpixelix.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.domain.model.AppThemeMode


actual fun applySystemNightMode(isDark: Boolean) {}

@Composable
actual fun generateColorScheme(
    nightModeValue: Int,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme {
    //TODO dynamicColor
    return remember(
        nightModeValue, dynamicColor, lightScheme, darkScheme
    ) {
        when (nightModeValue) {
            AppThemeMode.AMOLED -> darkScheme.toAmoled()
            AppThemeMode.DARK -> darkScheme
            else -> lightScheme
        }
    }
}