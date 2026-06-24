package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.general.WidgetService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import me.tatarka.inject.annotations.Inject

@Inject
class VernissageNotificationService(
    private val api: VernissageApi
) : NotificationService {
    //TODO: find sollution for follow requests, there is a own follow request api endpoint
    override fun getNotifications(maxNotificationId: String?) = loadVernissagePaginatedListResources {
        api.getNotifications(maxNotificationId)
    }
}