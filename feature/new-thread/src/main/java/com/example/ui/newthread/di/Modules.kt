package com.example.ui.newthread.di

import com.example.ui.newthread.NewThreadViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newThreadModule = module {
    viewModel { NewThreadViewModel(get(), androidContext()) }
}
