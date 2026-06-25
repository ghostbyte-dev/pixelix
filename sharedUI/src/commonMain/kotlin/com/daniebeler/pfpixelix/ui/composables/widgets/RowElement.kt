package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ButtonRowElement(
    text: String,
    onClick: () -> Unit,
    smallText: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground,
    icon: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(text = text, color = color)
            if (smallText.isNotBlank()) {
                Text(text = smallText, fontSize = 12.sp, lineHeight = 6.sp, color = color)
            }
        }
    }
}

@Composable
fun ButtonRowElement(
    icon: DrawableResource,
    text: String,
    onClick: () -> Unit,
    smallText: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    ButtonRowElement(text = text, onClick = onClick, smallText = smallText, color = color) {
        Icon(
            imageVector = vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 12.dp),
            tint = color
        )
    }
}

@Composable
fun ButtonRowElement(
    image: ImageBitmap,
    roundedImage: Boolean = true,
    text: String,
    onClick: () -> Unit,
    smallText: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    ButtonRowElement(text = text, onClick = onClick, smallText = smallText, color = color) {
        Image(
            image,
            contentDescription = null,
            modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 12.dp).height(24.dp)
                .width(24.dp).then(if (roundedImage) Modifier.clip(CircleShape) else Modifier)
        )
    }
}