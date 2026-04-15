package com.daniebeler.pfpixelix.ui.composables.custom_account

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.daniebeler.pfpixelix.ui.composables.widgets.FollowButton
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.follower
import pixelix.app.generated.resources.trash

/**
 * Clickable account row with navigation and optional follow button.
 */
@Composable
fun CustomAccount(
    account: Account,
    relationship: Relationship?,
    navController: NavController,
    showFollowers: Boolean = true,
    onClick: () -> Unit = {},
    removeSavedSearch: (() -> Unit)? = null,
    viewModel: CustomAccountViewModel = injectViewModel(key = "custom-account" + account.id) { customAccountViewModel }
) {
    AccountRow(
        account = account,
        showFollowers = showFollowers,
        modifier = Modifier.clickable {
            onClick()
            navController.navigate(Destination.Profile(account.id))
        }
    ) {
        FollowButton(
            firstLoaded = relationship != null,
            isLoading = viewModel.relationshipState.isLoading,
            isFollowing = if (viewModel.gotUpdatedRelationship) {
                viewModel.relationshipState.accountRelationship?.following ?: false
            } else {
                relationship?.following ?: false
            },
            onFollowClick = { viewModel.followAccount(account.id) },
            onUnFollowClick = { viewModel.unfollowAccount(account.id) },
            iconButton = true
        )

        if (removeSavedSearch != null) {
            IconButton(onClick = removeSavedSearch, modifier = Modifier.height(22.dp).width(22.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Non-clickable account row for display-only contexts (e.g. account switcher, trending).
 */
@Composable
fun CustomAccount(
    account: Account,
    showFollowers: Boolean = true,
    logoutButton: Boolean = false,
    logout: () -> Unit = {}
) {
    AccountRow(
        account = account,
        showFollowers = showFollowers
    ) {
        if (logoutButton) {
            IconButton(
                onClick = logout,
                modifier = Modifier.height(36.dp).width(36.dp)
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

@Composable
private fun AccountRow(
    account: Account,
    showFollowers: Boolean,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = null,
            modifier = Modifier.height(46.dp).width(46.dp).clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (account.displayname != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.displayname,
                        lineHeight = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (showFollowers) {
                        Text(
                            text = " \u2022 ${StringFormat.groupDigits(account.followersCount)} ${
                                pluralStringResource(Res.plurals.follower, account.followersCount)
                            }",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 8.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = account.username, fontSize = 12.sp)
                val domain = account.url.substringAfter("https://").substringBefore("/")
                Text(
                    text = " \u2022 $domain",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
            }
        }

        trailingContent()
    }
}
