package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAppIconService
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
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
@AppSingleton
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