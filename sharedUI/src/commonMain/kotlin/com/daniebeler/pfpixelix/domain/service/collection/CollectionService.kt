package com.daniebeler.pfpixelix.domain.service.collection

import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class CollectionService(
    private val api: PixelfedApi
) {

    fun getCollections(userId: String, page: Int) = loadListResources {
        api.getCollectionsByUserId(userId, page)
    }

    fun getCollection(collectionId: String) = loadResource {
        api.getCollection(collectionId)
    }

    fun getPostsOfCollection(collectionId: String, page: Int = 1) = loadListResources {
        api.getPostsOfCollection(collectionId, page)
    }

    fun removePostOfCollection(collectionId: String, postId: String) = loadResource {
        api.removePostOfCollection(collectionId, postId)
    }

    fun addPostOfCollection(collectionId: String, postId: String) = loadResource {
        api.addPostOfCollection(collectionId, postId)
    }

    fun updateCollection(
        collectionId: String,
        title: String,
        description: String,
        visibility: String
    ) = loadResource {
        api.updateCollection(collectionId, title, description, visibility)
    }
}