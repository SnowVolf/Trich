package com.example.ui.threadlist.di

import com.example.ui.threadlist.ThreadListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val threadListModule = module {
    viewModel {
        ThreadListViewModel(
            repository = get(),
            settingsRepository = get()
        )
    }
}
