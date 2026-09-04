package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel

@Composable
fun CategoryTimelineComposable(
    navController: AppNavigator,
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