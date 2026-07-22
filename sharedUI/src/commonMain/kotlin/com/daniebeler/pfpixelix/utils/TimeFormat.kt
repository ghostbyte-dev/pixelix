package com.daniebeler.pfpixelix.utils

import org.jetbrains.compose.resources.getPluralString
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.days_ago
import pixelix.app.generated.resources.hours_ago
import pixelix.app.generated.resources.minutes_ago
import pixelix.app.generated.resources.months_ago
import pixelix.app.generated.resources.seconds_ago
import pixelix.app.generated.resources.weeks_ago
import pixelix.app.generated.resources.years_ago
import kotlin.time.Clock
import kotlin.time.Instant


suspend fun timeAgo(dataDate: String): String {
    if (dataDate.isEmpty()) {
        return ""
    }
    var convTime = ""

    try {
        val pasTime: Instant = Instant.parse(dataDate)
        val nowTime: Instant = Clock.System.now()
        val dateDiff = nowTime - pasTime

        val seconds = dateDiff.inWholeSeconds
        val minutes = dateDiff.inWholeMinutes
        val hours = dateDiff.inWholeHours
        val days = dateDiff.inWholeDays

        convTime = when {
            seconds < 60 -> {
                val count = seconds.toInt().coerceAtLeast(1)
                getPluralString(Res.plurals.seconds_ago, count, count)
            }

            minutes < 60 -> {
                val count = minutes.toInt()
                getPluralString(Res.plurals.minutes_ago, count, count)
            }

            hours < 24 -> {
                val count = hours.toInt()
                getPluralString(Res.plurals.hours_ago, count, count)
            }

            days < 7 -> {
                val count = days.toInt()
                getPluralString(Res.plurals.days_ago, count, count)
            }

            days < 30 -> {
                val count = (days / 7).toInt()
                getPluralString(Res.plurals.weeks_ago, count, count)
            }

            days < 365 -> {
                val count = (days / 30).toInt()
                getPluralString(Res.plurals.months_ago, count, count)
            }

            else -> {
                val count = (days / 365).toInt()
                getPluralString(Res.plurals.years_ago, count, count)
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return convTime
}


expect fun formatLocalized(dataDate: String): String
expect fun formatLocalizedOnlyDate(dataDate: String): String