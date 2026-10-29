package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedNotificationService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageNotificationService
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

interface NotificationService {
    fun getNotifications(maxNotificationId: String? = null): Flow<Resource<PaginatedResponse<Notification>>>
    fun getUnreadCount(): Flow<Resource<Int>>
    fun markNotifications(notificationId: String): Flow<Resource<Unit>>
}

@Inject
@SingleIn(AppScope::class)
class NotificationServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedNotificationService,
    private val vernissage: VernissageNotificationService
) : NotificationService {

    private val current: NotificationService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getNotifications(maxNotificationId: String?): Flow<Resource<PaginatedResponse<Notification>>> =
        current.getNotifications(maxNotificationId)

    override fun getUnreadCount(): Flow<Resource<Int>> = current.getUnreadCount()
    override fun markNotifications(notificationId: String): Flow<Resource<Unit>> = current.markNotifications(notificationId)

}