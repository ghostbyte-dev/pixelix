package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class VernissageTimelineService(
    private val api: VernissageApi,
    private val prefs: UserPreferences
): TimelineService {

    override fun getHomeTimeline(maxPostId: String?, enableReblogs: Boolean) =
        loadListResources {
            api.getHomeTimeline(maxPostId).data.map { it.toDomain() }
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun getLocalTimeline(maxPostId: String?) = loadListResources {
        api.getLocalTimeline(maxPostId).data.map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getGlobalTimeline(maxPostId: String?) = loadListResources {
        api.getGlobalTimeline(maxPostId).data.map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getHashtagTimeline(
        hashtag: String,
        maxId: String?,
        limit: Int
    ) = loadListResources {
        api.getHashtagTimeline(hashtag, maxId, limit).data.map { it.toDomain() }
    }.filterSensitive(prefs.hideSensitiveContent)
}