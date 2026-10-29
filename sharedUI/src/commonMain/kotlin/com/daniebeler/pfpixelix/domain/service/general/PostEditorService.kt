package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.model.request.NewPostRequest
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedPostEditorService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissagePostEditorService
import com.daniebeler.pfpixelix.utils.KmpUri
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

interface PostEditorService {

    fun uploadMedia(uri: KmpUri): Flow<Resource<MediaAttachment>>

    fun updateMedia(id: String, metadata: MediaAttachmentMetadataRequest): Flow<Resource<Unit>>

    fun createPost(createPostDto: NewPostRequest): Flow<Resource<Post>>

    fun updatePost(postId: String, updatePostDto: NewPostRequest): Flow<Resource<Unit>>

    fun deletePost(postId: String): Flow<Resource<Unit>>
}

@Inject
@SingleIn(AppScope::class)
class PostEditorServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedPostEditorService,
    private val vernissage: VernissagePostEditorService
) : PostEditorService {

    private val current: PostEditorService
        get() = when (session.backendType.value) {
             BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun uploadMedia(
        uri: KmpUri
    ): Flow<Resource<MediaAttachment>> = current.uploadMedia(uri)

    override fun updateMedia(
        id: String, metadata: MediaAttachmentMetadataRequest
    ) = current.updateMedia(id, metadata)

    override fun createPost(createPostDto: NewPostRequest): Flow<Resource<Post>> =
        current.createPost(createPostDto)

    override fun updatePost(
        postId: String, updatePostDto: NewPostRequest
    ): Flow<Resource<Unit>> = current.updatePost(postId, updatePostDto)

    override fun deletePost(postId: String): Flow<Resource<Unit>> = current.deletePost(postId)

}