package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.painterResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar

@Composable
fun FollowerElementComposable(
    account: Account, navController: NavController
) {
    Row(
        modifier = Modifier
            .clickable {
                navController.navigate(Destination.Profile(account.id))
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            , verticalAlignment = Alignment.CenterVertically
    ) {
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
            if (account.displayname != null) {
                Text(
                    text = account.displayname,
                    lineHeight = 8.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = account.username, fontSize = 12.sp)
                Text(
                    text = " • " + (account.url.substringAfter("https://").substringBefore("/") ?: ""),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))
    }
}