package com.daniebeler.pfpixelix.domain.model

data class Tag(
    override val id: String,
    val name: String,
    val url: String,
    val following: Boolean,
    val count: Int,
    val total: Int,
    val hashtag: String?
): Identifiable