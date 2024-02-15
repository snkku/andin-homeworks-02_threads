package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class PostRepositoryNet : PostRepository {

    data class BooleanResponse (
        val status: Int,
        val error: String?
    )
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    companion object {
        private const val BASE_URL = "http://nm.kamensk.ru"
        private val jsonType = "application/json".toMediaType()
        private val type: Type? = object : TypeToken<List<Post>> () {}.type
    }
    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                Log.d("TAG", "getAll: ${it.toString()}")
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                gson.fromJson(it, type)
            }
    }

    override fun like(id: Long): Boolean {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/like/${id}")
            .build()

        val response = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, BooleanResponse::class.java)}
        return response.status >= 0
    }

    override fun unlike(id: Long): Boolean {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/like/${id}")
            .build()

        val response = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, BooleanResponse::class.java)}
        return response.status >= 0
    }

    override fun share(id: Long): Boolean {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/share/${id}")
            .build()

        val response = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, BooleanResponse::class.java)}
        return response.status >= 0
    }

    override fun remove(id: Long): Boolean {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/${id}")
            .build()

        val response = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, BooleanResponse::class.java)}
        return response.status >= 0
    }

    override fun save(post: Post): Boolean {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        val response = client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, BooleanResponse::class.java)}
        return response.status >= 0
    }

    override fun view(id: Long) {
    }
}