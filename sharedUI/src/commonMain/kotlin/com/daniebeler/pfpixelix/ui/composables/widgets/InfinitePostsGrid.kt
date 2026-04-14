package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val gridColumns = StaggeredGridCells.Fixed(maxOf(3, (maxWidth / 120.dp).toInt()))

    LazyVerticalStaggeredGrid(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalItemSpacing = 4.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        state = lazyStaggeredGridState,
        columns = gridColumns,
        contentPadding = PaddingValues(top = contentPaddingTop)
    ) {

        if (before != null) {
            item(span = StaggeredGridItemSpan.FullLine) {
                before()
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(4.dp))
        }

        items(items.size, key = { items[it].id }) {
            val photo = items[it]

            CustomPost(
                post = photo,
                navController = navController,
                customModifier = Modifier,
                edit = edit,
                editRemove = { id -> editRemove(id) },
                onClick = onClick
            )
        }

        if (after != null) {
            item(span = StaggeredGridItemSpan.FullLine) {
                after()
            }
        }




        if (endReached && items.size > 10) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EndOfListComposable()
            }
        }

        if (before != null) {
            if (isLoading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    FixedHeightLoadingComposable()
                }
            }


            if (items.isEmpty()) {
                if (!isLoading && error.isEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        FixedHeightEmptyStateComposable(emptyMessage)
                    }
                }
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
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

    }

    InfiniteStaggeredGridHandler(lazyStaggeredGridState = lazyStaggeredGridState, itemCount = items.size) {
        getItemsPaginated()
    }
}
