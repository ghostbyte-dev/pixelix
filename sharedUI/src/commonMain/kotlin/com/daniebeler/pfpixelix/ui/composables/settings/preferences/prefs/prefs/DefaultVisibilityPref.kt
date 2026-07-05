package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ExpandOptionsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ValueOption
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.imageVectorIconBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.radioButtonBlock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.amoled_theme
import pixelix.app.generated.resources.audience_public
import pixelix.app.generated.resources.dark_theme
import pixelix.app.generated.resources.default_visibility
import pixelix.app.generated.resources.device_theme
import pixelix.app.generated.resources.eye
import pixelix.app.generated.resources.followers_only
import pixelix.app.generated.resources.light_theme
import pixelix.app.generated.resources.unlisted

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultVisibilityPref() {
    val pref = LocalAppComponent.current.preferences
    val visibility by pref.defaultVisibilityFlow.collectAsState(pref.defaultVisibility)

    val onOptionClick = { mode: Visibility ->
        pref.defaultVisibility = mode
    }

    ExpandOptionsPref(
        leadingIcon = Res.drawable.eye,
        title = stringResource(Res.string.default_visibility),
        index = 1,
        count = 2
    ) {
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.device_theme),
                contentDescription = stringResource(Res.string.audience_public)
            ),
            title = stringResource(Res.string.audience_public),
            trailingContent = radioButtonBlock(visibility == Visibility.PUBLIC),
            value = Visibility.PUBLIC,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.light_theme),
                contentDescription = stringResource(Res.string.unlisted)
            ),
            title = stringResource(Res.string.unlisted),
            trailingContent = radioButtonBlock(visibility == Visibility.UNLISTED),
            value = Visibility.UNLISTED,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.dark_theme),
                contentDescription = stringResource(Res.string.followers_only)
            ),
            title = stringResource(Res.string.followers_only),
            trailingContent = radioButtonBlock(visibility == Visibility.PRIVATE),
            value = Visibility.PRIVATE,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.amoled_theme),
                contentDescription = "Mentioned only"
            ),
            title = "Mentioned only",
            trailingContent = radioButtonBlock(visibility == Visibility.DIRECT),
            value = Visibility.DIRECT,
            onOptionClick = onOptionClick,
        )
    }
}