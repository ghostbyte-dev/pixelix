package com.daniebeler.pfpixelix.ui.composables.licences_dropdown

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.daniebeler.pfpixelix.domain.model.License
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.confirm
import pixelix.app.generated.resources.license
import pixelix.app.generated.resources.license_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesDropdownComposable(
    licenses: List<License>,
    selectedLicense: License?,
    onLicenseSelected: (License) -> Unit,
    modifier: Modifier = Modifier,
    textFieldColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        TextField(
            value = getLicenseName(selectedLicense),
            onValueChange = { isExpanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            label = { Text(stringResource(Res.string.license_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            leadingIcon = {
                Icon(imageVector = vectorResource(Res.drawable.license), contentDescription = null)
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor
            ),
            shape = MaterialTheme.shapes.medium,
            readOnly = true
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            licenses.forEach { license ->
                DropdownMenuItem(
                    text = { Text(getLicenseName(license)) },
                    onClick = {
                        onLicenseSelected(license)
                        isExpanded = false
                    },
                    trailingIcon = {
                        if (selectedLicense?.id == license.id) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.confirm),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun getLicenseName(license: License?): String {
    if (license == null){
        return ""
    }
    if (license.code.isNullOrBlank()) {
        return license.name ?: ""
    }

    return license.name + " (" + license.code + ")"
}