package com.daniebeler.pfpixelix.ui.composables.newpost

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
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomLoader
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add
import pixelix.app.generated.resources.are_you_sure
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.arrow_right
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.cancel_post_warning
import pixelix.app.generated.resources.discard
import pixelix.app.generated.resources.new_post
import pixelix.app.generated.resources.ok
import pixelix.app.generated.resources.release

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewPostComposable(
    navController: NavController,
    uris: List<KmpUri>? = null,
    viewModel: NewPostViewModel = injectViewModel(key = "new-post-viewmodel-key") { newPostViewModel }
) {

    var showReleaseAlert by remember {
        mutableStateOf(false)
    }
    var isCancelAlertOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(uris) {
        uris?.let {
            uris.forEach {
                viewModel.addImage(
                    uri = it, metadata = MediaAttachmentMetadataRequest()
                )
            }
        }
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { viewModel.images.size + 1 })
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
                                text = "Back", style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (viewModel.images.isEmpty()) {
                                    navController.navigateUp()
                                } else {
                                    isCancelAlertOpen = true
                                }
                            },
                        ) {
                            Text(
                                text = "Cancel",
                                style = ButtonDefaults.textStyleFor(topBarButtonSize)
                            )
                        }
                    }
                }, title = {
                    Text(
                        text = stringResource(Res.string.new_post),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }, actions = {
                    if (viewModel.images.isNotEmpty() && !viewModel.isOnGeneralPage) {
                        Button(
                            contentPadding = ButtonDefaults.contentPaddingFor(
                                topBarButtonSize, hasEndIcon = true
                            ),
                            onClick = { viewModel.isOnGeneralPage = true },
                        ) {
                            Text("Next", style = ButtonDefaults.textStyleFor(topBarButtonSize))
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
                            onClick = { showReleaseAlert = true },
                        ) {
                            Text("Publish", style = ButtonDefaults.textStyleFor(topBarButtonSize))
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
                isBackEnabled = viewModel.images.isNotEmpty() || viewModel.caption.text.isNotBlank() || viewModel.caption.text.isNotBlank() || viewModel.locationId.isNotBlank(),
                onBackCompleted = {
                    isCancelAlertOpen = true
                })

            if (viewModel.isOnGeneralPage) {
                GeneralTab(viewModel, paddingValues)
            } else {
                Column(Modifier.padding(paddingValues)) {
                    if (viewModel.images.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            itemsIndexed(viewModel.images) { index, image ->
                                val isSelected = pagerState.currentPage == index

                                AsyncImage(
                                    model = image.imageUri.getPlatformUriObject(), // Use your actual uri property here
                                    contentDescription = "Thumbnail $index",
                                    contentScale = ContentScale.Crop,
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
                                        })
                            }

                            item {
                                val addImageTabIndex = viewModel.images.size
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
                                        imageVector = vectorResource(Res.drawable.add), // Change icon if needed
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
                        if (viewModel.images.isEmpty()) {
                            EmptyImageTab { file, metadata -> viewModel.addImage(file, metadata) }
                        } else {
                            if (tabIndex < viewModel.images.size) {
                                ImageTab(
                                    image = viewModel.images[tabIndex],
                                    canMoveLeft = tabIndex > 0,
                                    canMoveRight = tabIndex < viewModel.images.size - 1,
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
                                    capabilities = viewModel.capabilities,
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


        if (viewModel.addImageError.type == AddMediaErrorType.ERROR) {
            AlertDialog(title = {
                Text(text = viewModel.addImageError.title)
            }, text = {
                Text(text = viewModel.addImageError.description)
            }, onDismissRequest = {
                viewModel.addImageError = AddMediaError()
            }, confirmButton = {
                TextButton(onClick = {
                    viewModel.addImageError = AddMediaError()
                }) {
                    Text(stringResource(Res.string.ok))
                }
            })
        }

        if (viewModel.addImageError.type == AddMediaErrorType.TOO_BIG_MEDIA) {
            AlertDialog(title = {
                Text(text = viewModel.addImageError.title)
            }, text = {
                Text(text = viewModel.addImageError.description)
            }, onDismissRequest = {
                viewModel.addImageError = AddMediaError()
            }, dismissButton = {
                TextButton(onClick = {
                    viewModel.addImageError = AddMediaError()
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            }, confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.compressImage(viewModel.addImageError.uri)
                    }
                }) {
                    Text("Compress")
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
                    viewModel.post(navController)
                }) {
                    Text(stringResource(Res.string.release))
                }
            })
        }

        if (viewModel.compressionLoading) {
            AlertDialog(title = {
                Text(text = "Compressing...")
            }, text = {
                CustomLoader()
            }, onDismissRequest = {
            }, dismissButton = {
            }, confirmButton = {
            })
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
            errorMessage = viewModel.createPostState.error, onDismiss = {
                viewModel.createPostState = viewModel.createPostState.copy(error = "")
            })
    }
}
