package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject
import kotlin.collections.emptyList

@Inject
class VernissageExploreService(
    private val prefs: UserPreferences,
    private val api: VernissageApi
): ExploreService {
    override fun getTrendingAccounts(range: String) = loadVernissagePaginatedListResources {
        api.getTrendingUsers(range)
    }

    override fun getTrendingPosts(range: String, maxId: String?) =
        loadVernissagePaginatedListResources {
            api.getTrendingPosts(range, maxId = maxId)
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun search(searchText: String, type: String?, limit: Int)= loadResource {
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

    override fun searchLocations(searchText: String) = loadListResources {
        //api.searchLocations(searchText).map { it.toDomain() }
        emptyList<Location>()
    }

    override fun getTrendingHashtags(range: String) = loadVernissagePaginatedListResources {
        api.getTrendingHashtags(range)
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
            val followedDeferred = async {api.getFollowedHashtags()}
            val searchDeferred = async {api.getSearch(hashtag, "hashtags")}

            val followedHashtags = followedDeferred.await()
            val searchHashtags = searchDeferred.await().tags

            val count = searchHashtags.find { it.name == hashtag }?.amount ?: 0
            val isFollowed = followedHashtags.find { it.name == hashtag } != null

            Tag(name = hashtag, url = "", following = isFollowed, id = "", postsCount = count, hashtag = null)
        }
    }

    override fun followHashtag(tagId: String) = loadResource {
        api.followHashtag(tagId).toDomain()
    }

    override fun unfollowHashtag(tagId: String) = loadResource {
        api.unfollowHashtag(tagId)
    }
}