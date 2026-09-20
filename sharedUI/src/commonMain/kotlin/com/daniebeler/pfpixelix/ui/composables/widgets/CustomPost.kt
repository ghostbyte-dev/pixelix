package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.BlurHashDecoder
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.eye_off
import pixelix.app.generated.resources.images
import pixelix.app.generated.resources.pin
import pixelix.app.generated.resources.remove_circle

private const val DEFAULT_BLUR_HASH = "LEHLk~WB2yk8pyo0adR*.7kCMdnj"

@Composable
fun CustomPost(
    post: Post,
    navController: AppNavigator,
    modifier: Modifier = Modifier,
    isFullQuality: Boolean = false,
    onClick: ((id: String) -> Unit)? = null,
    edit: Boolean = false,
    roundedCornerShape: RoundedCornerShape,
    editRemove: (postId: String) -> Unit = {}
) {
    val prefs = LocalAppComponent.current.preferences
    val blurSensitiveContent by prefs.blurSensitiveContentFlow
        .collectAsState(initial = prefs.hideSensitiveContent)

    val firstBlurHash = post.mediaAttachments.firstOrNull()?.blurHash ?: DEFAULT_BLUR_HASH
    val blurHashBitmap = remember(firstBlurHash) { BlurHashDecoder.decode(firstBlurHash) }

    val handleClick: () -> Unit = {
        if (onClick != null) {
            onClick(post.id)
        } else if (!edit) {
            navController.navigate(Destination.Post(post.id))
        }
    }

    Box(modifier = modifier.clip(roundedCornerShape).aspectRatio(1f)) {
        if (blurHashBitmap != null) {
            Image(
                blurHashBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1f)
            )
        }

        if (post.sensitive && blurSensitiveContent) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.aspectRatio(1f).clickable(onClick = handleClick)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.eye_off),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clickable(onClick = handleClick)
                    .padding(all = if (edit) 12.dp else 0.dp)
                    .clip(RoundedCornerShape(if (edit) 12.dp else 0.dp))
            ) {
                if (post.mediaAttachments.isNotEmpty()) {
                    AsyncImage(
                        model = if (isFullQuality) post.mediaAttachments[0].url
                        else post.mediaAttachments[0].thumbnail ?: post.mediaAttachments[0].url,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(1f)
                    )
                }

                if (post.mediaAttachments.size > 1 && !edit) {
                    Box(
                        modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)
                            .background(
                            MaterialTheme.colorScheme.surfaceContainer.copy(0.8f),
                                RoundedCornerShape(8.dp)
                        ).size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.images),
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                if (post.pinned) {
                    Box(
                        modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer.copy(0.8f),
                                RoundedCornerShape(8.dp)
                            ).size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.pin),
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (edit) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).clickable { editRemove(post.id) }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.remove_circle),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
