package com.daniebeler.pfpixelix.ui.composables.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_right
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.i_don_t_have_an_account
import pixelix.app.generated.resources.pixelfed_full_logo_black
import pixelix.app.generated.resources.pixelfed_full_logo_white
import pixelix.app.generated.resources.pixelix_logo_black_xxl
import pixelix.app.generated.resources.pixelix_logo_white_xxl
import pixelix.app.generated.resources.server_url
import pixelix.app.generated.resources.vernissage_full_logo_black
import pixelix.app.generated.resources.vernissage_full_logo_white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginComposable(
    isCloseable: Boolean = false,
    navController: NavController,
    viewModel: LoginViewModel = injectViewModel("LoginViewModel") { loginViewModel }
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5
    val suggestionsState by viewModel.serversSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = true,
        onBackCompleted = {
            if (viewModel.currentStep == LoginStep.SERVER_INPUT) {
                viewModel.goBackToPlatformSelection()
            } else {
                viewModel.onClose()
                navController.popBackStack()
            }
        })
    Box(Modifier.imePadding().fillMaxSize()) {
        Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)) { innerPadding ->
            Column(Modifier.imePadding().fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(paddingValues = innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                    ) {
                        if (isCloseable) {
                            IconButton(
                                onClick = {
                                    viewModel.onClose()
                                    navController.popBackStack()
                                },
                                modifier = Modifier.clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.close),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    if (!isCloseable) {
                        Spacer(Modifier.height(48.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            painter = painterResource(
                                if (dark) {
                                    Res.drawable.pixelix_logo_black_xxl
                                } else {
                                    Res.drawable.pixelix_logo_white_xxl
                                }
                            ),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Welcome to Pixelix", style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth().clip(
                                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            ).background(MaterialTheme.colorScheme.background)

                        ) {

                            Spacer(modifier = Modifier.height(16.dp))

                            viewModel.error?.let { err ->
                                if (err.isNotBlank()) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = err)
                                    }
                                }
                            }

                            when (viewModel.currentStep) {
                                LoginStep.PLATFORM_SELECTION -> {
                                    PlatformSelectionLayout(
                                        onPlatformSelected = { platform ->
                                            viewModel.selectPlatform(platform)
                                        })
                                }

                                LoginStep.SERVER_INPUT -> {
                                    ServerInputLayout(
                                        viewModel = viewModel
                                    )
                                }

                            }

                        }
                    }
                }
            }

        }
        if (viewModel.serversSuggestionsManager.suggestionsOpen) {

            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                SuggestionsBar(
                    state = suggestionsState,
                    bottomBarPadding = false,
                    onSelected = { selected ->
                        viewModel.selectSuggestion(
                            viewModel.serversSuggestionsManager.selectSuggestion(
                                selected
                            )
                        )
                    })
            }
        }
    }


}


@Composable
fun ServerInputLayout(
    viewModel: LoginViewModel
) {
    Column(Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Platform: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = viewModel.selectedPlatform?.name?.lowercase()
                        ?.replaceFirstChar { it.uppercase() } ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            TextButton(
                onClick = { viewModel.goBackToPlatformSelection() },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Change", fontSize = 14.sp
                )
            }
        }

        Row {
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(Res.string.server_url), fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        fun login() {
            viewModel.serversSuggestionsManager.onFocusChanged(false)
            keyboardController?.hide()
            focusManager.clearFocus()
            viewModel.auth()
        }

        Row(verticalAlignment = Alignment.Bottom) {
            TextField(
                value = viewModel.serverHost,
                onValueChange = { viewModel.updateServerHost(it) },
                prefix = { Text("https://") },
                singleLine = true,
                modifier = Modifier.weight(1f).onFocusChanged { focusState ->
                    viewModel.serversSuggestionsManager.onFocusChanged(focusState.isFocused)
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { login() })
            )

            Spacer(Modifier.width(12.dp))

            if (viewModel.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(56.dp).width(56.dp).clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    LoadingComposable(
                        modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Button(
                    onClick = { login() },
                    Modifier.height(56.dp).width(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(12.dp),
                    enabled = viewModel.isValidHost,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.chevron_right),
                        contentDescription = "submit",
                        Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        TextButton(onClick = { viewModel.showAvailableServers() }) {
            Text(
                stringResource(Res.string.i_don_t_have_an_account),
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PlatformSelectionLayout(
    onPlatformSelected: (BackendType) -> Unit, modifier: Modifier = Modifier
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5

    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose your platform",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Select the Fediverse service you want to connect with.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        PlatformCard(
            title = "Pixelfed",
            description = "The classic fediverse photo app. Share your daily moments, create photo albums, and connect privately with direct messages.",
            image = if (dark) Res.drawable.pixelfed_full_logo_white else Res.drawable.pixelfed_full_logo_black,
            onClick = { onPlatformSelected(BackendType.PIXELFED) })

        Spacer(modifier = Modifier.height(16.dp))

        PlatformCard(
            title = "Vernissage",
            description = "Built for photographers first. Publish and explore high-quality art with full EXIF metadata, HDR support, and account verification.",
            image = if (dark) Res.drawable.vernissage_full_logo_white else Res.drawable.vernissage_full_logo_black,
            onClick = { onPlatformSelected(BackendType.VERNISSAGE) })
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PlatformCard(
    title: String, description: String, image: DrawableResource, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), onClick = {
            onClick()
        }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}