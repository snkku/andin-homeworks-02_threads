package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    suspend fun showNew()
    fun getNewer(): Flow<Int>
    suspend fun like(id: Long, remove: Boolean)
    suspend fun share(id: Long)
    suspend fun remove(id: Long)
    suspend fun save(post: Post)
    suspend fun view(id: Long)

}