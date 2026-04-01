package com.daniebeler.pfpixelix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppAccentColor
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.DARK
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.FOLLOW_SYSTEM
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.LIGHT


fun ColorScheme.toAmoled(): ColorScheme {
    fun Color.darken(fraction: Float = 0.5f): Color = blend(Color.Black, fraction)
    return copy(
        primary = primary.darken(0.3f),
        onPrimary = onPrimary.darken(0.3f),
        primaryContainer = primaryContainer.darken(0.3f),
        onPrimaryContainer = onPrimaryContainer.darken(0.3f),
        inversePrimary = inversePrimary.darken(0.3f),
        secondary = secondary.darken(0.3f),
        onSecondary = onSecondary.darken(0.3f),
        secondaryContainer = secondaryContainer.darken(0.3f),
        onSecondaryContainer = onSecondaryContainer.darken(0.3f),
        tertiary = tertiary.darken(0.3f),
        onTertiary = onTertiary.darken(0.3f),
        tertiaryContainer = tertiaryContainer.darken(0.3f),
        onTertiaryContainer = onTertiaryContainer.darken(0.2f),
        background = Color.Black,
        onBackground = onBackground.darken(0.15f),
        surface = Color.Black,
        onSurface = onSurface.darken(0.15f),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        surfaceContainer = surfaceContainer.darken(0.4f),
        inverseSurface = inverseSurface.darken(0.2f),
        inverseOnSurface = inverseOnSurface.darken(0.2f),
        outline = outline.darken(0.2f),
        outlineVariant = outlineVariant.darken(0.2f)
    )
}

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)


private val blueLightScheme = lightColorScheme(
    primary = bluePrimaryLight,
    onPrimary = blueOnPrimaryLight,
    primaryContainer = bluePrimaryContainerLight,
    onPrimaryContainer = blueOnPrimaryContainerLight,
    secondary = blueSecondaryLight,
    onSecondary = blueOnSecondaryLight,
    secondaryContainer = blueSecondaryContainerLight,
    onSecondaryContainer = blueOnSecondaryContainerLight,
    tertiary = blueTertiaryLight,
    onTertiary = blueOnTertiaryLight,
    tertiaryContainer = blueTertiaryContainerLight,
    onTertiaryContainer = blueOnTertiaryContainerLight,
    error = blueErrorLight,
    onError = blueOnErrorLight,
    errorContainer = blueErrorContainerLight,
    onErrorContainer = blueOnErrorContainerLight,
    background = blueBackgroundLight,
    onBackground = blueOnBackgroundLight,
    surface = blueSurfaceLight,
    onSurface = blueOnSurfaceLight,
    surfaceVariant = blueSurfaceVariantLight,
    onSurfaceVariant = blueOnSurfaceVariantLight,
    outline = blueOutlineLight,
    outlineVariant = blueOutlineVariantLight,
    scrim = blueScrimLight,
    inverseSurface = blueInverseSurfaceLight,
    inverseOnSurface = blueInverseOnSurfaceLight,
    inversePrimary = blueInversePrimaryLight,
    surfaceDim = blueSurfaceDimLight,
    surfaceBright = blueSurfaceBrightLight,
    surfaceContainerLowest = blueSurfaceContainerLowestLight,
    surfaceContainerLow = blueSurfaceContainerLowLight,
    surfaceContainer = blueSurfaceContainerLight,
    surfaceContainerHigh = blueSurfaceContainerHighLight,
    surfaceContainerHighest = blueSurfaceContainerHighestLight,
)

private val blueDarkScheme = darkColorScheme(
    primary = bluePrimaryDark,
    onPrimary = blueOnPrimaryDark,
    primaryContainer = bluePrimaryContainerDark,
    onPrimaryContainer = blueOnPrimaryContainerDark,
    secondary = blueSecondaryDark,
    onSecondary = blueOnSecondaryDark,
    secondaryContainer = blueSecondaryContainerDark,
    onSecondaryContainer = blueOnSecondaryContainerDark,
    tertiary = blueTertiaryDark,
    onTertiary = blueOnTertiaryDark,
    tertiaryContainer = blueTertiaryContainerDark,
    onTertiaryContainer = blueOnTertiaryContainerDark,
    error = blueErrorDark,
    onError = blueOnErrorDark,
    errorContainer = blueErrorContainerDark,
    onErrorContainer = blueOnErrorContainerDark,
    background = blueBackgroundDark,
    onBackground = blueOnBackgroundDark,
    surface = blueSurfaceDark,
    onSurface = blueOnSurfaceDark,
    surfaceVariant = blueSurfaceVariantDark,
    onSurfaceVariant = blueOnSurfaceVariantDark,
    outline = blueOutlineDark,
    outlineVariant = blueOutlineVariantDark,
    scrim = blueScrimDark,
    inverseSurface = blueInverseSurfaceDark,
    inverseOnSurface = blueInverseOnSurfaceDark,
    inversePrimary = blueInversePrimaryDark,
    surfaceDim = blueSurfaceDimDark,
    surfaceBright = blueSurfaceBrightDark,
    surfaceContainerLowest = blueSurfaceContainerLowestDark,
    surfaceContainerLow = blueSurfaceContainerLowDark,
    surfaceContainer = blueSurfaceContainerDark,
    surfaceContainerHigh = blueSurfaceContainerHighDark,
    surfaceContainerHighest = blueSurfaceContainerHighestDark,
)

