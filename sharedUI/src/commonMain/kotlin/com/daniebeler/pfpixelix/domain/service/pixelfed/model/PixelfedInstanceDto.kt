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
    @SerialName("uri") val domain: String,
    @SerialName("rules") val rules: List<PixelfedRuleDto>,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("description") val description: String,
    @SerialName("thumbnail") val thumbnailUrl: String,
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
    @SerialName("user_count") val userCount: Int,
    @SerialName("status_count") val statusCount: Int,
    @SerialName("domain_count") val domainCount: Int
): DtoMappable<InstanceStats> {
    override fun toDomain() = InstanceStats(
        userCount = this.userCount,
        statusCount = this.statusCount,
        domainCount = this.domainCount
    )
}

@Serializable
data class PixelfedConfigurationDto(
    @SerialName("media_attachments") val mediaAttachmentConfig: PixelfedMediaAttachmentConfigurationDto,
    @SerialName("statuses") val statusConfig: PixelfedStatusConfigurationDto
): DtoMappable<Configuration> {
    override fun toDomain() = Configuration(
        mediaAttachmentConfig = this.mediaAttachmentConfig.toDomain(),
        statusConfig = this.statusConfig.toDomain()
    )
}

@Serializable
data class PixelfedMediaAttachmentConfigurationDto(
    @SerialName("supported_mime_types") val supportedMimeTypes: List<String>,
    @SerialName("image_size_limit") val imageSizeLimit: Long,
    @SerialName("video_size_limit") val videoSizeLimit: Long
): DtoMappable<MediaAttachmentConfiguration> {
    override  fun toDomain() = MediaAttachmentConfiguration(
        supportedMimeTypes = this.supportedMimeTypes,
        imageSizeLimit = this.imageSizeLimit,
        videoSizeLimit = this.videoSizeLimit
    )
}

@Serializable
data class PixelfedStatusConfigurationDto(
    @SerialName("max_media_attachments") val maxMediaAttachments: Int,
    @SerialName("max_characters") val maxCharacters: Int?
): DtoMappable<StatusConfiguration> {
    override fun toDomain() = StatusConfiguration(
        maxMediaAttachments = this.maxMediaAttachments,
        maxCharacters = this.maxCharacters
    )
}