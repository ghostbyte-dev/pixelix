package com.daniebeler.pfpixelix.widget.notifications.models

import coil3.Bitmap
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.widget.BitmapSerializer
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsStore(
    val notifications: List<NotificationStoreItem> = emptyList(),
    val refreshing: Boolean = false,
    val error: String = ""
)

@Serializable
data class NotificationStoreItem(
    val id: String,
    val accountAvatarUrl: String,
    @Serializable(with = BitmapSerializer::class) // <--- Add this
    val accountAvatarBitmap: Bitmap?,
    val accountId: String,
    val accountUsername: String,
    val timeAgo: String,
    //TODO: check if it works, if serializer is needed
    val type: NotificationType
)