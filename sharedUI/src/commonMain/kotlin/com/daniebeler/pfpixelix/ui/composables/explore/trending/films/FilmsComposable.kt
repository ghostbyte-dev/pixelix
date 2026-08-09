package com.daniebeler.pfpixelix.ui.composables.explore.trending.films

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
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.no_categories

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilmsComposable(
    navController: NavController,
    viewModel: FilmsViewModel = injectViewModel(key = "films-key") { filmsViewModel }
) {

    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.filmsState.isRefreshing,
        onRefresh = { viewModel.getFilms(true) },
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.filmsState.films, key = {
                    it.id
                }) {
                    ExploreGridElement(
                        keyId = it.name, title = it.name, onClick = {
                            //navController.navigate(Destination.CategoryTimeline(category.id))
                        } as () -> Unit, fetcher = { filmName ->
                            viewModel.timelineService.getFilmTimeline(
                                filmName, limit = 39
                            )
                        }, navController = navController
                    )
                }

                if (viewModel.filmsState.isLoading && viewModel.filmsState.films.isNotEmpty()) {
                    item {
                        LoadingComposable()
                    }
                }
            })

        if (viewModel.filmsState.films.isEmpty()) {
            if (viewModel.filmsState.isLoading && !viewModel.filmsState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.filmsState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.filmsState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.filmsState.isLoading && viewModel.filmsState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_categories)))
            }
        }
    }
}