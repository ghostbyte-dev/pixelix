package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.service.pixelfed.model.request.PixelfedUpdateUserRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUpdateFieldRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUpdateUserRequest

data class UpdateUserRequest(
    val displayName: String? = null,
    val note: String? = null,
    val website: String? = null,
    val manuallyAcceptNewFollowers: Boolean? = null,
    val includeProfilePageInSearchEngines: Boolean? = null,
    val includePublicPostsInSearchEngines: Boolean? = null,
    val locked: Boolean,
    val fields: List<UpdateFieldRequest> = emptyList()
)

data class UpdateFieldRequest(
    val id: String? = null,
    val key: String = "",
    val value: String = "",
    val valueHtml: String? = null,
    val isVerified: Boolean? = null
)

fun UpdateUserRequest.toPixelfed(): PixelfedUpdateUserRequest {
    return PixelfedUpdateUserRequest(
        displayName = this.displayName,
        note = this.note,
        website = this.website,
        locked = this.locked
    )
}

fun UpdateFieldRequest.toVernissage(): VernissageUpdateFieldRequest {
    return VernissageUpdateFieldRequest(
        id = this.id,
        key = this.key,
        value = this.value,
        valueHtml = this.valueHtml,
        isVerified = this.isVerified
    )
}

fun UpdateUserRequest.toVernissage(): VernissageUpdateUserRequest {
    return VernissageUpdateUserRequest(
        name = this.displayName,
        bio = this.note,
        manuallyApprovesFollowers = this.manuallyAcceptNewFollowers,
        includeProfilePageInSearchEngines = this.includeProfilePageInSearchEngines,
        includePublicPostsInSearchEngines = this.includePublicPostsInSearchEngines,
        fields = this.fields.map { it.toVernissage() }
    )
}