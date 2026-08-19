package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.ScreenScaffold
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.no_posts
import pixelix.app.generated.resources.photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    title: String,
    navController: NavController,
    viewModel: PaginatedPostsViewModel,
    contentPaddingTop: Dp = 24.dp,
    isFirstItemLarge: Boolean = false,
    emptyState: EmptyState = EmptyState(
        icon = vectorResource(Res.drawable.photo),
        heading = stringResource(Res.string.no_posts),
        message = ""
    ),
    beforeContent: (@Composable () -> Unit)? = null
) {
    ScreenScaffold(
        title = title,
        navController = navController,
    ) {
        InfinitePostsList(
            contentPaddingTop = contentPaddingTop,
            items = viewModel.timelineState.posts,
            isLoading = viewModel.timelineState.isLoading,
            isRefreshing = viewModel.timelineState.isRefreshing,
            error = viewModel.timelineState.error,
            endReached = viewModel.timelineState.endReached,
            view = viewModel.view,
            changeView = { viewModel.changeView(it) },
            isFirstItemLarge = isFirstItemLarge,
            emptyMessage = emptyState,
            itemGetsDeleted = { viewModel.postGetsDeleted(it) },
            getItemsPaginated = { viewModel.getItemsPaginated() },
            onRefresh = { viewModel.refresh() },
            navController = navController,
            postGetsUpdated = { viewModel.postGetsUpdated(it) },
            before = beforeContent
        )
    }
}