package com.daniebeler.pfpixelix.ui.composables.explore.trending.editors_choice_posts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.ParametricTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditorsChoicePostsComposable(
    navController: NavController,
    viewModel: ParametricTimelineViewModel = injectViewModel(key = "editors-choice-posts") {
        parametricTimelineViewModel.apply { init(ParametricTimelineViewModel.FetchType.EDITORS_CHOICE_POSTS) }
    }
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        InfinitePostsList(
            items = viewModel.timelineState.posts,
            isLoading = viewModel.timelineState.isLoading,
            isRefreshing = viewModel.timelineState.isRefreshing,
            error = viewModel.timelineState.error,
            endReached = viewModel.timelineState.endReached,
            view = viewModel.view,
            changeView = { viewModel.changeView(it) },
            isFirstItemLarge = true,
            itemGetsDeleted = { },
            getItemsPaginated = { viewModel.getItemsPaginated() },
            onRefresh = { viewModel.refresh() },
            navController = navController,
            postGetsUpdated = { },
            contentPaddingTop = 32.dp,
            contentPaddingBottom = 80.dp)
    }
}