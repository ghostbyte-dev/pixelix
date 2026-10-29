package com.daniebeler.pfpixelix.domain.model

data class Place(
    val id: String,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val country: Country? = null,
    val slug: String? = null
)

data class Country(
    val id: String?, val name: String, val code: String?
)