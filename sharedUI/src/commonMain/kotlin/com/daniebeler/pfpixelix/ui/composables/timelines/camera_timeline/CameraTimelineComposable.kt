package com.daniebeler.pfpixelix.ui.composables.timelines.camera_timeline

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.FollowButton
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTimelineComposable(
    navController: NavController,
    camera: String,
    viewModel: CameraTimelineViewModel = injectViewModel(key = "hashtag-timeline$camera") {
        cameraTimelineViewModel.apply {
            cameraState = CameraState(camera = camera)
        }
    }
) {

    ScreenScaffold(
        title = camera,
        navController = navController,
    ) {
        InfinitePostsList(
            contentPaddingTop = 24.dp,
            items = viewModel.timelineState.posts,
            isLoading = viewModel.timelineState.isLoading,
            isRefreshing = viewModel.timelineState.isRefreshing,
            error = viewModel.timelineState.error,
            endReached = viewModel.timelineState.endReached,
            view = viewModel.view,
            changeView = { viewModel.changeView(it) },
            isFirstItemLarge = true,
            itemGetsDeleted = { viewModel.postGetsDeleted(it) },
            getItemsPaginated = { viewModel.getItemsPaginated() },
            onRefresh = { viewModel.refresh() },
            navController = navController,
            postGetsUpdated = { viewModel.postGetsUpdated(it) })
    }
}
