package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.all
import pixelix.app.generated.resources.extension_puzzle_outline
import pixelix.app.generated.resources.followers
import pixelix.app.generated.resources.likes_
import pixelix.app.generated.resources.mentions
import pixelix.app.generated.resources.notifications
import pixelix.app.generated.resources.reposts
import pixelix.app.generated.resources.you_don_t_have_any_notifications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsComposable(
    navController: NavController,
    viewModel: NotificationsViewModel = injectViewModel(key = "notifications-viewmodel-key") { notificationsViewModel }
) {

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(
            title = {
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
                            imageVector = vectorResource(Res.drawable.extension_puzzle_outline),
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
                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                        .horizontalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    if (viewModel.filter == NotificationsFilterEnum.All) {
                        ActiveFilterButton(text = stringResource(Res.string.all))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.all), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.All)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Followers) {
                        ActiveFilterButton(text = stringResource(Res.string.followers))
                    } else {
                        InactiveFilterButton(
                            text = stringResource(Res.string.followers), onClick = {
                                viewModel.changeFilter(NotificationsFilterEnum.Followers)
                            })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Likes) {
                        ActiveFilterButton(text = stringResource(Res.string.likes_))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.likes_), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Likes)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Reposts) {
                        ActiveFilterButton(text = stringResource(Res.string.reposts))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.reposts), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Reposts)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Mentions) {
                        ActiveFilterButton(text = stringResource(Res.string.mentions))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.mentions), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Mentions)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Spacer(
                    modifier = Modifier.fillMaxWidth().height(12.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                )

                PullToRefreshBox(
                    isRefreshing = viewModel.notificationsState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                ) {
                    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize(), content = {
                        if (viewModel.notificationsState.notifications.isNotEmpty()) {
                            items(viewModel.notificationsState.notifications, key = {
                                it.id
                            }) {
                                if (viewModel.filter == NotificationsFilterEnum.All) {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Likes && it.type == "favourite") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Followers && it.type == "follow") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Reposts && it.type == "reblog") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Mentions && it.type == "mention") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                }
                            }

                            if (viewModel.notificationsState.isLoading && !viewModel.notificationsState.isRefreshing) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier.fillMaxWidth().height(80.dp)
                                            .wrapContentSize(Alignment.Center)
                                    )
                                }
                            }

                            if (viewModel.notificationsState.endReached && viewModel.notificationsState.notifications.size > 10) {
                                item {
                                    EndOfListComposable()
                                }
                            }
                        }
                    })

                    if (!viewModel.notificationsState.isLoading && viewModel.notificationsState.error.isEmpty() && viewModel.notificationsState.notifications.isEmpty()) {
                        FullscreenEmptyStateComposable(
                            EmptyState(
                                icon = Icons.Outlined.Email, heading = stringResource(
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

        InfiniteListHandler(lazyListState = lazyListState) {
            viewModel.getNotificationsPaginated()
        }
    }
}

@Composable
private fun ActiveFilterButton(text: String) {
    Button(onClick = { }, shape = RoundedCornerShape(12.dp)) {
        Text(text = text)
    }
}

@Composable
private fun InactiveFilterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}