package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PixelfedPostDto @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("id") val id: String = "",
    @SerialName("account") val account: PixelfedAccountDto,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("content") val content: String?,
    @SerialName("content_text") val contentText: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("favourited") val favourited: Boolean = false,
    @SerialName("favourites_count") val favouritesCount: Int = 0,
    @SerialName("in_reply_to_id") val inReplyToId: String?,
    @SerialName("liked_by") val likedBy: PixelfedLikedByDto?,
    @SerialName("media_attachments") val mediaAttachments: List<PixelfedMediaAttachmentDto> = emptyList(),
    @SerialName("mentions") val mentions: List<PixelfedAccountDto> = emptyList(),
    @SerialName("place") val place: PixelfedPlaceDto?,
    @SerialName("reblog") val reblog: PixelfedPostDto?,
    @SerialName("reblogged") val reblogged: Boolean = false,
    @SerialName("reblogs_count") val reblogCount: Int = 0,

    // @JsonNames allows fallback matching during parsing.
    // This completely removes the need for PostDtoTransformingSerializer!
    @JsonNames("replies_count", "reply_count") val replyCount: Int = 0,

    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("spoiler_text") val spoilerText: String = "",
    @SerialName("tags") val tags: List<PixelfedTagDto> = emptyList(),
    @SerialName("url") val url: String = "",
    @SerialName("visibility") val visibility: PixelfedVisibilityDto,
    @SerialName("bookmarked") val bookmarked: Boolean = false,
    @SerialName("emojis") val emojis: List<PixelfedEmojiDto> = emptyList(),
    @SerialName("comments_disabled") val commentsDisabled: Boolean = false
)

fun PixelfedPostDto.toDomain(): Post {
    val activePost = this.reblog ?: this

    return Post(
        // Handle flattened structural changes if this was a share/boost
        id = this.id,
        reblogId = this.reblog?.id,
        rebloggedBy = if (this.reblog != null) this.account.toDomain() else null,

        // Dynamic fallback matching for string fields
        content = activePost.content ?: activePost.contentText,

        // Pass map downstream to children models
        account = activePost.account.toDomain(),
        mediaAttachments = activePost.mediaAttachments.map { it.toDomain() },
        tags = activePost.tags.map { it.toDomain() },
        mentions = activePost.mentions.map { it.toDomain() },
        emojis = activePost.emojis.map { it.toDomain() },

        // Primitive values mapped cleanly
        favouritesCount = activePost.favouritesCount,
        replyCount = activePost.replyCount,
        createdAt = activePost.createdAt,
        url = activePost.url,
        sensitive = activePost.sensitive,
        spoilerText = activePost.spoilerText,
        favourited = activePost.favourited,
        reblogged = activePost.reblogged,
        bookmarked = activePost.bookmarked,
        reblogCount = activePost.reblogCount,
        inReplyToId = activePost.inReplyToId,

        location = activePost.place?.toDomain(),
        likedBy = activePost.likedBy?.toDomain(),
        visibility = activePost.visibility.toDomain(),
        commentsDisabled = activePost.commentsDisabled
    )
}