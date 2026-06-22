package com.daniebeler.pfpixelix.domain.model

data class Location(
    val id: String,
    val name: String?,
    val latitude: String? = null,
    val longitude: String? = null,
    val country: Country?
)

data class Country(
    val id: String?, val name: String?, val code: String?
)