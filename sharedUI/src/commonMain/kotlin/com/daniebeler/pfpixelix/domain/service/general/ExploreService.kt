package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Camera
import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.model.Country
import com.daniebeler.pfpixelix.domain.model.Film
import com.daniebeler.pfpixelix.domain.model.Lens
import com.daniebeler.pfpixelix.domain.model.License
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Location
import com.daniebeler.pfpixelix.domain.model.PagePaginatedResponse
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
    fun getTrendingAccounts(range: TrendingRange, maxId: String? = null): Flow<Resource<PaginatedResponse<Account>>>
    fun getTrendingPosts(range: TrendingRange, maxId: String? = null): Flow<Resource<PaginatedResponse<Post>>>

    fun search(searchText: String, type: String? = null, limit: Int = 5): Flow<Resource<Search>>

    fun searchLocations(searchText: String, countryCode: String?): Flow<Resource<List<Location>>>
    fun getAllCountries(): Flow<Resource<List<Country>>>

    fun getTrendingHashtags(range: TrendingRange, maxId: String? = null): Flow<Resource<PaginatedResponse<Tag>>>

    fun getFollowedHashtags(): Flow<Resource<List<Tag>>>

    fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>>

    fun getHashtag(hashtag: String): Flow<Resource<Tag>>

    fun followHashtag(tagId: String): Flow<Resource<Tag>>

    fun unfollowHashtag(tagId: String): Flow<Resource<Unit>>

    fun getEditorsChoicePosts(maxId: String? = null): Flow<Resource<PaginatedResponse<Post>>>
    fun getEditorsChoiceAccounts(maxId: String? = null): Flow<Resource<PaginatedResponse<Account>>>
    fun getCategories(): Flow<Resource<List<Category>>>
    fun getCameras(page: Int = 1, size: Int = 20): Flow<Resource<PagePaginatedResponse<Camera>>>
    fun getLenses(page: Int = 1, size: Int = 20): Flow<Resource<PagePaginatedResponse<Lens>>>
    fun getFilms(page: Int = 1, size: Int = 20): Flow<Resource<PagePaginatedResponse<Film>>>
    fun getLicenses(): Flow<Resource<List<License>>>

    fun Flow<Resource<PaginatedResponse<Post>>>.filterSensitive(hideSensitiveContent: Boolean) =
        this.map { event ->
            if (event is Resource.Success<PaginatedResponse<Post>>) {
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

    override fun getTrendingAccounts(range: TrendingRange, maxId: String?): Flow<Resource<PaginatedResponse<Account>>> = current.getTrendingAccounts(range, maxId)

    override fun getTrendingPosts(range: TrendingRange, maxId: String?): Flow<Resource<PaginatedResponse<Post>>> =
        current.getTrendingPosts(range, maxId)

    override fun search(
        searchText: String,
        type: String?,
        limit: Int
    ): Flow<Resource<Search>> = current.search(searchText, type, limit)

    override fun searchLocations(searchText: String, countryCode: String?): Flow<Resource<List<Location>>> = current.searchLocations(searchText, countryCode)

    override fun getAllCountries(): Flow<Resource<List<Country>>> = current.getAllCountries()

    override fun getTrendingHashtags(range: TrendingRange, maxId: String?): Flow<Resource<PaginatedResponse<Tag>>> = current.getTrendingHashtags(range, maxId)

    override fun getFollowedHashtags(): Flow<Resource<List<Tag>>> = current.getFollowedHashtags()

    override fun getRelatedHashtags(hashtag: String): Flow<Resource<List<RelatedHashtag>>> = current.getRelatedHashtags(hashtag)

    override fun getHashtag(hashtag: String): Flow<Resource<Tag>> = current.getHashtag(hashtag)

    override fun followHashtag(tagId: String): Flow<Resource<Tag>> = current.followHashtag(tagId)

    override fun unfollowHashtag(tagId: String): Flow<Resource<Unit>> = current.unfollowHashtag(tagId)

    override fun getEditorsChoicePosts(maxId: String?): Flow<Resource<PaginatedResponse<Post>>> =
        current.getEditorsChoicePosts(maxId)

    override fun getEditorsChoiceAccounts(maxId: String?): Flow<Resource<PaginatedResponse<Account>>> =
        current.getEditorsChoiceAccounts(maxId)

    override fun getCategories(): Flow<Resource<List<Category>>> = current.getCategories()

    override fun getCameras(page: Int, size: Int): Flow<Resource<PagePaginatedResponse<Camera>>> = current.getCameras(page, size)

    override fun getLenses(page: Int, size: Int): Flow<Resource<PagePaginatedResponse<Lens>>> = current.getLenses(page, size)

    override fun getFilms(page: Int, size: Int): Flow<Resource<PagePaginatedResponse<Film>>> = current.getFilms(page, size)

    override fun getLicenses(): Flow<Resource<List<License>>> = current.getLicenses()
}