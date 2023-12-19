package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data
    override fun like(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id == id)
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1
                )
            else it
        }
        data.value = posts
    }

    override fun share(id: Long) {
        dao.share(id)
        posts = posts.map {
            if (it.id == id)
                it.copy(shared = it.shared + 1)
            else it
        }
        data.value = posts
    }

    override fun remove(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)

        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id == post.id) it else saved
            }
        }
        data.value = posts
    }

    override fun view(id: Long) {
        dao.view(id)
        posts = posts.map {
            if (it.id == id)
                it.copy(viewed = it.viewed + 1)
            else it
        }
        data.value = posts
    }
}