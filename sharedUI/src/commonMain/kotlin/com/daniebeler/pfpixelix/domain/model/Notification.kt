package com.daniebeler.pfpixelix.domain.model

data class Notification(
    val account: Account = Account(),
    override val id: String,
    val type: NotificationType,
    val post: Post?,
    val createdAt: String
): Identifiable

enum class NotificationType {
    MENTION,
    STATUS,
    REBLOG,
    FOLLOW,
    FOLLOW_REQUEST,
    FAVOURITE,
    UPDATE,
    NEW_COMMENT,
    DIRECT_MESSAGE,
    UNDEFINED
}