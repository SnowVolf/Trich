package ru.svolf.trich.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import ru.svolf.trich.api.DvachApi
import ru.svolf.trich.db.Draft
import ru.svolf.trich.db.DraftDao
import ru.svolf.trich.db.FavoriteThread
import ru.svolf.trich.db.FavoriteThreadDao
import ru.svolf.trich.db.VisitedThread
import ru.svolf.trich.db.VisitedThreadDao
import ru.svolf.trich.domain.model.Board
import ru.svolf.trich.domain.model.Post
import ru.svolf.trich.domain.model.ThreadSummary
import ru.svolf.trich.domain.model.toDomain

/**
 * Репозиторий для работы с данными.
 */
class DvachRepository(
    private val api: DvachApi,
    private val visitedThreadDao: VisitedThreadDao,
    private val draftDao: DraftDao,
    private val favoriteThreadDao: FavoriteThreadDao,
    private val apiUrlProvider: ru.svolf.trich.api.ApiUrlProvider,
) {
    /**
     * Возвращает список всех досок.
     * @return Список досок Board.
     */
    suspend fun getBoards(): List<Board> {
        return withContext(Dispatchers.IO) {
            api.getBoards().map { it.toDomain() }
        }
    }

    /**
     * Возвращает каталог тредов для выбранной доски.
     * @param board код доски.
     * @return Список тредов ThreadSummary.
     */
    suspend fun getThreads(board: String): List<ThreadSummary> {
        return withContext(Dispatchers.IO) {
            val response = api.getCatalog(board)
            response.threads?.map { it.toDomain(apiUrlProvider.baseUrl) } ?: emptyList()
        }
    }

    /**
     * Возвращает посты внутри выбранного треда.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @param lastNum Номер последнего поста.
     * @return Список постов в треде.
     */
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
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Возвращает историю просмотра тредов.
     * @return Список истории посещений VisitedThread.
     */
    suspend fun getHistory(): List<VisitedThread> = withContext(Dispatchers.IO) {
        visitedThreadDao.getAll()
    }

    /**
     * Возвращает сохраненные черновики сообщений.
     * @return Список черновиков.
     */
    suspend fun getDrafts(): List<Draft> = withContext(Dispatchers.IO) {
        draftDao.getAll()
    }

    /**
     * Сохраняет черновик сообщения в базу данных.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @param text Текст черновика.
     * @param title Заголовок треда.
     */
    suspend fun saveDraft(board: String, threadNum: Int, text: String, title: String) =
        withContext(Dispatchers.IO) {
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

    /**
     * Узнать, сохранен ли черновик для выбранного треда.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @return Черновик, если найден, или null.
     */
    suspend fun getDraft(board: String, threadNum: Int): Draft? = withContext(Dispatchers.IO) {
        draftDao.getDraftById("${board}_$threadNum")
    }

    /**
     * Очищает все черновики.
     */
    suspend fun clearAllDrafts() = withContext(Dispatchers.IO) {
        draftDao.deleteAll()
    }

    /**
     * Удалят выбранные черновики.
     * @param ids Список ID черновиков на удаление.
     */
    suspend fun deleteDrafts(ids: List<String>) = withContext(Dispatchers.IO) {
        draftDao.deleteByIds(ids)
    }

    /**
     * Запрашивает ID новой emoji капчи.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @return Ответ с ID капчи.
     */
    suspend fun getCaptchaId(board: String, threadNum: Int) = withContext(Dispatchers.IO) {
        api.getEmojiCaptchaId(board, threadNum)
    }

    /**
     * Запрашивает вариант выбора emoji капчи.
     * @param id ID капчи.
     * @return Экран капчи (EmojiCaptchaShowResponse).
     */
    suspend fun showEmojiCaptcha(id: String) = withContext(Dispatchers.IO) {
        val response = api.showEmojiCaptcha(id)
        val baseUrl = apiUrlProvider.baseUrl.removeSuffix("/")
        response.copy(
            image = response.image?.let { if (!it.startsWith("http")) "$baseUrl/${it.removePrefix("/")}" else it },
            keyboard = response.keyboard?.map {
                if (!it.startsWith("http")) "$baseUrl/${
                    it.removePrefix(
                        "/"
                    )
                }" else it
            }
        )
    }

    /**
     * Совершает клик на капче с emoji по индексу.
     * @param id ID капчи.
     * @param index Индекс нажатого варианта emoji.
     * @return Следующий шаг процесса прохождения капчи.
     */
    suspend fun clickEmojiCaptcha(id: String, index: Int) = withContext(Dispatchers.IO) {
        val response =
            api.clickEmojiCaptcha(ru.svolf.trich.model.EmojiCaptchaClickRequest(id, index))
        val baseUrl = apiUrlProvider.baseUrl.removeSuffix("/")
        response.copy(
            image = response.image?.let { if (!it.startsWith("http")) "$baseUrl/${it.removePrefix("/")}" else it },
            keyboard = response.keyboard?.map {
                if (!it.startsWith("http")) "$baseUrl/${
                    it.removePrefix(
                        "/"
                    )
                }" else it
            }
        )
    }

    /**
     * Выполняет постинг сообщения в тред.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @param comment Комментарий (текст поста).
     * @param captchaId ID пройденной капчи.
     * @return Ответ сервера о постинге.
     */
    suspend fun postMessage(board: String, threadNum: Int, comment: String, captchaId: String) =
        withContext(Dispatchers.IO) {
            val captchaType = "emoji_captcha".toRequestBody(okhttp3.MultipartBody.FORM)
            val emojiCaptchaId = captchaId.toRequestBody(okhttp3.MultipartBody.FORM)
            val boardPart = board.toRequestBody(okhttp3.MultipartBody.FORM)
            val threadPart = threadNum.toString().toRequestBody(okhttp3.MultipartBody.FORM)
            val commentPart = comment.toRequestBody(okhttp3.MultipartBody.FORM)
            api.postNewMessage(
                captchaType = captchaType,
                emojiCaptchaId = emojiCaptchaId,
                board = boardPart,
                thread = threadPart,
                comment = commentPart,
                subject = null,
                name = null,
                email = null,
                tags = null,
                icon = null,
                opMark = null,
                files = null
            )
        }

    /**
     * Выполняет создание нового треда.
     * @param board Код доски.
     * @param subject Заголовок.
     * @param comment Контент.
     * @param captchaId ID пройденной капчи.
     * @return Ответ сервера о постинге.
     */
    suspend fun createThread(
        board: String,
        subject: String,
        comment: String,
        captchaId: String,
        files: List<okhttp3.MultipartBody.Part>? = null,
    ) = withContext(Dispatchers.IO) {
        val captchaType = "emoji_captcha".toRequestBody(okhttp3.MultipartBody.FORM)
        val emojiCaptchaId = captchaId.toRequestBody(okhttp3.MultipartBody.FORM)
        val boardPart = board.toRequestBody(okhttp3.MultipartBody.FORM)
        val commentPart = comment.toRequestBody(okhttp3.MultipartBody.FORM)
        val subjectPart = subject.toRequestBody(okhttp3.MultipartBody.FORM)
        api.postNewMessage(
            captchaType = captchaType,
            emojiCaptchaId = emojiCaptchaId,
            board = boardPart,
            thread = null,
            comment = commentPart,
            subject = subjectPart,
            name = null,
            email = null,
            tags = null,
            icon = null,
            opMark = null,
            files = files
        )
    }

    /**
     * Очищает список избранных тредов.
     */
    suspend fun clearAllFavorites() = withContext(Dispatchers.IO) {
        favoriteThreadDao.deleteAll()
    }

    /**
     * Удаляет указанные треды из избранного.
     * @param ids Список ID выбранных тредов.
     */
    suspend fun deleteFavorites(ids: List<String>) = withContext(Dispatchers.IO) {
        favoriteThreadDao.deleteByIds(ids)
    }

    /**
     * Возвращает список всех избранных тредов.
     * @return Список FavoriteThread.
     */
    suspend fun getFavorites(): List<FavoriteThread> = withContext(Dispatchers.IO) {
        favoriteThreadDao.getAll()
    }

    /**
     * Проверяет добавлен ли указанный тред в избранное.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @return Является ли тред избранным.
     */
    suspend fun isFavorite(board: String, threadNum: Int): Boolean = withContext(Dispatchers.IO) {
        favoriteThreadDao.isFavorite("${board}_$threadNum")
    }

    /**
     * Обновляет количество постов в избранном треде.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @param count Новое количество постов в треде.
     */
    suspend fun updateFavoritePostsCount(board: String, threadNum: Int, count: Int) =
        withContext(Dispatchers.IO) {
            val id = "${board}_$threadNum"
            if (favoriteThreadDao.isFavorite(id)) {
                val fav = favoriteThreadDao.getAll().find { it.id == id }
                if (fav != null) {
                    favoriteThreadDao.insertOrUpdate(fav.copy(lastKnownPostsCount = count))
                }
            }
        }

    /**
     * Переключает статус 'добавлено в избранное' для выбранного треда.
     * @param board Код доски.
     * @param threadNum Номер треда.
     * @param title Заголовок или текст.
     * @param postsCount Ориентировочное количество постов.
     * @return True если тред добавлен, False - если убран из избранного.
     */
    suspend fun toggleFavorite(
        board: String,
        threadNum: Int,
        title: String,
        postsCount: Int = 0,
    ): Boolean = withContext(Dispatchers.IO) {
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
