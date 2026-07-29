package com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras

import com.daniebeler.pfpixelix.domain.model.Camera

data class CamerasState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val cameras: List<Camera> = emptyList(),
    val error: String = "",
    val page: Int = 1,
    val endReached: Boolean = false
)
