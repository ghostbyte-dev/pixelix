package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(
    title: String,
    navController: NavController,
    items: List<Account>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    emptyStateText: String,
    onRefresh: () -> Unit,
    itemContent: @Composable (Account) -> Unit
) {
    ScreenScaffold(title = title, navController = navController) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(top = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { account ->
                    itemContent(account)
                }
            }
            if (items.isEmpty()) {
                if (isLoading && !isRefreshing) FullscreenLoadingComposable()
                if (error.isNotEmpty()) FullscreenErrorComposable(message = error)
                if (!isLoading && error.isEmpty()) {
                    FullscreenEmptyStateComposable(EmptyState(heading = emptyStateText))
                }
            }
        }
    }
}
