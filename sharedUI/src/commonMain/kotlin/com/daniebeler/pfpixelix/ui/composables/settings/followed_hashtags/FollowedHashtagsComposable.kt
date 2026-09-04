package com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomHashtag
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.followed_hashtags
import pixelix.app.generated.resources.hash
import pixelix.app.generated.resources.no_followed_hashtags

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FollowedHashtagsComposable(
    navController: AppNavigator,
    viewModel: FollowedHashtagsViewModel = injectViewModel(key = "followed-hashtags-key") { followedHashtagsViewModel }
) {
    ScreenScaffold(title = stringResource(Res.string.followed_hashtags), navController = navController) {
        CustomPullToRefreshBox(
            isRefreshing = viewModel.followedHashtagsState.isRefreshing,
            onRefresh = { viewModel.getFollowedHashtags(true) },
            modifier = Modifier.fillMaxSize(),
            animatedBox = true
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(300.dp),
                verticalItemSpacing = 4.dp,
                contentPadding = PaddingValues(top = 24.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                    items(viewModel.followedHashtagsState.followedHashtags) { tag ->
                        CustomHashtag(hashtag = tag, navController = navController)
                    }
                }

            if (viewModel.followedHashtagsState.followedHashtags.isEmpty()) {
                if (viewModel.followedHashtagsState.isLoading && !viewModel.followedHashtagsState.isRefreshing) {
                    LoadingComposable()
                }

                if (viewModel.followedHashtagsState.error.isNotEmpty()) {
                    ErrorComposable(message = viewModel.followedHashtagsState.error, modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp))
                }

                if (!viewModel.followedHashtagsState.isLoading && viewModel.followedHashtagsState.error.isEmpty()) {
                    EmptyStateComposable(
                        EmptyState(
                            icon = vectorResource(Res.drawable.hash),
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
}
