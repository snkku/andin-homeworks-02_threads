package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostCardBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.Shorter

typealias onClickListener = (post: Post) -> Unit

class PostAdapter (private val onLikeListener: onClickListener, private val onShareListener: onClickListener): ListAdapter<Post, PostViewHolder>(PostDiffCallback)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: PostCardBinding,
    private val onLikeListener: onClickListener,
    private val onShareListener: onClickListener
): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post)
    {
        val shorter = Shorter()
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            viewedCountText.text = shorter.short(post.viewed.toDouble())

            likesCountText.text = shorter.short(post.likes.toDouble())
            shareCountText.text = shorter.short(post.shared.toDouble())

            imLikes.setImageResource(if (post.likedByMe) R.drawable.image_liked else R.drawable.image_like)
            likesCountText.text = shorter.short(post.likes.toDouble())
            shareCountText.text = shorter.short(post.shared.toDouble())

            imLikes.setOnClickListener{
                onLikeListener(post)
            }
            imShare.setOnClickListener {
                onShareListener(post)
            }
        }

    }
}

object PostDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
