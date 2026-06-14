package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Модель поста.
 */
@Serializable
data class PostInfo(
    @SerialName("num") val num: Int = 0,
    @SerialName("parent") val parent: Int = 0,
    @SerialName("board") val board: String = "",
    @SerialName("timestamp") val timestamp: Long = 0L,
    @SerialName("lasthit") val lasthit: Long = 0L,
    @SerialName("date") val date: String = "",
    @SerialName("subject") val subject: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("views") val views: Int = 0,
    @SerialName("sticky") val sticky: Int = 0,
    @SerialName("endless") val endless: Int = 0,
    @SerialName("closed") val closed: Int = 0,
    @SerialName("banned") val banned: Int = 0,
    @SerialName("op") val op: Int = 0,
    @SerialName("files") val files: List<FileInfo>? = null,
)

/**
 * Модель файла.
 */
@Serializable
data class FileInfo(
    @SerialName("name") val name: String = "",
    @SerialName("fullname") val fullname: String = "",
    @SerialName("displayname") val displayname: String = "",
    @SerialName("path") val path: String = "",
    @SerialName("thumbnail") val thumbnail: String = "",
    @SerialName("type") val type: Int = 0,
    @SerialName("size") val size: Int = 0,
)

/**
 * Ошибка API.
 */
@Serializable
data class ApiError(
    @SerialName("code") val code: Int = 0,
    @SerialName("message") val message: String = "",
)

/**
 * Ответ постов треда.
 */
@Serializable
data class ThreadPostsResponse(
    @SerialName("posts") val posts: List<PostInfo>? = null,
    @SerialName("error") val error: ApiError? = null,
)

@Serializable
data class ThreadPostsMakabaResponse(
    @SerialName("threads") val threads: List<MakabaThread>? = null,
    @SerialName("Error") val error: String? = null,
)

@Serializable
data class MakabaThread(
    @SerialName("posts") val posts: List<PostInfo>? = null,
)

/**
 * Полный тред. Информация
 */
@Serializable
data class ThreadInfoResponse(
    @SerialName("result") val result: Int = 0,
    @SerialName("thread") val thread: ThreadInfo? = null,
    @SerialName("error") val error: ApiError? = null,
)

/**
 * Инфа о треде
 */
@Serializable
data class ThreadInfo(
    @SerialName("num") val num: Int = 0,
    @SerialName("timestamp") val timestamp: Long = 0L,
    @SerialName("posts") val posts: Int = 0,
)

/**
 * Ответ капчи emoji.
 */
@Serializable
data class CaptchaIdResponse(
    @SerialName("result") val result: Int = 0,
    @SerialName("type") val type: String = "",
    @SerialName("id") val id: String = "",
    @SerialName("input") val input: String? = null,
)

@Serializable
data class EmojiCaptchaClickRequest(
    @SerialName("captchaTokenID") val captchaTokenID: String,
    @SerialName("emojiNumber") val emojiNumber: Int,
)

@Serializable
data class EmojiCaptchaShowResponse(
    @SerialName("image") val image: String? = null,
    @SerialName("keyboard") val keyboard: List<String>? = null,
    @SerialName("success") val success: String? = null,
)

@Serializable
data class CatalogResponse(
    @SerialName("threads") val threads: List<CatalogThread>? = null,
)

@Serializable
data class CatalogThread(
    @SerialName("num") val num: Int = 0,
    @SerialName("subject") val subject: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("files") val files: List<FileInfo>? = null,
)

/**
 * Ответ на создание поста/треда.
 */
@Serializable
data class PostingResponse(
    @SerialName("result") val result: Int = 0,
    @SerialName("error") val error: ApiError? = null,
    @SerialName("num") val num: Int? = null,
    @SerialName("thread") val thread: Int? = null,
)
