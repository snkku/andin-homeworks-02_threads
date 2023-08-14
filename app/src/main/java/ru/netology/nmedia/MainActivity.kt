package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.onInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PostViewModel by viewModels()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val interaction = object : onInteractionListener {
            override fun onShare(post: Post) {
                viewModel.share(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onEdit(post: Post) {
                binding.editTextPreview.text = post.content
                binding.vgEdit.visibility = View.VISIBLE
                with(binding.content) {
                    setText(post.content)
                    AndroidUtils.showKeyboard(binding.root)
                    requestFocus()
                }
                viewModel.edit(post)
            }

        }
        setContentView(binding.root)
        val adapter = PostAdapter(interaction)
        binding.recycler.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val curSize = adapter.currentList.size
            adapter.submitList(posts) {
                if (curSize < posts.size)
                    binding.recycler.smoothScrollToPosition(0)
            }
        }
        viewModel.edited.observe(this) { post ->

        }

        binding.content.doOnTextChanged { text, start, before, count ->
            with(binding.save)
            {
                if (text.toString()
                        .trim().length > 0 && viewModel.edited.value?.content != text.toString()
                ) {
                    setEnabled(true)
                    setClickable(true)
                    imageAlpha = 255
                } else {
                    setEnabled(false)
                    setClickable(false)
                    imageAlpha = 50
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.content.setText("")
            viewModel.clearEdited()
            AndroidUtils.hideKeyboard(binding.root)
            binding.vgEdit.visibility = View.GONE

        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            viewModel.save(binding.content.text.toString())
            binding.cancelButton.performClick()
        }
    }
}