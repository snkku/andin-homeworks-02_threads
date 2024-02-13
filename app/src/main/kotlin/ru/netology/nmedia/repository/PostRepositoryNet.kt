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

    override fun like(id: Long) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/like/${id}")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun unlike(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/like/${id}")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun share(id: Long) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/share/${id}")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun remove(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/${id}")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun view(id: Long) {
    }
}