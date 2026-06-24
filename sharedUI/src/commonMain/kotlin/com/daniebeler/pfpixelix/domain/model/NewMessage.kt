package com.daniebeler.pfpixelix.domain.model

data class NewMessage(
    val toId: String,
    val message: String,
    val type: String
)