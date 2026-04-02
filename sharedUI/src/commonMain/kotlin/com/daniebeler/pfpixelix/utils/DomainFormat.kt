package com.daniebeler.pfpixelix.utils

object DomainFormat {
    fun formatDomain(domain: String): String {
        return domain
            .removePrefix("https://")
            .removePrefix("http://")
            .removeSuffix("/")
    }
}