package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedAccountDto(
    @SerialName("id") val id: String = "",
    @SerialName("username") val username: String = "",
    @SerialName("acct") val acct: String = "",
    @SerialName("display_name") val displayname: String? = null,
    @SerialName("avatar") val avatar: String = "",
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("statuses_count") val postsCount: Int = 0,
    @SerialName("website") val website: String = "",
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("note") val note: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("is_admin") val isAdmin: Boolean = false,
    @SerialName("pronouns") val pronouns: List<String> = emptyList()
): DtoMappable<Account> {
    override fun toDomain(): Account {
        return Account(
            id = this.id,
            username = this.username,
            shortUsername = this.username.substringBefore("@"),
            acct = this.acct,
            displayname = this.displayname,
            avatar = this.avatar,
            followersCount = this.followersCount,
            followingCount = this.followingCount,
            postsCount = this.postsCount,
            website = this.website,
            note = this.note,
            url = this.url,
            locked = this.locked,
            createdAt = this.createdAt,
            isAdmin = this.isAdmin,
            pronouns = this.pronouns
        )
    }

    fun toMutedAccount(): MutedAccount {
        return MutedAccount(
            id = id,
            account = this.toDomain(),
            UserMuteRequest(
                mute = true
            )
        )
    }
}

