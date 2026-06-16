package ru.svolf.trich.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Boards : NavKey

@Serializable
data object History : NavKey

@Serializable
data object Drafts : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data object Favorites : NavKey

@Serializable
data class ThreadDest(val board: String, val threadNum: Int, val scrollToPost: Int? = null) : NavKey

@Serializable
data class ThreadListDest(val board: String) : NavKey

@Serializable
data class GalleryDest(val urls: List<String>, val initialIndex: Int) : NavKey

@Serializable
data class NewThreadDest(val board: String) : NavKey

