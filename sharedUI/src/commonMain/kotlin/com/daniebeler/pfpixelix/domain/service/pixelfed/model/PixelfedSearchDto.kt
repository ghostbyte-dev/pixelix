package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Search
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedSearchDto(
    @SerialName("accounts") val accounts: List<PixelfedAccountDto> = emptyList(),
    @SerialName("statuses") val posts: List<PixelfedPostDto> = emptyList(),
    @SerialName("hashtags") val tags: List<PixelfedTagDto> = emptyList()
)

fun PixelfedSearchDto.toDomain(): Search {
    return Search(
        accounts = this.accounts.map { it.toDomain() },
        posts = this.posts.map { it.toDomain() },
        tags = this.tags.map { it.toDomain() }
    )
}