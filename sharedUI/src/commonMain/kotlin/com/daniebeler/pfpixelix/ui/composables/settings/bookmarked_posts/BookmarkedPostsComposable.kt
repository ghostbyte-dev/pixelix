package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.ParametricTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.TimelineScreen
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bookmarked_posts

@Composable
fun BookmarkedPostsComposable(
    navController: AppNavigator,
    viewModel: ParametricTimelineViewModel = assistedMetroViewModel<
        ParametricTimelineViewModel, ParametricTimelineViewModel.Factory
    >(key = "bookmarked-posts") {
        create(ParametricTimelineViewModel.FetchType.BOOKMARKED_POSTS)
    }
) {
    TimelineScreen(
        title = stringResource(Res.string.bookmarked_posts),
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}