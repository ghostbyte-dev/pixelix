package com.daniebeler.pfpixelix.utils

object DomainFormat {
    fun formatDomain(domain: String): String {
        return domain
            .removePrefix("https://")
            .removePrefix("http://")
            .removeSuffix("/")
    }

    fun extractUrl(text: String): String? {
        val urlRegex = Regex("""https?://[^\s"'<>]+""")
        val match = urlRegex.find(text)?.value?.trimEnd('/') ?: return null
        return match
            .removePrefix("https://")
            .removePrefix("http://")
    }
}