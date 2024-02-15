package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.util.copy
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNet
import ru.netology.nmedia.utils.SingleLiveEvent
import java.lang.Exception
import java.util.regex.Pattern.compile
import kotlin.concurrent.thread

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
    private val repository: PostRepository = PostRepositoryNet()
    private val youtubeRegexp = compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|live\\/|v\\/)?)([\\w\\-]+)(\\S+)?\$")
    private val _state = MutableLiveData(FeedState())
    private val _saveState = SingleLiveEvent<Unit>()
    val data : LiveData<FeedState>
        get() = _state

    val saveState: LiveData<Unit>
        get() = _saveState
    init {
        load()
    }
    fun load() {
        thread {
            _state.postValue(FeedState(loading = true))
            try {
                val posts = repository.getAll()
                _state.postValue(FeedState(posts = posts, empty = posts.isEmpty()))
            } catch (e: Exception) {
                Log.d("LOAD", "load: ${e.message}")
                _state.postValue(FeedState(error = true))
            }
        }

    }
    val edited = MutableLiveData(empty)
    var filteredId: Long = 0
    var draftContent: String? = null

    fun like(id: Long, likedByMe: Boolean) {
        thread {
            var responseOk: Boolean
            if (likedByMe)
                responseOk = repository.unlike(id)
            else
                responseOk = repository.like(id)
            Log.d("LIKE", "like response: ${responseOk.toString()}")
            if (responseOk)
                _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().map {
                    if (it.id == id) it.copy(likedByMe = !likedByMe, likes = (if (!likedByMe) it.likes+1 else it.likes-1)) else it
                }))
        }
    }
    fun share(id: Long) {
        thread {
            val responseOk = repository.share(id)
            if (responseOk)
                _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().map {
                    if (it.id == id) it.copy(shared = it.shared+1) else it
                }))
        }
    }
    fun remove(id: Long) {
        thread {
            val responseOk = repository.remove(id)
            if (responseOk)
                _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().filter {
                    it.id !== id
                }))
        }
    }

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
                thread {
                    repository.save(it.copy(content = outputText, videoURL = videoURL))
                    _saveState.postValue(Unit)
                }
            } else {
                outputText = text
                thread {
                    repository.save(it.copy(content = outputText))
                    _saveState.postValue(Unit)
                }
            }
            edited.value = empty
        }
    }
}