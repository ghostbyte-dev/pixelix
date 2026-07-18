package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.pixelfed.model.request.PixelfedUpdateUserRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUpdateUserRequest

data class UpdateUserRequest(
    val displayName: String? = null,
    val note: String? = null,
    val website: String? = null,
    val manuallyAcceptNewFollowers: Boolean? = null,
    val includeProfilePageInSearchEngines: Boolean? = null,
    val includePublicPostsInSearchEngines: Boolean? = null,
    val locked: Boolean
)

fun UpdateUserRequest.toPixelfed(): PixelfedUpdateUserRequest {
    return PixelfedUpdateUserRequest(
        displayName = this.displayName,
        note = this.note,
        website = this.website,
        locked = this.locked
    )
}

fun UpdateUserRequest.toVernissage(): VernissageUpdateUserRequest {
    return VernissageUpdateUserRequest(
        name = this.displayName,
        bio = this.note,
        manuallyApprovesFollowers = this.manuallyAcceptNewFollowers,
        includeProfilePageInSearchEngines = this.includeProfilePageInSearchEngines,
        includePublicPostsInSearchEngines = this.includePublicPostsInSearchEngines
    )
}