package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.utils.KeepScreenOn
import io.github.kdroidfilter.composemediaplayer.VideoPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoAttachment(
    attachment: MediaAttachment, viewModel: PostViewModel, onReady: () -> Unit
) {
    val player = rememberVideoPlayerState().apply {
        loop = true
        userDragging = false
    }
    LaunchedEffect(attachment) {
        player.openUri(attachment.url.orEmpty())
    }

    var videoFrameIsVisible by remember { mutableStateOf(false) }

    if (player.isPlaying) {
        KeepScreenOn()
    }

    Column {
        Box(Modifier.clickable {
            player.toggleFullscreen()
        }) {
            VideoPlayerSurface(playerState = player, modifier = Modifier.fillMaxWidth().run {
                val aspect = attachment.meta?.original?.aspect?.toFloat()
                if (aspect != null) aspectRatio(aspect) else this
            }.isVisible(threshold = 50) { videoFrameIsVisible = it }) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (player.isFullscreen) {
                        var controlsVisible by remember { mutableStateOf(true) }
                        Box(modifier = Modifier.clickable {
                            controlsVisible = !controlsVisible
                        }.fillMaxSize().pointerInput(Unit) {
                        var totalVerticalDrag = 0f
                        val triggerThreshold = 150.dp.toPx() // Distance to drag down before exiting

                        detectVerticalDragGestures(
                            onDragStart = {
                                totalVerticalDrag = 0f
                            },
                            onDragEnd = {
                                // If swiped down far enough, close fullscreen
                                if (totalVerticalDrag > triggerThreshold) {
                                    player.isFullscreen = false
                                }
                            },
                            onDragCancel = {
                                totalVerticalDrag = 0f
                            },
                            onVerticalDrag = { change, dragAmount ->
                                // Only care about downward movement (positive dragAmount)
                                if (dragAmount > 0 || totalVerticalDrag > 0) {
                                    totalVerticalDrag += dragAmount
                                    change.consume()
                                }
                            }
                        )
                    }) {
                            if (controlsVisible) {
                                IconButton(
                                    onClick = { player.toggleFullscreen() },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Exit Fullscreen",
                                        tint = Color.White
                                    )
                                }
                                Row(modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        modifier = Modifier.padding(8.dp), onClick = {
                                            if (player.isPlaying) {
                                                player.pause()
                                            } else {
                                                player.play()
                                            }
                                        }, colors = IconButtonDefaults.filledTonalIconButtonColors()
                                    ) {
                                        if (player.isPlaying) {
                                            Icon(
                                                Icons.Default.Pause,
                                                contentDescription = "Volume on",
                                                Modifier.size(18.dp)
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = "Volume off",
                                                Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Box(Modifier.weight(1f)) {
                                        TimelineControls(player)
                                    }

                                    val hasAudio = (player.metadata.audioChannels ?: 0) > 0
                                    if (hasAudio) {
                                        IconButton(
                                            modifier = Modifier.padding(8.dp),
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
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
            ) {
                val hasAudio = (player.metadata.audioChannels ?: 0) > 0
                if (hasAudio) {
                    IconButton(
                        modifier = Modifier.padding(8.dp), onClick = {
                            viewModel.toggleVolume(!viewModel.volume)
                        }, colors = IconButtonDefaults.filledTonalIconButtonColors()
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
                IconButton(
                    modifier = Modifier.padding(8.dp), onClick = {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }, colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    if (player.isPlaying) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Volume on",
                            Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
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

    LaunchedEffect(player.isPlaying) {
        if (player.isPlaying) onReady()
    }

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

@Composable
fun TimelineControls(
    playerState: VideoPlayerState
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = Modifier.weight(1f),
            value = playerState.sliderPos, onValueChange = {
                playerState.sliderPos = it
                playerState.userDragging = true
            }, onValueChangeFinished = {
                playerState.userDragging = false
                playerState.seekTo(playerState.sliderPos)
            }, valueRange = 0f..1000f, colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
            )
        )
        val remainingText = remember(playerState.durationText, playerState.currentTime) {
            try {
                val parts = playerState.durationText.split(":")
                val totalSeconds = (parts[0].toInt() * 60) + parts[1].toInt()

                val remaining = (totalSeconds - playerState.currentTime).toInt().coerceAtLeast(0)

                val mins = remaining / 60
                val secs = remaining % 60
                "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
            } catch (e: Exception) {
                "--:--"
            }
        }
        Text(text = remainingText, modifier = Modifier.padding(PaddingValues(4.dp, 0.dp, 0.dp, 0.dp)))
    }
}