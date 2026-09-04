package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposableDialog
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.timeAgo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.boosted_status_updated
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.follow_request
import pixelix.app.generated.resources.followed_you
import pixelix.app.generated.resources.liked_your_post
import pixelix.app.generated.resources.mentioned_you_in_a_post
import pixelix.app.generated.resources.new_comment
import pixelix.app.generated.resources.new_status
import pixelix.app.generated.resources.notification
import pixelix.app.generated.resources.reblogged_your_post
import pixelix.app.generated.resources.sent_a_dm

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomNotification(
    notification: Notification,
    navController: AppNavigator,
    removeNotification: () -> Unit,
    index: Int,
    count: Int,
    viewModel: CustomNotificationViewModel = injectViewModel(key = "custom-notification-viewmodel-key${notification.id}") { customNotificationViewModel }
) {
    var showImage = false
    var text: String
    when (notification.type) {
        NotificationType.FOLLOW -> {
            text = " " + stringResource(Res.string.followed_you)
        }

        NotificationType.MENTION -> {
            text = " " + stringResource(Res.string.mentioned_you_in_a_post)
            showImage = true
        }

        NotificationType.DIRECT_MESSAGE -> {
            text = " " + stringResource(Res.string.sent_a_dm)
        }

        NotificationType.FAVOURITE -> {
            text = " " + stringResource(Res.string.liked_your_post)
            showImage = true
        }

        NotificationType.REBLOG -> {
            text = " " + stringResource(Res.string.reblogged_your_post)
            showImage = true
        }

        NotificationType.NEW_COMMENT -> {
            text = " " + stringResource(Res.string.new_comment)
            showImage = true
        }

        NotificationType.FOLLOW_REQUEST -> {
            text = " " + stringResource(Res.string.follow_request)
        }

        NotificationType.STATUS -> {
            text = " " + stringResource(Res.string.new_status)
            showImage = true
        }

        NotificationType.UPDATE -> {
            text = " " + stringResource(Res.string.boosted_status_updated)
            showImage = true
        }

        NotificationType.UNDEFINED -> {
            text = " " + stringResource(Res.string.notification)
        }
    }

    LaunchedEffect(notification) {
        if (notification.type == NotificationType.MENTION && notification.post?.inReplyToId != null && notification.post.inReplyToId.isNotBlank()) {
            viewModel.loadAncestor(notification.post.inReplyToId)
        }
    }

    val timeAgoText = produceState(initialValue = "") {
        value = timeAgo(notification.createdAt)
    }

    val annotatedText = buildAnnotatedString {
        pushStringAnnotation(tag = "username", annotation = notification.account.id)
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            append(notification.account.displayname ?: notification.account.username)
        }
        pop()
        append(" ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append(text)
        }
    }

    SegmentedListItem(
        shapes = ListItemDefaults.segmentedShapes(index, count),
        content = {
            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "username", start = offset, end = offset
                    ).firstOrNull()?.let { annotation ->
                        if (annotation.tag == "username") {
                            navController.navigate(
                                Destination.Profile(
                                    annotation.item, notification.account.username
                                )
                            )
                        }
                    } ?: run {
                        if (notification.post != null && notification.post.mediaAttachments.isEmpty()) {
                            navController.navigate(Destination.Mention(notification.post.id))
                        } else if (notification.post != null && notification.post.mediaAttachments.isNotEmpty()) {
                            navController.navigate(Destination.Post(notification.post.id))
                        } else if (notification.post == null) {
                            navController.navigate(
                                Destination.Profile(
                                    notification.account.id, notification.account.username
                                )
                            )
                        }
                    }
                })
        },
        supportingContent = {
            if (notification.type == NotificationType.FOLLOW_REQUEST && viewModel.capabilities.value.notification.supportsFollowRequestActions) {
                Row {
                    Button(
                        onClick = {
                            viewModel.acceptFollowRequest(
                                notification.account.id, removeNotification
                            )
                        },
                        modifier = Modifier.padding(end = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        if (viewModel.followRequestState.value.isLoading && viewModel.followRequestState.value.isAccepting) {
                            LoadingComposable(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(text = "Accept")
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.rejectFollowRequest(
                                notification.account.id, removeNotification
                            )
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    ) {
                        if (viewModel.followRequestState.value.isLoading && !viewModel.followRequestState.value.isAccepting) {
                            LoadingComposable(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        } else {
                            Text(text = "Reject")
                        }
                    }
                }
            } else {
                Text(
                    text = timeAgoText.value,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        leadingContent = {
            AsyncImage(
                model = notification.account.avatar,
                error = painterResource(Res.drawable.default_avatar),
                contentDescription = "",
                modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape).clickable {
                    navController.navigate(
                        Destination.Profile(
                            notification.account.id, notification.account.username
                        )
                    )
                })
        },
        trailingContent = {
            val doesMediaAttachmentExsist = (notification.post?.mediaAttachments?.size ?: 0) > 0
            if (showImage && (doesMediaAttachmentExsist || (viewModel.ancestor != null && viewModel.ancestor!!.mediaAttachments.isNotEmpty()))) {
                val previewUrl = if (doesMediaAttachmentExsist) {
                    notification.post?.mediaAttachments?.get(0)?.previewUrl ?: notification.post?.mediaAttachments?.get(0)?.url
                } else {
                    viewModel.ancestor?.mediaAttachments?.get(0)?.previewUrl ?: notification.post?.mediaAttachments?.get(0)?.url
                }
                Spacer(modifier = Modifier.width(10.dp))
                AsyncImage(
                    model = previewUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(36.dp).aspectRatio(1f).clip(RoundedCornerShape(4.dp))
                        .clickable {
                            navController.navigate(
                                Destination.Post(
                                    id = if (doesMediaAttachmentExsist) {
                                        notification.post!!.id
                                    } else {
                                        viewModel.ancestor!!.id
                                    }, openReplies = !doesMediaAttachmentExsist
                                )
                            )
                        })
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        onClick = {
            if (notification.post != null && notification.post.mediaAttachments.isEmpty()) {
                navController.navigate(Destination.Mention(notification.post.id))
            } else if (notification.post != null && notification.post.mediaAttachments.isNotEmpty()) {
                navController.navigate(Destination.Post(notification.post.id))
            } else if (notification.post == null) {
                navController.navigate(
                    Destination.Profile(
                        notification.account.id, notification.account.username
                    )
                )
            }
        })




    ErrorComposableDialog(viewModel.followRequestState.value.error, {
        viewModel.followRequestState.value = FollowRequestState()
    })
}