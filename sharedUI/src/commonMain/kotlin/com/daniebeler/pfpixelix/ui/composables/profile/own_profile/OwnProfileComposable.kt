package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import com.daniebeler.pfpixelix.ui.composables.profile.CollectionsComposable
import com.daniebeler.pfpixelix.ui.composables.profile.PostsWrapperComposable
import com.daniebeler.pfpixelix.ui.composables.profile.ProfileTopSection
import com.daniebeler.pfpixelix.ui.composables.profile.SwitchViewComposable
import com.daniebeler.pfpixelix.ui.composables.profile.server_stats.DomainSoftwareComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.DomainFormat
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.edit_profile

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

    Box(modifier = Modifier.fillMaxSize()) {

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                .fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = viewModel.accountState.refreshing || viewModel.postsState.refreshing,
                onRefresh = { viewModel.loadData(true) },
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                BoxWithConstraints {
                val gridContentWidth = maxWidth - 8.dp
                val gridColumnCount = maxOf(3, (gridContentWidth / 120.dp).toInt())
                LazyVerticalStaggeredGrid(
                    columns = when (viewModel.view) {
                        com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum.Grid -> StaggeredGridCells.Fixed(gridColumnCount)
                        com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum.Timeline -> StaggeredGridCells.Adaptive(350.dp)
                    },
                    verticalItemSpacing = 4.dp,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(
                                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            ).background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(top = 24.dp, bottom = 12.dp)
                        ) {
                            if (viewModel.accountState.account != null) {
                                ProfileTopSection(
                                    account = viewModel.accountState.account,
                                    relationship = null,
                                    navController,
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

                    item(span = StaggeredGridItemSpan.FullLine) {
                        SwitchViewComposable(
                            postsCount = viewModel.accountState.account?.postsCount ?: 0,
                            viewType = viewModel.view,
                            onViewChange = { viewModel.changeView(it) })
                    }

                    PostsWrapperComposable(
                        posts = viewModel.postsState.posts,
                        isLoading = viewModel.postsState.isLoading,
                        isRefreshing = viewModel.accountState.refreshing || viewModel.postsState.refreshing,
                        error = viewModel.postsState.error,
                        endReached = viewModel.postsState.endReached,
                        emptyMessage = EmptyState(
                            icon = Icons.Outlined.Photo, heading = "No Posts"
                        ),
                        view = viewModel.view,
                        postGetsDeleted = { viewModel.postGetsDeleted(it) },
                        updatePost = { viewModel.updatePost(it) },
                        isFirstImageLarge = true,
                        gridColumnCount = gridColumnCount,
                        gridContentWidth = gridContentWidth,
                        navController = navController
                    )
                }

                if (viewModel.postsState.posts.isEmpty() && viewModel.postsState.error.isNotBlank()) {
                    FullscreenErrorComposable(message = viewModel.postsState.error)
                }
                }
            }
        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), title = {
                Row(Modifier.clickable { showBottomSheet = 2 }) {
                    Column {
                        Text(
                            text = viewModel.accountState.account?.username ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = DomainFormat.formatDomain(viewModel.ownDomain), fontSize = 12.sp, lineHeight = 6.sp
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
                        imageVector = Icons.Outlined.MoreVert, contentDescription = "preferences"
                    )
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
    }

    InfiniteStaggeredGridHandler(lazyStaggeredGridState = lazyGridState) {
        viewModel.getPostsPaginated()
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
                    instanceDomain = viewModel.ownDomain,
                    appIcon = icon.value,
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
