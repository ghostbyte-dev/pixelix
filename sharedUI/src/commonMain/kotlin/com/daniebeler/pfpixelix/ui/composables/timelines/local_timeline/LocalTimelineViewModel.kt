package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class LocalTimelineViewModel(
    private val timelineService: TimelineService,
    private val userPreferences: UserPreferences
) : PaginatedPostsViewModel(userPreferences) {
    var showTimelineHelp by mutableStateOf(false)

    init {
        loadItems(false)
        viewModelScope.launch {
            userPreferences.showLocalTimelineHelpFlow.collect {
                showTimelineHelp = it
            }
        }
    }

    override fun fetchPage(maxId: String?) = timelineService.getLocalTimeline(maxId)

    fun discardHelp() {
        viewModelScope.launch {
            userPreferences.showLocalTimelineHelp = false
        }
    }
}
