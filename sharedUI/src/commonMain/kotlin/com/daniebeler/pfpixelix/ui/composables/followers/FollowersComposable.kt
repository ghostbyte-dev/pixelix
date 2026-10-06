package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.custom_account.AccountListItem
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.empty
import pixelix.app.generated.resources.no_followers_yet
import pixelix.app.generated.resources.nobody_follows_you_yet
import pixelix.app.generated.resources.user_group

@Composable
fun FollowersComposable(
    navController: AppNavigator,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 32.dp, start = 8.dp, end = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(viewModel.followersState.followers, key = { _, it ->
            it.id
        }) { index, account ->
            Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                AccountListItem(
                    account = account,
                    relationship = null,
                    navController = navController,
                    index = index,
                    count = viewModel.followersState.followers.size
                )
            }
        }

        if (viewModel.followersState.followers.isNotEmpty() && viewModel.followersState.isLoading && !viewModel.followersState.isRefreshing) {
            item {
                Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                    LoadingComposable()
                }
            }
        }

        if (viewModel.followersState.endReached && viewModel.followersState.followers.size > 10) {
            item {
                Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                    EndOfListComposable()
                }
            }
        }
    }

    if (!viewModel.followersState.isLoading && viewModel.followersState.error.isEmpty() && viewModel.followersState.followers.isEmpty()) {
        val message =
            if (viewModel.loggedInAccountId == viewModel.accountId) stringResource(Res.string.nobody_follows_you_yet)
            else stringResource(Res.string.no_followers_yet)

        EmptyStateComposable(
            emptyState = EmptyState(
                icon = vectorResource(Res.drawable.user_group),
                heading = stringResource(Res.string.empty),
                message = message
            )
        )
    }

    InfiniteListHandler(
        lazyListState = listState,
    ) {
        viewModel.getFollowersPaginated()
    }

    LoadingComposable(isLoading = viewModel.followersState.isLoading && viewModel.followersState.followers.isEmpty())
    ErrorComposable(message = viewModel.followersState.error)
}