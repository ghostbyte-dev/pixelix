package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.profile.CollectionsComposable
import com.daniebeler.pfpixelix.ui.composables.profile.ProfileTopSection
import com.daniebeler.pfpixelix.ui.composables.profile.server_stats.DomainSoftwareComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.DomainFormat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.edit_profile
import pixelix.app.generated.resources.more_menu
import pixelix.app.generated.resources.user_switch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnProfileComposable(
    navController: NavController,
    openPreferencesDrawer: () -> Unit,
    viewModel: OwnProfileViewModel = injectViewModel(key = "own-profile-key") { ownProfileViewModel }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(0) }

    val lazyGridState = rememberLazyStaggeredGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior, title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.clickable { showBottomSheet = 2 }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.user_switch),
                            contentDescription = "Switch account"
                        )
                        Column {
                            Text(
                                text = viewModel.accountState.account?.displayname.orEmpty()
                                    .ifBlank { viewModel.accountState.account?.shortUsername }
                                    ?: "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp)
                            Text(
                                text = viewModel.accountState.account?.acct ?: "",
                                fontSize = 12.sp,
                                lineHeight = 6.sp
                            )
                        }
                    }
                }, actions = {
                    if (viewModel.ownDomain.isNotEmpty()) {
                        DomainSoftwareComposable(
                            domain = viewModel.ownDomain
                        )
                    }

                    IconButton(onClick = {
                        showBottomSheet = 1
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.more_menu),
                            contentDescription = "preferences"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            InfinitePostsList(
                items = viewModel.postsState.posts,
                isLoading = viewModel.postsState.isLoading,
                isRefreshing = viewModel.accountState.refreshing || viewModel.postsState.refreshing,
                error = viewModel.postsState.error,
                view = viewModel.view,
                changeView = { viewModel.changeView(it) },
                endReached = false,
                navController = navController,
                getItemsPaginated = { viewModel.getPostsPaginated() },
                onRefresh = {
                    viewModel.loadData(true)
                },
                postsCount = viewModel.accountState.account?.postsCount,
                itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) },
                postGetsUpdated = { },
                isFirstItemLarge = true,
                before = {
                    Column(
                        modifier = Modifier.layout { measurable, constraints ->
                            val horizontalPadding = 4.dp.roundToPx()

                            val expandedWidth = constraints.maxWidth + (horizontalPadding * 2)
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
                                relationship = null,
                                postsLabel = viewModel.postsLabel,
                                followingLabel = viewModel.followingLabel,
                                followerLabel = viewModel.followerLabel,
                                navController = navController,
                                openUrl = { url -> viewModel.openUrl(url) })

                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate(Destination.EditProfile)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = stringResource(Res.string.edit_profile))
                                }
                            }
                        }
                        if (viewModel.capabilities.value.profile.showCollectionsOwnProfile) {
                            CollectionsComposable(
                                collectionsState = viewModel.collectionsState,
                                getMoreCollections = {
                                    viewModel.accountState.account?.let {
                                        viewModel.getCollections(
                                            it.id, true
                                        )
                                    }
                                },
                                navController = navController,
                                addNewButton = PlatformFeatures.addCollection,
                                instanceDomain = viewModel.ownDomain,
                            ) { url -> viewModel.openUrl(url) }
                        }
                    }
                })
        }
    }

    InfiniteStaggeredGridHandler(
        lazyStaggeredGridState = lazyGridState, itemCount = viewModel.postsState.posts.size
    ) {
        viewModel.getPostsPaginated()
    }

    if (viewModel.accountState.error.isNotEmpty()) {
        ErrorComposableDialog(
            viewModel.accountState.error, onDismiss = {
                viewModel.dismissError()
            })
    }

    if (showBottomSheet > 0) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = 0
            }, sheetState = sheetState
        ) {
            if (showBottomSheet == 1) {
                val icon = viewModel.appIcon.collectAsState()
                ModalBottomSheetContent(
                    navController = navController,
                    instanceDomain = DomainFormat.formatDomain(viewModel.ownDomain),
                    appIcon = icon.value,
                    backendType = viewModel.backendType,
                    closeBottomSheet = {
                        showBottomSheet = 0
                    },
                    openPreferencesDrawer
                )
            } else if (showBottomSheet == 2) {
                AccountSwitchBottomSheet(
                    navController = navController,
                    closeBottomSheet = { showBottomSheet = 0 },
                    viewModel
                )
            }
        }
    }
}
