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
    navController: NavController,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        state = staggeredGridState,
        contentPadding = PaddingValues(top = 32.dp, start = 8.dp, end = 8.dp)
    ) {
        itemsIndexed(viewModel.followingState.following, key = { _, it ->
            it.id
        }) { index, account ->
            AccountListItem(
                account = account,
                relationship = null,
                navController = navController,
                index = index,
                count = viewModel.followingState.following.size
            )
        }

        if (viewModel.followingState.following.isNotEmpty() && viewModel.followingState.isLoading && !viewModel.followingState.isRefreshing) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadingComposable()
            }
        }

        if (viewModel.followingState.endReached && viewModel.followingState.following.size > 10) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EndOfListComposable()
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

    InfiniteStaggeredGridHandler(
        lazyStaggeredGridState = staggeredGridState,
        itemCount = viewModel.followingState.following.size
    ) {
        viewModel.getFollowingPaginated()
    }

    LoadingComposable(isLoading = viewModel.followingState.isLoading && viewModel.followingState.following.isEmpty())
    ErrorComposable(message = viewModel.followingState.error)
}