package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.alt_text
import kotlin.time.Clock

@Composable
fun ImageTab(
    image: NewPostViewModel.ImageItem, updateMetadata: (MediaAttachmentMetadataRequest) -> Unit
) {
    val verticalScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(verticalScrollState)
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = image.imageUri.getPlatformUriObject(),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxWidth = 300.dp, maxHeight = 300.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Inside
                )
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
                { updateMetadata(image.metadata.copy(model = image.metadata.model.copy(isIncluded = it))) }) {
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
                { updateMetadata(image.metadata.copy(flash = image.metadata.flash.copy(isIncluded = it))) }) {
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
                            focalLenIn35mmFilm = image.metadata.focalLenIn35mmFilm.copy(isIncluded = it)
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
                    label = "Software",
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
                        val currentInstant = image.metadata.createDate.value ?: Clock.System.now()
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
                            image.metadata.copy(createDate = image.metadata.createDate.copy(value = updatedInstant))
                        )

                    })

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

        Row {
            content()
        }

    }
}