private val redLightScheme = lightColorScheme(
    primary = redPrimaryLight,
    onPrimary = redOnPrimaryLight,
    primaryContainer = redPrimaryContainerLight,
    onPrimaryContainer = redOnPrimaryContainerLight,
    secondary = redSecondaryLight,
    onSecondary = redOnSecondaryLight,
    secondaryContainer = redSecondaryContainerLight,
    onSecondaryContainer = redOnSecondaryContainerLight,
    tertiary = redTertiaryLight,
    onTertiary = redOnTertiaryLight,
    tertiaryContainer = redTertiaryContainerLight,
    onTertiaryContainer = redOnTertiaryContainerLight,
    error = redErrorLight,
    onError = redOnErrorLight,
    errorContainer = redErrorContainerLight,
    onErrorContainer = redOnErrorContainerLight,
    background = redBackgroundLight,
    onBackground = redOnBackgroundLight,
    surface = redSurfaceLight,
    onSurface = redOnSurfaceLight,
    surfaceVariant = redSurfaceVariantLight,
    onSurfaceVariant = redOnSurfaceVariantLight,
    outline = redOutlineLight,
    outlineVariant = redOutlineVariantLight,
    scrim = redScrimLight,
    inverseSurface = redInverseSurfaceLight,
    inverseOnSurface = redInverseOnSurfaceLight,
    inversePrimary = redInversePrimaryLight,
    surfaceDim = redSurfaceDimLight,
    surfaceBright = redSurfaceBrightLight,
    surfaceContainerLowest = redSurfaceContainerLowestLight,
    surfaceContainerLow = redSurfaceContainerLowLight,
    surfaceContainer = redSurfaceContainerLight,
    surfaceContainerHigh = redSurfaceContainerHighLight,
    surfaceContainerHighest = redSurfaceContainerHighestLight,
)

private val redDarkScheme = darkColorScheme(
    primary = redPrimaryDark,
    onPrimary = redOnPrimaryDark,
    primaryContainer = redPrimaryContainerDark,
    onPrimaryContainer = redOnPrimaryContainerDark,
    secondary = redSecondaryDark,
    onSecondary = redOnSecondaryDark,
    secondaryContainer = redSecondaryContainerDark,
    onSecondaryContainer = redOnSecondaryContainerDark,
    tertiary = redTertiaryDark,
    onTertiary = redOnTertiaryDark,
    tertiaryContainer = redTertiaryContainerDark,
    onTertiaryContainer = redOnTertiaryContainerDark,
    error = redErrorDark,
    onError = redOnErrorDark,
    errorContainer = redErrorContainerDark,
    onErrorContainer = redOnErrorContainerDark,
    background = redBackgroundDark,
    onBackground = redOnBackgroundDark,
    surface = redSurfaceDark,
    onSurface = redOnSurfaceDark,
    surfaceVariant = redSurfaceVariantDark,
    onSurfaceVariant = redOnSurfaceVariantDark,
    outline = redOutlineDark,
    outlineVariant = redOutlineVariantDark,
    scrim = redScrimDark,
    inverseSurface = redInverseSurfaceDark,
    inverseOnSurface = redInverseOnSurfaceDark,
    inversePrimary = redInversePrimaryDark,
    surfaceDim = redSurfaceDimDark,
    surfaceBright = redSurfaceBrightDark,
    surfaceContainerLowest = redSurfaceContainerLowestDark,
    surfaceContainerLow = redSurfaceContainerLowDark,
    surfaceContainer = redSurfaceContainerDark,
    surfaceContainerHigh = redSurfaceContainerHighDark,
    surfaceContainerHighest = redSurfaceContainerHighestDark,
)

