package com.daniebeler.pfpixelix.ui.composables.textfield_location

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.globe
import pixelix.app.generated.resources.location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldLocationsComposable(
    submit: (location: Location) -> Unit,
    initialValue: Location?,
    labelStringId: StringResource,
    submitButton: (@Composable () -> Unit)?,
    modifier: Modifier?,
    imeAction: ImeAction,
    suggestionsBoxColor: Color,
    viewModel: TextFieldLocationsViewModel = injectViewModel("textFieldLocationsViewModel") { textFieldLocationsViewModel }
) {

    LaunchedEffect(viewModel.locationsSuggestions.location) {
        viewModel.locationsSuggestions.location?.let { location ->
            submit(location)
        }
    }

    LaunchedEffect(initialValue) {
        initialValue?.let {
            viewModel.initializePlace(initialValue)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    if (viewModel.capabilities.newPost.showCountryDropdown) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = viewModel.countriesState.country?.name ?: viewModel.countryText,
                onValueChange = { text ->
                    viewModel.changeCountryText(text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                    .onFocusChanged { if (it.isFocused) expanded = true },
                label = { Text("Country") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                leadingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.globe),
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = MaterialTheme.shapes.medium,
                readOnly = false
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (viewModel.countriesState.isLoading) {
                    DropdownMenuItem(
                        text = { Text("Loading countries...") },
                        onClick = {},
                        enabled = false
                    )
                } else if (viewModel.countriesState.error.isNotEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Error: ${viewModel.countriesState.error}") },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    viewModel.countriesState.filteredCountries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country.name) },
                            onClick = {
                                viewModel.selectCountry(country)
                                expanded = false
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }


    ExposedDropdownMenuBox(
        expanded = viewModel.locationsDropdownOpen,
        onExpandedChange = { viewModel.locationsDropdownOpen = it }
    ) {
        TextField(
            value = viewModel.locationsSuggestions.location?.name ?: viewModel.locationText,
            onValueChange = { text ->
                viewModel.changeLocationText(text)
                viewModel.locationsDropdownOpen = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true),
            label = { Text("City") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.locationsDropdownOpen)
            },
            leadingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.location),
                    contentDescription = null
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = MaterialTheme.shapes.medium,
            readOnly = false
        )

        ExposedDropdownMenu(
            expanded = viewModel.locationsDropdownOpen,
            onDismissRequest = { viewModel.locationsDropdownOpen = false }
        ) {
            if (viewModel.locationsSuggestions.isLoading) {
                DropdownMenuItem(
                    text = { Text("Loading countries...") },
                    onClick = {},
                    enabled = false
                )
            } else if (viewModel.locationsSuggestions.error.isNotEmpty()) {
                DropdownMenuItem(
                    text = { Text("Error: ${viewModel.countriesState.error}") },
                    onClick = {},
                    enabled = false
                )
            } else {
                viewModel.locationsSuggestions.locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.name ?: "undefined") },
                        onClick = {
                            submit(location)
                            viewModel.clickLocation(location)
                            viewModel.locationsDropdownOpen = false
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }

    /*
    Column {
        if (viewModel.locationsSuggestions.location != null) {
            NewPostPref(
                leadingIcon = Res.drawable.browser,
                title = viewModel.locationsSuggestions.location!!.name!!,
                trailingContent = {
                    Row {
                        IconButton(onClick = {
                            viewModel.edit()
                            submit("")
                            submitPlace(null)
                        }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.edit),
                                contentDescription = "edit"
                            )
                        }
                        IconButton(onClick = {
                            viewModel.removeLocation()
                            submit("")
                            submitPlace(null)
                        }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.trash),
                                contentDescription = "remove"
                            )
                        }
                    }
                }
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = viewModel.text,
                    singleLine = false,
                    onValueChange = {
                        viewModel.changeText(it)
                    },
                    placeholder = { Text(stringResource(labelStringId)) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            4.dp
                        )
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = imeAction),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                )


                if (submitButton != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    submitButton()
                }
            }
        }
        if (viewModel.locationsDropdownOpen) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(suggestionsBoxColor)
                    .fillMaxWidth()
            ) {
                if (viewModel.locationsSuggestions.locations.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        viewModel.locationsSuggestions.locations.map {
                            TextButton(onClick = {
                                viewModel.clickLocation(it)
                                submit(it.id)
                                submitPlace(it)
                            }) {
                                Text(
                                    text = "${it.name ?: ""}, ${it.country}",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }*/
    ErrorComposableDialog(viewModel.countriesState.error, {
        viewModel.countriesState =
            CountriesState()
    })
    ErrorComposableDialog(viewModel.locationsSuggestions.error, {
        viewModel.locationsSuggestions =
            LocationsState()
    })
}