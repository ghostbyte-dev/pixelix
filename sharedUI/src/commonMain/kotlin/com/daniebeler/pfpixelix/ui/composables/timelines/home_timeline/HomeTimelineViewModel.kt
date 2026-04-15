package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.account.AccountService
import com.daniebeler.pfpixelix.domain.service.timeline.TimelineService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class HomeTimelineViewModel @Inject constructor(
    private val timelineService: TimelineService,
    private val accountService: AccountService
) : PaginatedPostsViewModel() {

    private var enableReblogs: Boolean = false

    init {
        getSettings()
    }

    private fun getSettings() {
        accountService.getAccountSettings().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    enableReblogs = result.data?.enableReblogs ?: false
                    loadItems(false)
                }
                is Resource.Error -> loadItems(false)
                is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    override fun fetchPage(maxId: String?) = timelineService.getHomeTimeline(maxId, enableReblogs)
}
