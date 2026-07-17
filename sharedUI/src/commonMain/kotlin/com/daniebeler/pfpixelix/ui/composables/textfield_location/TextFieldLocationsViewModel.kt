package com.daniebeler.pfpixelix.ui.composables.textfield_location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TextFieldLocationsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    private val session: Session
) : ViewModel() {
    val capabilities = session.capabilities.value
    var locationText by mutableStateOf("")
    var countryText by mutableStateOf("")
    var locationsDropdownOpen by mutableStateOf(false)
    var locationsSuggestions by mutableStateOf(LocationsState())
    var countriesState by mutableStateOf(CountriesState())


    init {
        if (capabilities.newPost.showCountryDropdown) {
            loadCountries()
        }
    }

    fun initializePlace(initialPlace: Location) {
        countriesState = countriesState.copy(country = initialPlace.country)
        locationsSuggestions = LocationsState(location = initialPlace)
        locationText = initialPlace.name!!
    }

    fun changeLocationText(newText: String) {
        locationText = newText

        locationsDropdownOpen = true
        searchLocations(locationText)
    }

    fun changeCountryText(newText: String) {
        countryText = newText
        countriesState = countriesState.copy(
            filteredCountries = countriesState.countries.filter { country ->
                country.name.startsWith(newText, ignoreCase = true)
            },
            country = null
        )
    }

    private fun searchLocations(location: String?) {
        if (location == null) {
            return
        }
        exploreService.searchLocations(location, countriesState.country?.code).onEach { result ->
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

    fun selectCountry(country: Country) {
        countriesState = countriesState.copy(
            country = country
        )
    }

    fun removeLocation() {
        locationText = ""
        locationsSuggestions = LocationsState()
    }

    fun edit() {
        locationsSuggestions = locationsSuggestions.copy(location = null)
    }


    private fun loadCountries() {
        exploreService.getAllCountries().onEach { result ->
            countriesState = when (result) {
                is Resource.Success -> {
                    countriesState.copy(countries = result.data)
                }

                is Resource.Error -> {
                    countriesState.copy(error = result.message)
                }

                is Resource.Loading -> {
                    countriesState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}