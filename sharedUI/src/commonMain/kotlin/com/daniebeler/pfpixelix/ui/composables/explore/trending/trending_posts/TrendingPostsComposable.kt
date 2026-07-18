package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.datetime

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendingPostsComposable(
    navController: NavController,
    viewModel: TrendingPostsViewModel = injectViewModel(key = "trending-posts") { trendingPostsViewModel }
) {
    val calendarIcon = vectorResource(Res.drawable.datetime)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        InfinitePostsList(
            items = viewModel.trendingState.trendingPosts,
            isLoading = viewModel.trendingState.isLoading,
            isRefreshing = viewModel.trendingState.isRefreshing,
            error = viewModel.trendingState.error,
            endReached = viewModel.trendingState.endReached,
            view = viewModel.view,
            changeView = { viewModel.changeView(it) },
            isFirstItemLarge = true,
            itemGetsDeleted = { },
            getItemsPaginated = { viewModel.getTrendingPostsPaginated() },
            onRefresh = { viewModel.getTrendingPosts(true) },
            navController = navController,
            postGetsUpdated = { },
            contentPaddingTop = 32.dp,
            contentPaddingBottom = 80.dp,
            before = {
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
                        })

                    toggleableItem(
                        weight = 1f,
                        checked = viewModel.timeRange == TrendingRange.MONTHLY,
                        onCheckedChange = { viewModel.changeTimeRange(TrendingRange.MONTHLY) },
                        label = "Monthly",
                        icon = {
                            if (viewModel.timeRange == TrendingRange.MONTHLY) {
                                Icon(imageVector = calendarIcon, contentDescription = "")
                            }
                        })

                    toggleableItem(
                        weight = 1f,
                        checked = viewModel.timeRange == TrendingRange.YEARLY,
                        onCheckedChange = { viewModel.changeTimeRange(TrendingRange.YEARLY) },
                        label = "Yearly",
                        icon = {
                            if (viewModel.timeRange == TrendingRange.YEARLY) {
                                Icon(imageVector = calendarIcon, contentDescription = "")
                            }
                        })
                }
            })
    }
}