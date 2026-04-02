package com.daniebeler.pfpixelix.widget.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.glance.state.GlanceStateDefinition
import com.daniebeler.pfpixelix.widget.notifications.models.NotificationsStore
import com.daniebeler.pfpixelix.widget.notifications.utils.NotificationsStoreSerializer
import java.io.File

object CustomNotificationsStateDefinition : GlanceStateDefinition<NotificationsStore> {
    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<NotificationsStore> {
        return context.dataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        // Note: The Datastore Preference file resides is in the context.applicationContext.filesDir + "datastore/"
        return File(context.applicationContext.filesDir, "datastore/$fileName")
    }

    private const val fileName = "notifications_widget_store"
    private val Context.dataStore: DataStore<NotificationsStore> by dataStore(fileName, NotificationsStoreSerializer)
}

