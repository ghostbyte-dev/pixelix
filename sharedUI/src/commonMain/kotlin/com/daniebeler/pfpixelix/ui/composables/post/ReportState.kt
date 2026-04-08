package com.daniebeler.pfpixelix.ui.composables.post

data class ReportState(
    val isLoading: Boolean = false,
    val reported: Boolean = false,
    val error: String = ""
)
