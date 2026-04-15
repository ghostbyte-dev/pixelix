package com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline

import com.daniebeler.pfpixelix.domain.service.timeline.TimelineService
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import me.tatarka.inject.annotations.Inject

class GlobalTimelineViewModel @Inject constructor(
    private val timelineService: TimelineService
) : PaginatedPostsViewModel() {

    init {
        loadItems(false)
    }

    override fun fetchPage(maxId: String?) = timelineService.getGlobalTimeline(maxId)
}
