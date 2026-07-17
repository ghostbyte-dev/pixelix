package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class VernissageNotificationService(
    private val api: VernissageApi
) : NotificationService {
    override fun getNotifications(maxNotificationId: String?) = loadVernissagePaginatedListResources {
        api.getNotifications(maxNotificationId)
    }

    override fun getUnreadCount(): Flow<Resource<Int>> = loadResource {
        api.unreadNotificationsCount().toDomain()
    }

    override fun markNotifications(notificationId: String): Flow<Resource<Unit>> = loadResource {
        api.markNotifications(notificationId)
    }
}