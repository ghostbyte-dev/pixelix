package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun CameraTimelineComposable(
    navController: NavController,
    camera: String,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "camera-$camera") {
        parametricTimelineViewModel.apply { init(camera, ParametricTimelineViewModel.FetchType.CAMERA) }
    }
) {
    TimelineScreen(
        title = camera,
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}