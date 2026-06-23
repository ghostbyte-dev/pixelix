package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageExploreService
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject


interface ExploreService {
    fun getTrendingAccounts(range: String): Flow<Resource<PaginatedResponse<List<Account>>>>
    fun getTrendingPosts(range: TrendingRange, maxId: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun search(searchText: String, type: String? = null, limit: Int = 5): Flow<Resource<Search>>

    fun searchLocations(searchText: String): Flow<Resource<List<Location>>>

    fun getTrendingHashtags(range: String): Flow<Resource<PaginatedResponse<List<Tag>>>>

    fun getFollowedHashtags(): Flow<Resource<List<Tag>>>

    fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>>

    fun getHashtag(hashtag: String): Flow<Resource<Tag>>

    fun followHashtag(tagId: String): Flow<Resource<Tag>>

    fun unfollowHashtag(tagId: String): Flow<Resource<Unit>>


    fun Flow<Resource<PaginatedResponse<List<Post>>>>.filterSensitive(hideSensitiveContent: Boolean) =
        this.map { event ->
            if (event is Resource.Success<PaginatedResponse<List<Post>>>) {
                val filtered = event.data.data.filter { !(hideSensitiveContent && it.sensitive) }
                Resource.Success(event.data.copy(data = filtered))
            } else {
                event
            }
        }
}


@Inject
@AppSingleton
class ExploreServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedExploreService,
    private val vernissage: VernissageExploreService
) : ExploreService {

    private val current: ExploreService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getTrendingAccounts(range: String): Flow<Resource<PaginatedResponse<List<Account>>>> = current.getTrendingAccounts(range)

    override fun getTrendingPosts(range: TrendingRange, maxId: String?): Flow<Resource<PaginatedResponse<List<Post>>>> =
        current.getTrendingPosts(range, maxId)

    override fun search(
        searchText: String,
        type: String?,
        limit: Int
    ): Flow<Resource<Search>> = current.search(searchText, type, limit)

    override fun searchLocations(searchText: String): Flow<Resource<List<Location>>> = current.searchLocations(searchText)

    override fun getTrendingHashtags(range: String): Flow<Resource<PaginatedResponse<List<Tag>>>> = current.getTrendingHashtags(range)

    override fun getFollowedHashtags(): Flow<Resource<List<Tag>>> = current.getFollowedHashtags()

    override fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>> = current.getRelatedHashtags(hashtag)

    override fun getHashtag(hashtag: String): Flow<Resource<Tag>> = current.getHashtag(hashtag)

    override fun followHashtag(tagId: String): Flow<Resource<Tag>> = current.followHashtag(tagId)

    override fun unfollowHashtag(tagId: String): Flow<Resource<Unit>> = current.unfollowHashtag(tagId)
}