package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.BlurHashDecoder
import com.daniebeler.pfpixelix.utils.TimeAgo
import com.daniebeler.pfpixelix.utils.zoomable.rememberZoomState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.snapBackZoomable
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.and
import pixelix.app.generated.resources.bookmark
import pixelix.app.generated.resources.bookmark_outline
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.chatbubble_outline
import pixelix.app.generated.resources.confirm
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.delete_post
import pixelix.app.generated.resources.document_text_outline
import pixelix.app.generated.resources.ellipsis_vertical
import pixelix.app.generated.resources.heart
import pixelix.app.generated.resources.heart_outline
import pixelix.app.generated.resources.liked_by
import pixelix.app.generated.resources.media_description
import pixelix.app.generated.resources.ok
import pixelix.app.generated.resources.others
import pixelix.app.generated.resources.reblogged_by
import pixelix.app.generated.resources.sync_outline
import pixelix.app.generated.resources.sync_outline_bold
import pixelix.app.generated.resources.this_action_cannot_be_undone

private val HeartRedColor = Color(0xFFDD2E44)

private enum class BottomSheetType { None, Comments, Menu, Likes }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposable(
    post: Post,
    navController: NavController,
    postGetsDeleted: (postId: String) -> Unit,
    setZindex: (zIndex: Float) -> Unit,
    openReplies: Boolean = false,
    showReplies: Boolean = true,
    modifier: Modifier = Modifier,
    updatePost: (post: Post) -> Unit = {},
    viewModel: PostViewModel = injectViewModel(key = "post" + post.id) { postViewModel }
) {
    var postId by remember { mutableStateOf(post.id) }
    val sheetState = rememberModalBottomSheetState()
    var activeSheet by remember {
        mutableStateOf(if (openReplies) BottomSheetType.Comments else BottomSheetType.None)
    }

    val timeAgoText = produceState(initialValue = "") {
        value = TimeAgo.convertTimeToText(post.createdAt)
    }

    LaunchedEffect(Unit) {
        if (post.reblogId != null) postId = post.reblogId
        if (viewModel.post == null) viewModel.updatePost(post)
    }

    LaunchedEffect(viewModel.deleteState.deleted) {
        if (viewModel.deleteState.deleted) postGetsDeleted(post.id)
    }

    LaunchedEffect(post) {
        if (viewModel.post == null || viewModel.post!!.copy() != post.copy()) {
            viewModel.updatePost(post)
        }
    }

    LaunchedEffect(openReplies) {
        if (openReplies) viewModel.loadReplies(postId)
    }

    val pagerState = rememberPagerState(pageCount = { post.mediaAttachments.count() })

    var animateBoost by remember { mutableStateOf(false) }
    val boostRotation by animateFloatAsState(
        label = "BoostRotation",
        targetValue = if (animateBoost) 720f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
    )

    var animateHeart by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (animateHeart) 1.3f else 1f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        finishedListener = { animateHeart = false })

    val currentPost = viewModel.post ?: return

    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        PostHeader(
            post = currentPost,
            timeAgoText = timeAgoText.value,
            navController = navController,
            onMenuClick = { activeSheet = BottomSheetType.Menu })

        Spacer(modifier = Modifier.height(6.dp))

        PostMediaSection(
            post = currentPost,
            viewModel = viewModel,
            pagerState = pagerState,
            postId = postId,
            setZindex = setZindex,
            onLikeAnimation = { animateHeart = true },
            updatePost = updatePost,
            navController = navController
        )

        if (!viewModel.isInFocusMode) {
            PostActionBar(
                post = currentPost,
                viewModel = viewModel,
                postId = postId,
                heartScale = heartScale,
                boostRotation = boostRotation,
                animateHeart = { animateHeart = true },
                animateBoost = { animateBoost = !animateBoost },
                onCommentsClick = {
                    viewModel.loadReplies(postId)
                    activeSheet = BottomSheetType.Comments
                },
                onLikesClick = {
                    viewModel.loadLikedBy(postId)
                    activeSheet = BottomSheetType.Likes
                },
                navController = navController,
                updatePost = updatePost
            )
        }
    }

    PostBottomSheet(
        activeSheet = activeSheet,
        sheetState = sheetState,
        post = post,
        viewModel = viewModel,
        pagerState = pagerState,
        navController = navController,
        onDismiss = { activeSheet = BottomSheetType.None })

    PostDeleteDialog(viewModel = viewModel)

    LoadingComposable(isLoading = viewModel.deleteState.isLoading)
}

