package com.daniebeler.pfpixelix.domain.repository.serializers

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.LikedBy
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Place
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.model.Visibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


internal object PostSerializer : KSerializer<Post> {
    private val dtoSerializer = PostDto.serializer()

    override val descriptor: SerialDescriptor = dtoSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Post
    ) {
        error("Post serialization is not available")
    }

    override fun deserialize(decoder: Decoder): Post {
        val p = dtoSerializer.deserialize(decoder)
        val reblog = p.reblog
        if (reblog != null) {
            return Post(
                rebloggedBy = p.account,
                id = p.id,
                reblogId = reblog.id,
                mediaAttachments = reblog.mediaAttachments,
                account = reblog.account,
                tags = reblog.tags,
                favouritesCount = reblog.favouritesCount,
                content = reblog.content ?: reblog.contentText,
                replyCount = reblog.replyCount,
                createdAt = reblog.createdAt,
                url = reblog.url,
                sensitive = reblog.sensitive,
                spoilerText = reblog.spoilerText,
                favourited = reblog.favourited,
                reblogged = reblog.reblogged,
                bookmarked = reblog.bookmarked,
                mentions = reblog.mentions,
                place = reblog.place,
                likedBy = reblog.likedBy,
                visibility = reblog.visibility,
                inReplyToId = reblog.inReplyToId
            )
        } else {
            return Post(
                id = p.id,
                mediaAttachments = p.mediaAttachments,
                account = p.account,
                tags = p.tags,
                favouritesCount = p.favouritesCount,
                content = p.content ?: p.contentText,
                replyCount = p.replyCount,
                createdAt = p.createdAt,
                url = p.url,
                sensitive = p.sensitive,
                spoilerText = p.spoilerText,
                favourited = p.favourited,
                reblogged = p.reblogged,
                bookmarked = p.bookmarked,
                mentions = p.mentions,
                place = p.place,
                likedBy = p.likedBy,
                visibility = p.visibility,
                inReplyToId = p.inReplyToId
            )
        }
    }

}

@Serializable
private data class PostDto(
    @SerialName("account") val account: Account,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("content") val content: String?,
    @SerialName("content_text") val contentText: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("favourited") val favourited: Boolean = false,
    @SerialName("favourites_count") val favouritesCount: Int = 0,
    @SerialName("id") val id: String = "",
    @SerialName("in_reply_to_id") val inReplyToId: String?,
    @SerialName("liked_by") val likedBy: LikedBy?,
    @SerialName("media_attachments") val mediaAttachments: List<MediaAttachment> = emptyList(),
    @SerialName("mentions") val mentions: List<Account> = emptyList(),
    @SerialName("place") val place: Place?,
    @SerialName("reblog") val reblog: PostDto?,
    @SerialName("reblogged") val reblogged: Boolean = false,
    @SerialName("reply_count") val replyCount: Int = 0,
    @SerialName("sensitive") val sensitive: Boolean = false,
    @SerialName("spoiler_text") val spoilerText: String = "",
    @SerialName("tags") val tags: List<Tag> = emptyList(),
    @SerialName("url") val url: String = "",
    @SerialName("visibility") val visibility: Visibility,
    @SerialName("bookmarked") val bookmarked: Boolean = false,
)