package ru.svolf.trich.ui.boards.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.boards.BoardsViewModel

val boardsModule = module {
    viewModel { BoardsViewModel(get()) }
}
