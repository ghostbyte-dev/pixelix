package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.post.VideoPlayerContent
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable

@Composable
fun MessageMedia(
    media: String,
    type: String,
    volumeOn: Boolean,
    onToggleVolume: () -> Unit,
    onClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    var mediaLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (type == "photo") {
            AsyncImage(
                model = media,
                contentDescription = "image",
                modifier = Modifier.fillMaxSize(),
                onSuccess = {
                    mediaLoading = false
                }
            )
            if (mediaLoading) {
                LoadingComposable(color = textColor)
            }
        } else if (type == "video") {
            VideoPlayerContent(
                id = "chat-${media}",
                url = media,
                aspectRatio = null,
                volumeOn = volumeOn,
                onToggleVolume = { onToggleVolume() },
                autoplay = false,
                allowFullscreenOnClick = true,
                onReady = {
                    mediaLoading = false
                }
            )
        }
    }
}