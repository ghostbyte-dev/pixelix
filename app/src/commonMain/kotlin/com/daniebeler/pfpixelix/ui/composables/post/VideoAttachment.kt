package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState

@Composable
fun VideoAttachment(
    attachment: MediaAttachment,
    viewModel: PostViewModel,
    onReady: () -> Unit
) {
    val player = rememberVideoPlayerState().apply {
        loop = true
        userDragging = false
    }
    LaunchedEffect(attachment) {
        player.openUri(attachment.url.orEmpty())
    }

    var videoFrameIsVisible by remember { mutableStateOf(false) }

    Column {
        Box(Modifier.clickable {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }) {
            VideoPlayerSurface(
                playerState = player,
                modifier = Modifier
                    .fillMaxWidth()
                    .run {
                        val aspect = attachment.meta?.original?.aspect?.toFloat()
                        if (aspect != null) aspectRatio(aspect) else this
                    }
                    .isVisible(threshold = 50) { videoFrameIsVisible = it }
            )
            val hasAudio = (player.metadata.audioChannels ?: 0) > 0
            if (hasAudio) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    onClick = {
                        viewModel.toggleVolume(!viewModel.volume)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    if (viewModel.volume) {
                        Icon(
                            Icons.AutoMirrored.Outlined.VolumeUp,
                            contentDescription = "Volume on",
                            Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            Icons.AutoMirrored.Outlined.VolumeOff,
                            contentDescription = "Volume off",
                            Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        LinearProgressIndicator(
            progress = { player.sliderPos / 1000 },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.background
        )
    }

    val started = player.sliderPos > 0
    LaunchedEffect(started) { if (started) onReady() }

    LaunchedEffect(viewModel.volume) {
        player.volume = if (viewModel.volume) 1f else 0f
    }

    val autoPlay = videoFrameIsVisible && viewModel.isAutoplayVideos
    LaunchedEffect(autoPlay) {
        if (autoPlay) {
            player.play()
        } else {
            player.pause()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (videoFrameIsVisible && viewModel.isAutoplayVideos) {
                        player.play()
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    player.pause()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

}
