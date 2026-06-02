package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineHelpCard
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.local
import pixelix.app.generated.resources.local_timeline_explained

@Composable
fun LocalTimelineComposable(
    navController: NavController,
    viewModel: LocalTimelineViewModel = injectViewModel(key = "local-timeline-key") { localTimelineViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()
    val appComponent = LocalAppComponent.current

    LaunchedEffect(Unit) {
        appComponent.backToTopTrigger.event.collect {
            staggeredGridState.animateScrollToItem(0, 0)
            viewModel.refresh()
        }
    }

    InfinitePostsList(items = viewModel.timelineState.posts,
        contentPaddingTop = 30.dp,
        isLoading = viewModel.timelineState.isLoading,
        isRefreshing = viewModel.timelineState.isRefreshing,
        error = viewModel.timelineState.error,
        endReached = false,
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