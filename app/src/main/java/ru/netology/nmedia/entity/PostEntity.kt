package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

data class NewPostEntity(
    val author: String,
    val content: String,
    val videoURL: String?
)
@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val published: String,
    @ColumnInfo(defaultValue = "0")
    val likes: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val shared: Int = 0,
    @ColumnInfo(defaultValue = "1")
    val viewed: Int = 1,
    @ColumnInfo(defaultValue = "0")
    val likedByMe: Boolean,
    val videoURL: String?
) {
    fun toDto() = Post(id, author, content, published, likes, shared, viewed, likedByMe, videoURL)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.shared, dto.viewed, dto.likedByMe, dto.videoURL)

    }
}