package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.TimeAgo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.followed_you
import pixelix.app.generated.resources.liked_your_post
import pixelix.app.generated.resources.mentioned_you_in_a_post
import pixelix.app.generated.resources.reblogged_your_post
import pixelix.app.generated.resources.sent_a_dm

@Composable
fun CustomNotification(
    notification: Notification,
    navController: NavController,
    viewModel: CustomNotificationViewModel = injectViewModel(key = "custom-notification-viewmodel-key${notification.id}") { customNotificationViewModel }
) {
    var showImage = false
    var text = ""
    when (notification.type) {
        "follow" -> {
            text = " " + stringResource(Res.string.followed_you)
        }

        "mention" -> {
            text = " " + stringResource(Res.string.mentioned_you_in_a_post)
            showImage = true
        }

        "direct" -> {
            text = " " + stringResource(Res.string.sent_a_dm)
        }

        "favourite" -> {
            text = " " + stringResource(Res.string.liked_your_post)
            showImage = true
        }

        "reblog" -> {
            text = " " + stringResource(Res.string.reblogged_your_post)
            showImage = true
        }
    }

    LaunchedEffect(notification) {
        if (notification.type == "mention" && notification.post?.inReplyToId != null && notification.post.inReplyToId.isNotBlank()) {
            viewModel.loadAncestor(notification.post.inReplyToId)
        }
    }

    val timeAgoText = produceState(initialValue = "") {
        value = TimeAgo.convertTimeToText(notification.createdAt)
    }

    Row(
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth().clickable {
                if (notification.post != null && notification.post.mediaAttachments.isEmpty()) {
                    navController.navigate(Destination.Mention(notification.post.id))
                } else if (notification.post != null && notification.post.mediaAttachments.isNotEmpty()) {
                    navController.navigate(Destination.Post(notification.post.id))
                } else if (notification.post == null) {
                    navController.navigate(Destination.Profile(notification.account.id))
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = notification.account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = "",
            modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape).clickable {
                    navController.navigate(Destination.Profile(notification.account.id))
                })
        Spacer(modifier = Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            val annotatedText = buildAnnotatedString {
                pushStringAnnotation(tag = "username", annotation = notification.account.id)
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    append(notification.account.username)
                }
                pop()
                append(" ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append(text)
                }
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "username", start = offset, end = offset
                    ).firstOrNull()?.let { annotation ->
                            if (annotation.tag == "username") {
                                navController.navigate(Destination.Profile(annotation.item))
                            }
                        } ?: kotlin.run {
                        if (notification.post != null && notification.post.mediaAttachments.isEmpty()) {
                            navController.navigate(Destination.Mention(notification.post.id))
                        } else if (notification.post != null && notification.post.mediaAttachments.isNotEmpty()) {
                            navController.navigate(Destination.Post(notification.post.id))
                        } else if (notification.post == null) {
                            navController.navigate(Destination.Profile(notification.account.id))
                        }
                    }
                })


            Text(
                text = timeAgoText.value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val doesMediaAttachmentExsist = (notification.post?.mediaAttachments?.size ?: 0) > 0
        if (showImage && (doesMediaAttachmentExsist || (viewModel.ancestor != null && viewModel.ancestor!!.mediaAttachments.isNotEmpty()))) {
            val previewUrl = if (doesMediaAttachmentExsist) {
                notification.post?.mediaAttachments?.get(0)?.previewUrl
            } else {
                viewModel.ancestor?.mediaAttachments?.get(0)?.previewUrl
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
    }
}