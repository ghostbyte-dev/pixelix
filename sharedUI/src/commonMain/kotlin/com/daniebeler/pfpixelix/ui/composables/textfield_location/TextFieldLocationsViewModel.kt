package com.daniebeler.pfpixelix.ui.composables.textfield_location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TextFieldLocationsViewModel @Inject constructor(
    private val exploreService: ExploreService
) : ViewModel() {
    var text by mutableStateOf(TextFieldValue(""))
    var locationsDropdownOpen by mutableStateOf(false)
    var locationsSuggestions by mutableStateOf(LocationsState())

    fun initializePlace(initialPlace: Location) {
        locationsSuggestions = LocationsState(location = initialPlace)
        text = TextFieldValue(initialPlace.name!!)
    }

    fun changeText(newText: TextFieldValue) {
        text = newText

        locationsDropdownOpen = true
        searchLocations(text.text)
    }

    private fun searchLocations(location: String?) {
        if (location == null) {
            return
        }
        exploreService.searchLocations(location).onEach { result ->
            locationsSuggestions = when (result) {
                is Resource.Success -> {
                    LocationsState(locations = result.data)
                }

                is Resource.Error -> {
                    LocationsState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    LocationsState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clickLocation(location: Location) {
        locationsDropdownOpen = false
        locationsSuggestions = locationsSuggestions.copy(location = location)
    }

    fun removeLocation() {
        text = TextFieldValue()
        locationsSuggestions = LocationsState()
    }

    fun edit() {
        locationsSuggestions = locationsSuggestions.copy(location = null)
    }
}