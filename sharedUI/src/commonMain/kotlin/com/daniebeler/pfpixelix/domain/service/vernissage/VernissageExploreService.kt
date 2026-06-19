package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Place
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
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
        emptyList<Place>()
    }

    override fun getTrendingHashtags(range: String) = loadVernissagePaginatedListResources {
        api.getTrendingHashtags(range)
    }

    override fun getFollowedHashtags() = loadListResources {
        //api.getFollowedHashtags().map { it.toDomain() }
        emptyList<Tag>()
    }

    override fun getRelatedHashtags(hashtag: String) = loadListResources {
        //api.getRelatedHashtags(hashtag).map { it.toDomain() }
        emptyList<RelatedHashtag>()
    }

    val emptyTag = Tag(
        name = "",
        url = "",
        following = false,
        count = 0,
        total = 0,
        hashtag = "#",
        id = ""
    )

    override fun getHashtag(hashtag: String) = loadResource {
        //api.getHashtag(hashtag).toDomain()
        emptyTag
    }

    override fun followHashtag(tagId: String) = loadResource {
        //api.followHashtag(tagId).toDomain()
        emptyTag
    }

    override fun unfollowHashtag(tagId: String) = loadResource {
        //api.unfollowHashtag(tagId).toDomain()
        emptyTag
    }
}