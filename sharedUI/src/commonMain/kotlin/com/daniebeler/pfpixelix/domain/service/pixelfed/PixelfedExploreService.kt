package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedExploreService(
    private val api: PixelfedApi
): ExploreService {
    override fun getTrendingAccounts() = loadListResources {
        api.getTrendingAccounts().map { it.toDomain() }
    }

    override fun getRelationships(userIds: List<String>) = loadListResources {
        api.getRelationships(userIds).map { it.toDomain() }
    }

    override fun search(searchText: String, type: String?, limit: Int) = loadResource {
        api.getSearch(searchText, type, limit).toDomain()
    }

    override fun searchLocations(searchText: String) = loadListResources {
        api.searchLocations(searchText).map { it.toDomain() }
    }

    override fun getTrendingHashtags() = loadListResources {
        api.getTrendingHashtags().map { it.toDomain() }
    }

    override fun getFollowedHashtags() = loadListResources {
        api.getFollowedHashtags().map { it.toDomain() }
    }

    override fun getRelatedHashtags(hashtag: String) = loadListResources {
        api.getRelatedHashtags(hashtag).map { it.toDomain() }
    }

    override fun getHashtag(hashtag: String) = loadResource {
        api.getHashtag(hashtag).toDomain()
    }

    override fun followHashtag(tagId: String) = loadResource {
        api.followHashtag(tagId).toDomain()
    }

    override fun unfollowHashtag(tagId: String) = loadResource {
        api.unfollowHashtag(tagId).toDomain()
    }
}