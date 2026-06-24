package com.daniebeler.pfpixelix.domain.model

data class NodeInfo(
    val software: String,
    val usage: NodeinfoUsage,
    val metadata: NodeinfoMetadata
)

data class NodeinfoMetadata(
    val nodeDescription: String = "",
    val nodeName: String = ""
)

data class NodeinfoUsage(
    val localComments: Int = 0,
    val localPosts: Int = 0,
    val users: NodeinfoUsers = NodeinfoUsers()
)

data class NodeinfoUsers(
    val activeHalfyear: Int = 0,
    val activeMonth: Int = 0,
    val total: Int = 0
)

data class ServerLocation(
    val city: String?,
    val country: String?
)

data class ServerStats(
    val monthlyActiveUsers: Int,
    val statusCount: Int,
    val userCount: Int
)

data class SoftwareSmall(
    val id: Int,
    val name: String,
    val url: String,
    val version: String
)