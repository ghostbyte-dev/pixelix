package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.ui.navigation.Destination

@Composable
fun CustomHashtag(hashtag: Tag, navController: NavController) {
    CustomHashtagPrivate(hashtag = hashtag, onClick = {}, navController = navController)
}

@Composable
fun CustomHashtag(hashtag: Tag, onClick: () -> Unit, navController: NavController) {
    CustomHashtagPrivate(hashtag = hashtag, onClick = onClick, navController = navController)
}

@Composable
private fun CustomHashtagPrivate(hashtag: Tag, onClick: () -> Unit, navController: NavController) {
    Row(
        Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
                navController.navigate(Destination.HashtagTimeline(hashtag.name))
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(46.dp)
                .width(46.dp)
                .background(MaterialTheme.colorScheme.surfaceBright, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Tag,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(text = "#" + hashtag.name)
            Text(
                text = hashtag.count.toString() + " posts",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}