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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.ui.composables.licences_dropdown.LicensesDropdownComposable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_license
import pixelix.app.generated.resources.license
import pixelix.app.generated.resources.ok

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultLicensePref(
    viewModel: DefaultLicenseViewModel = injectViewModel("DefaultLicenseViewmodel") { defaultLicenseViewModel }
) {
    val showAlert = remember { mutableStateOf(false) }

    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(prefs.defaultLicense) }

    if (showAlert.value) {
        DefaultLicenseDialog(
            state.value,
            allLicenses = viewModel.licenses.value,
            isLoading = viewModel.isLoading,
            {
                state.value = it
                prefs.defaultLicense = it
            },
            {
                showAlert.value = false
            })
    }

    SettingPref(
        icon = Res.drawable.license,
        title = stringResource(Res.string.default_license),
        desc = state.value?.name ?: "No default license",
        trailingContent = null,
        onClick = { showAlert.value = true },
        shapes = ListItemDefaults.segmentedShapes(index = 2, count = 3),
    )
}

@Composable
fun DefaultLicenseDialog(
    license: License?,
    allLicenses: List<License>,
    isLoading: Boolean,
    onChange: (License?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().imePadding()
        ) {
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(24.dp).widthIn(max = 400.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.default_license),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LicensesDropdownComposable(
                        licenses = allLicenses,
                        selectedLicense = license,
                        isLoading = isLoading,
                        onLicenseSelected = onChange,
                        textFieldColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onDismiss()
                            }) { Text(stringResource(Res.string.ok)) }
                    }
                }
            }
        }
    }
}