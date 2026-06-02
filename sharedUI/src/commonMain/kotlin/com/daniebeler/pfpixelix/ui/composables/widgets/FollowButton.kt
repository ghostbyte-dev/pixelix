package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add
import pixelix.app.generated.resources.remove
import pixelix.app.generated.resources.follow
import pixelix.app.generated.resources.unfollow

private enum class FollowState {
    Hidden, LoadingFollow, LoadingUnfollow, Following, NotFollowing
}

private fun resolveFollowState(
    firstLoaded: Boolean,
    isLoading: Boolean,
    isFollowing: Boolean
): FollowState = when {
    !firstLoaded -> FollowState.Hidden
    isLoading && isFollowing -> FollowState.LoadingUnfollow
    isLoading -> FollowState.LoadingFollow
    isFollowing -> FollowState.Following
    else -> FollowState.NotFollowing
}

@Composable
fun FollowButton(
    firstLoaded: Boolean,
    isLoading: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit,
    iconButton: Boolean = false
) {
    val state = resolveFollowState(firstLoaded, isLoading, isFollowing)
    if (state == FollowState.Hidden) return

    if (iconButton) {
        IconFollowButton(state, onFollowClick, onUnFollowClick)
    } else {
        TextFollowButton(state, onFollowClick, onUnFollowClick)
    }
}

@Composable
private fun IconFollowButton(
    state: FollowState,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit
) {
    val isFollowing = state == FollowState.Following || state == FollowState.LoadingUnfollow
    val isLoading = state == FollowState.LoadingFollow || state == FollowState.LoadingUnfollow

    val containerColor = if (isFollowing) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
    val contentColor = if (isFollowing) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary

    Box(modifier = Modifier.height(40.dp)) {
        IconButton(
            onClick = if (isLoading) ({}) else if (isFollowing) onUnFollowClick else onFollowClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            if (isLoading) {
                LoadingComposable(modifier = Modifier.size(20.dp), color = contentColor)
            } else if (isFollowing) {
                Icon(imageVector = vectorResource(Res.drawable.remove), contentDescription = null)
            } else {
                Icon(imageVector = vectorResource(Res.drawable.add), contentDescription = null)
            }
        }
    }
}

@Composable
private fun TextFollowButton(
    state: FollowState,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit
) {
    val isFollowing = state == FollowState.Following || state == FollowState.LoadingUnfollow
    val isLoading = state == FollowState.LoadingFollow || state == FollowState.LoadingUnfollow

    val buttonColors = if (isFollowing) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    } else {
        ButtonDefaults.buttonColors()
    }

    Box(modifier = Modifier.height(40.dp)) {
        Button(
            onClick = if (isLoading) ({}) else if (isFollowing) onUnFollowClick else onFollowClick,
            modifier = Modifier.width(120.dp),
            colors = buttonColors
        ) {
            if (isLoading) {
                LoadingComposable(
                    modifier = Modifier.size(20.dp),
                    color = if (isFollowing) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onPrimary
                )
            } else if (isFollowing) {
                Text(text = stringResource(Res.string.unfollow))
            } else {
                Text(text = stringResource(Res.string.follow))
            }
        }
    }
}
