package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.PostCollectionScreen
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.bookmarked_posts
import pixelix.app.generated.resources.no_bookmarked_posts

@Composable
fun BookmarkedPostsComposable(
    navController: NavController,
    viewModel: BookmarkedPostsViewModel = injectViewModel(key = "bookmarksviewmodel") { bookmarkedPostsViewModel }
) {
    PostCollectionScreen(
        title = stringResource(Res.string.bookmarked_posts),
        navController = navController,
        emptyState = EmptyState(heading = stringResource(Res.string.no_bookmarked_posts)),
        items = viewModel.bookmarkedPostsState.bookmarkedPosts,
        isLoading = viewModel.bookmarkedPostsState.isLoading,
        isRefreshing = viewModel.bookmarkedPostsState.isRefreshing,
        error = viewModel.bookmarkedPostsState.error,
        onLoadMore = { /*TODO*/ },
        onRefresh = { viewModel.getBookmarkedPosts(true) }
    )
}
