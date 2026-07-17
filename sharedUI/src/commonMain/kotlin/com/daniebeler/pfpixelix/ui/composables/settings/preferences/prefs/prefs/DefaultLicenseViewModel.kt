package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultLicenseViewModel(
    private val exploreService: ExploreService
) : ViewModel() {
    private val _licenses = MutableStateFlow<List<License>>(emptyList())

    val licenses: StateFlow<List<License>> = _licenses.asStateFlow()

    init {
        getLicenses()
    }


    private fun getLicenses() {
        exploreService.getLicenses().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _licenses.value = result.data
                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }
}