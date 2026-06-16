package ru.svolf.trich.ui.newthread.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.newthread.NewThreadViewModel

val newThreadModule = module {
    viewModel { NewThreadViewModel(get(), androidContext()) }
}
