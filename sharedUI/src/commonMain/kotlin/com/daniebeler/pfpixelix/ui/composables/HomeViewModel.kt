package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class HomeViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {
    var isSwipeBetweenTabsEnabled by mutableStateOf(true)

    init {
        viewModelScope.launch {
            prefs.enableSwipeBetweenTabsFlow.collect { isSwipeBetweenTabsEnabled = it }
        }
    }
}