package com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import pixelix.app.generated.resources.no_cameras
import pixelix.app.generated.resources.posts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CamerasComposable(
    navController: AppNavigator,
    viewModel: CamerasViewModel = injectViewModel(key = "cameras-key") { camerasViewModel }
) {
    PagePaginatedListScreen(
        state = viewModel.pagePaginatedState,
        onRefresh = { viewModel.getItems(true) },
        onLoadMore = { viewModel.getItemsPaginated() },
        emptyMessage = stringResource(Res.string.no_cameras),
        itemKey = { it.id }
    ) { camera ->
        ExploreGridElement(
            keyId = camera.name,
            title = camera.name,
            subtitle = "${StringFormat.groupDigits(camera.amount)} ${pluralStringResource(Res.plurals.posts, camera.amount)}",
            onClick = { navController.navigate(Destination.CameraTimeline(camera.name)) },
            fetcher = { viewModel.timelineService.getCameraTimeline(it, limit = 39) },
            navController = navController
        )
    }
}