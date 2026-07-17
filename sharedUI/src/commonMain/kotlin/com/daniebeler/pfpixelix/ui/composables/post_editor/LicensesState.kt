package com.daniebeler.pfpixelix.ui.composables.post_editor

import com.daniebeler.pfpixelix.domain.model.License

data class LicensesState(
    var isLoading: Boolean = false,
    var error: String = "",
    var selectedLicense: License? = null,
    var licenses: List<License> = emptyList()
)