@Composable
private fun PostHeader(
    post: Post, timeAgoText: String, navController: NavController, onMenuClick: () -> Unit
) {
    post.rebloggedBy?.let { reblogAccount ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 16.dp, end = 12.dp).clickable {
                navController.navigate(Destination.Profile(reblogAccount.id))
            }) {
            Icon(Icons.Outlined.Cached, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(
                stringResource(
                    Res.string.reblogged_by, reblogAccount.displayname ?: reblogAccount.username
                ), fontSize = 11.sp
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 12.dp).clickable {
            navController.navigate(Destination.Profile(post.account.id))
        }) {
        AsyncImage(
            model = post.account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = null,
            modifier = Modifier.height(40.dp).width(40.dp).clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
            Text(
                text = post.account.acct,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 8.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = timeAgoText,
                fontSize = 12.sp,
                lineHeight = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (post.place != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.height(20.dp)
                    )
                    Row {
                        Text(text = post.place?.name ?: "", fontSize = 12.sp)
                        if (post.place?.country != null) {
                            Text(text = ", ${post.place?.country ?: ""}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }


        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = vectorResource(Res.drawable.ellipsis_vertical),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PostMediaSection(
    post: Post,
    viewModel: PostViewModel,
    pagerState: PagerState,
    postId: String,
    setZindex: (zIndex: Float) -> Unit,
    onLikeAnimation: () -> Unit,
    updatePost: (post: Post) -> Unit,
    navController: NavController
) {
    if (post.mediaAttachments.isNotEmpty()) {
        if (post.sensitive && !viewModel.showPost && viewModel.blurSensitiveContent) {
            PostSensitiveOverlay(post = post, viewModel = viewModel)
        } else {
            PostMediaContent(
                post = post,
                viewModel = viewModel,
                pagerState = pagerState,
                postId = postId,
                setZindex = setZindex,
                onLikeAnimation = onLikeAnimation,
                updatePost = updatePost
            )
        }
    } else if (post.content.isNotBlank()) {
        Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
            HorizontalDivider()
            HashtagsMentionsTextView(
                text = post.content,
                mentions = post.mentions,
                navController = navController,
                textSize = 18.sp,
                openUrl = { url -> viewModel.openUrl(url) },
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                emojis = post.emojis
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun PostSensitiveOverlay(post: Post, viewModel: PostViewModel) {
    Box(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp).clip(RoundedCornerShape(16.dp))
    ) {
        val blurHashBitmap = BlurHashDecoder.decode(post.mediaAttachments[0].blurHash)
        val aspectRatio = post.mediaAttachments[0].meta?.original?.aspect?.toFloat() ?: 1.5f

        if (blurHashBitmap != null) {
            Image(
                blurHashBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(aspectRatio)
            )
        }

        Column(
            Modifier.aspectRatio(aspectRatio),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = post.spoilerText.ifEmpty { "This post may contain sensitive content." })
            Button(onClick = { viewModel.toggleShowPost() }) {
                Text(text = "Show post")
            }
        }
    }
}

@Composable
private fun PostMediaContent(
    post: Post,
    viewModel: PostViewModel,
    pagerState: PagerState,
    postId: String,
    setZindex: (zIndex: Float) -> Unit,
    onLikeAnimation: () -> Unit,
    updatePost: (post: Post) -> Unit
) {
    if (post.mediaAttachments.count() > 1) {
        val smallestAspectRatio = post.mediaAttachments.minByOrNull {
            it.meta?.original?.aspect ?: 1.0
        }
        Box {
            HorizontalPager(
                state = pagerState, modifier = Modifier.zIndex(50f).aspectRatio(
                    smallestAspectRatio?.meta?.original?.aspect?.toFloat() ?: 1f
                )
            ) { page ->
                Box(modifier = Modifier.zIndex(10f).padding(start = 8.dp, end = 8.dp)) {
                    PostImage(
                        mediaAttachment = post.mediaAttachments[page],
                        postId = postId,
                        setZindex = setZindex,
                        viewModel = viewModel,
                        like = onLikeAnimation,
                        updatePost = updatePost
                    )
                }
            }

            Box(
                modifier = Modifier.align(Alignment.TopEnd).zIndex(51f)
                    .padding(top = 20.dp, end = 20.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .padding(vertical = 2.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${post.mediaAttachments.count()}",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            Modifier.wrapContentHeight().fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                }
                Box(
                    modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp)
                )
            }
        }
    } else if (post.mediaAttachments.isNotEmpty()) {
        Box(modifier = Modifier.zIndex(10f).padding(start = 12.dp, end = 12.dp)) {
            PostImage(
                mediaAttachment = post.mediaAttachments[0],
                postId = postId,
                setZindex = setZindex,
                viewModel = viewModel,
                like = onLikeAnimation,
                updatePost = updatePost
            )
        }
    }
}

@Composable
private fun PostActionBar(
    post: Post,
    viewModel: PostViewModel,
    postId: String,
    heartScale: Float,
    boostRotation: Float,
    animateHeart: () -> Unit,
    animateBoost: () -> Unit,
    onCommentsClick: () -> Unit,
    onLikesClick: () -> Unit,
    navController: NavController,
    updatePost: (post: Post) -> Unit
) {
    Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like button with count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(percent = 50))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable {
                            if (post.favourited) {
                                viewModel.unlikePost(postId, updatePost)
                            } else {
                                animateHeart()
                                viewModel.likePost(postId, updatePost)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    if (post.favourited) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heart),
                            modifier = Modifier.size(22.dp).scale(heartScale),
                            contentDescription = "unlike post",
                            tint = HeartRedColor
                        )
                    } else {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heart_outline),
                            modifier = Modifier.size(22.dp),
                            contentDescription = "like post"
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = post.favouritesCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Comment button with count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(percent = 50))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable(onClick = onCommentsClick)
                        .padding(horizontal = 10.dp, vertical = 4.dp)

                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.chatbubble_outline),
                        modifier = Modifier.size(22.dp),
                        contentDescription = "open comments"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = post.replyCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(RoundedCornerShape(percent = 50))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh).clickable {
                            animateBoost()
                            if (post.reblogged) {
                                viewModel.unreblogPost(postId, updatePost)
                            } else {
                                viewModel.reblogPost(postId, updatePost)
                            }
                        }.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (post.reblogged) {
                            vectorResource(Res.drawable.sync_outline_bold)
                        } else {
                            vectorResource(Res.drawable.sync_outline)
                        }, contentDescription = "reblog", tint = if (post.reblogged) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }, modifier = Modifier.rotate(boostRotation).size(22.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = post.reblogCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }


                Spacer(Modifier.width(14.dp))

                // Bookmark button
                if (post.bookmarked) {
                    IconButton(onClick = { viewModel.unBookmarkPost(postId, updatePost) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.bookmark),
                            contentDescription = "unbookmark post"
                        )
                    }
                } else {
                    IconButton(onClick = { viewModel.bookmarkPost(postId, updatePost) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.bookmark_outline),
                            contentDescription = "bookmark post"
                        )
                    }
                }
            }
        }

        // "Liked by" row
        PostLikedByRow(
            post = post,
            viewModel = viewModel,
            navController = navController,
            onLikesClick = onLikesClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Post content text (when media is present)
        if (post.mediaAttachments.isNotEmpty() && post.content.isNotBlank()) {
            HashtagsMentionsTextView(
                text = post.content,
                mentions = post.mentions,
                navController = navController,
                openUrl = { url -> viewModel.openUrl(url) },
                maximumLines = 4,
                emojis = post.emojis
            )
        }
    }
}

