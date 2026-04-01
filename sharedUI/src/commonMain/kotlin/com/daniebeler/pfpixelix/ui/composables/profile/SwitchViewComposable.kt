package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import pixelix.app.generated.resources.grid
import pixelix.app.generated.resources.grid_outline
import pixelix.app.generated.resources.posts
import pixelix.app.generated.resources.reorder_four
import pixelix.app.generated.resources.reorder_four_outline

@Composable
fun SwitchViewComposable(
    postsCount: Int?, viewType: ViewEnum, onViewChange: (type: ViewEnum) -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            postsCount?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = postsCount.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(text = " " + stringResource(Res.string.posts), fontSize = 12.sp)
                }
            } ?: Box {}


            Row {
                Box(modifier = Modifier
                    .padding(4.dp)
                    .clickable { onViewChange(ViewEnum.Grid) }
                    .alpha(
                        if (viewType == ViewEnum.Timeline) {
                            0.5f
                        } else {
                            1f
                        }
                    )) {
                    Icon(
                        imageVector = if (viewType == ViewEnum.Grid) {
                            vectorResource(Res.drawable.grid)
                        } else {
                            vectorResource(Res.drawable.grid_outline)
                        },
                        modifier = Modifier.size(24.dp),
                        contentDescription = "grid view"
                    )
                }
                Box(modifier = Modifier
                    .padding(4.dp)
                    .clickable { onViewChange(ViewEnum.Timeline) }
                    .alpha(
                        if (viewType == ViewEnum.Grid) {
                            0.5f
                        } else {
                            1f
                        }
                    )) {
                    Icon(
                        imageVector = if (viewType == ViewEnum.Grid) {
                            vectorResource(Res.drawable.reorder_four_outline)
                        } else {
                            vectorResource(Res.drawable.reorder_four)
                        },
                        contentDescription = "timeline view"
                    )
                }
            }
        }
    }
}