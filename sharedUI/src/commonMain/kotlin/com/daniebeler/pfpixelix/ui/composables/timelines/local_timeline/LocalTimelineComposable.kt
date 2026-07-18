package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineHelpCard
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.local
import pixelix.app.generated.resources.local_timeline_explained
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun LocalTimelineComposable(
    pagerState: PagerState,
    tabIndex: Int,
    navController: NavController,
    viewModel: LocalTimelineViewModel = injectViewModel(key = "local-timeline-key") { localTimelineViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()
    val appComponent = LocalAppComponent.current;
    LaunchedEffect(Unit) {
        appComponent.backToTopTrigger.event.collect {
            Logger.d("BackToTop") {
                "collected on tab $tabIndex, current=${pagerState.currentPage}"
            }
            if (pagerState.currentPage == tabIndex) {
                try {
                    staggeredGridState.animateScrollToItem(0, 0)
                } catch (e: CancellationException) {
                    Logger.d("BackToTop") {
                        "scroll cancelled on tab $tabIndex"
                    }
                    // don't rethrow — keep the collector alive
                }
                viewModel.refresh()
            }
        }
    }

    InfinitePostsList(items = viewModel.timelineState.posts,
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