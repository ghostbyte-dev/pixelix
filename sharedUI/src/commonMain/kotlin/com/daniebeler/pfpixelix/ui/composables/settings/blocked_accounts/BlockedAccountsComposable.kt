package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.AccountListScreen
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.blocked_accounts
import pixelix.app.generated.resources.no_blocked_accounts

@Composable
fun BlockedAccountsComposable(
    navController: AppNavigator,
    viewModel: BlockedAccountsViewModel = injectViewModel(key = "blocked-accounts-key") { blockedAccountsViewModel }
) {
    AccountListScreen(
        title = stringResource(Res.string.blocked_accounts),
        navController = navController,
        items = viewModel.blockedAccountsState.blockedAccounts,
        isLoading = viewModel.blockedAccountsState.isLoading,
        isRefreshing = viewModel.blockedAccountsState.isRefreshing,
        error = viewModel.blockedAccountsState.error,
        emptyStateText = stringResource(Res.string.no_blocked_accounts),
        onRefresh = { viewModel.getBlockedAccounts(true) },
        itemContent = { account ->
            Row { CustomBlockedAccountRow(account = account, navController = navController, viewModel = viewModel) }
        }
    )
}
