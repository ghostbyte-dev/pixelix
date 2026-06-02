package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.grid_filled
import pixelix.app.generated.resources.grid
import pixelix.app.generated.resources.list_filled
import pixelix.app.generated.resources.list
import pixelix.app.generated.resources.masonry
import pixelix.app.generated.resources.masonry_filled
import pixelix.app.generated.resources.posts

@Composable
fun SwitchViewComposable(
    postsCount: Int?, viewType: ViewEnum, onViewChange: (type: ViewEnum) -> Unit
) {

    Column(
        Modifier.fillMaxWidth().padding(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            postsCount?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = postsCount.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                    Text(text = " " + stringResource(Res.string.posts), fontSize = 12.sp)
                }
            } ?: Box {}


            Row {
                Box(modifier = Modifier.padding(4.dp).clickable { onViewChange(ViewEnum.Masonry) }
                    .alpha(
                        if (viewType == ViewEnum.Masonry) {
                            1f
                        } else {
                            0.5f
                        }
                    )) {
                    Icon(
                        imageVector = if (viewType == ViewEnum.Masonry) {
                            vectorResource(Res.drawable.masonry_filled)
                        } else {
                            vectorResource(Res.drawable.masonry)
                        }, modifier = Modifier.size(24.dp), contentDescription = "masonry view"
                    )
                }

                Box(modifier = Modifier.padding(4.dp).clickable { onViewChange(ViewEnum.Grid) }
                    .alpha(
                        if (viewType == ViewEnum.Grid) {
                            1f
                        } else {
                            0.5f
                        }
                    )) {
                    Icon(
                        imageVector = if (viewType == ViewEnum.Grid) {
                            vectorResource(Res.drawable.grid_filled)
                        } else {
                            vectorResource(Res.drawable.grid)
                        }, modifier = Modifier.size(24.dp), contentDescription = "grid view"
                    )
                }
                Box(modifier = Modifier.padding(4.dp).clickable { onViewChange(ViewEnum.Timeline) }
                    .alpha(
                        if (viewType == ViewEnum.Timeline) {
                            1f
                        } else {
                            0.5f
                        }
                    )) {
                    Icon(
                        imageVector = if (viewType == ViewEnum.Timeline) {
                            vectorResource(Res.drawable.list_filled)
                        } else {
                            vectorResource(Res.drawable.list)
                        }, contentDescription = "timeline view"
                    )
                }
            }
        }
    }
}