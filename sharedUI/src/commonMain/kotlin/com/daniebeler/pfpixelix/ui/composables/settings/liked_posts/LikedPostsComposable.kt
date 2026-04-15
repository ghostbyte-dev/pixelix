package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.PostCollectionScreen
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.liked_posts
import pixelix.app.generated.resources.no_liked_posts

@Composable
fun LikedPostsComposable(
    navController: NavController,
    viewModel: LikedPostsViewModel = injectViewModel(key = "likey-posts-key") { likedPostsViewModel }
) {
    PostCollectionScreen(
        title = stringResource(Res.string.liked_posts),
        navController = navController,
        emptyState = EmptyState(icon = Icons.Outlined.FavoriteBorder, heading = stringResource(Res.string.no_liked_posts)),
        items = viewModel.likedPostsState.likedPosts,
        isLoading = viewModel.likedPostsState.isLoading,
        isRefreshing = viewModel.likedPostsState.isRefreshing,
        error = viewModel.likedPostsState.error,
        onLoadMore = { viewModel.getItemsPaginated() },
        onRefresh = { viewModel.getItemsFirstLoad(true) }
    )
}
