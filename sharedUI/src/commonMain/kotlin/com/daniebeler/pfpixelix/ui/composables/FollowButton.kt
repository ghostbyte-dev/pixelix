package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
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
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.follow
import pixelix.app.generated.resources.unfollow

@Composable
fun FollowButton(
    firstLoaded: Boolean,
    isLoading: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit,
    iconButton: Boolean = false
) {
    if (iconButton) {
        IconFollowButton(
            firstLoaded = firstLoaded,
            isLoading = isLoading,
            isFollowing = isFollowing,
            onFollowClick = onFollowClick,
            onUnFollowClick = onUnFollowClick
        )
    } else {
        TextFollowButton(
            firstLoaded = firstLoaded,
            isLoading = isLoading,
            isFollowing = isFollowing,
            onFollowClick = onFollowClick,
            onUnFollowClick = onUnFollowClick
        )
    }
}

@Composable
private fun IconFollowButton(
    firstLoaded: Boolean,
    isLoading: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit
) {
    Box(modifier = Modifier.height(40.dp)) {
        if (firstLoaded) {
            if (isLoading) {
                if (isFollowing) {
                    IconButton(
                        onClick = {}, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                } else {
                    IconButton(
                        onClick = {}, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                if (isFollowing) {
                    IconButton(
                        onClick = {
                            onUnFollowClick()
                        }, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove, contentDescription = ""
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            onFollowClick()
                        }, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextFollowButton(
    firstLoaded: Boolean,
    isLoading: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onUnFollowClick: () -> Unit
) {
    Box(modifier = Modifier.height(40.dp)) {
        if (firstLoaded) {
            if (isLoading) {
                if (isFollowing) {
                    Button(
                        onClick = {},
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Button(onClick = {}, modifier = Modifier.width(120.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                if (isFollowing) {
                    Button(
                        onClick = {
                            onUnFollowClick()
                        }, modifier = Modifier.width(120.dp), colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = stringResource(Res.string.unfollow))
                    }
                } else {
                    Button(onClick = {
                        onFollowClick()
                    }, modifier = Modifier.width(120.dp)) {
                        Text(text = stringResource(Res.string.follow))
                    }
                }
            }
        }
    }
}