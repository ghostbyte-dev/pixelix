package com.daniebeler.pfpixelix.ui.composables.post_editor

sealed interface PostEditorNavigationEffect {
    data object PostCreated : PostEditorNavigationEffect
    data class PostUpdated(val postId: String) : PostEditorNavigationEffect
}
