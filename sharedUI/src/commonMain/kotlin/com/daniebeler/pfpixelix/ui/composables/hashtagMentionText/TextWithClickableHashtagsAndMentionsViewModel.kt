package com.daniebeler.pfpixelix.ui.composables.hashtagMentionText

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class TextWithClickableHashtagsAndMentionsViewModel(
    private val authService: AuthService
) : ViewModel() {
    suspend fun getMyAccountId(): String {
        return authService.getCurrentSession()!!.accountId
    }
}