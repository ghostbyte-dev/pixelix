package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageCategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
): DtoMappable<Category> {
    override fun toDomain(): Category {
        return Category(
            id = this.id,
            name = this.name,
            isEnabled = null,
            priority = null
        )
    }

}
