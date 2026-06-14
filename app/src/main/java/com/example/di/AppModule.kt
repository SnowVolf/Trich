package com.example.di

import androidx.room.Room
import com.example.api.DvachApi
import com.example.db.AppDatabase
import com.example.repository.DvachRepository
import com.example.ui.boards.BoardsViewModel
import com.example.ui.drafts.DraftsViewModel
import com.example.ui.history.HistoryViewModel
import com.example.ui.thread.ThreadViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val appModule = module {
}
