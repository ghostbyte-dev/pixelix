package com.daniebeler.pfpixelix.domain.model

data class PaginatedResponse<T>(
    val data: List<T>,
    val next: String? = null
)