package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun CardButton(
    leadingIcon: DrawableResource,
    title: String,
    desc: String? = null,
    trailingContent: DrawableResource,
    onClick: () -> Unit = {},
) {
    val shape: Shape = MaterialTheme.shapes.medium
    val textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )

    Card(
        shape = shape,
        colors = cardColors,
    ) {
        Card(
            onClick = onClick,
            shape = shape,
            colors = cardColors,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.defaultMinSize(minHeight = 54.dp)
            ) {
                Box(Modifier.padding(start = 16.dp)) {
                    Icon(
                        imageVector = vectorResource(leadingIcon),
                        contentDescription = title,
                        tint = textColor
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.Unspecified
                    )
                    if (desc != null) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Unspecified
                        )
                    }
                }
                Box(Modifier.padding(start = 14.dp, end = 10.dp)) {
                    Icon(
                        imageVector = vectorResource(trailingContent),
                        contentDescription = "open",
                        tint = textColor
                    )
                }
            }
        }
    }
}