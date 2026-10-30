package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.composables.MaxWidthTopBar
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.ui.navigation.Destination
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.follow_accounts_or_hashtags_to_fill_your_home_timeline
import pixelix.app.generated.resources.no_posts
import pixelix.app.generated.resources.photo

@Composable
fun PlaceComposable(
    navController: AppNavigator,
    placeId: String,
    slug: String,
    viewModel: PlaceViewModel = metroViewModel(key = "place-viewmodel-key$placeId$slug")
) {

    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(placeId, slug) {
        viewModel.loadData(placeId, slug, false)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        MaxWidthTopBar {
            TopAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ), title = {
                    Column {
                        Text(
                            text = viewModel.postsForPlaceState.data?.place?.name ?: "Place",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = viewModel.postsForPlaceState.data?.place?.country?.name
                                ?: "Country",
                            fontSize = 12.sp,
                            lineHeight = 6.sp
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_left),
                            contentDescription = ""
                        )
                    }
                }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            InfinitePostsList(
                items = viewModel.postsForPlaceState.data?.posts ?: emptyList(),
                contentPaddingTop = 32.dp,
                view = viewModel.view,
                changeView = { viewModel.changeView(it) },
                isLoading = viewModel.postsForPlaceState.isLoading,
                isRefreshing = viewModel.postsForPlaceState.isRefreshing,
                error = viewModel.postsForPlaceState.error,
                endReached = false,
                navController = navController,
                emptyMessage = EmptyState(
                    icon = vectorResource(Res.drawable.photo),
                    heading = stringResource(Res.string.no_posts),
                    message = stringResource(Res.string.follow_accounts_or_hashtags_to_fill_your_home_timeline),
                    buttonText = stringResource(Res.string.explore_trending_profiles),
                    onClick = { navController.navigate(Destination.Search(1)) }),
                getItemsPaginated = { },
                onRefresh = { },
                refreshable = false,
                itemGetsDeleted = { },
                postGetsUpdated = { },
                staggeredGridState = staggeredGridState,
                before = {
                    viewModel.postsForPlaceState.coordinates?.let { coords ->
                        MapComposable(
                            lat = coords.latitude, lon = coords.longitude
                        )
                    }
                })
        }
    }
}