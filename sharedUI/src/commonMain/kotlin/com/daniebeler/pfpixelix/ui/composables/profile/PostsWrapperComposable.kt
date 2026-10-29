package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.uiKey
import com.daniebeler.pfpixelix.ui.composables.post.MasonryPost
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPost
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.post.PostViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import dev.zacsweers.metrox.viewmodel.metroViewModel

// Posts are deduplicated by the caller; non-timeline views receive only posts with media.
fun LazyStaggeredGridScope.postsWrapperComposable(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    view: ViewEnum,
    postGetsDeleted: (postId: String) -> Unit,
    updatePost: (post: Post) -> Unit,
    isFirstImageLarge: Boolean = false,
    gridColumnCount: Int,
    gridContentWidth: Dp,
    navController: AppNavigator,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {
    if (view == ViewEnum.Grid) {
        postsGridInScope(
            filteredPosts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            endReached = endReached,
            isFirstImageLarge = isFirstImageLarge,
            columnCount = gridColumnCount,
            contentWidth = gridContentWidth,
            navController = navController,
            edit = edit,
            editRemove = editRemove,
            onClick = onClick
        )
    }

    if (view == ViewEnum.Timeline) {
        postsListInScope(
            posts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            endReached = endReached,
            postGetsDeleted = postGetsDeleted,
            updatePost = updatePost,
            navController = navController
        )
    }

    if (view == ViewEnum.Masonry) {
        postsMasonryInScope(
            filteredPosts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            endReached = endReached,
            columnCount = gridColumnCount,
            navController = navController
        )
    }

    if (view == ViewEnum.LargeMasonry) {
        postsLargeMasonryInScope(
            filteredPosts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            endReached = endReached,
            columnCount = gridColumnCount,
            navController = navController
        )
    }
}

private fun LazyStaggeredGridScope.postsGridInScope(
    filteredPosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    isFirstImageLarge: Boolean = false,
    columnCount: Int = 3,
    contentWidth: Dp = 0.dp,
    navController: AppNavigator,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {
    val spacing = 4.dp
    val cornerRadius = 16.dp

    val columnWidth = if (contentWidth > 0.dp) {
        (contentWidth - spacing * (columnCount - 1)) / columnCount
    } else {
        0.dp
    }

    val featuredCount = if (isFirstImageLarge && filteredPosts.size >= 3) {
        val smallColumnsCount = columnCount - 2
        minOf(1 + smallColumnsCount * 2, filteredPosts.size)
    } else 0

    if (featuredCount >= 3 && contentWidth > 0.dp) {
        item(
            key = "first_line_key",
            contentType = "featured_posts",
            span = StaggeredGridItemSpan.FullLine
        ) {
            val bigSize = columnWidth * 2 + spacing
            val smallColumnsCount = columnCount - 2
            val remainingCount = filteredPosts.size - featuredCount

            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {

                // 1. LARGE IMAGE
                Box(modifier = Modifier.size(bigSize)) {
                    val largeShape = RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomStart = if (remainingCount == 0) cornerRadius else 0.dp,
                        bottomEnd = 0.dp
                    )

                    CustomPost(
                        post = filteredPosts[0],
                        navController = navController,
                        isFullQuality = true,
                        modifier = Modifier.fillMaxSize(),
                        edit = edit,
                        editRemove = editRemove,
                        onClick = onClick,
                        roundedCornerShape = largeShape
                    )
                }

                // 2. SMALL COLUMNS
                for (col in 0 until smallColumnsCount) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                        val topIdx = 1 + col * 2
                        val bottomIdx = topIdx + 1
                        val isLastColumn = col == smallColumnsCount - 1

                        if (topIdx < featuredCount) {
                            Box(Modifier.size(columnWidth)) {
                                val isAbsoluteLast = topIdx == filteredPosts.size - 1
                                val topShape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = if (isLastColumn) cornerRadius else 0.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = if (isLastColumn && isAbsoluteLast) cornerRadius else 0.dp
                                )

                                CustomPost(
                                    post = filteredPosts[topIdx],
                                    navController = navController,
                                    edit = edit,
                                    editRemove = editRemove,
                                    onClick = onClick,
                                    roundedCornerShape = topShape
                                )
                            }
                        }
                        if (bottomIdx < featuredCount) {
                            Box(Modifier.size(columnWidth)) {
                                val isAbsoluteLast = bottomIdx == filteredPosts.size - 1
                                val bottomShape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = if (isLastColumn && (remainingCount == 0 || isAbsoluteLast)) cornerRadius else 0.dp
                                )

                                CustomPost(
                                    post = filteredPosts[bottomIdx],
                                    navController = navController,
                                    edit = edit,
                                    editRemove = editRemove,
                                    onClick = onClick,
                                    roundedCornerShape = bottomShape
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. REMAINING ITEMS (Below the featured block)
        if (featuredCount < filteredPosts.size) {
            val remaining = filteredPosts.subList(featuredCount, filteredPosts.size)
            itemsIndexed(
                items = remaining,
                key = { _, post -> post.uiKey },
                contentType = { _, _ -> "grid_post" }
            ) { remIndex, post ->
                val remCol = remIndex % columnCount
                val isBottomRow = remIndex + columnCount >= remaining.size
                val isAbsoluteLast = remIndex == remaining.size - 1

                // Top corners are forced flat because they merge perfectly with the featured block above
                val remShape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = if (isBottomRow && remCol == 0) cornerRadius else 0.dp,
                    bottomEnd = if (isBottomRow && (remCol == columnCount - 1 || isAbsoluteLast)) cornerRadius else 0.dp
                )

                CustomPost(
                    post = post,
                    navController = navController,
                    edit = edit,
                    editRemove = editRemove,
                    onClick = onClick,
                    modifier = Modifier.height(columnWidth),
                    roundedCornerShape = remShape
                )
            }
        }
    } else {
        itemsIndexed(
            items = filteredPosts,
            key = { _, post -> post.uiKey },
            contentType = { _, _ -> "grid_post" }
        ) { index, post ->
            val shape = remember(index, filteredPosts.size, columnCount) {
                calculateOuterGridShape(index, filteredPosts.size, columnCount)
            }
            CustomPost(
                post = post,
                navController = navController,
                edit = edit,
                editRemove = editRemove,
                onClick = onClick,
                modifier = Modifier.height(columnWidth),
                roundedCornerShape = shape
            )
        }
    }

    if (endReached && filteredPosts.size > 10) {
        item(
            key = "end_of_list_key", contentType = "end_of_list", span = StaggeredGridItemSpan.FullLine
        ) { EndOfListComposable() }
    }

    if (!isRefreshing && isLoading) {
        item(key = "loading_key", contentType = "loading", span = StaggeredGridItemSpan.FullLine) {
            LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
        }
    }
}

private fun LazyStaggeredGridScope.postsListInScope(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    postGetsDeleted: (postId: String) -> Unit,
    updatePost: (post: Post) -> Unit,
    navController: AppNavigator,
) {
    val spacedBy: Dp = 24.dp

    if (posts.isNotEmpty()) {

        items(posts, key = { it.uiKey }, contentType = { "timeline_post" }) { post ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }
            Box(modifier = Modifier.zIndex(zIndex.floatValue).padding(horizontal = 8.dp)) {
                PostComposable(
                    post = post,
                    postGetsDeleted = postGetsDeleted,
                    viewModel = rememberLazyPostViewModel(post.uiKey),
                    navController = navController,
                    updatePost = updatePost,
                    setZindex = {
                        zIndex.floatValue = it
                    },
                    fullQuality = false
                )
            }
            Spacer(Modifier.height(spacedBy))
        }
    }

    if (isLoading && !isRefreshing) {
        item(key = "loading_key", contentType = "loading", span = StaggeredGridItemSpan.FullLine) {
            LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
        }
    }

    if (endReached && posts.size > 3) {
        item(key = "end_of_list_key", contentType = "end_of_list", span = StaggeredGridItemSpan.FullLine) {
            EndOfListComposable()
        }
    }
}

private fun LazyStaggeredGridScope.postsMasonryInScope(
    filteredPosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    columnCount: Int,
    navController: AppNavigator,
) {
    if (filteredPosts.isNotEmpty()) {

        itemsIndexed(
            items = filteredPosts,
            key = { _, post -> post.uiKey },
            contentType = { _, _ -> "masonry_post" }
        ) { index, post ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }

            val shape = remember(index, filteredPosts.size, columnCount) {
                calculateOuterGridShape(
                    index = index,
                    totalCount = filteredPosts.size,
                    columnCount = columnCount
                )
            }

            Box(modifier = Modifier.zIndex(zIndex.floatValue)) {
                MasonryPost(
                    post = post,
                    viewModel = rememberLazyPostViewModel(post.uiKey),
                    roundedCornerShape = shape,
                    navController = navController,
                )
            }
        }
    }

    if (isLoading && !isRefreshing) {
        item(key = "loading_list_key", contentType = "loading", span = StaggeredGridItemSpan.FullLine) {
            LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
        }
    }

    if (endReached && filteredPosts.size > 3) {
        item(key = "end_of_list_key", contentType = "end_of_list", span = StaggeredGridItemSpan.FullLine) {
            EndOfListComposable()
        }
    }
}

private fun LazyStaggeredGridScope.postsLargeMasonryInScope(
    filteredPosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    columnCount: Int = 1,
    navController: AppNavigator,
) {
    if (filteredPosts.isNotEmpty()) {

        itemsIndexed(
            items = filteredPosts,
            key = { _, post -> post.uiKey },
            contentType = { _, _ -> "large_masonry_post" }
        ) { index, post ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }

            val shape = remember(index, filteredPosts.size, columnCount) {
                calculateOuterGridShape(
                    index = index,
                    totalCount = filteredPosts.size,
                    columnCount = columnCount
                )
            }

            Box(modifier = Modifier.zIndex(zIndex.floatValue)) {
                MasonryPost(
                    post = post,
                    viewModel = rememberLazyPostViewModel(post.uiKey),
                    roundedCornerShape = shape,
                    navController = navController,
                )
            }
        }
    }
    if (isLoading && !isRefreshing) {
        item(key = "loading_key", contentType = "loading", span = StaggeredGridItemSpan.FullLine) {
            LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
        }
    }

    if (endReached && filteredPosts.size > 3) {
        item(key = "end_of_list_key", contentType = "end_of_list", span = StaggeredGridItemSpan.FullLine) {
            EndOfListComposable()
        }
    }
}

