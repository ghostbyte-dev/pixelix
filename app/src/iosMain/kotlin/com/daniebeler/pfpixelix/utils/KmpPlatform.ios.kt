package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreServices.UTTypeCopyPreferredTagWithClass
import platform.CoreServices.UTTypeCreatePreferredIdentifierForTag
import platform.CoreServices.kUTTagClassFilenameExtension
import platform.CoreServices.kUTTagClassMIMEType
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.NSURL
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
actual fun KmpContext.getMimeType(uri: KmpUri): String {
    val fileExtension = uri.url.pathExtension()
    @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
    val fileExtensionRef = CFBridgingRetain(fileExtension as NSString) as CFStringRef
    val uti = UTTypeCreatePreferredIdentifierForTag(
        kUTTagClassFilenameExtension,
        fileExtensionRef,
        null
    )
    CFRelease(fileExtensionRef)
    val mimeType = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)
    CFRelease(uti)
    return CFBridgingRelease(mimeType) as String
}
