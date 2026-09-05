package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.utils.KmpContext

@Composable
actual fun PushDistributorPrefDialog(
    distributor: String,
    setDistributor: (distributor: String) -> Unit,
    onDismiss: () -> Unit,
    context: KmpContext
) {
}