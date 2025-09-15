package com.dpm.sixpack.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class UserPreferenceDataSourceImpl
    @Inject
    constructor(
        @Named("user") private val dataStore: DataStore<Preferences>,
    ) : UserPreferenceDataSource {
        override val userId: Flow<Long> =
            dataStore.data.map { preferences ->
                preferences[USER_ID] ?: -1L
            }

        override val sessionId: Flow<Long?> =
            dataStore.data.map { preferences ->
                preferences[SESSION_ID]
            }

        override suspend fun updateUserId(userId: Long) {
            dataStore.edit { preferences ->
                preferences[USER_ID] = userId
            }
        }

        override suspend fun updateSessionId(sessionId: Long) {
            dataStore.edit { preferences ->
                preferences[SESSION_ID] = sessionId
            }
        }

        override suspend fun clearSessionId() {
            dataStore.edit { preferences ->
                preferences.remove(SESSION_ID)
            }
        }

        companion object {
            val USER_ID = longPreferencesKey("USER_ID")
            val SESSION_ID = longPreferencesKey("SESSION_ID")
        }
    }
