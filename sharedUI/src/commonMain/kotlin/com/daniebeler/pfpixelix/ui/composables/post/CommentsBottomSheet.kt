package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.service.general.ReplyChildrenState
import com.daniebeler.pfpixelix.domain.service.general.ReplyNode
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.MaxLengthTextField
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.timeAgo
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.delete_reply
import pixelix.app.generated.resources.edit
import pixelix.app.generated.resources.heart
import pixelix.app.generated.resources.heart_filled
import pixelix.app.generated.resources.no_comments_yet
import pixelix.app.generated.resources.reply
import pixelix.app.generated.resources.send
import pixelix.app.generated.resources.this_action_cannot_be_undone
import pixelix.app.generated.resources.trash

@Composable
fun CommentsBottomSheet(
    post: Post, navController: AppNavigator, viewModel: PostViewModel
) {
    val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box {
        Column(Modifier.fillMaxWidth().align(Alignment.TopStart)) {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false).padding(horizontal = 12.dp)
            ) {
                item {
                    if (post.content.isNotEmpty()) {
                        val ownDescription = Post(
                            "0",
                            content = post.content,
                            mentions = post.mentions,
                            account = post.account,
                            createdAt = post.createdAt,
                            replyCount = post.replyCount,
                            likedBy = post.likedBy,
                            mediaAttachments = emptyList(),
                            favouritesCount = 0,
                            tags = emptyList(),
                            url = "",
                            reblogged = false,
                            sensitive = false,
                            bookmarked = false,
                            favourited = false,
                            visibility = Visibility.PUBLIC,
                            spoilerText = "",
                            location = null,
                            inReplyToId = null,
                            emojis = emptyList(),
                            reblogCount = 0,
                            commentsDisabled = false,
                            category = null
                        )
                        ReplyElement(
                            reply = ReplyNode(
                                post = ownDescription,
                                knownReplyCount = 0,
                                childrenState = ReplyChildrenState.NotLoaded
                            ),
                            true,
                            navController = navController,
                            {},
                            viewModel.myAccountId,
                            { url -> viewModel.openUrl(url) },
                            instance = viewModel.instance,
                            viewModel = viewModel
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MaxLengthTextField(
                            value = viewModel.replyText,
                            onValueChange = { viewModel.updateReplyText(it) },
                            textFieldModifier = Modifier.fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    viewModel.hashtagMentionsSuggestionsManager.onFocusChanged(
                                        focusState.isFocused
                                    )
                                },
                            modifier = Modifier.weight(1f),
                            label = Res.string.reply,
                            imeAction = ImeAction.Send,
                            maxLength = viewModel.instance?.configuration?.statusConfig?.maxCharacters,
                            submit = { text ->
                                focusManager.clearFocus()
                                keyboardController?.hide()

                                viewModel.replyText = TextFieldValue()
                                viewModel.createReply(
                                    post.id, text
                                )
                            },
                        )
                        Button(
                            onClick = {
                                if (!viewModel.ownReplyState.isLoading) {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()

                                    viewModel.createReply(post.id, viewModel.replyText.text)
                                    viewModel.replyText = viewModel.replyText.copy(text = "")
                                }
                            },
                            Modifier.height(56.dp).width(56.dp).padding(0.dp, 0.dp),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(12.dp),
                            enabled = viewModel.replyText.text.length < (viewModel.instance?.configuration?.statusConfig?.maxCharacters
                                ?: Int.MAX_VALUE)
                        ) {
                            if (viewModel.ownReplyState.isLoading) {
                                LoadingComposable(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.send),
                                    contentDescription = "submit",
                                    Modifier.fillMaxSize().fillMaxWidth()
                                )
                            }

                        }

                    }

                    HorizontalDivider(Modifier.padding(12.dp))
                }

                items(viewModel.repliesState.replies, key = {
                    it.post.id
                }) { node ->
                    ReplyElement(
                        reply = node,
                        false,
                        navController = navController,
                        { viewModel.deleteReply(node.post.id) },
                        viewModel.myAccountId,
                        { url -> viewModel.openUrl(url) },
                        instance = viewModel.instance,
                        viewModel = viewModel
                    )
                }

                if (viewModel.repliesState.isLoading) {
                    item {
                        LoadingComposable()
                    }
                }

                if (!viewModel.repliesState.isLoading && viewModel.repliesState.error.isBlank() && viewModel.repliesState.replies.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = stringResource(Res.string.no_comments_yet))
                        }
                    }
                }

                if (!viewModel.repliesState.isLoading && viewModel.repliesState.error.isNotBlank() && viewModel.repliesState.replies.isEmpty()) {
                    item {
                        ErrorComposable(viewModel.repliesState.error)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Spacer(
                        Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    )
                }
            }
            if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
                SuggestionsBar(
                    state = suggestionsState, bottomBarPadding = false, onSelected = { selected ->
                        viewModel.replyText =
                            viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                                selected, viewModel.replyText
                            )
                    })
            }
        }
    }
}


