package com.example.repository

import com.example.api.DvachApi
import com.example.db.Draft
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.db.DraftDao
import com.example.db.VisitedThread
import com.example.db.VisitedThreadDao
import com.example.db.FavoriteThread
import com.example.db.FavoriteThreadDao
import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Репозиторий для работы с данными.
 */
class DvachRepository(
    private val api: DvachApi,
    private val visitedThreadDao: VisitedThreadDao,
    private val draftDao: DraftDao,
    private val favoriteThreadDao: FavoriteThreadDao,
    private val apiUrlProvider: com.example.api.ApiUrlProvider
) {
    suspend fun getBoards(): List<Board> {
        return withContext(Dispatchers.IO) {
            api.getBoards().map { it.toDomain() }
        }
    }

    suspend fun getThreads(board: String): List<ThreadSummary> {
        return withContext(Dispatchers.IO) {
            val response = api.getCatalog(board)
            response.threads?.map { it.toDomain(apiUrlProvider.baseUrl) } ?: emptyList()
        }
    }

    suspend fun getThreadPosts(board: String, threadNum: Int, lastNum: Int = 0): List<Post> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getThreadPostsMakaba(board, threadNum)
                val posts = response.threads?.firstOrNull()?.posts ?: emptyList()

                // Если мы зашли в тред, обновим дату посещения и информацию
                if (posts.isNotEmpty()) {
                    val opPost = posts.first()
                    visitedThreadDao.insertOrUpdate(
                        VisitedThread(
                            id = "${board}_$threadNum",
                            board = board,
                            threadNum = threadNum,
                            title = opPost.subject.ifBlank { opPost.comment.take(50) },
                            lastVisitedDate = System.currentTimeMillis()
                        )
                    )
                }
                posts.map { it.toDomain(apiUrlProvider.baseUrl) }
            } catch(e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getHistory(): List<VisitedThread> = withContext(Dispatchers.IO) {
        visitedThreadDao.getAll()
    }

    suspend fun getDrafts(): List<Draft> = withContext(Dispatchers.IO) {
        draftDao.getAll()
    }

    suspend fun saveDraft(board: String, threadNum: Int, text: String, title: String) = withContext(Dispatchers.IO) {
        draftDao.insertOrUpdate(
            Draft(
                id = "${board}_$threadNum",
                board = board,
                threadNum = threadNum,
                threadTitle = title,
                text = text,
                creationDate = System.currentTimeMillis()
            )
        )
    }

    suspend fun getDraft(board: String, threadNum: Int): Draft? = withContext(Dispatchers.IO) {
        draftDao.getDraftById("${board}_$threadNum")
    }

    suspend fun clearAllDrafts() = withContext(Dispatchers.IO) {
        draftDao.deleteAll()
    }

    suspend fun deleteDrafts(ids: List<String>) = withContext(Dispatchers.IO) {
        draftDao.deleteByIds(ids)
    }

    suspend fun getCaptchaId(board: String, threadNum: Int) = withContext(Dispatchers.IO) {
        api.getEmojiCaptchaId(board, threadNum)
    }

    suspend fun showEmojiCaptcha(id: String) = withContext(Dispatchers.IO) {
        api.showEmojiCaptcha(id)
    }

    suspend fun clickEmojiCaptcha(id: String, index: Int) = withContext(Dispatchers.IO) {
        api.clickEmojiCaptcha(com.example.model.EmojiCaptchaClickRequest(id, index))
    }

    suspend fun postMessage(board: String, threadNum: Int, comment: String, captchaId: String) = withContext(Dispatchers.IO) {
        val captchaType = "emoji_captcha".toRequestBody(okhttp3.MultipartBody.FORM)
        val emojiCaptchaId = captchaId.toRequestBody(okhttp3.MultipartBody.FORM)
        val boardPart = board.toRequestBody(okhttp3.MultipartBody.FORM)
        val threadPart = threadNum.toString().toRequestBody(okhttp3.MultipartBody.FORM)
        val commentPart = comment.toRequestBody(okhttp3.MultipartBody.FORM)
        api.postNewMessage(captchaType, emojiCaptchaId, boardPart, threadPart, commentPart, null)
    }

    suspend fun clearAllFavorites() = withContext(Dispatchers.IO) {
        favoriteThreadDao.deleteAll()
    }

    suspend fun deleteFavorites(ids: List<String>) = withContext(Dispatchers.IO) {
        favoriteThreadDao.deleteByIds(ids)
    }

    suspend fun getFavorites(): List<FavoriteThread> = withContext(Dispatchers.IO) {
        favoriteThreadDao.getAll()
    }

    suspend fun isFavorite(board: String, threadNum: Int): Boolean = withContext(Dispatchers.IO) {
        favoriteThreadDao.isFavorite("${board}_$threadNum")
    }

    suspend fun updateFavoritePostsCount(board: String, threadNum: Int, count: Int) = withContext(Dispatchers.IO) {
        val id = "${board}_$threadNum"
        if (favoriteThreadDao.isFavorite(id)) {
            val fav = favoriteThreadDao.getAll().find { it.id == id }
            if (fav != null) {
                favoriteThreadDao.insertOrUpdate(fav.copy(lastKnownPostsCount = count))
            }
        }
    }

    suspend fun toggleFavorite(board: String, threadNum: Int, title: String, postsCount: Int = 0): Boolean = withContext(Dispatchers.IO) {
        val id = "${board}_$threadNum"
        if (favoriteThreadDao.isFavorite(id)) {
            favoriteThreadDao.deleteById(id)
            false
        } else {
            favoriteThreadDao.insertOrUpdate(
                FavoriteThread(
                    id = id,
                    board = board,
                    threadNum = threadNum,
                    title = title,
                    addedDate = System.currentTimeMillis(),
                    lastKnownPostsCount = postsCount
                )
            )
            true
        }
    }
}
