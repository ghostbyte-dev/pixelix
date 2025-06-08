package com.daniebeler.pfpixelix.domain.service.file

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.getMimeType
import com.daniebeler.pfpixelix.utils.toKmpUri
import com.daniebeler.pfpixelix.utils.toPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.isRegularFile
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.resolve
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.size
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Inject
import okio.Path
import okio.Path.Companion.toPath

@Inject
class FileService(
    private val context: KmpContext,
    private val httpClient: HttpClient
) {
    companion object {
        val dataStoreDir = FileKit.filesDir.resolve("datastore")
        val imageCacheDir = FileKit.cacheDir.resolve("image_cache")
    }
    private val client = httpClient.config { followRedirects = true }

    suspend fun getCacheSizeInBytes(): Long = imageCacheDir.sizeRecursively()
    suspend fun cleanCache() {
        imageCacheDir.deleteRecursively()
    }

    suspend fun download(url: String) {
        with(Dispatchers.IO) {
            val bytes = client.get(url).bodyAsBytes()
            val name = url.substringAfterLast('/')
            Logger.d { "Downloading: $name" }
            FileKit.saveImageToGallery(bytes, name)
        }
    }

    fun getMimeType(file: PlatformFile): String = context.getMimeType(file.toKmpUri())

    private suspend fun PlatformFile.sizeRecursively(): Long {
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

internal fun PlatformFile(kmpUri: KmpUri): PlatformFile = kmpUri.toPlatformFile()
internal fun PlatformFile.toOkIoPath(): Path = path.toPath()
