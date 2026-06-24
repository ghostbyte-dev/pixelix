package com.daniebeler.pfpixelix.domain.model

data class Settings(
    val enableReblogs: Boolean,
    val hideCollections: Boolean?,
    val hideGroups: Boolean?,
    val hideStories: Boolean?
)