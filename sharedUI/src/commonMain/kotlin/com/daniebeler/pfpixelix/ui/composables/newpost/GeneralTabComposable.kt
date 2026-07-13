package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.MaxLengthTextField
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.audience
import pixelix.app.generated.resources.audience_public
import pixelix.app.generated.resources.caption
import pixelix.app.generated.resources.category
import pixelix.app.generated.resources.chatbubble
import pixelix.app.generated.resources.confirm
import pixelix.app.generated.resources.content_warning_or_spoiler_text
import pixelix.app.generated.resources.disable_comments
import pixelix.app.generated.resources.eye_off
import pixelix.app.generated.resources.followers_only
import pixelix.app.generated.resources.globe
import pixelix.app.generated.resources.location
import pixelix.app.generated.resources.lock
import pixelix.app.generated.resources.mentioned_only
import pixelix.app.generated.resources.send
import pixelix.app.generated.resources.sensitive_content
import pixelix.app.generated.resources.sensitive_nsfw_media
import pixelix.app.generated.resources.tag
import pixelix.app.generated.resources.translation
import pixelix.app.generated.resources.unlisted


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralTab(
    viewModel: NewPostViewModel,
    paddingValues: PaddingValues
) {
    val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()
    val verticalScrollState = rememberScrollState()
    Column(Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(verticalScrollState).weight(1f)
                .padding(paddingValues).padding(all = 12.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                itemsIndexed(viewModel.images) { index, image ->
                    AsyncImage(
                        model = image.imageUri.getPlatformUriObject(),
                        contentDescription = "Thumbnail $index",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            MaxLengthTextField(
                value = viewModel.caption,
                onValueChange = { viewModel.updateCaption(it) },
                textFieldModifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    viewModel.hashtagMentionsSuggestionsManager.onFocusChanged(
                        focusState.isFocused
                    )
                },
                label = Res.string.caption,
                maxLength = viewModel.instance?.configuration?.statusConfig?.maxCharacters,
                minLines = 4,
                submit = {})

            NewPostPref(
                leadingIcon = Res.drawable.sensitive_content,
                title = stringResource(Res.string.sensitive_nsfw_media),
                trailingContent = {
                    Switch(
                        checked = viewModel.sensitive,
                        onCheckedChange = { viewModel.sensitive = it })
                })

            AnimatedVisibility(
                visible = viewModel.sensitive,
                enter = slideInVertically() + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
            ) {
                NewPostTextField(
                    value = viewModel.sensitiveText,
                    onChange = { viewModel.sensitiveText = it },
                    label = stringResource(Res.string.content_warning_or_spoiler_text)
                )
            }

            var isExpandedVisibility by remember { mutableStateOf(false) }

            val buttonText: String = when (viewModel.audience) {
                Visibility.PUBLIC -> stringResource(Res.string.audience_public)
                Visibility.UNLISTED -> stringResource(Res.string.unlisted)
                Visibility.PRIVATE -> stringResource(Res.string.followers_only)
                Visibility.DIRECT -> stringResource(Res.string.mentioned_only)
            }

            val buttonIcon: DrawableResource = when (viewModel.audience) {
                Visibility.PUBLIC -> Res.drawable.globe
                Visibility.UNLISTED -> Res.drawable.eye_off
                Visibility.PRIVATE -> Res.drawable.lock
                Visibility.DIRECT -> Res.drawable.send
            }

            ExposedDropdownMenuBox(
                expanded = isExpandedVisibility,
                onExpandedChange = { isExpandedVisibility = it }
            ) {
                TextField(
                    value = buttonText,
                    onValueChange = { text ->
                        isExpandedVisibility = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                    label = { Text(stringResource(Res.string.audience)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedVisibility)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = vectorResource(buttonIcon),
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
                    readOnly = true
                )

                ExposedDropdownMenu(
                    expanded = isExpandedVisibility, onDismissRequest = {
                        isExpandedVisibility = false
                    }) {
                    if (!(viewModel.accountState.account?.locked ?: false)) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.audience_public)) },
                            onClick = {
                                viewModel.audience = Visibility.PUBLIC
                                isExpandedVisibility = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.globe),
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (viewModel.audience == Visibility.PUBLIC) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.confirm),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.unlisted)) },
                            onClick = {
                                viewModel.audience = Visibility.UNLISTED
                                isExpandedVisibility = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.eye_off),
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (viewModel.audience == Visibility.UNLISTED) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.confirm),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            })
                    }
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.followers_only)) },
                        onClick = {
                            viewModel.audience = Visibility.PRIVATE
                            isExpandedVisibility = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.lock),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (viewModel.audience == Visibility.PRIVATE) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.confirm),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        })
                    if (viewModel.capabilities.newPost.includeDirectVisibility) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.mentioned_only)) },
                            onClick = {
                                viewModel.audience = Visibility.DIRECT
                                isExpandedVisibility = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.send),
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (viewModel.audience == Visibility.DIRECT) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.confirm),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            })
                    }
                }
            }

            if (viewModel.capabilities.newPost.showCategoriesDropdown) {
                var isCategoriesExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = isCategoriesExpanded,
                    onExpandedChange = { isCategoriesExpanded = it }
                ) {
                    TextField(
                        value = viewModel.categoriesState.selectedCategory?.name ?: "",
                        onValueChange = { text ->
                            isCategoriesExpanded = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                        label = { Text(stringResource(Res.string.category)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoriesExpanded)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.tag),
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
                        readOnly = true
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoriesExpanded, onDismissRequest = {
                            isCategoriesExpanded = false
                        }) {
                        viewModel.categoriesState.categories.forEach {
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                onClick = {
                                    viewModel.categoriesState = viewModel.categoriesState.copy(selectedCategory = it)
                                    isCategoriesExpanded = false
                                },
                                trailingIcon = {
                                    if (viewModel.categoriesState.selectedCategory?.id == it.id) {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.confirm),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                })
                        }
                    }
                }
            }


            NewPostPref(
                leadingIcon = Res.drawable.chatbubble,
                title = stringResource(Res.string.disable_comments),
                trailingContent = {
                    Switch(
                        checked = viewModel.disableComments,
                        onCheckedChange = { viewModel.disableComments = it })
                })

            if (viewModel.capabilities.newPost.showLocationInputInGeneral) {
                TextFieldLocationsComposable(
                    submit = {
                        viewModel.locationId = it.id
                    },
                    initialValue = null,
                    labelStringId = Res.string.location,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Default,
                    suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                    submitButton = null
                )
            }
        }
        if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
            SuggestionsBar(
                state = suggestionsState, bottomBarPadding = false, onSelected = { selected ->
                    viewModel.caption =
                        viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                            selected, viewModel.caption
                        )
                })
        }
    }

}