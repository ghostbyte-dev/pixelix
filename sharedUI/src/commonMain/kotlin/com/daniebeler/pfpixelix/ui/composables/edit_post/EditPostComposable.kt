package com.daniebeler.pfpixelix.ui.composables.edit_post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.ui.composables.widgets.MaxLengthTextField
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsComposable
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.alt_text
import pixelix.app.generated.resources.are_you_sure
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.arrow_right
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.cancel_post_edit
import pixelix.app.generated.resources.caption
import pixelix.app.generated.resources.content_warning_or_spoiler_text
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.discard
import pixelix.app.generated.resources.edit_post
import pixelix.app.generated.resources.location
import pixelix.app.generated.resources.save
import pixelix.app.generated.resources.sensitive_nsfw_media
import pixelix.app.generated.resources.sure_update_post
import pixelix.app.generated.resources.trash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostComposable(
    postId: String,
    navController: NavController,
    viewModel: EditPostViewModel = injectViewModel("editPostViewModel") { editPostViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isCancelAlertOpen by remember { mutableStateOf(false) }

    var showSaveAlert by remember {
        mutableStateOf(false)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadData(postId)
    }
    val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = viewModel.isEdited,
        onBackCompleted = {
            isCancelAlertOpen = true
        })

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(Res.string.edit_post), fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.isEdited) {
                            isCancelAlertOpen = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_left),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    if (viewModel.editPostState.post != null) {
                        if (viewModel.isEdited && viewModel.caption.text.length <= (viewModel.instance?.configuration?.statusConfig?.maxCharacters
                                ?: Int.MAX_VALUE)
                        ) {
                            if (viewModel.editPostState.isLoading) {
                                Button(
                                    onClick = { }, modifier = Modifier.width(120.dp)
                                ) {
                                    LoadingComposable(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { showSaveAlert = true },
                                    modifier = Modifier.width(120.dp)
                                ) {
                                    Text(text = stringResource(Res.string.save))
                                }
                            }
                        } else {
                            Button(
                                onClick = { }, enabled = false, modifier = Modifier.width(120.dp)
                            ) {
                                Text(text = stringResource(Res.string.save))
                            }
                        }
                    }
                })
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Column(
                Modifier.imePadding().fillMaxSize()
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(12.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImagesPagerEditPost(
                        viewModel.mediaAttachmentsEdit,
                        viewModel.mediaDescriptionItems,
                        { mediaDescriptionIndex, altText ->

                            //val changed = it != (oldMediaAttachment!!.description ?: "")
                            viewModel.mediaDescriptionItems[mediaDescriptionIndex] =
                                viewModel.mediaDescriptionItems[mediaDescriptionIndex].copy(
                                    description = altText, changed = true
                                )
                        },
                        { index -> viewModel.moveMediaAttachmentUp(index) },
                        { index -> viewModel.moveMediaAttachmentDown(index) },
                        { index -> viewModel.deleteMedia(index) },
                    )
                    if (!viewModel.editPostState.isLoading && viewModel.editPostState.post != null) {
                        MaxLengthTextField(
                            value = viewModel.caption,
                            onValueChange = { viewModel.updateCaption(it) },
                            textFieldModifier = Modifier.fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    viewModel.hashtagMentionsSuggestionsManager.onFocusChanged(
                                        focusState.isFocused
                                    )
                                },
                            label = Res.string.caption,
                            maxLength = viewModel.instance?.configuration?.statusConfig?.maxCharacters,
                            submit = {}
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(Res.string.sensitive_nsfw_media))
                            Switch(
                                checked = viewModel.sensitive,
                                onCheckedChange = { viewModel.sensitive = it })
                        }
                        if (viewModel.sensitive) {
                            TextField(
                                value = viewModel.sensitiveText,
                                singleLine = false,
                                onValueChange = { viewModel.sensitiveText = it },
                                placeholder = { Text(stringResource(Res.string.content_warning_or_spoiler_text)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        4.dp
                                    ),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        4.dp
                                    )
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                })
                            )
                        }
                        TextFieldLocationsComposable(
                            submit = { viewModel._setLocation(it) },
                            initialValue = viewModel.editPostState.post!!.location,
                            labelStringId = Res.string.location,
                            modifier = Modifier.fillMaxWidth(),
                            imeAction = ImeAction.Default,
                            suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                            submitButton = null
                        )
                    }

                    LoadingComposable(isLoading = viewModel.editPostState.isLoading)
                    ErrorComposableDialog(
                        errorMessage = viewModel.editPostState.error,
                        onDismiss = {
                            viewModel.editPostState = viewModel.editPostState.copy(error = "")
                        }
                    )
                }
                if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
                    SuggestionsBar(
                        state = suggestionsState, bottomBarPadding = true, onSelected = { selected ->
                            viewModel.caption =
                                viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                                    selected, viewModel.caption
                                )
                        })
                }
            }

            if (showSaveAlert) {
                AlertDialog(title = {
                    Text(text = stringResource(Res.string.sure_update_post))
                }, onDismissRequest = {
                    showSaveAlert = false
                }, dismissButton = {
                    TextButton(onClick = {
                        showSaveAlert = false
                    }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }, confirmButton = {
                    TextButton(onClick = {
                        showSaveAlert = false
                        viewModel.updatePost(postId, navController)
                    }) {
                        Text(stringResource(Res.string.save))
                    }
                })
            }
        }
    }

    if (viewModel.deleteMediaDialog != null) {
        AlertDialog(icon = {
            Icon(imageVector = vectorResource(Res.drawable.trash), contentDescription = null)
        }, title = {
            Text(text = "Remove Media")
        }, text = {
            Text(text = "Are you sure you want to delete this media")
        }, onDismissRequest = {
            viewModel.deleteMediaDialog = null
        }, confirmButton = {
            TextButton(onClick = {
                viewModel.deleteMedia(viewModel.deleteMediaDialog!!)
            }) {
                Text(stringResource(Res.string.delete))
            }
        }, dismissButton = {
            TextButton(onClick = {
                viewModel.deleteMediaDialog = null
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }

    if (isCancelAlertOpen) {
        AlertDialog(
            title = {
                Text(text = stringResource(Res.string.are_you_sure))
            },
            text = {
                Text(text = stringResource(Res.string.cancel_post_edit))
            }, onDismissRequest = {
                isCancelAlertOpen = false
            }, dismissButton = {
                TextButton(onClick = {
                    isCancelAlertOpen = false
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            }, confirmButton = {
                TextButton(onClick = {
                    isCancelAlertOpen = false
                    navController.popBackStack()
                }) {
                    Text(stringResource(Res.string.discard))
                }
            })
    }
}


@Composable
fun ImagesPagerEditPost(
    images: List<MediaAttachment>,
    mediaDescriptionItems: List<EditPostViewModel.MediaDescriptionItem>,
    updateAltText: (index: Int, altText: String) -> Unit,
    moveImageUp: (index: Int) -> Unit,
    moveImageDown: (index: Int) -> Unit,
    deleteMedia: (mediaId: String) -> Unit,
) {
    val pagerState = rememberPagerState { images.size }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 32.dp),
        pageSpacing = 10.dp,
        verticalAlignment = Alignment.Top
    ) { page ->
        val image = images[page]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (page == 0) {
                    Box(Modifier.width(48.dp)) {}
                } else {
                    IconButton(onClick = {
                        moveImageUp(page)
                        scope.launch {
                            pagerState.animateScrollToPage(page = page - 1)
                        }
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_left),
                            contentDescription = "move Image upwards"
                        )
                    }
                }
                IconButton(onClick = {
                    deleteMedia(image.id)
                }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.trash),
                        contentDescription = "delete Image",
                        tint = MaterialTheme.colorScheme.error
                    )
                }


                if (page == images.size - 1) {
                    Box(Modifier.width(48.dp)) {}
                } else {
                    IconButton(onClick = {
                        moveImageDown(page)
                        scope.launch {
                            pagerState.animateScrollToPage(page = page + 1)
                        }
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_right),
                            contentDescription = "move Image downwards"
                        )
                    }
                }
            }
            Card(Modifier.fillMaxWidth().aspectRatio(1f)) {

                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                    val type = image.type

                    if (image.url != null) {
                        if (type?.take(5) == "video") {
                            //todo KMP video
                            AsyncImage(
                                model = image.url.toKmpUri().getPlatformUriObject(),
                                contentDescription = "video thumbnail",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            AsyncImage(
                                model = image.url.toKmpUri().getPlatformUriObject(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        LoadingComposable(
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
            val mediaDescriptionItem =
                mediaDescriptionItems.find { mediaDescriptionItem -> mediaDescriptionItem.imageId == image.id }
                    ?: EditPostViewModel.MediaDescriptionItem(
                        image.id, "", false
                    )
            val indexOfDescriptionItem =
                mediaDescriptionItems.indexOf(mediaDescriptionItem)
            TextField(
                value = mediaDescriptionItem.description,
                onValueChange = { updateAltText(indexOfDescriptionItem, it) },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                ),
                label = { Text(stringResource(Res.string.alt_text)) },
            )
        }
    }
}