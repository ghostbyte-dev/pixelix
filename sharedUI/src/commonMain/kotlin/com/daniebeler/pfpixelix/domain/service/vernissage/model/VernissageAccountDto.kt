package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageAccountDto(
    @SerialName("id") val id: String,
    @SerialName("userName") val username: String,
    @SerialName("account") val account: String = "",
    @SerialName("name") val displayname: String? = null,
    @SerialName("avatar") val avatar: String? = "",
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("photosCount") val postsCount: Int = 0,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("bioHtml") val note: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

fun VernissageAccountDto.toDomain(): Account {
    return Account(
        id = this.id,
        username = this.username,
        acct = this.account,
        displayname = this.displayname,
        avatar = this.avatar ?: "",
        followersCount = this.followersCount,
        followingCount = this.followingCount,
        postsCount = this.postsCount,
        website = "",
        note = this.note,
        url = this.url,
        locked = false,
        createdAt = this.createdAt,
        isAdmin = false,
        pronouns = emptyList()
    )
}