@Composable
private fun ReplyElement(
    reply: ReplyNode,
    postDescription: Boolean,
    navController: AppNavigator,
    deleteReply: () -> Unit,
    myAccountId: String?,
    openUrl: (url: String) -> Unit,
    viewModel: PostViewModel,
    instance: Instance?
) {

    var timeAgo: String by remember { mutableStateOf("") }
    var replyCount: Int by remember { mutableIntStateOf(reply.post.replyCount) }
    val openAddReplyDialog = remember { mutableStateOf(false) }
    val showDeleteReplyDialog = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(reply.post.createdAt) {
        timeAgo = timeAgo(reply.post.createdAt)
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row {
            AsyncImage(
                model = reply.post.account.avatar,
                contentDescription = "",
                modifier = Modifier.height(42.dp).width(42.dp).clip(CircleShape).clickable {
                    navController.navigate(
                        Destination.Profile(
                            reply.post.account.id,
                            reply.post.account.username
                        )
                    )
                })

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row {
                    Text(
                        text = reply.post.account.acct,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable {
                            navController.navigate(
                                Destination.Profile(
                                    reply.post.account.id,
                                    reply.post.account.username
                                )
                            )
                        })

                    Text(
                        text = " • $timeAgo",
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                HashtagsMentionsTextView(
                    text = reply.post.content,
                    mentions = reply.post.mentions,
                    emojis = reply.post.emojis,
                    navController = navController,
                    openUrl = { url -> openUrl(url) })
            }
        }

        if (!postDescription) {
            Row(Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                if (reply.post.account.id == myAccountId) {
                    IconButton(onClick = { showDeleteReplyDialog.value = true }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.trash),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                TextButton(onClick = { openAddReplyDialog.value = true }) {
                    Text(
                        text = stringResource(Res.string.reply),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (reply.post.favourited) {
                    IconButton(onClick = {
                        viewModel.unlikeReply(reply.post.id)
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heart_filled),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.likeReply(reply.post.id)
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.heart),
                            contentDescription = ""
                        )
                    }
                }
            }

            if (reply.childrenState is ReplyChildrenState.NotLoaded && replyCount > 0) {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    TextButton(onClick = { viewModel.loadReplies(reply.post.id) }) {
                        Text(
                            text = if (replyCount == 1) {
                                "view $replyCount reply"
                            } else {
                                "view $replyCount replies"
                            }, fontSize = 12.sp
                        )
                    }
                }
            }
            if (reply.childrenState is ReplyChildrenState.Loading) {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    LoadingComposable(Modifier.fillMaxWidth().padding(vertical = 50.dp))
                }
            } else if (reply.childrenState is ReplyChildrenState.Error) {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    ErrorComposable(
                        reply.childrenState.message,
                        Modifier.fillMaxWidth().padding(vertical = 50.dp)
                    )
                }
            } else if (reply.childrenState is ReplyChildrenState.Loaded) {
                Box(Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    val childrenState = reply.childrenState
                    Column {
                        childrenState.nodes.forEach {
                            ReplyElement(
                                reply = it, false, navController = navController, {
                                    viewModel.deleteReply(it.post.id)
                                    replyCount--
                                }, myAccountId, openUrl, instance = viewModel.instance,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    if (openAddReplyDialog.value) {
        AddReplyDialog(onDismissRequest = { openAddReplyDialog.value = false }, onConfirmation = {
            openAddReplyDialog.value = false
            replyCount++
            viewModel.createReply(reply.post.id, it)
        }, instance = instance, viewModel = viewModel)
    }

    if (showDeleteReplyDialog.value) {
        AlertDialog(icon = {
            Icon(
                imageVector = vectorResource(Res.drawable.trash),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }, title = {
            Text(text = stringResource(Res.string.delete_reply))
        }, text = {
            Text(text = stringResource(Res.string.this_action_cannot_be_undone))
        }, onDismissRequest = {
            showDeleteReplyDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                deleteReply()
                showDeleteReplyDialog.value = false
            }) {
                Text(stringResource(Res.string.delete), color = MaterialTheme.colorScheme.error)
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDeleteReplyDialog.value = false
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }
}

@Composable
fun AddReplyDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (replyText: String) -> Unit,
    instance: Instance?,
    viewModel: PostViewModel
) {
    val suggestionsState by viewModel.hashtagMentionsSuggestionsManager.suggestionsState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismissRequest, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .imePadding()
        ) {
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    .widthIn(max = 400.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(vectorResource(Res.drawable.edit), contentDescription = null)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.reply),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MaxLengthTextField(
                        value = viewModel.replyText,
                        onValueChange = { viewModel.updateReplyText(it) },
                        textFieldModifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                            viewModel.hashtagMentionsSuggestionsManager.onFocusChanged(focusState.isFocused)
                        },
                        label = Res.string.reply,
                        maxLength = instance?.configuration?.statusConfig?.maxCharacters,
                        submit = { text ->
                            onConfirmation(
                                text
                            )
                            viewModel.replyText = TextFieldValue()
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer

                        ),
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismissRequest) { Text(stringResource(Res.string.cancel)) }
                        TextButton(
                            onClick = {
                                onConfirmation(viewModel.replyText.text)
                                viewModel.replyText = TextFieldValue()
                            },
                            enabled = (instance?.configuration?.statusConfig?.maxCharacters
                                ?: Int.MAX_VALUE) > viewModel.replyText.text.length
                        ) { Text(stringResource(Res.string.send)) }
                    }
                }
            }

            if (viewModel.hashtagMentionsSuggestionsManager.suggestionsOpen) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    SuggestionsBar(
                        state = suggestionsState,
                        bottomBarPadding = false,
                        onSelected = { selected ->
                            viewModel.replyText =
                                viewModel.hashtagMentionsSuggestionsManager.selectSuggestion(
                                    selected, viewModel.replyText
                                )
                        })
                }
            }
        }
    }
}