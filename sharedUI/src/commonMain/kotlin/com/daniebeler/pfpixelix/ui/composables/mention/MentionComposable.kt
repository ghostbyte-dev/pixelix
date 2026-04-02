package com.daniebeler.pfpixelix.ui.composables.mention

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.by
import pixelix.app.generated.resources.chevron_back_outline
import pixelix.app.generated.resources.post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentionComposable(
    mentionId: String,
    navController: NavController,
    viewModel: MentionViewModel = injectViewModel(key = "mention$mentionId") { mentionViewModel }
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.loadData(mentionId)
    }

    LaunchedEffect(viewModel.postState.post, viewModel.postContextState.postContext) {
        if (viewModel.postState.post != null && viewModel.postContextState.postContext != null) {
            val index = viewModel.postContextState.postContext!!.ancestors.size + 1
            coroutineScope.launch {
                lazyListState.scrollToItem(index)
            }
        }
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.post), fontWeight = FontWeight.Bold
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
                    imageVector = vectorResource(Res.drawable.chevron_back_outline), contentDescription = ""
                )
            }
        })
    }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = viewModel.postContextState.isRefreshing && viewModel.postState.isRefreshing,
            onRefresh = { viewModel.loadData(mentionId, true) },
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                viewModel.postContextState.postContext?.ancestors?.let { ancestors ->
                    item {
                        SubPosts(ancestors, navController)
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
                viewModel.postState.post?.let { post ->
                    item {
                        PostComposable(
                            post,
                            navController,
                            postGetsDeleted = {
                                navController.navigate(Destination.OwnProfile)
                            },
                            setZindex = { },
                            openReplies = false,
                            showReplies = false
                        )
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
                viewModel.postContextState.postContext?.descendants?.let { descendants ->
                    item {
                        SubPosts(descendants, navController)
                    }
                }
            }

            LoadingComposable(isLoading = viewModel.postState.isLoading)
            ErrorComposable(message = viewModel.postState.error)
        }
    }
}

@Composable
private fun SubPosts(posts: List<Post>, navController: NavController) {
    Column(Modifier.padding(start = 32.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        posts.forEach { ancestor ->
            CustomLayoutWithDivider {
                PostComposable(
                    ancestor,
                    navController,
                    postGetsDeleted = {
                        navController.navigate(Destination.OwnProfile) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    setZindex = { },
                    openReplies = false,
                    showReplies = false,
                    modifier = Modifier.clickable {
                        navController.navigate(Destination.Mention(ancestor.id))
                    }
                )
            }
        }
    }
}

@Composable
fun CustomLayoutWithDivider(content: @Composable () -> Unit) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .padding(vertical = 20.dp)
                    .fillMaxHeight()  // Ensure the divider takes full height
                    .background(Color.Gray)
                    .clip(RoundedCornerShape(10.dp))
            )
            Box{ content() }  // Wrap the content in a Box to ensure it's passed correctly
        }
    ) { measurables, constraints ->
        if (measurables.size < 2) {
            throw IllegalStateException("Expected exactly 2 children: divider and content")
        }

        val dividerMeasurable = measurables[0]
        val contentMeasurable = measurables[1]

        // Measure the content first to determine its height
        val contentPlaceable = contentMeasurable.measure(constraints)
        val dividerPlaceable = dividerMeasurable.measure(
            constraints.copy(minHeight = contentPlaceable.height)
        )

        layout(contentPlaceable.width + 10.dp.roundToPx() + dividerPlaceable.width, contentPlaceable.height) {
            dividerPlaceable.placeRelative(0, 0)  // Place divider at the start
            contentPlaceable.placeRelative(dividerPlaceable.width + 8.dp.roundToPx(), 0)  // Place content next to it
        }
    }
}