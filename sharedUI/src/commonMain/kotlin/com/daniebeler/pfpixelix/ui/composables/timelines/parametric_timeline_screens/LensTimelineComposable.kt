package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun LensTimelineComposable(
    navController: AppNavigator,
    lens: String,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "lens-$lens") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.LENS, lens) }
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