package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware

data class DomainSoftwareState(
    val isLoading: Boolean = false,
    val fediSoftware: FediseaSoftware? = null,
    val fediServer: FediseaInstance? = null,
    val error: String = ""
)
