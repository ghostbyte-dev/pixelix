package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation (
    @SerialName("id") val id: Int,
    @SerialName("unread") val unread: Boolean,
    @SerialName("accounts") val accounts: List<Account>,
    @SerialName("last_status") val lastPost: Post
)