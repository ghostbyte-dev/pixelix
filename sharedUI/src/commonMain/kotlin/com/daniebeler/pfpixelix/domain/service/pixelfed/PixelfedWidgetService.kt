package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.WidgetService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedWidgetService(
    private val api: PixelfedApi
) : WidgetService {
    override fun getNotifications(maxNotificationId: String?) = loadPaginatedListResources {
        api.getNotifications(maxNotificationId).map { it.toDomain() }
    }

    override fun getLatestImage() = loadResource {
        api.getHomeTimeline(limit = 5).first { post -> post.mediaAttachments[0].type == "image" }.toDomain()
    }
}