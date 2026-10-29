package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedWidgetService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageWidgetService
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

interface WidgetService {
    fun getNotifications(maxNotificationId: String? = null): Flow<Resource<PaginatedResponse<Notification>>>

    fun getLatestImage(): Flow<Resource<Post>>
}

@Inject
@SingleIn(AppScope::class)
class WidgetServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedWidgetService,
    private val vernissage: VernissageWidgetService
) : WidgetService {

    private val current: WidgetService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getNotifications(maxNotificationId: String?): Flow<Resource<PaginatedResponse<Notification>>> =
        current.getNotifications(maxNotificationId)

    override fun getLatestImage(): Flow<Resource<Post>> = current.getLatestImage()

}