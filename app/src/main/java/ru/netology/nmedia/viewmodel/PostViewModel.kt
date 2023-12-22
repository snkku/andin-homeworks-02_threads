package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl
import java.util.regex.Pattern.compile

private val empty = Post(
    id = 0,
    author = "Me",
    content = "",
    published = "",
    likes = 0,
    shared = 0,
    viewed = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao()
    )
    private val youtubeRegexp = compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|live\\/|v\\/)?)([\\w\\-]+)(\\S+)?\$")
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    var filteredId: Long = 0
    var draftContent: String? = null
    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
    fun remove(id: Long) = repository.remove(id)

    fun edit(post: Post) {
        edited.value = post
    }

    fun incView(id: Long)
    {
        repository.view(id)
    }
    fun clearEdited() {
        edited.value = empty
    }
    fun save(text: String) {
        // parse for youtube url and remove from main text, store in videoURL
        edited.value?.let {
            var videoURL: String? = null
            var outputText: String
            text.split(" ", "\n", "\r").map { text ->
                if (youtubeRegexp.matcher(text).matches()) {
                    Log.d("REGEXP:", "Youtube match " + youtubeRegexp.toRegex().find(text)?.value)
                    videoURL = youtubeRegexp.toRegex().find(text)?.value.toString()
                }
                return@map
            }
            if (!videoURL.isNullOrBlank()) {
                outputText = text.replace(videoURL!!, "")
                repository.save(it.copy(content = outputText, videoURL = videoURL))
            } else {
                outputText = text
                repository.save(it.copy(content = outputText))
            }
            edited.value = empty
        }
    }
}