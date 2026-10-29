package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class HomeViewModel(
    private val prefs: UserPreferences,
    private val platform: Platform,
    session: Session
) : ViewModel() {

    val capabilities = session.capabilities
    val defaultTab = prefs.defaultHomeTab
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