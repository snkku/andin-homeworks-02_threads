package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun like(id: Long)
    fun unlike(id: Long)
    fun share(id: Long)
    fun remove(id: Long)
    fun save(post: Post)
    fun view(id: Long)
}