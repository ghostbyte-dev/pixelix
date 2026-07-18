package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionStorage(
    val sessions: Map<String, Credentials>,
    val activeKey: String?
) {
    fun getActiveSession() = activeKey?.let { sessions[it] }

}