package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FediseaInstance(
    @SerialName("domain")
    val domain: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("software")
    val software: String,

    @SerialName("version")
    val version: String,

    @SerialName("openRegistration")
    val openRegistration: Boolean,

    @SerialName("thumbnail")
    val thumbnailUrl: String? = null,

    @SerialName("sourceUrl")
    val sourceUrl: String? = null,

    @SerialName("totalUsers")
    val totalUsers: Int = 0,

    @SerialName("activeUsersHalfyear")
    val activeUsersHalfyear: Int = 0,

    @SerialName("activeUsersMonth")
    val activeUsersMonth: Int = 0,

    @SerialName("localPosts")
    val localPosts: Int = 0,

    @SerialName("localComments")
    val localComments: Int? = null,

    @SerialName("softwareLogoUrl")
    val softwareLogoUrl: String? = null
)