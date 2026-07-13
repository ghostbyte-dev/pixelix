package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsComposable
import com.daniebeler.pfpixelix.utils.formatLocalizedOnlyDate
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.alt_text
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.arrow_right
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.location
import pixelix.app.generated.resources.trash
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun ImageTab(
    image: NewPostViewModel.ImageItem,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onDelete: () -> Unit,
    updateMetadata: (MediaAttachmentMetadataRequest) -> Unit,
    capabilities: Capabilities
) {
    val verticalScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(verticalScrollState).padding(bottom = 24.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = image.imageUri.getPlatformUriObject(),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxWidth = 300.dp, maxHeight = 300.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMoveLeft,
                    enabled = canMoveLeft,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(vectorResource(Res.drawable.arrow_left), contentDescription = "Move Left")
                }

                IconButton(
                    onClick = onDelete, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(vectorResource(Res.drawable.trash), contentDescription = "Delete Image")
                }

                IconButton(
                    onClick = onMoveRight,
                    enabled = canMoveRight,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        vectorResource(Res.drawable.arrow_right), contentDescription = "Move Right"
                    )
                }
            }

            TextField(
                value = image.metadata.description ?: "",
                onValueChange = {
                    updateMetadata(
                        image.metadata.copy(description = it)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                label = { Text(text = stringResource(Res.string.alt_text)) })

            if (capabilities.newPost.showLocationInputInImageTab) {
                TextFieldLocationsComposable(
                    submit = {
                        updateMetadata(
                            image.metadata.copy(
                                locationId = it.id
                            )
                        )
                    },
                    initialValue = null,
                    labelStringId = Res.string.location,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Default,
                    suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                    submitButton = null
                )
            }

            if (capabilities.newPost.showMetadata) {
                IsIncludedField(
                    label = "Brand",
                    image.metadata.make.isIncluded,
                    { updateMetadata(image.metadata.copy(make = image.metadata.make.copy(isIncluded = it))) }) {
                    CustomTextField(
                        value = image.metadata.make,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(make = it)
                            )
                        },
                        label = "Brand",
                    )
                }
                IsIncludedField(
                    label = "Model",
                    image.metadata.model.isIncluded,
                    {
                        updateMetadata(
                            image.metadata.copy(
                                model = image.metadata.model.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.model,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(model = it)
                            )
                        },
                        label = "Model",
                    )
                }
                IsIncludedField(
                    label = "Flash",
                    image.metadata.flash.isIncluded,
                    {
                        updateMetadata(
                            image.metadata.copy(
                                flash = image.metadata.flash.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.flash,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(flash = it)
                            )
                        },
                        label = "Flash",
                    )
                }


                IsIncludedField(
                    label = "Lens",
                    image.metadata.lens.isIncluded,
                    { updateMetadata(image.metadata.copy(lens = image.metadata.lens.copy(isIncluded = it))) }) {
                    CustomTextField(
                        value = image.metadata.lens,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(lens = it)
                            )
                        },
                        label = "Lens",
                    )
                }
                IsIncludedField(
                    label = "Focal length",
                    image.metadata.focalLength.isIncluded || image.metadata.focalLenIn35mmFilm.isIncluded,
                    {
                        updateMetadata(
                            image.metadata.copy(
                                focalLength = image.metadata.focalLength.copy(isIncluded = it),
                                focalLenIn35mmFilm = image.metadata.focalLenIn35mmFilm.copy(
                                    isIncluded = it
                                )
                            ),
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.focalLength, onValueChange = {
                            updateMetadata(
                                image.metadata.copy(focalLength = it)
                            )
                        }, label = "Focal length", modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        value = image.metadata.focalLenIn35mmFilm, onValueChange = {
                            updateMetadata(
                                image.metadata.copy(focalLenIn35mmFilm = it)
                            )
                        }, label = "Focal length 35 mm", modifier = Modifier.weight(1f)
                    )
                }
                IsIncludedField(
                    label = "Aperture", image.metadata.fNumber.isIncluded, {
                        updateMetadata(
                            image.metadata.copy(
                                fNumber = image.metadata.fNumber.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.fNumber,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(fNumber = it)
                            )
                        },
                        label = "Aperture",
                    )
                }

                IsIncludedField(
                    label = "Exposure time", image.metadata.exposureTime.isIncluded, {
                        updateMetadata(
                            image.metadata.copy(
                                exposureTime = image.metadata.exposureTime.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.exposureTime,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(exposureTime = it)
                            )
                        },
                        label = "Exposure time",
                    )
                }

                IsIncludedField(
                    label = "ISO", image.metadata.photographicSensitivity.isIncluded, {
                        updateMetadata(
                            image.metadata.copy(
                                photographicSensitivity = image.metadata.photographicSensitivity.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.photographicSensitivity,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(photographicSensitivity = it)
                            )
                        },
                        label = "ISO",
                    )
                }

                IsIncludedField(
                    label = "Software", image.metadata.software.isIncluded, {
                        updateMetadata(
                            image.metadata.copy(
                                software = image.metadata.software.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    CustomTextField(
                        value = image.metadata.software,
                        onValueChange = {
                            updateMetadata(
                                image.metadata.copy(software = it)
                            )
                        },
                        placeholder = "Software",
                    )
                }

                IsIncludedField(
                    "Creation date", image.metadata.createDate.isIncluded, {
                        updateMetadata(
                            image.metadata.copy(
                                createDate = image.metadata.createDate.copy(
                                    isIncluded = it
                                )
                            )
                        )
                    }) {
                    DatePickerFieldToModal(image.metadata.createDate, {
                        updateMetadata(
                            image.metadata.copy(createDate = image.metadata.createDate.copy(value = it))
                        )
                    }, modifier = Modifier.weight(1f))
                    TimePickerFieldToModal(
                        image.metadata.createDate,
                        modifier = Modifier.weight(1f),
                        onDateSelected = { hour, min ->
                            val currentInstant =
                                image.metadata.createDate.value ?: Clock.System.now()
                            val timeZone = TimeZone.currentSystemDefault()

                            val localDateTime = currentInstant.toLocalDateTime(timeZone)

                            val updatedLocalDateTime = LocalDateTime(
                                year = localDateTime.year,
                                month = localDateTime.month.number,
                                day = localDateTime.day,
                                hour = hour,
                                minute = min,
                                second = localDateTime.second,
                                nanosecond = localDateTime.nanosecond
                            )

                            val updatedInstant = updatedLocalDateTime.toInstant(timeZone)

                            updateMetadata(
                                image.metadata.copy(
                                    createDate = image.metadata.createDate.copy(
                                        value = updatedInstant
                                    )
                                )
                            )

                        })

                }

            }
        }
    }
}

