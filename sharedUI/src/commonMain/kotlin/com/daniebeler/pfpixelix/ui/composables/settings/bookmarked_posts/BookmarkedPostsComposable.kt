package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.ParametricTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.TimelineScreen
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bookmarked_posts

@Composable
fun BookmarkedPostsComposable(
    navController: NavController,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "bookmarked-posts") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.BOOKMARKED_POSTS) }
    }
) {
    TimelineScreen(
        title = stringResource(Res.string.bookmarked_posts),
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}