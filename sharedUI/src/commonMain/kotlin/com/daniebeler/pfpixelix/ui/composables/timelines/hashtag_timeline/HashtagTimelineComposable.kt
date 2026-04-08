package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.FollowButton
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList

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
        viewModel.getRelatedHashtags(hashtag)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp).fillMaxSize()) {
            InfinitePostsList(
                contentPaddingTop = 24.dp,
                items = viewModel.postsState.hashtagTimeline,
                isLoading = viewModel.postsState.isLoading,
                isRefreshing = viewModel.postsState.isRefreshing,
                error = viewModel.postsState.error,
                endReached = viewModel.postsState.endReached,
                view = viewModel.view,
                changeView = { viewModel.changeView(it) },
                isFirstItemLarge = true,
                itemGetsDeleted = { viewModel.postGetsDeleted(it) },
                getItemsPaginated = { viewModel.getItemsPaginated(hashtag) },
                onRefresh = { viewModel.refresh() },
                postsCount = viewModel.hashtagState.hashtag?.count ?: 0,
                navController = navController,
                postGetsUpdated = { viewModel.postGetsUpdated(it) })
        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "#$hashtag",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }, actions = {
                FollowButton(
                    iconButton = true,
                    firstLoaded = viewModel.hashtagState.hashtag != null,
                    isLoading = viewModel.hashtagState.isLoading,
                    isFollowing = viewModel.hashtagState.hashtag?.following ?: false,
                    onFollowClick = { viewModel.followHashtag(viewModel.hashtagState.hashtag!!.name) },
                    onUnFollowClick = { viewModel.unfollowHashtag(viewModel.hashtagState.hashtag!!.name) })
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
    }
}