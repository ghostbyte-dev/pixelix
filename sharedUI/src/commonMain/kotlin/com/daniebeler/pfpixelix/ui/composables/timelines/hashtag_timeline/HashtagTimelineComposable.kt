package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.FollowButton
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashtagTimelineComposable(
    navController: NavController,
    hashtag: String,
    viewModel: HashtagTimelineViewModel = injectViewModel(key = "hashtag-timeline$hashtag") { hashtagTimelineViewModel }
) {

    LaunchedEffect(hashtag) {
        viewModel.getItemsFirstLoad(hashtag)
        viewModel.getHashtagInfo(hashtag)
    }

    ScreenScaffold(
        title = "#$hashtag",
        subtitle = "Hashtag",
        navController = navController,
        actions = {
            FollowButton(
                iconButton = true,
                firstLoaded = viewModel.hashtagState.hashtag != null,
                isLoading = viewModel.hashtagState.isLoading,
                isFollowing = viewModel.hashtagState.hashtag?.following ?: false,
                onFollowClick = { viewModel.followHashtag(viewModel.hashtagState.hashtag!!.name) },
                onUnFollowClick = { viewModel.unfollowHashtag(viewModel.hashtagState.hashtag!!.name) }
            )
        }
    ) {
        InfinitePostsList(
            contentPaddingTop = 24.dp,
            items = viewModel.timelineState.posts,
            isLoading = viewModel.timelineState.isLoading,
            isRefreshing = viewModel.timelineState.isRefreshing,
            error = viewModel.timelineState.error,
            endReached = viewModel.timelineState.endReached,
            view = viewModel.view,
            changeView = { viewModel.changeView(it) },
            isFirstItemLarge = true,
            itemGetsDeleted = { viewModel.postGetsDeleted(it) },
            getItemsPaginated = { viewModel.getItemsPaginated(hashtag) },
            onRefresh = { viewModel.refresh() },
            postsCount = viewModel.hashtagState.hashtag?.postsCount,
            navController = navController,
            postGetsUpdated = { viewModel.postGetsUpdated(it) })
    }
}
