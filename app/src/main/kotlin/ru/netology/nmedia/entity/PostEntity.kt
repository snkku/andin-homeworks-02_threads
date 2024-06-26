package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.netology.nmedia.dao.AuthorDao
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.AuthorRepository

@Entity(
    foreignKeys = [ForeignKey(
        entity = AuthorEntity::class,
        parentColumns = ["authorId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["isHidden"])]
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val userId: Long,
    @Embedded
    val author: AuthorEntity,
    val content: String,
    val published: String,
    var likes: Int,
    val shared: Int,
    val viewed: Int,
    val likedByMe: Boolean,
    val videoURL: String? = null,
    val synced: Boolean = true,
    val removed: Boolean = false,
    val isNew: Boolean = false,

    val isHidden: Boolean = false
) {
    fun toDto() = Post(
        id,
        userId,
        author.toDto(),
        content,
        published,
        likes,
        shared,
        viewed,
        likedByMe,
        videoURL,
        isHidden
    )
    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.userId,
                AuthorEntity.fromDto(dto.author),
                dto.content,
                dto.published,
                dto.likes,
                dto.shared,
                dto.viewed,
                dto.likedByMe,
                dto.videoURL,
                synced = true,
                removed = false,
                isNew = dto.id == 0L,
                isHidden = dto.isHidden ?: false
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)