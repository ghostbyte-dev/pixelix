package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissagePostDto @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("id") val id: String = "",
    @SerialName("user") val account: VernissageAccountDto,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("noteHtml") val content: String?,
    @SerialName("note") val contentText: String = "",
    @SerialName("createdAt") val createdAt: String = "",
    @SerialName("favourited") val favourited: Boolean = false,
    @SerialName("favouritesCount") val favouritesCount: Int = 0,
    //@SerialName("in_reply_to_id") val inReplyToId: String?,
    //@SerialName("liked_by") val likedBy: PixelfedLikedByDto?,
    @SerialName("attachments") val mediaAttachments: List<VernissageMediaAttachmentDto> = emptyList(),
    //@SerialName("mentions") val mentions: List<PixelfedAccountDto> = emptyList(),
    //@SerialName("place") val place: PixelfedPlaceDto?,
    @SerialName("reblog") val reblog: VernissagePostDto?,
    @SerialName("reblogged") val reblogged: Boolean = false,
    @SerialName("reblogsCount") val reblogsCount: Int = 0,

    // @JsonNames allows fallback matching during parsing.
    // This completely removes the need for PostDtoTransformingSerializer!
    @SerialName("repliesCount") val replyCount: Int = 0,

    @SerialName("sensitive") val sensitive: Boolean = false,
    //@SerialName("spoiler_text") val spoilerText: String = "",
    @SerialName("tags") val tags: List<VernissageTagDto> = emptyList(),
    @SerialName("activityPubUrl") val url: String = "",
    @SerialName("visibility") val visibility: VernissageVisibilityDto,
    @SerialName("bookmarked") val bookmarked: Boolean = false,
 //   @SerialName("emojis") val emojis: List<PixelfedEmojiDto> = emptyList()
    @SerialName("commentsDisabled") val commentsDisabled: Boolean = false,
    @SerialName("category") val category: VernissageCategoryDto?
): DtoMappable<Post> {
    override fun toDomain(): Post {
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
            // mentions = activePost.mentions.map { it.toDomain() },
            mentions = emptyList(),
            emojis = emptyList(),
            // emojis = activePost.emojis.map { it.toDomain() },

            // Primitive values mapped cleanly
            favouritesCount = activePost.favouritesCount,
            replyCount = activePost.replyCount,
            createdAt = activePost.createdAt,
            url = activePost.url,
            sensitive = activePost.sensitive,
            //spoilerText = activePost.spoilerText,
            spoilerText = "",
            favourited = activePost.favourited,
            reblogged = activePost.reblogged,
            bookmarked = activePost.bookmarked,
            reblogCount = activePost.reblogsCount,
            //inReplyToId = activePost.inReplyToId,
            inReplyToId = null,
            // Nested nullable options
            location = null,
            //place = activePost.place?.toDomain(),
            //likedBy = activePost.likedBy?.toDomain(),
            likedBy = null,
            visibility = activePost.visibility.toDomain(),
            commentsDisabled = activePost.commentsDisabled,
            category = activePost.category?.toDomain()
        )
    }
}