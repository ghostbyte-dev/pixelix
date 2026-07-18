package com.daniebeler.pfpixelix.domain.model

import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest

data class MutedAccount(
    override val id: String = "",
    val account: Account,
    val muteOptions: UserMuteRequest
): Identifiable

