package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun CameraTimelineComposable(
    navController: AppNavigator,
    camera: String,
    viewModel: ParametricTimelineViewModel = assistedMetroViewModel<
        ParametricTimelineViewModel, ParametricTimelineViewModel.Factory
    >(key = "camera-$camera") {
        create(ParametricTimelineViewModel.FetchType.CAMERA, camera)
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