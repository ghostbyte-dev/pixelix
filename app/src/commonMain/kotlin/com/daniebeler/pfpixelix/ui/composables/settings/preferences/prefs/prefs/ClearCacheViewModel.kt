package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.file.FileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ClearCacheViewModel(
    private val fileService: FileService
) : ViewModel() {

    private val cacheSizeState = MutableStateFlow(0L)
    val cacheSize = cacheSizeState.onStart {
        cacheSizeState.value = fileService.getCacheSizeInBytes()
    }.map { bytes ->
        humanReadableByteCountSI(bytes)
    }

    fun refresh() {
        viewModelScope.launch {
            cacheSizeState.value = fileService.getCacheSizeInBytes()
        }
    }

    fun cleanCache() {
        viewModelScope.launch {
            fileService.cleanCache()
            cacheSizeState.value = fileService.getCacheSizeInBytes()
        }
    }

    private fun humanReadableByteCountSI(bytes: Long): String {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val chars = "kMGTPE".toCharArray()
        var ci = 0
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci++
        }

        val valueRounded = (bytes / 100.0).toInt() / 10.0 // Round down to one decimal place
        return "$valueRounded ${chars[ci]}B"
    }
}