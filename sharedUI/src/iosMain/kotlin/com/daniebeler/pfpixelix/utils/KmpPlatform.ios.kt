package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.GPSData
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreServices.UTTypeCopyPreferredTagWithClass
import platform.CoreServices.UTTypeCreatePreferredIdentifierForTag
import platform.CoreServices.kUTTagClassFilenameExtension
import platform.CoreServices.kUTTagClassMIMEType
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.kCGImagePropertyExifDateTimeOriginal
import platform.ImageIO.kCGImagePropertyExifDictionary
import platform.ImageIO.kCGImagePropertyExifExposureTime
import platform.ImageIO.kCGImagePropertyExifFNumber
import platform.ImageIO.kCGImagePropertyExifFlash
import platform.ImageIO.kCGImagePropertyExifFocalLenIn35mmFilm
import platform.ImageIO.kCGImagePropertyExifFocalLength
import platform.ImageIO.kCGImagePropertyExifISOSpeedRatings
import platform.ImageIO.kCGImagePropertyExifLensMake
import platform.ImageIO.kCGImagePropertyExifLensModel
import platform.ImageIO.kCGImagePropertyGPSDictionary
import platform.ImageIO.kCGImagePropertyGPSLatitude
import platform.ImageIO.kCGImagePropertyGPSLatitudeRef
import platform.ImageIO.kCGImagePropertyGPSLongitude
import platform.ImageIO.kCGImagePropertyGPSLongitudeRef
import platform.ImageIO.kCGImagePropertyTIFFDictionary
import platform.ImageIO.kCGImagePropertyTIFFMake
import platform.ImageIO.kCGImagePropertyTIFFModel
import platform.ImageIO.kCGImagePropertyTIFFSoftware
import platform.UIKit.UIViewController

private data class IosUri(override val url: NSURL) : KmpUri() {
    override fun toString(): String = url.toString()
}

actual abstract class KmpUri {
    abstract val url: NSURL
    actual abstract override fun toString(): String
}

actual val EmptyKmpUri: KmpUri = IosUri(NSURL(string = ""))
actual fun KmpUri.getPlatformUriObject(): Any = url
actual fun String.toKmpUri(): KmpUri = IosUri(NSURL(string = this))
actual fun PlatformFile.toKmpUri(): KmpUri = IosUri(nsUrl)
actual fun KmpUri.toPlatformFile(): PlatformFile = PlatformFile(url)

actual abstract class KmpContext {
    abstract val viewController: UIViewController
}

actual val KmpContext.coilContext get() = PlatformContext.INSTANCE

@OptIn(ExperimentalForeignApi::class)
actual fun parseExifMetadata(bytes: ByteArray): MediaAttachmentMetadataRequest {
    return try {
        val nsData = bytes.usePinned {
            NSData.create(
                bytes = it.addressOf(0),
                length = bytes.size.toULong()
            )
        }
        val imageSource = CGImageSourceCreateWithData(nsData as CFDataRef, null)
            ?: return MediaAttachmentMetadataRequest()

        val properties =
            CGImageSourceCopyPropertiesAtIndex(imageSource, 0u, null) as? Map<Any?, Any?>
                ?: return MediaAttachmentMetadataRequest()

        val exifDict = properties[kCGImagePropertyExifDictionary] as? Map<Any?, Any?>
        val tiffDict = properties[kCGImagePropertyTIFFDictionary] as? Map<Any?, Any?>
        val gpsDict = properties[kCGImagePropertyGPSDictionary] as? Map<Any?, Any?>

        // GPS Logic
        val lat = gpsDict?.get(kCGImagePropertyGPSLatitude) as? Double
        val latRef = gpsDict?.get(kCGImagePropertyGPSLatitudeRef) as? String
        val lon = gpsDict?.get(kCGImagePropertyGPSLongitude) as? Double
        val lonRef = gpsDict?.get(kCGImagePropertyGPSLongitudeRef) as? String

        val gpsData = if (lat != null && lon != null) {
            val finalLat = if (latRef == "S") -lat else lat
            val finalLon = if (lonRef == "W") -lon else lon
            GPSData(lat = finalLat.toString(), long = finalLon.toString())
        } else {
            GPSData(lat = "", long = "")
        }

        // Lens Logic
        val lensMake = exifDict?.get(kCGImagePropertyExifLensMake) as? String
        val lensModel = exifDict?.get(kCGImagePropertyExifLensModel) as? String
        val lens = if (!lensMake.isNullOrBlank() && !lensModel.isNullOrBlank()) {
            "$lensMake $lensModel"
        } else {
            null
        }

        MediaAttachmentMetadataRequest(
            make = FieldState(tiffDict?.get(kCGImagePropertyTIFFMake) as? String),
            model = FieldState(tiffDict?.get(kCGImagePropertyTIFFModel) as? String),
            createDate = FieldState(parseExifDateTime(exifDict?.get(kCGImagePropertyExifDateTimeOriginal) as? String)),
            focalLength = FieldState(exifDict?.get(kCGImagePropertyExifFocalLength)?.toString()),
            fNumber = FieldState(exifDict?.get(kCGImagePropertyExifFNumber)?.let { "f/$it" }),
            exposureTime = FieldState(formatDecimalToExposureFraction(exifDict?.get(kCGImagePropertyExifExposureTime)?.toString())),
            photographicSensitivity = FieldState((exifDict?.get(kCGImagePropertyExifISOSpeedRatings) as? List<*>)?.firstOrNull()?.toString()),
            software = FieldState(tiffDict?.get(kCGImagePropertyTIFFSoftware) as? String),
            flash = FieldState(getFlashReadableString(exifDict?.get(kCGImagePropertyExifFlash)?.toString())),
            lens = FieldState(lens),
            focalLenIn35mmFilm = FieldState(exifDict?.get(kCGImagePropertyExifFocalLenIn35mmFilm)?.toString()),
            gpsData = FieldState(gpsData, isIncluded = false)
        )
    } catch (e: Throwable) {
        MediaAttachmentMetadataRequest()
    }
}