package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

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
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.muted_accounts
import pixelix.app.generated.resources.no_muted_accounts

@Composable
fun MutedAccountsComposable(
    navController: AppNavigator,
    viewModel: MutedAccountsViewModel = injectViewModel(key = "muted-accounts-key") { mutedAccountsViewModel }
) {
    val listState = rememberLazyListState()

    ScreenScaffold(
        title = stringResource(Res.string.muted_accounts), navController = navController
    ) {
        CustomPullToRefreshBox(
            isRefreshing = viewModel.mutedAccountsState.isRefreshing,
            onRefresh = { viewModel.getMutedAccounts(true) },
            modifier = Modifier.fillMaxSize(),
            enabled = PlatformFeatures.supportsPullToRefresh,
            animatedBox = true
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 24.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(viewModel.mutedAccountsState.mutedAccounts, key = { it.id }) { account ->
                    Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                        CustomMutedAccountRow(
                            mutedAccount = account,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
            if (viewModel.mutedAccountsState.mutedAccounts.isEmpty()) {
                if (viewModel.mutedAccountsState.isLoading && !viewModel.mutedAccountsState.isRefreshing) LoadingComposable()
                if (viewModel.mutedAccountsState.error.isNotEmpty()) ErrorComposable(
                    message = viewModel.mutedAccountsState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
                if (!viewModel.mutedAccountsState.isLoading && viewModel.mutedAccountsState.error.isEmpty()) {
                    EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_muted_accounts)))
                }
            }
        }
    }
}
