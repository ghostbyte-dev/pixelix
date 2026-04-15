package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.AccountListScreen
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.muted_accounts
import pixelix.app.generated.resources.no_muted_accounts

@Composable
fun MutedAccountsComposable(
    navController: NavController,
    viewModel: MutedAccountsViewModel = injectViewModel(key = "muted-accounts-key") { mutedAccountsViewModel }
) {
    AccountListScreen(
        title = stringResource(Res.string.muted_accounts),
        navController = navController,
        items = viewModel.mutedAccountsState.mutedAccounts,
        isLoading = viewModel.mutedAccountsState.isLoading,
        isRefreshing = viewModel.mutedAccountsState.isRefreshing,
        error = viewModel.mutedAccountsState.error,
        emptyStateText = stringResource(Res.string.no_muted_accounts),
        onRefresh = { viewModel.getMutedAccounts(true) },
        itemContent = { account ->
            Row { CustomMutedAccountRow(account = account, navController = navController, viewModel = viewModel) }
        }
    )
}
