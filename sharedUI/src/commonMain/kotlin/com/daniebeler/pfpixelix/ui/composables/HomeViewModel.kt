package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class HomeViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val platform: Platform,
    session: Session
) : ViewModel() {

    val capabilities = session.capabilities
    val defaultTab = prefs.defaultHomeTab
    var hasRequestedPushPermission = prefs.hasRequestedPushPermission
    var isSwipeBetweenTabsEnabled by mutableStateOf(true)
    init {
        viewModelScope.launch {
            prefs.enableSwipeBetweenTabsFlow.collect { isSwipeBetweenTabsEnabled = it }
        }
    }

    fun openUrl (url: String) {
        platform.openUrl(url)
    }
}