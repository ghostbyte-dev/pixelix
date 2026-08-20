package com.daniebeler.pfpixelix.ui.composables.explore.trending

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.PagePaginatedResponse
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BasePagePaginatedViewModel<T>(private val fetcher: (page: Int) -> Flow<Resource<PagePaginatedResponse<T>>>) : ViewModel() {
    var pagePaginatedState by mutableStateOf(PagePaginatedState<T>())
        protected set

    init {
        getItems()
    }

    fun getItems(refreshing: Boolean = false) {
        if (!refreshing && pagePaginatedState.items.isNotEmpty()) return
        fetchItems(page = 1, isRefreshing = refreshing)
    }

    fun getItemsPaginated() {
        if (pagePaginatedState.isLoading || pagePaginatedState.endReached || pagePaginatedState.items.isEmpty()) return
        fetchItems(page = pagePaginatedState.page + 1, isRefreshing = false)
    }

    private fun fetchItems(page: Int, isRefreshing: Boolean) {
        fetcher(page).onEach { result ->
            pagePaginatedState = when (result) {
                is Resource.Success -> {
                    val newItems = result.data.data
                    val updatedItems = if (page == 1) newItems else pagePaginatedState.items + newItems
                    val isEndReached = updatedItems.size >= result.data.total

                    pagePaginatedState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        items = updatedItems,
                        page = page,
                        endReached = isEndReached,
                        error = ""
                    )
                }
                is Resource.Error -> pagePaginatedState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = result.message
                )
                is Resource.Loading -> pagePaginatedState.copy(
                    isLoading = true,
                    isRefreshing = isRefreshing
                )
            }
        }.launchIn(viewModelScope)
    }
}