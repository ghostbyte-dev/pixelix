package com.daniebeler.pfpixelix.ui.composables.profile.other_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.profile.CollectionsComposable
import com.daniebeler.pfpixelix.ui.composables.profile.MutualFollowersComposable
import com.daniebeler.pfpixelix.ui.composables.profile.postsWrapperComposable
import com.daniebeler.pfpixelix.ui.composables.profile.ProfileTopSection
import com.daniebeler.pfpixelix.ui.composables.profile.SwitchViewComposable
import com.daniebeler.pfpixelix.ui.composables.profile.server_stats.DomainSoftwareComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.DomainFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.block
import pixelix.app.generated.resources.block_account
import pixelix.app.generated.resources.block_consequence_1
import pixelix.app.generated.resources.block_consequence_10
import pixelix.app.generated.resources.block_consequence_2
import pixelix.app.generated.resources.block_consequence_3
import pixelix.app.generated.resources.block_consequence_4
import pixelix.app.generated.resources.block_consequence_5
import pixelix.app.generated.resources.block_consequence_6
import pixelix.app.generated.resources.block_consequence_7
import pixelix.app.generated.resources.block_consequence_8
import pixelix.app.generated.resources.block_consequence_9
import pixelix.app.generated.resources.block_this_profile
import pixelix.app.generated.resources.browser
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.more_menu
import pixelix.app.generated.resources.follow
import pixelix.app.generated.resources.message
import pixelix.app.generated.resources.mute
import pixelix.app.generated.resources.mute_account
import pixelix.app.generated.resources.mute_consequence_1
import pixelix.app.generated.resources.mute_consequence_2
import pixelix.app.generated.resources.mute_consequence_3
import pixelix.app.generated.resources.mute_consequence_4
import pixelix.app.generated.resources.mute_consequence_5
import pixelix.app.generated.resources.mute_this_profile
import pixelix.app.generated.resources.open_in_browser
import pixelix.app.generated.resources.photo
import pixelix.app.generated.resources.blocked
import pixelix.app.generated.resources.muted
import pixelix.app.generated.resources.share
import pixelix.app.generated.resources.share_this_profile
import pixelix.app.generated.resources.unblock_account
import pixelix.app.generated.resources.unblock_caps
import pixelix.app.generated.resources.unblock_this_profile
import pixelix.app.generated.resources.unfollow
import pixelix.app.generated.resources.unmute_account
import pixelix.app.generated.resources.unmute_caps
import pixelix.app.generated.resources.unmute_this_profile

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun OtherProfileComposable(
    navController: NavController,
    userId: String?,
    username: String?,
    viewModel: OtherProfileViewModel = injectViewModel(key = "other-profile$userId$username") { otherProfileViewModel }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val lazyGridState = rememberLazyStaggeredGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val photoIcon = vectorResource(Res.drawable.photo)

    var showBottomSheet by remember { mutableStateOf(false) }
    var showMuteAlert by remember { mutableStateOf(false) }
    var showUnMuteAlert by remember { mutableStateOf(false) }
    var showBlockAlert by remember { mutableStateOf(false) }
    var showUnBlockAlert by remember { mutableStateOf(false) }

    LaunchedEffect(userId, username) {
        viewModel.loadData(userId, username, false, navController)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior, title = {
                    Row {
                        Column {
                            Text(
                                text = viewModel.accountState.account?.username ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = DomainFormat.formatDomain(viewModel.domain),
                                fontSize = 12.sp,
                                lineHeight = 6.sp
                            )
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

                    if (viewModel.domain.isNotEmpty()) {
                        DomainSoftwareComposable(
                            domain = viewModel.domain
                        )
                    }

                    IconButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.more_menu),
                            contentDescription = ""
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            CustomPullToRefreshBox(
                isRefreshing = viewModel.accountState.refreshing || viewModel.postsState.refreshing,
                onRefresh = { viewModel.loadData(userId, username,true, navController) },
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {

                BoxWithConstraints {
                    val gridContentWidth = maxWidth - 8.dp
                    val gridColumnCount = maxOf(3, (gridContentWidth / 120.dp).toInt())
                    LazyVerticalStaggeredGrid(
                        columns = when (viewModel.view) {
                            com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum.Grid -> StaggeredGridCells.Fixed(
                                gridColumnCount
                            )

                            com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum.Masonry -> StaggeredGridCells.Adaptive(
                                150.dp
                            )

                            com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum.Timeline -> StaggeredGridCells.Adaptive(
                                350.dp
                            )
                        },
                        verticalItemSpacing = 4.dp,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        state = lazyGridState,
                        contentPadding = PaddingValues(bottom = 60.dp, start = 4.dp, end = 4.dp)
                    ) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column(
                                modifier = Modifier.layout { measurable, constraints ->
                                    val horizontalPadding = 4.dp.roundToPx()

                                    val expandedWidth =
                                        constraints.maxWidth + (horizontalPadding * 2)
                                    val placeable = measurable.measure(
                                        constraints.copy(
                                            maxWidth = expandedWidth, minWidth = expandedWidth
                                        )
                                    )
                                    layout(constraints.maxWidth, placeable.height) {
                                        placeable.placeRelative(-horizontalPadding, 0)
                                    }
                                }.fillMaxWidth().clip(
                                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                ).background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(bottom = 12.dp)
                            ) {
                                if (viewModel.accountState.account != null) {
                                    ProfileTopSection(
                                        account = viewModel.accountState.account,
                                        relationship = viewModel.relationshipState.accountRelationship,
                                        navController,
                                        openUrl = { url ->
                                            viewModel.openUrl(url)
                                        })
                                }

                                MutualFollowersComposable(
                                    mutualFollowersState = viewModel.mutualFollowersState,
                                    navController = navController
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                                ) {
                                    var containerColor by remember {
                                        mutableStateOf(Color(0xFFFFFFFF))
                                    }

                                    var contentColor by remember {
                                        mutableStateOf(Color(0xFFFFFFFF))
                                    }

                                    if (viewModel.relationshipState.accountRelationship?.following == true) {
                                        containerColor =
                                            MaterialTheme.colorScheme.secondaryContainer
                                        contentColor =
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        containerColor = MaterialTheme.colorScheme.primary
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    }

                                    //TODO: show state request (if user has setting follow request, if trying to follow, state requested is returned
                                    Button(
                                        onClick = {
                                            if (!viewModel.relationshipState.isLoading && viewModel.relationshipState.accountRelationship != null) {
                                                if (viewModel.relationshipState.accountRelationship?.following == true) {
                                                    viewModel.unfollowAccount()
                                                } else {
                                                    viewModel.followAccount()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = containerColor,
                                            contentColor = contentColor
                                        )
                                    ) {
                                        if (viewModel.relationshipState.isLoading) {
                                            LoadingComposable(
                                                modifier = Modifier.size(20.dp),
                                                color = contentColor
                                            )
                                        } else {
                                            if (viewModel.relationshipState.accountRelationship?.following == true) {
                                                Text(text = stringResource(Res.string.unfollow))
                                            } else {
                                                Text(text = stringResource(Res.string.follow))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Button(
                                        onClick = {
                                            viewModel.accountState.account?.let { account ->
                                                navController.navigate(Destination.Chat(account.id))
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Text(text = stringResource(Res.string.message))
                                    }
                                }

                                viewModel.accountState.account?.let { account ->
                                    CollectionsComposable(
                                        collectionsState = viewModel.collectionsState,
                                        getMoreCollections = {
                                            viewModel.getCollections(
                                                account.id, true
                                            )
                                        },
                                        navController = navController,
                                        instanceDomain = viewModel.domain,
                                        openUrl = { url -> viewModel.openUrl(url) })
                                }
                            }
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            SwitchViewComposable(
                                postsCount = viewModel.accountState.account?.postsCount ?: 0,
                                viewType = viewModel.view,
                                onViewChange = {
                                    viewModel.changeView(it)
                                })
                        }

                        postsWrapperComposable(
                            posts = viewModel.postsState.posts,
                            isLoading = viewModel.postsState.isLoading,
                            isRefreshing = viewModel.accountState.refreshing || viewModel.postsState.refreshing,
                            endReached = viewModel.postsState.endReached,
                            view = viewModel.view,
                            postGetsDeleted = { viewModel.postGetsDeleted(it) },
                            updatePost = { viewModel.updatePost(it) },
                            isFirstImageLarge = true,
                            gridColumnCount = gridColumnCount,
                            gridContentWidth = gridContentWidth,
                            navController = navController
                        )
                        if (viewModel.postsState.posts.isEmpty() && viewModel.postsState.error.isNotBlank()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                ErrorComposable(
                                    message = viewModel.postsState.error,
                                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                                )
                            }
                        }
                        if (viewModel.postsState.isLoading && viewModel.postsState.posts.isEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                LoadingComposable(viewModel.postsState.isLoading)
                            }
                        }
                        if (viewModel.postsState.posts.isEmpty() && !viewModel.postsState.isLoading && viewModel.postsState.error.isEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                EmptyStateComposable(
                                    emptyState = EmptyState(icon = photoIcon, heading = "No Posts")
                                )
                            }
                        }
                    }
                }
            }

            InfiniteStaggeredGridHandler(
                lazyStaggeredGridState = lazyGridState, itemCount = viewModel.postsState.posts.size
            ) {
                viewModel.getPostsPaginated(viewModel.userId)
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
                        if (viewModel.relationshipState.accountRelationship != null) {
                            if (viewModel.relationshipState.accountRelationship!!.muted) {
                                ButtonRowElement(
                                    icon = Res.drawable.muted, text = stringResource(
                                        Res.string.unmute_this_profile
                                    ), onClick = {
                                        showUnMuteAlert = true
                                    })
                            } else {
                                ButtonRowElement(
                                    icon = Res.drawable.muted, text = stringResource(
                                        Res.string.mute_this_profile
                                    ), onClick = {
                                        showMuteAlert = true
                                    }, color = MaterialTheme.colorScheme.error
                                )
                            }

                            if (viewModel.relationshipState.accountRelationship!!.blocked) {
                                ButtonRowElement(
                                    icon = Res.drawable.blocked, text = stringResource(
                                        Res.string.unblock_this_profile
                                    ), onClick = {
                                        showUnBlockAlert = true
                                    })
                            } else {
                                ButtonRowElement(
                                    icon = Res.drawable.blocked, text = stringResource(
                                        Res.string.block_this_profile
                                    ), onClick = {
                                        showBlockAlert = true
                                    }, color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        HorizontalDivider(Modifier.padding(12.dp))

                        ButtonRowElement(
                            icon = Res.drawable.browser, text = stringResource(
                                Res.string.open_in_browser
                            ), onClick = {
                                viewModel.openUrl(viewModel.accountState.account!!.url)
                            })

                        ButtonRowElement(
                            icon = Res.drawable.share,
                            text = stringResource(Res.string.share_this_profile),
                            onClick = {
                                viewModel.shareAccountUrl()
                            })
                    }
                }
            }

            if (showUnMuteAlert) {
                UnMuteAccountAlert(
                    onDismissRequest = { showUnMuteAlert = false },
                    onConfirmation = {
                        showUnMuteAlert = false
                        showBottomSheet = false
                        viewModel.unMuteAccount()
                    },
                    account = viewModel.accountState.account!!
                )
            }
            if (showMuteAlert) {
                MuteAccountAlert(
                    onDismissRequest = { showMuteAlert = false }, onConfirmation = {
                        showMuteAlert = false
                        showBottomSheet = false
                        viewModel.muteAccount()
                    }, account = viewModel.accountState.account!!
                )
            }
            if (showBlockAlert) {
                BlockAccountAlert(
                    onDismissRequest = { showBlockAlert = false }, onConfirmation = {
                        showBlockAlert = false
                        showBottomSheet = false
                        viewModel.blockAccount()
                    }, account = viewModel.accountState.account!!
                )
            }
            if (showUnBlockAlert) {
                UnBlockAccountAlert(
                    onDismissRequest = { showUnBlockAlert = false },
                    onConfirmation = {
                        showUnBlockAlert = false
                        showBottomSheet = false
                        viewModel.unblockAccount()
                    },
                    account = viewModel.accountState.account!!
                )
            }

            ErrorComposableDialog(
                errorMessage = viewModel.relationshipState.error, onDismiss = {
                    viewModel.relationshipState = viewModel.relationshipState.copy(error = "")
                    showBottomSheet = false
                })
            ErrorComposableDialog(
                viewModel.accountState.error, onDismiss = {
                    viewModel.accountState = viewModel.accountState.copy(error = "")
                })
        }
    }
}

@Composable
fun MuteAccountAlert(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, account: Account?
) {
    AlertDialog(title = {
        Text(text = stringResource(Res.string.mute_account))
    }, text = {
        Column {

            account?.let {
                AlertTopSection(account = account)
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
            }


            Text(text = stringResource(Res.string.mute_consequence_1))
            Text(text = stringResource(Res.string.mute_consequence_2))
            Text(text = stringResource(Res.string.mute_consequence_3))
            Text(text = stringResource(Res.string.mute_consequence_4))

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Text(text = stringResource(Res.string.mute_consequence_5))

        }
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation()
        }) {
            Text(stringResource(Res.string.mute))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(stringResource(Res.string.cancel))
        }
    })
}

@Composable
fun UnMuteAccountAlert(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, account: Account
) {
    AlertDialog(title = {
        Text(text = stringResource(Res.string.unmute_account))
    }, text = {
        AlertTopSection(account = account)


    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation()
        }) {
            Text(stringResource(Res.string.unmute_caps))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(stringResource(Res.string.cancel))
        }
    })
}

@Composable
fun BlockAccountAlert(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, account: Account?
) {
    AlertDialog(title = {
        Text(text = stringResource(Res.string.block_account))
    }, text = {
        Column {

            account?.let {
                AlertTopSection(account = account)
                HorizontalDivider(Modifier.padding(vertical = 12.dp))
            }


            Text(text = stringResource(Res.string.block_consequence_1))
            Text(text = stringResource(Res.string.block_consequence_2))
            Text(text = stringResource(Res.string.block_consequence_3))
            Text(text = stringResource(Res.string.block_consequence_4))
            Text(text = stringResource(Res.string.block_consequence_5))

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Text(text = stringResource(Res.string.block_consequence_6))
            Text(text = stringResource(Res.string.block_consequence_7))
            Text(text = stringResource(Res.string.block_consequence_8))
            Text(text = stringResource(Res.string.block_consequence_9))

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Text(text = stringResource(Res.string.block_consequence_10))
        }
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation()
        }) {
            Text(stringResource(Res.string.block))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(stringResource(Res.string.cancel))
        }
    })

}

@Composable
fun UnBlockAccountAlert(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, account: Account
) {
    AlertDialog(title = {
        Text(text = stringResource(Res.string.unblock_account))
    }, text = {
        AlertTopSection(account = account)
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation()
        }) {
            Text(stringResource(Res.string.unblock_caps))
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(stringResource(Res.string.cancel))
        }
    })
}

@Composable
fun AlertTopSection(account: Account) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = "",
            modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {

            Column {
                if (account.displayname != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.displayname,
                            lineHeight = 8.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.username,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = " • " + (account.url.substringAfter("https://")
                            .substringBefore("/")),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }
    }
}