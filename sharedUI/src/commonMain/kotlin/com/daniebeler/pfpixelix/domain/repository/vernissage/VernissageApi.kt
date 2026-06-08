package com.daniebeler.pfpixelix.domain.repository.vernissage

import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaServersResponse
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedChatDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedCollectionDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedConversationDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedMediaAttachmentDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedMessageDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedNodeInfoDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedNotificationDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPlaceDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostContextDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedPostDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedRelatedHashtagDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedRelationshipDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedReportResponseDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedSearchDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedSettingsDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedTagDto
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.request.forms.MultiPartFormDataContent

interface VernissageApi {
    companion object {
        const val HASHTAG_TIMELINE_POSTS_LIMIT = 20
        const val HOME_TIMELINE_POSTS_LIMIT = 20
        const val LOCAL_TIMELINE_POSTS_LIMIT = 20
        const val GLOBAL_TIMELINE_POSTS_LIMIT = 20
        const val NOTIFICATIONS_LIMIT = 40
        const val LIKED_POSTS_LIMIT = 40
        const val PROFILE_POSTS_LIMIT = 18
        const val LIKED_BY_LIMIT = 40
        const val FOLLOWERS_LIMIT = 40
        const val BOOKMARKED_LIMIT = 18
    }

    // Discover
    @GET("api/v1.1/discover/posts/trending")
    suspend fun getTrendingPosts(@Query("range") range: String): List<PixelfedPostDto>

    @GET("api/v1.1/discover/posts/hashtags?_pe=1")
    suspend fun getTrendingHashtags(): List<PixelfedTagDto>

    @GET("api/v1.1/discover/accounts/popular")
    suspend fun getTrendingAccounts(): List<PixelfedAccountDto>


    // Timelines
    @GET("api/v1/timelines/tag/{tag}?_pe=1")
    suspend fun getHashtagTimeline(
        @Path("tag") tag: String,
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int
    ): List<PixelfedPostDto>

