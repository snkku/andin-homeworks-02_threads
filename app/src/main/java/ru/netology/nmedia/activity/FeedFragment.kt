package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.onInteractionListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post


class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel: PostViewModel by activityViewModels()
        setFragmentResultListener("newText") { _, bundle ->
            val text = bundle.getString("text")
            if (text.isNullOrBlank())
                viewModel.clearEdited()
            else
                viewModel.save(text)
        }

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val interaction = object : onInteractionListener {
            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                Intent.createChooser(intent, getString(R.string.app_name))
                startActivity(intent)
                viewModel.share(post.id)
            }

            override fun onPlayVideo(post: Post) {
                val id = post.videoURL?.let { Regex("\\.*\\?v=([\\w\\-]+)(\\S+)?\$").findAll(it).map { it.groupValues[1] }.joinToString() }
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+id))
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+id))
                val intent = Intent()
                Intent.createChooser(intent, getString(R.string.app_name))
                try {
                    startActivity(appIntent)
                } catch(ex: ActivityNotFoundException) {
                    startActivity(webIntent)
                }
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                setFragmentResult("editText", bundleOf("text" to post.content))
                findNavController().navigate(R.id.action_new_post)
            }

            override fun openSingle(post: Post) {
                setFragmentResult("singlePost", bundleOf("post_id" to post.id))
                findNavController().navigate(R.id.action_single_post)

            }

        }

        val adapter = PostAdapter(interaction)
        binding.recycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val curSize = adapter.currentList.size
            adapter.submitList(posts) {
                if (curSize < posts.size)
                    binding.recycler.smoothScrollToPosition(0)
            }
        }
        viewModel.edited.observe(viewLifecycleOwner) { post ->

        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_new_post)
        }

          return binding.root
    }
}