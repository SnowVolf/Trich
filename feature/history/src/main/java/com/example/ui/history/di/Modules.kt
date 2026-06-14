package com.example.ui.history.di

import com.example.ui.history.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val historyModule = module {
    viewModel { HistoryViewModel(get()) }
}
