package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.AuthorDao
import ru.netology.nmedia.dto.Author

class AuthorRepositoryNet (
    private val authorDao: AuthorDao,
) : AuthorRepository {
    override val authors: LiveData<List<Author>>
        get() = TODO("Not yet implemented")

    override suspend fun getById(id: Long): Author {
        try {
            return PostApi.service.getAuthorById(id)
        } catch (e: Exception)
        {
            throw Exception(e.message)
        }
    }
}
