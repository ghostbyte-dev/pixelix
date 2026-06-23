package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedExploreService(
    private val prefs: UserPreferences,
    private val api: PixelfedApi
): ExploreService {
    override fun getTrendingAccounts(range: String) = loadPaginatedListResources {
        api.getTrendingAccounts().map { it.toDomain() }
    }

    override fun getTrendingPosts(range: TrendingRange, maxId: String?) = loadPaginatedListResources {
        if (maxId == null) {
            api.getTrendingPosts(range.toApiString()).map { it.toDomain() }
        } else {
            emptyList()
        }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun search(searchText: String, type: String?, limit: Int) = loadResource {
        api.getSearch(searchText, type, limit).toDomain()
    }

    override fun searchLocations(searchText: String) = loadListResources {
        api.searchLocations(searchText).map { it.toDomain() }
    }

    override fun getTrendingHashtags(range: String) = loadPaginatedListResources {
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
        api.unfollowHashtag(tagId)
        Unit
    }
}