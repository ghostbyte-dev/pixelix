package com.daniebeler.pfpixelix.ui.composables.post

data class DeleteState(
    val isLoading: Boolean = false,
    val error: String = ""
)

sealed interface DeleteEvent {
    object Success : DeleteEvent
}
