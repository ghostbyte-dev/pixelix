package com.daniebeler.pfpixelix.domain.service.vernissage.model

import com.daniebeler.pfpixelix.domain.model.Configuration
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.InstanceStats
import com.daniebeler.pfpixelix.domain.model.MediaAttachmentConfiguration
import com.daniebeler.pfpixelix.domain.model.Rule
import com.daniebeler.pfpixelix.domain.model.StatusConfiguration
import com.daniebeler.pfpixelix.domain.repository.serializers.HtmlAsTextSerializer
import com.daniebeler.pfpixelix.domain.service.general.DtoMappable
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VernissageInstanceDto(
    @SerialName("uri") val uri: String,
    @SerialName("rules") val rules: List<VernissgeRuleDto>,
    @SerialName("description") val description: String,
    @Serializable(with = HtmlAsTextSerializer::class)
    @SerialName("longDescription") val longDescription: String,
    @SerialName("thumbnail") val thumbnailUrl: String,
    @SerialName("contact") val admin: VernissageAccountDto? = null,
    @SerialName("stats") val stats: VernissageInstanceStatsDto,
    @SerialName("version") val version: String,
    @SerialName("configuration") val configuration: VernissageConfigurationDto
): DtoMappable<Instance> {

    override fun toDomain(): Instance {
        return Instance(
            domain = this.uri,
            rules = this.rules.map { it.toDomain() },
            shortDescription = this.description,
            description = this.longDescription,
            thumbnailUrl = this.thumbnailUrl,
            admin = this.admin?.toDomain(),
            stats = this.stats.toDomain(),
            version = this.version,
            configuration = this.configuration.toDomain()
        )
    }
}

@Serializable
data class VernissgeRuleDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String
): DtoMappable<Rule> {
    override fun toDomain() = Rule(
        id = this.id,
        text = this.text
    )
}

@Serializable
data class VernissageInstanceStatsDto(
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
data class VernissageConfigurationDto(
    @SerialName("attachments") val mediaAttachmentConfig: VernissageMediaAttachmentConfigurationDto,
    @SerialName("statuses") val statusConfig: VernissageStatusConfigurationDto
): DtoMappable<Configuration> {
    override fun toDomain() = Configuration(
        mediaAttachmentConfig = this.mediaAttachmentConfig.toDomain(),
        statusConfig = this.statusConfig.toDomain()
    )
}

@Serializable
data class VernissageMediaAttachmentConfigurationDto(
    @SerialName("supportedMimeTypes") val supportedMimeTypes: List<String>,
    @SerialName("imageSizeLimit") val imageSizeLimit: Long,
): DtoMappable<MediaAttachmentConfiguration> {
    override  fun toDomain() = MediaAttachmentConfiguration(
        supportedMimeTypes = this.supportedMimeTypes,
        imageSizeLimit = this.imageSizeLimit,
        videoSizeLimit = null
    )
}

@Serializable
data class VernissageStatusConfigurationDto(
    @SerialName("maxMediaAttachments") val maxMediaAttachments: Int,
    @SerialName("maxCharacters") val maxCharacters: Int?
): DtoMappable<StatusConfiguration> {
    override fun toDomain() = StatusConfiguration(
        maxMediaAttachments = this.maxMediaAttachments,
        maxCharacters = this.maxCharacters
    )
}