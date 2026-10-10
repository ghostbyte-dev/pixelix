package com.daniebeler.pfpixelix.ui.composables.explore.trending.editors_choice_accounts

import androidx.compose.foundation.layout.Arrangement
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
import com.daniebeler.pfpixelix.ui.composables.MaxWidthTopBar
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountElement
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.TimelineHelpCard
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.editors_choice_accounts
import pixelix.app.generated.resources.editors_choice_accounts_explained
import pixelix.app.generated.resources.no_trending_profiles

@Composable
fun EditorsChoiceAccountsComposable(
    navController: AppNavigator,
    viewModel: EditorsChoiceAccountsViewModel = injectViewModel(key = "editors-choice-accounts-key") { editorsChoiceAccountsViewModel }
) {

    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.accountsState.isRefreshing,
        onRefresh = { viewModel.getAccountsState(true) },
        animatedBox = true,
        enabled = PlatformFeatures.supportsPullToRefresh
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                if (viewModel.showHelp) {
                    item {
                        MaxWidthTopBar(hasBackground = false) {
                            TimelineHelpCard(
                                title = stringResource(Res.string.editors_choice_accounts),
                                description = stringResource(Res.string.editors_choice_accounts_explained),
                                onDiscard = {
                                    viewModel.discardHelp()
                                })
                        }
                    }
                }
                items(viewModel.accountsState.accounts, key = {
                    it.id
                }) {
                    Box(modifier = Modifier.widthIn(max = 800.dp).fillMaxWidth()) {
                        TrendingAccountElement(
                            account = it, navController = navController
                        )
                    }
                }
            })
        if (viewModel.accountsState.accounts.isEmpty()) {
            if (viewModel.accountsState.isLoading && !viewModel.accountsState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.accountsState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.accountsState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.accountsState.isLoading && viewModel.accountsState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_trending_profiles)))
            }
        }
    }

    InfiniteListHandler(
        lazyListState = lazyListState
    ) {
        viewModel.getAccountsPaginated()
    }
}