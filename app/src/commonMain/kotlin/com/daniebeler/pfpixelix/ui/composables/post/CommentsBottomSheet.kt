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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.ui.composables.post.reply.ReplyElementViewModel
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FixedHeightLoadingComposable
import com.daniebeler.pfpixelix.ui.composables.textfield_mentions.TextFieldMentionsComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.TimeAgo
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.delete_reply
import pixelix.app.generated.resources.no_comments_yet
import pixelix.app.generated.resources.reply
import pixelix.app.generated.resources.this_action_cannot_be_undone

@Composable
fun CommentsBottomSheet(
    post: Post, navController: NavController, viewModel: PostViewModel
) {
    var replyText by remember { mutableStateOf(TextFieldValue("")) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .imePadding()
                .fillMaxHeight()
                .align(Alignment.TopStart)
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
                        place = null,
                        inReplyToId = null
                    )
                    ReplyElement(
                        reply = ownDescription,
                        true,
                        navController = navController,
                        {},
                        viewModel.myAccountId,
                        { url -> viewModel.openUrl(url) }, instance = viewModel.instance)
                }
                Column {
                    TextFieldMentionsComposable(
                        submit = { text ->
                            replyText = TextFieldValue()
                            viewModel.createReply(
                                post.id, text
                            )
                        },
                        replyText,
                        changeText = { newText -> replyText = newText },
                        labelStringId = Res.string.reply,
                        modifier = null,
                        imeAction = ImeAction.Send,
                        suggestionsBoxColor = MaterialTheme.colorScheme.surface,
                        submitButton = { enabled ->
                            Button(
                                onClick = {
                                    if (!viewModel.ownReplyState.isLoading) {
                                        viewModel.createReply(post.id, replyText.text)
                                        replyText = replyText.copy(text = "")
                                    }
                                },
                                Modifier
                                    .height(56.dp)
                                    .width(56.dp)
                                    .padding(0.dp, 0.dp),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(12.dp),
                                enabled = enabled
                            ) {
                                if (viewModel.ownReplyState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "submit",
                                        Modifier
                                            .fillMaxSize()
                                            .fillMaxWidth()
                                    )
                                }

                            }
                        },
                        maxLength = viewModel.instance?.configuration?.statusConfig?.maxCharacters)
                }

                HorizontalDivider(Modifier.padding(12.dp))
            }

            items(viewModel.repliesState.replies, key = {
                it.id
            }) { reply ->
                ReplyElement(
                    reply = reply,
                    false,
                    navController = navController,
                    { viewModel.deleteReply(reply.id) },
                    viewModel.myAccountId,
                    { url -> viewModel.openUrl(url) }, instance = viewModel.instance)
            }

            if (viewModel.repliesState.isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            if (!viewModel.repliesState.isLoading && viewModel.repliesState.error.isBlank() && viewModel.repliesState.replies.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .fillMaxWidth(),
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
                    Modifier
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                )
            }
        }

    }
}


@Composable
private fun ReplyElement(
    reply: Post,
    postDescription: Boolean,
    navController: NavController,
    deleteReply: () -> Unit,
    myAccountId: String?,
    openUrl: (url: String) -> Unit,
    viewModel: ReplyElementViewModel = injectViewModel(key = reply.id) { replyElementViewModel },
    instance: Instance?
) {

    var timeAgo: String by remember { mutableStateOf("") }
    var replyCount: Int by remember { mutableIntStateOf(reply.replyCount) }
    val openAddReplyDialog = remember { mutableStateOf(false) }
    val showDeleteReplyDialog = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(reply.createdAt) {
        if (myAccountId != null) {
            viewModel.onInit(reply, myAccountId)
        }
        timeAgo = TimeAgo.convertTimeToText(reply.createdAt)
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row {
            AsyncImage(
                model = reply.account.avatar,
                contentDescription = "",
                modifier = Modifier
                    .height(42.dp)
                    .width(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(Destination.Profile(reply.account.id))
                    })

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row {
                    Text(
                        text = reply.account.acct,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Destination.Profile(reply.account.id))
                        })

                    Text(
                        text = " • $timeAgo",
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }


                HashtagsMentionsTextView(
                    text = reply.content,
                    mentions = reply.mentions,
                    navController = navController,
                    openUrl = { url -> openUrl(url) })
            }
        }

        if (!postDescription) {
            Row(Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                if (reply.account.id == myAccountId) {
                    IconButton(onClick = { showDeleteReplyDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
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

                if (viewModel.likedReply) {
                    IconButton(onClick = {
                        viewModel.unlikeReply(reply.id)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.likeReply(reply.id)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder, contentDescription = ""
                        )
                    }
                }
            }

            if (replyCount != 0 && viewModel.repliesState.replies.isEmpty()) {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    TextButton(onClick = { viewModel.loadReplies(reply.id) }) {
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
            if (viewModel.repliesState.isLoading) {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    FixedHeightLoadingComposable()
                }
            } else if (viewModel.repliesState.error != "") {
                Box(modifier = Modifier.padding(54.dp, 0.dp, 0.dp, 0.dp)) {
                    ErrorComposable(viewModel.repliesState.error)
                }
            } else if (viewModel.repliesState.replies.isNotEmpty()) {
                Box(Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                    Column {
                        viewModel.repliesState.replies.map {
                            ReplyElement(
                                reply = it, false, navController = navController, {
                                    viewModel.deleteReply(it.id)
                                    replyCount--
                                }, myAccountId, openUrl, instance = instance
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
            if (myAccountId != null) {
                viewModel.createReply(reply.id, it, myAccountId)
            }
        }, instance = instance)
    }

    if (showDeleteReplyDialog.value) {
        AlertDialog(icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
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
    instance: Instance?
) {
    var replyText by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(modifier = Modifier.imePadding(),icon = {
        Icon(Icons.Outlined.Edit, contentDescription = "Edit")
    }, title = {
        Text(text = stringResource(Res.string.reply))
    }, text = {
        TextFieldMentionsComposable(
            submit = { text ->
                replyText = TextFieldValue()
                onConfirmation(
                    text
                )
            },
            text = replyText,
            changeText = { newText -> replyText = newText },
            Res.string.reply,
            modifier = null,
            imeAction = ImeAction.Send,
            submitButton = null,
            suggestionsBoxColor = MaterialTheme.colorScheme.surface,
            maxLength = (instance?.configuration?.statusConfig?.maxCharacters),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation(replyText.text)
        }, enabled = ((instance?.configuration?.statusConfig?.maxCharacters ?: Int.MAX_VALUE) > replyText.text.length)) {
            Text("Send")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text("Dismiss")
        }
    })
}