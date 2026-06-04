package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedExploreService(
    private val api: PixelfedApi
): ExploreService {
    override fun getTrendingAccounts() = loadListResources {
        api.getTrendingAccounts()
    }

    override fun getRelationships(userIds: List<String>) = loadListResources {
        api.getRelationships(userIds)
    }

    override fun search(searchText: String, type: String?, limit: Int) = loadResource {
        api.getSearch(searchText, type, limit)
    }

    override fun searchLocations(searchText: String) = loadListResources {
        api.searchLocations(searchText)
    }

    override fun getTrendingHashtags() = loadListResources {
        api.getTrendingHashtags()
    }

    override fun getFollowedHashtags() = loadListResources {
        api.getFollowedHashtags()
    }

    override fun getRelatedHashtags(hashtag: String) = loadListResources {
        api.getRelatedHashtags(hashtag)
    }

    override fun getHashtag(hashtag: String) = loadResource {
        api.getHashtag(hashtag)
    }

    override fun followHashtag(tagId: String) = loadResource {
        api.followHashtag(tagId)
    }

    override fun unfollowHashtag(tagId: String) = loadResource {
        api.unfollowHashtag(tagId)
    }
}