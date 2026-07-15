package com.daniebeler.pfpixelix.utils

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.GPSData
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import io.github.kdroidfilter.composemediaplayer.util.getUri
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.io.ByteArrayInputStream
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.time.Instant

actual typealias KmpUri = Uri

actual val EmptyKmpUri: KmpUri = Uri.EMPTY
actual fun KmpUri.getPlatformUriObject(): Any = this
actual fun String.toKmpUri(): KmpUri = this.toUri()
actual fun PlatformFile.toKmpUri(): KmpUri = this.getUri().toUri()
actual fun KmpUri.toPlatformFile(): PlatformFile = PlatformFile(this)

actual typealias KmpContext = Context

actual val KmpContext.coilContext: PlatformContext get() = this
actual fun parseExifMetadata(bytes: ByteArray): MediaAttachmentMetadataRequest {
    return try {
        val inputStream = ByteArrayInputStream(bytes)
        val exif = ExifInterface(inputStream)

        val lensMake = exif.getAttribute(ExifInterface.TAG_LENS_MAKE)?.trim()
        val lensModel = exif.getAttribute(ExifInterface.TAG_LENS_MODEL)?.trim()

        val coords = exif.latLong
        val gpsData = if (coords != null && coords.size >= 2) {
            GPSData(
                lat = coords[0].toString(),
                long = coords[1].toString()
            )
        } else {
            GPSData(lat = "", long = "")
        }

        MediaAttachmentMetadataRequest(
            make = FieldState(exif.getAttribute(ExifInterface.TAG_MAKE)),
            model = FieldState(exif.getAttribute(ExifInterface.TAG_MODEL)),
            createDate = FieldState(parseExifDateTime(exif.getAttribute(ExifInterface.TAG_DATETIME))),
            focalLength = FieldState(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)),
            fNumber = FieldState(convertApexToFNumber(
                exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE) ?: ""
            )),
            exposureTime = FieldState(formatDecimalToExposureFraction(exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME))),
            photographicSensitivity = FieldState(exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)),
            software = FieldState(exif.getAttribute(ExifInterface.TAG_SOFTWARE)),
            flash = FieldState(getFlashReadableString(exif.getAttribute(ExifInterface.TAG_FLASH))),
            lens = FieldState(if (!lensMake.isNullOrBlank() && !lensModel.isNullOrBlank()) {
                "$lensMake $lensModel"
            } else {
                null
            }),
            focalLenIn35mmFilm = FieldState(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM)),
            gpsData = FieldState(gpsData, isIncluded = false)
        )
    } catch (e: Throwable) {
        e.printStackTrace()
        MediaAttachmentMetadataRequest()
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

    return "f/" + String.format(java.util.Locale.US, "%.1f", fNumber)
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

fun parseExifDateTime(exifString: String?): Instant? {
    if (exifString.isNullOrBlank()) return null

    return try {
        val isoString = exifString
            .replaceFirst(':', '-')
            .replaceFirst(':', '-')
            .replace(' ', 'T')

        val localDateTime = LocalDateTime.parse(isoString)

        localDateTime.toInstant(TimeZone.UTC)
    } catch (_: Throwable) {
        null
    }
}