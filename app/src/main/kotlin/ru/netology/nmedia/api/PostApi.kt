package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
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
interface PostApi {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("post")
    fun save(@Body post: Post): Call<PostRepositoryNet.BooleanResponse>

    @DELETE("post/{id}")
    fun remove(@Path("id") id:Long): Call<PostRepositoryNet.BooleanResponse>

    @GET("like/{id}")
    fun like (@Path("id") id: Long): Call<PostRepositoryNet.BooleanResponse>

    @DELETE("like/{id}")
    fun unlike (@Path("id") id: Long): Call<PostRepositoryNet.BooleanResponse>

    @GET("share/{id}")
    fun share (@Path("id") id: Long): Call<PostRepositoryNet.BooleanResponse>

}

object PostApiService {
    val service: PostApi by lazy {
        retrofit.create()
    }
}