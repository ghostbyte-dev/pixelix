package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ExpandOptionsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ValueOption
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.imageVectorIconBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.radioButtonBlock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_timeline_tab
import pixelix.app.generated.resources.global
import pixelix.app.generated.resources.globe
import pixelix.app.generated.resources.home
import pixelix.app.generated.resources.house
import pixelix.app.generated.resources.local
import pixelix.app.generated.resources.share

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultHomeTab() {
    val pref = LocalAppComponent.current.preferences
    val defaultHomeTab by pref.defaultHomeTabFlow.collectAsState(pref.defaultHomeTab)

    val onOptionClick = { mode: Int ->
        pref.defaultHomeTab = mode
    }

    val visibilityDesc = when (defaultHomeTab) {
        0 -> stringResource(Res.string.home)
        1 -> stringResource(Res.string.local)
        2 -> stringResource(Res.string.global)
        else -> ""
    }

    val openCount = 8
    ExpandOptionsPref(
        leadingIcon = Res.drawable.globe,
        title = stringResource(Res.string.default_timeline_tab),
        desc = visibilityDesc,
        index = 2,
        count = 5
    ) {
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 3, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.house),
                contentDescription = stringResource(Res.string.home)
            ),
            title = stringResource(Res.string.home),
            trailingContent = radioButtonBlock(defaultHomeTab == 0),
            value = 0,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 4, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.share),
                contentDescription = stringResource(Res.string.local)
            ),
            title = stringResource(Res.string.local),
            trailingContent = radioButtonBlock(defaultHomeTab == 1),
            value = 1,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 5, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.globe),
                contentDescription = stringResource(Res.string.global)
            ),
            title = stringResource(Res.string.global),
            trailingContent = radioButtonBlock(defaultHomeTab == 2),
            value = 2,
            onOptionClick = onOptionClick,
        )
    }
}