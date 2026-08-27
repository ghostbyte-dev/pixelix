package com.daniebeler.pfpixelix.ui.composables.single_post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.by
import pixelix.app.generated.resources.post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePostComposable(
    navController: NavController,
    postId: String,
    refresh: Boolean,
    openReplies: Boolean,
    viewModel: SinglePostViewModel = injectViewModel(key = "single-post$postId") { singlePostViewModel }
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getPost(postId)
    }

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.postState = SinglePostState()
            viewModel.getPost(postId)
        }
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
                .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight - 24.dp)
                .fillMaxSize()
        ) {
            CustomPullToRefreshBox(
                isRefreshing = viewModel.postState.isRefreshing,
                onRefresh = { viewModel.getPost(postId, true) },
                modifier = Modifier.fillMaxSize(),
                animatedBox = true
            ) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                        .padding(top = 28.dp, start = 4.dp, end = 4.dp, bottom = 28.dp)
                ) {
                    if (viewModel.postState.post != null) {
                        PostComposable(
                            viewModel.postState.post!!, navController, postGetsDeleted = {
                                navController.navigate(Destination.HomeTabOwnProfile) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                        saveState = false
                                    }
                                }
                            },
                            setZindex = { },
                            openReplies,
                            fullQuality = true
                        )
                    }
                }
            }

            if (!viewModel.postState.isRefreshing) {
                LoadingComposable(isLoading = viewModel.postState.isLoading)
            }
            ErrorComposable(message = viewModel.postState.error)
        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), title = {
                Column {
                    Text(
                        text = stringResource(Res.string.post),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = stringResource(
                            Res.string.by, (viewModel.postState.post?.account?.username ?: "")
                        ), fontSize = 12.sp, lineHeight = 6.sp
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
}