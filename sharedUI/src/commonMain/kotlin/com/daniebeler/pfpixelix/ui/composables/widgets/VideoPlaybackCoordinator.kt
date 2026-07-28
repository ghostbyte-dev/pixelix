package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class VideoPlaybackCoordinator {
    var activeId by mutableStateOf<String?>(null)
        private set

    private val visibleIds = mutableStateListOf<String>()

    fun setVisible(id: String, visible: Boolean) {
        if (visible) {
            if (id !in visibleIds) visibleIds.add(id)
            if (activeId == null) activeId = id
        } else {
            visibleIds.remove(id)
            if (activeId == id) {
                activeId = visibleIds.firstOrNull()
            }
        }
    }

    fun requestActive(id: String) {
        activeId = id
    }

    fun clear(id: String) {
        visibleIds.remove(id)
        if (activeId == id) {
            activeId = visibleIds.firstOrNull()
        }
    }
}

val LocalVideoPlaybackCoordinator = compositionLocalOf { VideoPlaybackCoordinator() }
