package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Модель доски.
 */
@Serializable
data class BoardSetting(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("info") val info: String = "",
    @SerialName("bump_limit") val bumpLimit: Int = 500,
    @SerialName("enable_posting") val enablePosting: Boolean = true,
    @SerialName("enable_names") val enableNames: Boolean = false,
    @SerialName("enable_trips") val enableTrips: Boolean = false,
    @SerialName("enable_subject") val enableSubject: Boolean = false,
    @SerialName("enable_sage") val enableSage: Boolean = false,
    @SerialName("enable_icons") val enableIcons: Boolean = false,
    @SerialName("enable_flags") val enableFlags: Boolean = false,
    @SerialName("enable_dices") val enableDices: Boolean = false,
    @SerialName("enable_shield") val enableShield: Boolean = false,
    @SerialName("enable_thread_tags") val enableThreadTags: Boolean = false,
    @SerialName("enable_likes") val enableLikes: Boolean = false,
    @SerialName("enable_oekaki") val enableOekaki: Boolean = false,
)

/**
 * Ответ со списком досок.
 */
// The API path is /api/mobile/v2/boards returning array of Board setting directly?
// OpenAPI says: type: array items: $ref: '#/components/schemas/Board'
