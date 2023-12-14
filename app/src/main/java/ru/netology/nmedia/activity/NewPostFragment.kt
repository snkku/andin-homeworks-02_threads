package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        setFragmentResultListener("editText") { _, bundle ->
            val text = bundle.getString("text")
            if (!text.isNullOrBlank())
                binding.content.setText(text.toString())
        }

        binding.ok.setOnClickListener {
            setFragmentResult("newText", bundleOf("text" to binding.content.text.toString()))
            findNavController().navigateUp()
        }
        return binding.root
    }
}
