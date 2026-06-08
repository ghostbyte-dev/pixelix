package com.daniebeler.pfpixelix.utils

import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.ago
import pixelix.app.generated.resources.day
import pixelix.app.generated.resources.hour
import pixelix.app.generated.resources.minute
import pixelix.app.generated.resources.month
import pixelix.app.generated.resources.second
import pixelix.app.generated.resources.week
import pixelix.app.generated.resources.year
import kotlin.time.Clock
import kotlin.time.Instant

object TimeAgo {

    suspend fun convertTimeToText(dataDate: String): String {
        if (dataDate.isEmpty()) {
            return ""
        }
        var convTime = ""
        val suffix = getString(Res.string.ago)

        try {
            val pasTime: Instant = Instant.parse(dataDate)
            // Using the new standard library Clock
            val nowTime: Instant = Clock.System.now()
            val dateDiff = nowTime - pasTime

            val seconds = dateDiff.inWholeSeconds
            val minutes = dateDiff.inWholeMinutes
            val hours = dateDiff.inWholeHours
            val days = dateDiff.inWholeDays

            // Logic flow from smallest to largest unit
            convTime = when {
                seconds < 60 -> {
                    val count = seconds.toInt()
                    "$count ${getPluralString(Res.plurals.second, count)} $suffix"
                }
                minutes < 60 -> {
                    val count = minutes.toInt()
                    "$count ${getPluralString(Res.plurals.minute, count)} $suffix"
                }
                hours < 24 -> {
                    val count = hours.toInt()
                    "$count ${getPluralString(Res.plurals.hour, count)} $suffix"
                }
                days < 7 -> {
                    val count = days.toInt()
                    "$count ${getPluralString(Res.plurals.day, count)} $suffix"
                }
                days < 30 -> {
                    val count = (days / 7).toInt()
                    "$count ${getPluralString(Res.plurals.week, count)} $suffix"
                }
                days < 365 -> {
                    val count = (days / 30).toInt()
                    "$count ${getPluralString(Res.plurals.month, count)} $suffix"
                }
                else -> {
                    val count = (days / 365).toInt()
                    "$count ${getPluralString(Res.plurals.year, count)} $suffix"
                }
            }
        } catch (e: Exception) {
            // Instant.parse can throw DateTimeFormatException or IllegalArgumentException
            e.printStackTrace()
        }
        return convTime
    }
}