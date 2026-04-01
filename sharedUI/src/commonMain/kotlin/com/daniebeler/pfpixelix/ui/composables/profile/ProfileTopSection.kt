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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.HashtagsMentionsTextView
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.admin
import pixelix.app.generated.resources.blocked
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.follower
import pixelix.app.generated.resources.following
import pixelix.app.generated.resources.follows_you
import pixelix.app.generated.resources.joined_date
import pixelix.app.generated.resources.muted
import pixelix.app.generated.resources.posts

@Composable
fun ProfileTopSection(
    account: Account?,
    relationship: Relationship?,
    navController: NavController,
    openUrl: (url: String) -> Unit
) {
    if (account != null) {
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
                            navController.navigate(Destination.Followers(account.id, true))
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
                            navController.navigate(Destination.Followers(account.id, false))
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
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (account.isAdmin) {
                        ProfileBadge(text = stringResource(Res.string.admin))
                    }
                    if (relationship != null && relationship.followedBy) {
                        ProfileBadge(text = stringResource(Res.string.follows_you))
                    }

                    if (relationship != null && relationship.muting) {
                        ProfileBadge(
                            text = stringResource(Res.string.muted),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (relationship != null && relationship.blocking) {
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
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = account.website.substringAfter("https://"),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = { openUrl(account.website) })
                    )
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

@Composable
private fun ProfileBadge(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Box(
        Modifier.border(
                BorderStroke(1.dp, color), shape = RoundedCornerShape(8.dp)
            ).padding(horizontal = 6.dp)
    ) {
        Text(text = text, fontSize = 9.sp, color = color)
    }
}