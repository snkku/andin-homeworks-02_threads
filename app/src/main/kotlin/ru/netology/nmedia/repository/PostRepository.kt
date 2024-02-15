package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun like(id: Long): Boolean
    fun unlike(id: Long): Boolean
    fun share(id: Long): Boolean
    fun remove(id: Long): Boolean
    fun save(post: Post): Boolean
    fun view(id: Long)
}