package ru.svolf.trich.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * База данных приложения.
 */
@Database(
    entities = [VisitedThread::class, Draft::class, FavoriteThread::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun visitedThreadDao(): VisitedThreadDao
    abstract fun draftDao(): DraftDao
    abstract fun favoriteThreadDao(): FavoriteThreadDao
}
