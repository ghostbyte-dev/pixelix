package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.model.request.NewPostRequest
import com.daniebeler.pfpixelix.domain.model.request.toPixelfed
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedPostEditorService(
    private val api: PixelfedApi, private val fileService: FileService, private val json: Json
) : PostEditorService {

    override fun uploadMedia(uri: KmpUri) = loadResource {
        val file = PlatformFile(uri)
        if (!fileService.exists(file)) error("File doesn't exist")
        val bytes = file.readBytes()
        val mimeType = fileService.getMimeType(file)
        val thumbnail = if (mimeType.startsWith("image")) {
            fileService.compressImage(
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
        api.updateMedia(id, metadata.description ?: "")
    }

    override fun createPost(createPostDto: NewPostRequest) = loadResource {
        api.createPost(createPostDto.toPixelfed()).toDomain()
    }

    override fun updatePost(postId: String, updatePostDto: NewPostRequest) = loadResource {
        api.updatePost(postId, updatePostDto.toPixelfed())
    }

    override fun deletePost(postId: String) = loadResource {
        api.deletePost(postId).toDomain()
    }
}