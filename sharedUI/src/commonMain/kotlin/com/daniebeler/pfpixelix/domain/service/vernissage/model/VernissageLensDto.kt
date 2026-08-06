package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Lens
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageLensDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Int
): DtoMappable<Lens> {
    override fun toDomain(): Lens {
        return Lens(
            id = this.id,
            name = this.name,
            amount = this.amount,
        )
    }
}