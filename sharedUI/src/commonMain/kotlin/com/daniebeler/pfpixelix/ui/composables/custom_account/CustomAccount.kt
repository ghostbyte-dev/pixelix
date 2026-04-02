package com.daniebeler.pfpixelix.ui.composables.custom_account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.ui.composables.FollowButton
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.follower
import pixelix.app.generated.resources.trash

@Composable
fun CustomAccount(
    account: Account,
    relationship: Relationship?,
    navController: NavController,
    showFollowers: Boolean = true,
    viewModel: CustomAccountViewModel = injectViewModel(key = "custom-account" + account.id) { customAccountViewModel }
) {
    CustomAccountPrivate(
        account = account,
        relationship = relationship,
        navController = navController,
        onClick = {},
        savedSearchItem = false,
        showFollowers = showFollowers,
        removeSavedSearch = {},
        viewModel = viewModel
    )
}

@Composable
fun CustomAccount(
    account: Account,
    logoutButton: Boolean = false,
    showFollowers: Boolean = true,
    logout: () -> Unit = {}
) {
    CustomAccountPrivateNotClickable(account = account, showFollowers = showFollowers, logoutButton = logoutButton, logout = logout)
}

@Composable
fun CustomAccount(
    account: Account,
    relationship: Relationship?,
    navController: NavController,
    removeSavedSearch: () -> Unit,
    showFollowers: Boolean = true,
    viewModel: CustomAccountViewModel = injectViewModel(key = "custom-account" + account.id) { customAccountViewModel }
) {
    CustomAccountPrivate(
        account = account,
        relationship = relationship,
        navController = navController,
        onClick = {},
        savedSearchItem = true,
        showFollowers = showFollowers,
        removeSavedSearch = removeSavedSearch,
        viewModel = viewModel
    )
}

@Composable
fun CustomAccount(
    account: Account,
    relationship: Relationship?,
    showFollowers: Boolean = true,
    onClick: () -> Unit,
    navController: NavController,
    viewModel: CustomAccountViewModel = injectViewModel(key = "custom-account" + account.id) { customAccountViewModel }
) {
    CustomAccountPrivate(
        account = account,
        relationship = relationship,
        onClick = onClick,
        savedSearchItem = false,
        removeSavedSearch = {},
        showFollowers = showFollowers,
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun CustomAccountPrivate(
    account: Account,
    relationship: Relationship?,
    onClick: () -> Unit,
    savedSearchItem: Boolean,
    removeSavedSearch: () -> Unit,
    showFollowers: Boolean,
    navController: NavController,
    viewModel: CustomAccountViewModel
) {
    Row(modifier = Modifier
        .clickable {
            onClick()
            navController.navigate(Destination.Profile(account.id))
        }
        .padding(horizontal = 12.dp, vertical = 8.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = "",
            modifier = Modifier
                .height(46.dp)
                .width(46.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {

            Column {
                if (account.displayname != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.displayname,
                            lineHeight = 8.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (showFollowers) {
                            Text(
                                text = " • " + StringFormat.groupDigits(
                                    account.followersCount
                                ) + " " + pluralStringResource(Res.plurals.follower, account.followersCount),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 8.sp
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = account.username, fontSize = 12.sp)
                    Text(
                        text = " • " + (account.url.substringAfter("https://").substringBefore("/")
                            ?: ""), color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp
                    )
                }

            }


        }
        Spacer(modifier = Modifier.weight(1f))

        FollowButton(
            firstLoaded = relationship != null,
            isLoading = viewModel.relationshipState.isLoading,
            isFollowing = if (viewModel.gotUpdatedRelationship) viewModel.relationshipState.accountRelationship?.following
                ?: false else relationship?.following ?: false,
            onFollowClick = { viewModel.followAccount(account.id) },
            onUnFollowClick = { viewModel.unfollowAccount(account.id) },
            iconButton = true
        )

        if (savedSearchItem) {
            Box(
                modifier = Modifier
                    .height(22.dp)
                    .width(22.dp)
                    .clickable { removeSavedSearch() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CustomAccountPrivateNotClickable(
    account: Account, logoutButton: Boolean, showFollowers: Boolean, logout: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = account.avatar,
            contentDescription = "",
            modifier = Modifier
                .height(46.dp)
                .width(46.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {

            Column {
                if (account.displayname != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = account.displayname,
                            lineHeight = 8.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (showFollowers) {
                            Text(
                                text = " • " + StringFormat.groupDigits(
                                    account.followersCount
                                ) + " " + pluralStringResource(Res.plurals.follower, account.followersCount),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 8.sp
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = account.username, fontSize = 12.sp)
                    Text(
                        text = " • " + (account.url.substringAfter("https://").substringBefore("/")
                            ?: ""), color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp
                    )
                }

            }
        }
        Spacer(modifier = Modifier.weight(1f))

        if (logoutButton) {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp)
                    .clickable { logout() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.trash),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}