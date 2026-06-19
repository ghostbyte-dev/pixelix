package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.serializers.TagNameSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageTagDto(
    @Serializable(with = TagNameSerializer::class)
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("amount") val amount: Int?
): DtoMappable<Tag> {
    override fun toDomain(): Tag {
        return Tag(
            name = this.name,
            url = this.url,
            following = false,
            postsCount = this.amount,
            hashtag = "",
            id = ""
        )
    }
}