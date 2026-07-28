package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.AMOLED
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.DARK
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.FOLLOW_SYSTEM
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.LIGHT
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ExpandOptionsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SwitchPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ValueOption
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.imageVectorIconBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.radioButtonBlock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.amoled
import pixelix.app.generated.resources.amoled_theme
import pixelix.app.generated.resources.app_theme
import pixelix.app.generated.resources.theme
import pixelix.app.generated.resources.dark_theme
import pixelix.app.generated.resources.device_theme
import pixelix.app.generated.resources.light_theme
import pixelix.app.generated.resources.theme_dark
import pixelix.app.generated.resources.theme_light
import pixelix.app.generated.resources.theme_system
import pixelix.app.generated.resources.use_dynamic_colors

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemePref() {
    val pref = LocalAppComponent.current.preferences
    val appTheme by pref.appThemeModeFlow.collectAsState(pref.appThemeMode)
    val useDynamicColors = remember { mutableStateOf(pref.useDynamicColors) }

    val onOptionClick = { mode: Int ->
        pref.appThemeMode = mode
    }

    LaunchedEffect(useDynamicColors.value) {
        pref.useDynamicColors = useDynamicColors.value
    }

    ExpandOptionsPref(
        leadingIcon = Res.drawable.theme,
        title = stringResource(Res.string.app_theme),
        index = 0,
        count = 2
    ) {
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 8),
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
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = 8),
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
            shapes = ListItemDefaults.segmentedShapes(index = 3, count = 8),
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
            shapes = ListItemDefaults.segmentedShapes(index = 4, count = 8),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.amoled_theme),
                contentDescription = stringResource(Res.string.amoled)
            ),
            title = stringResource(Res.string.amoled),
            trailingContent = radioButtonBlock(appTheme == AMOLED),
            value = AMOLED,
            onOptionClick = onOptionClick,
        )

        if (!PlatformFeatures.supportsDynamicColors) {
            Spacer(modifier = Modifier.height(1.dp))

            CustomAccentColorPref()
        } else {
            SwitchPref(
                icon = Res.drawable.theme,
                title = stringResource(Res.string.use_dynamic_colors),
                shapes = ListItemDefaults.segmentedShapes(index = 5, count = 7),
                state = useDynamicColors,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            )
            AnimatedVisibility(visible = !useDynamicColors.value) {
                CustomAccentColorPref()
            }
        }
    }
}