package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Precision
import coil3.size.Size
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.domain.model.Message
import com.daniebeler.pfpixelix.ui.composables.post.VideoPlayerContent
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.utils.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.this_action_cannot_be_undone
import pixelix.app.generated.resources.delete_message
import pixelix.app.generated.resources.eye_off
import pixelix.app.generated.resources.trash

@Composable
fun ConversationElementComposable(
    message: Message, deleteMessage: () -> Unit, navController: AppNavigator
) {
    var arrangement = Arrangement.Start
    var alignment = Alignment.Start
    var contentAlignment = Alignment.TopStart
    var backgroundColor = MaterialTheme.colorScheme.surfaceContainer
    var textColor = MaterialTheme.colorScheme.onSurface
    val showDeleteReplyDialog = remember {
        mutableStateOf(false)
    }

    var showMediaDialog by remember { mutableStateOf<Message?>(null) }

    var volumeOn by rememberSaveable { mutableStateOf(true) }

    if (message.isAuthor) {
        arrangement = Arrangement.End
        alignment = Alignment.End
        contentAlignment = Alignment.TopEnd
        backgroundColor = MaterialTheme.colorScheme.primary
        textColor = MaterialTheme.colorScheme.onPrimary
    }

    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {

        Box(modifier = Modifier.fillMaxWidth(0.75f), contentAlignment = contentAlignment) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = backgroundColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(
                ) {

                    message.media?.let { media ->
                        MessageMedia(
                            media = media,
                            type = message.type,
                            onClick = { showMediaDialog = message },
                            volumeOn = volumeOn,
                            onToggleVolume = {
                                volumeOn = !volumeOn
                            },
                            textColor = textColor
                        )
                        if (message.text.isNotBlank()) Spacer(Modifier.height(4.dp))
                    }

                    if (message.text.isNotBlank()) {
                        Text(text = message.text, color = textColor)
                    }

                    Row(
                        modifier = Modifier.align(alignment),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = message.timeAgo, color = textColor, fontSize = 10.sp)
                        if (message.seen) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.eye_off),
                                contentDescription = null
                            )
                        }
                        if (message.isAuthor) {
                            Box(modifier = Modifier.clickable {
                                showDeleteReplyDialog.value = true
                            }) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.trash),
                                    contentDescription = "delete message",
                                    Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showDeleteReplyDialog.value) AlertDialog(icon = {
            Icon(
                imageVector = vectorResource(Res.drawable.trash),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }, title = {
            Text(text = stringResource(Res.string.delete_message))
        }, text = {
            Text(text = stringResource(Res.string.this_action_cannot_be_undone))
        }, onDismissRequest = {
            showDeleteReplyDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                deleteMessage()
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

        showMediaDialog?.let {
            MediaDialogChat(it, volumeOn = volumeOn, onToggleVolume = {volumeOn = !volumeOn}, closeDialog = { showMediaDialog = null })
        }
    }


}

@Composable
private fun MediaDialogChat(
    message: Message, volumeOn: Boolean, onToggleVolume: () -> Unit, closeDialog: () -> Unit
) {
    val zoomState = rememberZoomState()
    var isLoading by remember { mutableStateOf(true) }
    if (message.media == null) {
        return
    }
    Dialog(
        onDismissRequest = closeDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))
                .clickable { closeDialog() }, contentAlignment = Alignment.Center
        ) {
            LoadingComposable(isLoading)
            Box(modifier = Modifier.zIndex(2f).zoomable(zoomState).clickable { }) {
                if (message.type != "video") {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current).data(
                            message.media
                        ).size(Size.ORIGINAL).precision(Precision.EXACT).build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        onSuccess = { state ->
                        })
                } else {
                    VideoPlayerContent(
                        id = "chat-${message.id}",
                        url = message.media,
                        aspectRatio = null,
                        volumeOn = volumeOn,
                        onToggleVolume = { onToggleVolume() },
                        autoplay = false,
                        allowFullscreenOnClick = true,
                    )
                }
            }
            Box(Modifier.align(Alignment.TopEnd).padding(20.dp).zIndex(2f)) {
                IconButton(onClick = closeDialog) {
                    Icon(
                        vectorResource(Res.drawable.close),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}