@Composable
private fun rememberLazyPostViewModel(key: String): PostViewModel {
    //local ViewModelStoreOwner. VM will be destroyed when the composable is gone
    val owner = rememberViewModelStoreOwner()
    return metroViewModel(viewModelStoreOwner = owner, key = key)
}

fun calculateOuterGridShape(
    index: Int, totalCount: Int, columnCount: Int, cornerRadius: Dp = 16.dp
): RoundedCornerShape {
    if (totalCount <= 1) return RoundedCornerShape(cornerRadius)

    val column = index % columnCount
    val isTopRow = index < columnCount

    val remainder = totalCount % columnCount
    val lastRowSize = if (remainder == 0) columnCount else remainder
    val lastRowStartIndex = totalCount - lastRowSize
    val isBottomRow = index >= lastRowStartIndex

    val topLeft = if (isTopRow && column == 0) cornerRadius else 0.dp
    val topRight =
        if (isTopRow && (column == columnCount - 1 || index == totalCount - 1)) cornerRadius else 0.dp
    val bottomLeft = if (isBottomRow && column == 0) cornerRadius else 0.dp
    val bottomRight =
        if (isBottomRow && (column == columnCount - 1 || index == totalCount - 1)) cornerRadius else 0.dp

    return RoundedCornerShape(
        topStart = topLeft, topEnd = topRight, bottomStart = bottomLeft, bottomEnd = bottomRight
    )
}