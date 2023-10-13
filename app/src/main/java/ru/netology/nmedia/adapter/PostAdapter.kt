package ru.netology.nmedia.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostCardBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.Shorter

@GlideModule
class GlideApp : AppGlideModule()
interface onInteractionListener {
    fun onLike(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
}

class PostAdapter(private val onInteractionListener: onInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}


class PostViewHolder(
    private val binding: PostCardBinding,
    private val onInteractionListener: onInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        val shorter = Shorter()
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            viewedCountText.text = shorter.short(post.viewed.toDouble())

            imLikes.text = shorter.short(post.likes.toDouble())
            imShare.text = shorter.short(post.shared.toDouble())
            imLikes.isChecked = post.likedByMe
            imLikes.text = shorter.short(post.likes.toDouble())
            imShare.text = shorter.short(post.shared.toDouble())
            //https://www.youtube.com/watch?v=DCRojLC8xWM
            if (!post.videoURL.isNullOrBlank())
            {
                val id = Regex("\\.*\\?v=([\\w\\-]+)(\\S+)?\$").findAll(post.videoURL).map { it.groupValues[1] }.joinToString()
                try {
                    val image = Glide.with(binding.root)
                        .asBitmap()
                        .load(String.format("https://img.youtube.com/vi/%s/0.jpg", id))
                        .into(videoPreviewImage)
                    Log.d("IMG", image.toString())
/*
                    image.getSize { width, height ->
                        with(videoPreviewImage){
                            layoutParams.width = width
                            layoutParams.height = height
                        }
                    }
  */
                } catch (e: Exception) {
                    Log.d("IMAGE", "Image get error: " + e.printStackTrace())
                }
                Log.d("POST ADAPTER, videoId: ", id)
//                videoPreviewContent.visibility = View.VISIBLE
            }
//https://img.youtube.com/vi/<id>>/0.jpg
            imLikes.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            imShare.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }

                    }
                }.show()
            }
        }

    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
