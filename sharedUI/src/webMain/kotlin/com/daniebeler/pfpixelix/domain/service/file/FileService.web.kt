@file:OptIn(ExperimentalWasmJsInterop::class)

package com.daniebeler.pfpixelix.domain.service.file

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.core.okio.WebLocalStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import coil3.disk.DiskCache
import com.daniebeler.pfpixelix.di.AppComponent
import io.github.vinceglb.filekit.BrowserFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.WebFile
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.utils.toJsArray
import io.ktor.client.HttpClient
import kotlinx.coroutines.await
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.js.Promise

@Inject
actual class FileService actual constructor(
    @AppComponent.SimpleClient httpClient: HttpClient
) {
    actual companion object {

        actual fun createPreferences(name: String): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                storage = WebLocalStorage(
                    name = name,
                    serializer = PreferencesSerializer,
                )
            )
        }
        actual fun <T> createDataStore(name: String, serializer: OkioSerializer<T>): DataStore<T> {
            return DataStoreFactory.create(
                storage = WebLocalStorage(
                    name = name,
                    serializer = serializer,
                )
            )
        }

        actual fun createDiskCache(): DiskCache? = null
    }

    actual suspend fun getCacheSizeInBytes(): Long {
        //TODO
        return 0L
    }

    actual suspend fun cleanCache() {
        //TODO
    }

    actual suspend fun download(url: String) {
        openInNewTab(url)
    }

    actual fun getMimeType(file: PlatformFile): String = file.mimeType().toString()

    // A picked file is a live in-memory blob; it is always present.
    actual fun exists(file: PlatformFile): Boolean = true

    actual suspend fun createTempFile(fileName: String, bytes: ByteArray): PlatformFile {
        val file = createInMemoryFile(
            fileBits = bytes.toJsArray(),
            fileName = fileName,
            mimeType = mimeTypeFromFileName(fileName)
        ).unsafeCast<BrowserFile>()
        return PlatformFile(WebFile.FileWrapper(file))
    }

    actual suspend fun compressImage(
        bytes: ByteArray,
        quality: Int,
        maxWidth: Int,
        maxHeight: Int,
        imageFormat: ImageFormat
    ): ByteArray {
        val compressed = compressImageInBrowser(
            fileBits = bytes.toJsArray(),
            mimeType = imageFormat.toMimeType(),
            quality = quality.coerceIn(0, 100) / 100.0,
            maxWidth = maxWidth,
            maxHeight = maxHeight
        ).await()!!.unsafeCast<BrowserFile>()
        return PlatformFile(WebFile.FileWrapper(compressed)).readBytes()
    }
}

private fun ImageFormat.toMimeType(): String = when (this) {
    ImageFormat.JPEG -> "image/jpeg"
    ImageFormat.PNG -> "image/png"
}

private fun mimeTypeFromFileName(fileName: String): String =
    when (fileName.substringAfterLast('.', "").lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        else -> "application/octet-stream"
    }

private fun openInNewTab(url: String) {
    js("window.open(url, '_blank')")
}

/** Wraps [fileBits] in a browser `File` (an in-memory blob), tagged with [mimeType]. */
private fun createInMemoryFile(
    fileBits: JsArray<JsAny?>,
    fileName: String,
    mimeType: String
): JsAny = js("new File(fileBits, fileName, { type: mimeType })")

/**
 * Decodes [fileBits] into an `<img>`, redraws it onto a `<canvas>` scaled to fit
 * [maxWidth]/[maxHeight] (keeping the aspect ratio, never upscaling) and re-encodes it via
 * `canvas.toBlob`. Resolves with the resulting browser `File`; [quality] is in the 0.0..1.0 range.
 */
private fun compressImageInBrowser(
    fileBits: JsArray<JsAny?>,
    mimeType: String,
    quality: Double,
    maxWidth: Int,
    maxHeight: Int
): Promise<JsAny?> = js(
    """{
        return new Promise(function (resolve, reject) {
            var url = URL.createObjectURL(new Blob(fileBits, { type: mimeType }));
            var img = new Image();
            img.onload = function () {
                var scale = Math.min(maxWidth / img.width, maxHeight / img.height, 1);
                var w = Math.max(1, Math.round(img.width * scale));
                var h = Math.max(1, Math.round(img.height * scale));
                var canvas = document.createElement('canvas');
                canvas.width = w;
                canvas.height = h;
                canvas.getContext('2d').drawImage(img, 0, 0, w, h);
                URL.revokeObjectURL(url);
                canvas.toBlob(function (blob) {
                    if (blob === null) {
                        reject(new Error('canvas.toBlob returned null'));
                    } else {
                        resolve(new File([blob], 'compressed', { type: mimeType }));
                    }
                }, mimeType, quality);
            };
            img.onerror = function () {
                URL.revokeObjectURL(url);
                reject(new Error('Failed to decode image for compression'));
            };
            img.src = url;
        });
    }"""
)