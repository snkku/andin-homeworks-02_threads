package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Author

interface AuthorRepository {
    val authors: LiveData<List<Author>>
    suspend fun getById(id: Long): Author

}