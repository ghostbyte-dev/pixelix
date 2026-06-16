package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.NewReply
import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.PostContext
import com.daniebeler.pfpixelix.domain.model.ReportResponse
import com.daniebeler.pfpixelix.domain.model.Visibility
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import com.daniebeler.pfpixelix.utils.executeAndParsePagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import kotlin.collections.emptyList

@Inject
class VernissagePostService(
    private val api: VernissageApi,
    private val prefs: UserPreferences,
    private val authService: AuthService,
    private val json: Json
) : PostService {
    val emptyPost = Post(
        id = "",
        mediaAttachments = emptyList(),
        account = Account(),
        tags = emptyList(),
        favouritesCount = 0,
        content = "",
        replyCount = 0,
        createdAt = "",
        url = "",
        sensitive = false,
        spoilerText = "",
        favourited = false,
        reblogged = false,
        bookmarked = false,
        mentions = emptyList(),
        place = null,
        likedBy = null,
        visibility = Visibility.PUBLIC,
        inReplyToId = null,
        rebloggedBy = null,
        reblogId = null,
        reblogCount = 0,
        emojis = emptyList()
    )

    override fun getPostById(postId: String) = loadResource {
        // api.getPostById(postId).toDomain()
        emptyPost
    }

    override fun getOwnPosts(
        maxPostId: String?, limit: Int
    ): Flow<Resource<PaginatedResponse<List<Post>>>> {
        val current = authService.getCurrentSession()
        return if (current == null) {
            flowOf(Resource.Error("No account found"))
        } else {
            getPostsByAccountId(current.username, maxPostId, limit)
        }
    }

    override fun getPostsOfAccount(
        accountId: String, username: String, maxPostId: String?, limit: Int
    ) = getPostsByAccountId(username, maxPostId, limit).filterSensitive(prefs.hideSensitiveContent)

    private fun getPostsByAccountId(
        identifier: String, maxPostId: String?, limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getPostsByAccount(identifier, maxPostId, limit)
    }

    override fun getLikedPosts(maxId: String?) = flow {
        emit(Resource.Loading<PaginatedResponse<List<Post>>>())
        /*
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
               }*/
        //TODO: get liked posts

    }

    override fun createReply(postId: String, content: String) = loadResource {
        // val dto = NewReply(status = content, toId = postId)
        // api.createReply(json.encodeToString(dto)).toDomain()
        emptyPost
    }

    override fun getReplies(postId: String) = loadResource {
        PostContext(emptyList(), emptyList())
    // api.getReplies(postId).toDomain()
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
        emit(Resource.Loading< PaginatedResponse<List<Post>>>())

       /* try {
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
        }*/
    }

    override fun reportPost(reportBody: NewReport) = loadResource {
        ReportResponse("", 0)
       // api.reportPost(json.encodeToString(reportBody)).toDomain()
    }

    override fun getTrendingPosts(range: String) = loadListResources {
        //api.getTrendingPosts(range).map { it.toDomain() }
        emptyList<Post>()
    }
    // }.filterSensitive()


}