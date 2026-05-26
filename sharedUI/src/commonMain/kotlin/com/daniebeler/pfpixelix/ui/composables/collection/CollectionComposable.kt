package com.daniebeler.pfpixelix.ui.composables.collection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.composables.widgets.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add_circle
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.by
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.confirm
import pixelix.app.generated.resources.more_menu
import pixelix.app.generated.resources.heart
import pixelix.app.generated.resources.open_in_browser
import pixelix.app.generated.resources.open
import pixelix.app.generated.resources.edit
import pixelix.app.generated.resources.share
import pixelix.app.generated.resources.share_this_collection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionComposable(
    navController: NavController,
    collectionId: String,
    viewModel: CollectionViewModel = injectViewModel(key = "collection-viewmodel-key") { collectionViewModel }
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showAddPostBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddPostBottomSheet by remember { mutableStateOf(false) }

    val filteredPosts by remember(viewModel.editState.allPosts, viewModel.editState.editPosts) {
        derivedStateOf {
            viewModel.filterPostsExceptCollection(viewModel.editState.allPosts)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadData(collectionId)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight - 24.dp)
                .padding(paddingValues)
        ) {
            InfinitePostsList(
                contentPaddingTop = 24.dp,
                items = if (viewModel.editState.editMode) {
                    viewModel.editState.editPosts
                } else {
                    viewModel.collectionPostsState.posts
                },
                postsCount = viewModel.collectionPostsState.posts.count(),
                view = if (viewModel.editState.editMode) ViewEnum.Grid else viewModel.view,
                changeView = { viewModel.changeView(it) },
                isLoading = viewModel.collectionPostsState.isLoading,
                isRefreshing = viewModel.collectionPostsState.isRefreshing,
                error = viewModel.collectionPostsState.error,
                emptyMessage = EmptyState(
                    icon = vectorResource(Res.drawable.heart), heading = "Empty Collection"
                ),
                endReached = viewModel.collectionPostsState.endReached,
                itemGetsDeleted = {},
                postGetsUpdated = {},
                navController = navController,
                getItemsPaginated = {
                    viewModel.getPostsPaginated(false)
                },
                isFirstItemLarge = true,
                after = {
                    if (viewModel.editState.editMode) {
                        Spacer(Modifier.height(22.dp))
                        IconButton(onClick = {
                            showAddPostBottomSheet = true
                            viewModel.getAllPosts()
                        }) {
                            Icon(
                                vectorResource(Res.drawable.add_circle),
                                contentDescription = "",
                                Modifier.height(200.dp).width(200.dp)
                            )
                        }
                    }
                },
                onRefresh = {
                    viewModel.refresh()
                },
                edit = viewModel.editState.editMode,
                editRemove = { id -> viewModel.editRemove(id) })
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }, sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {

                    ButtonRowElement(
                        icon = Res.drawable.open, text = stringResource(
                            Res.string.open_in_browser
                        ), onClick = {
                            if (viewModel.collectionState.collection != null) {
                                viewModel.openUrl(viewModel.collectionState.collection!!.url)
                            }
                        })

                    ButtonRowElement(
                        icon = Res.drawable.share,
                        text = stringResource(Res.string.share_this_collection),
                        onClick = { viewModel.shareCollectionUrl() })
                }

            }

        }
        if (showAddPostBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddPostBottomSheet = false
                }, sheetState = showAddPostBottomSheetState
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {

                    InfinitePostsList(
                        items = filteredPosts,
                        isLoading = viewModel.editState.isAllPostsLoading,
                        isRefreshing = false,
                        error = viewModel.editState.errorAllPosts,
                        endReached = viewModel.editState.isAllPostsEndReached,
                        itemGetsDeleted = {},
                        postGetsUpdated = {},
                        navController = navController,
                        getItemsPaginated = {
                            viewModel.getPostsExceptCollectionPaginated()
                        },
                        onRefresh = {},
                        onClick = { viewModel.addPostToCollection(it) },
                        view = ViewEnum.Grid,
                        refreshable = false
                    )
                }
            }
        }

        ErrorComposableDialog(
            viewModel.editState.updateError,
            onDismiss = {
                viewModel.getCollection()
                viewModel.getPostsFirstLoad(true)
                viewModel.editState = viewModel.editState.copy(updateError = "")
            })



        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), title = {
                if (viewModel.collectionState.collection != null) {
                    if (viewModel.editState.editMode) {
                        TextField(
                            value = viewModel.editState.name,
                            singleLine = true,
                            onValueChange = {
                                viewModel.editState = viewModel.editState.copy(name = it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    } else {
                        Column {
                            Text(
                                viewModel.collectionState.collection!!.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                stringResource(
                                    Res.string.by, viewModel.collectionState.collection!!.username
                                ), fontSize = 12.sp, lineHeight = 6.sp
                            )
                        }
                    }
                }
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_left),
                        contentDescription = ""
                    )
                }
            }, actions = {
                if (viewModel.editState.editMode) {
                    TextButton(onClick = {
                        viewModel.toggleEditMode()
                    }) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(onClick = {
                        viewModel.confirmEdit()
                    }) {
                        Text(stringResource(Res.string.confirm))
                    }
                } else {

                    viewModel.collectionState.collection?.let {
                        if (it.username == viewModel.myUsername) {
                            IconButton(onClick = {
                                viewModel.toggleEditMode()
                            }) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.edit),
                                    contentDescription = ""
                                )
                            }
                        }
                    }

                    IconButton(onClick = {
                        //Navigate.navigate("settings_screen", navController)
                        showBottomSheet = true
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.more_menu),
                            contentDescription = ""
                        )
                    }
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
    }
}