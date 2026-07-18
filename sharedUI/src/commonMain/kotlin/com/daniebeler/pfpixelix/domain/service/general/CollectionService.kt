package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Collection
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAccountService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedCollectionService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject


interface CollectionService {

    fun getCollections(userId: String, page: Int): Flow<Resource<List<Collection>>>

    fun getCollection(collectionId: String): Flow<Resource<Collection>>

    fun getPostsOfCollection(collectionId: String, page: Int = 1): Flow<Resource<List<Post>>>

    fun removePostOfCollection(collectionId: String, postId: String): Flow<Resource<String>>

    fun addPostOfCollection(collectionId: String, postId: String): Flow<Resource<String>>

    fun updateCollection(
        collectionId: String,
        title: String,
        description: String,
        visibility: String
    ): Flow<Resource<Collection>>
}

@Inject
@AppSingleton
class CollectionServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedCollectionService,
    //private val vernissage: VernissageTimelineService
) : CollectionService {

    private val current: CollectionService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getCollections(
        userId: String,
        page: Int
    ): Flow<Resource<List<Collection>>> = current.getCollections(userId, page)

    override fun getCollection(collectionId: String): Flow<Resource<Collection>> = current.getCollection(collectionId)

    override fun getPostsOfCollection(
        collectionId: String,
        page: Int
    ): Flow<Resource<List<Post>>> = current.getPostsOfCollection(collectionId, page)

    override fun removePostOfCollection(
        collectionId: String,
        postId: String
    ): Flow<Resource<String>> = current.removePostOfCollection(collectionId,postId)

    override fun addPostOfCollection(
        collectionId: String,
        postId: String
    ): Flow<Resource<String>> = current.addPostOfCollection(collectionId, postId)

    override fun updateCollection(
        collectionId: String,
        title: String,
        description: String,
        visibility: String
    ): Flow<Resource<Collection>> = current.updateCollection(collectionId, title, description, visibility)
}