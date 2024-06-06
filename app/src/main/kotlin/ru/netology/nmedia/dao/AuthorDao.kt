package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.AuthorEntity
import ru.netology.nmedia.entity.PostEntity

@Dao
interface AuthorDao {
    @Query("SELECT * FROM AuthorEntity")
    fun getAll(): LiveData<List<AuthorEntity>>

    @Query("SELECT * FROM AuthorEntity WHERE authorId = :id")
    suspend fun getById(id: Long): AuthorEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(author: AuthorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authors: List<AuthorEntity>)
}