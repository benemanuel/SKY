package com.sky.app.data

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sky_preferences")

class PreferencesRepository(private val application: Application) {

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val isDarkMode: Flow<Boolean> = application.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }

    suspend fun setDarkMode(isDark: Boolean) {
        application.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
}

private val android.content.Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sky_preferences")
