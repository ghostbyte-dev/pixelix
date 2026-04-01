package com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList

@Composable
fun GlobalTimelineComposable(
    navController: NavController,
    viewModel: GlobalTimelineViewModel = injectViewModel(key = "global-timeline-key") { globalTimelineViewModel }
) {
    InfinitePostsList(items = viewModel.globalTimelineState.globalTimeline,
        contentPaddingTop = 30.dp,
        isLoading = viewModel.globalTimelineState.isLoading,
        isRefreshing = viewModel.globalTimelineState.refreshing,
        error = viewModel.globalTimelineState.error,
        endReached = false,
        navController = navController,
        getItemsPaginated = { viewModel.getItemsPaginated() },
        onRefresh = {
            viewModel.refresh()
        },
        itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) },
        postGetsUpdated = { post -> viewModel.postGetsUpdated(post) }
    )
}