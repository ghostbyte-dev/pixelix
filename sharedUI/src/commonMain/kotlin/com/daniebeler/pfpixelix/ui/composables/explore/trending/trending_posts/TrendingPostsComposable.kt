package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum

@Composable
fun TrendingPostsComposable(
    range: String,
    navController: NavController,
    viewModel: TrendingPostsViewModel = injectViewModel(key = "trending-posts") { trendingPostsViewModel }
) {

    DisposableEffect(range) {
        viewModel.getTrendingPosts(range)
        onDispose {}
    }

    Box(modifier = Modifier.fillMaxSize()) {
        InfinitePostsList(
            items = viewModel.trendingState.trendingPosts,
            isLoading = viewModel.trendingState.isLoading,
            isRefreshing = viewModel.trendingState.isRefreshing,
            error = viewModel.trendingState.error,
            endReached = false,
            view = ViewEnum.Grid,
            changeView = {  },
            isFirstItemLarge = true,
            itemGetsDeleted = {  },
            getItemsPaginated = {  },
            onRefresh = { viewModel.getTrendingPosts(range, true) },
            navController = navController,
            postGetsUpdated = {  },
            contentPaddingTop = 32.dp,
            contentPaddingBottom = 20.dp
        )
    }
}