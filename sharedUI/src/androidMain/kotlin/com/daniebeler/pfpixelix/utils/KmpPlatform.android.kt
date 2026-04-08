package com.daniebeler.pfpixelix.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil3.PlatformContext
import io.github.kdroidfilter.composemediaplayer.util.getUri
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import java.io.File

actual typealias KmpUri = Uri
actual val EmptyKmpUri: KmpUri = Uri.EMPTY
actual fun KmpUri.getPlatformUriObject(): Any = this
actual fun String.toKmpUri(): KmpUri = this.toUri()
actual fun PlatformFile.toKmpUri(): KmpUri = this.getUri().toUri()
actual fun KmpUri.toPlatformFile(): PlatformFile = PlatformFile(this)

actual typealias KmpContext = Context
actual val KmpContext.coilContext: PlatformContext get() = this
