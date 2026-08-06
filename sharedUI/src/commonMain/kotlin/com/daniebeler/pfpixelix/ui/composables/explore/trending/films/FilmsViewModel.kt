package com.daniebeler.pfpixelix.ui.composables.explore.trending.films

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

class FilmsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    val timelineService: TimelineService,
    session: Session
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities

    var filmsState by mutableStateOf(FilmsState())

    init {
        getFilms()
    }

    fun getFilms(refreshing: Boolean = false) {
        if (!refreshing && filmsState.films.isNotEmpty()) return

        fetchFilms(page = 1, isRefreshing = refreshing)
    }

    fun getFilmsPaginated() {
        if (filmsState.isLoading ||
            filmsState.endReached ||
            filmsState.page == 1 ||
            filmsState.films.isEmpty()) {
            return
        }

        fetchFilms(page = filmsState.page, isRefreshing = false)
    }

    private fun fetchFilms(page: Int, isRefreshing: Boolean) {
        exploreService.getFilms(page).onEach { result ->
            filmsState = when (result) {
                is Resource.Success -> {
                    val newCategories = result.data.data
                    val updatedCategories = if (page == 1) newCategories else filmsState.films + newCategories

                    filmsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        films = updatedCategories,
                        page = result.data.currentPage,
                        endReached = result.data.isEndReached,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    filmsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    filmsState.copy(
                        isLoading = true,
                        isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}