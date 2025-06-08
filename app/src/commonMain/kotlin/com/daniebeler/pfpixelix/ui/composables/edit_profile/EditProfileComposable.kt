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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
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
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bio
import pixelix.app.generated.resources.chevron_back_outline
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

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(Res.string.edit_profile),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.chevron_back_outline),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    if (viewModel.firstLoaded) {
                        if (viewModel.displayname == (viewModel.accountState.account?.displayname
                                ?: "") && viewModel.note == (viewModel.accountState.account?.note
                                ?: "") && "https://" + viewModel.website == (viewModel.accountState.account?.website
                                ?: "") && viewModel.newAvatar == null && viewModel.privateProfile == viewModel.accountState.account?.locked
                        ) {
                            if (!viewModel.accountState.isLoading) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.width(120.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
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
                })
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(state = rememberScrollState())
                    .imeAwareInsets(90.dp)
            ) {

                if (viewModel.accountState.account != null) {

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
                                modifier = Modifier
                                    .height(112.dp)
                                    .width(112.dp)
                                    .clip(CircleShape)
                                    .clickable { filePicker.launch() }
                            )
                        } else {
                            AsyncImage(
                                model = viewModel.avatarUri.toString(),
                                contentDescription = "",
                                modifier = Modifier
                                    .height(112.dp)
                                    .width(112.dp)
                                    .clip(CircleShape)
                                    .clickable { filePicker.launch() }
                            )
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
                        value = viewModel.displayname,
                        singleLine = true,
                        onValueChange = { viewModel.displayname = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
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
                        onValueChange = { viewModel.note = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.website),
                            fontWeight = FontWeight.Bold
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
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
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

                }

            }
        }

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
                    CenterAlignedTopAppBar(
                        title = {},
                        navigationIcon = {
                            androidx.compose.material.IconButton(onClick = { state.done(accept = false) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        },
                        actions = {
                            IconButton(onClick = { state.reset() }) {
                                Icon(Icons.Default.Refresh, null)
                            }
                            IconButton(
                                onClick = { state.done(accept = true) },
                                enabled = !state.accepted
                            ) {
                                Icon(Icons.Default.Done, null)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    CropperPreview(state = state, modifier = Modifier.fillMaxSize())
                    Box(Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                    ) {
                        DefaultControls(state)
                    }
                }
            }
        }
    }
}
