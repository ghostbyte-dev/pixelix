package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedWidgetService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageWidgetService
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface WidgetService {
    fun getNotifications(maxNotificationId: String? = null): Flow<Resource<PaginatedResponse<List<Notification>>>>

    fun getLatestImage(): Flow<Resource<Post>>
}

@Inject
@AppSingleton
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

    override fun getNotifications(maxNotificationId: String?): Flow<Resource<PaginatedResponse<List<Notification>>>> =
        current.getNotifications(maxNotificationId)

    override fun getLatestImage(): Flow<Resource<Post>> = current.getLatestImage()

}