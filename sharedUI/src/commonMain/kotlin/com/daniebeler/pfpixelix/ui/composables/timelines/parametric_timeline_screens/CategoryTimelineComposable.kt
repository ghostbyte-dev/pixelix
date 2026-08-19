package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun CategoryTimelineComposable(
    navController: NavController,
    category: String,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "category-$category") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.CATEGORY, category) }
    }
) {
    TimelineScreen(
        title = category,
        subtitle = "Category",
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}