package com.daniebeler.pfpixelix.domain.service.timeline

import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class TimelineService(
    private val api: PixelfedApi,
    private val prefs: UserPreferences
) {
    fun getHomeTimeline(maxPostId: String? = null, enableReblogs: Boolean = false) =
        loadListResources {
            api.getHomeTimeline(maxPostId, enableReblogs)
        }.filterSensitive()

    fun getLocalTimeline(maxPostId: String? = null) = loadListResources {
        api.getLocalTimeline(maxPostId)
    }.filterSensitive()

    fun getGlobalTimeline(maxPostId: String? = null) = loadListResources {
        api.getGlobalTimeline(maxPostId)
    }.filterSensitive()

    fun getHashtagTimeline(
        hashtag: String,
        maxId: String? = null,
        limit: Int = PixelfedApi.HASHTAG_TIMELINE_POSTS_LIMIT
    ) = loadListResources {
        api.getHashtagTimeline(hashtag, maxId, limit)
    }.filterSensitive()

    private fun Flow<Resource<List<Post>>>.filterSensitive() = this.map { event ->
        if (event is Resource.Success<List<Post>>) {
            val hideSensitiveContent = prefs.hideSensitiveContent
            val filtered = event.data.filter { !(hideSensitiveContent && it.sensitive) }
            Resource.Success(filtered)
        } else {
            event
        }
    }
}