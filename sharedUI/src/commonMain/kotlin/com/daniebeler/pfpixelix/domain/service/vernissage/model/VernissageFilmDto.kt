package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Film
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageFilmDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Int
): DtoMappable<Film> {
    override fun toDomain(): Film {
        return Film(
            id = this.id,
            name = this.name,
            amount = this.amount,
        )
    }
}