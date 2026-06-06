package com.daniebeler.pfpixelix.domain.model

data class Instance(
    val domain: String,
    val rules: List<Rule>,
    val shortDescription: String,
    val description: String,
    val thumbnailUrl: String,
    val admin: Account? = null,
    val stats: InstanceStats,
    val version: String,
    val configuration: Configuration
)

data class Rule(
    val id: String,
    val text: String
)

data class InstanceStats(
    val userCount: Int,
    val statusCount: Int,
    val domainCount: Int
)

data class Configuration(
    val mediaAttachmentConfig: MediaAttachmentConfiguration,
    val statusConfig: StatusConfiguration
)

data class MediaAttachmentConfiguration(
    val supportedMimeTypes: List<String>,
    val imageSizeLimit: Long,
    val videoSizeLimit: Long
)

data class StatusConfiguration(
    val maxMediaAttachments: Int,
    val maxCharacters: Int?
)