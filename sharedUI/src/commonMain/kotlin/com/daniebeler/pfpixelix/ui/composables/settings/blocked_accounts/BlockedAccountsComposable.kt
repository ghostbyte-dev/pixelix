package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.blocked_accounts
import pixelix.app.generated.resources.no_blocked_accounts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedAccountsComposable(
    navController: NavController,
    viewModel: BlockedAccountsViewModel = injectViewModel(key = "blocked-accounts-key") { blockedAccountsViewModel }
) {
    Box(modifier = Modifier.fillMaxSize()) {

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Box(
            modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                .fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = viewModel.blockedAccountsState.isRefreshing,
                onRefresh = { viewModel.getBlockedAccounts(true) },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 24.dp)
                ) {
                    items(viewModel.blockedAccountsState.blockedAccounts, key = {
                        it.id
                    }) {
                        Row {
                            CustomBlockedAccountRow(
                                account = it, navController = navController, viewModel
                            )
                        }
                    }
                }
            }

        }

        TopAppBar(
            modifier = Modifier.clip(
            RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ), title = {
            Text(
                text = stringResource(Res.string.blocked_accounts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                )
            }
        }, colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
        )

        if (viewModel.blockedAccountsState.blockedAccounts.isEmpty()) {
            if (viewModel.blockedAccountsState.isLoading && !viewModel.blockedAccountsState.isRefreshing) {
                FullscreenLoadingComposable()
            }

            if (viewModel.blockedAccountsState.error.isNotEmpty()) {
                FullscreenErrorComposable(message = viewModel.blockedAccountsState.error)
            }

            if (!viewModel.blockedAccountsState.isLoading && viewModel.blockedAccountsState.error.isEmpty()) {
                FullscreenEmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_blocked_accounts)))
            }
        }
    }
}