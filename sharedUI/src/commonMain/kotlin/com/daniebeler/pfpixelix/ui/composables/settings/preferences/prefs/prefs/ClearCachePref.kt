package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.basic.SettingPref
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.clear_cache
import pixelix.app.generated.resources.save_outline

@Composable
fun ClearCachePref(drawerState: DrawerState) {
    val viewModel = injectViewModel("ClearCacheViewModel") { clearCacheViewModel }
    val cacheSize = viewModel.cacheSize.collectAsStateWithLifecycle("")

    LaunchedEffect(drawerState.isOpen) {
        viewModel.refresh()
    }

    SettingPref(
        leadingIcon = Res.drawable.save_outline,
        title = stringResource(Res.string.clear_cache),
        desc = cacheSize.value,
        trailingContent = null,
        onClick = { viewModel.cleanCache() }
    )
}
