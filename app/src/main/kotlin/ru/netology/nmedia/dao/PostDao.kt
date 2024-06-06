package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE removed = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM PostEntity WHERE isHidden = 1")
    fun countNew(): Int

    @Query("SELECT max(id) FROM PostEntity WHERE isHidden = 0")
    fun getLastUnreaded(): Long

    @Query("SELECT * FROM PostEntity WHERE synced = 0")
    suspend fun getUnSynced(): List<PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPostById(id: Long): List<PostEntity>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET removed = :removed WHERE id = :id")
    suspend fun markToRemoveById(id: Long, removed: Boolean)

    @Query("UPDATE PostEntity SET synced = :synced WHERE id = :id")
    suspend fun markToSync(id: Long, synced: Boolean)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}

