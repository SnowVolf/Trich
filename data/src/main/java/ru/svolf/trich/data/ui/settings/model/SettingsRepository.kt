package ru.svolf.trich.ui.settings.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val PASSCODE = stringPreferencesKey("passcode")
    private val PASSCODE_ACTIVATED = booleanPreferencesKey("passcode_activated")
    private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    private val FONT_SIZE = intPreferencesKey("font_size")
    private val BACKGROUND_CHECK_FAVORITES = booleanPreferencesKey("background_check_favs")

    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { preferences ->
        SettingsState(
            passcode = preferences[PASSCODE] ?: "",
            passcodeActivated = preferences[PASSCODE_ACTIVATED] ?: false,
            isDarkTheme = preferences[IS_DARK_THEME] ?: true,
            fontSize = preferences[FONT_SIZE] ?: 16,
            backgroundCheckFavorites = preferences[BACKGROUND_CHECK_FAVORITES] ?: false
        )
    }

    suspend fun savePasscode(passcode: String) {
        context.dataStore.edit { it[PASSCODE] = passcode }
    }

    suspend fun setPasscodeActivated(activated: Boolean) {
        context.dataStore.edit { it[PASSCODE_ACTIVATED] = activated }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[IS_DARK_THEME] = isDark }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { it[FONT_SIZE] = size }
    }

    suspend fun setBackgroundCheckFavorites(enabled: Boolean) {
        context.dataStore.edit { it[BACKGROUND_CHECK_FAVORITES] = enabled }
    }
}
