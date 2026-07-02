package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
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
import pixelix.app.generated.resources.add
import pixelix.app.generated.resources.alt_text
import pixelix.app.generated.resources.new_post
import pixelix.app.generated.resources.release

@OptIn(ExperimentalMaterial3Api::class)
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
            uris.forEach { viewModel.addImage(uri = it) }
        }
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { viewModel.images.size + 1 })
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.new_post),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }, actions = {

                    val launcher = rememberFilePickerLauncher(
                        type = FileKitType.ImageAndVideo, mode = FileKitMode.Multiple()
                    ) { files ->
                        files?.forEach { file ->
                            viewModel.addImage(file.toKmpUri())
                        }
                    }
                    if (viewModel.images.size != pagerState.currentPage) {
                        Button(
                            onClick = { launcher.launch() },
                        ) {
                            Text(text = "add image")
                        }
                    } else {
                        Button(
                            onClick = { showReleaseAlert = true },
                            enabled = (viewModel.images.isNotEmpty() && viewModel.images.none { it.isLoading } && viewModel.caption.text.length <= (viewModel.instance?.configuration?.statusConfig?.maxCharacters
                                ?: Int.MAX_VALUE))) {
                            Text(text = stringResource(Res.string.release))
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            val navigationBarPadding =
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            Column(Modifier.imeAwareInsets(60.dp)
                .padding(bottom = 60.dp + navigationBarPadding),
            ) {

                PrimaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                    if (viewModel.images.isEmpty()) {
                        Tab(
                            text = { Text("image 1") },
                            selected = pagerState.currentPage == 0,
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            })
                    } else {
                        viewModel.images.forEachIndexed { index, image ->
                            Tab(
                                text = { Text("image ${index + 1}") },
                                selected = pagerState.currentPage == index,
                                selectedContentColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                })
                        }

                        Tab(
                            text = { Text("General") },
                            selected = pagerState.currentPage == viewModel.images.size,
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(viewModel.images.size)
                                }
                            })
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 2,
                    modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.background)
                ) { tabIndex ->
                    if (viewModel.images.isEmpty()) {
                        EmptyImageTab { viewModel.addImage(it) }
                    } else {
                        if (tabIndex < viewModel.images.size) {
                            ImageTab(viewModel.images[tabIndex], {viewModel.updateImageMetadata(tabIndex, it)})
                        } else {
                            Text("general")
                        }
                    }
                }
                /* var isExpandedVisibility by remember { mutableStateOf(false) }
                 var showReleaseAlert by remember {
                     mutableStateOf(false)
                 }
                 var isCancelAlertOpen by remember { mutableStateOf(false) }
                 val scope = rememberCoroutineScope()
                 LaunchedEffect(uris) {
                     uris?.let {
                         uris.forEach { viewModel.addImage(uri = it) }
                     }
                 }

                 if (viewModel.capabilities.general.supportsPosting) {


                     NavigationBackHandler(
                         state = rememberNavigationEventState(NavigationEventInfo.None),
                         isBackEnabled = viewModel.images.isNotEmpty() || viewModel.caption.text.isNotBlank() || viewModel.caption.text.isNotBlank() || viewModel.locationId.isNotBlank(),
                         onBackCompleted = {
                             isCancelAlertOpen = true
                         })

                     val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()
                     Box(modifier = Modifier.fillMaxSize()) {

                         val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                         val navigationBarPadding =
                             WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

                         Box(
                             modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                                 .fillMaxSize()
                         ) {
                             Box {
                                 Column(
                                     Modifier.imeAwareInsets(60.dp).fillMaxSize(),
                                 ) {
                                     Column(
                                         Modifier.weight(1f).verticalScroll(rememberScrollState())
                                             .padding(bottom = 60.dp + navigationBarPadding),
                                         verticalArrangement = Arrangement.spacedBy(16.dp)
                                     ) {
                                         Spacer(Modifier.height(0.dp))

                                         ImagesPager(
                                             viewModel.images,
                                             { index, altText ->
                                                 viewModel.updateAltTextVariable(
                                                     index, altText
                                                 )
                                             },
                                             { index -> viewModel.moveMediaAttachmentUp(index) },
                                             { index -> viewModel.moveMediaAttachmentDown(index) },
                                             { index -> viewModel.deleteMedia(index) },
                                             { kmpUri: KmpUri -> viewModel.addImage(kmpUri) })

                                         Column(
                                             Modifier.padding(12.dp),
                                             verticalArrangement = Arrangement.spacedBy(10.dp)
                                         ) {
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
                                             NewPostPref(
                                                 leadingIcon = Res.drawable.audience,
                                                 title = stringResource(Res.string.audience),
                                                 trailingContent = {
                                                     Box {
                                                         OutlinedButton(onClick = {
                                                             isExpandedVisibility = !isExpandedVisibility
                                                         }) {
                                                             val buttonText: String = when (viewModel.audience) {
                                                                 Visibility.PUBLIC -> stringResource(Res.string.audience_public)
                                                                 Visibility.UNLISTED -> stringResource(Res.string.unlisted)
                                                                 Visibility.PRIVATE -> stringResource(Res.string.followers_only)
                                                                 else -> ""
                                                             }
                                                             Text(text = buttonText)
                                                         }
                                                         DropdownMenu(
                                                             expanded = isExpandedVisibility,
                                                             onDismissRequest = {
                                                                 isExpandedVisibility = false
                                                             }) {
                                                             if (!(viewModel.accountState.account?.locked
                                                                     ?: false)
                                                             ) {
                                                                 DropdownMenuItem(
                                                                     text = { Text(stringResource(Res.string.audience_public)) },
                                                                     onClick = {
                                                                         viewModel.audience = Visibility.PUBLIC
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
                                     if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
                                         SuggestionsBar(
                                             state = suggestionsState,
                                             bottomBarPadding = true,
                                             onSelected = { selected ->
                                                 viewModel.caption =
                                                     viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                                                         selected, viewModel.caption
                                                     )
                                             })

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
                                         null
                                     }, dismissButton = {
                                         null
                                     }, confirmButton = {
                                         null
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



                                 LoadingComposable(isLoading = viewModel.createPostState.isLoading)
                                 //LoadingComposable(isLoading = viewModel.mediaUploadState.isLoading)
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
                         TopAppBar(
                             modifier = Modifier.clip(
                                 RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                             ), title = {
                                 Text(
                                     text = stringResource(Res.string.new_post),
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 18.sp
                                 )
                             }, actions = {
                                 Button(
                                     onClick = { showReleaseAlert = true },
                                     enabled = (viewModel.images.isNotEmpty() && viewModel.images.none { it.isLoading } && viewModel.caption.text.length <= (viewModel.instance?.configuration?.statusConfig?.maxCharacters
                                         ?: Int.MAX_VALUE))) {
                                     Text(text = stringResource(Res.string.release))
                                 }
                             }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                                 containerColor = MaterialTheme.colorScheme.surfaceContainer
                             )
                         )
                     }
                 } else {
                     Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                         Text("Posting is not yet supported for Vernissage :(")
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
                                         modifier = Modifier.height(50.dp).width(50.dp),
                                         imageVector = vectorResource(Res.drawable.add),
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
                                             imageVector = vectorResource(Res.drawable.arrow_left),
                                             contentDescription = "move Image upwards"
                                         )
                                     }
                                 }
                                 IconButton(onClick = {
                                     deleteMedia(page)
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
                                         LoadingComposable()
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
                 }*/
            }
        }
    }
}

@Composable
fun EmptyImageTab(addImage: (KmpUri) -> Unit) {
    Column {
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
                    modifier = Modifier.height(50.dp).width(50.dp),
                    imageVector = vectorResource(Res.drawable.add),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun ImageTab(image: NewPostViewModel.ImageItem, updateMetadata: (MediaAttachmentMetadataRequest) -> Unit) {
    val verticalScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(verticalScrollState)
        ) {
            AsyncImage(
                model = image.imageUri.getPlatformUriObject(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Inside
            )

            TextField(
                value = image.metadata.description ?: "",
                onValueChange = { updateMetadata(
                    image.metadata.copy(description = it)
                ) },
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