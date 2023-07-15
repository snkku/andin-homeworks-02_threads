package ru.netology.nmedia

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.utils.Shorter
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val shorter = Shorter()

        val viewModel: PostViewModel by viewModels()

        viewModel.data.observe(this) { post ->
            with(binding){
                author.text = post.author
                published.text = post.published
                content.text = post.content
                viewedCountText.text = shorter.short(post.viewed.toDouble())

                likesCountText.text = shorter.short(post.likes.toDouble())
                shareCountText.text = shorter.short(post.shared.toDouble())

                imLikes.setImageResource(if (post.likedByMe) R.drawable.image_liked else R.drawable.image_like)
                likesCountText.text = shorter.short(post.likes.toDouble())
                shareCountText.text = shorter.short(post.shared.toDouble())
            }
        }
        binding.imShare.setOnClickListener {
            viewModel.share()
            Log.d(TAG, "Share button pressed")
        }
        binding.imLikes.setOnClickListener {
            viewModel.like()
        }

    }
}