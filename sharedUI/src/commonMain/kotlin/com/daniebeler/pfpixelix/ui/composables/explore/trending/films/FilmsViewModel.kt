package com.daniebeler.pfpixelix.ui.composables.explore.trending.films

import com.daniebeler.pfpixelix.domain.model.Film
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.ui.composables.explore.trending.BasePagePaginatedViewModel
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

class FilmsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : BasePagePaginatedViewModel<Film>(fetcher = { page -> exploreService.getFilms(page) }) {

    val capabilities: StateFlow<Capabilities> = session.capabilities
}