package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageMutedAccountDto(
    @SerialName("id") val id: String,
    @SerialName("mutedUser") val mutedUser: VernissageAccountDto,
    @SerialName("muteNotifications") val muteNotifications: Boolean,
    @SerialName("muteReblogs") val muteReblogs: Boolean,
    @SerialName("muteStatuses") val muteStatuses: Boolean,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
) : DtoMappable<MutedAccount> {
    override fun toDomain(): MutedAccount {
        return MutedAccount(
            id = id,
            account = mutedUser.toDomain(),
            muteOptions = UserMuteRequest(
                mute = false,
                muteNotifications = muteNotifications,
                muteReblogs = muteReblogs,
                muteStatuses = muteStatuses
            ),
        )
    }
}
