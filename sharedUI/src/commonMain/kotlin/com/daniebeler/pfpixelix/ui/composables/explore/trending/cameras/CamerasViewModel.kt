package com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingRange
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class CamerasViewModel @Inject constructor(
    private val exploreService: ExploreService,
    session: Session
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities

    var camerasState by mutableStateOf(CamerasState())

    init {
        getCategories()
    }

    fun getCategories(refreshing: Boolean = false) {
        if (!refreshing && camerasState.cameras.isNotEmpty()) return

        fetchCategories(page = 1, isRefreshing = refreshing)
    }

    fun getCategoriesPaginated() {
        if (camerasState.isLoading ||
            camerasState.endReached ||
            camerasState.page == 1 ||
            camerasState.cameras.isEmpty()) {
            return
        }

        fetchCategories(page = camerasState.page, isRefreshing = false)
    }

    private fun fetchCategories(page: Int, isRefreshing: Boolean) {
        exploreService.getCameras(page).onEach { result ->
            camerasState = when (result) {
                is Resource.Success -> {
                    val newCategories = result.data.data
                    val updatedCategories = if (page == 1) newCategories else camerasState.cameras + newCategories

                    camerasState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        cameras = updatedCategories,
                        page = result.data.currentPage,
                        endReached = result.data.isEndReached,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    camerasState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    camerasState.copy(
                        isLoading = true,
                        isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}