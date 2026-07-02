package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.NewPost
import com.daniebeler.pfpixelix.domain.model.UpdatePost
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
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
class PixelfedPostEditorService(
    private val api: PixelfedApi, private val fileService: FileService, private val json: Json
) : PostEditorService {

    override fun uploadMedia(uri: KmpUri) = loadResource {
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
                append("description", "")
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
            })

        api.uploadMedia(data).toDomain()
    }

    override fun updateMedia(id: String, metadata: MediaAttachmentMetadataRequest) = loadResource {
        api.updateMedia(id, metadata.description ?: "").toDomain()
    }

    override fun createPost(createPostDto: NewPost) = loadResource {
        api.createPost(json.encodeToString(createPostDto)).toDomain()
    }

    override fun updatePost(postId: String, updatePostDto: UpdatePost) = loadResource {
        api.updatePost(postId, json.encodeToString(updatePostDto))
    }

    override fun deletePost(postId: String) = loadResource {
        api.deletePost(postId).toDomain()
    }
}