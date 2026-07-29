package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.bookmark
import pixelix.app.generated.resources.bookmarked_posts
import pixelix.app.generated.resources.liked_posts
import pixelix.app.generated.resources.no_bookmarked_posts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedPostsComposable(
    navController: NavController,
    viewModel: BookmarkedPostsViewModel = injectViewModel(key = "bookmarksviewmodel") { bookmarkedPostsViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                title = { Text(stringResource(Res.string.bookmarked_posts), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_left),
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            InfinitePostsList(
                items = viewModel.bookmarkedPostsState.bookmarkedPosts,
                postsCount = viewModel.bookmarkedPostsState.bookmarkedPosts.count(),
                view = viewModel.view,
                changeView = { viewModel.changeView(it) },
                isLoading = viewModel.bookmarkedPostsState.isLoading,
                isRefreshing = viewModel.bookmarkedPostsState.isRefreshing,
                error = viewModel.bookmarkedPostsState.error,
                emptyMessage = EmptyState(
                    icon = vectorResource(Res.drawable.bookmark), heading = stringResource(Res.string.no_bookmarked_posts)
                ),
                endReached = false,
                itemGetsDeleted = {},
                postGetsUpdated = {},
                navController = navController,
                getItemsPaginated = {
                    viewModel.getItemsPaginated()
                },
                isFirstItemLarge = true,
                onRefresh = {
                    viewModel.refresh()
                },
            )
        }
    }
    /*
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
    )*/
}
