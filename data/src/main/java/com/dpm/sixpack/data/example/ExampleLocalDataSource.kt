package com.dpm.sixpack.data.example

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleLocalDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore by preferencesDataStore(
            name = PreferencesConstants.EXAMPLE_PREFERENCES_NAME,
        )

        fun getCount(): Flow<Int> =
            context.dataStore.data.map { pref ->
                pref[PreferencesConstants.COUNTER_KEY] ?: 0
            }

        suspend fun changeCount(amount: Int) {
            context.dataStore.edit { preferences ->
                val currentCount = preferences[PreferencesConstants.COUNTER_KEY] ?: 0
                preferences[PreferencesConstants.COUNTER_KEY] = currentCount + amount
            }
        }

        private object PreferencesConstants {
            const val EXAMPLE_PREFERENCES_NAME = "example_preferences"
            val COUNTER_KEY = intPreferencesKey("counter_key")
        }
    }
