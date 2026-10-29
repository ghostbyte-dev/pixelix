package com.daniebeler.pfpixelix.ui.composables.profile.server_stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.FediseaService
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.DomainSoftwareState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class ServerStatsViewModel(
    private val platform: Platform,
    private val fediseaService: FediseaService,
) : ViewModel() {

    var statsState by mutableStateOf(DomainSoftwareState())

    fun getData(domain: String) {
        getFediServer(domain)
    }

    private fun getFediServer(domain: String) {
        val formattedDomain = formatDomain(domain)
        fediseaService.getServerFromFedisea(formattedDomain).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    statsState = DomainSoftwareState(
                        fediServer = result.data, fediSoftware = statsState.fediSoftware
                    )
                    if (result.data.software.isNotEmpty()) {
                        getFediSoftware(result.data.software)
                    }
                }

                is Resource.Error -> {
                    statsState = DomainSoftwareState(
                        error = result.message ?: "An unexpected error occurred",
                        fediServer = statsState.fediServer,
                        fediSoftware = statsState.fediSoftware
                    )
                }

                is Resource.Loading -> {
                    statsState = DomainSoftwareState(
                        isLoading = true,
                        fediServer = statsState.fediServer,
                        fediSoftware = statsState.fediSoftware
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun formatDomain(domain: String): String {
        return domain.removePrefix("https://").removeSuffix("/")
    }

    private fun getFediSoftware(softwareSlug: String) {
        fediseaService.getSoftwareFromFedisea(softwareSlug).onEach { result ->
            statsState = when (result) {
                is Resource.Success -> {
                    DomainSoftwareState(
                        fediSoftware = result.data, fediServer = statsState.fediServer
                    )
                }

                is Resource.Error -> {
                    DomainSoftwareState(
                        error = result.message ?: "An unexpected error occurred",
                        fediServer = statsState.fediServer,
                        fediSoftware = statsState.fediSoftware
                    )
                }

                is Resource.Loading -> {
                    DomainSoftwareState(
                        isLoading = true,
                        fediServer = statsState.fediServer,
                        fediSoftware = statsState.fediSoftware
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }

}