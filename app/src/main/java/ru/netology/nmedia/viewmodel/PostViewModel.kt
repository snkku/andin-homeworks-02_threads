package ru.netology.nmedia.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory
import java.util.regex.Pattern.compile

private val empty = Post(
    id = 0,
    author = "Me",
    content = "",
    published = "now",
    likes = 0,
    shared = 0,
    viewed = 0,
    likedByMe = false
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemory()
    private val youtubeRegexp = compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|live\\/|v\\/)?)([\\w\\-]+)(\\S+)?\$")
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
    fun remove(id: Long) = repository.remove(id)

    fun edit(post: Post) {
        edited.value = post
    }
    fun clearEdited() {
        edited.value = empty
    }
    fun save(text: String) {
        // parse for youtube url and remove from main text, store in videoURL
        edited.value?.let {
            var videoURL: String? = null
            var outputText = text
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