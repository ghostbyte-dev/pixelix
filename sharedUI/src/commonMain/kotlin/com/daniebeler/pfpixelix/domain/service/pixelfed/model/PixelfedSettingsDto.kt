package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Settings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedSettingsDto(
    @SerialName("enable_reblogs") val enableReblogs: Boolean,
    @SerialName("hide_collections") val hideCollections: Boolean?,
    @SerialName("hide_groups") val hideGroups: Boolean?,
    @SerialName("hide_stories") val hideStories: Boolean?
)

fun PixelfedSettingsDto.toDomain(): Settings {
    return Settings(
        enableReblogs = this.enableReblogs,
        hideCollections = this.hideCollections,
        hideGroups = this.hideGroups,
        hideStories = this.hideStories
    )
}
