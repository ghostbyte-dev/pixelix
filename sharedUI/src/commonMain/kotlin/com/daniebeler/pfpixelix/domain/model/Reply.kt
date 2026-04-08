package com.daniebeler.pfpixelix.domain.model

import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Reply(
    @SerialName("id") val id: String,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("content") val contentHtml: String?,
    @SerialName("content_text") val contentText: String,
    @SerialName("mentions") val mentions: List<Account>,
    @SerialName("account") val account: Account,
    @SerialName("created_at") val createdAt: String,
    @SerialName("reply_count") val replyCount: Int,
    @SerialName("liked_by") val likedBy: LikedBy
) {
    @Transient val content = contentHtml ?: contentText
}