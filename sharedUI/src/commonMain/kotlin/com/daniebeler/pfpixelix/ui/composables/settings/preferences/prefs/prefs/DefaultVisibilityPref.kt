package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ExpandOptionsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.ValueOption
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.imageVectorIconBlock
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.radioButtonBlock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.audience_public
import pixelix.app.generated.resources.default_visibility
import pixelix.app.generated.resources.eye
import pixelix.app.generated.resources.eye_off
import pixelix.app.generated.resources.followers_only
import pixelix.app.generated.resources.globe
import pixelix.app.generated.resources.lock
import pixelix.app.generated.resources.mentioned_only
import pixelix.app.generated.resources.send
import pixelix.app.generated.resources.unlisted

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultVisibilityPref(capabilities: Capabilities) {
    val pref = LocalAppComponent.current.preferences
    val visibility by pref.defaultVisibilityFlow.collectAsState(pref.defaultVisibility)

    val onOptionClick = { mode: Visibility ->
        pref.defaultVisibility = mode
    }
    val openCount = if (capabilities.newPost.includeDirectVisibility) {4} else {3}
    ExpandOptionsPref(
        leadingIcon = Res.drawable.eye,
        title = stringResource(Res.string.default_visibility),
        index = 1,
        count = 2
    ) {
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.globe),
                contentDescription = stringResource(Res.string.audience_public)
            ),
            title = stringResource(Res.string.audience_public),
            trailingContent = radioButtonBlock(visibility == Visibility.PUBLIC),
            value = Visibility.PUBLIC,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 1, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.eye_off),
                contentDescription = stringResource(Res.string.unlisted)
            ),
            title = stringResource(Res.string.unlisted),
            trailingContent = radioButtonBlock(visibility == Visibility.UNLISTED),
            value = Visibility.UNLISTED,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = openCount),
            leadingIcon = imageVectorIconBlock(
                imageVector = vectorResource(Res.drawable.lock),
                contentDescription = stringResource(Res.string.followers_only)
            ),
            title = stringResource(Res.string.followers_only),
            trailingContent = radioButtonBlock(visibility == Visibility.PRIVATE),
            value = Visibility.PRIVATE,
            onOptionClick = onOptionClick,
        )
        if (capabilities.newPost.includeDirectVisibility) {
            ValueOption(
                shapes = ListItemDefaults.segmentedShapes(index = 3, count = openCount),
                leadingIcon = imageVectorIconBlock(
                    imageVector = vectorResource(Res.drawable.send),
                    contentDescription = stringResource(Res.string.mentioned_only)
                ),
                title = stringResource(Res.string.mentioned_only),
                trailingContent = radioButtonBlock(visibility == Visibility.DIRECT),
                value = Visibility.DIRECT,
                onOptionClick = onOptionClick,
            )
        }
    }
}