package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteStaggeredGridHandler
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.empty
import pixelix.app.generated.resources.no_followers_yet
import pixelix.app.generated.resources.nobody_follows_you_yet

@Composable
fun FollowersComposable(
    navController: NavController,
    viewModel: FollowersViewModel = injectViewModel(key = "followers-viewmodel-key") { followersViewModel }
) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp), state = staggeredGridState, contentPadding = PaddingValues(top = 24.dp)) {
        items(viewModel.followersState.followers, key = {
            it.id
        }) {
            FollowerElementComposable(account = it, navController)
        }

        if (viewModel.followersState.followers.isNotEmpty() && viewModel.followersState.isLoading && !viewModel.followersState.isRefreshing) {
            item(span = StaggeredGridItemSpan.FullLine) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .wrapContentSize(Alignment.Center)
                )
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
                icon = Icons.Outlined.Groups,
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