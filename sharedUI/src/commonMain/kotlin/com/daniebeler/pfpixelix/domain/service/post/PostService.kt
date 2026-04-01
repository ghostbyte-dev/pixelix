package com.daniebeler.pfpixelix.domain.service.post

import com.daniebeler.pfpixelix.domain.model.LikedPostsWithNext
import com.daniebeler.pfpixelix.domain.model.NewReply
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Inject
class PostService(
    private val api: PixelfedApi,
    private val prefs: UserPreferences,
    private val authService: AuthService,
    private val json: Json
) {
    fun getPostById(postId: String) = loadResource {
        api.getPostById(postId)
    }

    fun getOwnPosts(
        maxPostId: String? = null, limit: Int = PixelfedApi.PROFILE_POSTS_LIMIT
    ): Flow<Resource<List<Post>>> {
        val current = authService.getCurrentSession()
        return if (current == null) {
            flowOf(Resource.Error("No account found"))
        } else {
            getPostsByAccountId(current.accountId, maxPostId, limit)
        }
    }

    fun getPostsOfAccount(
        accountId: String, maxPostId: String? = null, limit: Int = PixelfedApi.PROFILE_POSTS_LIMIT
    ) = getPostsByAccountId(accountId, maxPostId, limit).filterSensitive()

    private fun getPostsByAccountId(
        accountId: String, maxPostId: String?, limit: Int
    ) = loadListResources {
        api.getPostsByAccountId(accountId, maxPostId, limit)
    }

    fun getLikedPosts(maxId: String? = null) = flow {
        emit(Resource.Loading())

        try {
            val (response, data) = api.getLikedPosts(maxId).executeWithResponse()
            val linkHeader = response.headers["link"] ?: ""
            val onlyLink = linkHeader.substringAfter("rel=\"next\",<", "").substringBefore(">", "")
            val nextMinId = onlyLink.substringAfter("min_id=", "")

            val posts = data.filter { it.mediaAttachments.isNotEmpty() }

            val result = LikedPostsWithNext(posts, nextMinId)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    fun createReply(postId: String, content: String) = loadResource {
        val dto = NewReply(status = content, toId = postId)
        api.createReply(json.encodeToString(dto))
    }

    fun getReplies(postId: String) = loadResource {
        api.getReplies(postId)
    }

    fun likePost(postId: String) = loadResource {
        api.likePost(postId)
    }

    fun unlikePost(postId: String) = loadResource {
        api.unlikePost(postId)
    }

    fun reblogPost(postId: String) = loadResource {
        api.reblogPost(postId)
    }

    fun unreblogPost(postId: String) = loadResource {
        api.unreblogPost(postId)
    }

    fun bookmarkPost(postId: String) = loadResource {
        api.bookmarkPost(postId)
    }

    fun unBookmarkPost(postId: String) = loadResource {
        api.unbookmarkPost(postId)
    }

    fun getBookmarkedPosts() = loadListResources {
        api.getBookmarkedPosts()
    }

    fun reportPost(reportBody: NewReport) = loadResource {
        api.reportPost(json.encodeToString(reportBody))
    }

    fun getTrendingPosts(range: String) = loadListResources {
        api.getTrendingPosts(range)
    }.filterSensitive()

    private fun Flow<Resource<List<Post>>>.filterSensitive() = this.map { event ->
        if (event is Resource.Success<List<Post>>) {
            val hideSensitiveContent = prefs.hideSensitiveContent
            val filtered = event.data.filter { !(hideSensitiveContent && it.sensitive) }
            Resource.Success(filtered)
        } else {
            event
        }
    }

    private suspend fun <T> Call<T>.executeWithResponse() = suspendCoroutine { cont ->
        onExecute(object : Callback<T> {
            override fun onResponse(call: T, response: HttpResponse) {
                cont.resume(response to call)
            }

            override fun onError(exception: Throwable) {
                cont.resumeWithException(exception)
            }
        })
    }
}