package com.daniebeler.pfpixelix.domain.model

import androidx.compose.ui.graphics.Color

enum class AppAccentColor(val light: Long, val dark: Long) {
    GREEN(0xFF2D6A44, 0xFF95D5A7),
    BLUE(0xFF4C5C92, 0xFFB5C4FF),
    RED(0xFF8F4C38, 0xFFFFB5A0),
    YELLOW(0xFF6D5E0F, 0xFFDBC66E),
    PINK(0xFF8C4A5F, 0xFFFFB1C7),
    PURPLE(0xFF775083, 0xFFE6B6F1),
    TURQUOISE(0xFF006B5F, 0xFF82D5C7),
    WHITE(0xFF5D5F5F, 0xFFFFFFFF);

    fun getColor(isDarkTheme: Boolean): Color {
        return Color(if (isDarkTheme) dark else light)
    }
}
