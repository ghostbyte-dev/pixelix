package com.daniebeler.pfpixelix.ui.composables.timelines

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.help

@Composable
fun TimelineHelpCard(title: String, description: String, onDiscard: () -> Unit) {
    val shape: Shape = MaterialTheme.shapes.medium
    val textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )

    Card(
        shape = shape,
        colors = cardColors,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.padding(start = 16.dp)) {
                Icon(
                    imageVector = vectorResource(Res.drawable.help),
                    contentDescription = "help",
                    Modifier.size(32.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = textColor)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = textColor)

            }
            IconButton(
                onClick = onDiscard,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.close),
                    contentDescription = "Close card",
                    modifier = Modifier.size(20.dp),
                    tint = textColor
                )
            }
        }
    }
}