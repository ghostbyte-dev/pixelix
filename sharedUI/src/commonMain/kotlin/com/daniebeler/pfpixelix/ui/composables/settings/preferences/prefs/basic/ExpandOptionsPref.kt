package com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_down
import pixelix.app.generated.resources.chevron_right

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandOptionsPref(
    leadingIcon: DrawableResource,
    title: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    desc: String? = null,
    initializeExpanded: Boolean = false,
    options: @Composable () -> Unit // Passes the final count to children
) {
    var expanded by rememberSaveable { mutableStateOf(initializeExpanded) }
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        SettingPref(
            icon = leadingIcon,
            title = title,
            desc = desc,
            shapes = ListItemDefaults.segmentedShapes(index = index, count = if (expanded) {count + 1} else {count}),
            trailingContent = {
                Icon(
                    imageVector = vectorResource(Res.drawable.chevron_down),
                    contentDescription = null,
                    modifier = Modifier.rotate(rotate)
                )
            },
            onClick = {
                expanded = !expanded
            },
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
            exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            ) {
                options()
            }
        }
    }
}

@Composable
fun imageVectorIconBlock(
    imageVector: ImageVector, contentDescription: String? = null
): @Composable () -> Unit = {
    Icon(imageVector = imageVector, contentDescription = contentDescription)
}

@Composable
fun radioButtonBlock(selected: Boolean): @Composable () -> Unit = {
    RadioButton(selected = selected, onClick = null)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Any> ValueOption(
    title: String,
    shapes: ListItemShapes,
    value: T,
    onOptionClick: (value: T) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Option(
        title = title,
        shapes = shapes,
        leadingIcon = leadingIcon,
        desc = desc,
        trailingContent = trailingContent,
        onClick = { onOptionClick(value) })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Option(
    title: String,
    shapes: ListItemShapes,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = {
        Icon(imageVector = vectorResource(Res.drawable.chevron_right), contentDescription = title)
    },
    onClick: () -> Unit = {},
) {
    SegmentedListItem(
        onClick = onClick,
        shapes = shapes,
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier,
        leadingContent = leadingIcon,
        trailingContent = trailingContent,
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
        },
        supportingContent = desc?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        })
}