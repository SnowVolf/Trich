package ru.svolf.trich.ui.drafts.model

import  ru.svolf.trich.db.Draft

data class DraftsState(
    val isLoading: Boolean = false,
    val drafts: List<Draft> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
)
