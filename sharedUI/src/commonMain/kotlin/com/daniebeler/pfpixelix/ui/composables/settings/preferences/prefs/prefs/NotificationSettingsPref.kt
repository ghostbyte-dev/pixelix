package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_right
import pixelix.app.generated.resources.notification_settings
import pixelix.app.generated.resources.notifications

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationSettingsPref(
    navController: AppNavigator, closePreferenceDrawer: () -> Unit
) {
    SettingPref(
        icon = Res.drawable.notifications,
        title = stringResource(Res.string.notification_settings),
        trailingContent = {
            Icon(
                imageVector = vectorResource(Res.drawable.chevron_right),
                contentDescription = null,
            )
        },
        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 5),
        onClick = {
            closePreferenceDrawer()
            navController.navigate(Destination.NotificationSettings)
        })
}
