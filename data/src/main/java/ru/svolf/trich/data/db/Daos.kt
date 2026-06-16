package ru.svolf.trich.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO для посещенных тредов.
 */
@Dao
interface VisitedThreadDao {
    /**
     * Получает все посещенные потоки, отсортированные по дате последнего посещения.
     * @return Список посещенных потоков.
     */
    @Query("SELECT * FROM visited_threads ORDER BY lastVisitedDate DESC")
    suspend fun getAll(): List<VisitedThread>

    /**
     * Вставляет новый посещенный поток или обновляет существующий.
     * @param thread Информация о посещенном потоке.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(thread: VisitedThread)
}

/**
 * DAO для избранных тредов.
 */
@Dao
interface FavoriteThreadDao {
    /**
     * Получает все избранные потоки, отсортированные по дате добавления.
     * @return Список избранных потоков.
     */
    @Query("SELECT * FROM favorite_threads ORDER BY addedDate DESC")
    suspend fun getAll(): List<FavoriteThread>

    /**
     * Добавляет поток в избранное или обновляет существующую запись.
     * @param thread Избранный поток.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(thread: FavoriteThread)

    /**
     * Удаляет поток из избранного по идентификатору.
     * @param id Уникальный идентификатор потока.
     */
    @Query("DELETE FROM favorite_threads WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Проверяет, добавлен ли поток в избранное.
     * @param id Уникальный идентификатор потока.
     * @return Возвращает true, если поток в избранном, иначе false.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_threads WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    /**
     * Удаляет несколько потоков из избранного по списку идентификаторов.
     * @param ids Список идентификаторов.
     */
    @Query("DELETE FROM favorite_threads WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    /**
     * Очищает список избранных потоков.
     */
    @Query("DELETE FROM favorite_threads")
    suspend fun deleteAll()
}

/**
 * DAO для черновиков.
 */
@Dao
interface DraftDao {
    /**
     * Получает все черновики, отсортированные по дате создания.
     * @return Список черновиков.
     */
    @Query("SELECT * FROM drafts ORDER BY creationDate DESC")
    suspend fun getAll(): List<Draft>

    /**
     * Добавляет новый черновик или обновляет существующий.
     * @param draft Данные черновика.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(draft: Draft)

    /**
     * Удаляет черновики по списку идентификаторов.
     * @param ids Список идентификаторов.
     */
    @Query("DELETE FROM drafts WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    /**
     * Удаляет все черновики.
     */
    @Query("DELETE FROM drafts")
    suspend fun deleteAll()

    /**
     * Возвращает черновик по его идентификатору.
     * @param id Уникальный идентификатор черновика.
     * @return Найденный черновик или null.
     */
    @Query("SELECT * FROM drafts WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: String): Draft?
}
