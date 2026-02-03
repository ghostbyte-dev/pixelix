package com.daniebeler.pfpixelix.widget.notifications.models

import coil3.Bitmap
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
    val accountAvatarUri: String?,
    @Serializable(with = BitmapSerializer::class) // <--- Add this
    val accountAvatarBitmap: Bitmap?,
    val accountId: String,
    val accountUsername: String,
    val timeAgo: String,
    val type: String
)