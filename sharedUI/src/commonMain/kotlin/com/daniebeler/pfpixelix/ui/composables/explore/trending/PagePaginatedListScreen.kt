package com.daniebeler.pfpixelix.ui.composables.explore.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PagePaginatedListScreen(
    state: PagePaginatedState<T>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    emptyMessage: String,
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(state.items, key = itemKey) { item ->
                itemContent(item)
            }

            if (state.isLoading && state.items.isNotEmpty()) {
                item { LoadingComposable() }
            }
        }

        if (state.items.isEmpty()) {
            if (state.isLoading && !state.isRefreshing) LoadingComposable()
            if (state.error.isNotEmpty()) {
                ErrorComposable(message = state.error, modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp))
            }
            if (!state.isLoading && state.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = emptyMessage))
            }
        }
    }

    InfiniteListHandler(lazyListState = lazyListState) {
        onLoadMore()
    }
}