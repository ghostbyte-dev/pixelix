package com.daniebeler.pfpixelix.domain.model

data class Reply(
    val id: String,
    val contentHtml: String?,
    val contentText: String,
    val mentions: List<Account>,
    val account: Account,
    val createdAt: String,
    val replyCount: Int,
    val likedBy: LikedBy
) {
    // Native computed property, completely decoupling business logic from serialization
    val content: String = contentHtml ?: contentText
}