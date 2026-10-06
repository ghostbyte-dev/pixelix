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
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.empty
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.not_following_anyone
import pixelix.app.generated.resources.the_profiles_you_follow_will_appear_here
import pixelix.app.generated.resources.user_group

@Composable
fun FollowingComposable(
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
        itemsIndexed(viewModel.followingState.following, key = { _, it ->
            it.id
        }) { index, account ->
            Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                AccountListItem(
                    account = account,
                    relationship = null,
                    navController = navController,
                    index = index,
                    count = viewModel.followingState.following.size
                )
            }
        }

        if (viewModel.followingState.following.isNotEmpty() && viewModel.followingState.isLoading && !viewModel.followingState.isRefreshing) {
            item {
                Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                    LoadingComposable()
                }
            }
        }

        if (viewModel.followingState.endReached && viewModel.followingState.following.size > 10) {
            item {
                Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                    EndOfListComposable()
                }
            }
        }
    }

    if (!viewModel.followingState.isLoading && viewModel.followingState.error.isEmpty() && viewModel.followingState.following.isEmpty()) {

        val message =
            if (viewModel.loggedInAccountId == viewModel.accountId) stringResource(Res.string.the_profiles_you_follow_will_appear_here)
            else stringResource(Res.string.not_following_anyone)

        EmptyStateComposable(
            emptyState = EmptyState(
                icon = vectorResource(Res.drawable.user_group),
                heading = stringResource(Res.string.empty),
                message = message,
                buttonText = stringResource(Res.string.explore_trending_profiles),
                onClick = {
                    navController.navigate(Destination.Search(1))
                })
        )
    }

    InfiniteListHandler(
        lazyListState = listState,
    ) {
        viewModel.getFollowingPaginated()
    }

    LoadingComposable(isLoading = viewModel.followingState.isLoading && viewModel.followingState.following.isEmpty())
    ErrorComposable(message = viewModel.followingState.error)
}