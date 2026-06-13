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
    @GET("api/mobile/v2/boards")
    suspend fun getBoards(): List<BoardSetting>

    @GET("/{board}/catalog.json")
    suspend fun getCatalog(
        @Path("board") board: String
    ): com.example.model.CatalogResponse

    @GET("/{board}/res/{thread}.json")
    suspend fun getThreadPostsMakaba(
        @Path("board") board: String,
        @Path("thread") threadNum: Int
    ): com.example.model.ThreadPostsMakabaResponse

    @GET("api/mobile/v2/info/{board}/{thread}")
    suspend fun getThreadInfo(
        @Path("board") board: String,
        @Path("thread") threadNum: Int
    ): ThreadInfoResponse

    @GET("/api/captcha/emoji/id")
    suspend fun getEmojiCaptchaId(
        @Query("board") board: String,
        @Query("thread") threadNum: Int
    ): CaptchaIdResponse

    @GET("/api/captcha/emoji/show")
    suspend fun showEmojiCaptcha(
        @Query("id") id: String
    ): EmojiCaptchaShowResponse

    @POST("/api/captcha/emoji/click")
    suspend fun clickEmojiCaptcha(
        @Body request: com.example.model.EmojiCaptchaClickRequest
    ): EmojiCaptchaShowResponse

    @Multipart
    @POST("/user/posting")
    suspend fun postNewMessage(
        @Part("captcha_type") captchaType: RequestBody,
        @Part("emoji_captcha_id") emojiCaptchaId: RequestBody?,
        @Part("board") board: RequestBody,
        @Part("thread") thread: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part files: List<MultipartBody.Part>? = null
    ): kotlinx.serialization.json.JsonObject
}
