package com.daniebeler.pfpixelix.ui.composables.profile

enum class ViewEnum {
    Timeline,
    Grid,
    Masonry,
    LargeMasonry;

    companion object {
        fun getView(ordinal: Int): ViewEnum {
            return entries.getOrNull(ordinal) ?: Grid
        }
    }
}