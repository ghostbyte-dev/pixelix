package com.daniebeler.pfpixelix.ui.composables.textfield_location

import com.daniebeler.pfpixelix.domain.model.Place

data class LocationsState(
    val isLoading: Boolean = false,
    val locations: List<Place> = emptyList(),
    val location: Place? = null,
    val error: String = ""
)
