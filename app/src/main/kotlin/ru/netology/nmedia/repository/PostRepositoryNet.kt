package ru.netology.nmedia.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.HttpException
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.AuthorDao
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.entity.AuthorEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto

class PostRepositoryNet(
    private val postDao: PostDao,
    private val authorDao: AuthorDao
) : PostRepository {

    data class BooleanResponse(
        val status: Int,
        val newId: Long?,
        val error: String?
    )

    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.map { it ->
            it.toDto()
        }
    }
    override suspend fun getAll() {
        try {
            val posts: List<Post> = PostApi.service.getAll()
            authorDao.insert(posts.map {
                Log.d("AuthorDAO", "getAll: ${it.author}")
                AuthorEntity.fromDto(it.author!!)
            })
            postDao.insert(posts.map {
                Log.d("PostDAO", "getAll: ${it}")
                PostEntity.fromDto(it)
            })
        } catch (e: Exception)
        {
            throw Exception("Shit happened ${e.message}")
            Log.d("GETALL", "getAll: ${e.message}")
        }
    }

    override suspend fun like(id: Long, remove: Boolean) {
        Log.d("POST REP NET", "like: ${remove}")
        val response: BooleanResponse = if (!remove)
            PostApi.service.unlike(id)
        else
            PostApi.service.like(id)
        if (response.status < 0)
            throw Exception("Failed to update like status")
        else {
            val post = postDao.getPostById(id).toDto()
            post.map {
                postDao.updatePost(PostEntity.fromDto(it.copy(likedByMe = remove, likes = (if(remove) it.likes + 1 else it.likes - 1))))
            }
        }
    }

    override suspend fun share(id: Long) {
        val response: BooleanResponse = PostApi.service.share(id)
        if (response.status < 0)
            throw Exception("Failed to update share status")
    }

    override suspend fun remove(id: Long) {
        val response: BooleanResponse = PostApi.service.remove(id)
        if (response.status < 0)
            throw Exception("Failed to remove post")
        else
            postDao.removeById(id)
    }

    override suspend fun save(post: Post) {
        try {
            val response: BooleanResponse = PostApi.service.save(post)
            if (response.status < 0)
                throw Exception("Failed to save post")
            else {
                if (post.id == 0L)
                {
                    val newId = response.newId
                    postDao.insert(PostEntity.fromDto(post.copy(id = newId!!)))
                } else {
                    postDao.updatePost(
                        PostEntity.fromDto(
                            post.copy(
                                content = post.content,
                                videoURL = post.videoURL
                            )
                        )
                    )
                }
            }
        }
        catch (e: HttpException)
        {
            Log.d("SAVE ERROR HTTP", e.toString())
        }
        catch (e: Exception)
        {
            Log.d("SAVE GENERIC EXCP", e.toString())
        }
    }

    override suspend fun view(id: Long) {
    }
}