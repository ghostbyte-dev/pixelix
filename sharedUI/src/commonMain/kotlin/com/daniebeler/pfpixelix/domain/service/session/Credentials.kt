package com.daniebeler.pfpixelix.domain.service.session

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

@Serializable
data class Credentials(
    val accountId: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val serverUrl: String,
    val token: String
)

@Serializable
data class SessionStorage(
    val sessions: Set<Credentials>,
    val activeUserId: String?
) {
    fun getActiveSession() = activeUserId?.let { sessions.first { s -> s.accountId == it }}
}

object SessionStorageDataSerializer: OkioSerializer<SessionStorage> {
    override val defaultValue: SessionStorage
        get() = SessionStorage(emptySet(), null)

    override suspend fun readFrom(source: BufferedSource): SessionStorage {
        return try {
            Json.decodeFromString(
                deserializer = SessionStorage.serializer(),
                string = source.readUtf8()
            )
        } catch (e: SerializationException) {
            e.printStackTrace();
            defaultValue
        }
    }

    override suspend fun writeTo(t: SessionStorage, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = SessionStorage.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}