package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int,
    val shared: Int,
    val viewed: Int,
    val likedByMe: Boolean,
    val videoURL: String? = null
)