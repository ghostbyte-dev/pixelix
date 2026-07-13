package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.WebFile
import io.github.vinceglb.filekit.path
import org.w3c.dom.url.URL

/**
 * Browser URIs. Unlike the desktop target there is no `java.net.URI` and no reconstructable file
 * path — a picked file only exists as a live [PlatformFile] (a `WebFile`). We therefore keep the
 * string form (used for remote URLs and coil models) and, when the URI originates from a picked
 * file, a reference to that [PlatformFile] so it can be recovered for uploads.
 */
private class WebKmpUri(
    val value: String,
    val file: PlatformFile? = null
) : KmpUri() {
    override fun toString(): String = value
}

actual abstract class KmpUri {
    actual abstract override fun toString(): String
}

actual val EmptyKmpUri: KmpUri = WebKmpUri("")

actual fun KmpUri.getPlatformUriObject(): Any {
    val web = this as WebKmpUri
    return web.value
}

actual fun String.toKmpUri(): KmpUri = WebKmpUri(this)

/**
 * A picked/created file on web lives only as an in-memory browser blob; its [path] is merely a file
 * name and cannot be loaded by the browser. We therefore expose it through an object URL
 * (`blob:…`) so Coil / `<img>` can render it, while keeping the backing [PlatformFile] so the bytes
 * can still be recovered for uploads via [toPlatformFile].
 */
actual fun PlatformFile.toKmpUri(): KmpUri {
    val displayUrl = when (val webFile = webFile) {
        is WebFile.FileWrapper -> URL.createObjectURL(webFile.file)
        is WebFile.DirectoryWrapper -> path
    }
    return WebKmpUri(value = displayUrl, file = this)
}

actual fun KmpUri.toPlatformFile(): PlatformFile =
    (this as WebKmpUri).file
        ?: error("KmpUri without a backing PlatformFile cannot be converted to a file on web: $this")

actual abstract class KmpContext
actual val KmpContext.coilContext: PlatformContext get() = PlatformContext.INSTANCE

actual fun parseExifMetadata(bytes: ByteArray): MediaAttachmentMetadataRequest {
    // No EXIF parser on web yet; mirror the desktop behaviour and return empty metadata.
    return MediaAttachmentMetadataRequest()
}
