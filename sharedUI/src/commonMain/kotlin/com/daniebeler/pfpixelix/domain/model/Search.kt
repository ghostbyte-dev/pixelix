package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Search(
    @SerialName("accounts") val accounts: List<Account>,
    @SerialName("statuses") val posts: List<Post>,
    @SerialName("hashtags") val tags: List<Tag>
)