private val whiteLightScheme = lightColorScheme(
    primary = whitePrimaryLight,
    onPrimary = whiteOnPrimaryLight,
    primaryContainer = whitePrimaryContainerLight,
    onPrimaryContainer = whiteOnPrimaryContainerLight,
    secondary = whiteSecondaryLight,
    onSecondary = whiteOnSecondaryLight,
    secondaryContainer = whiteSecondaryContainerLight,
    onSecondaryContainer = whiteOnSecondaryContainerLight,
    tertiary = whiteTertiaryLight,
    onTertiary = whiteOnTertiaryLight,
    tertiaryContainer = whiteTertiaryContainerLight,
    onTertiaryContainer = whiteOnTertiaryContainerLight,
    error = whiteErrorLight,
    onError = whiteOnErrorLight,
    errorContainer = whiteErrorContainerLight,
    onErrorContainer = whiteOnErrorContainerLight,
    background = whiteBackgroundLight,
    onBackground = whiteOnBackgroundLight,
    surface = whiteSurfaceLight,
    onSurface = whiteOnSurfaceLight,
    surfaceVariant = whiteSurfaceVariantLight,
    onSurfaceVariant = whiteOnSurfaceVariantLight,
    outline = whiteOutlineLight,
    outlineVariant = whiteOutlineVariantLight,
    scrim = whiteScrimLight,
    inverseSurface = whiteInverseSurfaceLight,
    inverseOnSurface = whiteInverseOnSurfaceLight,
    inversePrimary = whiteInversePrimaryLight,
    surfaceDim = whiteSurfaceDimLight,
    surfaceBright = whiteSurfaceBrightLight,
    surfaceContainerLowest = whiteSurfaceContainerLowestLight,
    surfaceContainerLow = whiteSurfaceContainerLowLight,
    surfaceContainer = whiteSurfaceContainerLight,
    surfaceContainerHigh = whiteSurfaceContainerHighLight,
    surfaceContainerHighest = whiteSurfaceContainerHighestLight,
)

private val whiteDarkScheme = darkColorScheme(
    primary = whitePrimaryDark,
    onPrimary = whiteOnPrimaryDark,
    primaryContainer = whitePrimaryContainerDark,
    onPrimaryContainer = whiteOnPrimaryContainerDark,
    secondary = whiteSecondaryDark,
    onSecondary = whiteOnSecondaryDark,
    secondaryContainer = whiteSecondaryContainerDark,
    onSecondaryContainer = whiteOnSecondaryContainerDark,
    tertiary = whiteTertiaryDark,
    onTertiary = whiteOnTertiaryDark,
    tertiaryContainer = whiteTertiaryContainerDark,
    onTertiaryContainer = whiteOnTertiaryContainerDark,
    error = whiteErrorDark,
    onError = whiteOnErrorDark,
    errorContainer = whiteErrorContainerDark,
    onErrorContainer = whiteOnErrorContainerDark,
    background = whiteBackgroundDark,
    onBackground = whiteOnBackgroundDark,
    surface = whiteSurfaceDark,
    onSurface = whiteOnSurfaceDark,
    surfaceVariant = whiteSurfaceVariantDark,
    onSurfaceVariant = whiteOnSurfaceVariantDark,
    outline = whiteOutlineDark,
    outlineVariant = whiteOutlineVariantDark,
    scrim = whiteScrimDark,
    inverseSurface = whiteInverseSurfaceDark,
    inverseOnSurface = whiteInverseOnSurfaceDark,
    inversePrimary = whiteInversePrimaryDark,
    surfaceDim = whiteSurfaceDimDark,
    surfaceBright = whiteSurfaceBrightDark,
    surfaceContainerLowest = whiteSurfaceContainerLowestDark,
    surfaceContainerLow = whiteSurfaceContainerLowDark,
    surfaceContainer = whiteSurfaceContainerDark,
    surfaceContainerHigh = whiteSurfaceContainerHighDark,
    surfaceContainerHighest = whiteSurfaceContainerHighestDark,
)



@Composable
fun PixelixTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val prefs = LocalAppComponent.current.preferences
    val theme by prefs.appThemeModeFlow.collectAsState(prefs.appThemeMode)

    var nightModeValue = theme
    if (nightModeValue == FOLLOW_SYSTEM) {
        nightModeValue = if (isSystemInDarkTheme()) DARK else LIGHT
    }

    LaunchedEffect(nightModeValue) { applySystemNightMode(nightModeValue != LIGHT) }

    val accentColor by prefs.accentColorFlow.collectAsState(prefs.accentColor)

    val darkColorScheme = when (accentColor) {
        AppAccentColor.GREEN -> darkScheme
        AppAccentColor.RED -> redDarkScheme
        AppAccentColor.BLUE -> blueDarkScheme
        AppAccentColor.White -> whiteDarkScheme
        else -> darkScheme
    }

    val lightColorScheme = when (accentColor) {
        AppAccentColor.GREEN -> lightScheme
        AppAccentColor.RED -> redLightScheme
        AppAccentColor.BLUE -> blueLightScheme
        AppAccentColor.White -> whiteLightScheme
        else -> darkScheme
    }

    val colorScheme = generateColorScheme(nightModeValue, dynamicColor, lightColorScheme, darkColorScheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            Surface(modifier = Modifier.fillMaxSize(), content = content)
        }
    )
}

expect fun applySystemNightMode(isDark: Boolean)

@Composable
expect fun generateColorScheme(
    nightModeValue: Int,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme