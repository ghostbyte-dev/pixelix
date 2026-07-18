package com.daniebeler.pfpixelix.domain.service.file

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.preferences.core.Preferences
import coil3.disk.DiskCache
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toPlatformFile
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.ktor.client.*
import me.tatarka.inject.annotations.Inject


/**
 * Filesystem / media helper. The disk-backed operations differ per platform: JVM/Android/iOS use
 * the okio filesystem via FileKit, whereas the browser (wasmJs) has no filesystem and no image
 * codecs, so its [actual] either no-ops (cache) or throws (compression/writing) for the
 * post-editing paths that are disabled on web anyway. See the `nonWebMain` / `wasmJsMain` actuals.
 */
@Inject
expect class FileService(@AppComponent.SimpleClient httpClient: HttpClient) {
    companion object {
        fun createPreferences(name: String): DataStore<Preferences>
        fun <T> createDataStore(name: String, serializer: OkioSerializer<T>): DataStore<T>
        fun createDiskCache(): DiskCache?
    }

    suspend fun getCacheSizeInBytes(): Long
    suspend fun cleanCache()

    suspend fun download(url: String)
    fun getMimeType(file: PlatformFile): String

    fun exists(file: PlatformFile): Boolean

    suspend fun createTempFile(fileName: String, bytes: ByteArray): PlatformFile

    suspend fun compressImage(
        bytes: ByteArray,
        quality: Int,
        maxWidth: Int,
        maxHeight: Int,
        imageFormat: ImageFormat
    ): ByteArray
}

internal fun PlatformFile(kmpUri: KmpUri): PlatformFile = kmpUri.toPlatformFile()
