package com.example.api

import com.example.model.BoardSetting
import com.example.model.CaptchaIdResponse
import com.example.model.EmojiCaptchaShowResponse
import com.example.model.ThreadInfoResponse
import com.example.model.ThreadPostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс API 2ch.
 */
interface DvachApi {
    /**
     * Получить список доступных досок.
     * @return Список настроек досок.
     */
    @GET("api/mobile/v2/boards")
    suspend fun getBoards(): List<BoardSetting>

    /**
     * Получить каталог потоков на указанной доске.
     * @param board Код доски.
     * @return Ответ с каталогом потоков.
     */
    @GET("/{board}/catalog.json")
    suspend fun getCatalog(
        @Path("board") board: String,
    ): com.example.model.CatalogResponse

    /**
     * Получить посты потока через API Makaba.
     * @param board Код доски.
     * @param threadNum Номер потока.
     * @return Ответ с постами потока.
     */
    @GET("/{board}/res/{thread}.json")
    suspend fun getThreadPostsMakaba(
        @Path("board") board: String,
        @Path("thread") threadNum: Int,
    ): com.example.model.ThreadPostsMakabaResponse

    /**
     * Получить информацию о потоке.
     * @param board Код доски.
     * @param threadNum Номер потока.
     * @return Ответ с информацией о потоке.
     */
    @GET("api/mobile/v2/info/{board}/{thread}")
    suspend fun getThreadInfo(
        @Path("board") board: String,
        @Path("thread") threadNum: Int,
    ): ThreadInfoResponse

    /**
     * Получить идентификатор emoji-капчи.
     * @param board Код доски.
     * @param threadNum Номер потока.
     * @return Ответ с идентификатором капчи.
     */
    @GET("/api/captcha/emoji/id")
    suspend fun getEmojiCaptchaId(
        @Query("board") board: String,
        @Query("thread") threadNum: Int,
    ): CaptchaIdResponse

    /**
     * Отобразить emoji-капчу по её идентификатору.
     * @param id Идентификатор капчи.
     * @return Ответ с отображаемой капчей.
     */
    @GET("/api/captcha/emoji/show")
    suspend fun showEmojiCaptcha(
        @Query("id") id: String,
    ): EmojiCaptchaShowResponse

    /**
     * Проверить выбранные элементы emoji-капчи.
     * @param request Запрос с выбранными элементами капчи.
     * @return Ответ о результатах клика по капче.
     */
    @POST("/api/captcha/emoji/click")
    suspend fun clickEmojiCaptcha(
        @Body request: com.example.model.EmojiCaptchaClickRequest,
    ): EmojiCaptchaShowResponse

    /**
     * Опубликовать новое сообщение в поток.
     * @param captchaType Тип используемой капчи.
     * @param emojiCaptchaId Идентификатор emoji-капчи.
     * @param board Код доски.
     * @param thread Номер потока.
     * @param comment Текст комментария.
     * @param files Прикрепляемые файлы (опционально).
     * @return JsonObject с ответом сервера.
     */
    @Multipart
    @POST("/user/posting")
    suspend fun postNewMessage(
        @Part("captcha_type") captchaType: RequestBody,
        @Part("emoji_captcha_id") emojiCaptchaId: RequestBody?,
        @Part("board") board: RequestBody,
        @Part("thread") thread: RequestBody?,
        @Part("comment") comment: RequestBody,
        @Part("subject") subject: RequestBody?,
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("tags") tags: RequestBody? = null,
        @Part("icon") icon: RequestBody? = null,
        @Part("op_mark") opMark: RequestBody? = null,
        @Part files: List<MultipartBody.Part>? = null,
    ): com.example.model.PostingResponse
}
