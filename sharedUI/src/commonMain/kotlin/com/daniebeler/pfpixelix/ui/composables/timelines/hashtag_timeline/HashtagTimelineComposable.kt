package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.TimelineScreen
import com.daniebeler.pfpixelix.ui.composables.widgets.FollowButton

@Composable
fun HashtagTimelineComposable(
    navController: NavController,
    hashtag: String,
    viewModel: HashtagTimelineViewModel = injectViewModel(key = "hashtag-timeline-$hashtag") { hashtagTimelineViewModel }
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