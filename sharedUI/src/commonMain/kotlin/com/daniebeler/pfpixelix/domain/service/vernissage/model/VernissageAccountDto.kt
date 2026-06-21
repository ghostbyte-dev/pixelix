package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageAccountDto(
    @SerialName("id") val id: String,
    @SerialName("userName") val username: String,
    @SerialName("account") val account: String = "",
    @SerialName("name") val displayname: String? = null,
    @SerialName("avatarUrl") val avatar: String? = "",
    @SerialName("followersCount") val followersCount: Int = 0,
    @SerialName("followingCount") val followingCount: Int = 0,
    @SerialName("photosCount") val postsCount: Int = 0,
    @Serializable(with = HtmlAsTextSerializer::class) @SerialName("bioHtml") val note: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("createdAt") val createdAt: String = "",
    @SerialName("isAdmin") val isAdmin: Boolean = false,
    @SerialName("headerUrl") val headerUrl: String? = null,
    @SerialName("manuallyApprovesFollowers") val manuallyApprovesFollowers: Boolean? = null,
    @SerialName("includeProfilePageInSearchEngines") val includeProfilePageInSearchEngine: Boolean? = null,
    @SerialName("includePublicPostsInSearchEngines") val includePublicPostsInSearchEngines: Boolean? = null
): DtoMappable<Account> {
    override fun toDomain(): Account {
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
            pronouns = emptyList(),
            headerUrl = headerUrl,
            manuallyApprovesFollowers = this.manuallyApprovesFollowers,
            includePublicPostsInSearchEngines = this.includePublicPostsInSearchEngines,
            includeProfilePageInSearchEngines =  this.includeProfilePageInSearchEngine
        )
    }
}

