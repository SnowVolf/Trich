package ru.svolf.trich.domain.model

data class Board(
    val id: String,
    val name: String,
    val category: String,
    val info: String,
    val bumpLimit: Int = 500,
    val enablePosting: Boolean = true,
)

data class ThreadSummary(
    val num: Int,
    val subject: String,
    val comment: String,
    val files: List<Attachment>,
)

data class Post(
    val num: Int,
    val parent: Int,
    val board: String,
    val timestamp: Long,
    val date: String,
    val subject: String,
    val comment: String,
    val op: Int,
    val files: List<Attachment>,
)

data class Attachment(
    val path: String,
    val fullname: String,
    val thumbnail: String,
    val type: Int,
    val size: Int,
)
