package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun like(id: Long, remove: Boolean)
    suspend fun share(id: Long)
    suspend fun remove(id: Long)
    suspend fun save(post: Post)
    suspend fun view(id: Long)

}