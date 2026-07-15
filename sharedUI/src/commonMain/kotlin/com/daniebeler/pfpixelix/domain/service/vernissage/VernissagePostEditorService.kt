package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.model.request.NewPostRequest
import com.daniebeler.pfpixelix.domain.model.request.toVernissage
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.file.FileService
import com.daniebeler.pfpixelix.domain.service.file.PlatformFile
import com.daniebeler.pfpixelix.domain.service.general.PostEditorService
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import com.daniebeler.pfpixelix.utils.BlurHashEncoder
import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class VernissagePostEditorService(
    private val api: VernissageApi, private val fileService: FileService, private val json: Json
) : PostEditorService {

    override fun uploadMedia(uri: KmpUri) = loadResource {
        val file = PlatformFile(uri)
        if (!fileService.exists(file)) error("File doesn't exist")
        val bytes = file.readBytes()
        val mimeType = fileService.getMimeType(file)
        val data = MultiPartFormDataContent(
            parts = formData {
                append("description", "")
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=${file.nameWithoutExtension}")
                })
            })
        api.uploadMedia(data).toDomain()
    }

    override fun updateMedia(id: String, metadata: MediaAttachmentMetadataRequest) = loadResource {
        api.updateMedia(id, metadata.toVernissage())
    }

    override fun createPost(createPostDto: NewPostRequest) = loadResource {
        api.createPost(createPostDto.toVernissage()).toDomain()
    }

    override fun updatePost(postId: String, updatePostDto: NewPostRequest) = loadResource {
        api.updatePost(postId, updatePostDto.toVernissage())
    }

    override fun deletePost(postId: String) = loadResource {
        api.deletePost(postId).toDomain()
    }
}