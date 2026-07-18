package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.Serializable

@Serializable
data class JwtClaims(
    val sub: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val name: String? = null
)