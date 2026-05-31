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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.post.MasonryPost
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPost
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable

fun LazyStaggeredGridScope.postsWrapperComposable(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    view: ViewEnum,
    postGetsDeleted: (postId: String) -> Unit,
    updatePost: (post: Post) -> Unit,
    isFirstImageLarge: Boolean = false,
    gridColumnCount: Int = 3,
    gridContentWidth: Dp = 0.dp,
    navController: NavController,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {

    if (view == ViewEnum.Grid) {
        postsGridInScope(
            posts = posts,
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
            posts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            endReached = endReached,
            navController = navController
        )
    }
}

private fun LazyStaggeredGridScope.postsGridInScope(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    isFirstImageLarge: Boolean = false,
    columnCount: Int = 3,
    contentWidth: Dp = 0.dp,
    navController: NavController,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = { },
    onClick: ((id: String) -> Unit)? = null
) {
    val spacing = 4.dp
    val featuredCount = if (isFirstImageLarge && posts.size >= 3) {
        val smallColumnsCount = columnCount - 2
        minOf(1 + smallColumnsCount * 2, posts.size)
    } else 0

    if (featuredCount >= 3 && contentWidth > 0.dp) {
        item(span = StaggeredGridItemSpan.FullLine) {
            val columnWidth = (contentWidth - spacing * (columnCount - 1)) / columnCount
            val bigSize = columnWidth * 2 + spacing
            val smallColumnsCount = columnCount - 2

            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                Box(modifier = Modifier.size(bigSize)) {
                    CustomPost(
                        post = posts[0],
                        navController = navController,
                        isFullQuality = true,
                        modifier = Modifier.fillMaxSize(),
                        edit = edit,
                        editRemove = editRemove,
                        onClick = onClick
                    )
                }
                for (col in 0 until smallColumnsCount) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                        val topIdx = 1 + col * 2
                        val bottomIdx = topIdx + 1
                        if (topIdx < featuredCount) {
                            Box(Modifier.size(columnWidth)) {
                                CustomPost(
                                    post = posts[topIdx],
                                    navController = navController,
                                    edit = edit,
                                    editRemove = editRemove,
                                    onClick = onClick
                                )
                            }
                        }
                        if (bottomIdx < featuredCount) {
                            Box(Modifier.size(columnWidth)) {
                                CustomPost(
                                    post = posts[bottomIdx],
                                    navController = navController,
                                    edit = edit,
                                    editRemove = editRemove,
                                    onClick = onClick
                                )
                            }
                        }
                    }
                }
            }
        }

        if (featuredCount < posts.size) {
            val remaining = posts.subList(featuredCount, posts.size)
            items(remaining.size, key = { remaining[it].id }) { index ->
                val columnWidth = (contentWidth - spacing * (columnCount - 1)) / columnCount

                CustomPost(
                    post = remaining[index], navController = navController,
                    edit = edit,
                    editRemove = editRemove,
                    onClick = onClick, modifier = Modifier.size(columnWidth),
                )
            }
        }
    } else {
        items(posts.size, key = { posts[it].id }) { index ->
            CustomPost(
                post = posts[index],
                navController = navController,
                edit = edit,
                editRemove = editRemove,
                onClick = onClick
            )
        }
    }

    if (endReached && posts.size > 10) {
        item(span = StaggeredGridItemSpan.FullLine) {
            EndOfListComposable()
        }
    }

    if (!isRefreshing && isLoading && posts.isNotEmpty()) {
        item(span = StaggeredGridItemSpan.FullLine) {
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
    navController: NavController,
) {
    val spacedBy: Dp = 24.dp

    if (posts.isNotEmpty()) {

        items(posts.size, key = { posts[it].id }) { index ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }
            Box(modifier = Modifier.zIndex(zIndex.floatValue).padding(horizontal = 8.dp)) {
                PostComposable(
                    post = posts[index],
                    postGetsDeleted = postGetsDeleted,
                    navController = navController,
                    updatePost = updatePost,
                    setZindex = {
                        zIndex.floatValue = it
                    })
            }
            Spacer(Modifier.height(spacedBy))
        }

        if (isLoading && !isRefreshing) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
            }
        }

        if (endReached && posts.size > 3) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EndOfListComposable()
            }
        }
    }
}

private fun LazyStaggeredGridScope.postsMasonryInScope(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    endReached: Boolean,
    navController: NavController,
) {

    if (posts.isNotEmpty()) {

        items(posts.size, key = { posts[it].id }) { index ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }
            Box(modifier = Modifier.zIndex(zIndex.floatValue)) {
                MasonryPost(
                    post = posts[index],
                    navController = navController,
                )
            }
        }

        if (isLoading && !isRefreshing) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
            }
        }

        if (endReached && posts.size > 3) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EndOfListComposable()
            }
        }
    }
}
