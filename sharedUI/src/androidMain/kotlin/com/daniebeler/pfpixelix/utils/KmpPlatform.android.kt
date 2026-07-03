package com.daniebeler.pfpixelix.utils

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil3.PlatformContext
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

        MediaAttachmentMetadataRequest(
            make = exif.getAttribute(ExifInterface.TAG_MAKE),
            model = exif.getAttribute(ExifInterface.TAG_MODEL),
            createDate = exif.getAttribute(ExifInterface.TAG_DATETIME),
            focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH),
            fNumber = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE),
            exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME),
            photographicSensitivity = exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY),
            software = exif.getAttribute(ExifInterface.TAG_SOFTWARE),
            flash = getFlashReadableString(exif.getAttribute(ExifInterface.TAG_FLASH)),
            lens = exif.getAttribute(ExifInterface.TAG_LENS_MAKE) + " " + exif.getAttribute(
                ExifInterface.TAG_LENS_MODEL
            ),
            focalLenIn35mmFilm = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM)
        )
    } catch (e: Exception) {
        MediaAttachmentMetadataRequest()
    }
}