package com.daniebeler.pfpixelix.domain.service.file

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import coil3.disk.DiskCache
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.domain.repository.serializers.SavedSearchesSerializer
import com.daniebeler.pfpixelix.utils.io
import io.github.vinceglb.filekit.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

@Inject
actual class FileService actual constructor(
    @AppComponent.SimpleClient httpClient: HttpClient
) {

    actual companion object {
        private val dataStore = FileKit.filesDir.resolve("datastore")
        private val imageCache = FileKit.filesDir.resolve("image_cache")

        actual fun createPreferences(name: String): DataStore<Preferences> {
            return PreferenceDataStoreFactory.createWithPath(
                corruptionHandler = null,
                migrations = emptyList(),
                produceFile = {
                    dataStore.path.toPath().resolve(name)
                },
            )
        }
        actual fun <T> createDataStore(name: String, serializer: OkioSerializer<T>): DataStore<T> {
            return DataStoreFactory.create(
                storage = OkioStorage(
                    fileSystem = FileSystem.SYSTEM,
                    producePath = {
                        dataStore.path.toPath().resolve(name)
                    },
                    serializer = serializer,
                )
            )
        }

        actual fun createDiskCache(): DiskCache? {
            return DiskCache.Builder()
                .maxSizeBytes(50L * 1024L * 1024L)
                .directory(imageCache.path.toPath())
                .build()
        }
    }

    private val client = httpClient.config { followRedirects = true }

    actual suspend fun getCacheSizeInBytes(): Long = imageCache.sizeRecursively()
    actual suspend fun cleanCache() {
        imageCache.deleteRecursively()
    }

    actual suspend fun download(url: String) {
        with(Dispatchers.io) {
            val bytes = client.get(url).bodyAsBytes()
            val name = url.substringAfterLast('/')
            Logger.d { "Downloading: $name" }
            FileKit.saveImageToGallery(bytes, name)
        }
    }

    actual fun getMimeType(file: PlatformFile): String = file.mimeType().toString()

    actual fun exists(file: PlatformFile): Boolean = file.exists()

    actual suspend fun createTempFile(fileName: String, bytes: ByteArray): PlatformFile {
        return FileKit.cacheDir.resolve(fileName).also {
            it.write(bytes)
        }
    }

    actual suspend fun compressImage(
        bytes: ByteArray,
        quality: Int,
        maxWidth: Int,
        maxHeight: Int,
        imageFormat: ImageFormat
    ): ByteArray = FileKit.compressImage(bytes, imageFormat, quality, maxWidth, maxHeight)

    private fun PlatformFile.sizeRecursively(): Long {
        return when {
            !exists() -> 0L
            isRegularFile() -> size()
            else -> list().sumOf { it.sizeRecursively() }
        }
    }

    private suspend fun PlatformFile.deleteRecursively() {
        when {
            !exists() -> {
                return
            }
            isRegularFile() -> {
                delete(false)
            }
            else -> {
                list().forEach { it.deleteRecursively() }
                delete(false)
            }
        }
    }
}