package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.NewPost
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.UpdatePost
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedPostEditorService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.utils.KmpUri
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface PostEditorService {

    fun uploadMedia(uri: KmpUri, description: String): Flow<Resource<MediaAttachment>>

    fun updateMedia(id: String, description: String): Flow<Resource<MediaAttachment>>

    fun createPost(createPostDto: NewPost): Flow<Resource<Post>>

    fun updatePost(postId: String, updatePostDto: UpdatePost): Flow<Resource<Unit>>

    fun deletePost(postId: String): Flow<Resource<Post>>
}

@Inject
@AppSingleton
class PostEditorServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedPostEditorService,
    //private val vernissage: VernissageTimelineService
) : PostEditorService {

    private val current: PostEditorService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun uploadMedia(
        uri: KmpUri, description: String
    ): Flow<Resource<MediaAttachment>> = current.uploadMedia(uri, description)

    override fun updateMedia(
        id: String, description: String
    ): Flow<Resource<MediaAttachment>> = current.updateMedia(id, description)

    override fun createPost(createPostDto: NewPost): Flow<Resource<Post>> =
        current.createPost(createPostDto)

    override fun updatePost(
        postId: String, updatePostDto: UpdatePost
    ): Flow<Resource<Unit>> = current.updatePost(postId, updatePostDto)

    override fun deletePost(postId: String): Flow<Resource<Post>> = current.deletePost(postId)

}