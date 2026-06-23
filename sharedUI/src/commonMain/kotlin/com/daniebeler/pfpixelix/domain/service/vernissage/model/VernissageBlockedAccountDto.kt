package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageBlockedAccountDto(
    @SerialName("id") val id: String? = null,
    @SerialName("blockedUser") val blockedUser: VernissageAccountDto,
    @SerialName("reason") val reason: String,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
): DtoMappable<Account> {
    override fun toDomain(): Account {
        return blockedUser.toDomain()
    }
}
