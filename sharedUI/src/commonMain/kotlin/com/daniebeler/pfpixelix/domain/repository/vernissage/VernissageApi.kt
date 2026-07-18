package com.daniebeler.pfpixelix.domain.repository.vernissage

import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageMediaAttachmentMetadataRequest
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedNodeInfoDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageAccountDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageBlockedAccountDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageCategory
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageCountryDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageInstanceDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageLicenseDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageLocationDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageMutedAccountDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageNewPostRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageNotificationDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePagePaginatedResponse
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePaginatedResponse
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePostContextDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePostDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageRelationshipDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageSearchDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageTagDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageUnreadNotificationsCountDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageUploadedAttachment
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissageVisibilityDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageReblogRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUpdateUserRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserBlockRequest
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.VernissageUserMuteRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
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
    @GET("api/v1/timelines/hashtag/{tag}")
    suspend fun getHashtagTimeline(
        @Path("tag") tag: String,
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = HASHTAG_TIMELINE_POSTS_LIMIT
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

    @GET("api/v1/trending/users")
    suspend fun getTrendingUsers(
        @Query("period") period: String,
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = TRENDING_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageAccountDto>>

    @GET("api/v1/trending/hashtags")
    suspend fun getTrendingHashtags(
        @Query("period") period: String,
        @Query("maxId") maxId: String? = null,
        @Query("limit") limit: Int = TRENDING_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageTagDto>>

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
        @Query("maxId") maxId: String? = null, @Query("limit") limit: Int = LIKED_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/bookmarks")
    suspend fun getBookmarkedPosts(
        @Query("maxId") maxId: String? = null, @Query("limit") limit: Int = BOOKMARKED_LIMIT
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

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogPost(
        @Path("id") userId: String,
        @Body relogRequest: VernissageReblogRequest = VernissageReblogRequest(
            VernissageVisibilityDto.PUBLIC
        )
    ): VernissagePostDto

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

    @GET("api/v1/statuses/{id}/favourited")
    suspend fun getFavourited(
        @Path("id") postId: String,
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = PROFILE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageAccountDto>>

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

    @Headers("Content-Type: application/json")
    @PUT("api/v1/users/{username}")
    suspend fun updateAccount(
        @Path("username") username: String, @Body body: VernissageUpdateUserRequest
    ): VernissageAccountDto


    @POST("api/v1/avatars/{username}")
    suspend fun updateAvatar(
        @Path("username") username: String, @Body body: MultiPartFormDataContent
    )

    @POST("api/v1/headers/{username}")
    suspend fun updateHeader(
        @Path("username") username: String, @Body body: MultiPartFormDataContent
    )
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
        @Path("username") username: String, @Body muteRequest: VernissageUserMuteRequest
    ): VernissageRelationshipDto


    @Headers("Content-Type: application/json")
    @POST("api/v1/users/{username}/unmute")
    suspend fun unmuteUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @Headers("Content-Type: application/json")
    @POST("api/v1/users/{username}/block")
    suspend fun blockUser(
        @Path("username") username: String, @Body muteRequest: VernissageUserBlockRequest
    ): VernissageRelationshipDto

    @POST("api/v1/users/{username}/unblock")
    suspend fun unblockUser(
        @Path("username") username: String
    ): VernissageRelationshipDto

    @GET("api/v1/relationships")
    suspend fun getRelationships(
        @Query("id[]") userId: List<String>
    ): List<VernissageRelationshipDto>

    @GET("/api/v1/hashtags/followed")
    suspend fun getFollowedHashtags(
        @Query("limit") limit: Int = FOLLOWERS_LIMIT
    ): List<VernissageTagDto>

    @POST("/api/v1/hashtags/{tag}/follow")
    suspend fun followHashtag(
        @Path("tag") tag: String
    ): VernissageTagDto

    @POST("/api/v1/hashtags/{tag}/unfollow")
    suspend fun unfollowHashtag(
        @Path("tag") tag: String
    )

    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("maxId") maxId: String? = null, @Query("limit") limit: Int = NOTIFICATIONS_LIMIT
    ): VernissagePaginatedResponse<List<VernissageNotificationDto>>

    @GET("api/v1/notifications/count")
    suspend fun unreadNotificationsCount(): VernissageUnreadNotificationsCountDto

    @POST("api/v1/notifications/marker/{id}")
    suspend fun markNotifications(
        @Path("id") notificationId: String
    )

    @POST("api/v1/follow-requests/{userId}/approve")
    suspend fun approveFollowRequest(
        @Path("userId") accountId: String
    ): VernissageRelationshipDto

    @POST("api/v1/follow-requests/{userId}/reject")
    suspend fun denyFollowRequest(
        @Path("userId") accountId: String
    ): VernissageRelationshipDto

    @GET("api/v1/search")
    suspend fun getSearch(
        @Query("query") searchText: String, @Query("type") type: String?
    ): VernissageSearchDto

    @GET("api/v1/instance")
    suspend fun getInstance(): VernissageInstanceDto

    @GET("api/v1/user-blocked-users")
    suspend fun getBlockedAccounts(): VernissagePagePaginatedResponse<List<VernissageBlockedAccountDto>>

    @GET("api/v1/user-mutes")
    suspend fun getMutedAccounts(): VernissagePagePaginatedResponse<List<VernissageMutedAccountDto>>

    @POST("api/v1/attachments")
    suspend fun uploadMedia(
        @Body body: MultiPartFormDataContent
    ): VernissageUploadedAttachment

    @Headers("Content-Type: application/json")
    @PUT("api/v1/attachments/{attachmentId}")
    suspend fun updateMedia(
        @Path("attachmentId") attachmentId: String, @Body mediaAttachmentMetadata: VernissageMediaAttachmentMetadataRequest
    )

    @Headers("Content-Type: application/json")
    @POST("api/v1/statuses")
    suspend fun createPost(
        @Body createPost: VernissageNewPostRequest
    ): VernissagePostDto

    @Headers("Content-Type: application/json")
    @PUT("api/v1/statuses/{id}")
    suspend fun updatePost(
        @Path("id") postId: String, @Body updatePost: VernissageNewPostRequest
    )

    @DELETE("api/v1/statuses/{id}")
    suspend fun deletePost(
        @Path("id") postid: String
    )

    @GET("api/v1/countries")
    suspend fun getCountries(): List<VernissageCountryDto>

    @GET("api/v1/locations")
    suspend fun getLocations(
        @Query("code") countryCode: String?,
        @Query("query") query: String
    ): List<VernissageLocationDto>

    @GET("api/v1/categories/all")
    suspend fun getAllCategories(
        @Query("onlyUsed") onlyUsed: Boolean = false
    ): List<VernissageCategory>

    @GET("api/v1/licenses?page=1&size=100")
    suspend fun getAllLicenses(): VernissagePagePaginatedResponse<List<VernissageLicenseDto>>

    @GET
    suspend fun getNodeInfo(@Url domain: String): PixelfedNodeInfoDto
}