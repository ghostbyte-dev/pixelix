package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.TimelineScreen
import com.daniebeler.pfpixelix.ui.composables.widgets.FollowButton
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HashtagTimelineComposable(
    navController: AppNavigator,
    hashtag: String,
    viewModel: HashtagTimelineViewModel = metroViewModel(key = "hashtag-timeline-$hashtag")
) {
    LaunchedEffect(hashtag) {
        viewModel.init(hashtag)
    }

    TimelineScreen(
        title = "#$hashtag",
        subtitle = "Hashtag",
        navController = navController,
        viewModel = viewModel,
        isFirstItemLarge = true,
        actions = {
            FollowButton(
                iconButton = true,
                firstLoaded = viewModel.hashtagState.hashtag != null,
                isLoading = viewModel.hashtagState.isLoading,
                isFollowing = viewModel.hashtagState.hashtag?.following ?: false,
                onFollowClick = { viewModel.followHashtag() },
                onUnFollowClick = { viewModel.unfollowHashtag() }
            )
        }
    )
}