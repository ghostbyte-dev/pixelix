package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsComposable
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.getPlatformUriObject
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import com.daniebeler.pfpixelix.utils.toKmpUri
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add_outline
import pixelix.app.generated.resources.alt_text
import pixelix.app.generated.resources.audience
import pixelix.app.generated.resources.audience_public
import pixelix.app.generated.resources.browsers_outline
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.caption
import pixelix.app.generated.resources.content_warning_or_spoiler_text
import pixelix.app.generated.resources.followers_only
import pixelix.app.generated.resources.location
import pixelix.app.generated.resources.new_post
import pixelix.app.generated.resources.release
import pixelix.app.generated.resources.sensitive_nsfw_media
import pixelix.app.generated.resources.trash_outline
import pixelix.app.generated.resources.unlisted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostComposable(
    navController: NavController,
    uris: List<KmpUri>? = null,
    viewModel: NewPostViewModel = injectViewModel(key = "new-post-viewmodel-key") { newPostViewModel }
) {
    var expanded by remember { mutableStateOf(false) }
    var showReleaseAlert by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(uris) {
        uris?.let {
            uris.forEach { viewModel.addImage(uri = it) }
        }
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(Res.string.new_post), fontWeight = FontWeight.Bold)
        }, actions = {
            Button(
                onClick = { showReleaseAlert = true },
                enabled = (viewModel.images.isNotEmpty() && viewModel.images.none { it.isLoading })
            ) {
                Text(text = stringResource(Res.string.release))
            }
        })
    }) { paddingValues ->
        Box {
            Column(
                Modifier.padding(paddingValues).imeAwareInsets(90.dp).fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImagesPager(viewModel.images,
                    { index, altText ->
                        viewModel.updateAltTextVariable(
                            index, altText
                        )
                    },
                    { index -> viewModel.moveMediaAttachmentUp(index) },
                    { index -> viewModel.moveMediaAttachmentDown(index) },
                    { index -> viewModel.deleteMedia(index) },
                    { kmpUri: KmpUri -> viewModel.addImage(kmpUri) })

                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NewPostTextField(
                        value = viewModel.caption,
                        onChange = { viewModel.caption = it },
                        label = stringResource(Res.string.caption)
                    )
                    NewPostPref(leadingIcon = Res.drawable.browsers_outline,
                        title = stringResource(Res.string.sensitive_nsfw_media),
                        trailingContent = {
                            Switch(checked = viewModel.sensitive,
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
                    NewPostPref(leadingIcon = Res.drawable.browsers_outline,
                        title = stringResource(Res.string.audience),
                        trailingContent = {
                            Box {
                                OutlinedButton(onClick = { expanded = !expanded }) {
                                    val buttonText: String = when (viewModel.audience) {
                                        Visibility.PUBLIC -> stringResource(Res.string.audience_public)
                                        Visibility.UNLISTED -> stringResource(Res.string.unlisted)
                                        Visibility.PRIVATE -> stringResource(Res.string.followers_only)
                                        else -> ""
                                    }
                                    Text(text = buttonText)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(text = { Text(stringResource(Res.string.audience_public)) },
                                        onClick = { viewModel.audience = Visibility.PUBLIC },
                                        trailingIcon = {
                                            if (viewModel.audience == Visibility.PUBLIC) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        })
                                    DropdownMenuItem(text = { Text(stringResource(Res.string.unlisted)) },
                                        onClick = { viewModel.audience = Visibility.UNLISTED },
                                        trailingIcon = {
                                            if (viewModel.audience == Visibility.UNLISTED) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        })
                                    DropdownMenuItem(text = { Text(stringResource(Res.string.followers_only)) },
                                        onClick = { viewModel.audience = Visibility.PRIVATE },
                                        trailingIcon = {
                                            if (viewModel.audience == Visibility.PRIVATE) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        })
                                }


                            }
                        })
                    TextFieldLocationsComposable(
                        submit = { viewModel.setLocation(it) },
                        submitPlace = {},
                        initialValue = null,
                        labelStringId = Res.string.location,
                        modifier = Modifier.fillMaxWidth(),
                        imeAction = ImeAction.Default,
                        suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                        submitButton = null
                    )
                }
            }

            if (viewModel.addImageError.first.isNotBlank()) {
                AlertDialog(title = {
                    Text(text = viewModel.addImageError.first)
                }, text = {
                    Text(text = viewModel.addImageError.second)
                }, onDismissRequest = {
                    viewModel.addImageError = Pair("", "")
                }, confirmButton = {
                    TextButton(onClick = {
                        viewModel.addImageError = Pair("", "")
                    }) {
                        Text("Ok")
                    }
                })
            }

            if (showReleaseAlert) {
                AlertDialog(title = {
                    Text(text = "Are you sure?")
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

            LoadingComposable(isLoading = viewModel.createPostState.isLoading)
            //LoadingComposable(isLoading = viewModel.mediaUploadState.isLoading)
            ErrorComposableDialog(
                errorMessage = viewModel.mediaUploadState.error,
                onDismiss = { viewModel.mediaUploadState = viewModel.mediaUploadState.copy(error = "") }
            )

            ErrorComposableDialog(
                errorMessage = viewModel.createPostState.error,
                onDismiss = { viewModel.createPostState = viewModel.createPostState.copy(error = "") }
            )
        }
    }
}

@Composable
fun ImagesPager(
    images: List<NewPostViewModel.ImageItem>,
    updateAltText: (index: Int, altText: String) -> Unit,
    moveImageUp: (index: Int) -> Unit,
    moveImageDown: (index: Int) -> Unit,
    deleteMedia: (index: Int) -> Unit,
    addImage: (kmpUri: KmpUri) -> Unit
) {
    val pagerState = rememberPagerState { images.size + 1 }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 32.dp),
        pageSpacing = 10.dp,
        verticalAlignment = Alignment.Top
    ) { page ->
        if (page == images.size) {
            Column {
                Spacer(Modifier.height(48.dp))

                val launcher = rememberFilePickerLauncher(
                    type = FileKitType.ImageAndVideo, mode = FileKitMode.Multiple()
                ) { files ->
                    files?.forEach { file ->
                        addImage(file.toKmpUri())
                    }
                }

                Card(Modifier.fillMaxWidth().aspectRatio(1f).clickable { launcher.launch() }) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            modifier = Modifier.height(50.dp)
                                .width(50.dp),
                            imageVector = vectorResource(Res.drawable.add_outline),
                            contentDescription = null,
                        )
                    }
                }
            }
        } else {
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
                                imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                                contentDescription = "move Image upwards"
                            )
                        }
                    }
                    IconButton(onClick = {
                        deleteMedia(page)
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.trash_outline),
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
                                imageVector = Icons.AutoMirrored.Outlined.ArrowRight,
                                contentDescription = "move Image downwards"
                            )
                        }
                    }
                }
                Card(Modifier.fillMaxWidth().aspectRatio(1f)) {

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                        val type = image.mimeType

                        if (type.take(5) == "video") {
                            //todo KMP video
                            AsyncImage(
                                model = image.imageUri.getPlatformUriObject(),
                                contentDescription = "video thumbnail",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Inside
                            )
                        } else {
                            AsyncImage(
                                model = image.imageUri.getPlatformUriObject(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Inside
                            )
                        }
                        if (image.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                }
                TextField(
                    value = image.text,
                    onValueChange = { updateAltText(page, it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    label = { Text(stringResource(Res.string.alt_text)) },
                )
            }
        }
    }
}