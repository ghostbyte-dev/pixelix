package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.PushNotification
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VernissagePushPayloadDto(
    @SerialName("notification") val notification: VernissagePushNotificationDto
) : DtoMappable<PushNotification> {
    override fun toDomain(): PushNotification {
        return PushNotification(
            title = this.notification.title,
            body = this.notification.body,
            icon = this.notification.icon
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class VernissagePushNotificationDto(

    @SerialName("title") val title: String,
    @SerialName("body") val body: String,
    @SerialName("icon") val icon: String
)