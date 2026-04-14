package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun InfiniteListHandler(
    lazyListState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val shouldLoad by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            totalItems != 0 && lastVisibleItemIndex > (totalItems - buffer)
        }
    }

    if (shouldLoad) {
        val totalItems = lazyListState.layoutInfo.totalItemsCount
        LaunchedEffect(totalItems) {
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
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            totalItems != 0 && lastVisibleItemIndex > (totalItems - buffer)
        }
    }

    if (shouldLoad) {
        val totalItems = lazyStaggeredGridState.layoutInfo.totalItemsCount
        LaunchedEffect(totalItems, itemCount) {
            onLoadMore()
        }
    }
}
