package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.api.PostApiService

class PostRepositoryNet : PostRepository {

    data class BooleanResponse(
        val status: Int,
        val error: String?
    )

    override fun getAllAsync(callback: PostRepository.GetCallback<List<Post>>): Unit {
        PostApiService.service.getAll()
            .enqueue(
                object : Callback<List<Post>> {
                    override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                        callback.onError(Exception(t))
                    }

                    override fun onResponse(
                        call: Call<List<Post>>,
                        response: Response<List<Post>>
                    ) {
                        if (!response.isSuccessful) {
                            callback.onError(Exception(response.errorBody()?.string()))
                            return
                        }

                        val body = response.body() ?: throw RuntimeException()
                        callback.onSuccess(body)
                    }

                }
            )
    }

    override fun like(
        id: Long,
        remove: Boolean,
        callback: PostRepository.GetCallback<BooleanResponse>
    ) {

        var obj: Call<BooleanResponse>

        if (remove)
            obj = PostApiService.service.like(id)
        else
            obj = PostApiService.service.unlike(id)

        obj.enqueue(
            object : Callback<BooleanResponse> {
                override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: Call<BooleanResponse>,
                    response: Response<BooleanResponse>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException()
                    callback.onSuccess(body)
                }
            }
        )
    }

    override fun share(id: Long, callback: PostRepository.GetCallback<BooleanResponse>) {
        PostApiService.service.share(id)
            .enqueue(object : Callback<BooleanResponse> {
                override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: Call<BooleanResponse>,
                    response: Response<BooleanResponse>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException()
                    callback.onSuccess(body)
                }
            })
    }

    override fun remove(id: Long, callback: PostRepository.GetCallback<BooleanResponse>) {
        PostApiService.service.remove(id)
            .enqueue(object : Callback<BooleanResponse> {
                override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: Call<BooleanResponse>,
                    response: Response<BooleanResponse>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException()
                    callback.onSuccess(body)
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.GetCallback<BooleanResponse>) {
        PostApiService.service.save(post)
            .enqueue(object : Callback<BooleanResponse> {
                override fun onFailure(call: Call<BooleanResponse>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: Call<BooleanResponse>,
                    response: Response<BooleanResponse>
                ) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message()))
                        return
                    }
                    val body = response.body() ?: throw RuntimeException()
                    callback.onSuccess(body)
                }
            })
    }

    override fun view(id: Long) {
    }
}