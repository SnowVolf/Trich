package com.example.ui.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.boards.model.BoardsState
import com.example.repository.DvachRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием экрана списка досок.
 * @property repository Репозиторий для получения данных досок.
 */
class BoardsViewModel(private val repository: DvachRepository) : ViewModel() {
    private val _state = MutableStateFlow(BoardsState())

    /**
     * Текущее состояние экрана. Доступно для прослушивания (read-only).
     */
    val state: StateFlow<BoardsState> = _state.asStateFlow()

    init {
        loadBoards()
    }

    /**
     * Загружает список доступных досок.
     */
    private fun loadBoards() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val boards = repository.getBoards()
                _state.value = _state.value.copy(isLoading = false, boards = boards)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }
}
