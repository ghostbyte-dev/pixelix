package com.daniebeler.pfpixelix.domain.model

data class Place(
    val id: String,
    val slug: String?,
    val name: String?,
    val country: String?,
    val url: String?
)