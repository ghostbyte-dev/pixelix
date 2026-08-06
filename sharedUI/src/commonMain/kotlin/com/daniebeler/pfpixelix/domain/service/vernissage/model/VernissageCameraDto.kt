package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Camera
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.serializers.TagNameSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageCameraDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("make") val make: String?,
    @SerialName("model") val model: String?,
    @SerialName("amount") val amount: Int
): DtoMappable<Camera> {
    override fun toDomain(): Camera {
        return Camera(
            id = this.id,
            name = this.name,
            make = this.make,
            model = this.model,
            amount = this.amount,
        )
    }
}