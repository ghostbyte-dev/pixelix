package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Place
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedExploreService
import com.daniebeler.pfpixelix.domain.service.session.Session
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject


interface ExploreService {
    fun getTrendingAccounts(): Flow<Resource<List<Account>>>

    fun getRelationships(userIds: List<String>): Flow<Resource<List<Relationship>>>

    fun search(searchText: String, type: String? = null, limit: Int = 5): Flow<Resource<Search>>

    fun searchLocations(searchText: String): Flow<Resource<List<Place>>>

    fun getTrendingHashtags(): Flow<Resource<List<Tag>>>

    fun getFollowedHashtags(): Flow<Resource<List<Tag>>>

    fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>>

    fun getHashtag(hashtag: String): Flow<Resource<Tag>>

    fun followHashtag(tagId: String): Flow<Resource<Tag>>

    fun unfollowHashtag(tagId: String): Flow<Resource<Tag>>
}


@Inject
@AppSingleton
class ExploreServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedExploreService,
    //private val vernissage: VernissageTimelineService
) : ExploreService {

    private val current: ExploreService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getTrendingAccounts(): Flow<Resource<List<Account>>> = current.getTrendingAccounts()

    override fun getRelationships(userIds: List<String>): Flow<Resource<List<Relationship>>> = current.getRelationships(userIds)

    override fun search(
        searchText: String,
        type: String?,
        limit: Int
    ): Flow<Resource<Search>> = current.search(searchText, type, limit)

    override fun searchLocations(searchText: String): Flow<Resource<List<Place>>> = current.searchLocations(searchText)

    override fun getTrendingHashtags(): Flow<Resource<List<Tag>>> = current.getTrendingHashtags()

    override fun getFollowedHashtags(): Flow<Resource<List<Tag>>> = current.getFollowedHashtags()

    override fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>> = current.getRelatedHashtags(hashtag)

    override fun getHashtag(hashtag: String): Flow<Resource<Tag>> = current.getHashtag(hashtag)

    override fun followHashtag(tagId: String): Flow<Resource<Tag>> = current.followHashtag(tagId)

    override fun unfollowHashtag(tagId: String): Flow<Resource<Tag>> = current.unfollowHashtag(tagId)

}