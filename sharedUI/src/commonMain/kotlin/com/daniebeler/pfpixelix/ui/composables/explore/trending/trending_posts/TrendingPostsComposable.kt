package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.profile.SwitchViewComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.composables.profile.postsWrapperComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.photo

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendingPostsComposable(
    navController: NavController,
    viewModel: TrendingPostsViewModel = injectViewModel(key = "trending-posts") { trendingPostsViewModel }
) {

    val lazyGridState = rememberLazyStaggeredGridState()

    val calendarIcon = vectorResource(Res.drawable.datetime)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
//        InfinitePostsList(
//            items = viewModel.trendingState.trendingPosts,
//            isLoading = viewModel.trendingState.isLoading,
//            isRefreshing = viewModel.trendingState.isRefreshing,
//            error = viewModel.trendingState.error,
//            endReached = viewModel.trendingState.endReached,
//            view = ViewEnum.Grid,
//            changeView = {  },
//            isFirstItemLarge = true,
//            itemGetsDeleted = {  },
//            getItemsPaginated = { viewModel.getTrendingPostsPaginated(range) },
//            onRefresh = { viewModel.getTrendingPosts(range, true) },
//            navController = navController,
//            postGetsUpdated = {  },
//            contentPaddingTop = 32.dp,
//            contentPaddingBottom = 80.dp
//        )

        val gridContentWidth = maxWidth - 8.dp
        val gridColumnCount = maxOf(3, (gridContentWidth / 120.dp).toInt())

        LazyVerticalStaggeredGrid(
            columns = when (viewModel.view) {
                ViewEnum.Grid -> StaggeredGridCells.Fixed(
                    gridColumnCount
                )

                ViewEnum.Masonry -> StaggeredGridCells.Adaptive(
                    150.dp
                )

                ViewEnum.Timeline -> StaggeredGridCells.Adaptive(
                    350.dp
                )
            },
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = lazyGridState,
            contentPadding = PaddingValues(top = 40.dp, bottom = 60.dp, start = 4.dp, end = 4.dp)
        ) {

            item(span = StaggeredGridItemSpan.FullLine) {
                ButtonGroup(overflowIndicator = { Text("Daily") }) {
                    toggleableItem(
                        weight = 1f,
                        checked = viewModel.timeRange == TrendingRange.DAILY,
                        onCheckedChange = { viewModel.changeTimeRange(TrendingRange.DAILY) },
                        label = "Daily",
                        icon = {
                            if (viewModel.timeRange == TrendingRange.DAILY) {
                                Icon(imageVector = calendarIcon, contentDescription = "")
                            }
                        }
                    )

                    toggleableItem(
                        weight = 1f,
                        checked = viewModel.timeRange == TrendingRange.MONTHLY,
                        onCheckedChange = { viewModel.changeTimeRange(TrendingRange.MONTHLY) },
                        label = "Monthly",
                        icon = {
                            if (viewModel.timeRange == TrendingRange.MONTHLY) {
                                Icon(imageVector = calendarIcon, contentDescription = "")
                            }
                        }
                    )

                    toggleableItem(
                        weight = 1f,
                        checked = viewModel.timeRange == TrendingRange.YEARLY,
                        onCheckedChange = { viewModel.changeTimeRange(TrendingRange.YEARLY) },
                        label = "Yearly",
                        icon = {
                            if (viewModel.timeRange == TrendingRange.YEARLY) {
                                Icon(imageVector = calendarIcon, contentDescription = "")
                            }
                        }
                    )
                }

            }

            item(span = StaggeredGridItemSpan.FullLine) {
                SwitchViewComposable(
                    postsCount = 0,
                    viewType = viewModel.view,
                    onViewChange = { viewModel.changeView(it) })
            }

            postsWrapperComposable(
                posts = viewModel.trendingState.trendingPosts,
                isLoading = viewModel.trendingState.isLoading,
                isRefreshing = viewModel.trendingState.isRefreshing,
                endReached = viewModel.trendingState.endReached,
                view = viewModel.view,
                postGetsDeleted = { },
                updatePost = { },
                isFirstImageLarge = true,
                gridColumnCount = gridColumnCount,
                gridContentWidth = gridContentWidth,
                navController = navController
            )

            if (viewModel.trendingState.trendingPosts.isEmpty() && viewModel.trendingState.error.isNotBlank()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    ErrorComposable(
                        message = viewModel.trendingState.error,
                        modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                    )
                }
            }
            if (viewModel.trendingState.isLoading && viewModel.trendingState.trendingPosts.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    LoadingComposable(viewModel.trendingState.isLoading)
                }
            }
            if (viewModel.trendingState.trendingPosts.isEmpty() && !viewModel.trendingState.isLoading && viewModel.trendingState.error.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    EmptyStateComposable(
                        emptyState = EmptyState(
                            icon = vectorResource(Res.drawable.photo), heading = "No Posts"
                        )
                    )
                }
            }
        }

    }

    InfiniteStaggeredGridHandler(
        lazyStaggeredGridState = lazyGridState,
        itemCount = viewModel.trendingState.trendingPosts.size
    ) {
        viewModel.getTrendingPostsPaginated()
    }

    if (viewModel.trendingState.error.isNotEmpty()) {
        ErrorComposableDialog(
            viewModel.trendingState.error, onDismiss = {
                //viewModel.dismissError()
            })
    }
}