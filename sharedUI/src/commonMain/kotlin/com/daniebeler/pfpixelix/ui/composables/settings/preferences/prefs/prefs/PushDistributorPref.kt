package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import com.daniebeler.pfpixelix.utils.KmpContext
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_license
import pixelix.app.generated.resources.license
import pixelix.app.generated.resources.notifications
import pixelix.app.generated.resources.ok

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PushDistributorPref() {
    val showAlert = remember { mutableStateOf(false) }

    val prefs = LocalAppComponent.current.preferences
    val distributor by prefs.pushDistributorFlow.collectAsState(prefs.pushDistributor)

    if (showAlert.value) {
        PushDistributorPrefDialog(
            distributor = distributor, {
                prefs.pushDistributor = it
            }, onDismiss = {
                showAlert.value = false
            }, context = LocalAppComponent.current.context
        )
    }

    SettingPref(
        icon = Res.drawable.notifications,
        title = stringResource(Res.string.default_license),
        desc = distributor,
        trailingContent = null,
        onClick = { showAlert.value = true },
        shapes = ListItemDefaults.segmentedShapes(index = 2, count = 3),
    )
}

@Composable
expect fun PushDistributorPrefDialog(
    distributor: String,
    setDistributor: (distributor: String) -> Unit,
    onDismiss: () -> Unit,
    context: KmpContext
)