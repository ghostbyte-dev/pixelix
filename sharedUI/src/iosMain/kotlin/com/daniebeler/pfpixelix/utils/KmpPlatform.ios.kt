package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
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
import platform.ImageIO.kCGImagePropertyExifDictionary
import platform.ImageIO.kCGImagePropertyExifFlash
import platform.ImageIO.kCGImagePropertyTIFFDictionary
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
        val nsData = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
        val imageSource = CGImageSourceCreateWithData(nsData as CFDataRef, null) ?: return MediaAttachmentMetadataRequest()

        val properties = CGImageSourceCopyPropertiesAtIndex(imageSource, 0u, null) as? Map<Any?, Any?>
            ?: return MediaAttachmentMetadataRequest()

        val exifDict = properties[kCGImagePropertyExifDictionary] as? Map<Any?, Any?>
        val tiffDict = properties[kCGImagePropertyTIFFDictionary] as? Map<Any?, Any?>

        MediaAttachmentMetadataRequest(
            make = tiffDict?.get("Make") as? String,
            model = tiffDict?.get("Model") as? String,
            lens = exifDict?.get("LensModel") as? String,
            createDate = exifDict?.get("DateTimeOriginal") as? String,
            focalLength = exifDict?.get("FocalLength")?.toString(),
            fNumber = exifDict?.get("FNumber")?.toString(),
            exposureTime = exifDict?.get("ExposureTime")?.toString(),
            photographicSensitivity = (exifDict?.get("ISOSpeedRatings") as? List<*>)?.firstOrNull()?.toString(),
            software = tiffDict?.get("Software") as? String,
            flash = getFlashReadableString(exifDict?.get(kCGImagePropertyExifFlash)?.toString())
        )
    } catch (e: Exception) {
        MediaAttachmentMetadataRequest()
    }
}