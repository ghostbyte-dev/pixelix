package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import coil3.compose.AsyncImage
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.DefaultCropperStyle
import com.attafitamim.krop.core.crop.LocalCropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.core.images.ImageBitmapSrc
import com.attafitamim.krop.ui.CropperPreview
import com.attafitamim.krop.ui.DefaultControls
import com.daniebeler.pfpixelix.EdgeToEdgeDialogProperties
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.are_you_sure
import pixelix.app.generated.resources.bio
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.cancel_post_warning
import pixelix.app.generated.resources.cancel_profile_edit
import pixelix.app.generated.resources.caption
import pixelix.app.generated.resources.discard
import pixelix.app.generated.resources.displayname
import pixelix.app.generated.resources.edit_profile
import pixelix.app.generated.resources.private_profile
import pixelix.app.generated.resources.save
import pixelix.app.generated.resources.website

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileComposable(
    navController: NavController,
    viewModel: EditProfileViewModel = injectViewModel(key = "edit-profile-viewmodel-key") { editProfileViewModel }
) {
    val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()
    var isCancelAlertOpen by remember { mutableStateOf(false) }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = viewModel.isEdited,
        onBackCompleted = {
            isCancelAlertOpen = true
        })

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Column(
            Modifier.imeAwareInsets(60.dp).fillMaxSize()
        ) {
            Column(
                Modifier.padding(paddingValues)
                    .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight - 24.dp).weight(1f)
                    .padding(horizontal = 12.dp).verticalScroll(state = rememberScrollState())
            ) {

                if (viewModel.accountState.account != null) {

                    Spacer(Modifier.height(32.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        val coroutineScope = rememberCoroutineScope()
                        val imageCropper = rememberImageCropper()
                        val cropState = imageCropper.cropState
                        if (cropState != null) {
                            ImageCropperFullscreenDialog(cropState)
                        }

                        val filePicker = rememberFilePickerLauncher(
                            type = FileKitType.Image, mode = FileKitMode.Single
                        ) { file ->
                            file ?: return@rememberFilePickerLauncher
                            coroutineScope.launch {
                                val cropResult = imageCropper.crop {
                                    ImageBitmapSrc(file.readBytes().decodeToImageBitmap())
                                }
                                if (cropResult is CropResult.Success) {
                                    viewModel.newAvatar = cropResult.bitmap
                                }
                            }
                        }

                        val newAvatar = viewModel.newAvatar
                        if (newAvatar != null) {
                            Image(
                                bitmap = newAvatar,
                                contentDescription = "",
                                modifier = Modifier.height(112.dp).width(112.dp).clip(CircleShape)
                                    .clickable { filePicker.launch() })
                        } else {
                            AsyncImage(
                                model = viewModel.avatarUri.toString(),
                                contentDescription = "",
                                modifier = Modifier.height(112.dp).width(112.dp).clip(CircleShape)
                                    .clickable { filePicker.launch() })
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.displayname),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    TextField(
                        value = viewModel.displayName,
                        singleLine = true,
                        onValueChange = { viewModel.displayName = it },
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
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(text = stringResource(Res.string.bio), fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(6.dp))

                    TextField(
                        value = viewModel.note,
                        onValueChange = { viewModel.updateNote(it) },
                        modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                            viewModel.hashtagMentionsSuggestionsManager.onFocusChanged(focusState.isFocused)
                        },
                        label = { Text(stringResource(Res.string.caption)) },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                4.dp
                            ),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                4.dp
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.website), fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    TextField(
                        value = viewModel.website,
                        singleLine = true,
                        prefix = {
                            Text(text = "https://")
                        },
                        onValueChange = { viewModel.website = it },
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
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.private_profile),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = viewModel.privateProfile,
                            onCheckedChange = { viewModel.privateProfile = it })
                    }

                    Spacer(Modifier.height(24.dp))
                }

            }
            if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
                SuggestionsBar(
                    state = suggestionsState, bottomBarPadding = true, onSelected = { selected ->
                        viewModel.note =
                            viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                                selected, viewModel.note
                            )
                    })
            }

            if (isCancelAlertOpen) {
                AlertDialog(title = {
                    Text(text = stringResource(Res.string.are_you_sure))
                }, text = {
                    Text(text = stringResource(Res.string.cancel_profile_edit))
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
            ErrorComposableDialog(
                errorMessage = viewModel.accountState.error, onDismiss = {
                    viewModel.getAccount();
                })
        }

        TopAppBar(
            modifier = Modifier.clip(
            RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ), title = {
            Text(
                text = stringResource(Res.string.edit_profile),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }, navigationIcon = {
            IconButton(onClick = {
                if (viewModel.isEdited) {
                    isCancelAlertOpen = true
                } else {
                    navController.popBackStack()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                )
            }
        }, actions = {
            if (viewModel.firstLoaded) {
                if (!viewModel.isEdited) {
                    if (!viewModel.accountState.isLoading) {
                        Button(
                            onClick = {},
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(text = stringResource(Res.string.save))
                        }
                    }
                } else {
                    if (viewModel.accountState.isLoading) {
                        Button(
                            onClick = {},
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.save() },
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = stringResource(Res.string.save))
                        }
                    }
                }
            }
        }, colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCropperFullscreenDialog(
    state: CropState
) {
    val style = DefaultCropperStyle
    LaunchedEffect(Unit) {
        state.setInitialState(style)
        state.aspectLock = true
    }

    CompositionLocalProvider(LocalCropperStyle provides style) {
        Dialog(
            onDismissRequest = { state.done(accept = false) },
            properties = EdgeToEdgeDialogProperties()
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
                topBar = {
                    TopAppBar(
                        title = {}, navigationIcon = {
                        androidx.compose.material.IconButton(onClick = { state.done(accept = false) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }, actions = {
                        IconButton(onClick = { state.reset() }) {
                            Icon(Icons.Default.Refresh, null)
                        }
                        IconButton(
                            onClick = { state.done(accept = true) }, enabled = !state.accepted
                        ) {
                            Icon(Icons.Default.Done, null)
                        }
                    }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                    )
                }) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    CropperPreview(state = state, modifier = Modifier.fillMaxSize())
                    Box(
                        Modifier.fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                    ) {
                        DefaultControls(state)
                    }
                }
            }
        }
    }
}
