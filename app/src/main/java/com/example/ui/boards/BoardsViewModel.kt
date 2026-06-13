package com.example.ui.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.boards.model.BoardsState
import com.example.repository.DvachRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoardsViewModel(private val repository: DvachRepository) : ViewModel() {
    private val _state = MutableStateFlow(BoardsState())
    val state: StateFlow<BoardsState> = _state.asStateFlow()

    init {
        loadBoards()
    }

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
