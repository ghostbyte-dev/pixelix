package com.daniebeler.pfpixelix.ui.composables.explore.trending.films

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.PagePaginatedListScreen
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.ExploreGridElement
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.no_films
import pixelix.app.generated.resources.posts

@Composable
fun FilmsComposable(
    navController: AppNavigator,
    viewModel: FilmsViewModel = injectViewModel(key = "films-key") { filmsViewModel }
) {
    PagePaginatedListScreen(
        state = viewModel.pagePaginatedState,
        onRefresh = { viewModel.getItems(true) },
        onLoadMore = { viewModel.getItemsPaginated() },
        emptyMessage = stringResource(Res.string.no_films),
        itemKey = { it.id }
    ) { film ->
        ExploreGridElement(
            keyId = film.name,
            title = film.name,
            subtitle = "${StringFormat.groupDigits(film.amount)} ${pluralStringResource(Res.plurals.posts, film.amount)}",
            onClick = { navController.navigate(Destination.FilmTimeline(film.name)) },
            fetcher = { viewModel.timelineService.getFilmTimeline(it, limit = 39) },
            navController = navController
        )
    }
}