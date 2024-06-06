package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Author

@Entity
data class AuthorEntity(
    @PrimaryKey(autoGenerate = true)
    val authorId: Long,
    val name: String,
    val avatar: String? = null,
) {
    fun toDto() = Author(authorId, name, avatar)

    companion object {
        fun fromDto(dto: Author) =
            AuthorEntity(dto.id, dto.name, dto.avatar)
    }
}

fun List<AuthorEntity>.toDto(): List<Author> = map(AuthorEntity::toDto)
fun List<Author>.toEntity(): List<AuthorEntity> = map(AuthorEntity::fromDto)