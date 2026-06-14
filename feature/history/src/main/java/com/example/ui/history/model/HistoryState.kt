package com.example.ui.history.model

import com.example.db.VisitedThread

data class HistoryState(
    val isLoading: Boolean = false,
    val threads: List<VisitedThread> = emptyList(),
)
