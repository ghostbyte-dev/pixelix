package com.daniebeler.pfpixelix.domain.model.request

import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.model.toPixelfed
import com.daniebeler.pfpixelix.domain.model.toVernissage
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.request.PixelfedNewPostRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageNewPostRequest

data class NewPostRequest(
    val note: String,
    val mediaIds: List<String> = emptyList(),
    val sensitive: Boolean,
    val contentWarning: String?,
    val visibility: Visibility,
    val placeId: String?,
    val commentsDisabled: Boolean,
    val categoryId: String?
)

fun NewPostRequest.toPixelfed(): PixelfedNewPostRequest {
    return PixelfedNewPostRequest(
        status = this.note,
        mediaIds = this.mediaIds,
        sensitive = this.sensitive,
        visibility = this.visibility.toPixelfed(),
        spoilerText = this.contentWarning,
        placeId = this.placeId
    )
}

fun NewPostRequest.toVernissage(): VernissageNewPostRequest {
    return VernissageNewPostRequest(
        note = this.note,
        mediaIds = this.mediaIds,
        sensitive = this.sensitive,
        contentWarning = this.contentWarning,
        visibility = this.visibility.toVernissage(),
        placeId = this.placeId,
        commentsDisabled = this.commentsDisabled,
        categoryId = this.categoryId
    )
}