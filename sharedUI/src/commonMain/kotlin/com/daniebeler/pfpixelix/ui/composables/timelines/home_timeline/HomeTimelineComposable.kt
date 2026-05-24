package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.follow_accounts_or_hashtags_to_fill_your_home_timeline
import pixelix.app.generated.resources.no_posts
import pixelix.app.generated.resources.photo

@Composable
fun HomeTimelineComposable(
    navController: NavController,
    viewModel: HomeTimelineViewModel = injectViewModel(key = "home-timeline-key") { homeTimelineViewModel }
) {
    InfinitePostsList(
        items = viewModel.timelineState.posts,
        contentPaddingTop = 32.dp,
        isLoading = viewModel.timelineState.isLoading,
        isRefreshing = viewModel.timelineState.isRefreshing,
        error = viewModel.timelineState.error,
        endReached = false,
        navController = navController,
        emptyMessage = EmptyState(
            icon = vectorResource(Res.drawable.photo),
            heading = stringResource(Res.string.no_posts),
            message = stringResource(Res.string.follow_accounts_or_hashtags_to_fill_your_home_timeline),
            buttonText = stringResource(Res.string.explore_trending_profiles),
            onClick = { navController.navigate(Destination.Search(1)) }
        ),
        getItemsPaginated = { viewModel.getItemsPaginated() },
        onRefresh = { viewModel.refresh() },
        itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) },
        postGetsUpdated = { viewModel.postGetsUpdated(it) }
    )
}