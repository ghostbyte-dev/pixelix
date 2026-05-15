package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.widgets.InfinitePostsList
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.liked_posts
import pixelix.app.generated.resources.no_liked_posts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedPostsComposable(
    navController: NavController,
    viewModel: LikedPostsViewModel = injectViewModel(key = "likey-posts-key") { likedPostsViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                title = { Text(stringResource(Res.string.liked_posts), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            InfinitePostsList(
                items = viewModel.likedPostsState.likedPosts,
                postsCount = viewModel.likedPostsState.likedPosts.count(),
                view = viewModel.view,
                changeView = { viewModel.changeView(it) },
                isLoading = viewModel.likedPostsState.isLoading,
                isRefreshing = viewModel.likedPostsState.isRefreshing,
                error = viewModel.likedPostsState.error,
                emptyMessage = EmptyState(
                    icon = Icons.Outlined.FavoriteBorder, heading = "Empty Collection"
                ),
                endReached = viewModel.likedPostsState.endReached,
                itemGetsDeleted = {},
                postGetsUpdated = {},
                navController = navController,
                getItemsPaginated = {
                    viewModel.getItemsPaginated()
                },
                isFirstItemLarge = true,
                onRefresh = {
                    viewModel.refresh()
                }
            )
        }
    }
}
