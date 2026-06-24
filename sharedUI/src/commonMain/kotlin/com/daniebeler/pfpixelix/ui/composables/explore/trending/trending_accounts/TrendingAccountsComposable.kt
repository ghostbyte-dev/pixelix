package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.no_trending_profiles

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendingAccountsComposable(
    navController: NavController,
    viewModel: TrendingAccountsViewModel = injectViewModel(key = "trending-accounts-key") { trendingAccountsViewModel }
) {

    val calendarIcon = vectorResource(Res.drawable.datetime)
    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.trendingAccountsState.isRefreshing,
        onRefresh = { viewModel.getTrendingAccountsState(true) },
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                if (viewModel.capabilities.trending.supportsMultipleProfileTimeRanges) {
                    item {
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
                    }
                }

                items(viewModel.trendingAccountsState.trendingAccounts, key = {
                    it.id
                }) {
                    TrendingAccountElement(
                        account = it, navController = navController
                    )
                }
            })
        if (viewModel.trendingAccountsState.trendingAccounts.isEmpty()) {
            if (viewModel.trendingAccountsState.isLoading && !viewModel.trendingAccountsState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.trendingAccountsState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.trendingAccountsState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.trendingAccountsState.isLoading && viewModel.trendingAccountsState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_trending_profiles)))
            }
        }
    }

    InfiniteListHandler(
        lazyListState = lazyListState
    ) {
        viewModel.getTrendingAccountsPaginated()
    }
}