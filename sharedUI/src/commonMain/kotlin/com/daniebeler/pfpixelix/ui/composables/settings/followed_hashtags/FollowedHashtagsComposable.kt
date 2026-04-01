package com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.CustomHashtag
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.followed_hashtags
import pixelix.app.generated.resources.no_followed_hashtags

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FollowedHashtagsComposable(
    navController: NavController,
    viewModel: FollowedHashtagsViewModel = injectViewModel(key = "followed-hashtags-key") { followedHashtagsViewModel }
) {
    Box(modifier = Modifier.fillMaxSize()) {

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                .fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = viewModel.followedHashtagsState.isRefreshing,
                onRefresh = { viewModel.getFollowedHashtags(true) },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(top = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        items(viewModel.followedHashtagsState.followedHashtags) { tag ->
                            CustomHashtag(hashtag = tag, navController = navController)
                        }
                    })

                if (viewModel.followedHashtagsState.followedHashtags.isEmpty()) {
                    if (viewModel.followedHashtagsState.isLoading && !viewModel.followedHashtagsState.isRefreshing) {
                        FullscreenLoadingComposable()
                    }

                    if (viewModel.followedHashtagsState.error.isNotEmpty()) {
                        FullscreenErrorComposable(message = viewModel.followedHashtagsState.error)
                    }

                    if (!viewModel.followedHashtagsState.isLoading && viewModel.followedHashtagsState.error.isEmpty()) {
                        FullscreenEmptyStateComposable(
                            EmptyState(
                                icon = Icons.Outlined.Tag,
                                heading = stringResource(Res.string.no_followed_hashtags),
                                message = "Followed hashtags will appear here",
                                buttonText = "Explore trending hashtags",
                                onClick = {
                                    navController.navigate(Destination.Search(2))
                                })
                        )
                    }
                }
            }

        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), title = {
                Text(
                    stringResource(Res.string.followed_hashtags),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                    )
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

    }
}