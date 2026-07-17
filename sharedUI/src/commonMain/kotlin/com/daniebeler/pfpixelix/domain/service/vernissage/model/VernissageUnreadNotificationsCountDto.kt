package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageUnreadNotificationsCountDto(
    @SerialName("amount") val count: Int
): DtoMappable<Int> {
    override fun toDomain(): Int {
        return this.count
    }
}
