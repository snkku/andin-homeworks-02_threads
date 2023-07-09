package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 1204827,
    var shared: Int = 11998,
    var viewed: Int = 20302219,
    var likedByMe: Boolean = false
)