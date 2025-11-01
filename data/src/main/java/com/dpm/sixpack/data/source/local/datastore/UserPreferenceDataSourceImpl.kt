package com.dpm.sixpack.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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

        override val isOnboardingComplete: Flow<Boolean> =
            dataStore.data.map { preferences ->
                preferences[ONBOARDING_COMPLETE] ?: false
            }

        override val accessToken: Flow<String?> =
            dataStore.data.map { preferences ->
                preferences[ACCESS_TOKEN]
            }

        override val refreshToken: Flow<String?> =
            dataStore.data.map { preferences ->
                preferences[REFRESH_TOKEN]
            }

        override suspend fun updateOnboardingComplete(isComplete: Boolean) {
            dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETE] = isComplete
            }
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

        override suspend fun updateAccessToken(token: String) {
            dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN] = token
            }
        }

        override suspend fun updateRefreshToken(token: String) {
            dataStore.edit { preferences ->
                preferences[REFRESH_TOKEN] = token
            }
        }

        override suspend fun clearTokens() {
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
            }
        }

        companion object {
            val USER_ID = longPreferencesKey("USER_ID")
            val SESSION_ID = longPreferencesKey("SESSION_ID")
            val ONBOARDING_COMPLETE = booleanPreferencesKey("ONBOARDING_COMPLETE")
            val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
            val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
        }
    }
