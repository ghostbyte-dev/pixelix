package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.Category
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.RelatedHashtag
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedTimelineService(
    private val api: PixelfedApi,
    private val prefs: UserPreferences
): TimelineService {

    override fun getHomeTimeline(maxPostId: String?, enableReblogs: Boolean) =
        loadPaginatedListResources {
            api.getHomeTimeline(maxPostId, enableReblogs).map { it.toDomain() }
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun getLocalTimeline(maxPostId: String?) = loadPaginatedListResources {
        api.getLocalTimeline(maxPostId).map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getGlobalTimeline(maxPostId: String?) = loadPaginatedListResources {
        api.getGlobalTimeline(maxPostId).map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getHashtagTimeline(
        hashtag: String,
        maxId: String?,
        limit: Int
    ) = loadPaginatedListResources {
        api.getHashtagTimeline(hashtag, maxId, limit).map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getCategoryTimeline(
        category: String,
        maxId: String?,
        limit: Int
    ) = loadPaginatedListResources<Post> {
        emptyList()
    }

    override fun getCameraTimeline(
        camera: String,
        maxId: String?,
        limit: Int
    ) = loadPaginatedListResources<Post> {
        emptyList()
    }
}