package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: GetCallback<List<Post>>)
    fun like(id: Long, remove: Boolean, callback: GetCallback<PostRepositoryNet.BooleanResponse>)
    fun share(id: Long, callback: GetCallback<PostRepositoryNet.BooleanResponse>)
    fun remove(id: Long, callback: GetCallback<PostRepositoryNet.BooleanResponse>)
    fun save(post: Post, callback: GetCallback<PostRepositoryNet.BooleanResponse>)
    fun view(id: Long)

    interface GetCallback<T> {
        fun onSuccess(response: T)
        fun onError(throwable: Throwable)
    }
}