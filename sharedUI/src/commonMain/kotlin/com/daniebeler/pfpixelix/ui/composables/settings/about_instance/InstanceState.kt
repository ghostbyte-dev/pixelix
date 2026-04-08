package com.daniebeler.pfpixelix.ui.composables.settings.about_instance

import com.daniebeler.pfpixelix.domain.model.Instance

data class InstanceState(
    val isLoading: Boolean = false,
    val instance: Instance? = null,
    val error: String = ""
)
