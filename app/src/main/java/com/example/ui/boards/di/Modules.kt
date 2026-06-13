package com.example.ui.boards.di

import com.example.ui.boards.BoardsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val boardsModule = module {
    viewModel { BoardsViewModel(get()) }
}
