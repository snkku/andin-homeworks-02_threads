package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class AppActivity : AppCompatActivity(R.layout.fragment_feed) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: PostViewModel by viewModels()
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND)
                return@let

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank())
            {
                Snackbar.make(binding.root, R.string.error_empty_content, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }
        }

    }
}