package com.daniebeler.pfpixelix.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import kotlin.time.Instant

actual fun formatLocalized(dataDate: String): String {
    if (dataDate.isEmpty()) return ""
    return try {
        val instant = Instant.parse(dataDate)
        val nsDate = NSDate.dateWithTimeIntervalSince1970(
            instant.toEpochMilliseconds() / 1000.0
        )
        val formatter = NSDateFormatter().apply {
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterMediumStyle
            locale = NSLocale.currentLocale
        }
        formatter.stringFromDate(nsDate)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

actual fun formatLocalizedOnlyDate(dataDate: String): String {
    if (dataDate.isEmpty()) return ""
    return try {
        val instant = Instant.parse(dataDate)
        val nsDate = NSDate.dateWithTimeIntervalSince1970(
            instant.toEpochMilliseconds() / 1000.0
        )
        val formatter = NSDateFormatter().apply {
            dateStyle = NSDateFormatterShortStyle
            timeStyle = NSDateFormatterNoStyle
            locale = NSLocale.currentLocale
        }
        formatter.stringFromDate(nsDate)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
