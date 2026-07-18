package com.daniebeler.pfpixelix.domain.service.vernissage.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageNewReplyDto(
    @SerialName("replyToStatusId")
    val replyToStatusId: String,

    @SerialName("note")
    val note: String,

    @SerialName("visibility")
    val visibility: String = "public",

    @SerialName("sensitive")
    val sensitive: Boolean = false,

    @SerialName("commentsDisabled")
    val commentsDisabled: Boolean = false,

    @SerialName("attachmentIds")
    val attachmentIds: List<String> = emptyList()
)