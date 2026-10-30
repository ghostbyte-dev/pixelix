package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.PostsForPlace
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedPostsForPlaceDto(
    @SerialName("place") val place: PixelfedPlaceDto,
    @SerialName("posts") val posts: List<PixelfedPostDto> = emptyList(),
)

fun PixelfedPostsForPlaceDto.toDomain(): PostsForPlace {
    return PostsForPlace(
        place = this.place.toDomain(),
        posts = this.posts.map { it.toDomain() }
    )
}