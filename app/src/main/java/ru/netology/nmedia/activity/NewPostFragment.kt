package ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel: PostViewModel by activityViewModels()
        var editMode = false
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        Log.d("NPF", "onCreateView: draftContent = ${viewModel.draftContent}")
        if (binding.content.text.isNullOrBlank())
            binding.content.setText(viewModel.draftContent)
        setFragmentResultListener("editText") { _, bundle ->
            editMode = true
            val text = bundle.getString("text")
            if (!text.isNullOrBlank())
                binding.content.setText(text.toString())
        }

            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        Log.d("NPF", "handleOnBackPressed: back pressed")
                        if (!editMode)
                            viewModel.draftContent = binding.content.text.toString()
                        findNavController().navigateUp()
                    }
                }
            )

        binding.ok.setOnClickListener {
            setFragmentResult("newText", bundleOf("text" to binding.content.text.toString()))
            viewModel.draftContent = null
            findNavController().navigateUp()
        }
        return binding.root
    }
}
