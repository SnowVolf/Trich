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
    @SerialName("bump_limit") val bumpLimit: Int = 500
)

/**
 * Ответ со списком досок.
 */
// The API path is /api/mobile/v2/boards returning array of Board setting directly?
// OpenAPI says: type: array items: $ref: '#/components/schemas/Board'
