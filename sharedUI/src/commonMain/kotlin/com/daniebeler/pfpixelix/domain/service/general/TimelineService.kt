package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedTimelineService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageTimelineService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject


interface TimelineService {
    fun getHomeTimeline(
        maxPostId: String? = null, enableReblogs: Boolean = false
    ): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun getLocalTimeline(maxPostId: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun getGlobalTimeline(maxPostId: String? = null): Flow<Resource<PaginatedResponse<List<Post>>>>

    fun getHashtagTimeline(
        hashtag: String,
        maxId: String? = null,
        limit: Int = PixelfedApi.HASHTAG_TIMELINE_POSTS_LIMIT
    ): Flow<Resource<PaginatedResponse<List<Post>>>>

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
class TimelineServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedTimelineService,
    private val vernissage: VernissageTimelineService
) : TimelineService {

    private val current: TimelineService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getHomeTimeline(maxPostId: String?, enableReblogs: Boolean) =
        current.getHomeTimeline(maxPostId, enableReblogs)

    override fun getLocalTimeline(maxPostId: String?) = current.getLocalTimeline(maxPostId)

    override fun getGlobalTimeline(maxPostId: String?) = current.getGlobalTimeline(maxPostId)

    override fun getHashtagTimeline(
        hashtag: String, maxId: String?, limit: Int
    ) = current.getHashtagTimeline(hashtag, maxId, limit)
}