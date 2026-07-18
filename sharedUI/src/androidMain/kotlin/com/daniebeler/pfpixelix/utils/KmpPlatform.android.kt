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
import java.io.ByteArrayInputStream

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
