package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.BlurHashDecoder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.stack

@Composable
fun CustomPost(
    post: Post,
    isFullQuality: Boolean = false,
    navController: NavController,
    customModifier: Modifier = Modifier,
    onClick: ((id: String) -> Unit)? = null,
    edit: Boolean = false,
    editRemove: (postId: String) -> Unit = {}
) {
    val prefs = LocalAppComponent.current.preferences
    val blurSensitiveContent = remember { mutableStateOf(prefs.hideSensitiveContent) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            prefs.blurSensitiveContentFlow.collect { blurSensitiveContent.value = it }
        }
    }

    val firstBlurHash = post.mediaAttachments.firstOrNull()?.blurHash
        ?: "LEHLk~WB2yk8pyo0adR*.7kCMdnj"
    val blurHashBitmap = remember(firstBlurHash) { BlurHashDecoder.decode(firstBlurHash) }

    Box(modifier = customModifier.clip(RoundedCornerShape(6.dp)).aspectRatio(1f)) {
        if (blurHashBitmap != null) {
            Image(
                blurHashBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1f)
            )
        }

        if (post.sensitive && blurSensitiveContent.value) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(onClick = {
                        if (!edit && onClick == null) {
                            navController.navigate(Destination.Post(post.id))
                        } else if (onClick != null){
                            onClick(post.id)
                        }
                    }),
            ) {


                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    Modifier.size(50.dp)
                )
            }
        } else {
            Box(
                customModifier
                    .clickable(onClick = {
                        if (!edit && onClick == null) {
                            navController.navigate(Destination.Post(post.id))
                        } else if (onClick != null){
                            onClick(post.id)
                        }
                    })
                    .padding(
                        all = if (edit) {
                            12.dp
                        } else {
                            0.dp
                        }
                    )
                    .clip(if (edit) {RoundedCornerShape(12.dp)} else {
                        RoundedCornerShape(0)
                    })
            ) {

                if (post.mediaAttachments.isNotEmpty()) {
                    AsyncImage(
                        model = if (isFullQuality) {
                            post.mediaAttachments[0].url
                        } else {
                            post.mediaAttachments[0].previewUrl
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(1f)
                    )
                }

                if (post.mediaAttachments.size > 1 && !edit) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.stack),
                            tint = Color.White,
                            contentDescription = null,
                        )

                    }
                }


            }
            if (edit) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).clickable {
                        editRemove(post.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.RemoveCircle,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null,
                    )
                }
            }
        }
    }


}