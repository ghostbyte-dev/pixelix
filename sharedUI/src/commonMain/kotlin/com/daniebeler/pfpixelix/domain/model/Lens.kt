package com.daniebeler.pfpixelix.domain.model

data class Lens(
    override val id: String,
    val name: String,
    val amount: Int
): Identifiable