@Composable
private fun PostLikedByRow(
    post: Post, viewModel: PostViewModel, navController: NavController, onLikesClick: () -> Unit
) {
    if (post.likedBy?.username?.isNotBlank() != true) return

    Row {
        Text(text = stringResource(Res.string.liked_by) + " ", fontSize = 14.sp)
        Text(
            text = post.likedBy!!.username!!,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                navController.navigate(Destination.Profile(post.likedBy!!.id!!))
            })
        if (post.favouritesCount > 1) {
            Text(text = " ${stringResource(Res.string.and)} ", fontSize = 14.sp)
            Text(
                text = "${post.favouritesCount - 1} ${stringResource(Res.string.others)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onLikesClick)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostBottomSheet(
    activeSheet: BottomSheetType,
    sheetState: androidx.compose.material3.SheetState,
    post: Post,
    viewModel: PostViewModel,
    pagerState: PagerState,
    navController: NavController,
    onDismiss: () -> Unit
) {
    if (activeSheet == BottomSheetType.None) return

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        when (activeSheet) {
            BottomSheetType.Comments -> CommentsBottomSheet(post, navController, viewModel)
            BottomSheetType.Menu -> {
                val isMyPost =
                    viewModel.myAccountId != null && post.account.id == viewModel.myAccountId
                ShareBottomSheet(
                    post.url,
                    isMyPost,
                    viewModel,
                    post,
                    pagerState.currentPage,
                    navController,
                    onDismiss
                )
            }

            BottomSheetType.Likes -> LikesBottomSheet(viewModel, navController)
            BottomSheetType.None -> {}
        }
    }
}

@Composable
private fun PostDeleteDialog(viewModel: PostViewModel) {
    val postIdToDelete = viewModel.deleteDialog ?: return

    AlertDialog(
        icon = { Icon(imageVector = Icons.Outlined.Delete, contentDescription = null) },
        title = { Text(text = stringResource(Res.string.delete_post)) },
        text = { Text(text = stringResource(Res.string.this_action_cannot_be_undone)) },
        onDismissRequest = { viewModel.deleteDialog = null },
        confirmButton = {
            TextButton(onClick = { viewModel.deletePost(postIdToDelete) }) {
                Text(stringResource(Res.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.deleteDialog = null }) {
                Text(stringResource(Res.string.cancel))
            }
        })
}

// --- Post Image and Media Components ---

@Composable
fun PostImage(
    mediaAttachment: MediaAttachment,
    postId: String,
    setZindex: (zIndex: Float) -> Unit,
    viewModel: PostViewModel,
    like: () -> Unit,
    updatePost: (post: Post) -> Unit
) {
    var showHeart by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (showHeart) 1f else 0f, label = "heart animation")
    var imageLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(showHeart) {
        if (showHeart) {
            delay(1000)
            showHeart = false
        }
    }
    var showMediaDialog by remember { mutableStateOf<MediaAttachment?>(null) }
    var altText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxWidth().zIndex(80f).clip(RoundedCornerShape(16.dp))) {
        val blurHashBitmap = BlurHashDecoder.decode(mediaAttachment.blurHash)

        if (!imageLoaded && blurHashBitmap != null) {
            Image(
                blurHashBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(
                    mediaAttachment.meta?.original?.aspect?.toFloat() ?: 1f
                )
            )
        }

        val zoomState = rememberZoomState()
        val showAltTextIcon = remember { mutableStateOf(true) }

        if (zoomState.scale != 1f) {
            setZindex(100f)
            showAltTextIcon.value = false
        } else {
            setZindex(1f)
            showAltTextIcon.value = true
        }

        Box(modifier = Modifier.zIndex(2f).snapBackZoomable(zoomState).pointerInput(Unit) {
            detectTapGestures(onDoubleTap = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.likePost(postId, updatePost)
                    like()
                    showHeart = true
                }
            }, onTap = {
                if (mediaAttachment.type != "video") {
                    showMediaDialog = mediaAttachment
                }
            })
        }) {
            if (mediaAttachment.type != "video") {
                ImageWrapper(
                    mediaAttachment,
                    { zoomState.setContentSize(it.painter.intrinsicSize) },
                    { imageLoaded = true })
            } else {
                VideoAttachment(mediaAttachment, viewModel, { imageLoaded = true })
            }
        }

        if (mediaAttachment.description?.isNotBlank() == true && showAltTextIcon.value && !viewModel.isAltTextButtonHidden) {
            Box(
                modifier = Modifier.align(Alignment.BottomStart).zIndex(3f).padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .clickable { altText = mediaAttachment.description }.padding(10.dp)
            ) {
                Icon(vectorResource(Res.drawable.document_text_outline), contentDescription = null, Modifier.size(22.dp))
            }
        }

        Icon(
            imageVector = vectorResource(Res.drawable.heart),
            contentDescription = null,
            tint = HeartRedColor,
            modifier = Modifier.size(80.dp).align(Alignment.Center).scale(scale.value).zIndex(100f)
        )

        if (altText.isNotBlank()) {
            AlertDialog(
                title = { Text(text = stringResource(Res.string.media_description)) },
                text = { Text(text = altText) },
                onDismissRequest = { altText = "" },
                confirmButton = {
                    TextButton(onClick = { altText = "" }) {
                        Text(stringResource(Res.string.ok))
                    }
                })
        }
    }

    showMediaDialog?.let {
        MediaDialog(it, closeDialog = { showMediaDialog = null }, postViewModel = viewModel)
    }
}

