package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineHelpCard
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.follow_accounts_or_hashtags_to_fill_your_home_timeline
import pixelix.app.generated.resources.home
import pixelix.app.generated.resources.home_timeline_explained
import pixelix.app.generated.resources.no_posts
import pixelix.app.generated.resources.photo

@Composable
fun HomeTimelineComposable(
    pagerState: PagerState,
    tabIndex: Int,
    navController: NavController,
    viewModel: HomeTimelineViewModel = injectViewModel(key = "home-timeline-key") { homeTimelineViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()
    val appComponent = LocalAppComponent.current
    LaunchedEffect(Unit) {
        appComponent.backToTopTrigger.event.collect {
            if (pagerState.currentPage == tabIndex) {
                staggeredGridState.animateScrollToItem(0, 0)
                viewModel.refresh()
            }
        }
    }

    InfinitePostsList(
        items = viewModel.timelineState.posts,
        contentPaddingTop = 32.dp,
        view = viewModel.view,
        changeView = { viewModel.changeView(it) },
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
        postGetsUpdated = { viewModel.postGetsUpdated(it) },
        staggeredGridState = staggeredGridState,
        before = if (!viewModel.showTimelineHelp) {
            null
        } else {
            {
                TimelineHelpCard(
                    title = stringResource(Res.string.home),
                    description = stringResource(Res.string.home_timeline_explained),
                    onDiscard = {
                        viewModel.discardHelp()
                    }
                )
            }

        }
    )
}