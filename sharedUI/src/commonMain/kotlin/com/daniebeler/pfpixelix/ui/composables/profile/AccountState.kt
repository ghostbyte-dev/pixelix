package com.daniebeler.pfpixelix.ui.composables.profile

import com.daniebeler.pfpixelix.domain.model.Account

data class AccountState(
    val isLoading: Boolean = false,
    val refreshing: Boolean = false,
    val account: Account? = null,
    val error: String = ""
)