package com.daniebeler.pfpixelix.domain.model

data class PaginatedResponse<T>(
    val data: T,
    val next: String? = null
)