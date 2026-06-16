package com.daniebeler.pfpixelix.domain.repository.vernissage

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePostDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePaginatedResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

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
        @Query("limit") limit: Int = PixelfedApi.Companion.LOCAL_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>

    @GET("api/v1/timelines/public?onlyLocal=false")
    suspend fun getGlobalTimeline(
        @Query("maxId") maxPostId: String? = null,
        @Query("limit") limit: Int = PixelfedApi.Companion.GLOBAL_TIMELINE_POSTS_LIMIT
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
        @Query("limit") limit: Int = PixelfedApi.Companion.GLOBAL_TIMELINE_POSTS_LIMIT
    ): VernissagePaginatedResponse<List<VernissagePostDto>>
}