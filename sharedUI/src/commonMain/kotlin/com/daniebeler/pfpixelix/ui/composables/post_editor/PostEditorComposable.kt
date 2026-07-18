package com.daniebeler.pfpixelix.ui.composables.post_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomLoader
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import com.daniebeler.pfpixelix.utils.parseExifMetadata
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add
import pixelix.app.generated.resources.are_you_sure
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.arrow_right
import pixelix.app.generated.resources.back
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.cancel_post_warning
import pixelix.app.generated.resources.compress
import pixelix.app.generated.resources.compressing
import pixelix.app.generated.resources.discard
import pixelix.app.generated.resources.edit_images
import pixelix.app.generated.resources.edit_post
import pixelix.app.generated.resources.new_post
import pixelix.app.generated.resources.next
import pixelix.app.generated.resources.ok
import pixelix.app.generated.resources.publish
import pixelix.app.generated.resources.release
import pixelix.app.generated.resources.save

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostEditorComposable(
    navController: NavController,
    uris: List<KmpUri>? = null,
    viewModel: PostEditorViewModel = injectViewModel(key = "post-editor-viewmodel-key") { newPostViewModel }
) {

    var showReleaseAlert by remember {
        mutableStateOf(false)
    }
    var isCancelAlertOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(uris) {
        uris?.let {
            uris.forEach {
                scope.launch(Dispatchers.Default) {
                    try {
                        val file = PlatformFile(it)
                        val bytes = file.readBytes()

                        val extractedMetadata = parseExifMetadata(bytes)

                        withContext(Dispatchers.Main) {
                            viewModel.addImage(file.toKmpUri(), extractedMetadata)
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    val pagerState =
        rememberPagerState(initialPage = 0, pageCount = { viewModel.mediaItems.size + 1 })
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
            val topBarButtonSize = ButtonDefaults.ExtraSmallContainerHeight
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (viewModel.isOnGeneralPage) {
                        OutlinedButton(
                            contentPadding = ButtonDefaults.contentPaddingFor(
                                topBarButtonSize, hasStartIcon = true
                            ),
                            onClick = {
                                viewModel.isOnGeneralPage = false
                            },
                        ) {
                            Icon(
                                vectorResource(Res.drawable.arrow_left),
                                contentDescription = "",
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(topBarButtonSize)),
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(topBarButtonSize)))
                            Text(
                                text = (if (viewModel.mode == EditorMode.EDIT) stringResource(Res.string.edit_images) else stringResource(
                                    Res.string.back
                                )), style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (viewModel.mediaItems.isEmpty()) {
                                    navController.navigateUp()
                                } else {
                                    isCancelAlertOpen = true
                                }
                            },
                        ) {
                            Text(
                                text = stringResource(Res.string.cancel),
                                style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                        }
                    }
                }, title = {
                    Text(
                        text = if (viewModel.mode == EditorMode.EDIT) stringResource(Res.string.edit_post) else stringResource(
                            Res.string.new_post
                        ), fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                }, actions = {
                    if (viewModel.mediaItems.isNotEmpty() && !viewModel.isOnGeneralPage) {
                        Button(
                            contentPadding = ButtonDefaults.contentPaddingFor(
                                topBarButtonSize, hasEndIcon = true
                            ),
                            enabled = viewModel.mediaItems.all { !it.isLoading },
                            onClick = { viewModel.isOnGeneralPage = true },
                        ) {
                            Text(
                                stringResource(Res.string.next),
                                style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(topBarButtonSize)))
                            Icon(
                                vectorResource(Res.drawable.arrow_right),
                                contentDescription = "",
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(topBarButtonSize)),
                            )
                        }
                    }

                    if (viewModel.isOnGeneralPage) {
                        Button(
                            contentPadding = ButtonDefaults.contentPaddingFor(
                                topBarButtonSize
                            ),
                            enabled = viewModel.isEdited,
                            onClick = { showReleaseAlert = true },
                        ) {
                            Text(
                                text = if (viewModel.mode == EditorMode.EDIT) stringResource(Res.string.save) else stringResource(
                                    Res.string.publish
                                ), style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().imePadding()) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = viewModel.mediaItems.isNotEmpty() || viewModel.caption.text.isNotBlank() || viewModel.caption.text.isNotBlank() || viewModel.locationId.isNotBlank(),
                onBackCompleted = {
                    isCancelAlertOpen = true
                })

            if (viewModel.isOnGeneralPage) {
                GeneralTab(viewModel, paddingValues)
            } else {
                Column(Modifier.padding(paddingValues)) {
                    if (viewModel.mediaItems.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            itemsIndexed(viewModel.mediaItems) { index, image ->
                                val isSelected = pagerState.currentPage == index

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                                        .alpha(if (isSelected) 1f else 0.5f).then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    width = 3.dp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        ).clickable {
                                            scope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }) {
                                    AsyncImage(
                                        model = image.imageUri.getPlatformUriObject(),
                                        contentDescription = "Thumbnail $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (image.isLoading) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f))
                                        )

                                        LoadingComposable(
                                            size = 24.dp,
                                        )
                                    }
                                }
                            }

                            item {
                                val addImageTabIndex = viewModel.mediaItems.size
                                val isSelected = pagerState.currentPage == addImageTabIndex

                                Box(
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .alpha(if (isSelected) 1f else 0.5f).then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    width = 3.dp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        ).clickable {
                                            scope.launch {
                                                pagerState.animateScrollToPage(addImageTabIndex)
                                            }
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.add),
                                        contentDescription = "General Post Settings",
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 2,
                        modifier = Modifier.weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                    ) { tabIndex ->
                        if (viewModel.mediaItems.isEmpty()) {
                            EmptyImageTab { file, metadata -> viewModel.addImage(file, metadata) }
                        } else {
                            if (tabIndex < viewModel.mediaItems.size) {
                                ImageTab(
                                    image = viewModel.mediaItems[tabIndex],
                                    canMoveLeft = tabIndex > 0,
                                    canMoveRight = tabIndex < viewModel.mediaItems.size - 1,
                                    onMoveLeft = {
                                        viewModel.moveImage(tabIndex, tabIndex - 1)
                                        scope.launch { pagerState.animateScrollToPage(tabIndex - 1) }
                                    },
                                    onMoveRight = {
                                        viewModel.moveImage(tabIndex, tabIndex + 1)
                                        scope.launch { pagerState.animateScrollToPage(tabIndex + 1) }
                                    },
                                    onDelete = {
                                        viewModel.removeImage(tabIndex)
                                    },
                                    updateMetadata = {
                                        viewModel.updateImageMetadata(
                                            tabIndex, it
                                        )
                                    },
                                    capabilities = viewModel.capabilities.value,
                                    availableLicenses = viewModel.licensesState.licenses
                                )
                            } else {
                                EmptyImageTab { file, metadata ->
                                    viewModel.addImage(
                                        file, metadata
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        if (viewModel.mediaAdditionError.type == AddMediaErrorType.ERROR) {
            AlertDialog(title = {
                Text(text = viewModel.mediaAdditionError.title)
            }, text = {
                Text(text = viewModel.mediaAdditionError.description)
            }, onDismissRequest = {
                viewModel.mediaAdditionError = AddMediaError()
            }, confirmButton = {
                TextButton(onClick = {
                    viewModel.mediaAdditionError = AddMediaError()
                }) {
                    Text(stringResource(Res.string.ok))
                }
            })
        }

        if (viewModel.mediaAdditionError.type == AddMediaErrorType.TOO_BIG_MEDIA) {
            AlertDialog(title = {
                Text(text = viewModel.mediaAdditionError.title)
            }, text = {
                Text(text = viewModel.mediaAdditionError.description)
            }, onDismissRequest = {
                viewModel.mediaAdditionError = AddMediaError()
            }, dismissButton = {
                TextButton(onClick = {
                    viewModel.mediaAdditionError = AddMediaError()
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            }, confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.compressImage(viewModel.mediaAdditionError.uri)
                    }
                }) {
                    Text(stringResource(Res.string.compress))
                }
            })
        }

        if (showReleaseAlert) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.are_you_sure))
            }, onDismissRequest = {
                showReleaseAlert = false
            }, dismissButton = {
                TextButton(onClick = {
                    showReleaseAlert = false
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            }, confirmButton = {
                TextButton(onClick = {
                    showReleaseAlert = false
                    viewModel.submitPost(navController)
                }) {
                    Text(stringResource(Res.string.release))
                }
            })
        }

        if (viewModel.compressionLoading) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.compressing))
            }, text = {
                CustomLoader()
            }, onDismissRequest = {}, dismissButton = {}, confirmButton = {})
        }

        if (isCancelAlertOpen) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.are_you_sure))
            }, text = {
                Text(text = stringResource(Res.string.cancel_post_warning))
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
                    navController.navigateUp()
                }) {
                    Text(stringResource(Res.string.discard))
                }
            })
        }

        ErrorComposableDialog(
            errorMessage = viewModel.mediaUploadState.error, onDismiss = {
                viewModel.mediaUploadState = viewModel.mediaUploadState.copy(error = "")
            })

        ErrorComposableDialog(
            errorMessage = viewModel.postSubmissionState.error, onDismiss = {
                viewModel.postSubmissionState = viewModel.postSubmissionState.copy(error = "")
            })
    }
}
