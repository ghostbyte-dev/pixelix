package com.daniebeler.pfpixelix.domain.service.widget

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class WidgetService(
    private val api: PixelfedApi
) {
    fun getNotifications(maxNotificationId: String? = null) = loadListResources {
        api.getNotifications(maxNotificationId)
    }

    fun getLatestImage() = loadResource {
        api.getHomeTimeline(limit = 5)
            .first { post -> post.mediaAttachments[0].type == "image" }
    }
}