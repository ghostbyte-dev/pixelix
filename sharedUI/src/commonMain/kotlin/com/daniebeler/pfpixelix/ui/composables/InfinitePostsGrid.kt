package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.FixedHeightEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FixedHeightLoadingComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfinitePostsGrid(
    items: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean = false,
    emptyMessage: EmptyState,
    navController: NavController,
    getItemsPaginated: () -> Unit = { },
    contentPaddingTop: Dp = 0.dp,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    onRefresh: () -> Unit = { },
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null,
    pullToRefresh: Boolean = true
) {

    if (pullToRefresh) {
        PullToRefreshBox(
            isRefreshing = isRefreshing, onRefresh = { onRefresh() }, modifier = Modifier.fillMaxSize()
        ) {
            privateInfinitePostsGrid(items, isLoading, isRefreshing, error, endReached, emptyMessage, navController, getItemsPaginated, contentPaddingTop, before, after, edit, editRemove, onClick)
        }
    } else {
        Box(
          modifier = Modifier.fillMaxSize()
        ) {
            privateInfinitePostsGrid(items, isLoading, isRefreshing, error, endReached, emptyMessage, navController, getItemsPaginated, contentPaddingTop, before, after, edit, editRemove, onClick)
        }
    }
}

@Composable
fun privateInfinitePostsGrid(
    items: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean = false,
    emptyMessage: EmptyState,
    navController: NavController,
    getItemsPaginated: () -> Unit = { },
    contentPaddingTop: Dp,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {
    val lazyGridState = rememberLazyGridState()

    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        state = lazyGridState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(top = contentPaddingTop)
    ) {

        if (before != null) {
            item(span = { GridItemSpan(3) }) {
                before()
            }
        }

        item(span = { GridItemSpan(3) }) {
            Spacer(Modifier.height(4.dp))
        }

        itemsIndexed(items) { index, photo ->

            val isTopLeft = index == 0
            val isTopRight = index == 2
            val isBottomLeft = index >= items.size - 3 && index % 3 == 0
            val isBottomRight =
                (index == items.size - 1) || (index % 3 == 2 && items.size - index < 3)

            var roundedCorners: Modifier = Modifier

            if (isTopLeft) {
                roundedCorners = roundedCorners.clip(RoundedCornerShape(topStart = 16.dp))
            }
            if (isBottomLeft) {
                roundedCorners = roundedCorners.clip(RoundedCornerShape(bottomStart = 16.dp))
            }
            if (isTopRight) {
                roundedCorners = roundedCorners.clip(RoundedCornerShape(topEnd = 16.dp))
            }
            if (isBottomRight) {
                roundedCorners = roundedCorners.clip(RoundedCornerShape(bottomEnd = 16.dp))
            }

            CustomPost(
                post = photo,
                navController = navController,
                customModifier = roundedCorners,
                edit = edit,
                editRemove = { id -> editRemove(id) },
                onClick = onClick
            )
        }

        if (after != null) {
            item(span = { GridItemSpan(3) }) {
                after()
            }
        }




        if (endReached && items.size > 10) {
            item(span = { GridItemSpan(3) }) {
                EndOfListComposable()
            }
        }

        if (before != null) {
            if (isLoading) {
                item(span = { GridItemSpan(3) }) {
                    FixedHeightLoadingComposable()
                }
            }


            if (items.isEmpty()) {
                if (!isLoading && error.isEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        FixedHeightEmptyStateComposable(emptyMessage)
                    }
                }
            }
        }

        item(span = { GridItemSpan(3) }) {
            Spacer(Modifier.height(12.dp))
        }
    }

    if (items.isEmpty() && error.isNotBlank()) {
        FullscreenErrorComposable(message = error)
    }

    if (before == null && items.isEmpty()) {
        if (isLoading && !isRefreshing) {
            FullscreenLoadingComposable()
        }

        if (!isLoading && error.isEmpty()) {
            FullscreenEmptyStateComposable(emptyMessage)
        }
    }

    InfiniteGridHandler(lazyGridState = lazyGridState) {
        getItemsPaginated()
    }
}
