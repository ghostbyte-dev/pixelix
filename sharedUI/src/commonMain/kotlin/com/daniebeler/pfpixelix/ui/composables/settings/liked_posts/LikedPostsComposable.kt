package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.ParametricTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.TimelineScreen
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.liked_posts

@Composable
fun LikedPostsComposable(
    navController: AppNavigator,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "liked-posts") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.LIKED_POSTS) }
    }
) {
    TimelineScreen(
        title = stringResource(Res.string.liked_posts),
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true
    )
}