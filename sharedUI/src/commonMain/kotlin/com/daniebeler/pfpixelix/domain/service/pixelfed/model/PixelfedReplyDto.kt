package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Reply
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedReplyDto(
    @SerialName("id") val id: String,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("content") val contentHtml: String?,
    @SerialName("content_text") val contentText: String = "",
    @SerialName("mentions") val mentions: List<PixelfedAccountDto> = emptyList(),
    @SerialName("account") val account: PixelfedAccountDto,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("reply_count") val replyCount: Int = 0,
    @SerialName("liked_by") val likedBy: PixelfedLikedByDto
)

fun PixelfedReplyDto.toDomain(): Reply {
    return Reply(
        id = this.id,
        contentHtml = this.contentHtml,
        contentText = this.contentText,
        mentions = this.mentions.map { it.toDomain() },
        account = this.account.toDomain(),
        createdAt = this.createdAt,
        replyCount = this.replyCount,
        likedBy = this.likedBy.toDomain()
    )
}