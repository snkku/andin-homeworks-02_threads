package ru.netology.nmedia.repository


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.AuthorDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AuthorEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity



class PostRepositoryNet(
    private val postDao: PostDao,
    private val authorDao: AuthorDao
) : PostRepository {

    data class BooleanResponse(
        val status: Int,
        val newId: Long?,
        val error: String?
    )

    override val data = postDao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)

    override fun getNewer(): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val response = PostApi.service.getNewer(postDao.getLastUnreaded())
            if (!response.isSuccessful)
                throw Exception("Error ${response.code()}: ${response.message()}")

            val body = response.body()
                ?: throw Exception("Null body, code: ${response.code()}, message: ${response.message()}")
            postDao.insert(body.toEntity().map { it.copy(isHidden = true) })
            emit(postDao.countNew())
        }
    }
        .catch { e -> throw e }
        .flowOn(Dispatchers.Default)

    override suspend fun showNew() {
        try {
            data.first { posts ->
                posts.filter { it.isHidden == true }.forEach { post ->
                    Log.d("ShowNew", "Post modified: $post")
                    postDao.updatePost(PostEntity.fromDto(post.copy(isHidden = false)))
                }
                return@first true
            }
        } catch (e: Exception) {
            Log.d("showNew Rep", "showNew: ${e.printStackTrace()}")
        }
    }

    override suspend fun getAll() {
        try {
            PostApi.service.ping()
            // saving offline unsynced posts
            val unSyncedPosts = postDao.getUnSynced()
            unSyncedPosts.forEach { post ->
                // remove if marked as removed
                if (post.removed)
                    this.remove(post.id)
                else {
                    // make update if something changed
                    if (post.isNew)
                        this.save(post.copy(id = 0).toDto())
                    else
                        this.save(post.toDto())
                }
            }
            val posts: List<Post> = PostApi.service.getAll()
            authorDao.insert(posts.map {
                AuthorEntity.fromDto(it.author)
            })
            postDao.insert(posts.map {
                PostEntity.fromDto(it)
            })
        } catch (e: Exception)
        {
            Log.d("GETALL", "getAll: ${e.message}")
            throw Exception("Failed to sync with network: ${e.message}")
        }
    }

    override suspend fun like(id: Long, remove: Boolean) {
        Log.d("POST REP NET", "like: ${remove}")
        val post = postDao.getPostById(id).toDto()
        post.map {
            postDao.updatePost(
                PostEntity.fromDto(
                    it.copy(
                        likedByMe = remove,
                        likes = (if (remove) it.likes + 1 else it.likes - 1)
                    )
                )
            )
            postDao.markToSync(id, false)
        }
        try {
            val response: BooleanResponse = if (!remove)
                PostApi.service.unlike(id)
            else
                PostApi.service.like(id)
            if (response.status < 0)
                throw Exception("Failed to update like status")
            else
                postDao.markToSync(id, true)
        } catch (e: Exception) {
            throw Exception("Network failure: ${e.message}")
        }
    }

    override suspend fun share(id: Long) {
        try {
            PostApi.service.ping()
        } catch (e: Exception) {
            throw Exception("Cant share without network connection")
        }

        try {
            val response: BooleanResponse = PostApi.service.share(id)
            if (response.status < 0)
                throw Exception("Share error")
            else {
                // update only if shared via network
                val post = postDao.getPostById(id).toDto()
                post.map {
                    postDao.updatePost(PostEntity.fromDto(it.copy(shared = it.shared + 1)))
                }
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    override suspend fun remove(id: Long) {
        postDao.markToRemoveById(id, true)
        postDao.markToSync(id, false)
        val response: BooleanResponse = PostApi.service.remove(id)
        if (response.status < 0)
            throw Exception("Failed to remove post")
        else
            postDao.removeById(id)
    }

    override suspend fun save(post: Post) {
        var newLocalPostId: Long = 0L
        if (post.id == 0L) {
            newLocalPostId = postDao.insert(PostEntity.fromDto(post))
            Log.d("Offline save first", "save: ${newLocalPostId}")
            postDao.markToSync(newLocalPostId, false)
        } else {
            postDao.updatePost(PostEntity.fromDto(post))
            postDao.markToSync(post.id, false)
        }

        try {
            val response: BooleanResponse = PostApi.service.save(post)
            if (response.status < 0)
                throw Exception("Failed to save post")
            else {
                // update LOCAL post ID to NETWORK ID (IF NEW POST)
                if (response.newId != null)
                {
                    postDao.insert(
                        PostEntity.fromDto(
                            post.copy(id = response.newId)
                        )
                    )
                    postDao.removeById(post.id)
                    postDao.markToSync(response.newId, true)
                } else {
                    postDao.markToSync(post.id, true)
                }
            }
        }
        catch (e: HttpException)
        {
            Log.d("SAVE ERROR HTTP", e.toString())
            throw Exception("Error saving post, server side error: ${e.message}")
        }
        catch (e: Exception)
        {
            Log.d("SAVE GENERIC EXCP", e.toString())
        }
    }

    override suspend fun view(id: Long) {
    }
}