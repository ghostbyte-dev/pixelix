package com.daniebeler.pfpixelix.domain.repository.vernissage

import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedRelationshipDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageAccountDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePostDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePaginatedResponse
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePostContextDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageRelationshipDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserBlockRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserMuteRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface VernissageApi {
    companion object {
        const val HASHTAG_TIMELINE_POSTS_LIMIT = 20
        const val HOME_TIMELINE_POSTS_LIMIT = 20
        const val LOCAL_TIMELINE_POSTS_LIMIT = 20
        const val GLOBAL_TIMELINE_POSTS_LIMIT = 20
        const val TRENDING_TIMELINE_POSTS_LIMIT = 20
        const val NOTIFICATIONS_LIMIT = 40
        const val LIKED_POSTS_LIMIT = 40
        const val PROFILE_POSTS_LIMIT = 18
        const val LIKED_BY_LIMIT = 40
        const val FOLLOWERS_LIMIT = 40
        const val BOOKMARKED_LIMIT = 18
    }

    @GET("api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = HOME_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    // Timelines
    @GET("api/v1/timelines/tag/{tag}")
    suspend fun getHashtagTimeline(
        @Path("tag") tag: String,
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/timelines/public?onlyLocal=true")
    suspend fun getLocalTimeline(
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = LOCAL_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/timelines/public?onlyLocal=false")
    suspend fun getGlobalTimeline(
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = GLOBAL_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/trending/statuses")
    suspend fun getTrendingPosts(
        @Query("period") period: String,
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = TRENDING_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/statuses/{id}")
    suspend fun getPostById(
        @Path("id") postId: String
    ): VernissagePostDto

    @GET("api/v1/statuses/{postId}/context")
    suspend fun getReplies(
        @Path("postId") postId: String
    ): VernissagePostContextDto

    @GET("api/v1/favourites")
    suspend fun getLikedPosts(
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = LIKED_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/bookmarks")
    suspend fun getBookmarkedPosts(
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = LIKED_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @POST("api/v1/statuses/{id}/favourite")
    suspend fun likePost(@Path("id") userId: String): VernissagePostDto

    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unlikePost(
        @Path("id") userId: String
    ): VernissagePostDto

    @POST("api/v1/statuses/{id}/bookmark")
    suspend fun bookmarkPost(
        @Path("id") userId: String
    ): VernissagePostDto

    @POST("api/v1/statuses/{id}/unbookmark")
    suspend fun unbookmarkPost(
        @Path("id") userId: String
    ): VernissagePostDto

    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogPost(@Path("id") userId: String): VernissagePostDto

    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unreblogPost(
        @Path("id") userId: String
    ): VernissagePostDto

    @GET("api/v1/users/{userName}/statuses")
    suspend fun getPostsByAccount(
        @Path("userName") userName: String,
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = PROFILE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createReply(
        @Body createReply: String
    ): VernissagePostDto

    @GET("api/v1/users/{userName}/followers")
    suspend fun getAccountsFollowers(
        @Path("userName") userName: String,
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageAccountDto>>

    @GET("api/v1/users/{userName}/following")
    suspend fun getAccountsFollowing(
        @Path("userName") userName: String,
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageAccountDto>>

    @GET("api/v1/users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): VernissageAccountDto

    @POST("api/v1/users/{username}/follow")
    suspend fun followUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @POST("api/v1/users/{username}/unfollow")
    suspend fun unfollowUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @Headers("Content-Type: application/json")
    @POST("api/v1/users/{username}/mute")
    suspend fun muteUser(
        @Path("username") username: String,
        @Body muteRequest: VernissageUserMuteRequest
    ): VernissageRelationshipDto

    @POST("api/v1/users/{username}/unmute")
    suspend fun unmuteUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @Headers("Content-Type: application/json")
    @POST("api/v1/users/{username}/block")
    suspend fun blockUser(
        @Path("username") username: String,
        @Body muteRequest: VernissageUserBlockRequest
    ): VernissageRelationshipDto

    @POST("api/v1/users/{username}/unblock")
    suspend fun unblockUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @GET("api/v1/relationships")
    suspend fun getRelationships(
        @Query("id[]") userId: List<String>
    ): List<VernissageRelationshipDto>
}