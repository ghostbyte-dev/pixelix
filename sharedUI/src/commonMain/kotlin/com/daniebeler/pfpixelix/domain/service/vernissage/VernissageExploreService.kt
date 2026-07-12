package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import kotlin.collections.emptyList

@Inject
class VernissageExploreService(
    private val prefs: UserPreferences, private val api: VernissageApi
) : ExploreService {
    override fun getTrendingAccounts(range: TrendingRange, maxId: String?) = loadVernissagePaginatedListResources {
        api.getTrendingUsers(range.toApiString(), maxId = maxId)
    }

    override fun getTrendingPosts(range: TrendingRange, maxId: String?) =
        loadVernissagePaginatedListResources {
            api.getTrendingPosts(range.toApiString(), maxId = maxId)
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun search(searchText: String, type: String?, limit: Int) = loadResource {
        if (type == null) {
            coroutineScope {
                val accountsDeferred = async { api.getSearch(searchText, "accounts") }
                val hashtagsDeferred = async { api.getSearch(searchText, "hashtags") }

                val accountsDto = accountsDeferred.await().users
                val hashtagsDto = hashtagsDeferred.await().tags

                Search(
                    accounts = accountsDto.map { it.toDomain() },
                    tags = hashtagsDto.map { it.toDomain() },
                    posts = emptyList()
                )
            }
        } else {
            api.getSearch(searchText, type).toDomain()
        }
    }

    override fun searchLocations(searchText: String, countryCode: String?) = loadListResources {
        api.getLocations(countryCode,searchText).map { it.toDomain() }
    }

    override fun getAllCountries(): Flow<Resource<List<Country>>> = loadResource {
        api.getCountries().map { it.toDomain() }
    }

    override fun getTrendingHashtags(range: TrendingRange, maxId: String?) = loadVernissagePaginatedListResources {
        api.getTrendingHashtags(range.toApiString(), maxId)
    }

    override fun getFollowedHashtags() = loadListResources {
        api.getFollowedHashtags().map { it.toDomain() }.map { it.copy(following = true) }
    }

    override fun getRelatedHashtags(hashtag: String) = loadListResources {
        //api.getRelatedHashtags(hashtag).map { it.toDomain() }
        emptyList<RelatedHashtag>()
    }

    override fun getHashtag(hashtag: String) = loadResource {
        coroutineScope {
            val followedDeferred = async { api.getFollowedHashtags() }
            val searchDeferred = async { api.getSearch(hashtag, "hashtags") }

            val followedHashtags = followedDeferred.await()
            val searchHashtags = searchDeferred.await().tags

            val count = searchHashtags.find { it.name == hashtag }?.amount ?: 0
            val isFollowed = followedHashtags.find { it.name == hashtag } != null

            Tag(
                name = hashtag,
                url = "",
                following = isFollowed,
                id = "",
                postsCount = count,
                hashtag = null
            )
        }
    }

    override fun followHashtag(tagId: String) = loadResource {
        api.followHashtag(tagId).toDomain()
    }

    override fun unfollowHashtag(tagId: String) = loadResource {
        api.unfollowHashtag(tagId)
    }
}