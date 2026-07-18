package com.daniebeler.pfpixelix.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.time.Instant

fun parseExifDateTime(exifString: String?): Instant? {
    if (exifString.isNullOrBlank()) return null

    return try {
        val isoString = exifString.replaceFirst(':', '-').replaceFirst(':', '-').replace(' ', 'T')

        val localDateTime = LocalDateTime.parse(isoString)

        localDateTime.toInstant(TimeZone.UTC)
    } catch (_: Throwable) {
        null
    }
}

fun formatDecimalToExposureFraction(decimalStr: String?): String? {
    val exposureDecimal = decimalStr?.toDoubleOrNull() ?: return null

    return when {
        exposureDecimal >= 1.0 -> {
            exposureDecimal.roundToInt().toString() + "s"
        }

        exposureDecimal > 0.0 -> {
            val denominator = (1.0 / exposureDecimal).roundToInt()
            "1/$denominator" + "s"
        }

        else -> decimalStr + "s"
    }
}


fun convertApexToFNumber(apertureValueStr: String): String? {
    val parts = apertureValueStr.split("/")

    val apexValue = if (parts.size == 2) {
        val numerator = parts[0].toDoubleOrNull()
        val denominator = parts[1].toDoubleOrNull()
        if (numerator != null && denominator != null && denominator != 0.0) {
            numerator / denominator
        } else null
    } else {
        apertureValueStr.toDoubleOrNull()
    } ?: return null

    val fNumber = sqrt(2.0.pow(apexValue))

    val rounded = (fNumber * 10).roundToInt() / 10.0

    return "f/$rounded"
}


