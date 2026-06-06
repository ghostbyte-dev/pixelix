package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.CollectionService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedCollectionService(
    private val api: PixelfedApi
): CollectionService {

    override fun getCollections(userId: String, page: Int) = loadListResources {
        api.getCollectionsByUserId(userId, page).map { it.toDomain() }
    }

    override fun getCollection(collectionId: String) = loadResource {
        api.getCollection(collectionId).toDomain()
    }

    override fun getPostsOfCollection(collectionId: String, page: Int) = loadListResources {
        api.getPostsOfCollection(collectionId, page).map { it.toDomain() }
    }

    override fun removePostOfCollection(collectionId: String, postId: String) = loadResource {
        api.removePostOfCollection(collectionId, postId)
    }

    override fun addPostOfCollection(collectionId: String, postId: String) = loadResource {
        api.addPostOfCollection(collectionId, postId)
    }

    override fun updateCollection(
        collectionId: String,
        title: String,
        description: String,
        visibility: String
    ) = loadResource {
        api.updateCollection(collectionId, title, description, visibility).toDomain()
    }
}