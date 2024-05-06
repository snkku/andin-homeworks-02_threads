package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class Feed(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)

data class FeedState
(
    val loading: Boolean = false,
    val error: Boolean = false,
    val errorMessage: String? = null,
    val errorIsFatal: Boolean = false,
)