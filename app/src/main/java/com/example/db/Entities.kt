package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность посещенного треда.
 */
@Entity(tableName = "visited_threads")
data class VisitedThread(
    @PrimaryKey val id: String, // board_threadNum
    val board: String,
    val threadNum: Int,
    val title: String,
    val lastVisitedDate: Long
)

/**
 * Сущность избранного треда.
 */
@Entity(tableName = "favorite_threads")
data class FavoriteThread(
    @PrimaryKey val id: String, // board_threadNum
    val board: String,
    val threadNum: Int,
    val title: String,
    val addedDate: Long
)

/**
 * Сущность черновика сообщения.
 */
@Entity(tableName = "drafts")
data class Draft(
    @PrimaryKey val id: String, // board_threadNum
    val board: String,
    val threadNum: Int,
    val threadTitle: String,
    val text: String,
    val creationDate: Long
)
