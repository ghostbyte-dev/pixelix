package com.daniebeler.pfpixelix.utils

import kotlin.time.Instant

actual fun formatLocalized(dataDate: String): String {
    if (dataDate.isEmpty()) return ""
    return try {
        val millis = Instant.parse(dataDate).toEpochMilliseconds()
        formatDateTime(millis.toDouble())
    } catch (e: Throwable) {
        e.printStackTrace()
        ""
    }
}

actual fun formatLocalizedOnlyDate(dataDate: String): String {
    if (dataDate.isEmpty()) return ""
    return try {
        val millis = Instant.parse(dataDate).toEpochMilliseconds()
        formatDate(millis.toDouble())
    } catch (e: Throwable) {
        e.printStackTrace()
        ""
    }
}

// Delegate localization to the browser's Intl-backed Date formatting.
private fun formatDateTime(epochMillis: Double): String =
    js("new Date(epochMillis).toLocaleString()")

private fun formatDate(epochMillis: Double): String =
    js("new Date(epochMillis).toLocaleDateString()")
