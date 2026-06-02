package com.daniebeler.pfpixelix.domain.service.session

import androidx.datastore.core.DataMigration
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
    val token: String,
    val refreshToken: String,
    val clientId: String,
    val clientSecret: String,
    val createdAt: String
) {
    fun key(): String {
        val cleanUrl =
            serverUrl.removePrefix("https://").removePrefix("http://").removeSuffix("/")
        return "$cleanUrl:$accountId".lowercase()
    }
}

@Serializable
data class SessionStorage(
    val sessions: Map<String, Credentials>,
    val activeKey: String?
) {
    fun getActiveSession() = activeKey?.let { sessions[it] }

}

object SessionStorageDataSerializer : OkioSerializer<SessionStorage> {
    override val defaultValue: SessionStorage
        get() = SessionStorage(emptyMap(), null)

    override suspend fun readFrom(source: BufferedSource): SessionStorage {
        val rawJson = source.readUtf8();
        return try {
            Json.decodeFromString(
                deserializer = SessionStorage.serializer(),
                string = rawJson
            )
        } catch (e: SerializationException) {
            try {
                // Define what the old structure looked like
                @Serializable
                data class OldSessionStorage(
                    val sessions: Set<Credentials>,
                    val activeUserId: String?
                )

                val oldData = Json.decodeFromString(OldSessionStorage.serializer(), rawJson)

                val migratedMap = oldData.sessions.associateBy { it.key() }
                val migratedActiveKey = oldData.sessions
                    .firstOrNull { it.accountId == oldData.activeUserId }?.key()

                SessionStorage(
                    sessions = migratedMap,
                    activeKey = migratedActiveKey
                )
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
                defaultValue
            }
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