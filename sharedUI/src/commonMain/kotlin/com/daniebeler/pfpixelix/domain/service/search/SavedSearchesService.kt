package com.daniebeler.pfpixelix.domain.service.search

import androidx.datastore.core.DataStore
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.model.SavedSearchType
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.domain.model.SavedSearchesAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class SavedSearchesService(
    private val dataStore: DataStore<SavedSearches>
) {
    suspend fun addAccount(accountUsername: String, account: Account, accountId: String) {
        addItem(
            SavedSearchItem(
                savedSearchType = SavedSearchType.Account,
                value = accountUsername,
                account = SavedSearchesAccount(
                    id = account.id,
                    username = account.username,
                    acct = account.acct,
                    displayname = account.displayname,
                    avatar = account.avatar ?: "",
                    followersCount = account.followersCount
                )
            ), accountId
        )
    }

    suspend fun addHashtag(hashtag: String, accountId: String) {
        addItem(
            SavedSearchItem(
                savedSearchType = SavedSearchType.Hashtag,
                value = hashtag,
                account = null
            ), accountId
        )
    }

    suspend fun addSearch(search: String, accountId: String) {
        addItem(
            SavedSearchItem(
                savedSearchType = SavedSearchType.Search,
                value = search,
                account = null
            ), accountId
        )
    }

    private suspend fun addItem(item: SavedSearchItem, accountId: String) {
        try {
            dataStore.updateData { currentData ->
                val currentAccountList = currentData.accountData[accountId] ?: emptyList()

                val updatedList = currentAccountList
                    .filterNot { it.value == item.value && it.savedSearchType == item.savedSearchType } + item

                val newAccountData = currentData.accountData.toMutableMap().apply {
                    put(accountId, updatedList)
                }
                currentData.copy(accountData = newAccountData)
            }
        } catch (e: Throwable) {
            Logger.e("Add item error", e)
        }
    }

    suspend fun deleteElement(item: SavedSearchItem, accountId: String) {
        try {
            dataStore.updateData { currentData ->
                val currentAccountList =
                    currentData.accountData[accountId] ?: return@updateData currentData

                val updatedList = currentAccountList.filterNot { it == item }

                val newAccountData = currentData.accountData.toMutableMap().apply {
                    put(accountId, updatedList)
                }
                currentData.copy(accountData = newAccountData)
            }
        } catch (e: Throwable) {
            Logger.e("deleteElement error", e)
        }
    }

    fun getSavedSearches(accountId: String): Flow<List<SavedSearchItem>> =
        dataStore.data.map { savedSearches ->
            savedSearches.accountData[accountId] ?: emptyList()
        }.distinctUntilChanged()


    suspend fun clearSavedSearches() {
        try {
            dataStore.updateData { SavedSearches() }
        } catch (e: Throwable) {
            Logger.e("clearSavedSearches error", e)
        }
    }
}