package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.NotificationType
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.general.WidgetService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedNotificationService(
    private val api: PixelfedApi
) : NotificationService {
    override fun getNotifications(maxNotificationId: String?) = loadPaginatedListResources {
        if (maxNotificationId == null) {
            coroutineScope {
                val notificationsDeferred = async {api.getNotifications()}
                val followRequestsDeferred = async {api.getFollowRequests()}

                val notifications = notificationsDeferred.await()
                val followRequests = followRequestsDeferred.await()

                val notificationsList = notifications.map { it.toDomain() }.toMutableList()
                val followRequestsNotifications = followRequests.map {
                    Notification(
                        account = it.toDomain(),
                        createdAt = "",
                        id = it.id,
                        post = null,
                        type = NotificationType.FOLLOW_REQUEST
                    )
                }
                notificationsList.addAll(0, followRequestsNotifications)
                notificationsList
            }
        } else {
            api.getNotifications(maxNotificationId).map { it.toDomain() }
        }
    }
}