    @GET("api/v1/timelines/public?local=true&_pe=1")
    suspend fun getLocalTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int = LOCAL_TIMELINE_POSTS_LIMIT
    ): List<PixelfedPostDto>

    @GET("api/v1/timelines/public?remote=true&_pe=1")
    suspend fun getGlobalTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int = GLOBAL_TIMELINE_POSTS_LIMIT
    ): List<PixelfedPostDto>

    @GET("api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int = HOME_TIMELINE_POSTS_LIMIT
    ): List<PixelfedPostDto>

    // Favourites
    @GET("api/v1/favourites")
    fun getLikedPosts(
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = LIKED_POSTS_LIMIT
    ): Call<List<PixelfedPostDto>>

    @GET("api/v1/statuses/{postId}/favourited_by?_pe=1")
    suspend fun getAccountsWhoLikedPost(
        @Path("postId") postId: String,
        @Query("limit") limit: Int = LIKED_BY_LIMIT
    ): List<PixelfedAccountDto>


    // Notifications
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("max_id") maxNotificationId: String? = null,
        @Query("limit") limit: Int = NOTIFICATIONS_LIMIT
    ): List<PixelfedNotificationDto>


    // Accounts
    @GET("api/pixelfed/v1/accounts/{accountid}")
    suspend fun getAccount(
        @Path("accountid") accountId: String
    ): PixelfedAccountDto

    @GET("api/v1.1/accounts/username/{username}?_pe=1")
    suspend fun getAccountByUsername(
        @Path("username") username: String
    ): PixelfedAccountDto

    @POST("api/v1/accounts/update_credentials?_pe=1")
    suspend fun updateAccount(
        @Body body: MultiPartFormDataContent
    ): PixelfedAccountDto

    @GET("api/v1/accounts/{accountid}/statuses?pe=1")
    suspend fun getPostsByAccountId(
        @Path("accountid") accountId: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int
    ): List<PixelfedPostDto>

    @GET("api/v1/accounts/relationships")
    suspend fun getRelationships(
        @Query("id[]") userId: List<String>
    ): List<PixelfedRelationshipDto>

    @GET("api/v1.1/accounts/mutuals/{id}")
    suspend fun getMutalFollowers(
        @Path("id") userId: String
    ): List<PixelfedAccountDto>

    @POST("api/v1/accounts/{id}/follow")
    suspend fun followAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @POST("api/v1/accounts/{id}/unfollow")
    suspend fun unfollowAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @POST("api/v1/accounts/{id}/mute")
    suspend fun muteAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @POST("api/v1/accounts/{id}/unmute")
    suspend fun unmuteAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @POST("api/v1/accounts/{id}/block")
    suspend fun blockAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @POST("api/v1/accounts/{id}/unblock")
    suspend fun unblockAccount(
        @Path("id") userId: String
    ): PixelfedRelationshipDto

    @GET("api/v1/accounts/{id}/followers")
    suspend fun getAccountsFollowers(
        @Path("id") userId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): Call<List<PixelfedAccountDto>>

    @GET("api/v1/accounts/{id}/following")
    suspend fun getAccountsFollowing(
        @Path("id") userId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): Call<List<PixelfedAccountDto>>

    // Statuses
    @GET("api/v1/statuses/{postid}?_pe=1")
    suspend fun getPostById(
        @Path("postid") postId: String
    ): PixelfedPostDto

    @POST("api/v1/statuses/{id}/favourite")
    suspend fun likePost(@Path("id") userId: String): PixelfedPostDto

    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unlikePost(
        @Path("id") userId: String
    ): PixelfedPostDto

    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogPost(@Path("id") userId: String): PixelfedPostDto

    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unreblogPost(
        @Path("id") userId: String
    ): PixelfedPostDto

    @POST("api/v1/statuses/{id}/bookmark")
    suspend fun bookmarkPost(
        @Path("id") userId: String
    ): PixelfedPostDto

    @POST("api/v1/statuses/{id}/unbookmark")
    suspend fun unbookmarkPost(
        @Path("id") userId: String
    ): PixelfedPostDto


    // Collections
    @GET("api/v1.1/collections/accounts/{userId}")
    suspend fun getCollectionsByUserId(
        @Path("userId") userId: String, @Query("page") page: Int
    ): List<PixelfedCollectionDto>

    @GET("api/v1.1/collections/view/{collectionid}")
    suspend fun getCollection(
        @Path("collectionid") collectionId: String
    ): PixelfedCollectionDto

    @GET("api/v1.1/collections/items/{collectionid}")
    suspend fun getPostsOfCollection(
        @Path("collectionid") collectionId: String,
        @Query("page") page: Int,
        ): List<PixelfedPostDto>

    @POST("api/v1.1/collections/remove")
    suspend fun removePostOfCollection(
        @Query("collection_id") collectionId: String, @Query("post_id") postId: String
    ): String

    @POST("api/v1.1/collections/add")
    suspend fun addPostOfCollection(
        @Query("collection_id") collectionId: String, @Query("post_id") postId: String
    ): String

    @FormUrlEncoded
    @POST("api/v1.1/collections/update/{collectionId}")
    suspend fun updateCollection(
        @Path("collectionId") collectionId: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("visibility") visibility: String
    ): PixelfedCollectionDto

    // Tags
    @POST("api/v1/tags/{id}/follow")
    suspend fun followHashtag(
        @Path("id") tagId: String
    ): PixelfedTagDto

    @POST("api/v1/tags/{id}/unfollow")
    suspend fun unfollowHashtag(
        @Path("id") tagId: String
    ): PixelfedTagDto

    @GET("api/v1/followed_tags?_pe=1")
    suspend fun getFollowedHashtags(): List<PixelfedTagDto>

    @GET("api/v1/tags/{tag}?_pe=1")
    suspend fun getHashtag(@Path("tag") tag: String): PixelfedTagDto

    @GET("api/v1/tags/{tag}/related")
    suspend fun getRelatedHashtags(@Path("tag") tag: String): List<PixelfedRelatedHashtagDto>


    // Direct Messages
    @GET("api/v1/conversations")
    suspend fun getConversations(): List<PixelfedConversationDto>

    @GET("api/v1.1/direct/thread")
    suspend fun getChat(
        @Query("pid") accountId: String,
        @Query("max_id") maxId: String? = null
    ): PixelfedChatDto

    @Headers("Content-Type: application/json")
    @POST("api/v1.1/direct/thread/send")
    suspend fun sendMessage(@Body createMessage: String): PixelfedMessageDto

    @DELETE("api/v1.1/direct/thread/message")
    suspend fun deleteMessage(@Query("id") id: String): List<Int>
    // Other

    @GET("api/v1/bookmarks?_pe=1")
    suspend fun getBookmarkedPosts(
        @Query("limit") limit: Int = BOOKMARKED_LIMIT,
        @Query("cursor") cursor: String?
    ): Call<List<PixelfedPostDto>>

    @GET("api/v1/statuses/{postid}/context?_pe=1")
    suspend fun getReplies(
        @Path("postid") postid: String
    ): PixelfedPostContextDto

    @GET("api/v1/instance")
    suspend fun getInstance(): Instance

    @GET("api/v1/mutes")
    suspend fun getMutedAccounts(): List<PixelfedAccountDto>

    @GET("api/v1/blocks")
    suspend fun getBlockedAccounts(): List<PixelfedAccountDto>

    @GET("api/v2/search?_pe=1")
    suspend fun getSearch(
        @Query("q") searchText: String, @Query("type") type: String?, @Query("limit") limit: Int
    ): PixelfedSearchDto

    @GET("api/v1.1/compose/search/location?limit=5")
    suspend fun searchLocations(
        @Query("q") searchText: String
    ): List<PixelfedPlaceDto>

    @POST("api/v2/media")
    suspend fun uploadMedia(
        @Body body: MultiPartFormDataContent
    ): PixelfedMediaAttachmentDto

    @FormUrlEncoded
    @PUT("api/v1/media/{id}")
    suspend fun updateMedia(
        @Path("id") mediaAttachmentid: String,
        @Field("description") description: String,
    ): PixelfedMediaAttachmentDto

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createPost(
        @Body createPost: String
    ): PixelfedPostDto

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createReply(
        @Body createReply: String
    ): PixelfedPostDto

    @Headers("Content-Type: application/json")
    @PUT("api/v1/statuses/{id}")
    suspend fun updatePost(
        @Path("id") postId: String, @Body updatePost: String
    )

    @DELETE("api/v1/statuses/{id}")
    suspend fun deletePost(
        @Path("id") postid: String
    ): PixelfedPostDto

    @Headers("Content-Type: application/json")
    @POST("api/v1.1/report")
    suspend fun reportPost(
        @Body reportPostBody: String
    ): PixelfedReportResponseDto

    @GET("api/pixelfed/v1/web/settings")
    suspend fun getSettings(): PixelfedSettingsDto

    @GET
    suspend fun getNodeInfo(@Url domain: String): PixelfedNodeInfoDto

    @GET("https://api.fedisea.surf/v1/software/{slug}")
    suspend fun getSoftwareFromFedisea(
        @Path("slug") slug: String
    ): FediseaSoftware

    @GET("https://api.fedisea.surf/v1/instances/{slug}")
    suspend fun getServerFromFedisea(
        @Path("slug") domain: String
    ): FediseaInstance

    @GET("https://api.fedisea.surf/v1/instances")
    suspend fun getOpenServers(
        @Query("search") search: String,
        @Query("size") size: Int = 10,
        @Query("software") software: String = "pixelfed",
        @Query("sort") sort: String = "activeUsersMonth",
        @Query("order") order: String = "desc"
    ): FediseaServersResponse
}