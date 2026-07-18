package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import io.github.vinceglb.filekit.PlatformFile

expect abstract class KmpUri {
    abstract override fun toString(): String
}
expect val EmptyKmpUri: KmpUri
expect fun KmpUri.getPlatformUriObject(): Any
expect fun String.toKmpUri(): KmpUri
expect fun PlatformFile.toKmpUri(): KmpUri
expect fun KmpUri.toPlatformFile(): PlatformFile
expect fun parseExifMetadata(bytes: ByteArray): MediaAttachmentMetadataRequest
expect abstract class KmpContext

expect val KmpContext.coilContext: PlatformContext
