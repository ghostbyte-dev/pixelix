package com.daniebeler.pfpixelix.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import coil3.PlatformContext
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.uri

actual typealias KmpUri = Uri
actual val EmptyKmpUri: KmpUri = Uri.EMPTY
actual fun KmpUri.getPlatformUriObject(): Any = this
actual fun String.toKmpUri(): KmpUri = this.toUri()
actual fun PlatformFile.toKmpUri(): KmpUri = this.uri
actual fun KmpUri.toPlatformFile(): PlatformFile = PlatformFile(this)

actual typealias KmpContext = Context
actual val KmpContext.coilContext: PlatformContext get() = this
actual fun KmpContext.getMimeType(uri: KmpUri): String = when (uri.scheme) {
    ContentResolver.SCHEME_FILE -> {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
    }

    ContentResolver.SCHEME_CONTENT -> {
        contentResolver.getType(uri)
    }

    else -> null
} ?: "image/*"
