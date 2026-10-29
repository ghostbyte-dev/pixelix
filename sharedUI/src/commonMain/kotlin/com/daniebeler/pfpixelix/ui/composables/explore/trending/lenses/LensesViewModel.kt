package com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Lens
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.ui.composables.explore.trending.BasePagePaginatedViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class LensesViewModel(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : BasePagePaginatedViewModel<Lens>(fetcher = { page -> exploreService.getLenses(page) }) {

    val capabilities: StateFlow<Capabilities> = session.capabilities
}