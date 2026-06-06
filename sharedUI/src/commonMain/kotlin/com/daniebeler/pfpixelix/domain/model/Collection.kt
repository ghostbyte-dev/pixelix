package com.daniebeler.pfpixelix.domain.model

data class Collection(
    val id: String,
    val visibility: String,
    val title: String = "",
    val description: String = "",
    val thumbnail: String,
    val postCount: Int,
    val username: String,
    val url: String
)