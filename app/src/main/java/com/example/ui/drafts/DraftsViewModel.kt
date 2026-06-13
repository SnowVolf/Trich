package com.example.ui.drafts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.DvachRepository
import com.example.ui.drafts.model.DraftsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DraftsViewModel(private val repository: DvachRepository) : ViewModel() {
    private val _state = MutableStateFlow(DraftsState())
    val state: StateFlow<DraftsState> = _state.asStateFlow()

    fun loadDrafts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val drafts = repository.getDrafts()
            _state.value = _state.value.copy(isLoading = false, drafts = drafts)
        }
    }

    fun toggleSelectionMode(enabled: Boolean) {
        _state.value = _state.value.copy(
            isSelectionMode = enabled,
            selectedIds = if (!enabled) emptySet() else _state.value.selectedIds
        )
    }

    fun toggleSelection(id: String) {
        val currentSelection = _state.value.selectedIds.toMutableSet()
        if (currentSelection.contains(id)) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _state.value = _state.value.copy(
            selectedIds = currentSelection,
            isSelectionMode = currentSelection.isNotEmpty()
        )
    }

    fun clearAllDrafts() {
        viewModelScope.launch {
            repository.clearAllDrafts()
            loadDrafts()
            toggleSelectionMode(false)
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteDrafts(_state.value.selectedIds.toList())
            loadDrafts()
            toggleSelectionMode(false)
        }
    }
}
