package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.CustomPost
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FixedHeightEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FixedHeightLoadingComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable

fun LazyListScope.PostsWrapperComposable(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean,
    emptyMessage: EmptyState,
    view: ViewEnum,
    postGetsDeleted: (postId: String) -> Unit,
    updatePost: (post: Post) -> Unit,
    isFirstImageLarge: Boolean = false,
    navController: NavController
) {

    if (view == ViewEnum.Grid) {
        PostsGridInScope(
            posts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            error = error,
            endReached = endReached,
            emptyMessage = emptyMessage,
            isFirstImageLarge = isFirstImageLarge,
            navController = navController
        )
    }

    if (view == ViewEnum.Timeline) {
        PostsListInScope(
            posts = posts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            error = error,
            endReached = endReached,
            emptyMessage = emptyMessage,
            postGetsDeleted = postGetsDeleted,
            updatePost = updatePost,
            navController = navController
        )
    }
}

private fun LazyListScope.PostsGridInScope(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean,
    emptyMessage: EmptyState,
    isFirstImageLarge: Boolean = false,
    navController: NavController
) {
    if (isFirstImageLarge && posts.size >= 3) {
        var largePostRoundedCorners = Modifier.clip(RoundedCornerShape(topStart = 16.dp))
        var thirdPostRoundedCorners: Modifier = Modifier
        if (posts.size == 3) {
            largePostRoundedCorners =
                largePostRoundedCorners.clip(RoundedCornerShape(bottomStart = 16.dp))
            thirdPostRoundedCorners =
                thirdPostRoundedCorners.clip(RoundedCornerShape(bottomEnd = 16.dp))

        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(1.99f / 3f)) {
                    CustomPost(
                        post = posts.first(),
                        navController = navController,
                        isFullQuality = true,
                        customModifier = largePostRoundedCorners
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CustomPost(
                        post = posts[1],
                        navController = navController,
                        customModifier = Modifier.clip(
                            RoundedCornerShape(
                                topEnd = 16.dp
                            )
                        )
                    )
                    CustomPost(
                        post = posts[2],
                        navController = navController,
                        customModifier = thirdPostRoundedCorners
                    )

                }
            }
        }


        val rows = posts.takeLast(posts.size - 3).chunked(3)
        itemsIndexed(rows) { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Fill the row with 3 items (or fewer for the last row)
                rowItems.forEachIndexed { index, post ->

                    var roundedCorners: Modifier = Modifier
                    val isBottomLeft = rowItems == rows.last() && index == 0
                    val isBottomRight =
                        (rowItems == rows.last() && (index == 2 || index == rowItems.size - 1)) || (index == 2 && posts.size - (rowIndex + 2) * 3 < 3)

                    if (isBottomLeft) {
                        roundedCorners =
                            roundedCorners.clip(RoundedCornerShape(bottomStart = 16.dp))
                    }
                    if (isBottomRight) {
                        roundedCorners = roundedCorners.clip(RoundedCornerShape(bottomEnd = 16.dp))
                    }

                    Box(
                        Modifier.padding(horizontal = 2.dp).weight(1f)
                    ) {

                        CustomPost(
                            post = post,
                            navController = navController,
                            customModifier = roundedCorners
                        )
                    }
                }

                repeat(3 - rowItems.size) {
                    Spacer(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    )
                }
            }
        }


    } else {
        val rows = posts.chunked(3)
        items(rows) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                rowItems.forEachIndexed { index, post ->

                    val isTopLeft = rowItems == rows.first() && index == 0
                    val isTopRight =
                        rowItems == rows.first() && index == 2 || index == rowItems.size - 1
                    val isBottomLeft = rowItems == rows.last() && index == 0
                    val isBottomRight =
                        rowItems == rows.last() && index == 2 || index == rowItems.size - 1

                    var roundedCorners: Modifier = Modifier

                    if (isTopLeft) {
                        roundedCorners = roundedCorners.clip(RoundedCornerShape(topStart = 16.dp))
                    }
                    if (isBottomLeft) {
                        roundedCorners =
                            roundedCorners.clip(RoundedCornerShape(bottomStart = 16.dp))
                    }
                    if (isTopRight) {
                        roundedCorners = roundedCorners.clip(RoundedCornerShape(topEnd = 16.dp))
                    }
                    if (isBottomRight) {
                        roundedCorners = roundedCorners.clip(RoundedCornerShape(bottomEnd = 16.dp))
                    }

                    Box(
                        Modifier.padding(horizontal = 2.dp).weight(1f)
                    ) {

                        CustomPost(
                            post = post,
                            navController = navController,
                            customModifier = roundedCorners
                        )
                    }
                }

                // Add empty Composables for spacing if the last row has fewer than 3 items
                repeat(3 - rowItems.size) {
                    Spacer(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }


    if (endReached && posts.size > 10) {
        item {
            EndOfListComposable()
        }
    }

    if (!isRefreshing && isLoading) {
        item {
            FixedHeightLoadingComposable()
        }
    }

    if (posts.isEmpty()) {
        if (!isLoading && error.isEmpty()) {
            item {
                FixedHeightEmptyStateComposable(emptyMessage)
            }
        }
    }

    if (error.isNotEmpty() && posts.isEmpty()) {
        item {
            ErrorComposable(error)
        }
    }
}


private fun LazyListScope.PostsListInScope(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    endReached: Boolean,
    emptyMessage: EmptyState,
    postGetsDeleted: (postId: String) -> Unit,
    updatePost: (post: Post) -> Unit,
    navController: NavController
) {
    val spacedBy: Dp = 24.dp

    if (posts.isNotEmpty()) {

        items(posts, key = {
            it.id
        }) { item ->
            val zIndex = remember {
                mutableFloatStateOf(1f)
            }
            Box(modifier = Modifier.zIndex(zIndex.floatValue).padding(horizontal = 8.dp)) {
                PostComposable(post = item,
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
            item {
                FixedHeightLoadingComposable()
            }
        }

        if (endReached && posts.size > 3) {
            item {
                EndOfListComposable()
            }
        }
    }

    if (posts.isEmpty() && !isLoading && error.isEmpty()) {
        item {
            FullscreenEmptyStateComposable(emptyMessage)
        }
    }

    if (!isRefreshing && posts.isEmpty() && isLoading) {
        item {
            FullscreenLoadingComposable()
        }
    }

    if (error.isNotEmpty() && posts.isEmpty()) {
        item {
            ErrorComposable(error)
        }
    }
}