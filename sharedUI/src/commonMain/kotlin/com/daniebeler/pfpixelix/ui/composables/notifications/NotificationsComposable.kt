package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import io.ktor.client.request.invoke
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.all
import pixelix.app.generated.resources.widget
import pixelix.app.generated.resources.followers
import pixelix.app.generated.resources.likes_
import pixelix.app.generated.resources.mail
import pixelix.app.generated.resources.mentions
import pixelix.app.generated.resources.notifications
import pixelix.app.generated.resources.reposts
import pixelix.app.generated.resources.you_don_t_have_any_notifications

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsComposable(
    navController: NavController,
    viewModel: NotificationsViewModel = injectViewModel(key = "notifications-viewmodel-key") { notificationsViewModel }
) {

    val staggeredGridState = rememberLazyStaggeredGridState()
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val filteredNotifications = remember(viewModel.notificationsState.notifications, viewModel.filter) {
        viewModel.notificationsState.notifications.filter {
            when (viewModel.filter) {
                NotificationsFilterEnum.All -> true
                NotificationsFilterEnum.Likes -> it.type == NotificationType.FAVOURITE
                NotificationsFilterEnum.Followers -> it.type == NotificationType.FOLLOW
                NotificationsFilterEnum.Reposts -> it.type == NotificationType.REBLOG
                NotificationsFilterEnum.Mentions -> it.type == NotificationType.MENTION
                else -> false
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior, title = {
                    Text(
                        stringResource(Res.string.notifications),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }, actions = {
                    if (PlatformFeatures.notificationWidgets) {
                        IconButton(onClick = {
                            viewModel.pinWidget()
                        }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.widget),
                                contentDescription = "add widget"
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column {
                val allText = stringResource(Res.string.all)
                val followersText = stringResource(Res.string.followers)
                val likesText = stringResource(Res.string.likes_)
                val repostsText = stringResource(Res.string.reposts)
                val mentionsText = stringResource(Res.string.mentions)

                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                        .horizontalScroll(scrollState),horizontalArrangement = Arrangement.spacedBy(
                        ButtonGroupDefaults.ConnectedSpaceBetween)
                ) {

                    Spacer(modifier = Modifier.width(12.dp))
                    ToggleButton(
                        checked = viewModel.filter == NotificationsFilterEnum.All,
                        onCheckedChange = {
                            viewModel.changeFilter(NotificationsFilterEnum.All)
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        content = {
                            Text(allText)
                        })

                    Spacer(modifier = Modifier.width(12.dp))

                    ToggleButton(
                        checked = viewModel.filter == NotificationsFilterEnum.Followers,
                        onCheckedChange = {
                            viewModel.changeFilter(NotificationsFilterEnum.Followers)
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        content = {
                            Text(followersText)
                        })

                    Spacer(modifier = Modifier.width(12.dp))

                    ToggleButton(
                        checked = viewModel.filter == NotificationsFilterEnum.Likes,
                        onCheckedChange = {
                            viewModel.changeFilter(NotificationsFilterEnum.Likes)
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        content = {
                            Text(likesText)
                        })

                    Spacer(modifier = Modifier.width(12.dp))

                    ToggleButton(
                        checked = viewModel.filter == NotificationsFilterEnum.Reposts,
                        onCheckedChange = {
                            viewModel.changeFilter(NotificationsFilterEnum.Reposts)
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        content = {
                            Text(repostsText)
                        })


                    Spacer(modifier = Modifier.width(12.dp))

                    ToggleButton(
                        checked = viewModel.filter == NotificationsFilterEnum.Mentions,
                        onCheckedChange = {
                            viewModel.changeFilter(NotificationsFilterEnum.Mentions)
                        },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        content = {
                            Text(mentionsText)
                        })

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Spacer(
                    modifier = Modifier.fillMaxWidth().height(12.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                )

                CustomPullToRefreshBox(
                    isRefreshing = viewModel.notificationsState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    animatedBox = true
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(350.dp),
                        state = staggeredGridState,
                        contentPadding = PaddingValues(
                            start = 8.dp, end = 8.dp, bottom = 60.dp, top = 8.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (viewModel.notificationsState.notifications.isNotEmpty()) {
                            itemsIndexed(filteredNotifications, key = { _, it -> it.id }) { index, notification ->
                                CustomNotification(
                                    notification = notification,
                                    navController = navController,
                                    removeNotification = { viewModel.removeNotification(notification) },
                                    index = index,
                                    count = filteredNotifications.size
                                )
                            }

                            if (viewModel.notificationsState.isLoading && !viewModel.notificationsState.isRefreshing) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    LoadingComposable()
                                }
                            }

                            if (viewModel.notificationsState.endReached && viewModel.notificationsState.notifications.size > 10) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    EndOfListComposable()
                                }
                            }
                        }
                    }

                    if (!viewModel.notificationsState.isLoading && viewModel.notificationsState.error.isEmpty() && viewModel.notificationsState.notifications.isEmpty()) {
                        EmptyStateComposable(
                            EmptyState(
                                icon = vectorResource(Res.drawable.mail), heading = stringResource(
                                    Res.string.you_don_t_have_any_notifications
                                )
                            )
                        )
                    }

                    if (!viewModel.notificationsState.isRefreshing && viewModel.notificationsState.notifications.isEmpty()) {
                        LoadingComposable(isLoading = viewModel.notificationsState.isLoading)
                    }
                    ErrorComposable(message = viewModel.notificationsState.error)
                }
            }
        }

        InfiniteStaggeredGridHandler(
            lazyStaggeredGridState = staggeredGridState,
            itemCount = viewModel.notificationsState.notifications.size
        ) {
            viewModel.getNotificationsPaginated()
        }
    }
}
