package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedExploreService(
    private val prefs: UserPreferences, private val api: PixelfedApi
) : ExploreService {
    override fun getTrendingAccounts(range: TrendingRange, maxId: String?) =
        loadPaginatedListResources {
            if (maxId == null) {
                api.getTrendingAccounts().map { it.toDomain() }
            } else {
                emptyList()
            }
        }

    override fun getTrendingPosts(range: TrendingRange, maxId: String?) =
        loadPaginatedListResources {
            if (maxId == null) {
                api.getTrendingPosts(range.toApiString()).map { it.toDomain() }
            } else {
                emptyList()
            }
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun search(searchText: String, type: String?, limit: Int) = loadResource {
        api.getSearch(searchText, type, limit).toDomain()
    }

    override fun searchLocations(searchText: String, countryCode: String?) = loadListResources {
        api.searchLocations(searchText).map { it.toDomain() }
    }

    override fun getAllCountries(): Flow<Resource<List<Country>>> = loadResource {
        emptyList()
    }

    override fun getTrendingHashtags(range: TrendingRange, maxId: String?) =
        loadPaginatedListResources {
            if (maxId == null) {
                api.getTrendingHashtags().map { it.toDomain() }
            } else {
                emptyList()
            }

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

    override fun getCategories(): Flow<Resource<List<Category>>> = loadListResources {
        emptyList()
    }

    override fun getLicenses(): Flow<Resource<List<License>>> = loadListResources {
        emptyList()
    }
}