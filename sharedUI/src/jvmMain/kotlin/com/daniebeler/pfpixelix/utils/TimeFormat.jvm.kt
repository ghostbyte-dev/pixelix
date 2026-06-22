package com.daniebeler.pfpixelix.utils

import kotlin.time.Instant

actual fun formatLocalized(dataDate: String): String {
    if (dataDate.isEmpty()) return ""
    return try {
        val instant = Instant.parse(dataDate)
        val date = java.util.Date(instant.toEpochMilliseconds())
        java.text.DateFormat.getDateTimeInstance(
            java.text.DateFormat.SHORT, java.text.DateFormat.MEDIUM
        ).format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}