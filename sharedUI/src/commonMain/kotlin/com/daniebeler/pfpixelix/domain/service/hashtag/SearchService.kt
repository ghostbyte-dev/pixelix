package com.daniebeler.pfpixelix.domain.service.hashtag

import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class SearchService(
    private val api: PixelfedApi
) {
    fun getTrendingAccounts() = loadListResources {
        api.getTrendingAccounts()
    }

    fun getRelationships(userIds: List<String>) = loadListResources {
        api.getRelationships(userIds)
    }

    fun search(searchText: String, type: String? = null, limit: Int = 5) = loadResource {
        api.getSearch(searchText, type, limit)
    }

    fun searchLocations(searchText: String) = loadListResources {
        api.searchLocations(searchText)
    }

    fun getTrendingHashtags() = loadListResources {
        api.getTrendingHashtags()
    }

    fun getFollowedHashtags() = loadListResources {
        api.getFollowedHashtags()
    }

    fun getRelatedHashtags(hashtag: String) = loadListResources {
        api.getRelatedHashtags(hashtag)
    }

    fun getHashtag(hashtag: String) = loadResource {
        api.getHashtag(hashtag)
    }

    fun followHashtag(tagId: String) = loadResource {
        api.followHashtag(tagId)
    }

    fun unfollowHashtag(tagId: String) = loadResource {
        api.unfollowHashtag(tagId)
    }
}