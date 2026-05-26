package com.daniebeler.pfpixelix.ui.composables.profile

enum class ViewEnum {
    Timeline,
    Grid,
    Masonry;

    companion object {
        fun getView(ordinal: Int): ViewEnum {
            return entries.getOrNull(ordinal) ?: Grid // Defaults to EASY if invalid
        }
    }
}