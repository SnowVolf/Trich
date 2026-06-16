package ru.svolf.trich.ui.history.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.history.HistoryViewModel

val historyModule = module {
    viewModel { HistoryViewModel(get()) }
}
