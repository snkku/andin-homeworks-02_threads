package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import ru.netology.nmedia.dto.Post
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class PostRepositoryNet : PostRepository {

    data class BooleanResponse(
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
        private val type: Type? = object : TypeToken<List<Post>>() {}.type
    }

    override fun getAllAsync(callback: PostRepository.GetCallback<List<Post>>): Unit {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseText = response.body?.string()
                        if (responseText == null) {
                            callback.onError(RuntimeException("body null"))
                            return
                        }

                        try {
                            callback.onSuccess(gson.fromJson(responseText, type))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                }
            )
    }

    override fun like(
        id: Long,
        remove: Boolean,
        callback: PostRepository.GetCallback<BooleanResponse>
    ) {
        val rb: Request.Builder = Request.Builder().url("${BASE_URL}/api/like/${id}")
        if (remove)
            rb.delete()
        val request = rb.build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(
                            gson.fromJson(
                                response.body?.string(),
                                BooleanResponse::class.java
                            )
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun share(id: Long, callback: PostRepository.GetCallback<BooleanResponse>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/share/${id}")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(
                            gson.fromJson(
                                response.body?.string(),
                                BooleanResponse::class.java
                            )
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun remove(id: Long, callback: PostRepository.GetCallback<BooleanResponse>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/${id}")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(
                            gson.fromJson(
                                response.body?.string(),
                                BooleanResponse::class.java
                            )
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }

                }
            })
    }

    override fun save(post: Post, callback: PostRepository.GetCallback<BooleanResponse>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(
                        gson.fromJson(
                            response.body?.string(),
                            BooleanResponse::class.java
                        )
                    )
                }
            })
    }

    override fun view(id: Long) {
    }
}