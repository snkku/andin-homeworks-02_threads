package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNet
import ru.netology.nmedia.utils.SingleLiveEvent
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
    private val youtubeRegexp =
        compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|live\\/|v\\/)?)([\\w\\-]+)(\\S+)?\$")
    private val _state = MutableLiveData(FeedState())
    private val _saveState = SingleLiveEvent<Unit>()
    val data: LiveData<FeedState>
        get() = _state

    val saveState: LiveData<Unit>
        get() = _saveState

    init {
        load()
    }

    fun load() {
        _state.postValue(FeedState(loading = true))
        repository.getAllAsync(object : PostRepository.GetCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _state.postValue(FeedState(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(throwable: Throwable) {
                _state.postValue(FeedState(error = true))
            }
        })
    }

    val edited = MutableLiveData(empty)
    var filteredId: Long = 0
    var draftContent: String? = null

    fun like(id: Long, likedByMe: Boolean) {
        repository.like(
            id,
            likedByMe,
            object : PostRepository.GetCallback<PostRepositoryNet.BooleanResponse> {
                override fun onError(throwable: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onSuccess(response: PostRepositoryNet.BooleanResponse) {
                    if (response.status >= 0)
                        _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().map {
                            if (it.id == id) it.copy(
                                likedByMe = !likedByMe,
                                likes = (if (!likedByMe) it.likes + 1 else it.likes - 1)
                            ) else it
                        }))
                }
            })
    }

    fun share(id: Long) {
        repository.share(
            id,
            object : PostRepository.GetCallback<PostRepositoryNet.BooleanResponse> {
                override fun onError(throwable: Throwable) {
                }

                override fun onSuccess(response: PostRepositoryNet.BooleanResponse) {
                    if (response.status >= 0)
                        _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().map {
                            if (it.id == id) it.copy(shared = it.shared + 1) else it
                        }))
                }
            })
    }

    fun remove(id: Long) {
        repository.remove(
            id,
            object : PostRepository.GetCallback<PostRepositoryNet.BooleanResponse> {
                override fun onError(throwable: Throwable) {

                }

                override fun onSuccess(response: PostRepositoryNet.BooleanResponse) {
                    if (response.status >= 0)
                        _state.postValue(FeedState(posts = _state.value?.posts.orEmpty().filter {
                            it.id !== id
                        }))

                }
            })
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun incView(id: Long) {
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
                repository.save(
                    it.copy(content = outputText, videoURL = videoURL),
                    object : PostRepository.GetCallback<PostRepositoryNet.BooleanResponse> {
                        override fun onError(throwable: Throwable) {
                        }

                        override fun onSuccess(response: PostRepositoryNet.BooleanResponse) {
                            if (response.status >= 0)
                                _saveState.postValue(Unit)
                        }
                    })
            } else {
                outputText = text
                repository.save(
                    it.copy(content = outputText),
                    object : PostRepository.GetCallback<PostRepositoryNet.BooleanResponse> {
                        override fun onError(throwable: Throwable) {
                        }

                        override fun onSuccess(response: PostRepositoryNet.BooleanResponse) {
                            if (response.status >= 0)
                                _saveState.postValue(Unit)
                        }
                    })
            }
            edited.value = empty
        }
    }
}