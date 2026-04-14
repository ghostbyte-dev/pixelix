package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@Composable
fun PostCollectionScreen(
    title: String,
    navController: NavController,
    emptyState: EmptyState,
    items: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit
) {
    ScreenScaffold(title = title, navController = navController) {
        InfinitePostsGrid(
            items = items,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            error = error,
            emptyMessage = emptyState,
            navController = navController,
            getItemsPaginated = onLoadMore,
            onRefresh = onRefresh,
            contentPaddingTop = 24.dp
        )
    }
}
