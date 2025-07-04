package com.dunesaero.missionnotes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create a DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

enum class ThemeSetting {
    SYSTEM, LIGHT, DARK
}

@Singleton
class ThemeSettingsManager @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val themeSettingFlow: Flow<ThemeSetting> = context.dataStore.data
        .map { preferences ->
            ThemeSetting.valueOf(preferences[PreferencesKeys.THEME_MODE] ?: ThemeSetting.SYSTEM.name)
        }

    suspend fun setThemeSetting(themeSetting: ThemeSetting) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeSetting.name
        }
    }
}
