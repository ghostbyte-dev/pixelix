package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.NewReport
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.PostContext
import com.daniebeler.pfpixelix.domain.model.ReportResponse
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.ReplyChildrenState
import com.daniebeler.pfpixelix.domain.service.general.ReplyNode
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageNewReplyDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class VernissagePostService(
    private val api: VernissageApi,
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

    override fun getLikedPosts(maxId: String?) = loadVernissagePaginatedListResources {
        api.getLikedPosts(maxId)
    }

    override fun createReply(postId: String, content: String) = loadResource {
        val dto = VernissageNewReplyDto(note = content, replyToStatusId = postId)
        api.createReply(json.encodeToString(dto)).toDomain()
    }

    override fun getReplies(postId: String) = loadResource {
        val context = api.getReplies(postId).toDomain()
        val result = buildTree(context.descendants, rootId = postId)
        result
    }

    private fun buildTree(descendants: List<Post>, rootId: String): List<ReplyNode> {
        val byParent = descendants.groupBy { it.inReplyToId ?: rootId }

        fun buildNode(post: Post): ReplyNode = ReplyNode(
            post = post,
            knownReplyCount = post.replyCount,
            childrenState = ReplyChildrenState.Loaded(
                (byParent[post.id] ?: emptyList())
                    .sortedBy { it.createdAt }
                    .map { buildNode(it) }
            )
        )

        return (byParent[rootId] ?: emptyList())
            .sortedBy { it.createdAt }
            .map { buildNode(it) }
    }

    override fun postContext(postId: String): Flow<Resource<PostContext>> = loadResource {
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

    override fun getBookmarkedPosts(cursor: String?) = loadVernissagePaginatedListResources {
        api.getBookmarkedPosts(cursor)
    }

    //TODO: implement reporting
    override fun reportPost(reportBody: NewReport) = loadResource {
        ReportResponse("", 0)
        // api.reportPost(json.encodeToString(reportBody)).toDomain()
    }

    override fun getLikedBy(postId: String) = loadVernissagePaginatedListResources {
        api.getFavourited(postId)
    }
}