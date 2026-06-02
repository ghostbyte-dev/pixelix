package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FediseaSoftware(
    @SerialName("identifier")
    val identifier: String,

    @SerialName("name")
    val name: String? = null,

    @SerialName("website")
    val website: String? = null,

    @SerialName("sourceCode")
    val sourceCode: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("licence")
    val license: String? = null,

    @SerialName("joinUrl")
    val joinUrl: String? = null,

    @SerialName("instances")
    val instances: Int = 0,

    @SerialName("activeUsersHalfyear")
    val activeUsersHalfyear: Int = 0,

    @SerialName("activeUsersMonth")
    val activeUsersMonth: Int = 0,

    @SerialName("totalUsers")
    val totalUsers: Int = 0,

    @SerialName("localPosts")
    val localPosts: Int = 0,

    @SerialName("localComments")
    val localComments: Int? = null,

    @SerialName("iconUrl")
    val iconUrl: String? = null
)