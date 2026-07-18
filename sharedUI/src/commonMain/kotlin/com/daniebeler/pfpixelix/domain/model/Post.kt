package com.daniebeler.pfpixelix.domain.model

data class Post(
    override val id: String,
    val mediaAttachments: List<MediaAttachment>,
    val account: Account,
    val tags: List<Tag>,
    val favouritesCount: Int,
    val content: String,
    val replyCount: Int,
    val createdAt: String,
    val url: String,
    val sensitive: Boolean,
    val spoilerText: String,
    var favourited: Boolean,
    var reblogged: Boolean,
    val bookmarked: Boolean,
    val mentions: List<Account>,
    val location: Location?,
    val likedBy: LikedBy?,
    val visibility: Visibility,
    val inReplyToId: String?,
    val rebloggedBy: Account? = null,
    val reblogId: String? = null,
    val reblogCount: Int,
    val emojis: List<Emoji>,
    val commentsDisabled: Boolean,
    val category: Category?
): Identifiable

val Post.uiKey: String
    get() = if (reblogId != null) "${id}_reblog_${reblogId}" else id
