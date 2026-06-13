package com.example.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.settings.model.SettingsRepository
import com.example.ui.settings.model.SettingsState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val state: StateFlow<SettingsState> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    fun updatePasscode(passcode: String) {
        viewModelScope.launch {
            repository.savePasscode(passcode)
        }
    }

    fun activatePasscode() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            repository.setPasscodeActivated(true)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            repository.setDarkTheme(!state.value.isDarkTheme)
        }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch {
            repository.setFontSize(size)
        }
    }
}
