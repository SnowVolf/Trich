package com.example.ui.thread.di

import com.example.ui.thread.ThreadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val threadModule = module {
    viewModel {
        ThreadViewModel(
            repository = get()
        )
    }
}
