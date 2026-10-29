package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun LensTimelineComposable(
    navController: AppNavigator,
    lens: String,
    viewModel: ParametricTimelineViewModel = assistedMetroViewModel<
        ParametricTimelineViewModel, ParametricTimelineViewModel.Factory
    >(key = "lens-$lens") {
        create(ParametricTimelineViewModel.FetchType.LENS, lens)
    }
) {
    TimelineScreen(
        title = lens,
        subtitle = "Lens",
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}