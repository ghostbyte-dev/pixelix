package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun CameraTimelineComposable(
    navController: AppNavigator,
    camera: String,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "camera-$camera") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.CAMERA, camera) }
    }
) {
    TimelineScreen(
        title = camera,
        subtitle = "Camera",
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}