@Composable
fun IsIncludedField(
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    content: @Composable (() -> Unit)
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = value, onCheckedChange = {
                    onValueChange(it)
                })
            Spacer(Modifier.width(2.dp))
            Text(
                text = label, fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(3.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            content()
        }

    }
}

@Composable
fun CustomTextField(
    value: FieldState<String>,
    onValueChange: (FieldState<String>) -> Unit,
    label: String = "",
    placeholder: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
    TextField(
        value = value.value ?: "",
        onValueChange = {
            onValueChange(
                value.copy(value = it)
            )
        },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        label = { if (label.isNotBlank()) Text(text = label) },
        placeholder = { if (placeholder.isNotBlank()) Text(text = placeholder) },
        enabled = value.isIncluded
    )
}

@Composable
fun DatePickerFieldToModal(
    date: FieldState<Instant>, onDateSelected: (Instant?) -> Unit, modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    TextField(
        value = formatLocalizedOnlyDate(date.value.toString()),
        onValueChange = { },
        label = { Text("Date") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(vectorResource(Res.drawable.datetime), contentDescription = "Select date")
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        readOnly = true,
        enabled = date.isIncluded,
        modifier = modifier.fillMaxWidth().pointerInput(date) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (upEvent != null) {
                    showModal = true
                }
            }
        })

    if (showModal) {
        DatePickerModal(onDateSelected = {
            onDateSelected(it)
        }, onDismiss = { showModal = false })
    }
}

@Composable
fun DatePickerModal(
    onDateSelected: (Instant?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis?.let {
                Instant.fromEpochMilliseconds(
                    it
                )
            })
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}


@Composable
fun TimePickerFieldToModal(
    date: FieldState<Instant>, onDateSelected: (Int, Int) -> Unit, modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    TextField(
        value = date.value?.let { instant ->
            val timeZone = TimeZone.currentSystemDefault()
            val localDateTime = instant.toLocalDateTime(timeZone)

            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')

            "$hour:$minute"
        } ?: "",
        onValueChange = { },
        label = { Text("Time") },
        placeholder = { Text("HH:MM") },
        trailingIcon = {
            Icon(vectorResource(Res.drawable.datetime), contentDescription = "Select time")
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        readOnly = true,
        enabled = date.isIncluded,
        modifier = modifier.fillMaxWidth().pointerInput(date) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (upEvent != null) {
                    showModal = true
                }
            }
        })

    if (showModal) {
        TimePickerModal(initialInstant = date.value, onConfirm = { hour, min ->
            onDateSelected(hour, min)
            showModal = false
        }, onDismiss = { showModal = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    initialInstant: Instant?,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val timeZone = remember { TimeZone.currentSystemDefault() }
    val localDateTime = remember(initialInstant) {
        val targetInstant = initialInstant ?: Clock.System.now()
        targetInstant.toLocalDateTime(timeZone)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = localDateTime.hour,
        initialMinute = localDateTime.minute,
        is24Hour = true,
    )
    AlertDialog(onDismissRequest = onDismiss, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
            Text("OK")
        }
    }, text = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            TimePicker(state = timePickerState)
        }
    })
}