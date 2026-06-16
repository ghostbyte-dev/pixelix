package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.NewReply
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.utils.executeAndParsePagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedPostService(
    private val api: PixelfedApi,
    private val prefs: UserPreferences,
    private val authService: AuthService,
    private val json: Json
) : PostService {
    override fun getPostById(postId: String) = loadResource {
        api.getPostById(postId).toDomain()
    }

    override fun getOwnPosts(
        maxPostId: String?, limit: Int
    ): Flow<Resource<PaginatedResponse<List<Post>>>> {
        val current = authService.getCurrentSession()
        return if (current == null) {
            flowOf(Resource.Error("No account found"))
        } else {
            getPostsByAccountId(current.accountId, maxPostId, limit)
        }
    }

    override fun getPostsOfAccount(
        accountId: String, username: String, maxPostId: String?, limit: Int
    ) = getPostsByAccountId(accountId, maxPostId, limit).filterSensitive(prefs.hideSensitiveContent)

    private fun getPostsByAccountId(
        accountId: String, maxPostId: String?, limit: Int
    ) = loadPaginatedListResources<Post> {
        api.getPostsByAccountId(accountId, maxPostId, limit).map { it.toDomain() }
    }

    override fun getLikedPosts(maxId: String?) = flow {
        emit(Resource.Loading())

        try {
            val response: PaginatedResponse<List<Post>> =
                api.getLikedPosts(maxId).executeAndParsePagination(
                    true,
                    "max_id",
                    transform = { dtoList -> dtoList.map { it.toDomain() } }
                )
            val filteredPosts = response.data.filter { it.mediaAttachments.isNotEmpty() }
            val filteredResponse = response.copy(data = filteredPosts)
            emit(Resource.Success(filteredResponse))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }

    }

    override fun createReply(postId: String, content: String) = loadResource {
        val dto = NewReply(status = content, toId = postId)
        api.createReply(json.encodeToString(dto)).toDomain()
    }

    override fun getReplies(postId: String) = loadResource {
        api.getReplies(postId).toDomain()
    }

    override fun likePost(postId: String) = loadResource {
        api.likePost(postId).toDomain()
    }

    override fun unlikePost(postId: String) = loadResource {
        api.unlikePost(postId).toDomain()
    }

    override fun reblogPost(postId: String) = loadResource {
        api.reblogPost(postId).toDomain()
    }

    override fun unreblogPost(postId: String) = loadResource {
        api.unreblogPost(postId).toDomain()
    }

    override fun bookmarkPost(postId: String) = loadResource {
        api.bookmarkPost(postId).toDomain()
    }

    override fun unBookmarkPost(postId: String) = loadResource {
        api.unbookmarkPost(postId).toDomain()
    }

    override fun getBookmarkedPosts(cursor: String?) = flow {
        emit(Resource.Loading())

        try {
            val response: PaginatedResponse<List<Post>> =
                api.getBookmarkedPosts(cursor = cursor).executeAndParsePagination(
                    true,
                    "max_id",
                    transform = { dtoList -> dtoList.map { it.toDomain() } }
                )
            val filteredPosts = response.data.filter { it.mediaAttachments.isNotEmpty() }
            val filteredResponse = response.copy(data = filteredPosts)
            emit(Resource.Success(filteredResponse))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun reportPost(reportBody: NewReport) = loadResource {
        api.reportPost(json.encodeToString(reportBody)).toDomain()
    }

    override fun getTrendingPosts(range: String, maxId: String?) = loadPaginatedListResources {
        if (maxId == null) {
            api.getTrendingPosts(range).map { it.toDomain() }
        } else {
            emptyList()
        }
    }.filterSensitive(prefs.hideSensitiveContent)
}