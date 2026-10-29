package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAppIconService
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.DrawableResource

interface AppIconService {

    val icons: List<DrawableResource>

    val currentIcon: StateFlow<DrawableResource>

    fun selectIcon(icon: DrawableResource)
}

interface AppIconManager {
    fun getCurrentIcon(): DrawableResource
    fun setCustomIcon(icon: DrawableResource)
}

@Inject
@SingleIn(AppScope::class)
class AppIconServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedAppIconService,
    //private val vernissage: VernissageTimelineService
) : AppIconService {

    private val current: AppIconService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }
    override val icons: List<DrawableResource> = current.icons
    override val currentIcon: StateFlow<DrawableResource> = current.currentIcon

    override fun selectIcon(icon: DrawableResource) = current.selectIcon(icon)

}