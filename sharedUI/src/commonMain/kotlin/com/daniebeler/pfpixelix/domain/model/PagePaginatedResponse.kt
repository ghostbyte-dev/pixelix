package com.daniebeler.pfpixelix.domain.model

data class PagePaginatedResponse<T>(
    val data: List<T>,
    val currentPage: Int,
    val size: Int,
    val total: Int
) {
    val hasNextPage: Boolean
        get() = currentPage * size < total && data.isNotEmpty()

    val nextPage: Int?
        get() = if (hasNextPage) currentPage + 1 else null

    val isEndReached: Boolean
        get() = !hasNextPage
}