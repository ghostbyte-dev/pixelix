package com.daniebeler.pfpixelix.domain.service.editor

import com.daniebeler.pfpixelix.domain.model.NewPost
import com.daniebeler.pfpixelix.domain.model.UpdatePost
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class PostEditorService(
    private val api: PixelfedApi,
    private val fileService: FileService,
    private val json: Json
) {

    fun uploadMedia(uri: KmpUri, description: String) = loadResource {
        val file = PlatformFile(uri)
        if (!file.exists()) error("File doesn't exist")
        val bytes = file.readBytes()
        val mimeType = fileService.getMimeType(file)
        val thumbnail = if (mimeType.startsWith("image")) {
            FileKit.compressImage(
                bytes = bytes,
                quality = 85,
                maxWidth = 400,
                maxHeight = 400,
                imageFormat = ImageFormat.PNG
            )
        } else null

        val data = MultiPartFormDataContent(
            parts = formData {
                append("description", description)
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=${file.nameWithoutExtension}")
                })
                if (thumbnail != null) {
                    append("thumbnail", thumbnail, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=thumbnail")
                        append(HttpHeaders.ContentType, "image/png")
                    })
                }
            }
        )

        api.uploadMedia(data)
    }

    fun updateMedia(id: String, description: String) = loadResource {
        api.updateMedia(id, description)
    }

    fun createPost(createPostDto: NewPost) = loadResource {
        api.createPost(json.encodeToString(createPostDto))
    }

    fun updatePost(postId: String, updatePostDto: UpdatePost) = loadResource {
        api.updatePost(postId, json.encodeToString(updatePostDto))
    }

    fun deletePost(postId: String) = loadResource {
        api.deletePost(postId)
    }
}