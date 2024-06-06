package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.internal.http2.ErrorCode
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.repository.PostRepositoryNet
import java.util.concurrent.TimeUnit

private const val BASE_URL = BuildConfig.BASE_URI

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .let {
        if (BuildConfig.DEBUG)
            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        it
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PostApiService {
    @GET("posts")
    suspend fun getAll(): List<Post>

    @GET("posts/{id}")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("post")
    suspend fun save(@Body post: Post): PostRepositoryNet.BooleanResponse

    @DELETE("post/{id}")
    suspend fun remove(@Path("id") id:Long): PostRepositoryNet.BooleanResponse

    @GET("like/{id}")
    suspend fun like (@Path("id") id: Long): PostRepositoryNet.BooleanResponse

    @DELETE("like/{id}")
    suspend fun unlike (@Path("id") id: Long): PostRepositoryNet.BooleanResponse

    @GET("share/{id}")
    suspend fun share (@Path("id") id: Long): PostRepositoryNet.BooleanResponse

    @GET("author/{id}")
    suspend fun getAuthorById(@Path("id") id: Long): Author

    @GET("ping")
    suspend fun ping(): PostRepositoryNet.BooleanResponse

}

object PostApi {
    val service: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}