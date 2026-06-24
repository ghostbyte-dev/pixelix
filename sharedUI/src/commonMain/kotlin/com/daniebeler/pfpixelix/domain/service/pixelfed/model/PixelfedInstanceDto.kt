package com.daniebeler.pfpixelix.domain.service.pixelfed.model

import com.daniebeler.pfpixelix.domain.model.Configuration
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.InstanceStats
import com.daniebeler.pfpixelix.domain.model.MediaAttachmentConfiguration
import com.daniebeler.pfpixelix.domain.model.Rule
import com.daniebeler.pfpixelix.domain.model.StatusConfiguration
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixelfedInstanceDto(
    @SerialName("domain") val domain: String,
    @SerialName("rules") val rules: List<PixelfedRuleDto>,
    @SerialName("shortDescription") val shortDescription: String,
    @SerialName("description") val description: String,
    @SerialName("thumbnailUrl") val thumbnailUrl: String,
    @SerialName("admin") val admin: PixelfedAccountDto? = null,
    @SerialName("stats") val stats: PixelfedInstanceStatsDto,
    @SerialName("version") val version: String,
    @SerialName("configuration") val configuration: PixelfedConfigurationDto
): DtoMappable<Instance> {

    override fun toDomain(): Instance {
        return Instance(
            domain = this.domain,
            rules = this.rules.map { it.toDomain() },
            shortDescription = this.shortDescription,
            description = this.description,
            thumbnailUrl = this.thumbnailUrl,
            admin = this.admin?.toDomain(),
            stats = this.stats.toDomain(),
            version = this.version,
            configuration = this.configuration.toDomain()
        )
    }
}

@Serializable
data class PixelfedRuleDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String
): DtoMappable<Rule> {
    override fun toDomain() = Rule(
        id = this.id,
        text = this.text
    )
}

@Serializable
data class PixelfedInstanceStatsDto(
    @SerialName("userCount") val userCount: Int,
    @SerialName("statusCount") val statusCount: Int,
    @SerialName("domainCount") val domainCount: Int
): DtoMappable<InstanceStats> {
    override fun toDomain() = InstanceStats(
        userCount = this.userCount,
        statusCount = this.statusCount,
        domainCount = this.domainCount
    )
}

@Serializable
data class PixelfedConfigurationDto(
    @SerialName("mediaAttachmentConfig") val mediaAttachmentConfig: PixelfedMediaAttachmentConfigurationDto,
    @SerialName("statusConfig") val statusConfig: PixelfedStatusConfigurationDto
): DtoMappable<Configuration> {
    override fun toDomain() = Configuration(
        mediaAttachmentConfig = this.mediaAttachmentConfig.toDomain(),
        statusConfig = this.statusConfig.toDomain()
    )
}

@Serializable
data class PixelfedMediaAttachmentConfigurationDto(
    @SerialName("supportedMimeTypes") val supportedMimeTypes: List<String>,
    @SerialName("imageSizeLimit") val imageSizeLimit: Long,
    @SerialName("videoSizeLimit") val videoSizeLimit: Long
): DtoMappable<MediaAttachmentConfiguration> {
    override  fun toDomain() = MediaAttachmentConfiguration(
        supportedMimeTypes = this.supportedMimeTypes,
        imageSizeLimit = this.imageSizeLimit,
        videoSizeLimit = this.videoSizeLimit
    )
}

@Serializable
data class PixelfedStatusConfigurationDto(
    @SerialName("maxMediaAttachments") val maxMediaAttachments: Int,
    @SerialName("maxCharacters") val maxCharacters: Int?
): DtoMappable<StatusConfiguration> {
    override fun toDomain() = StatusConfiguration(
        maxMediaAttachments = this.maxMediaAttachments,
        maxCharacters = this.maxCharacters
    )
}