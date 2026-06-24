package com.daniebeler.pfpixelix.domain.service.vernissage.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageUpdateUserRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("manuallyApprovesFollowers") val manuallyApprovesFollowers: Boolean?,
    @SerialName("includeProfilePageInSearchEngines") val includeProfilePageInSearchEngines: Boolean? = null,
    @SerialName("includePublicPostsInSearchEngines") val includePublicPostsInSearchEngines: Boolean? = null
)
