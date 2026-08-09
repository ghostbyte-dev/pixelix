package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedNotificationService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageNotificationService
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface NotificationService {
    fun getNotifications(maxNotificationId: String? = null): Flow<Resource<PaginatedResponse<Notification>>>
    fun getUnreadCount(): Flow<Resource<Int>>
    fun markNotifications(notificationId: String): Flow<Resource<Unit>>
}

@Inject
@AppSingleton
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