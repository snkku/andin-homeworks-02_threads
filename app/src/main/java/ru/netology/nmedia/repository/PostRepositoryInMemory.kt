package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemory: PostRepository {

    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likedByMe = false,
        likes = 999,
        shared = 129998,
        viewed = 29929391
    )

    private val data = MutableLiveData(post)


    override fun get() = data

    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe,  likes = if (!post.likedByMe) post.likes + 1 else post.likes - 1)
        data.value = post
    }

    override fun share() {
        post = post.copy(shared = post.shared + 1)
        data.value = post
    }

}