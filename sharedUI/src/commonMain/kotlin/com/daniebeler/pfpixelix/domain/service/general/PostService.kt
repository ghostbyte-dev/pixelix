package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.PostContext
import com.daniebeler.pfpixelix.domain.model.ReportResponse
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedPostService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissagePostService
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageTimelineService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

interface PostService {
    fun getPostById(postId: String): Flow<Resource<Post>>

    fun getOwnPosts(
        maxPostId: String? = null, limit: Int = PixelfedApi.PROFILE_POSTS_LIMIT
    ): Flow<Resource<PaginatedResponse<List<Post>>>>

    /**
     * Fetches a paginated list of posts belonging to a specific account.
     *
     * @param identifier The account identifier. For **Vernissage**, this must be the account's
     * **username** (e.g., "username"). For **Pixelfed**, this must be the
     * unique **account ID** (e.g., "12345").
     * @param maxPostId Optional ID cursor used to fetch the next page of results for pagination.
     * If null, fetches the most recent posts (first page).
     * @param limit The maximum number of posts to retrieve in a single network request.
     * Defaults to [PixelfedApi.PROFILE_POSTS_LIMIT].
     * @return A [Flow] streaming the [Resource] state, wrapping a [PaginatedResponse]
     * containing the list of domain [Post] objects.
     */
    fun getPostsOfAccount(
        accountId: String, username: String, maxPostId: String? = null, limit: Int = PixelfedApi.PROFILE_POSTS_LIMIT
    ): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun getLikedPosts(maxId: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun createReply(postId: String, content: String): Flow<Resource<Post>>

    fun getReplies(postId: String): Flow<Resource<PostContext>>

    fun likePost(postId: String): Flow<Resource<Post>>

    fun unlikePost(postId: String): Flow<Resource<Post>>

    fun reblogPost(postId: String): Flow<Resource<Post>>

    fun unreblogPost(postId: String): Flow<Resource<Post>>

    fun bookmarkPost(postId: String): Flow<Resource<Post>>

    fun unBookmarkPost(postId: String): Flow<Resource<Post>>


    fun getBookmarkedPosts(cursor: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun reportPost(reportBody: NewReport): Flow<Resource<ReportResponse>>

    fun getTrendingPosts(range: String, maxId: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

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
class PostServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedPostService,
    private val vernissage: VernissagePostService
) : PostService {

    private val current: PostService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getPostById(postId: String): Flow<Resource<Post>> = current.getPostById(postId)

    override fun getOwnPosts(
        maxPostId: String?, limit: Int
    ): Flow<Resource<PaginatedResponse<List<Post>>>> = current.getOwnPosts(maxPostId, limit)

    override fun getPostsOfAccount(
        accountId: String, username: String, maxPostId: String?, limit: Int
    ): Flow<Resource<PaginatedResponse<List<Post>>>> = current.getPostsOfAccount(accountId, username, maxPostId, limit)

    override fun getLikedPosts(maxId: String?): Flow<Resource<PaginatedResponse<List<Post>>>> =
        current.getLikedPosts(maxId)

    override fun createReply(
        postId: String, content: String
    ): Flow<Resource<Post>> = current.createReply(postId, content)

    override fun getReplies(postId: String): Flow<Resource<PostContext>> =
        current.getReplies(postId)

    override fun likePost(postId: String): Flow<Resource<Post>> = current.likePost(postId)

    override fun unlikePost(postId: String): Flow<Resource<Post>> = current.unlikePost(postId)

    override fun reblogPost(postId: String): Flow<Resource<Post>> = current.reblogPost(postId)

    override fun unreblogPost(postId: String): Flow<Resource<Post>> = current.unreblogPost(postId)

    override fun bookmarkPost(postId: String): Flow<Resource<Post>> = current.bookmarkPost(postId)

    override fun unBookmarkPost(postId: String): Flow<Resource<Post>> =
        current.unBookmarkPost(postId)

    override fun getBookmarkedPosts(cursor: String?): Flow<Resource<PaginatedResponse<List<Post>>>> =
        current.getBookmarkedPosts(cursor)

    override fun reportPost(reportBody: NewReport): Flow<Resource<ReportResponse>> =
        current.reportPost(reportBody)

    override fun getTrendingPosts(range: String, maxId: String?): Flow<Resource<PaginatedResponse<List<Post>>>> =
        current.getTrendingPosts(range, maxId)

}