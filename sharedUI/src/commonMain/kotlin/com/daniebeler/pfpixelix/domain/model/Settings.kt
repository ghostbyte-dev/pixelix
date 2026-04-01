package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    @SerialName("enable_reblogs") val enableReblogs: Boolean,
    @SerialName("hide_collections") val hideCollections: Boolean?,
    @SerialName("hide_groups") val hideGroups: Boolean?,
    @SerialName("hide_stories") val hideStories: Boolean?,
)
