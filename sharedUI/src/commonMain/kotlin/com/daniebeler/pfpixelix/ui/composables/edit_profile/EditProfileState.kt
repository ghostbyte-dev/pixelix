package com.daniebeler.pfpixelix.ui.composables.edit_profile

import com.daniebeler.pfpixelix.domain.model.Account

data class EditProfileState(
    val isLoading: Boolean = false,
    val account: Account? = null,
    val error: String = ""
)
