package com.daniebeler.pfpixelix.domain.model

data class UpdatePost(
    val status: String,
    val mediaIds: List<String>?,
    val sensitive: Boolean?,
    val spoilerText: String?,
    val location: Location?
)