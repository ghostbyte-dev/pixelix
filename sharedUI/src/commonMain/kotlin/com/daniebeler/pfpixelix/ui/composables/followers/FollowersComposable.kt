package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.custom_account.AccountListItem
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.empty
import pixelix.app.generated.resources.no_followers_yet
import pixelix.app.generated.resources.nobody_follows_you_yet
import pixelix.app.generated.resources.user_group

@Composable
fun FollowersComposable(
    navController: NavController,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(1), state = staggeredGridState, contentPadding = PaddingValues(top = 32.dp, start = 8.dp, end = 8.dp)) {
        itemsIndexed(viewModel.followersState.followers, key = { _, it ->
            it.id
        }) { index, account ->
            AccountListItem(
                account = account,
                relationship = null,
                navController = navController,
                index = index,
                count = viewModel.followersState.followers.size
            )
        }

        if (viewModel.followersState.followers.isNotEmpty() && viewModel.followersState.isLoading && !viewModel.followersState.isRefreshing) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadingComposable()
            }
        }

        if (viewModel.followersState.endReached && viewModel.followersState.followers.size > 10) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EndOfListComposable()
            }
        }
    }

    if (!viewModel.followersState.isLoading && viewModel.followersState.error.isEmpty() && viewModel.followersState.followers.isEmpty()) {
        val message = if (viewModel.loggedInAccountId == viewModel.accountId)
            stringResource(Res.string.nobody_follows_you_yet)
        else
            stringResource(Res.string.no_followers_yet)

        EmptyStateComposable(
            emptyState = EmptyState(
                icon = vectorResource(Res.drawable.user_group),
                heading = stringResource(Res.string.empty),
                message = message
            )
        )
    }

    InfiniteStaggeredGridHandler(lazyStaggeredGridState = staggeredGridState, itemCount = viewModel.followersState.followers.size) {
        viewModel.getFollowersPaginated()
    }

    LoadingComposable(isLoading = viewModel.followersState.isLoading && viewModel.followersState.followers.isEmpty())
    ErrorComposable(message = viewModel.followersState.error)
}