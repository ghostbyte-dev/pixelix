package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("id") val id: String,
    @SerialName("isAuthor") val isAuthor: Boolean,
    @SerialName("reportId") val reportId: String,
    @SerialName("seen") val seen: Boolean,
    @SerialName("text") val text: String = "",
    @SerialName("timeAgo") val timeAgo: String,
    @SerialName("type") val type: String
)