package ru.netology.nmedia.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(
    private val context: Context
) : PostRepository {
    private val gson = Gson()
    private val filename = "posts.json"
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private var nextId = 0L
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
        }
    private val data = MutableLiveData(posts)


    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists())
        {
            context.openFileInput(filename).bufferedReader().use {
                posts  = gson.fromJson(it, type)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                data.value = posts
            }
        } else {
            sync()
        }
    }

    private fun sync()
    {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
    override fun getAll(): LiveData<List<Post>> = data

    override fun like(id: Long) {
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
        posts = posts.map {
            if (it.id == id)
                it.copy(shared = it.shared + 1)
            else it
        }
        data.value = posts
    }

    override fun remove(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = nextId++)) + posts
        } else {
            posts.map {
                if (it.id == post.id) post.copy() else it
            }
        }
        data.value = posts
        Log.d("POSTS", posts.toString())
    }

    override fun view(id: Long) {
        TODO("Not yet implemented")
    }
}