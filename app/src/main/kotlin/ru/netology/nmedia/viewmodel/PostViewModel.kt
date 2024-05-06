package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import retrofit2.HttpException
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.Feed
import ru.netology.nmedia.model.FeedState
import ru.netology.nmedia.repository.AuthorRepository
import ru.netology.nmedia.repository.AuthorRepositoryNet
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNet
import ru.netology.nmedia.utils.SingleLiveEvent
import java.util.regex.Pattern.compile
import kotlin.Exception

private val empty = Post(
    id = 0,
    userId = 1,
    author = Author(1,"Me","snake.png"),
    content = "",
    published = "",
    likes = 0,
    shared = 0,
    viewed = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNet(AppDb.getInstance(application).postDao(), AppDb.getInstance(application).authorDao())

    private val youtubeRegexp =
        compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|live\\/|v\\/)?)([\\w\\-]+)(\\S+)?\$")
    private val _saveState = SingleLiveEvent<Unit>()

    val data: LiveData<Feed> = repository.data.map(::Feed)

    private val _state = MutableLiveData<FeedState>()
    val state: LiveData<FeedState>
        get() = _state


    val saveState: LiveData<Unit>
        get() = _saveState

    init {
        load()
    }
    fun load() {

        viewModelScope.launch {
            _state.value = FeedState(loading = true)
            try {
                repository.getAll()
                _state.value = FeedState()
            } catch (e: Exception)
            {
                _state.value = FeedState(loading = false, error = true, errorIsFatal = true, errorMessage = e.message)
                e.printStackTrace()
            }
        }
    }

    val edited = MutableLiveData(empty)
    var filteredId: Long = 0
    var draftContent: String? = null

    fun like(id: Long, likedByMe: Boolean) {
        viewModelScope.launch {
            try {
                repository.like(id, !likedByMe)
            } catch (e: Exception)
            {
                _state.value = FeedState(error = true, errorMessage = e.message)
            }
        }
    }

    fun share(id: Long) {
        viewModelScope.launch {
            try {
                repository.share(id)
                data.value?.posts.orEmpty().map {
                    if (it.id == id) it.copy(shared = it.shared + 1) else it
                }
            } catch (e: Exception)
            {
                _state.value = FeedState(error = true, errorMessage = e.message)
            }
        }
    }

    fun remove(id: Long) {
        viewModelScope.launch {
            try {
                repository.remove(id)
                data.value?.posts.orEmpty().filter { it.id !== id }
            } catch (e: Exception)
            {
                _state.value = FeedState(error = true, errorMessage = e.message)
            }

        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun incView(id: Long) {
        viewModelScope.launch {
            try {
                repository.view(id)
            } catch (e: Exception)
            {}
        }
    }

    fun clearEdited() {
        edited.value = empty
    }

    fun save(text: String) {
        // parse for youtube url and remove from main text, store in videoURL
        viewModelScope.launch {
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
                    try {
                        repository.save(it.copy(content = outputText, videoURL = videoURL))
                        _saveState.value = Unit
                    } catch (e: Exception)
                    {
                        Log.d("EXCP UPDATE SAVE", "save: ${e.message}")
                        _state.value = FeedState(error = true, errorMessage = e.message)
                    }
                } else {
                    outputText = text
                    try {
                        repository.save(it.copy(content = outputText))
                        _saveState.value = Unit
                    } catch (e: Exception)
                    {
                        Log.d("EXCP NEW POST SAVE", "save: ${e.message}")
                        _state.value = FeedState(error = true, errorMessage = e.message)
                    }
                }
                edited.value = empty
            }
        }
    }
}