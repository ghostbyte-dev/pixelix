package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.profile.PostsWrapperComposable
import com.daniebeler.pfpixelix.ui.composables.profile.SwitchViewComposable
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfinitePostsList(
    items: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean,
    navController: NavController,
    getItemsPaginated: () -> Unit,
    emptyMessage: EmptyState = EmptyState(
        icon = Icons.Outlined.Photo, heading = "No Posts"
    ),
    onRefresh: () -> Unit,
    itemGetsDeleted: (postId: String) -> Unit,
    postGetsUpdated: (post: Post) -> Unit,
    view: ViewEnum = ViewEnum.Timeline,
    changeView: (view: ViewEnum) -> Unit = {},
    isFirstItemLarge: Boolean = false,
    postsCount: Int? = null,
    contentPaddingTop: Dp = 0.dp,
    contentPaddingBottom: Dp = 60.dp,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        BoxWithConstraints {
            val gridContentWidth = maxWidth - 8.dp // account for horizontal padding
            val gridColumnCount = maxOf(3, (gridContentWidth / 120.dp).toInt())
            val columns = when (view) {
                ViewEnum.Grid -> StaggeredGridCells.Fixed(gridColumnCount)
                ViewEnum.Timeline -> StaggeredGridCells.Adaptive(350.dp)
            }

            LazyVerticalStaggeredGrid(
                columns = columns,
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp),
                state = staggeredGridState,
                contentPadding = PaddingValues(
                    top = contentPaddingTop, bottom = contentPaddingBottom
                )
            ) {
                postsCount?.let {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SwitchViewComposable(
                            postsCount = postsCount,
                            viewType = view,
                            onViewChange = { changeView(it) }
                        )
                    }
                }
                if (before != null) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        before()
                    }
                }
                PostsWrapperComposable(
                    posts = items,
                    isLoading = isLoading,
                    isRefreshing = isRefreshing,
                    error = error,
                    endReached = endReached,
                    emptyMessage = emptyMessage,
                    view = view,
                    postGetsDeleted = { itemGetsDeleted(it) },
                    updatePost = { postGetsUpdated(it) },
                    isFirstImageLarge = isFirstItemLarge,
                    gridColumnCount = gridColumnCount,
                    gridContentWidth = gridContentWidth,
                    navController = navController,
                    edit = edit,
                    editRemove = editRemove,
                    onClick = onClick
                )
                if (after != null) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        after()
                    }
                }

            }
        }
        ToTopButton(staggeredGridState) { onRefresh() }
    }

    InfiniteStaggeredGridHandler(
        lazyStaggeredGridState = staggeredGridState,
        itemCount = items.size
    ) {
        getItemsPaginated()
    }
}
