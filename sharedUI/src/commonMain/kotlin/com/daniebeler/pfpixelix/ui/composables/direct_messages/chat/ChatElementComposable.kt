package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Message
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.delete
import pixelix.app.generated.resources.this_action_cannot_be_undone
import pixelix.app.generated.resources.delete_message
@Composable
fun ConversationElementComposable(
    message: Message, deleteMessage: () -> Unit, navController: NavController
) {
    var arrangement = Arrangement.Start
    var alignment = Alignment.Start
    var contentAlignment = Alignment.TopStart
    var backgroundColor = MaterialTheme.colorScheme.surfaceContainer
    var textColor = MaterialTheme.colorScheme.onSurface
    val showDeleteReplyDialog = remember {
        mutableStateOf(false)
    }

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
                    Text(text = message.text, color = textColor)

                    Row(
                        modifier = Modifier.align(alignment),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = message.timeAgo, color = textColor, fontSize = 10.sp)
                        if (message.seen) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveRedEye, contentDescription = null
                            )
                        }
                        if (message.isAuthor) {
                            Box(modifier = Modifier.clickable {
                                showDeleteReplyDialog.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
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
                imageVector = Icons.Outlined.Delete,
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
    }


}