package com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource


//@Composable
//fun SettingDivider() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = painterResource(Res.drawable.ic_wavy_line),
//            contentDescription = null
//        )
//    }
//}

@Composable
fun SettingPref(
    leadingIcon: ImageBitmap,
    title: String,
    desc: String? = null,
    trailingContent: DrawableResource?,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            trailingContent?.let {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    Icon(
                        imageVector = vectorResource(trailingContent),
                        contentDescription = null,
                    )
                }
            }
        },
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: Painter,
    title: String,
    desc: String? = null,
    trailingContent: DrawableResource?,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            trailingContent?.let {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    Icon(
                        imageVector = vectorResource(trailingContent),
                        contentDescription = null,
                    )
                }
            }
        },
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: DrawableResource,
    title: String,
    desc: String? = null,
    trailingContent: DrawableResource?,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            trailingContent?.let {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    Icon(
                        imageVector = vectorResource(trailingContent),
                        contentDescription = null,
                    )
                }
            }
        },
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: ImageBitmap,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        leadingIcon = {
            Box(Modifier.padding(start = 14.dp)) {
                Image(
                    leadingIcon,
                    contentDescription = "",
                    Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .clip(
                            CircleShape
                        )
                )
            }
        },
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: Painter,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        leadingIcon = {
            Box(Modifier.padding(start = 14.dp)) {
                Image(
                    leadingIcon,
                    contentDescription = "",
                    Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .clip(
                            CircleShape
                        )
                )
            }
        },
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}



@Composable
fun SettingPref(
    leadingIcon: DrawableResource,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        leadingIcon = {
            Box(Modifier.padding(start = 14.dp)) {
                textColor?.let {
                    Icon(
                        imageVector = vectorResource(leadingIcon),
                        contentDescription = title,
                        tint = textColor
                    )
                } ?: Icon(
                    imageVector = vectorResource(leadingIcon),
                    contentDescription = title,
                )
            }
        },
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        textColor = textColor,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: (@Composable () -> Unit)? = null,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    textColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    )
    Card(
        shape = shape,
        colors = cardColors,
        modifier = Modifier.animateContentSize(),
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
                if (leadingIcon != null) {
                    leadingIcon()
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
                        color = textColor ?: Color.Unspecified
                    )
                    if (desc != null) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            color = textColor ?: Color.Unspecified
                        )
                    }
                }
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }

        content()
    }
}
