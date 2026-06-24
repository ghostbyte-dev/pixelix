package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.DomainFormat
import com.daniebeler.pfpixelix.utils.StringFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.admin
import pixelix.app.generated.resources.are_you_sure
import pixelix.app.generated.resources.blocked
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.cancel_post_warning
import pixelix.app.generated.resources.confirm
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.discard
import pixelix.app.generated.resources.follower
import pixelix.app.generated.resources.following
import pixelix.app.generated.resources.follows_you
import pixelix.app.generated.resources.joined_date
import pixelix.app.generated.resources.lock
import pixelix.app.generated.resources.muted
import pixelix.app.generated.resources.ok
import pixelix.app.generated.resources.posts

@Composable
fun ProfileTopSection(
    account: Account?,
    relationship: Relationship?,
    navController: NavController,
    openUrl: (url: String) -> Unit
) {
    var isMuteInfoAlertOpen by remember { mutableStateOf(false) }

    if (account != null) {
        Column {
            if (account.headerUrl != null) {
                AsyncImage(
                    model = account.headerUrl,
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            } else {
                Spacer(Modifier.height(24.dp))
            }
            Column(Modifier.padding(12.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = account.avatar,
                        error = painterResource(Res.drawable.default_avatar),
                        contentDescription = "",
                        modifier = Modifier.height(76.dp).width(76.dp).clip(CircleShape)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = StringFormat.groupDigits(account.postsCount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = pluralStringResource(Res.plurals.posts, account.postsCount),
                                fontSize = 12.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    Destination.Followers(
                                        account.id, account.username, true
                                    )
                                )
                            }) {
                            Text(
                                text = StringFormat.groupDigits(account.followersCount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = pluralStringResource(
                                    Res.plurals.follower, account.followersCount
                                ), fontSize = 12.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    Destination.Followers(
                                        account.id, account.username, false
                                    )
                                )
                            }) {
                            Text(
                                text = StringFormat.groupDigits(account.followingCount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = pluralStringResource(
                                    Res.plurals.following, account.followingCount
                                ), fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.displayname ?: account.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (account.locked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = vectorResource(Res.drawable.lock),
                            contentDescription = null,
                            Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (account.isAdmin) {
                            ProfileBadge(text = stringResource(Res.string.admin))
                        }
                        if (account.isSupporterFlagEnabled) {
                            ProfileBadge(
                                text = "Supporter", color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (relationship != null && relationship.followedBy) {
                            ProfileBadge(text = stringResource(Res.string.follows_you))
                        }

                        if (relationship != null && (relationship.muted == true || relationship.mutedNotifications == true || relationship.mutedReblogs == true || relationship.mutedStatuses == true)) {
                            ProfileBadge(
                                text = stringResource(Res.string.muted),
                                color = MaterialTheme.colorScheme.error,
                                onClick = if (relationship.mutedReblogs != null || relationship.mutedStatuses != null || relationship.mutedNotifications != null) {
                                    {
                                        isMuteInfoAlertOpen = true
                                    }
                                } else {
                                    {}
                                })
                        }

                        if (relationship != null && relationship.blocked) {
                            ProfileBadge(
                                text = stringResource(Res.string.blocked),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }


                if (account.pronouns.isNotEmpty()) {
                    Text(
                        text = account.pronouns.joinToString(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (account.note.isNotBlank()) {
                    HashtagsMentionsTextView(
                        text = account.note,
                        textSize = 14.sp,
                        mentions = null,
                        navController = navController,
                        openUrl = { url -> openUrl(url) })
                }

                if (account.website.isNotBlank()) {
                    Row(
                        Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = account.website.substringAfter("https://"),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = { openUrl(account.website) })
                        )
                    }
                }

                if (account.fields.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        account.fields.forEach { field ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = field.key,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(100.dp)
                                )
                                if (field.isVerified) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.confirm),
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                                Text(
                                    text = DomainFormat.extractUrl(field.value) ?: field.value,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        openUrl(
                                            "https://" + (DomainFormat.extractUrl(
                                                field.value
                                            ) ?: field.value)
                                        )
                                    })
                            }
                        }
                    }
                }

                if (account.createdAt.isNotBlank()) {
                    val date: LocalDate = LocalDate.parse(account.createdAt.substringBefore("T"))
                    val formatter = LocalDate.Format {
                        monthName(MonthNames.ENGLISH_ABBREVIATED)
                        char(' ')
                        dayOfMonth()
                        chars(", ")
                        year()
                    }
                    Text(
                        text = stringResource(
                            Res.string.joined_date, formatter.format(date)
                        ), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp
                    )
                }
            }
        }
    }

    //TODO: improve design and add strings to strings.xml
    if (isMuteInfoAlertOpen && relationship != null) {
        AlertDialog(onDismissRequest = { isMuteInfoAlertOpen = false }, title = {
            Text(text = stringResource(Res.string.muted))
        }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "This account is muted for:")

                if (relationship.mutedStatuses != null && relationship.mutedStatuses) {
                    Text(text = "• Posts/Statuses")
                }
                if (relationship.mutedReblogs != null && relationship.mutedReblogs) {
                    Text(text = "• Reblogs/Shares")
                }
                if (relationship.mutedNotifications != null && relationship.mutedNotifications) {
                    Text(text = "• Notifications")
                }
            }
        }, confirmButton = {
            TextButton(onClick = {
                isMuteInfoAlertOpen = false
            }) {
                Text(stringResource(Res.string.ok))
            }
        })
    }
}

@Composable
private fun ProfileBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null
) {
    var baseModifier = Modifier.border(BorderStroke(1.dp, color), shape = RoundedCornerShape(8.dp))

    if (onClick != null) {
        baseModifier = baseModifier.clickable(onClick = onClick)
    }

    Box(
        modifier = baseModifier.padding(horizontal = 6.dp)
    ) {
        Text(text = text, fontSize = 9.sp, color = color)
    }
}