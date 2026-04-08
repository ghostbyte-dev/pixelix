package com.daniebeler.pfpixelix.ui.composables.post

data class DeleteState(
    val isLoading: Boolean = false,
    val deleted: Boolean = false,
    val error: String = ""
)
