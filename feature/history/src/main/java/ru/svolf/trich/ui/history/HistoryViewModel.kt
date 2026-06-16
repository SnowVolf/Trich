package ru.svolf.trich.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.svolf.trich.repository.DvachRepository
import ru.svolf.trich.ui.history.model.HistoryState

/**
 * ViewModel для управления экраном истории посещенных тредов.
 * @property repository Зависимость от DvachRepository для загрузки данных.
 */
class HistoryViewModel(private val repository: DvachRepository) : ViewModel() {
    private val _state = MutableStateFlow(HistoryState())

    /**
     * Поток состояния для UI экрана истории.
     */
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Загружает историю просмотренных тредов из репозитория.
     */
    fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val threads = repository.getHistory()
                _state.value = _state.value.copy(isLoading = false, threads = threads)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
