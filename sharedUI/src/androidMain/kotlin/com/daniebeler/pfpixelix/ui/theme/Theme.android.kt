package com.daniebeler.pfpixelix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.MyApplication
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.AMOLED
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.DARK
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.LIGHT

@Composable
actual fun generateColorScheme(
    nightModeValue: Int,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme {
    val context = LocalAppComponent.current.context
    return remember(
        nightModeValue, dynamicColor, lightScheme, darkScheme
    ) {
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (nightModeValue) {
                AMOLED -> dynamicDarkColorScheme(context).toAmoled()
                DARK -> dynamicDarkColorScheme(context)
                else -> dynamicLightColorScheme(context)
            }
        } else {
            when (nightModeValue) {
                AMOLED -> darkScheme.toAmoled()
                DARK -> darkScheme
                else -> lightScheme
            }
        }
    }
}

actual fun applySystemNightMode(isDark: Boolean) {
    val activity = MyApplication.currentActivity?.get() ?: return
    val window = activity.window
    Logger.d { "applySystemNightMode isDark=$isDark" }
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightStatusBars = !isDark
        isAppearanceLightNavigationBars = !isDark
    }
}