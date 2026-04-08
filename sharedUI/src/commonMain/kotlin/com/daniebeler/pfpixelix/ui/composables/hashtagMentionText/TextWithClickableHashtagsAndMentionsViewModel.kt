package com.daniebeler.pfpixelix.ui.composables.hashtagMentionText

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import me.tatarka.inject.annotations.Inject

class TextWithClickableHashtagsAndMentionsViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    suspend fun getMyAccountId(): String {
        return authService.getCurrentSession()!!.accountId
    }
}