package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.profile.postsWrapperComposable
import com.daniebeler.pfpixelix.ui.composables.profile.SwitchViewComposable
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.photo

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
        icon = vectorResource(Res.drawable.photo), heading = "No Posts"
    ),
    onRefresh: (() -> Unit),
    refreshable: Boolean = true,
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
    onClick: ((id: String) -> Unit)? = null,
    staggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState()
) {

    if (error.isEmpty() || items.isNotEmpty()) {
        CustomPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            animatedBox = true,
            enabled = refreshable
        ) {
            BoxWithConstraints {
                val gridContentWidth = maxWidth - 8.dp // account for horizontal padding
                val gridColumnCount = maxOf(3, (gridContentWidth / 120.dp).toInt())
                val columns = when (view) {
                    ViewEnum.Grid -> StaggeredGridCells.Fixed(gridColumnCount)
                    ViewEnum.Masonry -> StaggeredGridCells.Adaptive(150.dp)
                    ViewEnum.LargeMasonry -> StaggeredGridCells.Adaptive(350.dp)
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
                    if (before != null) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            before()
                        }
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        SwitchViewComposable(
                            postsCount = postsCount,
                            viewType = view,
                            onViewChange = { changeView(it) }
                        )
                    }

                    postsWrapperComposable(
                        posts = items,
                        isLoading = isLoading,
                        isRefreshing = isRefreshing,
                        endReached = endReached,
                        view = view,
                        postGetsDeleted = { itemGetsDeleted(it) },
                        updatePost = { postGetsUpdated(it) },
                        isFirstImageLarge = isFirstItemLarge,
                        gridColumnCount = gridColumnCount,
                        gridContentWidth = gridContentWidth,
                        navController = navController,
                        edit = edit, editRemove = editRemove,
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
    }

    InfiniteStaggeredGridHandler(
        lazyStaggeredGridState = staggeredGridState,
        itemCount = items.size
    ) {
        getItemsPaginated()
    }

    if (items.isEmpty() && !isLoading && error.isEmpty()) {
        EmptyStateComposable(emptyMessage, isRefreshing = isRefreshing, onRefresh = onRefresh)
    }

    if (!isRefreshing && items.isEmpty() && isLoading) {
        LoadingComposable()
    }


    if (error.isNotEmpty() && items.isEmpty()) {
        ErrorComposable(error, onRefresh = onRefresh, isRefreshing = isRefreshing)
    }
}
