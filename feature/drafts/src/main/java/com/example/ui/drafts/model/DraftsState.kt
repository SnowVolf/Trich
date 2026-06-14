package com.example.ui.drafts.model

import com.example.db.Draft

data class DraftsState(
    val isLoading: Boolean = false,
    val drafts: List<Draft> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
)
