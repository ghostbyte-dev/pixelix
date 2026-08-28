package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

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
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.local
import pixelix.app.generated.resources.local_timeline_explained

@Composable
fun LocalTimelineComposable(
    pagerState: PagerState,
    tabIndex: Int,
    navController: NavController,
    viewModel: LocalTimelineViewModel = injectViewModel(key = "local-timeline-key") { localTimelineViewModel }
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
        contentPaddingTop = 30.dp,
        isLoading = viewModel.timelineState.isLoading,
        isRefreshing = viewModel.timelineState.isRefreshing,
        error = viewModel.timelineState.error,
        view = viewModel.view,
        changeView = { viewModel.changeView(it) },
        endReached = viewModel.timelineState.nextId.isNullOrEmpty(),
        navController = navController,
        emptyMessage = EmptyState(heading = "No posts"),
        getItemsPaginated = {
            viewModel.getItemsPaginated()
        },
        onRefresh = {
            viewModel.refresh()
        },
        staggeredGridState = staggeredGridState,
        itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) },
        postGetsUpdated = { viewModel.postGetsUpdated(it) },
        before = if (!viewModel.showTimelineHelp) {
            null
        } else {
            {
                TimelineHelpCard(
                    title = stringResource(Res.string.local),
                    description = stringResource(Res.string.local_timeline_explained),
                    onDiscard = {
                        viewModel.discardHelp()
                    }
                )
            }
        }
    )
}