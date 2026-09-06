package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.initializePushNotifications
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_license
import pixelix.app.generated.resources.notifications

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PushDistributorPref() {
    val showAlert = remember { mutableStateOf(false) }
    val appComponent = LocalAppComponent.current
    val prefs = appComponent.preferences
    val distributor by prefs.pushDistributorFlow.collectAsState(prefs.pushDistributor)
    val activeUser = appComponent.authService.activeUser.collectAsState("none")

    if (showAlert.value) {
        PushDistributorPrefDialog(
            distributor = distributor, {
                prefs.pushDistributor = it
            }, onDismiss = {
                showAlert.value = false

                val capabilities = appComponent.authService.getCurrentCapabilities()
                if (capabilities.general.supportsPushNotifications) {
                    Logger.d(tag = "pushNotification") {
                        "active user: $activeUser"
                    }
                    activeUser.value?.let {
                        initializePushNotifications(
                            context = appComponent.context,
                            activeUser = it,
                            distributorPreference = appComponent.preferences.pushDistributor,
                            setDistributorPreference = { distributor ->
                                appComponent.preferences.pushDistributor = distributor
                            }
                        )
                    }
                }
            }, context = LocalAppComponent.current.context
        )
    }

    SettingPref(
        icon = Res.drawable.notifications,
        title = stringResource(Res.string.default_license),
        desc = distributor,
        trailingContent = null,
        onClick = { showAlert.value = true },
        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
    )
}

@Composable
expect fun PushDistributorPrefDialog(
    distributor: String,
    setDistributor: (distributor: String) -> Unit,
    onDismiss: () -> Unit,
    context: KmpContext
)