@Composable
private fun ImageWrapper(
    mediaAttachment: MediaAttachment,
    setContentSize: (painter: AsyncImagePainter.State.Success) -> Unit,
    onSuccess: () -> Unit
) {
    AsyncImage(
        model = mediaAttachment.url,
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
        onSuccess = { state ->
            setContentSize(state)
            onSuccess()
        })
}

fun Modifier.isVisible(
    threshold: Int, onVisibilityChange: (Boolean) -> Unit
) = composed {
    Modifier.onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
        val layoutHeight = layoutCoordinates.size.height
        val thresholdHeight = layoutHeight * threshold / 100
        val layoutTop = layoutCoordinates.positionInRoot().y
        val layoutBottom = layoutTop + layoutHeight

        val parent = layoutCoordinates.parentLayoutCoordinates
        parent?.boundsInRoot()?.let { rect: Rect ->
            val parentTop = rect.top
            val parentBottom = rect.bottom
            val isVisible =
                parentBottom - layoutTop > thresholdHeight && parentTop < layoutBottom - thresholdHeight
            onVisibilityChange(isVisible)
        }
    }
}

@Composable
fun MediaDialog(
    mediaAttachment: MediaAttachment, closeDialog: () -> Unit, postViewModel: PostViewModel
) {
    val zoomState = rememberZoomState()

    Dialog(
        onDismissRequest = closeDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))
                .clickable { closeDialog() }, contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.zIndex(2f).zoomable(zoomState).clickable { }) {
                if (mediaAttachment.type != "video") {
                    ImageWrapper(
                        mediaAttachment,
                        { zoomState.setContentSize(it.painter.intrinsicSize) },
                        {})
                } else {
                    VideoAttachment(mediaAttachment, postViewModel, {})
                }
            }
            Box(Modifier.align(Alignment.TopEnd).padding(20.dp).zIndex(2f)) {
                IconButton(onClick = closeDialog) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}
