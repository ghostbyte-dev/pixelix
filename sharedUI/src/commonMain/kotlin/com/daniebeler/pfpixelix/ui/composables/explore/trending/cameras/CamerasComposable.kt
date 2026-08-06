package com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.ExploreGridElement
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagsViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.no_cameras
import pixelix.app.generated.resources.no_trending_hashtags
import pixelix.app.generated.resources.posts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CamerasComposable(
    navController: NavController,
    viewModel: CamerasViewModel = injectViewModel(key = "cameras-key") { camerasViewModel }
) {
    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.camerasState.isRefreshing,
        onRefresh = { viewModel.getCameras(true) },
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.camerasState.cameras, key = {
                    it.id
                }) {
                    ExploreGridElement(
                        keyId = it.name,
                        title = it.name,
                        subtitle = "${StringFormat.groupDigits(it.amount)} ${
                            pluralStringResource(
                                Res.plurals.posts, it.amount
                            )
                        }",
                        onClick = {
                            navController.navigate(Destination.HashtagTimeline(it.name))
                        },
                        fetcher = { camera ->
                            viewModel.timelineService.getCameraTimeline(
                                camera, limit = 39
                            )
                        },
                        navController = navController
                    )
                }

                if (viewModel.camerasState.isLoading && viewModel.camerasState.cameras.isNotEmpty()) {
                    item {
                        LoadingComposable()
                    }
                }
            })

        if (viewModel.camerasState.cameras.isEmpty()) {
            if (viewModel.camerasState.isLoading && !viewModel.camerasState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.camerasState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.camerasState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.camerasState.isLoading && viewModel.camerasState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_cameras)))
            }
        }
    }

    InfiniteListHandler(
        lazyListState = lazyListState
    ) {
        viewModel.getCamerasPaginated()
    }
}