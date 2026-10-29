package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun CategoryTimelineComposable(
    navController: AppNavigator,
    category: String,
    viewModel: ParametricTimelineViewModel = assistedMetroViewModel<
        ParametricTimelineViewModel, ParametricTimelineViewModel.Factory
    >(key = "category-$category") {
        create(ParametricTimelineViewModel.FetchType.CATEGORY, category)
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