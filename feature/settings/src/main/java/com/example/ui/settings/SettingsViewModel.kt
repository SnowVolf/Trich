package ru.svolf.trich.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.svolf.trich.ui.settings.model.SettingsRepository
import ru.svolf.trich.ui.settings.model.SettingsState

/**
 * ViewModel для управления экраном настроек приложения (выбор темы, размер шрифта, пароль).
 * @property repository Зависимость от SettingsRepository для чтения и записи настроек.
 */
class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    /**
     * Поток состояния настроек для UI.
     */
    val state: StateFlow<SettingsState> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    /**
     * Сохраняет новый пин-код/пароль для входа.
     * @param passcode Выбранный пользователем пароль.
     */
    fun updatePasscode(passcode: String) {
        viewModelScope.launch {
            repository.savePasscode(passcode)
        }
    }

    /**
     * Активирует экран с запросом пинкода при запуске. Использует задержку в 1 сек для имитации загрузки.
     */
    fun activatePasscode() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            repository.setPasscodeActivated(true)
        }
    }

    /**
     * Переключает текущую тему приложения (светлая/темная).
     */
    fun toggleTheme() {
        viewModelScope.launch {
            repository.setDarkTheme(!state.value.isDarkTheme)
        }
    }

    /**
     * Устанавливает кастомный размер шрифта в приложении.
     * @param size Новый размер текста в SP.
     */
    fun setFontSize(size: Int) {
        viewModelScope.launch {
            repository.setFontSize(size)
        }
    }

    /**
     * Переключает настройку фоновой проверки новых сообщений в избранных тредах командой WorkManager.
     */
    fun toggleBackgroundCheckFavorites() {
        viewModelScope.launch {
            repository.setBackgroundCheckFavorites(!state.value.backgroundCheckFavorites)
        }
    }
}
