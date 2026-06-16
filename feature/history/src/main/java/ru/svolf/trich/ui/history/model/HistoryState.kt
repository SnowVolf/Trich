package ru.svolf.trich.ui.history.model

import  ru.svolf.trich.db.VisitedThread

data class HistoryState(
    val isLoading: Boolean = false,
    val threads: List<VisitedThread> = emptyList(),
)
