package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingPref(
    title: String,
    shapes: ListItemShapes,
    modifier: Modifier = Modifier,
    desc: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    SegmentedListItem(
        onClick = onClick,
        shapes = shapes,
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        content = {
            Text(
                text = title, fontWeight = FontWeight.Medium
            )
        },
        supportingContent = desc?.let {
            {
                Text(
                    text = it, maxLines = 5, overflow = TextOverflow.Ellipsis
                )
            }
        })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingPref(
    title: String,
    shapes: ListItemShapes,
    icon: DrawableResource, // Accept the resource directly
    modifier: Modifier = Modifier,
    desc: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    // Call the base component and wrap the icon for the caller
    SettingPref(
        title = title,
        shapes = shapes,
        modifier = modifier,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        leadingContent = {
            Icon(
                imageVector = vectorResource(icon), contentDescription = null
            )
        })
}