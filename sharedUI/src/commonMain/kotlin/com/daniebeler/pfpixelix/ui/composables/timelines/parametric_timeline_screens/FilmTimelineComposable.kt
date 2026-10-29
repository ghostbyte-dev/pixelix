package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun FilmTimelineComposable(
    navController: AppNavigator,
    film: String,
    viewModel: ParametricTimelineViewModel = assistedMetroViewModel<
        ParametricTimelineViewModel, ParametricTimelineViewModel.Factory
    >(key = "film-$film") {
        create(ParametricTimelineViewModel.FetchType.FILM, film)
    }
) {
    TimelineScreen(
        title = film,
        subtitle = "Film",
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}