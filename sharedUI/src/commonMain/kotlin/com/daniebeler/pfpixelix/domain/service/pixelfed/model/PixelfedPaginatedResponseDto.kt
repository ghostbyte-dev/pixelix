package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedPaginatedResponseDto<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("next") val next: String? = null
)


inline fun <T, R> PixelfedPaginatedResponseDto<T>.toDomain(transform: (T) -> R): PaginatedResponse<R> {
    return PaginatedResponse(
        data = this.data.map(transform),
        next = this.next
    )
}


inline fun <T, R> PaginatedResponse<T>.toDto(transform: (T) -> R): PixelfedPaginatedResponseDto<R> {
    return PixelfedPaginatedResponseDto(
        data = this.data.map(transform),
        next = this.next
    )
}