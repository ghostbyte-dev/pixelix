package com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses

import com.daniebeler.pfpixelix.domain.model.Lens
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.ui.composables.explore.trending.BasePagePaginatedViewModel
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

class LensesViewModel @Inject constructor(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : BasePagePaginatedViewModel<Lens>(fetcher = { page -> exploreService.getLenses(page) }) {

    val capabilities: StateFlow<Capabilities> = session.capabilities
}