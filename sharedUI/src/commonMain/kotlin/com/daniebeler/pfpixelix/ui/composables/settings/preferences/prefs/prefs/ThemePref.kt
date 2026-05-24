package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.AMOLED
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.DARK
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.FOLLOW_SYSTEM
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.LIGHT
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ExpandOptionsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.OptionShapes
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ValueOption
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.imageVectorIconBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.radioButtonBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.CustomAccentColorPref
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.amoled
import pixelix.app.generated.resources.amoled_theme
import pixelix.app.generated.resources.app_theme
import pixelix.app.generated.resources.color_palette_outline
import pixelix.app.generated.resources.dark_theme
import pixelix.app.generated.resources.device_theme
import pixelix.app.generated.resources.light_theme
import pixelix.app.generated.resources.theme_dark
import pixelix.app.generated.resources.theme_light
import pixelix.app.generated.resources.theme_system

@Composable
fun ThemePref() {
    val pref = LocalAppComponent.current.preferences
    val appTheme by pref.appThemeModeFlow.collectAsState(pref.appThemeMode)

    val onOptionClick = { mode: Int ->
        pref.appThemeMode = mode
    }

    ExpandOptionsPref(
        leadingIcon = Res.drawable.color_palette_outline,
        title = stringResource(Res.string.app_theme),
    ) {
        ValueOption(
            shape = OptionShapes.indexOfShape(0, 3),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.device_theme),
                contentDescription = stringResource(Res.string.theme_system)
            ),
            title = stringResource(Res.string.theme_system),
            trailingContent = radioButtonBlock(appTheme == FOLLOW_SYSTEM),
            value = FOLLOW_SYSTEM,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shape = OptionShapes.indexOfShape(1, 3),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.light_theme),
                contentDescription = stringResource(Res.string.theme_light)
            ),
            title = stringResource(Res.string.theme_light),
            trailingContent = radioButtonBlock(appTheme == LIGHT),
            value = LIGHT,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shape = OptionShapes.indexOfShape(2, 3),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.dark_theme),
                contentDescription = stringResource(Res.string.theme_dark)
            ),
            title = stringResource(Res.string.theme_dark),
            trailingContent = radioButtonBlock(appTheme == DARK),
            value = DARK,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shape = OptionShapes.indexOfShape(2, 3),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.amoled_theme),
                contentDescription = stringResource(Res.string.amoled)
            ),
            title = stringResource(Res.string.amoled),
            trailingContent = radioButtonBlock(appTheme == AMOLED),
            value = AMOLED,
            onOptionClick = onOptionClick,
        )

        if (PlatformFeatures.customAccentColors) {
            Spacer(modifier = Modifier.height(1.dp))

             CustomAccentColorPref()
        }
    }
}