package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageCategory(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("isEnabled") val isEnabled: Boolean,
    @SerialName("priority") val priority: Int
): DtoMappable<Category> {
    override fun toDomain(): Category {
        return Category(
           id = this.id,
            name = this.name,
            isEnabled = this.isEnabled,
            priority = this.priority
        )
    }
}
