package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.no_trending_hashtags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingHashtagsComposable(
    range: String,
    navController: NavController,
    viewModel: TrendingHashtagsViewModel = injectViewModel(key = "trending-hashtags-key") { trendingHashtagsViewModel }
) {
    DisposableEffect(range) {
        viewModel.getTrendingHashtags(range)
        onDispose {}
    }

    //TODO: pagination, for vernissage
    CustomPullToRefreshBox(
        isRefreshing = viewModel.trendingHashtagsState.isRefreshing,
        onRefresh = { viewModel.getTrendingHashtags(range, true) },
        animatedBox = true
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.trendingHashtagsState.trendingHashtags, key = {
                    if (!it.hashtag.isNullOrEmpty()) {
                        it.hashtag
                    } else {
                        it.name
                    }
                }) {
                    TrendingHashtagElement(hashtag = it, navController = navController)
                }
            })

        if (viewModel.trendingHashtagsState.trendingHashtags.isEmpty()) {
            if (viewModel.trendingHashtagsState.isLoading && !viewModel.trendingHashtagsState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.trendingHashtagsState.error.isNotEmpty()) {
                ErrorComposable(message = viewModel.trendingHashtagsState.error, modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp))
            }

            if (!viewModel.trendingHashtagsState.isLoading && viewModel.trendingHashtagsState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_trending_hashtags)))
            }
        }
    }
}