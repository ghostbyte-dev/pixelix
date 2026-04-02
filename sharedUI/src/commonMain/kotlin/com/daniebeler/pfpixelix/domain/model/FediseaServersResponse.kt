package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FediseaServersResponse(
    val data: List<Server>
)
