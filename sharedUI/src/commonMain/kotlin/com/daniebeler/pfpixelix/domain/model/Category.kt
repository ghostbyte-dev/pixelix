package com.daniebeler.pfpixelix.domain.model

data class Category(
    val id: String,
    val name: String,
    val isEnabled: Boolean?,
    val priority: Int?
)
