package com.daniebeler.pfpixelix.domain.model

data class AccountsWithCursor(
    val accounts: List<Account> = emptyList(),
    val cursor: String = ""
)