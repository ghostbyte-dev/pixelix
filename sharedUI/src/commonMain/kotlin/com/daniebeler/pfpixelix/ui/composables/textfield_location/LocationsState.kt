package com.daniebeler.pfpixelix.ui.composables.textfield_location

import com.daniebeler.pfpixelix.domain.model.Location

data class LocationsState(
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList(),
    val location: Location? = null,
    val error: String = ""
)
