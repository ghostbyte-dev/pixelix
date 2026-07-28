package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.ui.composables.widgets.LocalVideoPlaybackCoordinator
import com.daniebeler.pfpixelix.utils.KeepScreenOn
import io.github.kdroidfilter.composemediaplayer.AudioMode
import io.github.kdroidfilter.composemediaplayer.InterruptionMode
import io.github.kdroidfilter.composemediaplayer.VideoPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.autoplay
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.pause
import pixelix.app.generated.resources.volume_loud
import pixelix.app.generated.resources.volume_mute

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoAttachment(
    attachment: MediaAttachment, viewModel: PostViewModel, onReady: () -> Unit, isMasonry: Boolean
) {
    val coordinator = LocalVideoPlaybackCoordinator.current
    val isActive = coordinator.activeId == attachment.id

    val player = rememberVideoPlayerState(
        audioMode = AudioMode(interruptionMode = InterruptionMode.MixWithOthers)
    ).apply {
        loop = true
        userDragging = false
    }
    LaunchedEffect(attachment) {
        player.openUri(attachment.url)
    }

    DisposableEffect(attachment.id) {
        onDispose { coordinator.clear(attachment.id) }
    }

    var videoFrameIsVisible by remember { mutableStateOf(false) }

    if (player.isPlaying) {
        KeepScreenOn()
    }
    val modifier = if (!isMasonry) {
        Modifier.clickable {
                coordinator.requestActive(attachment.id)
                player.toggleFullscreen()
        }
    } else {
        Modifier
    }
    Column {
        Box(modifier = modifier) {
            VideoPlayerSurface(playerState = player, modifier = Modifier.fillMaxWidth().run {
                val aspect = attachment.aspectRatio?.toFloat()
                if (aspect != null) aspectRatio(aspect) else this
            }.isVisible(threshold = 50) { visible ->
                videoFrameIsVisible = visible
                coordinator.setVisible(attachment.id, visible)
            }) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (player.isFullscreen) {
                        NavigationBackHandler(
                            state = rememberNavigationEventState(NavigationEventInfo.None),
                            isBackEnabled = true,
                            onBackCompleted = {
                                player.toggleFullscreen()
                            }
                        )
                        var controlsVisible by remember { mutableStateOf(true) }
                        Box(modifier = Modifier.clickable {
                            controlsVisible = !controlsVisible
                        }.fillMaxSize()) {
                            if (controlsVisible) {
                                IconButton(
                                    onClick = { player.toggleFullscreen() },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                                ) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.close),
                                        contentDescription = "Exit Fullscreen"
                                    )
                                }
                                Row(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        modifier = Modifier.padding(8.dp), onClick = {
                                            if (player.isPlaying) {
                                                player.pause()
                                            } else {
                                                coordinator.requestActive(attachment.id)
                                                player.play()
                                            }
                                        }, colors = IconButtonDefaults.filledTonalIconButtonColors()
                                    ) {
                                        if (player.isPlaying) {
                                            Icon(
                                                vectorResource(Res.drawable.pause),
                                                contentDescription = "Pause",
                                                Modifier.size(18.dp)
                                            )
                                        } else {
                                            Icon(
                                                vectorResource(Res.drawable.autoplay),
                                                contentDescription = "Play",
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
                                                    vectorResource(Res.drawable.volume_loud),
                                                    contentDescription = "Volume on",
                                                    Modifier.size(18.dp)
                                                )
                                            } else {
                                                Icon(
                                                    vectorResource(Res.drawable.volume_mute),
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
                        modifier = Modifier.padding(9.dp), onClick = {
                            viewModel.toggleVolume(!viewModel.volume)
                        }, colors = IconButtonDefaults.filledTonalIconButtonColors()
                    ) {
                        if (viewModel.volume) {
                            Icon(
                                vectorResource(Res.drawable.volume_loud),
                                contentDescription = "Volume on",
                                Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                vectorResource(Res.drawable.volume_mute),
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
                            coordinator.requestActive(attachment.id)
                            player.play()
                        }
                    }, colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    if (player.isPlaying) {
                        Icon(
                            vectorResource(Res.drawable.pause),
                            contentDescription = "Pause",
                            Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            vectorResource(Res.drawable.autoplay),
                            contentDescription = "Play",
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

    LaunchedEffect(isActive, viewModel.volume) {
        player.volume = if (isActive && viewModel.volume) 1f else 0f
    }

    val autoPlay = isActive && viewModel.isAutoplayVideos
    LaunchedEffect(autoPlay) {
        if (autoPlay) player.play() else player.pause()
    }
    LaunchedEffect(coordinator.activeId) {
        if (!isActive) player.pause()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isActive && videoFrameIsVisible && viewModel.isAutoplayVideos) {
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
            } catch (_: Throwable) {
                "--:--"
            }
        }
        Text(
            text = remainingText,
            color = Color.White,
            modifier = Modifier.padding(PaddingValues(4.dp, 0.dp, 0.dp, 0.dp))
        )
    }
}