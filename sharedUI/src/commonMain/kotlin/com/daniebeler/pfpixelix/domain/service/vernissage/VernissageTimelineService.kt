package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
import me.tatarka.inject.annotations.Inject

@Inject
class VernissageTimelineService(
    private val api: VernissageApi,
    private val prefs: UserPreferences
) : TimelineService {

    override fun getHomeTimeline(maxPostId: String?, enableReblogs: Boolean) =
        loadVernissagePaginatedListResources {
            api.getHomeTimeline(maxPostId)
        }.filterSensitive(prefs.hideSensitiveContent)

    override fun getLocalTimeline(maxPostId: String?) = loadVernissagePaginatedListResources {
        api.getLocalTimeline(maxPostId)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getGlobalTimeline(maxPostId: String?) = loadVernissagePaginatedListResources {
        api.getGlobalTimeline(maxPostId)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getHashtagTimeline(
        hashtag: String,
        maxId: String?,
        limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getHashtagTimeline(hashtag, maxId, limit)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getCategoryTimeline(
        category: String,
        maxId: String?,
        limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getCategoryTimeline(category, maxId, limit)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getCameraTimeline(
        camera: String,
        maxId: String?,
        limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getCameraTimeline(camera, maxId, limit)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getLensTimeline(
        lens: String,
        maxId: String?,
        limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getLensTimeline(lens, maxId, limit)
    }.filterSensitive(prefs.hideSensitiveContent)

    override fun getFilmTimeline(
        film: String,
        maxId: String?,
        limit: Int
    ) = loadVernissagePaginatedListResources {
        api.getFilmTimeline(film, maxId, limit)
    }.filterSensitive(prefs.hideSensitiveContent)
}