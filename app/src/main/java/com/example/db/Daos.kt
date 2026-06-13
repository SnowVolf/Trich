package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO для посещенных тредов.
 */
@Dao
interface VisitedThreadDao {
    @Query("SELECT * FROM visited_threads ORDER BY lastVisitedDate DESC")
    suspend fun getAll(): List<VisitedThread>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(thread: VisitedThread)
}

/**
 * DAO для избранных тредов.
 */
@Dao
interface FavoriteThreadDao {
    @Query("SELECT * FROM favorite_threads ORDER BY addedDate DESC")
    suspend fun getAll(): List<FavoriteThread>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(thread: FavoriteThread)

    @Query("DELETE FROM favorite_threads WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_threads WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Query("DELETE FROM favorite_threads WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM favorite_threads")
    suspend fun deleteAll()
}

/**
 * DAO для черновиков.
 */
@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts ORDER BY creationDate DESC")
    suspend fun getAll(): List<Draft>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(draft: Draft)

    @Query("DELETE FROM drafts WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM drafts")
    suspend fun deleteAll()

    @Query("SELECT * FROM drafts WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: String): Draft?
}
