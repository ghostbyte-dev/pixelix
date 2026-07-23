package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.GPSData
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDataCreate
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
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithData
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.CGImageSourceGetCount
import platform.ImageIO.CGImageSourceGetStatusAtIndex
import platform.ImageIO.CGImageSourceGetType
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
        val cfData = bytes.usePinned { pinned ->
            CFDataCreate(null, pinned.addressOf(0).reinterpret(), bytes.size.toLong())
        } ?: return MediaAttachmentMetadataRequest()

        val imageSource = try {
            CGImageSourceCreateWithData(cfData, null)
        } finally {
            CFRelease(cfData)
        } ?: return MediaAttachmentMetadataRequest()
        try {
            val cfPropertiesRef = CGImageSourceCopyPropertiesAtIndex(imageSource, 0u, null)
                ?: return MediaAttachmentMetadataRequest()

            val properties = CFBridgingRelease(cfPropertiesRef) as? Map<Any?, Any?>
                ?: return MediaAttachmentMetadataRequest()

            val exifDict = (properties["{Exif}"]) as? Map<Any?, Any?>
            val tiffDict = (properties["{TIFF}"]) as? Map<Any?, Any?>
            val gpsDict  = (properties["{GPS}"]) as? Map<Any?, Any?>

            // Debug - remove once working
            println("properties keys: ${properties.keys}")
            println("exifDict: $exifDict")
            println("tiffDict: $tiffDict")

            val lat    = gpsDict?.get("Latitude") as? Double
            val latRef = gpsDict?.get("LatitudeRef") as? String
            val lon    = gpsDict?.get("Longitude") as? Double
            val lonRef = gpsDict?.get("LongitudeRef") as? String

            val gpsData = if (lat != null && lon != null) {
                val finalLat = if (latRef == "S") -lat else lat
                val finalLon = if (lonRef == "W") -lon else lon
                GPSData(lat = finalLat.toString(), long = finalLon.toString())
            } else {
                GPSData(lat = "", long = "")
            }

            val lensMake  = exifDict?.get("LensMake") as? String
            val lensModel = exifDict?.get("LensModel") as? String
            val lens = if (!lensMake.isNullOrBlank() && !lensModel.isNullOrBlank()) {
                "$lensMake $lensModel"
            } else null

            MediaAttachmentMetadataRequest(
                make        = FieldState(tiffDict?.get("Make") as? String),
                model       = FieldState(tiffDict?.get("Model") as? String),
                createDate  = FieldState(parseExifDateTime(exifDict?.get("DateTimeOriginal") as? String)),
                focalLength = FieldState(exifDict?.get("FocalLength")?.toString()),
                fNumber     = FieldState(exifDict?.get("FNumber")?.let { "f/$it" }),
                exposureTime = FieldState(formatDecimalToExposureFraction(exifDict?.get("ExposureTime")?.toString())),
                photographicSensitivity = FieldState((exifDict?.get("ISOSpeedRatings") as? List<*>)?.firstOrNull()?.toString()),
                software    = FieldState(tiffDict?.get("Software") as? String),
                flash       = FieldState(getFlashReadableString(exifDict?.get("Flash")?.toString())),
                lens        = FieldState(lens),
                focalLenIn35mmFilm = FieldState(exifDict?.get("FocalLenIn35mmFilm")?.toString()),
                gpsData     = FieldState(gpsData, isIncluded = false)
            )
        } finally {
            CFRelease(imageSource)
        }
    } catch (e: Throwable) {
        println("parseExifMetadata error: ${e.message}")
        MediaAttachmentMetadataRequest()
    }
}