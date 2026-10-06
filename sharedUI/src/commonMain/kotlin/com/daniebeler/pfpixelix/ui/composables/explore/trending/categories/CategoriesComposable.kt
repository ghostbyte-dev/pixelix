package com.daniebeler.pfpixelix.ui.composables.explore.trending.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.ExploreGridElement
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.no_categories

@Composable
fun CategoriesComposable(
    navController: AppNavigator,
    viewModel: CategoriesViewModel = injectViewModel(key = "categories-key") { categoriesViewModel }
) {
    val lazyListState = rememberLazyListState()

    CustomPullToRefreshBox(
        isRefreshing = viewModel.categoriesState.isRefreshing,
        onRefresh = { viewModel.getCategories(true) },
        enabled = PlatformFeatures.supportsPullToRefresh,
        animatedBox = true
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.categoriesState.categories, key = {
                    it.id
                }) {
                    ExploreGridElement(
                        keyId = it.name,
                        title = it.name,
                        onClick = {
                            navController.navigate(Destination.CategoryTimeline(it.name))
                        },
                        fetcher = { categoryName -> viewModel.timelineService.getCategoryTimeline(categoryName, limit = 39) },
                        navController = navController
                    )                }

                if (viewModel.categoriesState.isLoading && viewModel.categoriesState.categories.isNotEmpty()) {
                    item {
                        LoadingComposable()
                    }
                }
            })

        if (viewModel.categoriesState.categories.isEmpty()) {
            if (viewModel.categoriesState.isLoading && !viewModel.categoriesState.isRefreshing) {
                LoadingComposable()
            }

            if (viewModel.categoriesState.error.isNotEmpty()) {
                ErrorComposable(
                    message = viewModel.categoriesState.error,
                    modifier = Modifier.fillMaxSize().padding(36.dp, 20.dp)
                )
            }

            if (!viewModel.categoriesState.isLoading && viewModel.categoriesState.error.isEmpty()) {
                EmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_categories)))
            }
        }
    }
}