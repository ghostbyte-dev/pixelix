package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_right
import pixelix.app.generated.resources.customize_app_icon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomizeAppIconPref(
    navController: NavController, closePreferenceDrawer: () -> Unit, logo: DrawableResource
) {
    SettingPref(leadingContent = {
        Image(
            painterResource(logo),
            contentDescription = null,
            modifier = Modifier.height(24.dp).width(24.dp).clip(
                CircleShape
            )
        )
    }, title = stringResource(Res.string.customize_app_icon), trailingContent = {
        Icon(
            imageVector = vectorResource(Res.drawable.chevron_right),
            contentDescription = null,
        )
    }, shapes = ListItemDefaults.segmentedShapes(index = 0, count = 5), onClick = {
        closePreferenceDrawer()
        navController.navigate(Destination.IconSelection)
    })
}