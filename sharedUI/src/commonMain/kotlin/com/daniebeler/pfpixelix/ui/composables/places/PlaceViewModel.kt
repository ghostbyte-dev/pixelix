package com.daniebeler.pfpixelix.ui.composables.places

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import me.tatarka.inject.annotations.Inject

class PlaceViewModel @Inject constructor(
    private val accountService: AccountService,
    private val authService: AuthService
) : ViewModel() {


}