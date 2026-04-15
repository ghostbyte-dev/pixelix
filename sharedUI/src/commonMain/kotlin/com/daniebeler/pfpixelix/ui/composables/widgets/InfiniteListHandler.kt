package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

private fun shouldLoadMore(totalItems: Int, lastVisibleIndex: Int, buffer: Int): Boolean =
    totalItems != 0 && lastVisibleIndex + 1 > totalItems - buffer

@Composable
fun InfiniteListHandler(
    lazyListState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val shouldLoad by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            shouldLoadMore(
                totalItems = layoutInfo.totalItemsCount,
                lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0,
                buffer = buffer
            )
        }
    }

    if (shouldLoad) {
        LaunchedEffect(lazyListState.layoutInfo.totalItemsCount) {
            onLoadMore()
        }
    }
}

@Composable
fun InfiniteStaggeredGridHandler(
    lazyStaggeredGridState: LazyStaggeredGridState,
    itemCount: Int,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val shouldLoad by remember {
        derivedStateOf {
            val layoutInfo = lazyStaggeredGridState.layoutInfo
            shouldLoadMore(
                totalItems = layoutInfo.totalItemsCount,
                lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0,
                buffer = buffer
            )
        }
    }

    if (shouldLoad) {
        LaunchedEffect(lazyStaggeredGridState.layoutInfo.totalItemsCount, itemCount) {
            onLoadMore()
        }
    }
}
