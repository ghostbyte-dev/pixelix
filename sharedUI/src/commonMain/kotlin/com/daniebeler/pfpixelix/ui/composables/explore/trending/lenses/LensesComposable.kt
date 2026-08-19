package com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.ExploreGridElement
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.datetime
import pixelix.app.generated.resources.no_categories

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LensesComposable(
    navController: NavController,
    viewModel: LensesViewModel = injectViewModel(key = "lenses-key") { lensesViewModel }
) {

    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.lensesState.isRefreshing,
        onRefresh = { viewModel.getLenses(true) },
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.lensesState.lenses, key = {
                    it.id
                }) {
                    ExploreGridElement(
                        keyId = it.name, title = it.name, onClick = {
                            navController.navigate(Destination.LensTimeline(it.name))
                        }, fetcher = { lensName ->
                            viewModel.timelineService.getLensTimeline(
                                lensName, limit = 39
                            )
                        }, navController = navController
                    )
                }

                if (viewModel.lensesState.isLoading && viewModel.lensesState.lenses.isNotEmpty()) {
                    item {
                        LoadingComposable()
                    }
                }
            })

        if (viewModel.lensesState.lenses.isEmpty()) {
            if (viewModel.lensesState.isLoading && !viewModel.lensesState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.lensesState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.lensesState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.lensesState.isLoading && viewModel.lensesState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_categories)))
            }
        }
    }
}