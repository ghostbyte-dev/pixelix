package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun FilmTimelineComposable(
    navController: AppNavigator,
    film: String,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "film-$film") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.FILM, film) }
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