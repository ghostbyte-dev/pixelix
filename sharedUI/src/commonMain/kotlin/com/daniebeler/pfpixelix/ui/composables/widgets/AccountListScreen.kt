package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator

@Composable
fun AccountListScreen(
    title: String,
    navController: AppNavigator,
    items: List<Account>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String,
    emptyStateText: String,
    onRefresh: () -> Unit,
    itemContent: @Composable (Account) -> Unit
) {
    val listState = rememberLazyListState()

    ScreenScaffold(title = title, navController = navController) {
        CustomPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
            animatedBox = true,
            enabled = PlatformFeatures.supportsPullToRefresh
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 24.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(items, key = { it.id }) { account ->
                    Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                        itemContent(account)
                    }
                }
            }
            if (items.isEmpty()) {
                if (isLoading && !isRefreshing) LoadingComposable()
                if (error.isNotEmpty()) ErrorComposable(message = error, modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp))
                if (!isLoading && error.isEmpty()) {
                    EmptyStateComposable(EmptyState(heading = emptyStateText))
                }
            }
        }
    }
}
