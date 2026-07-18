package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.NodeInfo
import com.daniebeler.pfpixelix.domain.model.NodeinfoMetadata
import com.daniebeler.pfpixelix.domain.model.NodeinfoUsage
import com.daniebeler.pfpixelix.domain.model.NodeinfoUsers
import com.daniebeler.pfpixelix.domain.model.ServerLocation
import com.daniebeler.pfpixelix.domain.model.ServerStats
import com.daniebeler.pfpixelix.domain.model.SoftwareSmall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedNodeInfoDto(
    @SerialName("software") val software: String,
    @SerialName("usage") val usage: PixelfedNodeinfoUsageDto,
    @SerialName("metadata") val metadata: PixelfedNodeinfoMetadataDto
)

@Serializable
data class PixelfedNodeinfoMetadataDto(
    @SerialName("nodeDescription") val nodeDescription: String = "",
    @SerialName("nodeName") val nodeName: String = ""
)

@Serializable
data class PixelfedNodeinfoUsageDto(
    @SerialName("localComments") val localComments: Int = 0,
    @SerialName("localPosts") val localPosts: Int = 0,
    @SerialName("users") val users: PixelfedNodeinfoUsersDto = PixelfedNodeinfoUsersDto()
)

@Serializable
data class PixelfedNodeinfoUsersDto(
    @SerialName("activeHalfyear") val activeHalfyear: Int = 0,
    @SerialName("activeMonth") val activeMonth: Int = 0,
    @SerialName("total") val total: Int = 0
)

@Serializable
data class PixelfedServerLocationDto(
    @SerialName("city") val city: String?,
    @SerialName("country") val country: String?
)

@Serializable
data class PixelfedServerStatsDto(
    @SerialName("monthly_active_users") val monthlyActiveUsers: Int,
    @SerialName("status_count") val statusCount: Int,
    @SerialName("user_count") val userCount: Int
)

@Serializable
data class PixelfedSoftwareSmallDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("version") val version: String
)

// --- MAPPING EXTENSIONS ---

fun PixelfedNodeInfoDto.toDomain(): NodeInfo {
    return NodeInfo(
        software = this.software,
        usage = this.usage.toDomain(),
        metadata = this.metadata.toDomain()
    )
}

fun PixelfedNodeinfoMetadataDto.toDomain(): NodeinfoMetadata {
    return NodeinfoMetadata(
        nodeDescription = this.nodeDescription,
        nodeName = this.nodeName
    )
}

fun PixelfedNodeinfoUsageDto.toDomain(): NodeinfoUsage {
    return NodeinfoUsage(
        localComments = this.localComments,
        localPosts = this.localPosts,
        users = this.users.toDomain()
    )
}

fun PixelfedNodeinfoUsersDto.toDomain(): NodeinfoUsers {
    return NodeinfoUsers(
        activeHalfyear = this.activeHalfyear,
        activeMonth = this.activeMonth,
        total = this.total
    )
}

fun PixelfedServerLocationDto.toDomain(): ServerLocation {
    return ServerLocation(
        city = this.city,
        country = this.country
    )
}

fun PixelfedServerStatsDto.toDomain(): ServerStats {
    return ServerStats(
        monthlyActiveUsers = this.monthlyActiveUsers,
        statusCount = this.statusCount,
        userCount = this.userCount
    )
}

fun PixelfedSoftwareSmallDto.toDomain(): SoftwareSmall {
    return SoftwareSmall(
        id = this.id,
        name = this.name,
        url = this.url,
        version = this.version
    )
}