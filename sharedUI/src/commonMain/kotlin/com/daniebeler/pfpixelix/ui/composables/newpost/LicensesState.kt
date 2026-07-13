package com.daniebeler.pfpixelix.ui.composables.newpost

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.model.License

data class LicensesState(
    var isLoading: Boolean = false,
    var error: String = "",
    var selectedLicense: License? = null,
    var licenses: List<License> = emptyList()
)
