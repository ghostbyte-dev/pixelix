package com.daniebeler.pfpixelix.domain.repository

import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Chat
import com.daniebeler.pfpixelix.domain.model.Collection
import com.daniebeler.pfpixelix.domain.model.Conversation
import com.daniebeler.pfpixelix.domain.model.FediServerData
import com.daniebeler.pfpixelix.domain.model.FediSoftware
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Message
import com.daniebeler.pfpixelix.domain.model.NodeInfo
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.Place
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.PostContext
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.ReportResponse
import com.daniebeler.pfpixelix.domain.model.Search
import com.daniebeler.pfpixelix.domain.model.Server
import com.daniebeler.pfpixelix.domain.model.Settings
import com.daniebeler.pfpixelix.domain.model.Tag
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.request.forms.MultiPartFormDataContent

interface PixelfedApi {
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
        const val BOOKMARKED_LIMIT = 12
    }

    // Discover
    @GET("api/v1.1/discover/posts/trending")
    suspend fun getTrendingPosts(@Query("range") range: String): List<Post>

    @GET("api/v1.1/discover/posts/hashtags?_pe=1")
    suspend fun getTrendingHashtags(): List<Tag>

    @GET("api/v1.1/discover/accounts/popular")
    suspend fun getTrendingAccounts(): List<Account>


    // Timelines
    @GET("api/v1/timelines/tag/{tag}?_pe=1")
    suspend fun getHashtagTimeline(
        @Path("tag") tag: String,
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int
    ): List<Post>

    @GET("api/v1/timelines/public?local=true&_pe=1")
    suspend fun getLocalTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int = LOCAL_TIMELINE_POSTS_LIMIT
    ): List<Post>

    @GET("api/v1/timelines/public?remote=true&_pe=1")
    suspend fun getGlobalTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("limit") limit: Int = GLOBAL_TIMELINE_POSTS_LIMIT
    ): List<Post>

    @GET("api/v1/timelines/home?_pe=1")
    suspend fun getHomeTimeline(
        @Query("max_id") maxPostId: String? = null,
        @Query("include_reblogs") includeReblogs: Boolean = false,
        @Query("limit") limit: Int = HOME_TIMELINE_POSTS_LIMIT
    ): List<Post>

    // Favourites
    @GET("api/v1/favourites")
    fun getLikedPosts(
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = LIKED_POSTS_LIMIT
    ): Call<List<Post>>

    @GET("api/v1/statuses/{postId}/favourited_by?_pe=1")
    suspend fun getAccountsWhoLikedPost(
        @Path("postId") postId: String,
        @Query("limit") limit: Int = LIKED_BY_LIMIT
    ): List<Account>


    // Notifications
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("max_id") maxNotificationId: String? = null,
        @Query("limit") limit: Int = NOTIFICATIONS_LIMIT
    ): List<Notification>


    // Accounts
    @GET("api/pixelfed/v1/accounts/{accountid}")
    suspend fun getAccount(
        @Path("accountid") accountId: String
    ): Account

    @GET("api/v1.1/accounts/username/{username}?_pe=1")
    suspend fun getAccountByUsername(
        @Path("username") username: String
    ): Account

    @POST("api/v1/accounts/update_credentials?_pe=1")
    suspend fun updateAccount(
        @Body body: MultiPartFormDataContent
    ): Account

    @GET("api/pixelfed/v1/accounts/{accountid}/statuses?pe=1")
    suspend fun getPostsByAccountId(
        @Path("accountid") accountId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int
    ): Call<List<Post>>

    @GET("api/v1/accounts/relationships")
    suspend fun getRelationships(
        @Query("id[]") userId: List<String>
    ): List<Relationship>

    @GET("api/v1.1/accounts/mutuals/{id}")
    suspend fun getMutalFollowers(
        @Path("id") userId: String
    ): List<Account>

    @POST("api/v1/accounts/{id}/follow")
    suspend fun followAccount(
        @Path("id") userId: String
    ): Relationship

    @POST("api/v1/accounts/{id}/unfollow")
    suspend fun unfollowAccount(
        @Path("id") userId: String
    ): Relationship

    @POST("api/v1/accounts/{id}/mute")
    suspend fun muteAccount(
        @Path("id") userId: String
    ): Relationship

    @POST("api/v1/accounts/{id}/unmute")
    suspend fun unmuteAccount(
        @Path("id") userId: String
    ): Relationship

    @POST("api/v1/accounts/{id}/block")
    suspend fun blockAccount(
        @Path("id") userId: String
    ): Relationship

    @POST("api/v1/accounts/{id}/unblock")
    suspend fun unblockAccount(
        @Path("id") userId: String
    ): Relationship

    @GET("api/v1/accounts/{id}/followers")
    suspend fun getAccountsFollowers(
        @Path("id") userId: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): List<Account>

    @GET("api/v1/accounts/{id}/following")
    suspend fun getAccountsFollowing(
        @Path("id") userId: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): List<Account>

    // Statuses
    @GET("api/v1/statuses/{postid}?_pe=1")
    suspend fun getPostById(
        @Path("postid") postId: String
    ): Post

    @POST("api/v1/statuses/{id}/favourite")
    suspend fun likePost(@Path("id") userId: String): Post

    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unlikePost(
        @Path("id") userId: String
    ): Post

    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogPost(@Path("id") userId: String): Post

    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unreblogPost(
        @Path("id") userId: String
    ): Post

    @POST("api/v1/statuses/{id}/bookmark")
    suspend fun bookmarkPost(
        @Path("id") userId: String
    ): Post

    @POST("api/v1/statuses/{id}/unbookmark")
    suspend fun unbookmarkPost(
        @Path("id") userId: String
    ): Post


    // Collections
    @GET("api/v1.1/collections/accounts/{userId}")
    suspend fun getCollectionsByUserId(
        @Path("userId") userId: String, @Query("page") page: Int
    ): List<Collection>

    @GET("api/v1.1/collections/view/{collectionid}")
    suspend fun getCollection(
        @Path("collectionid") collectionId: String
    ): Collection

    @GET("api/v1.1/collections/items/{collectionid}")
    suspend fun getPostsOfCollection(
        @Path("collectionid") collectionId: String,
        @Query("page") page: Int,
        ): List<Post>

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
    ): Collection

    // Tags
    @POST("api/v1/tags/{id}/follow")
    suspend fun followHashtag(
        @Path("id") tagId: String
    ): Tag

    @POST("api/v1/tags/{id}/unfollow")
    suspend fun unfollowHashtag(
        @Path("id") tagId: String
    ): Tag

    @GET("api/v1/followed_tags?_pe=1")
    suspend fun getFollowedHashtags(): List<Tag>

    @GET("api/v1/tags/{tag}?_pe=1")
    suspend fun getHashtag(@Path("tag") tag: String): Tag

    @GET("api/v1/tags/{tag}/related")
    suspend fun getRelatedHashtags(@Path("tag") tag: String): List<RelatedHashtag>


    // Direct Messages
    @GET("api/v1/conversations")
    suspend fun getConversations(): List<Conversation>

    @GET("api/v1.1/direct/thread")
    suspend fun getChat(
        @Query("pid") accountId: String,
        @Query("max_id") maxId: String? = null
    ): Chat

    @Headers("Content-Type: application/json")
    @POST("api/v1.1/direct/thread/send")
    suspend fun sendMessage(@Body createMessage: String): Message

    @DELETE("api/v1.1/direct/thread/message")
    suspend fun deleteMessage(@Query("id") id: String): List<Int>
    // Other

    @GET("api/v1/bookmarks")
    suspend fun getBookmarkedPosts(
        @Query("limit") limit: Int = BOOKMARKED_LIMIT
    ): List<Post>

    @GET("api/v1/statuses/{postid}/context?_pe=1")
    suspend fun getReplies(
        @Path("postid") postid: String
    ): PostContext

    @GET("api/v1/instance")
    suspend fun getInstance(): Instance

    @GET("api/v1/mutes")
    suspend fun getMutedAccounts(): List<Account>

    @GET("api/v1/blocks")
    suspend fun getBlockedAccounts(): List<Account>

    @GET("api/v2/search?_pe=1&resolve")
    suspend fun getSearch(
        @Query("q") searchText: String, @Query("type") type: String?, @Query("limit") limit: Int
    ): Search

    @GET("api/v1.1/compose/search/location?limit=5")
    suspend fun searchLocations(
        @Query("q") searchText: String
    ): List<Place>

    @POST("api/v2/media")
    suspend fun uploadMedia(
        @Body body: MultiPartFormDataContent
    ): MediaAttachment

    @FormUrlEncoded
    @PUT("api/v1/media/{id}")
    suspend fun updateMedia(
        @Path("id") mediaAttachmentid: String,
        @Field("description") description: String,
    ): MediaAttachment

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createPost(
        @Body createPost: String
    ): Post

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createReply(
        @Body createReply: String
    ): Post

    @Headers("Content-Type: application/json")
    @PUT("api/v1/statuses/{id}")
    suspend fun updatePost(
        @Path("id") postId: String, @Body updatePost: String
    )

    @DELETE("api/v1/statuses/{id}")
    suspend fun deletePost(
        @Path("id") postid: String
    ): Post

    @Headers("Content-Type: application/json")
    @POST("api/v1.1/report")
    suspend fun reportPost(
        @Body reportPostBody: String
    ): ReportResponse

    @GET("api/pixelfed/v1/web/settings")
    suspend fun getSettings(): Settings

    @GET
    suspend fun getNodeInfo(@Url domain: String): NodeInfo

    @GET("https://api.fedidb.org/v1/software/{slug}")
    suspend fun getSoftwareFromFediDB(
        @Path("slug") slug: String
    ): FediSoftware

    @GET("https://api.fedidb.org/v1/server/domain/{slug}")
    suspend fun getServerFromFediDB(
        @Path("slug") domain: String
    ): FediServerData

    @GET("https://pixelfed.org/api/v1/mobile-app/servers/open.json")
    suspend fun getOpenServers(
        @Header("X-Pixelfed-App") pixelfedApp: Int = 1
    ): List<Server>
}