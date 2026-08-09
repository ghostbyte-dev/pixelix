package com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class LensesViewModel @Inject constructor(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities

    var lensesState by mutableStateOf(LensesState())

    init {
        getLenses()
    }

    fun getLenses(refreshing: Boolean = false) {
        if (!refreshing && lensesState.lenses.isNotEmpty()) return

        fetchLenses(page = 1, isRefreshing = refreshing)
    }

    fun getCamerasPaginated() {
        if (lensesState.isLoading ||
            lensesState.endReached ||
            lensesState.page == 1 ||
            lensesState.lenses.isEmpty()) {
            return
        }

        fetchLenses(page = lensesState.page, isRefreshing = false)
    }

    private fun fetchLenses(page: Int, isRefreshing: Boolean) {
        exploreService.getLenses(page).onEach { result ->
            lensesState = when (result) {
                is Resource.Success -> {
                    val newCategories = result.data.data
                    val updatedCategories = if (page == 1) newCategories else lensesState.lenses + newCategories

                    lensesState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        lenses = updatedCategories,
                        page = result.data.currentPage,
                        endReached = result.data.isEndReached,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    lensesState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    lensesState.copy(
                        isLoading = true,
                        isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}